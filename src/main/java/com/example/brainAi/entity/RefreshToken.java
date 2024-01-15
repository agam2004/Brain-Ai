package com.example.brainAi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Data
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;
    private Instant expiryDate;

    // Specifies a one-to-one relationship between RefreshToken and User entities.
    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "id")
    private User user;
}
