package y.w.reactor.practice;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * https://www.woolha.com/tutorials/project-reactor-processing-flux-in-parallel
 *
 * By default, Reactive flow is sequential. To use parallel, use ParallelFlux or flatMap.
 */
public class ReactorParallelTest {

    @Test
    void testParallelFlux1() {
        Flux.range(1, 10)
            .parallel(5)
            .runOn(Schedulers.boundedElastic())
            .doOnNext(i -> {
                System.out.println(String.format("Executing %s on thread %s", i, Thread.currentThread().getName()));

                // Do something which takes time.

                System.out.println(String.format("Finish executing %s", i));
            })
            .log()
            .subscribe();
    }

    @Test
    void testParallelFlux2() {
        Flux.range(1, 10)
            .parallel(5, 1)
            .runOn(Schedulers.newParallel("parallel", 10))
            .flatMap(
                i -> {
                    System.out.println(String.format("Start executing %s on thread %s", i, Thread.currentThread().getName()));

                    // Do something here

                    System.out.println(String.format("Finish executing %s", i));

                    return Mono.just(i);
                }
            )
            .log()
            .subscribe();
    }

    @Test
    void testFlatMap1() {
        Scheduler scheduler = Schedulers.boundedElastic();

        Flux.range(1, 10)
            .flatMap(
                i -> Mono.defer(() -> {
                    System.out.println(String.format("Executing %s on thread %s", i, Thread.currentThread().getName()));

                    // Do something

                    System.out.println(String.format("Finish executing %s", i));

                    return Mono.just(i + 5);
                }).subscribeOn(scheduler)
                ,
                5
            )
            .log()
            .subscribe(System.out::println);
    }

    @Test
    void flatMap2() {
        Scheduler scheduler = Schedulers.newParallel("parallel", 2);

        Flux.range(1, 10)
            .flatMap(
                i -> Mono.defer(() -> {
                    System.out.println(String.format("Executing %s on thread %s", i, Thread.currentThread().getName()));

                    return Mono.delay(Duration.ofSeconds(i))
                        .flatMap(x -> {
                            System.out.println(String.format("Finish executing %s", i));

                            return Mono.just(i);
                        });
                }).subscribeOn(scheduler),
                5
            )
            .log()
            .subscribe(System.out::println);
    }
}
