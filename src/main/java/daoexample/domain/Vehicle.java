package daoexample.domain;

public class Vehicle {

    private int vehicleId;
    private int customerId;
    private String make;
    private String model;
    private String registrationNumber;
    private int year;

    public Vehicle() {
    }

    public Vehicle(int vehicleId, int customerId, String make, String model, String registrationNumber, int year) {
        setVehicleId(vehicleId);
        setCustomerId(customerId);
        setMake(make);
        setModel(model);
        setRegistrationNumber(registrationNumber);
        setYear(year);
    }

    public Vehicle(int customerId, String make, String model, String registrationNumber, int year) {
        setCustomerId(customerId);
        setMake(make);
        setModel(model);
        setRegistrationNumber(registrationNumber);
        setYear(year);
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        if (vehicleId < 0) {
            throw new IllegalArgumentException("Vehicle ID cannot be negative.");
        }
        this.vehicleId = vehicleId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        if (customerId <= 0) {
            throw new IllegalArgumentException("Customer ID must be greater than 0.");
        }
        this.customerId = customerId;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        if (make == null || make.trim().isEmpty()) {
            throw new IllegalArgumentException("Make cannot be empty.");
        }
        this.make = make.trim();
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        if (model == null || model.trim().isEmpty()) {
            throw new IllegalArgumentException("Model cannot be empty.");
        }
        this.model = model.trim();
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        if (registrationNumber == null || registrationNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration number cannot be empty.");
        }
        this.registrationNumber = registrationNumber.trim().toUpperCase();
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        if (year < 1886 || year > 2100) {
            throw new IllegalArgumentException("Year must be realistic.");
        }
        this.year = year;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleId=" + vehicleId +
                ", customerId=" + customerId +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", year=" + year +
                '}';
    }
}
