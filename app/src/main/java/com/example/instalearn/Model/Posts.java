package com.example.instalearn.Model;

public class Posts {

    String post;
    String caption;
    String user;

    public Posts(){}


    public Posts(String post, String caption, String user) {
        this.post = post;
        this.caption = caption;
        this.user = user;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
