package com.example.brainAi.service;

import com.example.brainAi.dto.ArticalDTO;
import com.example.brainAi.entity.ArticalPost;
import com.example.brainAi.repository.ArticalsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticalsService {
    private final ArticalsRepository articalsRepository;

    @Autowired
    public ArticalsService(ArticalsRepository articalsRepository) {
        this.articalsRepository = articalsRepository;
    }

    public ArticalDTO mapPostToPostDTO(ArticalPost articalPost) {
        if (articalPost != null) {
            ArticalDTO postDTO = new ArticalDTO();
            postDTO.setDescription(articalPost.getDescription());
            postDTO.setContent(articalPost.getContent());
            postDTO.setTitle(articalPost.getTitle());
            postDTO.setDate(articalPost.getDate());
        }
        return null;
    }

    public ArticalPost mapPostDTOToPost(ArticalDTO articalDTO) {
        ArticalPost post = new ArticalPost();
        post.setDescription(articalDTO.getDescription());
        post.setContent(articalDTO.getContent());
        post.setTitle(articalDTO.getTitle());
        post.setDate(articalDTO.getDate());
        return post;
    }

    public ArticalDTO createPost(ArticalDTO articalDTO) throws Exception {
        try {
            ArticalPost articalPost = mapPostDTOToPost(articalDTO);
            articalsRepository.save(articalPost);
        } catch (Exception e) {
            throw new Exception("Some error occured");
        }
        return articalDTO;
    }

    public List<ArticalPost> findPostByTitle(String title) throws Exception {
        List<ArticalPost> posts = articalsRepository.findPostByTitle(title);
        if(posts.isEmpty()) {
            throw new Exception("No posts with this name");
        }
        return posts;
    }

    public ArticalDTO updatePostContent(Long id, ArticalDTO articalDTO) throws Exception {
        ArticalPost articalPost = articalsRepository.findById(id)
                .orElseThrow(() -> new Exception("Post not found"));
        articalPost.setContent(articalDTO.getContent());

        articalPost = articalsRepository.save(articalPost);
        return mapPostToPostDTO(articalPost);
    }
}
