package com.serbest.magazine.backend.entity;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "sub_categories")
public class SubCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category")
    private Category category;

    @OneToMany(mappedBy = "subCategory", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Post> posts;

    private Boolean active;

    public SubCategory() {
    }

    public SubCategory(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    public SubCategory(UUID id, String name, Category category, Boolean active) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.active = active;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubCategory that = (SubCategory) o;
        return id.equals(that.id) && name.equals(that.name) && category.equals(that.category) && active.equals(that.active);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, category, active);
    }
}
