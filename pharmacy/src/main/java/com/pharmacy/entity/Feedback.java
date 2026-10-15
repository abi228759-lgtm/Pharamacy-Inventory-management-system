package com.pharmacy.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public class Feedback {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    private int rating;
    @Column(length = 1000)
    private String message;
    private LocalDateTime createdAt = LocalDateTime.now();

    public String getCreatedAtDisplay() { return createdAt.format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")); }
    public Long getId() { return id; }
    public String getUserName() { return userName; }
    public void setUserName(String v) { this.userName = v; }
    public int getRating() { return rating; }
    public void setRating(int v) { this.rating = v; }
    public String getMessage() { return message; }
    public void setMessage(String v) { this.message = v; }
}
