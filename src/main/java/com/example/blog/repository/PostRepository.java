package com.example.blog.repository;

import com.example.blog.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByPublishedTrueOrderByCreatedAtDesc();

    List<Post> findByPublishedTrueAndFeaturedTrueOrderByCreatedAtDesc();

    List<Post> findByPublishedTrueAndTagsSlugOrderByCreatedAtDesc(String slug);

    Optional<Post> findBySlug(String slug);

    Optional<Post> findBySlugAndPublishedTrue(String slug);

    boolean existsBySlug(String slug);

    List<Post> findAllByOrderByUpdatedAtDesc();

    /**
     * 本地全文搜索：关键词命中标题或正文（大小写不敏感）。
     */
    @Query("""
            SELECT DISTINCT p FROM Post p
            WHERE p.published = true
              AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :q, '%'))
                   OR LOWER(p.content) LIKE LOWER(CONCAT('%', :q, '%')))
            ORDER BY p.createdAt DESC
            """)
    List<Post> searchPublished(@Param("q") String query);
}
