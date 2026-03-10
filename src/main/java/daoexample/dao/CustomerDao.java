package daoexample.dao;

import daoexample.domain.Customer;

import java.util.List;
import java.util.Optional;

/// <summary>
/// DAO contract for reading and writing Task objects.
/// </summary>
public interface CustomerDao {

    Customer insert(Customer customer) throws Exception;

    Optional<Customer> getCustomerById(int id) throws Exception;

    List<Customer> getAllCustomers() throws Exception;

    Customer updateCustomer(int id, Customer customer) throws Exception;

    boolean deleteCustomerById(int id) throws Exception;
}