package daoexample.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import daoexample.dao.JdbcVehicleDao;
import daoexample.dao.VehicleDao;
import daoexample.domain.Vehicle;
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

@WebServlet("/api/vehicles/*")
public class VehicleServlet extends HttpServlet {

    private VehicleDao vehicleDao;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        vehicleDao = new JdbcVehicleDao();
        objectMapper = JacksonConfig.getObjectMapper();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = req.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            CreateRequest<Vehicle> createReq = objectMapper.readValue(sb.toString(),
                    objectMapper.getTypeFactory().constructParametricType(CreateRequest.class, Vehicle.class));

            Vehicle saved = vehicleDao.insert(createReq.getEntity());

            ApiResponse<Vehicle> response = ApiResponse.success("Vehicle created successfully", saved);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            out.print(objectMapper.writeValueAsString(response));

        } catch (Exception e) {
            ApiResponse<Vehicle> errorResponse = ApiResponse.error(
                    "Failed to create vehicle",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    e.getMessage()
            );
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(objectMapper.writeValueAsString(errorResponse));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            String pathInfo = req.getPathInfo();
            int id = Integer.parseInt(pathInfo.substring(1));

            StringBuilder sb = new StringBuilder();
            BufferedReader reader = req.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            Vehicle updatedVehicle = objectMapper.readValue(sb.toString(), Vehicle.class);
            Vehicle result = vehicleDao.updateVehicle(id, updatedVehicle);

            ApiResponse<Vehicle> response = ApiResponse.success("Vehicle updated successfully", result);
            resp.setStatus(HttpServletResponse.SC_OK);
            out.print(objectMapper.writeValueAsString(response));

        } catch (Exception e) {
            ApiResponse<Vehicle> errorResponse = ApiResponse.error(
                    "Failed to update vehicle",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    e.getMessage()
            );
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(objectMapper.writeValueAsString(errorResponse));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            String pathInfo = req.getPathInfo();
            int id = Integer.parseInt(pathInfo.substring(1));

            boolean deleted = vehicleDao.deleteVehicleById(id);

            if (deleted) {
                ApiResponse<Void> response = ApiResponse.success("Vehicle deleted successfully");
                resp.setStatus(HttpServletResponse.SC_OK);
                out.print(objectMapper.writeValueAsString(response));
            } else {
                ApiResponse<Void> errorResponse = ApiResponse.error(
                        "Vehicle not found",
                        HttpServletResponse.SC_NOT_FOUND,
                        "No vehicle found with ID: " + id
                );
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(objectMapper.writeValueAsString(errorResponse));
            }

        } catch (Exception e) {
            ApiResponse<Void> errorResponse = ApiResponse.error(
                    "Failed to delete vehicle",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    e.getMessage()
            );
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(objectMapper.writeValueAsString(errorResponse));
        }
    }
}