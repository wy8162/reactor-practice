package y.w.reactor.practice;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class ConcatMergeZipTests {

    // Sequential
    @Test
    void concatTest() {
        Flux.concat(Flux.just(1, 2, 3), Flux.just(4,5,6))
            .log()
            .subscribe();

        Flux.just(1, 2, 3)
            .concatWith(Flux.just(4,5,6))
            .log()
            .subscribe();
    }

    // Interleave
    @Test
    void mergeTest() throws InterruptedException {
        Flux.merge(
                Flux.just(1, 2, 3).delayElements(Duration.ofMillis(100)),
                Flux.just(4, 5, 6).delayElements(Duration.ofMillis(50))
            )
            .log()
            .subscribe();

        Flux.just(7, 8, 9)
            .mergeWith(Flux.just(10, 11, 12))
            .log()
            .subscribe();

        Thread.sleep(2000);
    }

    // Sequential
    @Test
    void mergeSequentialTest() throws InterruptedException {
        Flux.mergeSequential(
                Flux.just(1, 2, 3).delayElements(Duration.ofMillis(100)),
                Flux.just(4, 5, 6).delayElements(Duration.ofMillis(50))
            )
            .log()
            .subscribe();

        Thread.sleep(2000);
    }

    @Test
    void zipTest() {
        Flux.just(7, 8, 9)
            .zipWith(Flux.just(10, 11, 12))
            .log()
            .subscribe();

        Flux.zip(Flux.just(7, 8, 9), (Flux.just(10, 11, 12)))
            .log()
            .subscribe();

        // Combines two values using the combinator BiFunction.
        var flux = Flux.just(7, 8, 9)
            .zipWith(Flux.just(10, 11, 12), (i, j) -> i * j)
            .log();

        StepVerifier.create(flux)
            .expectNext(70)
            .expectNext(88)
            .expectNext(108)
            .verifyComplete();
    }
}
