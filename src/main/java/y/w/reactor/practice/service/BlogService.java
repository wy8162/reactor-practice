package y.w.reactor.practice.service;


import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import y.w.reactor.practice.model.Blog;
import y.w.reactor.practice.model.BlogDataBase;

@Service
public class BlogService {
    public Flux<Blog> getBlogsByAuthorId(long id) {
        return Flux.fromIterable(BlogDataBase.blogs.values().stream().filter(b -> b.getAuthorId() == id).collect(Collectors.toList()));
    }

    public Mono<Blog> getBlogByBlogId(long id) {
        return Optional.ofNullable(BlogDataBase.blogs.get(id))
            .map(Mono::just)
            .orElse(Mono.empty());
    }
}
