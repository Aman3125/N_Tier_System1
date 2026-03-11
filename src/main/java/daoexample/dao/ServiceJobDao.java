package daoexample.dao;

import daoexample.domain.ServiceJob;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface ServiceJobDao {

    ServiceJob insert(ServiceJob serviceJob) throws Exception;

    Optional<ServiceJob> getServiceJobById(int id) throws Exception;

    List<ServiceJob> getAllServiceJobs() throws Exception;

    ServiceJob updateServiceJob(int id, ServiceJob serviceJob) throws Exception;

    boolean deleteServiceJobById(int id) throws Exception;


    /**
     * F8: Find service jobs matching a filter
     */
    List<ServiceJob> findServiceJobsByFilter(Predicate<ServiceJob> filter) throws Exception;

    /**
     * F9: Convert a single service job to JSON
     */
    String serviceJobToJson(ServiceJob serviceJob) throws Exception;

    /**
     * F9: Convert JSON to a ServiceJob object
     */
    ServiceJob serviceJobFromJson(String json) throws Exception;

    /**
     * F9: Convert a list of service jobs to JSON
     */
    String serviceJobListToJson(List<ServiceJob> serviceJobs) throws Exception;
}