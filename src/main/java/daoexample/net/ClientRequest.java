package daoexample.net;

// F12: Client sends a JSON request to the server.
public class ClientRequest {

    private String entity;
    private String action;
    private Integer id;
    private String entityData;  // NEW: JSON data for create/update operations

    public ClientRequest() {
    }

    public ClientRequest(String entity, String action, Integer id) {
        this.entity = entity;
        this.action = action;
        this.id = id;
    }

    // NEW: Constructor for create/update with data
    public ClientRequest(String entity, String action, String entityData) {
        this.entity = entity;
        this.action = action;
        this.entityData = entityData;
    }

    // NEW: Constructor for create/update with data and ID (for update)
    public ClientRequest(String entity, String action, Integer id, String entityData) {
        this.entity = entity;
        this.action = action;
        this.id = id;
        this.entityData = entityData;
    }

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
}