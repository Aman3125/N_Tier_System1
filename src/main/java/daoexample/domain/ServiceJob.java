package daoexample.domain;

// F1: DTO entity class representing a ServiceJob table record.
// Fields are private and validated through setters to enforce valid data.
public class ServiceJob {

    private int serviceJobId;
    private int vehicleId;
    private String description;
    private String status;
    private double cost;
    private String dateCreated;


    public ServiceJob(int serviceJobId, int vehicleId, String description, String status, double cost, String dateCreated) {
        setServiceJobId(serviceJobId);
        setVehicleId(vehicleId);
        setDescription(description);
        setStatus(status);
        setCost(cost);
        setDateCreated(dateCreated);
    }

    public ServiceJob(int vehicleId, String description, String status, double cost, String dateCreated) {
        setVehicleId(vehicleId);
        setDescription(description);
        setStatus(status);
        setCost(cost);
        setDateCreated(dateCreated);
    }

    public int getServiceJobId() {
        return serviceJobId;
    }

    public void setServiceJobId(int serviceJobId) {
        if (serviceJobId < 0) {
            throw new IllegalArgumentException("Service job ID cannot be negative.");
        }
        this.serviceJobId = serviceJobId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        if (vehicleId <= 0) {
            throw new IllegalArgumentException("Vehicle ID must be greater than 0.");
        }
        this.vehicleId = vehicleId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty.");
        }
        this.description = description.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be empty.");
        }

        String cleanStatus = status.trim().toUpperCase();

        if (!cleanStatus.equals("PENDING") &&
                !cleanStatus.equals("IN_PROGRESS") &&
                !cleanStatus.equals("COMPLETED")) {
            throw new IllegalArgumentException("Status must be PENDING, IN_PROGRESS, or COMPLETED.");
        }

        this.status = cleanStatus;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        if (cost < 0) {
            throw new IllegalArgumentException("Cost cannot be negative.");
        }
        this.cost = cost;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        if (dateCreated == null || dateCreated.trim().isEmpty()) {
            throw new IllegalArgumentException("Date created cannot be empty.");
        }
        this.dateCreated = dateCreated.trim();
    }

    @Override
    public String toString() {
        return "ServiceJob{" +
                "serviceJobId=" + serviceJobId +
                ", vehicleId=" + vehicleId +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", cost=" + cost +
                ", dateCreated='" + dateCreated + '\'' +
                '}';
    }
}