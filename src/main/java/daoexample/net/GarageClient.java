package daoexample.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;
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
            System.out.println("1. Display Customer by ID");
            System.out.println("2. Display All Customers");
            System.out.println("3. Display Vehicle by ID");
            System.out.println("4. Display All Vehicles");
            System.out.println("5. Display ServiceJob by ID");
            System.out.println("6. Display All ServiceJobs");
            System.out.println("0. Exit");
            System.out.print("Choose option: ");

            String option = kb.nextLine();

            try {
                switch (option) {
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
                    case "0":
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

    private void displayCustomerById(int id) throws Exception {
        ClientRequest request = new ClientRequest("CUSTOMER", "GET_BY_ID", id);
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

        System.out.println(response.getStatus());
        System.out.println(response.getMessage());
        System.out.println(response.getData());
    }

    private void displayAllCustomers() throws Exception {
        ClientRequest request = new ClientRequest("CUSTOMER", "GET_ALL", null);
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

        System.out.println(response.getStatus());
        System.out.println(response.getMessage());

        if (response.getData() != null) {
            for (Customer c : response.getData()) {
                System.out.println(c);
            }
        }
    }

    private void displayVehicleById(int id) throws Exception {
        ClientRequest request = new ClientRequest("VEHICLE", "GET_BY_ID", id);
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

        System.out.println(response.getStatus());
        System.out.println(response.getMessage());
        System.out.println(response.getData());
    }

    private void displayAllVehicles() throws Exception {
        ClientRequest request = new ClientRequest("VEHICLE", "GET_ALL", null);
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

        System.out.println(response.getStatus());
        System.out.println(response.getMessage());

        if (response.getData() != null) {
            for (Vehicle v : response.getData()) {
                System.out.println(v);
            }
        }
    }

    private void displayServiceJobById(int id) throws Exception {
        ClientRequest request = new ClientRequest("SERVICEJOB", "GET_BY_ID", id);
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

        System.out.println(response.getStatus());
        System.out.println(response.getMessage());
        System.out.println(response.getData());
    }

    private void displayAllServiceJobs() throws Exception {
        ClientRequest request = new ClientRequest("SERVICEJOB", "GET_ALL", null);
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

        System.out.println(response.getStatus());
        System.out.println(response.getMessage());

        if (response.getData() != null) {
            for (ServiceJob s : response.getData()) {
                System.out.println(s);
            }
        }
    }
}