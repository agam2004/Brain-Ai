package com.example.brainAi.controller;

import com.example.brainAi.dto.ArticalDTO;
import com.example.brainAi.entity.ArticalPost;
import com.example.brainAi.service.ArticalsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("articales")
@RequiredArgsConstructor
public class ArticalController {

    private final ArticalsService articalsService;

    @PostMapping
    public ResponseEntity<ArticalDTO> createPatient(@Valid @RequestBody ArticalDTO articalDTO) throws Exception {
        return ResponseEntity.ok(articalsService.createPost(articalDTO));
    }

    @GetMapping
    public List<ArticalPost> findPostByTitle(String title) throws Exception {
        return articalsService.findPostByTitle(title);
    }

    @PutMapping
    public ResponseEntity<ArticalDTO> updatePostContent(@PathVariable Long id,@Valid @RequestBody ArticalDTO articalDTO) throws Exception {
        return ResponseEntity.ok(articalsService.updatePostContent(id, articalDTO));
    }
}
