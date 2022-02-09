package y.w.reactor.practice;

import java.util.List;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class StreamEventsTest {

    @Test
    public void streamEventsTest() {
        Flux.fromIterable(List.of("Cat", "Tiger", "Wolf"))
            .log()
            .subscribe(System.out::println);
    }

    @Test
    public void verifySteps() {
        var flux = Flux.fromIterable(List.of("Cat", "Tiger", "Wolf"));

        StepVerifier
            .create(flux)
            .expectNext("Cat", "Tiger", "Wolf")
            .verifyComplete();

        StepVerifier
            .create(flux)
            .expectNext("Cat")
            .expectNextCount(2)
            .verifyComplete();
    }

    @Test
    public void ifEmpty() {
        Mono.empty()
            .defaultIfEmpty("itIsEmpty")
            .log()
            .subscribe();

        Mono.empty()
            .switchIfEmpty(Mono.just("switchedBecauseOfEmpty"))
            .log()
            .subscribe();
    }
}
