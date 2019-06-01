package com.example.thebeast.smartadmin;

public class CommentPostId {


    public String CommentPostIdString;
    public String BlogPostIdString;

//used to pass the blog post id to the model class without adding too much code

    public<T extends CommentPostId> T withId(final String comment_id,final String blog_id){

        this.CommentPostIdString=comment_id;
        this.CommentPostIdString=blog_id;

        return (T)this;
    }
}
