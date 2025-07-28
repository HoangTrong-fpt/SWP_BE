package com.quitsmoking.platform.api;

import com.quitsmoking.platform.dto.BlogRequest;
import com.quitsmoking.platform.dto.BlogResponse;
import com.quitsmoking.platform.service.BlogService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "api")
@RequestMapping("/api/blogs")
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<BlogResponse> create(@RequestBody BlogRequest request) {
        return ResponseEntity.ok(blogService.create(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<BlogResponse> update(@PathVariable Long id, @RequestBody BlogRequest request) {
        return ResponseEntity.ok(blogService.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        blogService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'COACH')")
    @GetMapping
    public List<BlogResponse> getAll() {
        return blogService.getAll();
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'COACH')")
    @GetMapping("/{id}")
    public ResponseEntity<BlogResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(blogService.getById(id));
    }
}
