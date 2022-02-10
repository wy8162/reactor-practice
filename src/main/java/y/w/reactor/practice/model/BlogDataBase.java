package y.w.reactor.practice.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BlogDataBase {
    public final static Map<Long, Blog> blogs = new HashMap<>();
    public final static Map<Long, Review> reviews = new HashMap<>();
    public final static Map<Long, Author> authors = new HashMap<>();

    static {
        for (long i = 1; i <= 10; i++) {
            authors.put(i, new Author("Author #" + i));
        }

        for (long i = 1; i <= 10; i++) {
            reviews.put(i, new Review(i, "My review #" + i));
        }

        var random = new Random();
        for (long i = 1; i <= 10; i++) {
            var authorId = random.nextInt(10);
            var blogReviews = new ArrayList<Review>();
            for (int j = 0; j < random.nextInt(5); j++) {
                blogReviews.add(reviews.get(random.nextInt(10)));
            }

            blogs.put(i, new Blog(i, "My posts #" + i, blogReviews));
        }
    }
}
