package com.example.thebeast.smartadmin;

/**
 * Created by Fidel M Omolo on 5/7/2018.
 */

public class BlogPostId {

    public String BlogPostIdString;

//used to pass the blog post id to the model class without adding too much code

    public<T extends BlogPostId> T withId(final String id){

        this.BlogPostIdString=id;

        return (T)this;
    }
}
