package daoexample.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Optional;

import com.google.gson.Gson;
import daoexample.dao.CustomerDao;
import daoexample.dao.JdbcCustomerDao;
import daoexample.dao.JdbcServiceJobDao;
import daoexample.dao.JdbcVehicleDao;
import daoexample.dao.ServiceJobDao;
import daoexample.dao.VehicleDao;
import daoexample.domain.Customer;
import daoexample.domain.ServiceJob;
import daoexample.domain.Vehicle;


// F10: Each connected client is handled on a separate thread.
// F11: All replies are wrapped in ServerResponse<T> and sent as JSON.
public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final Gson gson = new Gson();

    private final CustomerDao customerDao = new JdbcCustomerDao();
    private final VehicleDao vehicleDao = new JdbcVehicleDao();
    private final ServiceJobDao serviceJobDao = new JdbcServiceJobDao();

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String requestJson = in.readLine();

            if (requestJson == null || requestJson.isBlank()) {
                out.println(gson.toJson(ServerResponse.error("Empty request received.")));
                return;
            }

            ClientRequest request = gson.fromJson(requestJson, ClientRequest.class);
            String responseJson = handleRequest(request);

            out.println(responseJson);

        } catch (Exception e) {
            try {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println(gson.toJson(ServerResponse.error("Server error: " + e.getMessage())));
            } catch (Exception ignored) {
            }
        } finally {
            try {
                clientSocket.close();
            } catch (Exception ignored) {
            }
        }
    }

    private String handleRequest(ClientRequest request) throws Exception {
        if (request == null) {
            return gson.toJson(ServerResponse.error("Invalid request"));
        }

        if (request.getEntity() == null || request.getAction() == null) {
            return gson.toJson(ServerResponse.error("Request must contain entity and action"));
        }

        switch (request.getEntity().toUpperCase()) {
            case "CUSTOMER":
                return handleCustomer(request);

            case "VEHICLE":
                return handleVehicle(request);

            case "SERVICEJOB":
                return handleServiceJob(request);

            default:
                return gson.toJson(ServerResponse.error("Unknown entity"));
        }
    }

    private String handleCustomer(ClientRequest request) throws Exception {
        switch (request.getAction().toUpperCase()) {
            case "GET_BY_ID":
                if (request.getId() == null) {
                    return gson.toJson(ServerResponse.error("Customer ID is required"));
                }

                Optional<Customer> customer = customerDao.getCustomerById(request.getId());
                if (customer.isPresent()) {
                    ServerResponse<Customer> response =
                            ServerResponse.success("Customer found", customer.get());
                    return gson.toJson(response);
                }
                return gson.toJson(ServerResponse.error("Customer not found"));

            case "GET_ALL":
                List<Customer> customers = customerDao.getAllCustomers();
                ServerResponse<List<Customer>> allCustomersResponse =
                        ServerResponse.success("Customers retrieved", customers);
                return gson.toJson(allCustomersResponse);

            default:
                return gson.toJson(ServerResponse.error("Unsupported action"));
        }
    }

    private String handleVehicle(ClientRequest request) throws Exception {
        switch (request.getAction().toUpperCase()) {
            case "GET_BY_ID":
                if (request.getId() == null) {
                    return gson.toJson(ServerResponse.error("Vehicle ID is required"));
                }

                Optional<Vehicle> vehicle = vehicleDao.getVehicleById(request.getId());
                if (vehicle.isPresent()) {
                    ServerResponse<Vehicle> response =
                            ServerResponse.success("Vehicle found", vehicle.get());
                    return gson.toJson(response);
                }
                return gson.toJson(ServerResponse.error("Vehicle not found"));

            case "GET_ALL":
                List<Vehicle> vehicles = vehicleDao.getAllVehicles();
                ServerResponse<List<Vehicle>> allVehiclesResponse =
                        ServerResponse.success("Vehicles retrieved", vehicles);
                return gson.toJson(allVehiclesResponse);

            default:
                return gson.toJson(ServerResponse.error("Unsupported action"));
        }
    }

    private String handleServiceJob(ClientRequest request) throws Exception {
        switch (request.getAction().toUpperCase()) {
            case "GET_BY_ID":
                if (request.getId() == null) {
                    return gson.toJson(ServerResponse.error("Service job ID is required"));
                }

                Optional<ServiceJob> job = serviceJobDao.getServiceJobById(request.getId());
                if (job.isPresent()) {
                    ServerResponse<ServiceJob> response =
                            ServerResponse.success("Service job found", job.get());
                    return gson.toJson(response);
                }
                return gson.toJson(ServerResponse.error("Service job not found"));

            case "GET_ALL":
                List<ServiceJob> jobs = serviceJobDao.getAllServiceJobs();
                ServerResponse<List<ServiceJob>> allJobsResponse =
                        ServerResponse.success("Service jobs retrieved", jobs);
                return gson.toJson(allJobsResponse);

            default:
                return gson.toJson(ServerResponse.error("Unsupported action"));
        }
    }
}