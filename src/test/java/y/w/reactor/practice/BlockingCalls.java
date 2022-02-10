package y.w.reactor.practice;

import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class BlockingCalls {

    @Test
    void testBlockingCalls() {
        var mono0 = Mono.just(100);
        var mono1 = Mono
            .fromCallable(() -> fakeBlockService(500))
            .subscribeOn(Schedulers.boundedElastic());

        Flux.just(1)
            .zipWith(mono0, (i, j) -> i + j)
            .zipWith(mono1, (i, j) -> i * j)
            .log()
            .doOnNext(System.out::println)
            .blockLast();
    }

    @Test
    void testBlockingCallsMany() {
        var mono = Mono
            .fromCallable(() -> fakeBlockService(500))
            .repeat(20)
            .subscribeOn(Schedulers.parallel());

        Flux.range(1, 20)
            .zipWith(mono, (i, j) -> i * j)
            .log()
            .doOnNext(System.out::println)
            .blockLast();
    }

    @Test
    void testBlockingCallsFlatMap() {
        var mono = Mono
            .fromCallable(() -> fakeBlockService(1000))
            .subscribeOn(Schedulers.boundedElastic());

        Flux.range(1, 100)
            .publishOn(Schedulers.parallel())
            .flatMap(i -> {
                long start = System.currentTimeMillis();
                var v = mono.map(j -> {
                    log.info("Zipping it now: {} + {}", i, j);
                    return i + j;
                });

                log.info("Finished mono blocking calls: " + (System.currentTimeMillis() - start));

                return v;
            })
            .log()
            .doOnNext(System.out::println)
            .blockLast();
    }

    private static final Random random = new Random();

    private int fakeBlockService(long delayTime) {
        delay(delayTime);

        var r = random.nextInt(1000);

        log.info("Got number: " + r);

        return r;
    }

    private void delay(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
        }
    }
}
