package daoexample.dao;

import daoexample.db.DatabaseConnection;
import daoexample.domain.ServiceJob;

import java.sql.Connection;
import java.sql.Date;
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

    @Override
    public ServiceJob insert(ServiceJob serviceJob) throws Exception {
        if (serviceJob == null) {
            throw new IllegalArgumentException("Service job cannot be null.");
        }

        String sql = "INSERT INTO service_job (vehicle_id, description, status, cost, date_created) VALUES (?, ?, ?, ?, ?)";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, serviceJob.getVehicleId());
            ps.setString(2, serviceJob.getDescription());
            ps.setString(3, serviceJob.getStatus());
            ps.setDouble(4, serviceJob.getCost());
            ps.setDate(5, Date.valueOf(serviceJob.getDateCreated()));

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
                        serviceJob.getDateCreated()
                );
            }
        }
    }

    @Override
    public Optional<ServiceJob> getServiceJobById(int id) throws Exception {
        if (id <= 0) {
            return Optional.empty();
        }

        String sql = "SELECT service_job_id, vehicle_id, description, status, cost, date_created " +
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

    @Override
    public List<ServiceJob> getAllServiceJobs() throws Exception {
        String sql = "SELECT service_job_id, vehicle_id, description, status, cost, date_created " +
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

    @Override
    public ServiceJob updateServiceJob(int id, ServiceJob serviceJob) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("Service job ID must be greater than 0.");
        }

        if (serviceJob == null) {
            throw new IllegalArgumentException("Service job cannot be null.");
        }

        String sql = "UPDATE service_job SET vehicle_id = ?, description = ?, status = ?, cost = ?, date_created = ? " +
                "WHERE service_job_id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, serviceJob.getVehicleId());
            ps.setString(2, serviceJob.getDescription());
            ps.setString(3, serviceJob.getStatus());
            ps.setDouble(4, serviceJob.getCost());
            ps.setDate(5, Date.valueOf(serviceJob.getDateCreated()));
            ps.setInt(6, id);

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
                    serviceJob.getDateCreated()
            );
        }
    }

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

    // ========== F8: FILTER METHOD ==========
    @Override
    public List<ServiceJob> findServiceJobsByFilter(Predicate<ServiceJob> filter) throws Exception {
        return getAllServiceJobs().stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    // ========== F9: JSON METHODS ==========
    @Override
    public String serviceJobToJson(ServiceJob job) throws Exception {
        return "{"
                + "\"serviceJobId\":" + job.getServiceJobId() + ","
                + "\"vehicleId\":" + job.getVehicleId() + ","
                + "\"description\":\"" + job.getDescription() + "\","
                + "\"status\":\"" + job.getStatus() + "\","
                + "\"cost\":" + job.getCost() + ","
                + "\"dateCreated\":\"" + job.getDateCreated() + "\""
                + "}";
    }

    @Override
    public ServiceJob serviceJobFromJson(String json) throws Exception {
        // Remove { and } from the ends
        json = json.substring(1, json.length() - 1);
        String[] pairs = json.split(",");

        int serviceJobId = 0, vehicleId = 0;
        String description = "", status = "";
        double cost = 0.0;
        java.time.LocalDate dateCreated = java.time.LocalDate.now();

        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);

            if (keyValue.length < 2) {
                continue;
            }
            String key = keyValue[0].replace("\"", "");
            String value = keyValue[1].replace("\"", "");

            switch (key) {
                case "serviceJobId": serviceJobId = Integer.parseInt(value); break;
                case "vehicleId": vehicleId = Integer.parseInt(value); break;
                case "description": description = value; break;
                case "status": status = value; break;
                case "cost": cost = Double.parseDouble(value); break;
                case "dateCreated": dateCreated = java.time.LocalDate.parse(value); break;
            }
        }

        return new ServiceJob(serviceJobId, vehicleId, description, status, cost, dateCreated);
    }

    @Override
    public String serviceJobListToJson(List<ServiceJob> jobs) throws Exception {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < jobs.size(); i++) {
            if (i > 0) json.append(",");
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
        java.time.LocalDate dateCreated = rs.getDate("date_created").toLocalDate();

        return new ServiceJob(serviceJobId, vehicleId, description, status, cost, dateCreated);
    }
}