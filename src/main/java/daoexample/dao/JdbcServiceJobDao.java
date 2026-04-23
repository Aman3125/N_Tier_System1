package daoexample.dao;

import daoexample.db.DatabaseConnection;
import daoexample.domain.ServiceJob;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

// F2: JDBC implementation of ServiceJobDao using PreparedStatement for all SQL operations.
public class JdbcServiceJobDao implements ServiceJobDao {

    public JdbcServiceJobDao() {
    }

    // F6: Inserts a new service job record and returns the populated DTO
    // including the auto-generated ID from getGeneratedKeys().
    @Override
    public ServiceJob insert(ServiceJob serviceJob) throws Exception {
        if (serviceJob == null) {
            throw new IllegalArgumentException("Service job cannot be null.");
        }

        String sql = "INSERT INTO service_job " +
                "(vehicle_id, description, status, cost, date_created, file_data, file_name, content_type, file_size) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, serviceJob.getVehicleId());
            ps.setString(2, serviceJob.getDescription());
            ps.setString(3, serviceJob.getStatus());
            ps.setDouble(4, serviceJob.getCost());
            ps.setString(5, serviceJob.getDateCreated());
            ps.setBytes(6, serviceJob.getFileData());
            ps.setString(7, serviceJob.getFileName());
            ps.setString(8, serviceJob.getContentType());
            ps.setInt(9, serviceJob.getFileSize());

            int rows = ps.executeUpdate();

            if (rows != 1) {
                throw new IllegalStateException("Insert failed. rows = " + rows);
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new IllegalStateException("No generated key returned.");
                }

                int newId = keys.getInt(1);

                return new ServiceJob(
                        newId,
                        serviceJob.getVehicleId(),
                        serviceJob.getDescription(),
                        serviceJob.getStatus(),
                        serviceJob.getCost(),
                        serviceJob.getDateCreated(),
                        serviceJob.getFileData(),
                        serviceJob.getFileName(),
                        serviceJob.getContentType(),
                        serviceJob.getFileSize()
                );
            }
        }
    }

    // F4: Retrieves a service job by ID and returns Optional<ServiceJob>.
    // Returns Optional.empty() if no matching record exists.
    @Override
    public Optional<ServiceJob> getServiceJobById(int id) throws Exception {
        if (id <= 0) {
            return Optional.empty();
        }

        String sql = "SELECT service_job_id, vehicle_id, description, status, cost, date_created, " +
                "file_data, file_name, content_type, file_size " +
                "FROM service_job WHERE service_job_id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                return Optional.of(mapRow(rs));
            }
        }
    }

    // F3: Retrieves all service job records and returns them as a List<ServiceJob>.
    @Override
    public List<ServiceJob> getAllServiceJobs() throws Exception {
        String sql = "SELECT service_job_id, vehicle_id, description, status, cost, date_created, " +
                "file_data, file_name, content_type, file_size " +
                "FROM service_job ORDER BY service_job_id";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<ServiceJob> serviceJobs = new ArrayList<>();

            while (rs.next()) {
                serviceJobs.add(mapRow(rs));
            }

            return serviceJobs;
        }
    }

    // F7: Updates an existing service job by ID and returns the updated ServiceJob DTO.
    @Override
    public ServiceJob updateServiceJob(int id, ServiceJob serviceJob) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("Service job ID must be greater than 0.");
        }

        if (serviceJob == null) {
            throw new IllegalArgumentException("Service job cannot be null.");
        }

        String sql = "UPDATE service_job SET vehicle_id = ?, description = ?, status = ?, cost = ?, date_created = ?, " +
                "file_data = ?, file_name = ?, content_type = ?, file_size = ? " +
                "WHERE service_job_id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, serviceJob.getVehicleId());
            ps.setString(2, serviceJob.getDescription());
            ps.setString(3, serviceJob.getStatus());
            ps.setDouble(4, serviceJob.getCost());
            ps.setString(5, serviceJob.getDateCreated());
            ps.setBytes(6, serviceJob.getFileData());
            ps.setString(7, serviceJob.getFileName());
            ps.setString(8, serviceJob.getContentType());
            ps.setInt(9, serviceJob.getFileSize());
            ps.setInt(10, id);

            int rows = ps.executeUpdate();

            if (rows != 1) {
                throw new IllegalStateException("Update failed. No service job found with id " + id);
            }

            return new ServiceJob(
                    id,
                    serviceJob.getVehicleId(),
                    serviceJob.getDescription(),
                    serviceJob.getStatus(),
                    serviceJob.getCost(),
                    serviceJob.getDateCreated(),
                    serviceJob.getFileData(),
                    serviceJob.getFileName(),
                    serviceJob.getContentType(),
                    serviceJob.getFileSize()
            );
        }
    }

    // F5: Deletes a service job by ID and returns true if one row was removed.
    @Override
    public boolean deleteServiceJobById(int id) throws Exception {
        if (id <= 0) {
            return false;
        }

        String sql = "DELETE FROM service_job WHERE service_job_id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public List<ServiceJob> findServiceJobsByFilter(Predicate<ServiceJob> filter) throws Exception {
        return getAllServiceJobs().stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    @Override
    public String serviceJobToJson(ServiceJob job) throws Exception {
        return "{"
                + "\"serviceJobId\":" + job.getServiceJobId() + ","
                + "\"vehicleId\":" + job.getVehicleId() + ","
                + "\"description\":\"" + job.getDescription() + "\","
                + "\"status\":\"" + job.getStatus() + "\","
                + "\"cost\":" + job.getCost() + ","
                + "\"dateCreated\":\"" + job.getDateCreated() + "\","
                + "\"fileName\":" + (job.getFileName() == null ? "null" : "\"" + job.getFileName() + "\"") + ","
                + "\"contentType\":" + (job.getContentType() == null ? "null" : "\"" + job.getContentType() + "\"") + ","
                + "\"fileSize\":" + job.getFileSize()
                + "}";
    }

    @Override
    public ServiceJob serviceJobFromJson(String json) throws Exception {
        json = json.substring(1, json.length() - 1);
        String[] pairs = json.split(",");

        int serviceJobId = 0;
        int vehicleId = 0;
        String description = "";
        String status = "";
        double cost = 0.0;
        String dateCreated = "";
        String fileName = null;
        String contentType = null;
        int fileSize = 0;

        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);

            if (keyValue.length < 2) {
                continue;
            }

            String key = keyValue[0].replace("\"", "").trim();
            String value = keyValue[1].trim();

            if (value.equals("null")) {
                value = null;
            }
            else {
                value = value.replace("\"", "");
            }

            switch (key) {
                case "serviceJobId":
                    serviceJobId = Integer.parseInt(value);
                    break;
                case "vehicleId":
                    vehicleId = Integer.parseInt(value);
                    break;
                case "description":
                    description = value;
                    break;
                case "status":
                    status = value;
                    break;
                case "cost":
                    cost = Double.parseDouble(value);
                    break;
                case "dateCreated":
                    dateCreated = value;
                    break;
                case "fileName":
                    fileName = value;
                    break;
                case "contentType":
                    contentType = value;
                    break;
                case "fileSize":
                    fileSize = Integer.parseInt(value);
                    break;
            }
        }

        return new ServiceJob(
                serviceJobId,
                vehicleId,
                description,
                status,
                cost,
                dateCreated,
                null,
                fileName,
                contentType,
                fileSize
        );
    }

    @Override
    public String serviceJobListToJson(List<ServiceJob> jobs) throws Exception {
        StringBuilder json = new StringBuilder("[");

        for (int i = 0; i < jobs.size(); i++) {
            if (i > 0) {
                json.append(",");
            }
            json.append(serviceJobToJson(jobs.get(i)));
        }

        json.append("]");
        return json.toString();
    }

    private ServiceJob mapRow(ResultSet rs) throws Exception {
        int serviceJobId = rs.getInt("service_job_id");
        int vehicleId = rs.getInt("vehicle_id");
        String description = rs.getString("description");
        String status = rs.getString("status");
        double cost = rs.getDouble("cost");
        String dateCreated = rs.getString("date_created");
        byte[] fileData = rs.getBytes("file_data");
        String fileName = rs.getString("file_name");
        String contentType = rs.getString("content_type");
        int fileSize = rs.getInt("file_size");

        return new ServiceJob(
                serviceJobId,
                vehicleId,
                description,
                status,
                cost,
                dateCreated,
                fileData,
                fileName,
                contentType,
                fileSize
        );
    }
}