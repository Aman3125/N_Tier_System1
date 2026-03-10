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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class Main {

    public static void main(String[] args) {

        CustomerDao dao = new JdbcCustomerDao();
        VehicleDao vehicleDao = new JdbcVehicleDao();
        ServiceJobDao serviceJobDao = new JdbcServiceJobDao();

        createCustomerDemo(dao);
        readCustomerDemo(dao);
        updateCustomerDemo(dao);
        deleteCustomerDemo(dao);

        createVehicleDemo(vehicleDao);
        readVehicleDemo(vehicleDao);

        createServiceJobDemo(serviceJobDao);
        readServiceJobDemo(serviceJobDao);
    }

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
            System.out.println("CREATE CUSTOMER failed");
            e.printStackTrace();
        }
    }

    private static void readCustomerDemo(CustomerDao dao) {
        try {
            System.out.println("READ CUSTOMERS");

            Optional<Customer> found = dao.getCustomerById(1);
            if (found.isPresent()) {
                System.out.println("Found by id: " + found.get());
            } else {
                System.out.println("No customer found with id 1");
            }

            List<Customer> all = dao.getAllCustomers();
            System.out.println("All customers:");
            for (Customer c : all) {
                System.out.println(c);
            }

            System.out.println();

        } catch (Exception e) {
            System.out.println("READ CUSTOMERS failed");
            e.printStackTrace();
        }
    }

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
            System.out.println("UPDATE CUSTOMER failed");
            e.printStackTrace();
        }
    }

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
            System.out.println("DELETE CUSTOMER failed");
            e.printStackTrace();
        }
    }

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
            System.out.println("CREATE VEHICLE failed");
            e.printStackTrace();
        }
    }

    private static void readVehicleDemo(VehicleDao dao) {
        try {
            System.out.println("READ VEHICLES");

            Optional<Vehicle> found = dao.getVehicleById(1);
            if (found.isPresent()) {
                System.out.println("Found by id: " + found.get());
            } else {
                System.out.println("No vehicle found with id 1");
            }

            List<Vehicle> all = dao.getAllVehicles();
            System.out.println("All vehicles:");
            for (Vehicle v : all) {
                System.out.println(v);
            }

            System.out.println();

        } catch (Exception e) {
            System.out.println("READ VEHICLES failed");
            e.printStackTrace();
        }
    }

    private static void createServiceJobDemo(ServiceJobDao dao) {
        try {
            System.out.println("CREATE SERVICE JOB");

            ServiceJob newJob = new ServiceJob(
                    1,
                    "Wheel alignment",
                    "PENDING",
                    75.00,
                    LocalDate.now()
            );

            ServiceJob saved = dao.insert(newJob);
            System.out.println("Inserted: " + saved);
            System.out.println();

        } catch (Exception e) {
            System.out.println("CREATE SERVICE JOB failed");
            e.printStackTrace();
        }
    }

    private static void readServiceJobDemo(ServiceJobDao dao) {
        try {
            System.out.println("READ SERVICE JOBS");

            Optional<ServiceJob> found = dao.getServiceJobById(1);
            if (found.isPresent()) {
                System.out.println("Found by id: " + found.get());
            } else {
                System.out.println("No service job found with id 1");
            }

            List<ServiceJob> all = dao.getAllServiceJobs();
            System.out.println("All service jobs:");
            for (ServiceJob s : all) {
                System.out.println(s);
            }

            System.out.println();

        } catch (Exception e) {
            System.out.println("READ SERVICE JOBS failed");
            e.printStackTrace();
        }
    }
}