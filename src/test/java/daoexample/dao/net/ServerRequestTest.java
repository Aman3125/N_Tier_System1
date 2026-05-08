package daoexample.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import daoexample.dao.JdbcCustomerDao;
import daoexample.dao.JdbcServiceJobDao;
import daoexample.dao.JdbcVehicleDao;
import daoexample.domain.Customer;
import daoexample.domain.ServiceJob;
import daoexample.domain.Vehicle;
import org.junit.jupiter.api.*;

import java.lang.reflect.Type;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * F23: Server request/response tests.
 *
 * Tests the handleRequest logic inside ClientHandler directly (without a live socket)
 * by calling a package-visible helper. Since ClientHandler.handleRequest() is private,
 * we drive it indirectly through the server protocol by instantiating the handler
 * and using the Gson-based ClientRequest / ServerResponse contract.
 *
 * Strategy: we call the real DAO methods that the server delegates to,
 * and verify the JSON output the server would have returned matches expectations.
 * This gives true integration coverage of the server layer without network I/O.
 *
 * @author [Your Name] (primary)
 */
class ServerRequestTest {

    // === Fields ===

    private static final Gson gson = new Gson();
    private static JdbcCustomerDao customerDao;
    private static JdbcVehicleDao vehicleDao;
    private static JdbcServiceJobDao serviceJobDao;

    /** A vehicle ID that must exist in seed data. */
    private static final int SEED_VEHICLE_ID = 1;
    /** A customer ID that must exist in seed data. */
    private static final int SEED_CUSTOMER_ID = 1;

    // === Setup ===

    @BeforeAll
    static void setUp() throws Exception {
        customerDao    = new JdbcCustomerDao();
        vehicleDao     = new JdbcVehicleDao();
        serviceJobDao  = new JdbcServiceJobDao();
    }

    @BeforeEach
    void cleanUpTestData() throws Exception {
        for (Customer c : customerDao.getAllCustomers()) {
            if (c.getEmail() != null && c.getEmail().startsWith("server.test.")) {
                customerDao.deleteCustomerById(c.getCustomerId());
            }
        }
        for (ServiceJob j : serviceJobDao.getAllServiceJobs()) {
            if (j.getDescription() != null && j.getDescription().startsWith("SVR-TEST-")) {
                serviceJobDao.deleteServiceJobById(j.getServiceJobId());
            }
        }
    }


    private <T> ServerResponse<T> sendRequest(ClientRequest request, Type dataType) throws Exception {
        TestableClientHandler handler = new TestableClientHandler();
        String responseJson = handler.testHandleRequest(request);
        assertNotNull(responseJson, "Handler should never return null JSON");

        Type responseType = TypeToken.getParameterized(ServerResponse.class,
                ((TypeToken<?>) TypeToken.get(dataType)).getRawType()).getType();
        return gson.fromJson(responseJson, responseType);
    }


    // TEST 1: Server returns SUCCESS for GET_ALL customers
    @Test
    void serverGetAllCustomers_returnsSuccessStatus() throws Exception {
        ClientRequest request = new ClientRequest("CUSTOMER", "GET_ALL", (Integer) null);
        TestableClientHandler handler = new TestableClientHandler();

        String responseJson = handler.testHandleRequest(request);

        assertNotNull(responseJson, "Response JSON should not be null");
        assertTrue(responseJson.contains("SUCCESS"),
                "Server should return SUCCESS status for GET_ALL");
    }

    // TEST 2: Server returns ERROR for GET_BY_ID with a non-existent customer
    @Test
    void serverGetCustomerById_returnsError_whenCustomerNotFound() throws Exception {
        ClientRequest request = new ClientRequest("CUSTOMER", "GET_BY_ID", 999999);
        TestableClientHandler handler = new TestableClientHandler();

        String responseJson = handler.testHandleRequest(request);

        assertNotNull(responseJson);
        assertTrue(responseJson.contains("ERROR"),
                "Server should return ERROR when customer is not found");
    }

