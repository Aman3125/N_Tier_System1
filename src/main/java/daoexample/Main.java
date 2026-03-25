package daoexample;

import daoexample.dao.CustomerDao;
import daoexample.dao.JdbcCustomerDao;
import daoexample.dao.JdbcServiceJobDao;
import daoexample.dao.JdbcVehicleDao;
import daoexample.dao.ServiceJobDao;
import daoexample.dao.VehicleDao;
import daoexample.domain.Customer;
import daoexample.domain.ServiceJob;
import daoexample.domain.Vehicle;

import java.util.List;
import java.util.Optional;

public class Main {

    public static void main(String[] args) {

        // F1 - DTO entities + mysqlSetup.sql database setup
        // F2 - DAO interfaces with JDBC implementations

        CustomerDao dao = new JdbcCustomerDao();
        VehicleDao vehicleDao = new JdbcVehicleDao();
        ServiceJobDao serviceJobDao = new JdbcServiceJobDao();

        createCustomerDemo(dao);        // F6 Insert
        readCustomerDemo(dao);          // F3 Get All + F4 Get by ID
        updateCustomerDemo(dao);        // F7 Update
        deleteCustomerDemo(dao);        // F5 Delete

        createVehicleDemo(vehicleDao);  // F6 Insert
        readVehicleDemo(vehicleDao);    // F3 Get All + F4 Get by ID

        createServiceJobDemo(serviceJobDao); // F6 Insert
        readServiceJobDemo(serviceJobDao);   // F3 Get All + F4 Get by ID

        filterCustomerDemo(dao);            // F8 Predicate Filter
        filterVehicleDemo(vehicleDao);      // F8 Predicate Filter
        filterServiceJobDemo(serviceJobDao);// F8 Predicate Filter

        customerJsonDemo(dao);              // F9 JSON Conversion
        vehicleJsonDemo(vehicleDao);        // F9 JSON Conversion
        serviceJobJsonDemo(serviceJobDao);  // F9 JSON Conversion
    }

