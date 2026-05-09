package daoexample.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import daoexample.dao.JdbcServiceJobDao;
import daoexample.dao.ServiceJobDao;
import daoexample.domain.ServiceJob;
import daoexample.dto.ApiResponse;
import daoexample.dto.CreateRequest;
import daoexample.util.JacksonConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/service-jobs/*")
public class ServiceJobServlet extends HttpServlet {

    private ServiceJobDao serviceJobDao;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        serviceJobDao = new JdbcServiceJobDao();
        objectMapper = JacksonConfig.getObjectMapper();
    }

    // F13: CREATE Service Job
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            // Read JSON body
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = req.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            // Parse the request
            CreateRequest<ServiceJob> createReq = objectMapper.readValue(sb.toString(),
                    objectMapper.getTypeFactory().constructParametricType(CreateRequest.class, ServiceJob.class));

            ServiceJob jobToCreate = createReq.getEntity();

            // Validate
            if (jobToCreate == null) {
                ApiResponse<ServiceJob> errorResponse = ApiResponse.error(
                        "Invalid request",
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Service job entity is required"
                );
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(objectMapper.writeValueAsString(errorResponse));
                return;
            }

            // Insert using DAO
            ServiceJob saved = serviceJobDao.insert(jobToCreate);

            // Return success response
            ApiResponse<ServiceJob> response = ApiResponse.success(
                    "Service job created successfully",
                    saved
            );

            resp.setStatus(HttpServletResponse.SC_CREATED);
            out.print(objectMapper.writeValueAsString(response));

        } catch (IllegalArgumentException e) {
            ApiResponse<ServiceJob> errorResponse = ApiResponse.error(
                    "Validation failed",
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(objectMapper.writeValueAsString(errorResponse));

        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<ServiceJob> errorResponse = ApiResponse.error(
                    "Failed to create service job",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    e.getMessage()
            );
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(objectMapper.writeValueAsString(errorResponse));
        }
    }

    // F15: UPDATE Service Job
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            // Extract ID from URL
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/") || pathInfo.isEmpty()) {
                ApiResponse<ServiceJob> errorResponse = ApiResponse.error(
                        "Invalid service job ID",
                        HttpServletResponse.SC_BAD_REQUEST,
                        "ID is required in the URL"
                );
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(objectMapper.writeValueAsString(errorResponse));
                return;
            }

            int id = Integer.parseInt(pathInfo.substring(1));

            if (id <= 0) {
                ApiResponse<ServiceJob> errorResponse = ApiResponse.error(
                        "Invalid service job ID",
                        HttpServletResponse.SC_BAD_REQUEST,
                        "ID must be a positive number"
                );
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(objectMapper.writeValueAsString(errorResponse));
                return;
            }

            // Read JSON body
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = req.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            // Parse the updated service job
            ServiceJob updatedJob = objectMapper.readValue(sb.toString(), ServiceJob.class);

            // Validate
            if (updatedJob == null) {
                ApiResponse<ServiceJob> errorResponse = ApiResponse.error(
                        "Invalid request",
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Service job data is required"
                );
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(objectMapper.writeValueAsString(errorResponse));
                return;
            }

            // Update using DAO
            ServiceJob result = serviceJobDao.updateServiceJob(id, updatedJob);

            // Return success response
            ApiResponse<ServiceJob> response = ApiResponse.success(
                    "Service job updated successfully",
                    result
            );

            resp.setStatus(HttpServletResponse.SC_OK);
            out.print(objectMapper.writeValueAsString(response));

        } catch (IllegalArgumentException | IllegalStateException e) {
            ApiResponse<ServiceJob> errorResponse = ApiResponse.error(
                    "Update failed",
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(objectMapper.writeValueAsString(errorResponse));

        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<ServiceJob> errorResponse = ApiResponse.error(
                    "Failed to update service job",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    e.getMessage()
            );
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(objectMapper.writeValueAsString(errorResponse));
        }
    }

    // F14: DELETE Service Job
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            // Extract ID from URL
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/") || pathInfo.isEmpty()) {
                ApiResponse<Void> errorResponse = ApiResponse.error(
                        "Invalid service job ID",
                        HttpServletResponse.SC_BAD_REQUEST,
                        "ID is required in the URL"
                );
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(objectMapper.writeValueAsString(errorResponse));
                return;
            }

            int id = Integer.parseInt(pathInfo.substring(1));

            if (id <= 0) {
                ApiResponse<Void> errorResponse = ApiResponse.error(
                        "Invalid service job ID",
                        HttpServletResponse.SC_BAD_REQUEST,
                        "ID must be a positive number"
                );
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(objectMapper.writeValueAsString(errorResponse));
                return;
            }

            // Delete using DAO
            boolean deleted = serviceJobDao.deleteServiceJobById(id);

            if (deleted) {
                ApiResponse<Void> response = ApiResponse.success(
                        "Service job deleted successfully"
                );
                resp.setStatus(HttpServletResponse.SC_OK);
                out.print(objectMapper.writeValueAsString(response));
            } else {
                ApiResponse<Void> errorResponse = ApiResponse.error(
                        "Service job not found",
                        HttpServletResponse.SC_NOT_FOUND,
                        "No service job found with ID: " + id
                );
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(objectMapper.writeValueAsString(errorResponse));
            }

        } catch (NumberFormatException e) {
            ApiResponse<Void> errorResponse = ApiResponse.error(
                    "Invalid service job ID",
                    HttpServletResponse.SC_BAD_REQUEST,
                    "ID must be a valid number"
            );
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(objectMapper.writeValueAsString(errorResponse));

        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<Void> errorResponse = ApiResponse.error(
                    "Failed to delete service job",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    e.getMessage()
            );
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(objectMapper.writeValueAsString(errorResponse));
        }
    }
}