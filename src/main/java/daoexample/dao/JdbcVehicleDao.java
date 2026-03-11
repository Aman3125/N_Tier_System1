package daoexample.dao;

import daoexample.db.DatabaseConnection;
import daoexample.domain.Vehicle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class JdbcVehicleDao implements VehicleDao {

    public JdbcVehicleDao() {
    }

    @Override
    public Vehicle insert(Vehicle vehicle) throws Exception {
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null.");
        }

        String sql = "INSERT INTO vehicle (customer_id, make, model, registration_number, year) VALUES (?, ?, ?, ?, ?)";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, vehicle.getCustomerId());
            ps.setString(2, vehicle.getMake());
            ps.setString(3, vehicle.getModel());
            ps.setString(4, vehicle.getRegistrationNumber());
            ps.setInt(5, vehicle.getYear());

            int rows = ps.executeUpdate();

            if (rows != 1) {
                throw new IllegalStateException("Insert failed. rows = " + rows);
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new IllegalStateException("No generated key returned.");
                }

                int newId = keys.getInt(1);

                return new Vehicle(
                        newId,
                        vehicle.getCustomerId(),
                        vehicle.getMake(),
                        vehicle.getModel(),
                        vehicle.getRegistrationNumber(),
                        vehicle.getYear()
                );
            }
        }
    }

    @Override
    public Optional<Vehicle> getVehicleById(int id) throws Exception {
        if (id <= 0) {
            return Optional.empty();
        }

        String sql = "SELECT vehicle_id, customer_id, make, model, registration_number, year FROM vehicle WHERE vehicle_id = ?";

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
    public List<Vehicle> getAllVehicles() throws Exception {
        String sql = "SELECT vehicle_id, customer_id, make, model, registration_number, year FROM vehicle ORDER BY vehicle_id";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Vehicle> vehicles = new ArrayList<>();

            while (rs.next()) {
                vehicles.add(mapRow(rs));
            }

            return vehicles;
        }
    }

    @Override
    public Vehicle updateVehicle(int id, Vehicle vehicle) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("Vehicle ID must be greater than 0.");
        }

        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null.");
        }

        String sql = "UPDATE vehicle SET customer_id = ?, make = ?, model = ?, registration_number = ?, year = ? WHERE vehicle_id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, vehicle.getCustomerId());
            ps.setString(2, vehicle.getMake());
            ps.setString(3, vehicle.getModel());
            ps.setString(4, vehicle.getRegistrationNumber());
            ps.setInt(5, vehicle.getYear());
            ps.setInt(6, id);

            int rows = ps.executeUpdate();

            if (rows != 1) {
                throw new IllegalStateException("Update failed. No vehicle found with id " + id);
            }

            return new Vehicle(
                    id,
                    vehicle.getCustomerId(),
                    vehicle.getMake(),
                    vehicle.getModel(),
                    vehicle.getRegistrationNumber(),
                    vehicle.getYear()
            );
        }
    }

    @Override
    public boolean deleteVehicleById(int id) throws Exception {
        if (id <= 0) {
            return false;
        }

        String sql = "DELETE FROM vehicle WHERE vehicle_id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    // ========== F8: FILTER METHOD ==========
    @Override
    public List<Vehicle> findVehiclesByFilter(Predicate<Vehicle> filter) throws Exception {
        return getAllVehicles().stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    // ========== F9: JSON METHODS ==========
    @Override
    public String vehicleToJson(Vehicle vehicle) throws Exception {
        return "{"
                + "\"vehicleId\":" + vehicle.getVehicleId() + ","
                + "\"customerId\":" + vehicle.getCustomerId() + ","
                + "\"make\":\"" + vehicle.getMake() + "\","
                + "\"model\":\"" + vehicle.getModel() + "\","
                + "\"registrationNumber\":\"" + vehicle.getRegistrationNumber() + "\","
                + "\"year\":" + vehicle.getYear()
                + "}";
    }

    @Override
    public Vehicle vehicleFromJson(String json) throws Exception {
        // Remove { and } from the ends
        json = json.substring(1, json.length() - 1);
        String[] pairs = json.split(",");

        int vehicleId = 0, customerId = 0, year = 0;
        String make = "", model = "", registrationNumber = "";

        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);

            if (keyValue.length < 2) {
                continue;
            }

            String key = keyValue[0].replace("\"", "");
            String value = keyValue[1].replace("\"", "");

            switch (key) {
                case "vehicleId": vehicleId = Integer.parseInt(value); break;
                case "customerId": customerId = Integer.parseInt(value); break;
                case "make": make = value; break;
                case "model": model = value; break;
                case "registrationNumber": registrationNumber = value; break;
                case "year": year = Integer.parseInt(value); break;
            }
        }

        return new Vehicle(vehicleId, customerId, make, model, registrationNumber, year);
    }

    @Override
    public String vehicleListToJson(List<Vehicle> vehicles) throws Exception {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < vehicles.size(); i++) {
            if (i > 0) json.append(",");
            json.append(vehicleToJson(vehicles.get(i)));
        }
        json.append("]");
        return json.toString();
    }

    private Vehicle mapRow(ResultSet rs) throws Exception {
        int vehicleId = rs.getInt("vehicle_id");
        int customerId = rs.getInt("customer_id");
        String make = rs.getString("make");
        String model = rs.getString("model");
        String registrationNumber = rs.getString("registration_number");
        int year = rs.getInt("year");

        return new Vehicle(vehicleId, customerId, make, model, registrationNumber, year);
    }
}
