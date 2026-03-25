package daoexample.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import daoexample.dao.CustomerDao;
import daoexample.dao.JdbcCustomerDao;
import daoexample.domain.Customer;
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

@WebServlet("/api/customers/*")
public class CustomerServlet extends HttpServlet {

    private CustomerDao customerDao;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        customerDao = new JdbcCustomerDao();
        objectMapper = JacksonConfig.getObjectMapper();
    }

    // F13: CREATE
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

            CreateRequest<Customer> createReq = objectMapper.readValue(sb.toString(),
                    objectMapper.getTypeFactory().constructParametricType(CreateRequest.class, Customer.class));

            Customer saved = customerDao.insert(createReq.getEntity());

            ApiResponse<Customer> response = ApiResponse.success("Customer created successfully", saved);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            out.print(objectMapper.writeValueAsString(response));

        } catch (Exception e) {
            ApiResponse<Customer> errorResponse = ApiResponse.error(
                    "Failed to create customer",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    e.getMessage()
            );
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(objectMapper.writeValueAsString(errorResponse));
        }
    }

    // F15: UPDATE
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

            Customer updatedCustomer = objectMapper.readValue(sb.toString(), Customer.class);
            Customer result = customerDao.updateCustomer(id, updatedCustomer);

            ApiResponse<Customer> response = ApiResponse.success("Customer updated successfully", result);
            resp.setStatus(HttpServletResponse.SC_OK);
            out.print(objectMapper.writeValueAsString(response));

        } catch (Exception e) {
            ApiResponse<Customer> errorResponse = ApiResponse.error(
                    "Failed to update customer",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    e.getMessage()
            );
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(objectMapper.writeValueAsString(errorResponse));
        }
    }

    // F14: DELETE
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            String pathInfo = req.getPathInfo();
            int id = Integer.parseInt(pathInfo.substring(1));

            boolean deleted = customerDao.deleteCustomerById(id);

            if (deleted) {
                ApiResponse<Void> response = ApiResponse.success("Customer deleted successfully");
                resp.setStatus(HttpServletResponse.SC_OK);
                out.print(objectMapper.writeValueAsString(response));
            } else {
                ApiResponse<Void> errorResponse = ApiResponse.error(
                        "Customer not found",
                        HttpServletResponse.SC_NOT_FOUND,
                        "No customer found with ID: " + id
                );
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(objectMapper.writeValueAsString(errorResponse));
            }

        } catch (Exception e) {
            ApiResponse<Void> errorResponse = ApiResponse.error(
                    "Failed to delete customer",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    e.getMessage()
            );
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(objectMapper.writeValueAsString(errorResponse));
        }
    }
}