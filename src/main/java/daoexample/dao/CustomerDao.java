package daoexample.dao;

import daoexample.domain.Customer;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/// <summary>
/// DAO contract for reading and writing Task objects.
/// </summary>
public interface CustomerDao {

    Customer insert(Customer customer) throws Exception;

    Optional<Customer> getCustomerById(int id) throws Exception;

    List<Customer> getAllCustomers() throws Exception;

    Customer updateCustomer(int id, Customer customer) throws Exception;

    boolean deleteCustomerById(int id) throws Exception;

    /**
     * F8: Find customers matching a filter
     */
    List<Customer> findCustomersByFilter(Predicate<Customer> filter) throws Exception;

    /**
     * F9: Convert a single customer to JSON
     */
    String customerToJson(Customer customer) throws Exception;

    /**
     * F9: Convert JSON to a Customer object
     */
    Customer customerFromJson(String json) throws Exception;

    /**
     * F9: Convert a list of customers to JSON
     */
    String customerListToJson(List<Customer> customers) throws Exception;
}