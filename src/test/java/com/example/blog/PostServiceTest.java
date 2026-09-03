package com.example.blog;

import com.example.blog.model.Post;
import com.example.blog.model.Tag;
import com.example.blog.repository.PostRepository;
import com.example.blog.service.PostService;
import com.example.blog.util.BlogUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 自主扩展功能测试：阅读时长估算、按标签相关推荐。
 */
@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Test
    void readingMinutesIsEstimatedFromContentLength() {
        assertThat(BlogUtils.readingMinutes("短文本")).isEqualTo(1);
        assertThat(BlogUtils.readingMinutes("字".repeat(800))).isEqualTo(2);
        assertThat(BlogUtils.readingMinutes("")).isEqualTo(1);
    }

    @Test
    @Transactional
    void relatedPostsShareTagsAndExcludeSelf() {
        Post post = postRepository.findBySlug("spring-boot-quickstart").orElseThrow();
        List<Post> related = postService.related(post, 3);

        assertThat(related).isNotEmpty();
        assertThat(related).doesNotContain(post);

        Set<Long> postTagIds = post.getTags().stream().map(Tag::getId).collect(Collectors.toSet());
        boolean sharesTag = related.stream()
                .anyMatch(r -> r.getTags().stream().anyMatch(t -> postTagIds.contains(t.getId())));
        assertThat(sharesTag).isTrue();
    }
}
