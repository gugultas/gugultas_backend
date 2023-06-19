package com.serbest.magazine.backend.entity;

import com.serbest.magazine.backend.common.entity.Masterpiece;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Picture extends Masterpiece {

    public Picture() {
    }

    private Picture(Builder builder) {
        setId(builder.id);
        setTitle(builder.title);
        setOwner(builder.owner);
        setInfo(builder.info);
        setImage(builder.image);
        setShowLink(builder.showLink);
        setShowLink2(builder.showLink2);
        setMarketLink(builder.marketLink);
        setCreateDateTime(builder.createDateTime);
        setUpdateDateTime(builder.updateDateTime);
    }

    public static final class Builder {
        private UUID id;
        private String title;
        private String owner;
        private String info;
        private String image;
        private String showLink;
        private String showLink2;
        private String marketLink;
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

        public Builder owner(String val) {
            owner = val;
            return this;
        }

        public Builder info(String val) {
            info = val;
            return this;
        }

        public Builder image(String val) {
            image = val;
            return this;
        }

        public Builder showLink(String val) {
            showLink = val;
            return this;
        }

        public Builder showLink2(String val) {
            showLink2 = val;
            return this;
        }

        public Builder marketLink(String val) {
            marketLink = val;
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

        public Picture build() {
            return new Picture(this);
        }
    }
}
