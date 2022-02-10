package y.w.reactor.practice;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

@Slf4j
public class HotStream {

    /**
     * Cold Stream - all subscriptions get the whole reactive stream.
     * Hot Stream - subscriptions happen continuously.
     */
    @Test
    void hotStream() {
        var flux = Flux.range(1, 20)
            .delayElements(Duration.ofSeconds(1));

        var conn = flux.publish();
        conn.connect();

        conn.subscribe(i -> System.out.println("Sub1 - Value " + i));

        delay(5000);

        conn.subscribe(i -> System.out.println("Sub2 - Value " + i));

        delay(10000);
    }

    /**
     * Cold Stream - all subscriptions get the whole reactive stream.
     * Hot Stream - subscriptions happen continuously.
     */
    @Test
    void hotStreamAutoConnect() {
        var flux = Flux.range(1, 20)
            .delayElements(Duration.ofSeconds(1));

        var conn = flux.publish().autoConnect(2);

        conn.subscribe(i -> System.out.println("Sub1 - Value " + i));
        System.out.println("First sub1 done");

        delay(1000);
        conn.subscribe(i -> System.out.println("Sub2 - Value " + i));
        System.out.println("Second sub2 done");

        delay(1000);
        conn.subscribe(i -> System.out.println("Sub3 - Value " + i));
        System.out.println("Third sub3 done");

        delay(20000);
    }

    private void delay(long delayMs) {
        try {
            Thread.sleep(delayMs);
        } catch (Exception e) {}
    }
}
