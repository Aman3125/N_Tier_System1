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

// F12: Client sends JSON request, receives ServerResponse<T> JSON, parses and displays it
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
            System.out.println("\n=== GARAGE CLIENT MENU ===");
            System.out.println("=== READ OPERATIONS ===");
            System.out.println("1. Display Customer by ID");
            System.out.println("2. Display All Customers");
            System.out.println("3. Display Vehicle by ID");
            System.out.println("4. Display All Vehicles");
            System.out.println("5. Display ServiceJob by ID");
            System.out.println("6. Display All ServiceJobs");
            System.out.println("\n=== WRITE OPERATIONS ===");
            System.out.println("7. Create Customer");
            System.out.println("8. Update Customer");
            System.out.println("9. Delete Customer");
            System.out.println("10. Create Vehicle");
            System.out.println("11. Update Vehicle");
            System.out.println("12. Delete Vehicle");
            System.out.println("13. Create Service Job");
            System.out.println("14. Update Service Job");
            System.out.println("15. Delete Service Job");
            System.out.println("16. Upload File to Service Job");
            System.out.println("17. Download File from Service Job");
            System.out.println("\n=== F20: FILE METADATA ===");
            System.out.println("18. Get File Metadata (file info only - no download)");
            System.out.println("\n0. Exit");
            System.out.print("Choose option: ");

            String option = kb.nextLine();

            try {
                switch (option) {
                    case "1": System.out.print("Enter customer ID: ");
                        displayCustomerById(Integer.parseInt(kb.nextLine())); break;
                    case "2": displayAllCustomers(); break;
                    case "3": System.out.print("Enter vehicle ID: ");
                        displayVehicleById(Integer.parseInt(kb.nextLine())); break;
                    case "4": displayAllVehicles(); break;
                    case "5": System.out.print("Enter service job ID: ");
                        displayServiceJobById(Integer.parseInt(kb.nextLine())); break;
                    case "6": displayAllServiceJobs(); break;
                    case "7": createCustomer(kb); break;
                    case "8": updateCustomer(kb); break;
                    case "9": deleteCustomer(kb); break;
                    case "10": createVehicle(kb); break;
                    case "11": updateVehicle(kb); break;
                    case "12": deleteVehicle(kb); break;
                    case "13": createServiceJob(kb); break;
                    case "14": updateServiceJob(kb); break;
                    case "15": deleteServiceJob(kb); break;
                    case "16": uploadFileToServiceJob(kb); break;
                    case "17": downloadFileFromServiceJob(kb); break;
                    case "18": getFileMetadata(kb); break;
                    case "0":
                        // ===== F21: DISCONNECT / EXIT =====
                        // Send disconnect message before closing
                        sendDisconnect();
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

    // Sends request to server and returns response
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

    // ===== F21: Send disconnect message to server =====
    private void sendDisconnect() {
        try {
            ClientRequest request = new ClientRequest("SYSTEM", "DISCONNECT", (Integer) null);
            sendRequest(request);
            System.out.println("Disconnected from server.");
        } catch (Exception e) {
            // Server might have closed connection, that's fine
        }
    }

    // Encode file to Base64 for upload
    private String encodeFileToBase64(String filePath) throws Exception {
        byte[] fileBytes = Files.readAllBytes(Path.of(filePath));
        return Base64.getEncoder().encodeToString(fileBytes);
    }

    private int getFileSize(String filePath) throws Exception {
        return (int) Files.size(Path.of(filePath));
    }

    // ===== F20: Get file metadata without downloading =====
    private void getFileMetadata(Scanner kb) throws Exception {
        System.out.println("\n--- GET FILE METADATA ---");
        System.out.print("Service Job ID: ");
        int serviceJobId = Integer.parseInt(kb.nextLine());

        ClientRequest request = new ClientRequest("SERVICEJOB", "GET_METADATA", serviceJobId);
        String json = sendRequest(request);

        if (json == null || json.isBlank()) {
            System.out.println("No response from server.");
            return;
        }

        Type type = new TypeToken<ServerResponse<ServiceJob>>() {}.getType();
        ServerResponse<ServiceJob> response = gson.fromJson(json, type);

        System.out.println("Status: " + response.getStatus());
        System.out.println("Message: " + response.getMessage());

        if (response.getData() != null) {
            ServiceJob metadata = response.getData();
            System.out.println("\n=== FILE INFORMATION (No file downloaded) ===");
            System.out.println("Service Job ID: " + metadata.getServiceJobId());
            System.out.println("Vehicle ID: " + metadata.getVehicleId());
            System.out.println("Description: " + metadata.getDescription());
            System.out.println("Status: " + metadata.getStatus());
            System.out.println("Cost: $" + metadata.getCost());
            System.out.println("Date Created: " + metadata.getDateCreated());
            System.out.println("File Name: " + metadata.getFileName());
            System.out.println("File Type: " + metadata.getContentType());
            System.out.println("File Size: " + metadata.getFileSize() + " bytes");
            System.out.println("(Binary data was NOT downloaded - this is just metadata)");
        }
    }

    // ========== READ METHODS ==========
    private void displayCustomerById(int id) throws Exception {
        ClientRequest request = new ClientRequest("CUSTOMER", "GET_BY_ID", id);
        String json = sendRequest(request);
        Type type = new TypeToken<ServerResponse<Customer>>() {}.getType();
        ServerResponse<Customer> response = gson.fromJson(json, type);
        System.out.println("Status: " + response.getStatus());
        if (response.getData() != null) {
            System.out.println("Data: " + response.getData());
        }
    }

    private void displayAllCustomers() throws Exception {
        ClientRequest request = new ClientRequest("CUSTOMER", "GET_ALL", (Integer) null);
        String json = sendRequest(request);
        Type type = new TypeToken<ServerResponse<List<Customer>>>() {}.getType();
        ServerResponse<List<Customer>> response = gson.fromJson(json, type);
        System.out.println("Status: " + response.getStatus());
        if (response.getData() != null) {
            for (Customer c : response.getData()) {
                System.out.println(c);
            }
        }
    }

    private void displayVehicleById(int id) throws Exception {
        ClientRequest request = new ClientRequest("VEHICLE", "GET_BY_ID", id);
        String json = sendRequest(request);
        Type type = new TypeToken<ServerResponse<Vehicle>>() {}.getType();
        ServerResponse<Vehicle> response = gson.fromJson(json, type);
        System.out.println("Status: " + response.getStatus());
        if (response.getData() != null) {
            System.out.println("Data: " + response.getData());
        }
    }

    private void displayAllVehicles() throws Exception {
        ClientRequest request = new ClientRequest("VEHICLE", "GET_ALL", (Integer) null);
        String json = sendRequest(request);
        Type type = new TypeToken<ServerResponse<List<Vehicle>>>() {}.getType();
        ServerResponse<List<Vehicle>> response = gson.fromJson(json, type);
        System.out.println("Status: " + response.getStatus());
        if (response.getData() != null) {
            for (Vehicle v : response.getData()) {
                System.out.println(v);
            }
        }
    }

    private void displayServiceJobById(int id) throws Exception {
        ClientRequest request = new ClientRequest("SERVICEJOB", "GET_BY_ID", id);
        String json = sendRequest(request);
        Type type = new TypeToken<ServerResponse<ServiceJob>>() {}.getType();
        ServerResponse<ServiceJob> response = gson.fromJson(json, type);
        System.out.println("Status: " + response.getStatus());
        if (response.getData() != null) {
            System.out.println("Data: " + response.getData());
        }
    }

    private void displayAllServiceJobs() throws Exception {
        ClientRequest request = new ClientRequest("SERVICEJOB", "GET_ALL", (Integer) null);
        String json = sendRequest(request);
        Type type = new TypeToken<ServerResponse<List<ServiceJob>>>() {}.getType();
        ServerResponse<List<ServiceJob>> response = gson.fromJson(json, type);
        System.out.println("Status: " + response.getStatus());
        if (response.getData() != null) {
            for (ServiceJob s : response.getData()) {
                System.out.println(s);
            }
        }
    }

    // ========== CREATE METHODS ==========
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
        ClientRequest request = new ClientRequest("CUSTOMER", "CREATE", customerJson);
        String json = sendRequest(request);
        Type type = new TypeToken<ServerResponse<Customer>>() {}.getType();
        ServerResponse<Customer> response = gson.fromJson(json, type);
        System.out.println("Status: " + response.getStatus());
        System.out.println("Message: " + response.getMessage());
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
        ClientRequest request = new ClientRequest("VEHICLE", "CREATE", vehicleJson);
        String json = sendRequest(request);
        Type type = new TypeToken<ServerResponse<Vehicle>>() {}.getType();
        ServerResponse<Vehicle> response = gson.fromJson(json, type);
        System.out.println("Status: " + response.getStatus());
        System.out.println("Message: " + response.getMessage());
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
        ClientRequest request = new ClientRequest("SERVICEJOB", "CREATE", jobJson);
        String json = sendRequest(request);
        Type type = new TypeToken<ServerResponse<ServiceJob>>() {}.getType();
        ServerResponse<ServiceJob> response = gson.fromJson(json, type);
        System.out.println("Status: " + response.getStatus());
        System.out.println("Message: " + response.getMessage());
    }

    // ========== UPDATE METHODS ==========
    private void updateCustomer(Scanner kb) throws Exception {
        System.out.println("\n--- Update Customer ---");
        System.out.print("Customer ID to update: ");
        int id = Integer.parseInt(kb.nextLine());
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

        Customer updatedCustomer = new Customer(id, firstName, lastName, phone, email, address);
        String customerJson = gson.toJson(updatedCustomer);
        ClientRequest request = new ClientRequest("CUSTOMER", "UPDATE", id, customerJson);
        String json = sendRequest(request);
        Type type = new TypeToken<ServerResponse<Customer>>() {}.getType();
        ServerResponse<Customer> response = gson.fromJson(json, type);
        System.out.println("Status: " + response.getStatus());
        System.out.println("Message: " + response.getMessage());
    }

    private void updateVehicle(Scanner kb) throws Exception {
        System.out.println("\n--- Update Vehicle ---");
        System.out.print("Vehicle ID to update: ");
        int id = Integer.parseInt(kb.nextLine());
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

        Vehicle updatedVehicle = new Vehicle(id, customerId, make, model, regNumber, year);
        String vehicleJson = gson.toJson(updatedVehicle);
        ClientRequest request = new ClientRequest("VEHICLE", "UPDATE", id, vehicleJson);
        String json = sendRequest(request);
        Type type = new TypeToken<ServerResponse<Vehicle>>() {}.getType();
        ServerResponse<Vehicle> response = gson.fromJson(json, type);
        System.out.println("Status: " + response.getStatus());
        System.out.println("Message: " + response.getMessage());
    }

    private void updateServiceJob(Scanner kb) throws Exception {
        System.out.println("\n--- Update Service Job ---");
        System.out.print("Service Job ID to update: ");
        int id = Integer.parseInt(kb.nextLine());
        System.out.print("Vehicle ID: ");
        int vehicleId = Integer.parseInt(kb.nextLine());
        System.out.print("Description: ");
        String description = kb.nextLine();
        System.out.print("Status: ");
        String status = kb.nextLine();
        System.out.print("Cost: ");
        double cost = Double.parseDouble(kb.nextLine());
        System.out.print("Date Created: ");
        String dateCreated = kb.nextLine();

        ServiceJob updatedJob = new ServiceJob(id, vehicleId, description, status, cost, dateCreated);
        String jobJson = gson.toJson(updatedJob);
        ClientRequest request = new ClientRequest("SERVICEJOB", "UPDATE", id, jobJson);
        String json = sendRequest(request);
        Type type = new TypeToken<ServerResponse<ServiceJob>>() {}.getType();
        ServerResponse<ServiceJob> response = gson.fromJson(json, type);
        System.out.println("Status: " + response.getStatus());
        System.out.println("Message: " + response.getMessage());
    }

    // ========== DELETE METHODS ==========
    private void deleteCustomer(Scanner kb) throws Exception {
        System.out.print("Customer ID to delete: ");
        int id = Integer.parseInt(kb.nextLine());
        System.out.print("Are you sure? (y/n): ");
        String confirm = kb.nextLine();
        if (!confirm.equalsIgnoreCase("y")) return;

        ClientRequest request = new ClientRequest("CUSTOMER", "DELETE", id);
        String json = sendRequest(request);
        Type type = new TypeToken<ServerResponse<Void>>() {}.getType();
        ServerResponse<Void> response = gson.fromJson(json, type);
        System.out.println("Status: " + response.getStatus());
        System.out.println("Message: " + response.getMessage());
    }

    private void deleteVehicle(Scanner kb) throws Exception {
        System.out.print("Vehicle ID to delete: ");
        int id = Integer.parseInt(kb.nextLine());
        System.out.print("Are you sure? (y/n): ");
        String confirm = kb.nextLine();
        if (!confirm.equalsIgnoreCase("y")) return;

        ClientRequest request = new ClientRequest("VEHICLE", "DELETE", id);
        String json = sendRequest(request);
        Type type = new TypeToken<ServerResponse<Void>>() {}.getType();
        ServerResponse<Void> response = gson.fromJson(json, type);
        System.out.println("Status: " + response.getStatus());
        System.out.println("Message: " + response.getMessage());
    }

    private void deleteServiceJob(Scanner kb) throws Exception {
        System.out.print("Service Job ID to delete: ");
        int id = Integer.parseInt(kb.nextLine());
        System.out.print("Are you sure? (y/n): ");
        String confirm = kb.nextLine();
        if (!confirm.equalsIgnoreCase("y")) return;

        ClientRequest request = new ClientRequest("SERVICEJOB", "DELETE", id);
        String json = sendRequest(request);
        Type type = new TypeToken<ServerResponse<Void>>() {}.getType();
        ServerResponse<Void> response = gson.fromJson(json, type);
        System.out.println("Status: " + response.getStatus());
        System.out.println("Message: " + response.getMessage());
    }

    // ========== FILE UPLOAD/DOWNLOAD ==========
    private void uploadFileToServiceJob(Scanner kb) throws Exception {
        System.out.println("\n--- Upload File to Service Job ---");
        System.out.print("Service Job ID: ");
        int serviceJobId = Integer.parseInt(kb.nextLine());
        System.out.print("File Path: ");
        String filePath = kb.nextLine();
        System.out.print("Content Type (e.g., image/png): ");
        String contentType = kb.nextLine();

        String base64FileData = encodeFileToBase64(filePath);
        String fileName = Path.of(filePath).getFileName().toString();
        int fileSize = getFileSize(filePath);

        ClientRequest request = new ClientRequest("SERVICEJOB", "UPLOAD_FILE", serviceJobId, null,
                base64FileData, fileName, contentType, fileSize);
        String json = sendRequest(request);
        Type type = new TypeToken<ServerResponse<ServiceJob>>() {}.getType();
        ServerResponse<ServiceJob> response = gson.fromJson(json, type);
        System.out.println("Status: " + response.getStatus());
        System.out.println("Message: " + response.getMessage());
    }

    private void downloadFileFromServiceJob(Scanner kb) throws Exception {
        System.out.println("\n--- Download File from Service Job ---");
        System.out.print("Service Job ID: ");
        int serviceJobId = Integer.parseInt(kb.nextLine());

        ClientRequest request = new ClientRequest("SERVICEJOB", "DOWNLOAD_FILE", serviceJobId);
        String json = sendRequest(request);
        Type type = new TypeToken<ServerResponse<ServiceJob>>() {}.getType();
        ServerResponse<ServiceJob> response = gson.fromJson(json, type);

        System.out.println("Status: " + response.getStatus());
        if (response.getData() != null && response.getData().getFileData() != null) {
            ServiceJob job = response.getData();
            System.out.print("Enter folder path to save file: ");
            String folderPath = kb.nextLine();
            Path outputPath = Path.of(folderPath, job.getFileName());
            Files.write(outputPath, job.getFileData());
            System.out.println("File saved to: " + outputPath);
        }
    }
}