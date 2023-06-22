package com.serbest.magazine.backend.entity;

import jakarta.mail.Multipart;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "authors")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    private String firstName;
    private String lastName;

    @Lob
    private String description;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean active;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Post> posts;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Like> likes;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "profile_image",
            joinColumns = {
                    @JoinColumn(name = "author_id", referencedColumnName = "id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "image_id", referencedColumnName = "id")
            }
    )
    private ImageModel profileImage;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "author_roles",
            joinColumns = @JoinColumn(name = "author_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Set<Role> roles;

    @Column(name = "enabled")
    private boolean enabled;

    private String facebook;
    private String twitter;
    private String instagram;
    private String youtube;
    private String blog;

    @CreationTimestamp
    private LocalDateTime createDateTime;

    @UpdateTimestamp
    private LocalDateTime updateDateTime;

    public Author() {
        super();
    }

    public Author(UUID id, String username, String email, String firstName, String lastName, String description,
                  String password, Boolean active, List<Post> posts, List<Comment> comments, List<Like> likes,
                  ImageModel profileImage, Set<Role> roles, String facebook, String twitter, String instagram,
                  String youtube, String blog, LocalDateTime createDateTime, LocalDateTime updateDateTime) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.description = description;
        this.password = password;
        this.active = active;
        this.posts = posts;
        this.comments = comments;
        this.likes = likes;
        this.profileImage = profileImage;
        this.enabled = false;
        this.roles = roles;
        this.facebook = facebook;
        this.twitter = twitter;
        this.instagram = instagram;
        this.youtube = youtube;
        this.blog = blog;
        this.createDateTime = createDateTime;
        this.updateDateTime = updateDateTime;
    }

    private Author(Builder builder) {
        setId(builder.id);
        setUsername(builder.username);
        setEmail(builder.email);
        setFirstName(builder.firstName);
        setLastName(builder.lastName);
        setDescription(builder.description);
        setPassword(builder.password);
        setActive(builder.active);
        setPosts(builder.posts);
        setComments(builder.comments);
        setLikes(builder.likes);
        setProfileImage(builder.profileImage);
        setRoles(builder.roles);
        setCreateDateTime(builder.createDateTime);
        setUpdateDateTime(builder.updateDateTime);
    }



    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    public ImageModel getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(ImageModel profileImage) {
        this.profileImage = profileImage;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public String getBlog() {
        return blog;
    }

    public void setBlog(String blog) {
        this.blog = blog;
    }

    public static final class Builder {
        private UUID id;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private String description;
        private String password;
        private Boolean active;
        private List<Post> posts;
        private List<Comment> comments;
        private List<Like> likes;
        private ImageModel profileImage;
        private Set<Role> roles;
        private LocalDateTime createDateTime;
        private LocalDateTime updateDateTime;

        private String facebook;
        private String twitter;
        private String instagram;
        private String youtube;
        private String blog;

        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder id(UUID val) {
            id = val;
            return this;
        }

        public Builder username(String val) {
            username = val;
            return this;
        }

        public Builder email(String val) {
            email = val;
            return this;
        }

        public Builder firstName(String val) {
            firstName = val;
            return this;
        }

        public Builder lastName(String val) {
            lastName = val;
            return this;
        }

        public Builder description(String val) {
            description = val;
            return this;
        }

        public Builder password(String val) {
            password = val;
            return this;
        }

        public Builder active(Boolean val) {
            active = val;
            return this;
        }

        public Builder posts(List<Post> val) {
            posts = val;
            return this;
        }

        public Builder comments(List<Comment> val) {
            comments = val;
            return this;
        }

        public Builder likes(List<Like> val) {
            likes = val;
            return this;
        }

        public Builder profileImage(ImageModel val) {
            profileImage = val;
            return this;
        }

        public Builder roles(Set<Role> val) {
            roles = val;
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

        public Builder facebook(String val) {
            facebook = val;
            return this;
        }
        public Builder twitter(String val) {
            twitter = val;
            return this;
        }
        public Builder instagram(String val) {
            instagram = val;
            return this;
        }
        public Builder youtube(String val) {
            youtube = val;
            return this;
        }
        public Builder blog(String val) {
            blog = val;
            return this;
        }

        public Author build() {
            return new Author(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Author author = (Author) o;
        return id.equals(author.id) && username.equals(author.username) && email.equals(author.email) && Objects.equals(firstName, author.firstName) && Objects.equals(lastName, author.lastName) && Objects.equals(description, author.description) && Objects.equals(password, author.password) && Objects.equals(active, author.active) && Objects.equals(posts, author.posts) && Objects.equals(comments, author.comments) && Objects.equals(likes, author.likes) && Objects.equals(profileImage, author.profileImage) && Objects.equals(roles, author.roles) && Objects.equals(createDateTime, author.createDateTime) && Objects.equals(updateDateTime, author.updateDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, firstName, lastName, description, password, active, posts, comments, likes, profileImage, roles, createDateTime, updateDateTime);
    }
}
