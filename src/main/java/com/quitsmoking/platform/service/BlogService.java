package com.quitsmoking.platform.service;

import com.quitsmoking.platform.dto.BlogRequest;
import com.quitsmoking.platform.dto.BlogResponse;
import com.quitsmoking.platform.entity.Blog;
import com.quitsmoking.platform.repository.BlogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BlogService  {

    private final BlogRepository blogRepo;

    public BlogService(BlogRepository blogRepo) {
        this.blogRepo = blogRepo;
    }


    public BlogResponse create(BlogRequest request) {
        Blog blog = new Blog();
        blog.setTitle(request.getTitle());
        blog.setContent(request.getContent());
        blog.setPublishedAt(LocalDateTime.now());

        blogRepo.save(blog);
        return toDto(blog);
    }


    public BlogResponse update(Long id, BlogRequest request) {
        Blog blog = blogRepo.findById(id).orElseThrow();
        blog.setTitle(request.getTitle());
        blog.setContent(request.getContent());
        blogRepo.save(blog);
        return toDto(blog);
    }

    public void delete(Long id) {
        blogRepo.deleteById(id);
    }

    public List<BlogResponse> getAll() {
        return blogRepo.findAll().stream().map(this::toDto).toList();
    }

    public BlogResponse getById(Long id) {
        return blogRepo.findById(id).map(this::toDto).orElseThrow();
    }

    private BlogResponse toDto(Blog blog) {
        return new BlogResponse(blog.getId(), blog.getTitle(), blog.getContent(), blog.getPublishedAt());
    }
}
