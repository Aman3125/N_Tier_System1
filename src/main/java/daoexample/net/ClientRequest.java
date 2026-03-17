package daoexample.net;

// F12: Client sends a JSON request to the server.
public class ClientRequest {

    private String entity;
    private String action;
    private Integer id;

    public ClientRequest() {
    }

    public ClientRequest(String entity, String action, Integer id) {
        this.entity = entity;
        this.action = action;
        this.id = id;
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
}
