package daoexample.dao;

import daoexample.domain.ServiceJob;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface ServiceJobDao {

    // Basic CRUD operations
    ServiceJob insert(ServiceJob serviceJob) throws Exception;
    Optional<ServiceJob> getServiceJobById(int id) throws Exception;
    List<ServiceJob> getAllServiceJobs() throws Exception;
    ServiceJob updateServiceJob(int id, ServiceJob serviceJob) throws Exception;
    boolean deleteServiceJobById(int id) throws Exception;

    // F8: Filter method
    List<ServiceJob> findServiceJobsByFilter(Predicate<ServiceJob> filter) throws Exception;

    // F9: JSON methods
    String serviceJobToJson(ServiceJob serviceJob) throws Exception;
    ServiceJob serviceJobFromJson(String json) throws Exception;
    String serviceJobListToJson(List<ServiceJob> serviceJobs) throws Exception;

    // ===== F20: FILE METADATA QUERY =====
    // Gets file information (name, type, size) WITHOUT downloading the actual binary file
    // This saves bandwidth and is faster than full download
    ServiceJob getServiceJobMetadataById(int id) throws Exception;
}