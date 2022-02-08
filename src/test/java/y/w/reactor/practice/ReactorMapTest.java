package y.w.reactor.practice;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class ReactorMapTest {

    @Test
    void testMap() {
        var flux = Flux.just("Cat", "Tiger", "Monkey")
            .log()
            .map(s -> s.toLowerCase())
            .log();

        StepVerifier
            .create(flux)
            .expectNextCount(3)
            .verifyComplete();
    }
}
