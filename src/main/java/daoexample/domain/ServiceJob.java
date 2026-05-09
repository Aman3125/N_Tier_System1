package daoexample.domain;

// F1: DTO entity class representing a ServiceJob table record.
// F17: Extended to include binary file data and metadata.
public class ServiceJob {

    private int serviceJobId;
    private int vehicleId;
    private String description;
    private String status;
    private double cost;
    private String dateCreated;

    // F17: Binary file fields
    private byte[] fileData;
    private String fileName;
    private String contentType;
    private int fileSize;

    public ServiceJob() {
    }

    // Full constructor


    public ServiceJob(int serviceJobId, int vehicleId, String description,
                      String status, double cost, String dateCreated,
                      byte[] fileData, String fileName,
                      String contentType, int fileSize) {

        this.serviceJobId = serviceJobId;
        this.vehicleId = vehicleId;
        this.description = description;
        this.status = status;
        this.cost = cost;
        this.dateCreated = dateCreated;

        // F17 fields
        this.fileData = fileData;
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
    }

    // Constructor without ID (used for insert)
    public ServiceJob(int vehicleId, String description,
                      String status, double cost, String dateCreated,
                      byte[] fileData, String fileName, String contentType, int fileSize)
    {

        setVehicleId(vehicleId);
        setDescription(description);
        setStatus(status);
        setCost(cost);
        setDateCreated(dateCreated);
        setFileData(fileData);
        setFileName(fileName);
        setContentType(contentType);
        setFileSize(fileSize);
    }

    // Constructor without ID and without file data
    public ServiceJob(int vehicleId, String description,
                      String status, double cost, String dateCreated) {

        this(vehicleId, description, status, cost, dateCreated,
                null, null, null, 0);
    }

    // Existing constructor (no file data)
    public ServiceJob(int serviceJobId, int vehicleId, String description,
                      String status, double cost, String dateCreated) {

        this(serviceJobId, vehicleId, description, status, cost, dateCreated,
                null, null, null, 0);
    }

    // ------------------- Getters & Setters -------------------

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getServiceJobId() {
        return serviceJobId;
    }

    public void setServiceJobId(int serviceJobId) {
        this.serviceJobId = serviceJobId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }


    // ------------------- F17 File Fields -------------------

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData; // can be null
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        if (fileName != null && fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be empty.");
        }
        this.fileName = (fileName == null) ? null : fileName.trim();
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        if (contentType != null && contentType.trim().isEmpty()) {
            throw new IllegalArgumentException("Content type cannot be empty.");
        }
        this.contentType = (contentType == null) ? null : contentType.trim();
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        if (fileSize < 0) {
            throw new IllegalArgumentException("File size cannot be negative.");
        }
        this.fileSize = fileSize;
    }

    // ------------------- toString -------------------

    @Override
    public String toString() {
        return "ServiceJob{" +
                "serviceJobId=" + serviceJobId +
                ", vehicleId=" + vehicleId +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", cost=" + cost +
                ", dateCreated='" + dateCreated + '\'' +
                ", fileName='" + fileName + '\'' +
                ", contentType='" + contentType + '\'' +
                ", fileSize=" + fileSize +
                '}';
    }
}