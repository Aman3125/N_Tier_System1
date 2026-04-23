package daoexample.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import daoexample.domain.Customer;
import daoexample.domain.ServiceJob;
import daoexample.domain.Vehicle;

// F12: Client sends JSON request, receives ServerResponse<T> JSON, parses and displays it.
public class GarageClient {

    private static final String HOST = "localhost";
    private static final int PORT = 5050;

    private final Gson gson = new Gson();

    public static void main(String[] args) {
        new GarageClient().run();
    }

    public void run() {
        Scanner kb = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Garage Client Menu ===");
            System.out.println("=== READ OPERATIONS ===");
            System.out.println("1. Display Customer by ID");
            System.out.println("2. Display All Customers");
            System.out.println("3. Display Vehicle by ID");
            System.out.println("4. Display All Vehicles");
            System.out.println("5. Display ServiceJob by ID");
            System.out.println("6. Display All ServiceJobs");
            System.out.println("\n=== WRITE OPERATIONS (F13-F15) ===");
            System.out.println("7. Create Customer");
            System.out.println("8. Update Customer");
            System.out.println("9. Delete Customer");
            System.out.println("10. Create Vehicle");
            System.out.println("11. Update Vehicle");
            System.out.println("12. Delete Vehicle");
            System.out.println("13. Create Service Job");
            System.out.println("14. Update Service Job");
            System.out.println("15. Delete Service Job");
            System.out.println("\n0. Exit");
            System.out.print("Choose option: ");

            String option = kb.nextLine();

            try {
                switch (option) {
                    // READ Operations
                    case "1":
                        System.out.print("Enter customer ID: ");
                        displayCustomerById(Integer.parseInt(kb.nextLine()));
                        break;
                    case "2":
                        displayAllCustomers();
                        break;
                    case "3":
                        System.out.print("Enter vehicle ID: ");
                        displayVehicleById(Integer.parseInt(kb.nextLine()));
                        break;
                    case "4":
                        displayAllVehicles();
                        break;
                    case "5":
                        System.out.print("Enter service job ID: ");
                        displayServiceJobById(Integer.parseInt(kb.nextLine()));
                        break;
                    case "6":
                        displayAllServiceJobs();
                        break;

                    // CREATE Operations (F13)
                    case "7":
                        createCustomer(kb);
                        break;
                    case "10":
                        createVehicle(kb);
                        break;
                    case "13":
                        createServiceJob(kb);
                        break;

                    // UPDATE Operations (F15)
                    case "8":
                        updateCustomer(kb);
                        break;
                    case "11":
                        updateVehicle(kb);
                        break;
                    case "14":
                        updateServiceJob(kb);
                        break;

                    // DELETE Operations (F14)
                    case "9":
                        deleteCustomer(kb);
                        break;
                    case "12":
                        deleteVehicle(kb);
                        break;
                    case "15":
                        deleteServiceJob(kb);
                        break;

                    case "0":
                        System.out.println("Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid option");
                }
            } catch (Exception e) {
                System.out.println("Client error: " + e.getMessage());
            }
        }
    }

    private String sendRequest(ClientRequest request) throws Exception {
        try (
                Socket socket = new Socket(HOST, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            out.println(gson.toJson(request));
            return in.readLine();
        } catch (java.net.ConnectException e) {
            throw new Exception("Cannot connect to server. Make sure GarageServer is running first.");
        }
    }

    // F18: Reads a file from disk and converts it to Base64 for JSON upload.
    private String encodeFileToBase64(String filePath) throws Exception {
        byte[] fileBytes = Files.readAllBytes(Path.of(filePath));
        return Base64.getEncoder().encodeToString(fileBytes);
    }

    // F18: Gets the size of a file in bytes before upload.
    private int getFileSize(String filePath) throws Exception {
        return (int) Files.size(Path.of(filePath));
    }

    // ========== READ METHODS ==========

    private void displayCustomerById(int id) throws Exception {
        ClientRequest request = new ClientRequest("CUSTOMER", "GET_BY_ID", (Integer) id);
        String json = sendRequest(request);

        if (json == null || json.isBlank()) {
            System.out.println("Client error: No response received from server.");
            return;
        }

        Type type = new TypeToken<ServerResponse<Customer>>() {}.getType();
        ServerResponse<Customer> response = gson.fromJson(json, type);

        if (response == null) {
            System.out.println("Client error: Server returned invalid JSON.");
            return;
        }

        System.out.println("Status: " + response.getStatus());
        System.out.println("Message: " + response.getMessage());
        if (response.getData() != null) {
            System.out.println("Data: " + response.getData());
        }
    }

    private void displayAllCustomers() throws Exception {
        // Cast null to Integer to resolve ambiguity
        ClientRequest request = new ClientRequest("CUSTOMER", "GET_ALL", (Integer) null);
        String json = sendRequest(request);

        if (json == null || json.isBlank()) {
            System.out.println("Client error: No response received from server.");
            return;
        }

        Type type = new TypeToken<ServerResponse<List<Customer>>>() {}.getType();
        ServerResponse<List<Customer>> response = gson.fromJson(json, type);

        if (response == null) {
            System.out.println("Client error: Server returned invalid JSON.");
            return;
        }

        System.out.println("Status: " + response.getStatus());
        System.out.println("Message: " + response.getMessage());

        if (response.getData() != null && !response.getData().isEmpty()) {
            for (Customer c : response.getData()) {
                System.out.println(c);
            }
        } else {
            System.out.println("No customers found.");
        }
    }

    private void displayVehicleById(int id) throws Exception {
        ClientRequest request = new ClientRequest("VEHICLE", "GET_BY_ID", (Integer) id);
        String json = sendRequest(request);

        if (json == null || json.isBlank()) {
            System.out.println("Client error: No response received from server.");
            return;
        }

        Type type = new TypeToken<ServerResponse<Vehicle>>() {}.getType();
        ServerResponse<Vehicle> response = gson.fromJson(json, type);

        if (response == null) {
            System.out.println("Client error: Server returned invalid JSON.");
            return;
        }

        System.out.println("Status: " + response.getStatus());
        System.out.println("Message: " + response.getMessage());
        if (response.getData() != null) {
            System.out.println("Data: " + response.getData());
        }
    }

    private void displayAllVehicles() throws Exception {
        // Cast null to Integer to resolve ambiguity
        ClientRequest request = new ClientRequest("VEHICLE", "GET_ALL", (Integer) null);
        String json = sendRequest(request);

        if (json == null || json.isBlank()) {
            System.out.println("Client error: No response received from server.");
            return;
        }

        Type type = new TypeToken<ServerResponse<List<Vehicle>>>() {}.getType();
        ServerResponse<List<Vehicle>> response = gson.fromJson(json, type);

        if (response == null) {
            System.out.println("Client error: Server returned invalid JSON.");
            return;
        }

        System.out.println("Status: " + response.getStatus());
        System.out.println("Message: " + response.getMessage());

        if (response.getData() != null && !response.getData().isEmpty()) {
            for (Vehicle v : response.getData()) {
                System.out.println(v);
            }
        } else {
            System.out.println("No vehicles found.");
        }
    }

    private void displayServiceJobById(int id) throws Exception {
        ClientRequest request = new ClientRequest("SERVICEJOB", "GET_BY_ID", (Integer) id);
        String json = sendRequest(request);

        if (json == null || json.isBlank()) {
            System.out.println("Client error: No response received from server.");
            return;
        }

        Type type = new TypeToken<ServerResponse<ServiceJob>>() {}.getType();
        ServerResponse<ServiceJob> response = gson.fromJson(json, type);

        if (response == null) {
            System.out.println("Client error: Server returned invalid JSON.");
            return;
        }

        System.out.println("Status: " + response.getStatus());
        System.out.println("Message: " + response.getMessage());
        if (response.getData() != null) {
            System.out.println("Data: " + response.getData());
        }
    }

    private void displayAllServiceJobs() throws Exception {
        // Cast null to Integer to resolve ambiguity
        ClientRequest request = new ClientRequest("SERVICEJOB", "GET_ALL", (Integer) null);
        String json = sendRequest(request);

        if (json == null || json.isBlank()) {
            System.out.println("Client error: No response received from server.");
            return;
        }

        Type type = new TypeToken<ServerResponse<List<ServiceJob>>>() {}.getType();
        ServerResponse<List<ServiceJob>> response = gson.fromJson(json, type);

        if (response == null) {
            System.out.println("Client error: Server returned invalid JSON.");
            return;
        }

        System.out.println("Status: " + response.getStatus());
        System.out.println("Message: " + response.getMessage());

        if (response.getData() != null && !response.getData().isEmpty()) {
            for (ServiceJob s : response.getData()) {
                System.out.println(s);
            }
        } else {
            System.out.println("No service jobs found.");
        }
    }

    // ========== CREATE METHODS (F13) ==========

    private void createCustomer(Scanner kb) throws Exception {
        System.out.println("\n--- Create New Customer ---");
        System.out.print("First Name: ");
        String firstName = kb.nextLine();
        System.out.print("Last Name: ");
        String lastName = kb.nextLine();
        System.out.print("Phone Number: ");
        String phone = kb.nextLine();
        System.out.print("Email: ");
        String email = kb.nextLine();
        System.out.print("Address: ");
        String address = kb.nextLine();

        Customer newCustomer = new Customer(firstName, lastName, phone, email, address);
        String customerJson = gson.toJson(newCustomer);

        ClientRequest request = new ClientRequest("CUSTOMER", "CREATE", (String) customerJson);
        String json = sendRequest(request);

        Type type = new TypeToken<ServerResponse<Customer>>() {}.getType();
        ServerResponse<Customer> response = gson.fromJson(json, type);

        System.out.println("Status: " + response.getStatus());
        System.out.println("Message: " + response.getMessage());
        if (response.getData() != null) {
            System.out.println("Created Customer: " + response.getData());
        }
    }

    private void createVehicle(Scanner kb) throws Exception {
        System.out.println("\n--- Create New Vehicle ---");
        System.out.print("Customer ID: ");
        int customerId = Integer.parseInt(kb.nextLine());
        System.out.print("Make: ");
        String make = kb.nextLine();
        System.out.print("Model: ");
        String model = kb.nextLine();
        System.out.print("Registration Number: ");
        String regNumber = kb.nextLine();
        System.out.print("Year: ");
        int year = Integer.parseInt(kb.nextLine());

        Vehicle newVehicle = new Vehicle(customerId, make, model, regNumber, year);
        String vehicleJson = gson.toJson(newVehicle);

        ClientRequest request = new ClientRequest("VEHICLE", "CREATE", (String) vehicleJson);
        String json = sendRequest(request);

        Type type = new TypeToken<ServerResponse<Vehicle>>() {}.getType();
        ServerResponse<Vehicle> response = gson.fromJson(json, type);

        System.out.println("Status: " + response.getStatus());
        System.out.println("Message: " + response.getMessage());
        if (response.getData() != null) {
            System.out.println("Created Vehicle: " + response.getData());
        }
    }

    private void createServiceJob(Scanner kb) throws Exception {
        System.out.println("\n--- Create New Service Job ---");
        System.out.print("Vehicle ID: ");
        int vehicleId = Integer.parseInt(kb.nextLine());
        System.out.print("Description: ");
        String description = kb.nextLine();
        System.out.print("Status (PENDING/IN_PROGRESS/COMPLETED): ");
        String status = kb.nextLine();
        System.out.print("Cost: ");
        double cost = Double.parseDouble(kb.nextLine());
        System.out.print("Date Created (YYYY-MM-DD): ");
        String dateCreated = kb.nextLine();

        ServiceJob newJob = new ServiceJob(vehicleId, description, status, cost, dateCreated);
        String jobJson = gson.toJson(newJob);

        ClientRequest request = new ClientRequest("SERVICEJOB", "CREATE", (String) jobJson);
        String json = sendRequest(request);

        Type type = new TypeToken<ServerResponse<ServiceJob>>() {}.getType();
        ServerResponse<ServiceJob> response = gson.fromJson(json, type);

        System.out.println("Status: " + response.getStatus());
        System.out.println("Message: " + response.getMessage());
        if (response.getData() != null) {
            System.out.println("Created Service Job: " + response.getData());
        }
    }

    // ========== UPDATE METHODS (F15) ==========

    private void updateCustomer(Scanner kb) throws Exception {
        System.out.println("\n--- Update Customer ---");
        System.out.print("Customer ID to update: ");
        int id = Integer.parseInt(kb.nextLine());

        System.out.println("Enter new details (leave blank to keep existing):");
        System.out.print("First Name: ");
        String firstName = kb.nextLine();
        System.out.print("Last Name: ");
        String lastName = kb.nextLine();
        System.out.print("Phone Number: ");
        String phone = kb.nextLine();
        System.out.print("Email: ");
        String email = kb.nextLine();
        System.out.print("Address: ");
        String address = kb.nextLine();

        // First get existing customer to fill in blanks
        ClientRequest getRequest = new ClientRequest("CUSTOMER", "GET_BY_ID", (Integer) id);
        String getJson = sendRequest(getRequest);
        Type getType = new TypeToken<ServerResponse<Customer>>() {}.getType();
        ServerResponse<Customer> getResponse = gson.fromJson(getJson, getType);

        if (getResponse.getData() == null) {
            System.out.println("Customer not found!");
            return;
        }

        Customer existing = getResponse.getData();

        // Use existing values if new ones are blank
        Customer updatedCustomer = new Customer(
                id,
                firstName.isBlank() ? existing.getFirstName() : firstName,
                lastName.isBlank() ? existing.getLastName() : lastName,
                phone.isBlank() ? existing.getPhoneNumber() : phone,
                email.isBlank() ? existing.getEmail() : email,
                address.isBlank() ? existing.getAddress() : address
        );

        String customerJson = gson.toJson(updatedCustomer);
        ClientRequest request = new ClientRequest("CUSTOMER", "UPDATE", (Integer) id, (String) customerJson);
        String json = sendRequest(request);

        Type type = new TypeToken<ServerResponse<Customer>>() {}.getType();
        ServerResponse<Customer> response = gson.fromJson(json, type);

        System.out.println("Status: " + response.getStatus());
        System.out.println("Message: " + response.getMessage());
        if (response.getData() != null) {
            System.out.println("Updated Customer: " + response.getData());
        }
    }

    private void updateVehicle(Scanner kb) throws Exception {
        System.out.println("\n--- Update Vehicle ---");
        System.out.print("Vehicle ID to update: ");
        int id = Integer.parseInt(kb.nextLine());

        System.out.println("Enter new details (leave blank to keep existing):");
        System.out.print("Customer ID: ");
        String customerIdStr = kb.nextLine();
        System.out.print("Make: ");
        String make = kb.nextLine();
        System.out.print("Model: ");
        String model = kb.nextLine();
        System.out.print("Registration Number: ");
        String regNumber = kb.nextLine();
        System.out.print("Year: ");
        String yearStr = kb.nextLine();

        // First get existing vehicle
        ClientRequest getRequest = new ClientRequest("VEHICLE", "GET_BY_ID", (Integer) id);
        String getJson = sendRequest(getRequest);
        Type getType = new TypeToken<ServerResponse<Vehicle>>() {}.getType();
        ServerResponse<Vehicle> getResponse = gson.fromJson(getJson, getType);

        if (getResponse.getData() == null) {
            System.out.println("Vehicle not found!");
            return;
        }

        Vehicle existing = getResponse.getData();

        Vehicle updatedVehicle = new Vehicle(
                id,
                customerIdStr.isBlank() ? existing.getCustomerId() : Integer.parseInt(customerIdStr),
                make.isBlank() ? existing.getMake() : make,
                model.isBlank() ? existing.getModel() : model,
                regNumber.isBlank() ? existing.getRegistrationNumber() : regNumber,
                yearStr.isBlank() ? existing.getYear() : Integer.parseInt(yearStr)
        );

        String vehicleJson = gson.toJson(updatedVehicle);
        ClientRequest request = new ClientRequest("VEHICLE", "UPDATE", (Integer) id, (String) vehicleJson);
        String json = sendRequest(request);

        Type type = new TypeToken<ServerResponse<Vehicle>>() {}.getType();
        ServerResponse<Vehicle> response = gson.fromJson(json, type);

        System.out.println("Status: " + response.getStatus());
        System.out.println("Message: " + response.getMessage());
        if (response.getData() != null) {
            System.out.println("Updated Vehicle: " + response.getData());
        }
    }

    private void updateServiceJob(Scanner kb) throws Exception {
        System.out.println("\n--- Update Service Job ---");
        System.out.print("Service Job ID to update: ");
        int id = Integer.parseInt(kb.nextLine());

        System.out.println("Enter new details (leave blank to keep existing):");
        System.out.print("Vehicle ID: ");
        String vehicleIdStr = kb.nextLine();
        System.out.print("Description: ");
        String description = kb.nextLine();
        System.out.print("Status (PENDING/IN_PROGRESS/COMPLETED): ");
        String status = kb.nextLine();
        System.out.print("Cost: ");
        String costStr = kb.nextLine();
        System.out.print("Date Created (YYYY-MM-DD): ");
        String dateCreated = kb.nextLine();

        // First get existing job
        ClientRequest getRequest = new ClientRequest("SERVICEJOB", "GET_BY_ID", (Integer) id);
        String getJson = sendRequest(getRequest);
        Type getType = new TypeToken<ServerResponse<ServiceJob>>() {}.getType();
        ServerResponse<ServiceJob> getResponse = gson.fromJson(getJson, getType);

        if (getResponse.getData() == null) {
            System.out.println("Service job not found!");
            return;
        }

        ServiceJob existing = getResponse.getData();

        ServiceJob updatedJob = new ServiceJob(
                id,
                vehicleIdStr.isBlank() ? existing.getVehicleId() : Integer.parseInt(vehicleIdStr),
                description.isBlank() ? existing.getDescription() : description,
                status.isBlank() ? existing.getStatus() : status,
                costStr.isBlank() ? existing.getCost() : Double.parseDouble(costStr),
                dateCreated.isBlank() ? existing.getDateCreated() : dateCreated
        );

        String jobJson = gson.toJson(updatedJob);
        ClientRequest request = new ClientRequest("SERVICEJOB", "UPDATE", (Integer) id, (String) jobJson);
        String json = sendRequest(request);

        Type type = new TypeToken<ServerResponse<ServiceJob>>() {}.getType();
        ServerResponse<ServiceJob> response = gson.fromJson(json, type);

        System.out.println("Status: " + response.getStatus());
        System.out.println("Message: " + response.getMessage());
        if (response.getData() != null) {
            System.out.println("Updated Service Job: " + response.getData());
        }
    }

    // ========== DELETE METHODS (F14) ==========

    private void deleteCustomer(Scanner kb) throws Exception {
        System.out.println("\n--- Delete Customer ---");
        System.out.print("Customer ID to delete: ");
        int id = Integer.parseInt(kb.nextLine());

        System.out.print("Are you sure? (y/n): ");
        String confirm = kb.nextLine();

        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Delete cancelled.");
            return;
        }

        ClientRequest request = new ClientRequest("CUSTOMER", "DELETE", (Integer) id);
        String json = sendRequest(request);

        Type type = new TypeToken<ServerResponse<Void>>() {}.getType();
        ServerResponse<Void> response = gson.fromJson(json, type);

        System.out.println("Status: " + response.getStatus());
        System.out.println("Message: " + response.getMessage());
    }

    private void deleteVehicle(Scanner kb) throws Exception {
        System.out.println("\n--- Delete Vehicle ---");
        System.out.print("Vehicle ID to delete: ");
        int id = Integer.parseInt(kb.nextLine());

        System.out.print("Are you sure? (y/n): ");
        String confirm = kb.nextLine();

        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Delete cancelled.");
            return;
        }

        ClientRequest request = new ClientRequest("VEHICLE", "DELETE", (Integer) id);
        String json = sendRequest(request);

        Type type = new TypeToken<ServerResponse<Void>>() {}.getType();
        ServerResponse<Void> response = gson.fromJson(json, type);

        System.out.println("Status: " + response.getStatus());
        System.out.println("Message: " + response.getMessage());
    }

    private void deleteServiceJob(Scanner kb) throws Exception {
        System.out.println("\n--- Delete Service Job ---");
        System.out.print("Service Job ID to delete: ");
        int id = Integer.parseInt(kb.nextLine());

        System.out.print("Are you sure? (y/n): ");
        String confirm = kb.nextLine();

        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Delete cancelled.");
            return;
        }

        ClientRequest request = new ClientRequest("SERVICEJOB", "DELETE", (Integer) id);
        String json = sendRequest(request);

        Type type = new TypeToken<ServerResponse<Void>>() {}.getType();
        ServerResponse<Void> response = gson.fromJson(json, type);

        System.out.println("Status: " + response.getStatus());
        System.out.println("Message: " + response.getMessage());
    }
}