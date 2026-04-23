package daoexample.net;

// F12: Client sends a JSON request to the server.
// F18: Extended to support binary file upload using Base64 and metadata.
public class ClientRequest {

    private String entity;
    private String action;
    private Integer id;
    private String entityData;  // JSON data for create/update operations

    // F18: File upload fields
    private String base64FileData;
    private String fileName;
    private String contentType;
    private Integer fileSize;

    public ClientRequest() {
    }

    public ClientRequest(String entity, String action, Integer id) {
        this.entity = entity;
        this.action = action;
        this.id = id;
    }

    // Constructor for create/update with data
    public ClientRequest(String entity, String action, String entityData) {
        this.entity = entity;
        this.action = action;
        this.entityData = entityData;
    }

    // Constructor for update with ID + data
    public ClientRequest(String entity, String action, Integer id, String entityData) {
        this.entity = entity;
        this.action = action;
        this.id = id;
        this.entityData = entityData;
    }

    // F18: Constructor for file upload
    public ClientRequest(String entity, String action, Integer id,
                         String entityData,
                         String base64FileData,
                         String fileName,
                         String contentType,
                         Integer fileSize) {

        this.entity = entity;
        this.action = action;
        this.id = id;
        this.entityData = entityData;
        this.base64FileData = base64FileData;
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
    }

    // ------------------- Getters -------------------

    public String getEntity() {
        return entity;
    }

    public String getAction() {
        return action;
    }

    public Integer getId() {
        return id;
    }

    public String getEntityData() {
        return entityData;
    }

    // F18 getters
    public String getBase64FileData() {
        return base64FileData;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public Integer getFileSize() {
        return fileSize;
    }

    // ------------------- Setters -------------------

    public void setBase64FileData(String base64FileData) {
        this.base64FileData = base64FileData;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }
}