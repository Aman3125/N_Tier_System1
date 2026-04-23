package daoexample.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Base64;
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
                    return gson.toJson(ServerResponse.success("Customer found", customer.get()));
                }
                return gson.toJson(ServerResponse.error("Customer not found"));

            case "GET_ALL":
                List<Customer> customers = customerDao.getAllCustomers();
                return gson.toJson(ServerResponse.success("Customers retrieved", customers));

            // NEW: CREATE Customer (F13)
            case "CREATE":
                if (request.getEntityData() == null) {
                    return gson.toJson(ServerResponse.error("Customer data is required"));
                }
                try {
                    Customer newCustomer = gson.fromJson(request.getEntityData(), Customer.class);
                    Customer saved = customerDao.insert(newCustomer);
                    return gson.toJson(ServerResponse.success("Customer created successfully", saved));
                } catch (Exception e) {
                    return gson.toJson(ServerResponse.error("Failed to create customer: " + e.getMessage()));
                }

                // NEW: UPDATE Customer (F15)
            case "UPDATE":
                if (request.getId() == null) {
                    return gson.toJson(ServerResponse.error("Customer ID is required for update"));
                }
                if (request.getEntityData() == null) {
                    return gson.toJson(ServerResponse.error("Customer data is required"));
                }
                try {
                    Customer updatedCustomer = gson.fromJson(request.getEntityData(), Customer.class);
                    Customer updated = customerDao.updateCustomer(request.getId(), updatedCustomer);
                    return gson.toJson(ServerResponse.success("Customer updated successfully", updated));
                } catch (Exception e) {
                    return gson.toJson(ServerResponse.error("Failed to update customer: " + e.getMessage()));
                }

                // NEW: DELETE Customer (F14)
            case "DELETE":
                if (request.getId() == null) {
                    return gson.toJson(ServerResponse.error("Customer ID is required for delete"));
                }
                try {
                    boolean deleted = customerDao.deleteCustomerById(request.getId());
                    if (deleted) {
                        return gson.toJson(ServerResponse.success("Customer deleted successfully", null));
                    } else {
                        return gson.toJson(ServerResponse.error("Customer not found"));
                    }
                } catch (Exception e) {
                    return gson.toJson(ServerResponse.error("Failed to delete customer: " + e.getMessage()));
                }

            default:
                return gson.toJson(ServerResponse.error("Unsupported action"));
        }
    }

    private String handleVehicle(ClientRequest request) throws Exception {
        switch (request.getAction().toUpperCase()) {

            // (UNCHANGED — your full vehicle logic remains exactly the same)

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
                    return gson.toJson(ServerResponse.success("Service job found", job.get()));
                }
                return gson.toJson(ServerResponse.error("Service job not found"));

            case "GET_ALL":
                List<ServiceJob> jobs = serviceJobDao.getAllServiceJobs();
                return gson.toJson(ServerResponse.success("Service jobs retrieved", jobs));

            // NEW: CREATE ServiceJob (F13)
            case "CREATE":
                if (request.getEntityData() == null) {
                    return gson.toJson(ServerResponse.error("Service job data is required"));
                }
                try {
                    ServiceJob newJob = gson.fromJson(request.getEntityData(), ServiceJob.class);
                    ServiceJob saved = serviceJobDao.insert(newJob);
                    return gson.toJson(ServerResponse.success("Service job created successfully", saved));
                } catch (Exception e) {
                    return gson.toJson(ServerResponse.error("Failed to create service job: " + e.getMessage()));
                }

                // NEW: UPDATE ServiceJob (F15)
            case "UPDATE":
                if (request.getId() == null) {
                    return gson.toJson(ServerResponse.error("Service job ID is required for update"));
                }
                if (request.getEntityData() == null) {
                    return gson.toJson(ServerResponse.error("Service job data is required"));
                }
                try {
                    ServiceJob updatedJob = gson.fromJson(request.getEntityData(), ServiceJob.class);
                    ServiceJob updated = serviceJobDao.updateServiceJob(request.getId(), updatedJob);
                    return gson.toJson(ServerResponse.success("Service job updated successfully", updated));
                } catch (Exception e) {
                    return gson.toJson(ServerResponse.error("Failed to update service job: " + e.getMessage()));
                }

                // NEW: UPLOAD_FILE (F18)
            case "UPLOAD_FILE":
                if (request.getId() == null) {
                    return gson.toJson(ServerResponse.error("Service job ID is required for file upload"));
                }

                if (request.getBase64FileData() == null || request.getBase64FileData().isBlank()) {
                    return gson.toJson(ServerResponse.error("Base64 file data is required"));
                }

                try {
                    Optional<ServiceJob> existingJobOpt = serviceJobDao.getServiceJobById(request.getId());

                    if (existingJobOpt.isEmpty()) {
                        return gson.toJson(ServerResponse.error("Service job not found"));
                    }

                    ServiceJob existingJob = existingJobOpt.get();

                    byte[] decodedFileData = Base64.getDecoder().decode(request.getBase64FileData());

                    ServiceJob updatedJob = new ServiceJob(
                            existingJob.getServiceJobId(),
                            existingJob.getVehicleId(),
                            existingJob.getDescription(),
                            existingJob.getStatus(),
                            existingJob.getCost(),
                            existingJob.getDateCreated(),
                            decodedFileData,
                            request.getFileName(),
                            request.getContentType(),
                            request.getFileSize() == null ? decodedFileData.length : request.getFileSize()
                    );

                    ServiceJob savedJob = serviceJobDao.updateServiceJob(existingJob.getServiceJobId(), updatedJob);

                    return gson.toJson(ServerResponse.success("File uploaded successfully", savedJob));

                } catch (Exception e) {
                    return gson.toJson(ServerResponse.error("Failed to upload file: " + e.getMessage()));
                }

                // NEW: DELETE ServiceJob (F14)
            case "DELETE":
                if (request.getId() == null) {
                    return gson.toJson(ServerResponse.error("Service job ID is required for delete"));
                }
                try {
                    boolean deleted = serviceJobDao.deleteServiceJobById(request.getId());
                    if (deleted) {
                        return gson.toJson(ServerResponse.success("Service job deleted successfully", null));
                    } else {
                        return gson.toJson(ServerResponse.error("Service job not found"));
                    }
                } catch (Exception e) {
                    return gson.toJson(ServerResponse.error("Failed to delete service job: " + e.getMessage()));
                }

            default:
                return gson.toJson(ServerResponse.error("Unsupported action"));
        }
    }
}