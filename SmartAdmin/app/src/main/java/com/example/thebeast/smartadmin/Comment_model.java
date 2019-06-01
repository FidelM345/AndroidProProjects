package com.example.thebeast.smartadmin;

import java.util.Date;

/**
 * Created by Fidel M Omolo on 5/8/2018.
 */

public class Comment_model extends CommentPostId {
    String message,user_id;
    Long time_stamp;


    public Comment_model() {
    }


    public Comment_model(String message, String user_id, Long time_stamp) {
        this.message = message;
        this.user_id = user_id;
        this.time_stamp = time_stamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Long getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(Long time_stamp) {
        this.time_stamp = time_stamp;
    }
}
