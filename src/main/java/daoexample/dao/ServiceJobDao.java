package daoexample.dao;

import daoexample.domain.ServiceJob;

import java.util.List;
import java.util.Optional;

public interface ServiceJobDao {

    ServiceJob insert(ServiceJob serviceJob) throws Exception;

    Optional<ServiceJob> getServiceJobById(int id) throws Exception;

    List<ServiceJob> getAllServiceJobs() throws Exception;

    ServiceJob updateServiceJob(int id, ServiceJob serviceJob) throws Exception;

    boolean deleteServiceJobById(int id) throws Exception;
}