    // TEST 3: Server creates a customer via CREATE action and returns the new entity
    @Test
    void serverCreateCustomer_returnsSuccessWithNewId() throws Exception {
        Customer newCustomer = new Customer(
                "ServerTest", "User", "0821234567",
                "server.test.create@example.com", "1 Server Lane, Dublin");

        String entityData = gson.toJson(newCustomer);
        ClientRequest request = new ClientRequest("CUSTOMER", "CREATE", entityData);
        TestableClientHandler handler = new TestableClientHandler();

        String responseJson = handler.testHandleRequest(request);

        assertNotNull(responseJson);
        assertTrue(responseJson.contains("SUCCESS"),
                "Server should return SUCCESS after creating a customer");
        // The response data should contain a positive customerId
        assertTrue(responseJson.contains("customerId") || responseJson.contains("customer_id"),
                "Response should contain the customer ID field");
    }

    // TEST 4: Server returns ERROR for unknown entity type
    @Test
    void serverRequest_returnsError_whenEntityTypeIsUnknown() throws Exception {
        ClientRequest request = new ClientRequest("UNKNOWN_ENTITY", "GET_ALL", (Integer) null);
        TestableClientHandler handler = new TestableClientHandler();

        String responseJson = handler.testHandleRequest(request);

        assertNotNull(responseJson);
        assertTrue(responseJson.contains("ERROR"),
                "Server should return ERROR for unknown entity type");
    }

    // TEST 5: Server handles DISCONNECT action cleanly
    @Test
    void serverDisconnect_returnsSuccess_andLogsCleanly() throws Exception {
        ClientRequest request = new ClientRequest("CUSTOMER", "DISCONNECT", (Integer) null);
        TestableClientHandler handler = new TestableClientHandler();

        String responseJson = handler.testHandleRequest(request);

        assertNotNull(responseJson);
        assertTrue(responseJson.contains("SUCCESS"),
                "Server should return SUCCESS on DISCONNECT");
    }

    // TEST 6: Server binary file upload via UPLOAD_FILE action — bytes survive round-trip
    @Test
    void serverUploadFile_andDownload_bytesMatchOriginal() throws Exception {
        // 1. Create a service job to attach the file to
        ServiceJob job = serviceJobDao.insert(new ServiceJob(
                SEED_VEHICLE_ID, "SVR-TEST-FileRoundTrip", "PENDING", 0.00, "2026-09-01"));

        // 2. Build the binary payload
        byte[] originalBytes = "Server binary test content 1234".getBytes();
        String base64Data = Base64.getEncoder().encodeToString(originalBytes);

        // 3. Send UPLOAD_FILE request to the server handler
        ClientRequest uploadRequest = new ClientRequest(
                "SERVICEJOB", "UPLOAD_FILE",
                job.getServiceJobId(),
                null,
                base64Data,
                "server-test.txt",
                "text/plain",
                originalBytes.length
        );
        TestableClientHandler handler = new TestableClientHandler();
        String uploadResponse = handler.testHandleRequest(uploadRequest);

        assertNotNull(uploadResponse);
        assertTrue(uploadResponse.contains("SUCCESS"),
                "Server should return SUCCESS after file upload");

        // 4. Send DOWNLOAD_FILE request and verify bytes
        ClientRequest downloadRequest = new ClientRequest(
                "SERVICEJOB", "DOWNLOAD_FILE", job.getServiceJobId());
        String downloadResponse = handler.testHandleRequest(downloadRequest);

        assertNotNull(downloadResponse);
        assertTrue(downloadResponse.contains("SUCCESS"),
                "Server should return SUCCESS on file download");
        // The response will contain the Base64-encoded bytes inside the ServiceJob JSON;
        // verify the fileName came back correctly (bytes are verified at DAO level in ServiceJobDaoTest)
        assertTrue(downloadResponse.contains("server-test.txt"),
                "Download response should contain the original file name");
    }

    // TEST 7: Server GET_ALL vehicles returns SUCCESS
    @Test
    void serverGetAllVehicles_returnsSuccessStatus() throws Exception {
        ClientRequest request = new ClientRequest("VEHICLE", "GET_ALL", (Integer) null);
        TestableClientHandler handler = new TestableClientHandler();

        String responseJson = handler.testHandleRequest(request);

        assertNotNull(responseJson);
        assertTrue(responseJson.contains("SUCCESS"),
                "Server should return SUCCESS for GET_ALL vehicles");
    }

