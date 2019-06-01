package com.example.thebeast.smartadmin;

public class FirstAidWeb_model extends BlogPostId{
    String thumbUri, imageUri,title,weburl;
    Long timestamp;


    public FirstAidWeb_model() {
    }

    public FirstAidWeb_model(String thumbUri, String imageUri,
                             String title, String weburl,
                             Long timestamp) {
        this.thumbUri = thumbUri;
        this.imageUri = imageUri;
        this.title = title;
        this.weburl = weburl;
        this.timestamp = timestamp;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWeburl() {
        return weburl;
    }

    public void setWeburl(String weburl) {
        this.weburl = weburl;
    }
}
