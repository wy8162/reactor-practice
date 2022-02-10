package y.w.reactor.practice;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

public class RetryRepeatTest {

    @Test
    void testRetry() {
        var flux = Flux.just(1, 2, 3, 4)
            .map(i -> i == 2 ? i / 0 : i * i)
            .retry(2)
            .log()
            .subscribe();
    }

    @Test
    void testWhenRetry() {
        var backoff = Retry.backoff(3, Duration.ofMillis(500))
            .onRetryExhaustedThrow(
                (spec, retrySignal) -> Exceptions.propagate(retrySignal.failure()));

        Flux.range(1, 10)
            .map(i -> i == 4 ? i / 0 : i * i)
            .onErrorMap(e -> {
                throw new RuntimeException();
            })
            .retryWhen(backoff)
            .log()
            .subscribe();
    }
}