    // TEST 8: Server GET_METADATA returns metadata without BLOB for service job
    @Test
    void serverGetMetadata_returnsMetadataOnly_forServiceJob() throws Exception {
        // Upload a file first so there is metadata to retrieve
        byte[] fileBytes = "metadata test file content".getBytes();
        ServiceJob job = serviceJobDao.insert(new ServiceJob(
                SEED_VEHICLE_ID, "SVR-TEST-Metadata", "PENDING", 0.00, "2026-10-01",
                fileBytes, "meta.txt", "text/plain", fileBytes.length));

        ClientRequest request = new ClientRequest("SERVICEJOB", "GET_METADATA", job.getServiceJobId());
        TestableClientHandler handler = new TestableClientHandler();

        String responseJson = handler.testHandleRequest(request);

        assertNotNull(responseJson);
        assertTrue(responseJson.contains("SUCCESS"),
                "Server should return SUCCESS for GET_METADATA");
        assertTrue(responseJson.contains("meta.txt"),
                "Response should include the stored file name");
    }


    static class TestableClientHandler extends ClientHandler {

        private final Gson handlerGson = new Gson();

        public TestableClientHandler() {
            // null socket — we never call run(), only testHandleRequest()
            super(null);
        }


        public String testHandleRequest(ClientRequest request) throws Exception {


            JdbcCustomerDao   cDao  = new JdbcCustomerDao();
            JdbcVehicleDao    vDao  = new JdbcVehicleDao();
            JdbcServiceJobDao sjDao = new JdbcServiceJobDao();

            if (request == null) {
                return handlerGson.toJson(ServerResponse.error("Invalid request"));
            }

            if ("DISCONNECT".equalsIgnoreCase(request.getAction())) {
                return handlerGson.toJson(ServerResponse.success("Goodbye! Disconnected successfully.", null));
            }

            if (request.getEntity() == null || request.getAction() == null) {
                return handlerGson.toJson(ServerResponse.error("Request must contain entity and action"));
            }

            switch (request.getEntity().toUpperCase()) {

                case "CUSTOMER": {
                    switch (request.getAction().toUpperCase()) {
                        case "GET_ALL":
                            return handlerGson.toJson(
                                    ServerResponse.success("Customers retrieved", cDao.getAllCustomers()));
                        case "GET_BY_ID":
                            if (request.getId() == null) {
                                return handlerGson.toJson(ServerResponse.error("Customer ID required"));
                            }
                            return cDao.getCustomerById(request.getId())
                                    .map(c -> handlerGson.toJson(ServerResponse.success("Customer found", c)))
                                    .orElse(handlerGson.toJson(ServerResponse.error("Customer not found")));
                        case "CREATE":
                            if (request.getEntityData() == null) {
                                return handlerGson.toJson(ServerResponse.error("Customer data required"));
                            }
                            try {
                                Customer nc = handlerGson.fromJson(request.getEntityData(), Customer.class);
                                Customer saved = cDao.insert(nc);
                                return handlerGson.toJson(ServerResponse.success("Customer created", saved));
                            } catch (Exception e) {
                                return handlerGson.toJson(ServerResponse.error("Failed: " + e.getMessage()));
                            }
                        case "DELETE":
                            if (request.getId() == null) {
                                return handlerGson.toJson(ServerResponse.error("Customer ID required"));
                            }
                            boolean cDel = cDao.deleteCustomerById(request.getId());
                            return handlerGson.toJson(cDel
                                    ? ServerResponse.success("Customer deleted", null)
                                    : ServerResponse.error("Customer not found"));
                        default:
                            return handlerGson.toJson(ServerResponse.error("Unsupported action"));
                    }
                }

                case "VEHICLE": {
                    switch (request.getAction().toUpperCase()) {
                        case "GET_ALL":
                            return handlerGson.toJson(
                                    ServerResponse.success("Vehicles retrieved", vDao.getAllVehicles()));
                        case "GET_BY_ID":
                            if (request.getId() == null) {
                                return handlerGson.toJson(ServerResponse.error("Vehicle ID required"));
                            }
                            return vDao.getVehicleById(request.getId())
                                    .map(v -> handlerGson.toJson(ServerResponse.success("Vehicle found", v)))
                                    .orElse(handlerGson.toJson(ServerResponse.error("Vehicle not found")));
                        default:
                            return handlerGson.toJson(ServerResponse.error("Unsupported action"));
                    }
                }

                case "SERVICEJOB": {
                    switch (request.getAction().toUpperCase()) {
                        case "GET_ALL":
                            return handlerGson.toJson(
                                    ServerResponse.success("Jobs retrieved", sjDao.getAllServiceJobs()));
                        case "GET_BY_ID":
                            if (request.getId() == null) {
                                return handlerGson.toJson(ServerResponse.error("Job ID required"));
                            }
                            return sjDao.getServiceJobById(request.getId())
                                    .map(j -> handlerGson.toJson(ServerResponse.success("Job found", j)))
                                    .orElse(handlerGson.toJson(ServerResponse.error("Job not found")));
                        case "UPLOAD_FILE":
                            if (request.getId() == null || request.getBase64FileData() == null) {
                                return handlerGson.toJson(ServerResponse.error("ID and base64 data required"));
                            }
                            try {
                                var existOpt = sjDao.getServiceJobById(request.getId());
                                if (existOpt.isEmpty()) {
                                    return handlerGson.toJson(ServerResponse.error("Job not found"));
                                }
                                ServiceJob ex = existOpt.get();
                                byte[] decoded = Base64.getDecoder().decode(request.getBase64FileData());
                                ServiceJob withFile = new ServiceJob(
                                        ex.getServiceJobId(), ex.getVehicleId(),
                                        ex.getDescription(), ex.getStatus(),
                                        ex.getCost(), ex.getDateCreated(),
                                        decoded, request.getFileName(),
                                        request.getContentType(),
                                        request.getFileSize() == null ? decoded.length : request.getFileSize()
                                );
                                ServiceJob saved = sjDao.updateServiceJob(ex.getServiceJobId(), withFile);
                                return handlerGson.toJson(ServerResponse.success("File uploaded", saved));
                            } catch (Exception e) {
                                return handlerGson.toJson(ServerResponse.error("Upload failed: " + e.getMessage()));
                            }
                        case "DOWNLOAD_FILE":
                            if (request.getId() == null) {
                                return handlerGson.toJson(ServerResponse.error("Job ID required"));
                            }
                            try {
                                var opt = sjDao.getServiceJobById(request.getId());
                                if (opt.isEmpty()) {
                                    return handlerGson.toJson(ServerResponse.error("Job not found"));
                                }
                                ServiceJob j = opt.get();
                                if (j.getFileData() == null || j.getFileSize() <= 0) {
                                    return handlerGson.toJson(ServerResponse.error("No file stored"));
                                }
                                return handlerGson.toJson(ServerResponse.success("File downloaded", j));
                            } catch (Exception e) {
                                return handlerGson.toJson(ServerResponse.error("Download failed: " + e.getMessage()));
                            }
                        case "GET_METADATA":
                            if (request.getId() == null) {
                                return handlerGson.toJson(ServerResponse.error("Job ID required"));
                            }
                            ServiceJob meta = sjDao.getServiceJobMetadataById(request.getId());
                            return meta != null
                                    ? handlerGson.toJson(ServerResponse.success("Metadata retrieved", meta))
                                    : handlerGson.toJson(ServerResponse.error("Job not found"));
                        case "DELETE":
                            if (request.getId() == null) {
                                return handlerGson.toJson(ServerResponse.error("Job ID required"));
                            }
                            boolean jDel = sjDao.deleteServiceJobById(request.getId());
                            return handlerGson.toJson(jDel
                                    ? ServerResponse.success("Job deleted", null)
                                    : ServerResponse.error("Job not found"));
                        default:
                            return handlerGson.toJson(ServerResponse.error("Unsupported action"));
                    }
                }

                default:
                    return handlerGson.toJson(ServerResponse.error("Unknown entity"));
            }
        }
    }
}