    // F6 Insert
    private static void createCustomerDemo(CustomerDao dao) {
        try {
            System.out.println("CREATE CUSTOMER");

            Customer newCustomer = new Customer(
                    "Lisa",
                    "O'Brien",
                    "0855555555",
                    "lisa.obrien@email.com",
                    "22 Lake Road, Dublin"
            );

            Customer saved = dao.insert(newCustomer);
            System.out.println("Inserted: " + saved);
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // F3 Get All + F4 Get by ID
    private static void readCustomerDemo(CustomerDao dao) {
        try {
            System.out.println("READ CUSTOMERS");

            Optional<Customer> found = dao.getCustomerById(1);
            found.ifPresentOrElse(
                    c -> System.out.println("Found by id: " + c),
                    () -> System.out.println("No customer found with id 1")
            );

            List<Customer> all = dao.getAllCustomers();
            System.out.println("All customers:");
            all.forEach(System.out::println);
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // F7 Update
    private static void updateCustomerDemo(CustomerDao dao) {
        try {
            System.out.println("UPDATE CUSTOMER");

            Customer updatedData = new Customer(
                    "Lisa",
                    "O'Brien",
                    "0899999999",
                    "lisa.new@email.com",
                    "99 New Street, Cork"
            );

            Customer updated = dao.updateCustomer(1, updatedData);
            System.out.println("Updated: " + updated);
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // F5 Delete
    private static void deleteCustomerDemo(CustomerDao dao) {
        try {
            System.out.println("DELETE CUSTOMER");

            Customer temp = new Customer(
                    "Temp",
                    "User",
                    "0800000000",
                    "temp.user@email.com",
                    "Temporary Address"
            );

            Customer saved = dao.insert(temp);
            boolean deleted = dao.deleteCustomerById(saved.getCustomerId());

            System.out.println("Deleted temp customer: " + deleted);
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // F6 Insert
    private static void createVehicleDemo(VehicleDao dao) {
        try {
            System.out.println("CREATE VEHICLE");

            Vehicle newVehicle = new Vehicle(
                    1,
                    "Honda",
                    "Civic",
                    "211-D-55555",
                    2021
            );

            Vehicle saved = dao.insert(newVehicle);
            System.out.println("Inserted: " + saved);
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // F3 Get All + F4 Get by ID
    private static void readVehicleDemo(VehicleDao dao) {
        try {
            System.out.println("READ VEHICLES");

            Optional<Vehicle> found = dao.getVehicleById(1);
            found.ifPresentOrElse(
                    v -> System.out.println("Found by id: " + v),
                    () -> System.out.println("No vehicle found with id 1")
            );

            List<Vehicle> all = dao.getAllVehicles();
            System.out.println("All vehicles:");
            all.forEach(System.out::println);
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // F6 Insert
    private static void createServiceJobDemo(ServiceJobDao dao) {
        try {
            System.out.println("CREATE SERVICE JOB");

            ServiceJob newJob = new ServiceJob(
                    1,
                    "Wheel alignment",
                    "PENDING",
                    75.00,
                    "2026-03-19"
            );

            ServiceJob saved = dao.insert(newJob);
            System.out.println("Inserted: " + saved);
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // F3 Get All + F4 Get by ID
    private static void readServiceJobDemo(ServiceJobDao dao) {
        try {
            System.out.println("READ SERVICE JOBS");

            Optional<ServiceJob> found = dao.getServiceJobById(1);
            found.ifPresentOrElse(
                    s -> System.out.println("Found by id: " + s),
                    () -> System.out.println("No service job found with id 1")
            );

            List<ServiceJob> all = dao.getAllServiceJobs();
            System.out.println("All service jobs:");
            all.forEach(System.out::println);
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // F8 Predicate Filter
    private static void filterCustomerDemo(CustomerDao dao) {
        try {
            List<Customer> filtered = dao.findCustomersByFilter(
                    c -> c.toString().toLowerCase().contains("lisa")
            );

            System.out.println("Filtered customers:");
            filtered.forEach(System.out::println);
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // F8 Predicate Filter
    private static void filterVehicleDemo(VehicleDao dao) {
        try {
            List<Vehicle> filtered = dao.findVehiclesByFilter(
                    v -> v.toString().toLowerCase().contains("honda")
            );

            System.out.println("Filtered vehicles:");
            filtered.forEach(System.out::println);
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // F8 Predicate Filter
    private static void filterServiceJobDemo(ServiceJobDao dao) {
        try {
            List<ServiceJob> filtered = dao.findServiceJobsByFilter(
                    s -> s.toString().toLowerCase().contains("pending")
            );

            System.out.println("Filtered service jobs:");
            filtered.forEach(System.out::println);
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // F9 JSON Conversion (round-trip verified)
    private static void customerJsonDemo(CustomerDao dao) {
        try {
            List<Customer> customers = dao.getAllCustomers();
            if (customers.isEmpty()) return;

            Customer original = customers.get(0);
            String json = dao.customerToJson(original);
            Customer back = dao.customerFromJson(json);
            String json2 = dao.customerToJson(back);

            System.out.println("Customer JSON round-trip ok: " + json.equals(json2));
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // F9 JSON Conversion (round-trip verified)
    private static void vehicleJsonDemo(VehicleDao dao) {
        try {
            List<Vehicle> vehicles = dao.getAllVehicles();
            if (vehicles.isEmpty()) return;

            Vehicle original = vehicles.get(0);
            String json = dao.vehicleToJson(original);
            Vehicle back = dao.vehicleFromJson(json);
            String json2 = dao.vehicleToJson(back);

            System.out.println("Vehicle JSON round-trip ok: " + json.equals(json2));
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // F9 JSON Conversion (round-trip verified)
    private static void serviceJobJsonDemo(ServiceJobDao dao) {
        try {
            List<ServiceJob> jobs = dao.getAllServiceJobs();
            if (jobs.isEmpty()) return;

            ServiceJob original = jobs.get(0);
            String json = dao.serviceJobToJson(original);
            ServiceJob back = dao.serviceJobFromJson(json);
            String json2 = dao.serviceJobToJson(back);

            System.out.println("ServiceJob JSON round-trip ok: " + json.equals(json2));
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}