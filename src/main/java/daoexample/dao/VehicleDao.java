package daoexample.dao;

import daoexample.domain.Vehicle;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface VehicleDao {

    Vehicle insert(Vehicle vehicle) throws Exception;

    Optional<Vehicle> getVehicleById(int id) throws Exception;

    List<Vehicle> getAllVehicles() throws Exception;

    Vehicle updateVehicle(int id, Vehicle vehicle) throws Exception;

    boolean deleteVehicleById(int id) throws Exception;

    /**
     * F8: Find vehicles matching a filter
     */
    List<Vehicle> findVehiclesByFilter(Predicate<Vehicle> filter) throws Exception;

    /**
     * F9: Convert a single vehicle to JSON
     */
    String vehicleToJson(Vehicle vehicle) throws Exception;

    /**
     * F9: Convert JSON to a Vehicle object
     */
    Vehicle vehicleFromJson(String json) throws Exception;

    /**
     * F9: Convert a list of vehicles to JSON
     */
    String vehicleListToJson(List<Vehicle> vehicles) throws Exception;
}
