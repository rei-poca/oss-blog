package com.example.blog;

import com.example.blog.model.Post;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * 内容主流程与权限测试：文章、标签、搜索、评论、后台权限。
 */
@SpringBootTest
class ContentFlowIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TagRepository tagRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void homePageListsPublishedPosts() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(content().string(containsString("Spring Boot 快速入门")));
    }

    @Test
    void tagPageFiltersPostsByTag() throws Exception {
        mockMvc.perform(get("/tag/java"))
                .andExpect(status().isOk())
                .andExpect(view().name("tag"))
                .andExpect(content().string(containsString("Spring Boot 快速入门")));
    }

    @Test
    void searchHitsChineseKeyword() throws Exception {
        mockMvc.perform(get("/search").param("q", "微服务"))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(content().string(containsString("微服务")));
    }

    @Test
    void searchNoResultShowsHint() throws Exception {
        mockMvc.perform(get("/search").param("q", "一个不存在的关键词xyz"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("没有找到")));
    }

    @Test
    void searchSupportsTagFilter() throws Exception {
        mockMvc.perform(get("/search").param("q", "Java").param("tag", "java"))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(content().string(containsString("filter-bar")));
    }

    @Test
    void anonymousCannotAccessAdmin() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser(username = "member", roles = "MEMBER")
    void memberCannotAccessAdmin() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminCanAccessAdmin() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminCanCreatePost() throws Exception {
        long before = postRepository.count();
        mockMvc.perform(post("/admin/posts/new")
                        .param("title", "一篇新发布的文章")
                        .param("content", "这是正文内容，用于验证管理员可以发布文章。")
                        .param("published", "true")
                        .param("tagIds", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
        assertThat(postRepository.count()).isEqualTo(before + 1);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminCannotCreatePostWithEmptyTitle() throws Exception {
        mockMvc.perform(post("/admin/posts/new")
                        .param("title", "")
                        .param("content", "正文"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/post-form"))
                .andExpect(content().string(containsString("标题不能为空")));
    }

    @Test
    @WithMockUser(username = "member", roles = "MEMBER")
    void memberCanComment() throws Exception {
        Post post = postRepository.findBySlug("spring-boot-quickstart").orElseThrow();
        long before = commentRepository.findByPostIdOrderByCreatedAtAsc(post.getId()).size();
        mockMvc.perform(post("/post/spring-boot-quickstart/comment")
                        .param("content", "写得很清楚，感谢分享！"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/spring-boot-quickstart"));
        assertThat(commentRepository.findByPostIdOrderByCreatedAtAsc(post.getId()).size()).isEqualTo(before + 1);
    }

    @Test
    void anonymousCannotComment() throws Exception {
        mockMvc.perform(post("/post/spring-boot-quickstart/comment")
                        .param("content", "匿名评论"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}
