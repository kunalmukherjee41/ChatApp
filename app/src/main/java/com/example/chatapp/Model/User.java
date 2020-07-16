package com.example.chatapp.Model;

public class User {

    private String username;
    private String id;
    private String image;
    private String status;
    private String search;

    public User(String img, String id, String username, String status, String search) {
        this.username = username;
        this.id = id;
        this.image = img;
        this.search = search;
        this.status = status;
    }

    public User() {

    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }
}
