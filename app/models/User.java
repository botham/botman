package models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {

  private String id, name, client, clientId;

  public User(String id, String name, String client, String clientId) {
    this.id = id;
    this.name = name;
    this.client = client;
    this.clientId = clientId;
  }

  public User(List<String> list) throws RuntimeException {
    this.id = list.get(0);
    this.name = list.get(1);
    this.client = list.get(2);
    this.clientId = list.get(3);
  }

  public Map<String, String> asMap() {
    Map<String, String> map = new HashMap<>();
    map.put("id", id);
    map.put("name", name);
    map.put("client", client);
    map.put("clientId", clientId);
    return map;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getClient() {
    return client;
  }

  public void setClient(String client) {
    this.client = client;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }
}
