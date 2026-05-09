package daoexample.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateRequest<T> {

    @JsonProperty("entity")
    private T entity;

    public CreateRequest() {}

    public T getEntity() { return entity; }
    public void setEntity(T entity) { this.entity = entity; }
}