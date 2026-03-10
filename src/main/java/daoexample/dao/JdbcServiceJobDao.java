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