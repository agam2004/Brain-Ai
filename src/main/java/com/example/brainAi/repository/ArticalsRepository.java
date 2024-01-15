package com.example.brainAi.repository;

import com.example.brainAi.entity.ArticalPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticalsRepository extends JpaRepository<ArticalPost, Long> {

    @Query(value = "SELECT p FROM ArticalPost p WHERE p.title = :title")
    List<ArticalPost> findPostByTitle(@Param("title") String title) throws Exception;
}
