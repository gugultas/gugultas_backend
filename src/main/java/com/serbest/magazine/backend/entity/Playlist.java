package com.serbest.magazine.backend.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "playlists")
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String title;

    @Size(max = 255)
    private String description;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "playlist_posts",
            joinColumns = @JoinColumn(name = "playlist_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "post_id", referencedColumnName = "postId")
    )
    private Set<Post> posts;

    @OneToOne
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private Author author;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "playlist_images",
            joinColumns = {
                    @JoinColumn(name = "playlist_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "image_id")
            }
    )
    private ImageModel playlistImage;

    @CreationTimestamp
    private LocalDateTime createDateTime;

    @UpdateTimestamp
    private LocalDateTime updateDateTime;

    public Playlist() {
    }

    public Playlist(String title, String description, Author author, ImageModel playlistImage) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.playlistImage = playlistImage;
    }

    public Playlist(UUID id, String title, String description, Set<Post> posts, Author author, ImageModel playlistImage,
                    LocalDateTime createDateTime, LocalDateTime updateDateTime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.posts = posts;
        this.author = author;
        this.playlistImage = playlistImage;
        this.createDateTime = createDateTime;
        this.updateDateTime = updateDateTime;
    }

    private Playlist(Builder builder) {
        setId(builder.id);
        setTitle(builder.title);
        setDescription(builder.description);
        setPosts(builder.posts);
        setAuthor(builder.author);
        setPlaylistImage(builder.playlistImage);
        setCreateDateTime(builder.createDateTime);
        setUpdateDateTime(builder.updateDateTime);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Post> getPosts() {
        return posts;
    }

    public void setPosts(Set<Post> posts) {
        this.posts = posts;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public ImageModel getPlaylistImage() {
        if (playlistImage == null){
            return new ImageModel();
        };
        return playlistImage;
    }

    public void setPlaylistImage(ImageModel playlistImage) {
        this.playlistImage = playlistImage;
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

    public static final class Builder {
        private UUID id;
        private String title;
        private @Size(max = 255) String description;
        private Set<Post> posts;
        private Author author;
        private ImageModel playlistImage;
        private LocalDateTime createDateTime;
        private LocalDateTime updateDateTime;

        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder id(UUID val) {
            id = val;
            return this;
        }

        public Builder title(String val) {
            title = val;
            return this;
        }

        public Builder description(@Size(max = 255) String val) {
            description = val;
            return this;
        }

        public Builder posts(Set<Post> val) {
            posts = val;
            return this;
        }

        public Builder author(Author val) {
            author = val;
            return this;
        }

        public Builder playlistImage(ImageModel val) {
            playlistImage = val;
            return this;
        }

        public Builder createDateTime(LocalDateTime val) {
            createDateTime = val;
            return this;
        }

        public Builder updateDateTime(LocalDateTime val) {
            updateDateTime = val;
            return this;
        }

        public Playlist build() {
            return new Playlist(this);
        }
    }
}
