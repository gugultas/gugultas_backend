package com.serbest.magazine.backend.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "verification_token")
public class VerificationToken {
    private static final int EXPIRATION = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String token;

    @OneToOne(targetEntity = Author.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "author")
    private Author author;

    private Date expiryDate;

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

    public VerificationToken() {
    }

    public VerificationToken(String token, Author author) {
        this.token = token;
        this.author = author;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    public VerificationToken(UUID id, String token, Author author, Date expiryDate) {
        this.id = id;
        this.token = token;
        this.author = author;
        this.expiryDate = expiryDate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
}
