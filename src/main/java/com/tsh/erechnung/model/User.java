package com.tsh.erechnung.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String email;
    private String password; // hashed
    private String name;
    private Instant createdAt;
    private Instant updatedAt;
}
