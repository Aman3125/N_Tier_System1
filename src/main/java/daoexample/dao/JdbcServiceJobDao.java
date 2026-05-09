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

public class JdbcServiceJobDao implements ServiceJobDao {

    public JdbcServiceJobDao() {
    }

    // F6: INSERT - creates new service job with auto-generated ID
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

    // F4: GET BY ID - retrieves one service job including file data
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

    // F3: GET ALL - retrieves all service jobs
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

    // F7: UPDATE - modifies existing service job
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

    // F5: DELETE - removes service job by ID
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

    // F8: FILTER - finds jobs matching a predicate
    @Override
    public List<ServiceJob> findServiceJobsByFilter(Predicate<ServiceJob> filter) throws Exception {
        return getAllServiceJobs().stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    // F9: JSON - converts single job to JSON string
    @Override
    public String serviceJobToJson(ServiceJob job) throws Exception {
        return "{"
                + "\"serviceJobId\":" + job.getServiceJobId() + ","
                + "\"vehicleId\":" + job.getVehicleId() + ","
                + "\"description\":\"" + escapeJson(job.getDescription()) + "\","
                + "\"status\":\"" + job.getStatus() + "\","
                + "\"cost\":" + job.getCost() + ","
                + "\"dateCreated\":\"" + job.getDateCreated() + "\","
                + "\"fileName\":" + (job.getFileName() == null ? "null" : "\"" + escapeJson(job.getFileName()) + "\"") + ","
                + "\"contentType\":" + (job.getContentType() == null ? "null" : "\"" + escapeJson(job.getContentType()) + "\"") + ","
                + "\"fileSize\":" + job.getFileSize()
                + "}";
    }

    // Helper method to escape JSON special characters
    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    // F9: JSON - converts JSON string back to ServiceJob object
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
            } else {
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

    // F9: JSON - converts list of jobs to JSON array
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

    // ===== F20: FILE METADATA QUERY =====
    // Gets file metadata (name, type, size) WITHOUT fetching the binary BLOB data
    // This is much faster and uses less memory than full download
    @Override
    public ServiceJob getServiceJobMetadataById(int id) throws Exception {
        if (id <= 0) {
            return null;
        }

        // IMPORTANT: This query does NOT include 'file_data' column (the BLOB)
        // So no binary data is transferred from database
        String sql = "SELECT service_job_id, vehicle_id, description, status, cost, date_created, " +
                "file_name, content_type, file_size " +
                "FROM service_job WHERE service_job_id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                // Return job with fileData = null (metadata only, no binary content)
                return new ServiceJob(
                        rs.getInt("service_job_id"),
                        rs.getInt("vehicle_id"),
                        rs.getString("description"),
                        rs.getString("status"),
                        rs.getDouble("cost"),
                        rs.getString("date_created"),
                        null,  // IMPORTANT: fileData = null - we don't fetch the BLOB!
                        rs.getString("file_name"),
                        rs.getString("content_type"),
                        rs.getInt("file_size")
                );
            }
        }
    }

    // Helper method to map database row to ServiceJob object
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