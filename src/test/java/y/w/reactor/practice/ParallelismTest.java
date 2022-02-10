package y.w.reactor.practice;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.test.StepVerifier.Step;

@Slf4j
public class ParallelismTest {

    /**
     * This runs on different thread but there is no parallelism.
     * <p>
     * It takes 5 seconds to finish.
     */
    @Test
    void simplePublishOn() {
        Flux.just(1, 2, 3, 4, 5)
            .publishOn(Schedulers.boundedElastic())
            .map(i -> {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                return i + i;
            })
            .log()
            .doOnNext(System.out::println)
            .blockLast();
    }

    /**
     * This runs on different thread in parallel.
     *
     * It takes 1 seconds to finish.
     */
    @Test
    void simpleParallelRunOn() {
        var flux = Flux.just(1, 2, 3, 4, 5)
            .map(i -> {
                log.info("Start map runs on MAIN...");
                return i + i;
            })
            .parallel()
            .runOn(Schedulers.boundedElastic())
            // Everything after this point runs on Schedulers - paralleled in different threads.
            .map(i -> {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                log.info("First map ...");
                return i + i;
            })
            .log()
            .map(i -> {
                log.info("Second map ...");
                return i;
            })
            .doOnNext(System.out::println);

        StepVerifier.create(flux)
            .expectNextCount(5)
            .verifyComplete();
    }

    /**
     * This runs on different thread in parallel.
     *
     * It takes 1 seconds to finish.
     */
    @Test
    void simpleFlatMapParallelRunOn() {
        var numOfCores = Runtime.getRuntime().availableProcessors();

        log.info("Total number of COREs: " + numOfCores);

        var flux = Flux.just(1, 2, 3, 4, 5)
            .parallel()
            .runOn(Schedulers.boundedElastic())
            .flatMap(i -> {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                return Mono.just(i + i);
            })
            .log()
            .doOnNext(System.out::println);

        StepVerifier.create(flux)
            .expectNextCount(5)
            .verifyComplete();
    }

    /**
     * Case 1: runs on MAIN and takes 5 seconds to finish.
     * Case 2: runs in parallel abd takes 1 second to finish.
     *
     * NOTE: flatMapSequential works the same way, but it preserves the ordering.
     */
    @Test
    void testFlatMapParallelism() {
        // Case 1
        var flux = Flux.range(1, 5)
            .flatMap(i -> {
                log.info("Get number : {}", i);
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                return Mono.just(i * 2);
            })
            .log();

        StepVerifier.create(flux)
            .expectNextCount(5)
            .verifyComplete();

        // Case 2
        var flux2 = Flux.range(1, 5)
            .flatMap(i -> Mono.just(i)
                .map(j -> {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }
                    log.info("Mapping happens in different threads in parallel ...");
                    return j + j;
                })
                .subscribeOn(Schedulers.parallel()))
            .log();

        StepVerifier.create(flux2)
            .expectNextCount(5)
            .verifyComplete();
    }
}
