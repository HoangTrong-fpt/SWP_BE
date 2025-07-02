package com.quitsmoking.platform.repository;


import com.quitsmoking.platform.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Blog, Long> {
}
