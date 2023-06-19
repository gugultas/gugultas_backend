package com.serbest.magazine.backend.common.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public abstract class Masterpiece {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String title;

    private String owner;

    @Lob
    private String info;

    private String image;

    private String showLink;

    private String showLink2;

    private String marketLink;

    @CreationTimestamp
    private LocalDateTime createDateTime;

    @UpdateTimestamp
    private LocalDateTime updateDateTime;

    public Masterpiece() {
    }

    public Masterpiece(String title, String owner, String info, String image, String showLink, String showLink2, String marketLink) {
        this.title = title;
        this.owner = owner;
        this.info = info;
        this.image = image;
        this.showLink = showLink;
        this.showLink2 = showLink2;
        this.marketLink = marketLink;
    }

    public Masterpiece(UUID id, String title, String owner, String info, String image, String showLink, String showLink2, String marketLink, LocalDateTime createDateTime, LocalDateTime updateDateTime) {
        this.id = id;
        this.title = title;
        this.owner = owner;
        this.info = info;
        this.image = image;
        this.showLink = showLink;
        this.showLink2 = showLink2;
        this.marketLink = marketLink;
        this.createDateTime = createDateTime;
        this.updateDateTime = updateDateTime;
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getShowLink() {
        return showLink;
    }

    public void setShowLink(String showLink) {
        this.showLink = showLink;
    }

    public String getShowLink2() {
        return showLink2;
    }

    public void setShowLink2(String showLink2) {
        this.showLink2 = showLink2;
    }

    public String getMarketLink() {
        return marketLink;
    }

    public void setMarketLink(String marketLink) {
        this.marketLink = marketLink;
    }

    public LocalDateTime getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(LocalDateTime createDateTime) {
        this.createDateTime = createDateTime;
    }

    public LocalDateTime getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(LocalDateTime updateDateTime) {
        this.updateDateTime = updateDateTime;
    }
}
