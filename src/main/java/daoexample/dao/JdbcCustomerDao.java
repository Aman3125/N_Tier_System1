package daoexample.dao;

import daoexample.db.DatabaseConnection;
import daoexample.domain.Customer;
import daoexample.domain.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/// <summary>
/// JDBC implementation of TaskDao.
/// </summary>
public class JdbcCustomerDao implements CustomerDao {

    public JdbcCustomerDao() {
    }

    @Override
    public Customer insert(Customer customer) throws Exception {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null.");
        }

        String sql = "INSERT INTO customer (first_name, last_name, phone_number, email, address) VALUES (?, ?, ?, ?, ?)";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, customer.getFirstName());
            ps.setString(2, customer.getLastName());
            ps.setString(3, customer.getPhoneNumber());
            ps.setString(4, customer.getEmail());
            ps.setString(5, customer.getAddress());

            int rows = ps.executeUpdate();

            if (rows != 1) {
                throw new IllegalStateException("Insert failed. rows = " + rows);
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new IllegalStateException("No generated key returned.");
                }

                int newId = keys.getInt(1);

                return new Customer(
                        newId,
                        customer.getFirstName(),
                        customer.getLastName(),
                        customer.getPhoneNumber(),
                        customer.getEmail(),
                        customer.getAddress()
                );
            }
        }
    }

    @Override
    public Optional<Customer> getCustomerById(int id) throws Exception {
        if (id <= 0) {
            return Optional.empty();
        }

        String sql = "SELECT customer_id, first_name, last_name, phone_number, email, address FROM customer WHERE customer_id = ?";

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
    public List<Customer> getAllCustomers() throws Exception {
        String sql = "SELECT customer_id, first_name, last_name, phone_number, email, address FROM customer ORDER BY customer_id";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Customer> customers = new ArrayList<>();

            while (rs.next()) {
                customers.add(mapRow(rs));
            }

            return customers;
        }
    }

    @Override
    public Customer updateCustomer(int id, Customer customer) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("Customer ID must be greater than 0.");
        }

        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null.");
        }

        String sql = "UPDATE customer SET first_name = ?, last_name = ?, phone_number = ?, email = ?, address = ? WHERE customer_id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, customer.getFirstName());
            ps.setString(2, customer.getLastName());
            ps.setString(3, customer.getPhoneNumber());
            ps.setString(4, customer.getEmail());
            ps.setString(5, customer.getAddress());
            ps.setInt(6, id);

            int rows = ps.executeUpdate();

            if (rows != 1) {
                throw new IllegalStateException("Update failed. No customer found with id " + id);
            }

            return new Customer(
                    id,
                    customer.getFirstName(),
                    customer.getLastName(),
                    customer.getPhoneNumber(),
                    customer.getEmail(),
                    customer.getAddress()
            );
        }
    }

    @Override
    public boolean deleteCustomerById(int id) throws Exception {
        if (id <= 0) {
            return false;
        }

        String sql = "DELETE FROM customer WHERE customer_id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    private Customer mapRow(ResultSet rs) throws Exception {
        int customerId = rs.getInt("customer_id");
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        String phoneNumber = rs.getString("phone_number");
        String email = rs.getString("email");
        String address = rs.getString("address");

        return new Customer(customerId, firstName, lastName, phoneNumber, email, address);
    }

    // F8: Find customers matching a filter
    @Override
    public List<Customer> findCustomersByFilter(Predicate<Customer> filter) throws Exception {
        return getAllCustomers().stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    // F9: Convert a single customer to JSON
    @Override
    public String customerToJson(Customer customer) throws Exception {
        return "{"
                + "\"customerId\":" + customer.getCustomerId() + ","
                + "\"firstName\":\"" + customer.getFirstName() + "\","
                + "\"lastName\":\"" + customer.getLastName() + "\","
                + "\"phoneNumber\":\"" + customer.getPhoneNumber() + "\","
                + "\"email\":\"" + customer.getEmail() + "\","
                + "\"address\":\"" + customer.getAddress() + "\""
                + "}";
    }

    // F9: Convert JSON to Customer object
    @Override
    public Customer customerFromJson(String json) throws Exception {
        // Remove { and } from the ends
        json = json.substring(1, json.length() - 1);
        String[] pairs = json.split(",");

        int id = 0;
        String firstName = "", lastName = "", phone = "", email = "", address = "";

        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);   // split only once

            if (keyValue.length < 2) {               // safety check
                continue;
            }

            String key = keyValue[0].replace("\"", "");
            String value = keyValue[1].replace("\"", "");

            switch (key) {
                case "customerId": id = Integer.parseInt(value); break;
                case "firstName": firstName = value; break;
                case "lastName": lastName = value; break;
                case "phoneNumber": phone = value; break;
                case "email": email = value; break;
                case "address": address = value; break;
            }
        }

        return new Customer(id, firstName, lastName, phone, email, address);
    }

    // F9: Convert a list of customers to JSON
    @Override
    public String customerListToJson(List<Customer> customers) throws Exception {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < customers.size(); i++) {
            if (i > 0) json.append(",");
            json.append(customerToJson(customers.get(i)));
        }
        json.append("]");
        return json.toString();
    }
}