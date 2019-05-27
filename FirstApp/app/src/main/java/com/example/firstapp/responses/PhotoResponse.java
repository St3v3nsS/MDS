package com.example.firstapp.responses;

public class PhotoResponse {
    // the url of photo

    private String photo;

    public PhotoResponse(String url) {
        this.photo = url;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
