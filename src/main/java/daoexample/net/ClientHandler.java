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
// F12: Server reads JSON request, calls DAO, returns JSON response.
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
            ClientRequest request = gson.fromJson(requestJson, ClientRequest.class);

            String responseJson = handleRequest(request);
            out.println(responseJson);

        } catch (Exception e) {
            try {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println(gson.toJson(ServerResponse.error("Server error: " + e.getMessage())));
            } catch (Exception ignored) {
            }
        }
    }

    private String handleRequest(ClientRequest request) throws Exception {
        if (request == null) {
            return gson.toJson(ServerResponse.error("Invalid request"));
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
                Optional<Customer> customer = customerDao.getCustomerById(request.getId());
                if (customer.isPresent()) {
                    return gson.toJson(ServerResponse.success("Customer found", customer.get()));
                }
                return gson.toJson(ServerResponse.error("Customer not found"));

            case "GET_ALL":
                List<Customer> customers = customerDao.getAllCustomers();
                return gson.toJson(ServerResponse.success("Customers retrieved", customers));

            default:
                return gson.toJson(ServerResponse.error("Unsupported action"));
        }
    }

    private String handleVehicle(ClientRequest request) throws Exception {
        switch (request.getAction().toUpperCase()) {
            case "GET_BY_ID":
                Optional<Vehicle> vehicle = vehicleDao.getVehicleById(request.getId());
                if (vehicle.isPresent()) {
                    return gson.toJson(ServerResponse.success("Vehicle found", vehicle.get()));
                }
                return gson.toJson(ServerResponse.error("Vehicle not found"));

            case "GET_ALL":
                List<Vehicle> vehicles = vehicleDao.getAllVehicles();
                return gson.toJson(ServerResponse.success("Vehicles retrieved", vehicles));

            default:
                return gson.toJson(ServerResponse.error("Unsupported action"));
        }
    }

    private String handleServiceJob(ClientRequest request) throws Exception {
        switch (request.getAction().toUpperCase()) {
            case "GET_BY_ID":
                Optional<ServiceJob> job = serviceJobDao.getServiceJobById(request.getId());
                if (job.isPresent()) {
                    return gson.toJson(ServerResponse.success("Service job found", job.get()));
                }
                return gson.toJson(ServerResponse.error("Service job not found"));

            case "GET_ALL":
                List<ServiceJob> jobs = serviceJobDao.getAllServiceJobs();
                return gson.toJson(ServerResponse.success("Service jobs retrieved", jobs));

            default:
                return gson.toJson(ServerResponse.error("Unsupported action"));
        }
    }
}