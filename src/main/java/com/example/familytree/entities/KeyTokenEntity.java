package com.example.familytree.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
@Table(name = "key_token")
public class KeyTokenEntity {
    @Id
    @Column(name = "user_id", nullable = false)
    private int userId;
    @Basic
    @Column(name = "private_key", nullable = false, length = 2147483647)
    private String privateKey;
    @Basic
    @Column(name = "public_key", nullable = false, length = 2147483647)
    private String publicKey;
    @Basic
    @Column(name = "refresh_token", nullable = true, length = 2147483647)
    private String refreshToken;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KeyTokenEntity that = (KeyTokenEntity) o;

        if (userId != that.userId) return false;
        if (privateKey != null ? !privateKey.equals(that.privateKey) : that.privateKey != null) return false;
        if (publicKey != null ? !publicKey.equals(that.publicKey) : that.publicKey != null) return false;
        if (refreshToken != null ? !refreshToken.equals(that.refreshToken) : that.refreshToken != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userId;
        result = 31 * result + (privateKey != null ? privateKey.hashCode() : 0);
        result = 31 * result + (publicKey != null ? publicKey.hashCode() : 0);
        result = 31 * result + (refreshToken != null ? refreshToken.hashCode() : 0);
        return result;
    }
}
