package daoexample.dao;

import daoexample.domain.Vehicle;

import java.util.List;
import java.util.Optional;

public interface VehicleDao {

    Vehicle insert(Vehicle vehicle) throws Exception;

    Optional<Vehicle> getVehicleById(int id) throws Exception;

    List<Vehicle> getAllVehicles() throws Exception;

    Vehicle updateVehicle(int id, Vehicle vehicle) throws Exception;

    boolean deleteVehicleById(int id) throws Exception;
}
