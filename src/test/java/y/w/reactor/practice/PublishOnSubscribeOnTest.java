package y.w.reactor.practice;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;


/**
 * publishOn and subscribeOn: hop on different threads.
 */
@Slf4j
public class PublishOnSubscribeOnTest {

    /**
     * This runs on different thread but there is no parallelism.
     */
    @Test
    void simplePublishOn() {
        Flux.just(1,2,3,4,5)
            .publishOn(Schedulers.boundedElastic())
            .map(i -> {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {}
                return i + i;
            })
            .log()
            .doOnNext(System.out::println)
            .blockLast();
    }

    /**
     * publishOn - impacts all operators down stream.
     */
    @Test
    void testPublishOn() {
        var flux1 = Flux.just(111, 112, 113, 114)
            .publishOn(Schedulers.parallel())
            .map(a -> {
                log.info("Third map - all operations run in bounded threads " + a);

                return a + 1000;
            });

        Flux.range(1, 10)
            .map(i -> {
                log.info("First map - all operations run in the main thread #" + i);
                return i + i;
            })
            .publishOn(Schedulers.boundedElastic())
            .map(i -> {
                log.info(
                    "Second map - all operations after publishOn runs in parallel threads #" + i);
                return i / 2 + 99;
            })
            .mergeWith(flux1)
            .log()
            .subscribe();
    }

    /**
     * subcribeOn - impact to the whole reactive stream : all will run in different threads.
     */
    @Test
    void testSubscribeOn() {
        var flux1 = Flux.just(111, 112, 113, 114)
            .map(a -> {
                log.info("Third map - all operations run in bounded threads " + a);

                return a + 1000;
            })
            .subscribeOn(Schedulers.parallel());

        Flux.range(1, 10)
            .map(i -> {
                log.info("First map - all operations run in bounded threads #" + i);
                return i + i;
            })
            .subscribeOn(Schedulers.parallel())
            .map(i -> {
                log.info(
                    "Second map - all operations after publishOn runs in parallel threads #" + i);
                return i / 2 + 99;
            })
            .mergeWith(flux1)
            .log()
            .subscribe();
    }

    @Test
    void example() {
        Flux.just("hello")
            .doOnNext(v -> System.out.println("just " + Thread.currentThread().getName()))
            .publishOn(Schedulers.boundedElastic())
            .doOnNext(v -> System.out.println("publish " + Thread.currentThread().getName()))
            .delayElements(Duration.ofMillis(500))
            .subscribeOn(Schedulers.elastic())
            .subscribe(v -> System.out.println(v + " delayed " + Thread.currentThread().getName()));
    }
}
