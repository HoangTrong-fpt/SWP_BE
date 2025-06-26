package com.quitsmoking.platform.entity;

package com.example.demo.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Feedback {

    @Id
    private Long id;
    private String user;
    private String comment;
    private int rating;

    // Constructors
    public Feedback() {}

    public Feedback(Long id, String user, String comment, int rating) {
        this.id = id;
        this.user = user;
        this.comment = comment;
        this.rating = rating;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
