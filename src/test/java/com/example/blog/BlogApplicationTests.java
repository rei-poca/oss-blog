package com.example.blog;

import com.example.blog.repository.PostRepository;
import com.example.blog.repository.TagRepository;
import com.example.blog.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BlogApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void seedDataIsLoaded() {
        assertThat(userRepository.count()).isGreaterThanOrEqualTo(2);
        assertThat(tagRepository.count()).isEqualTo(3);
        assertThat(postRepository.count()).isGreaterThanOrEqualTo(8);
    }
}
