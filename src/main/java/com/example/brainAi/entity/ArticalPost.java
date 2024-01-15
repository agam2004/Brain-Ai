package com.example.brainAi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticalPost {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "idArticalPost", nullable = false)
    private Long idArticalPost;

    @Column(nullable = false, unique = true, length = 70)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Date date;
}