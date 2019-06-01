package com.example.thebeast.smartadmin;

public class EmergencyLines_model extends BlogPostId{

    String email, thumbUri, imageUri,latitude, longitude,mobileno,title,user_id,weburl;

    public EmergencyLines_model() {
    }

    public EmergencyLines_model(String email, String thumbUri, String imageUri,
                                String latitude, String longitude, String mobileno, String title,
                                String user_id, String weburl) {
        this.email = email;
        this.thumbUri = thumbUri;
        this.imageUri = imageUri;
        this.latitude = latitude;
        this.longitude = longitude;
        this.mobileno = mobileno;
        this.title = title;
        this.user_id = user_id;
        this.weburl = weburl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getThumbUri() {
        return thumbUri;
    }

    public void setThumbUri(String thumbUri) {
        this.thumbUri = thumbUri;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getWeburl() {
        return weburl;
    }

    public void setWeburl(String weburl) {
        this.weburl = weburl;
    }
}
