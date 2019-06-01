package com.example.thebeast.smartadmin;

public class ViewAlertId {


    public String ViewAlertIdString;
//used to pass the blog post id to the model class without adding too much code

    public<T extends ViewAlertId> T withId(final String id){

        this.ViewAlertIdString=id;
        return (T)this;
    }
}
