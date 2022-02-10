package y.w.reactor.practice;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class CreatingFluxOrMono {

    @Test
    void syncGeneratingTest() {
        var flux = Flux.generate(
                () -> 1,
                (state, sink) -> {
                    sink.next(state * 2);
                    if (state == 10) {
                        sink.complete();
                    }

                    return state + 1;
                }
            )
            .log();

        StepVerifier.create(flux)
            .expectNextCount(10)
            .verifyComplete();
    }

    @Test
    void createMono() {
        var mono = Mono.create(sink -> {
            sink.success("Virginia");
        });

        StepVerifier.create(mono)
            .expectNext("Virginia")
            .verifyComplete();
    }

    @Test
    void asyncCreatingFlux() {
        var flux = Flux.create(sink -> {
                Arrays.asList(1, 2, 3, 4, 5)
                    .forEach(sink::next);
            })
            .log();

        StepVerifier.create(flux)
            .expectNextCount(5)
            .verifyComplete();
    }
}
