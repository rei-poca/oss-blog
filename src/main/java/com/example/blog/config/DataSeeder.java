package com.example.blog.config;

import com.example.blog.model.Post;
import com.example.blog.model.Role;
import com.example.blog.model.Tag;
import com.example.blog.model.User;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.TagRepository;
import com.example.blog.repository.UserRepository;
import com.example.blog.util.BlogUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

/**
 * 首次启动时注入演示数据：2 个账号、3 个标签、8 篇文章。
 * 仅当用户表为空时执行，保证重复启动不产生重复数据。
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      TagRepository tagRepository,
                      PostRepository postRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        User admin = userRepository.save(new User("admin", "admin@example.com",
                passwordEncoder.encode("admin123"), Role.ADMIN));
        User member = userRepository.save(new User("member", "member@example.com",
                passwordEncoder.encode("member123"), Role.MEMBER));

        Tag java = tagRepository.save(new Tag("Java", "java"));
        Tag spring = tagRepository.save(new Tag("Spring", "spring"));
        Tag opensource = tagRepository.save(new Tag("开源", "open-source"));

        postRepository.save(post("Spring Boot 快速入门", "spring-boot-quickstart", """
                Spring Boot 让 Java 开发者可以快速搭建一个可运行的 Web 应用。
                它通过自动配置和起步依赖（Starter）大幅减少了样板代码，你不需要再手动配置
                Tomcat、Spring MVC 和依赖注入，只需关注业务本身。

                下面是一个最小化的入口类：

                @SpringBootApplication
                public class BlogApplication {
                    public static void main(String[] args) {
                        SpringApplication.run(BlogApplication.class, args);
                    }
                }

                通过 @SpringBootApplication 注解，应用会自动扫描组件并完成默认配置。
                """, true, 0, admin, java, spring));

        postRepository.save(post("Java 8 到 17 的新特性", "java-8-to-17", """
                Java 在 8 之后进入了一个快速迭代的周期，很多新特性值得关注。

                例如 Java 14 引入了 switch 表达式，Java 15 引入了文本块，Java 16 引入了
                record，Java 17 则成为长期支持（LTS）版本。

                record Point(int x, int y) {}

                文本块让多行字符串不再需要大量的转义：

                String json = \"""
                        {"name": "blog", "lang": "java"}
                        \""";

                这些语法糖让代码更简洁、更易读。
                """, false, 1, admin, java));

        postRepository.save(post("什么是开源软件许可证", "open-source-licenses", """
                开源并不等于“随便用”。每一个开源项目都有一个许可证，它规定了使用者可以做什么、
                不可以做什么。

                常见的许可证包括 MIT、Apache-2.0、GPL 和 AGPL。MIT 非常宽松，只要保留版权声明
                即可；GPL 带有“传染性”，衍生作品也需要以 GPL 开源；AGPL 则进一步要求网络服务
                也要向用户提供源代码。

                在做二次开发时，一定要先阅读上游项目的许可证，并在自己的仓库中保留版权与来源说明。
                """, true, 2, admin, opensource));

        postRepository.save(post("使用 H2 数据库做本地持久化", "h2-local-persistence", """
                博客需要一个轻量、可复现的数据存储。H2 是一个纯 Java 的关系型数据库，支持内存
                和文件两种模式。

                文件模式适合本地演示：数据写入磁盘，应用重启后文章和评论仍然存在。

                jdbc:h2:file:./data/blogdb

                配合 Spring Data JPA，实体与表之间的映射、增删改查都由框架自动完成，
                我们只需要编写接口即可。
                """, false, 3, admin, spring, java));

        postRepository.save(post("个人博客的搜索功能设计", "blog-search-design", """
                搜索是博客的重要入口。一个简单可靠的方案是使用数据库的 LIKE 查询，对标题和正文
                做大小写不敏感的模糊匹配。

                对于中文内容，这种方案不需要额外的分词器，只要关键词能出现在标题或正文中，就能
                被命中。搜索无结果时，应当给用户明确的提示，而不是显示空白页面。

                后续如果需要更强的检索能力，可以引入全文索引，但这会带来部署复杂度和数据一致性
                方面的权衡。
                """, true, 4, admin, opensource, spring));

        postRepository.save(post("REST API 设计规范与 RealWorld", "rest-api-realworld", """
                RealWorld 是一个约定前后端契约的示例项目，它用统一的 API 规范描述了一个博客
                系统应有的行为：注册、登录、文章、标签、评论等。

                通过对比不同语言对同一规范的实现，可以更清楚地理解“规范”与“实现”的差异。
                规范只定义接口和数据结构，而具体的技术栈可以自由选择。

                这提醒我们：在做二次开发前，先明确数据边界和接口契约，再动手编码。
                """, false, 5, admin, opensource));

        postRepository.save(post("微服务架构入门与适用边界", "microservices-intro-and-boundary", """
                微服务把一个大系统拆分为多个可以独立部署的小服务，每个服务围绕一个业务能力组织。

                微服务带来的好处是独立部署和技术选型自由，代价则是分布式带来的复杂度：网络调用、
                数据一致性、服务治理都会变得困难。

                对于一个个人博客来说，微服务往往是过度设计。单体架构配合清晰的模块边界，
                通常已经足够。选择架构应当看问题本身，而不是追逐潮流。
                """, true, 6, admin, spring));

        postRepository.save(post("如何参与开源社区贡献", "how-to-contribute-to-open-source", """
                参与开源并不是只有写代码。报告一个清晰的 Issue、改进文档、修复一个拼写错误，
                都是有价值的贡献。

                一个规范的贡献流程通常包括：先在 Issue 中讨论，再创建特性分支开发，提交后发起
                Pull Request，最后由维护者 Code Review。

                使用 Git 留下清晰的提交记录，能让维护者快速理解你的意图，也方便他人复现你的工作。
                """, false, 7, member, opensource));
    }

    private Post post(String title, String slug, String content, boolean featured,
                      int daysAgo, User author, Tag... tags) {
        Post p = new Post();
        p.setTitle(title);
        p.setSlug(slug);
        p.setContent(content.strip());
        p.setExcerpt(BlogUtils.excerpt(content.strip(), 150));
        p.setPublished(true);
        p.setFeatured(featured);
        p.setAuthor(author);
        p.setTags(new HashSet<>(Arrays.asList(tags)));
        LocalDateTime createdAt = LocalDateTime.now().minusDays(daysAgo);
        p.setCreatedAt(createdAt);
        p.setUpdatedAt(createdAt);
        return p;
    }
}
