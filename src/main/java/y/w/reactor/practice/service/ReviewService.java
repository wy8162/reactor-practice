package y.w.reactor.practice.service;

import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import y.w.reactor.practice.model.BlogDataBase;
import y.w.reactor.practice.model.Review;

@Service
public class ReviewService {

    public Flux<Review> getReviewsByBlogId(long id) {
        return Flux.fromIterable(BlogDataBase.reviews.values().stream().filter(r -> r.getAuthorId() == id).collect(Collectors.toList()));
    }
}
