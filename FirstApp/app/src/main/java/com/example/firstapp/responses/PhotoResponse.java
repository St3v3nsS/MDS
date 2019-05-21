package com.example.firstapp.responses;

public class PhotoResponse {

    private String url;

    public PhotoResponse(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
