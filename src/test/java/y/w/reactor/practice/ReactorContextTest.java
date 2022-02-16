package y.w.reactor.practice;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class ReactorContextTest {

    @Test
    void contextTest1() {
        String multiplier = "multiplier";

        var f = Flux.range(1, 10)
            .flatMap(i -> Mono.deferContextual(ctx -> Mono.just(ctx.get(multiplier)))
                .map(s -> s + " " + i))
            .log()
            .contextWrite(ctx -> ctx.put(multiplier, "Multi -"));

        StepVerifier.create(f)
            .expectNextCount(10)
            .verifyComplete();
    }


    @Test
    void contextTest2() {
        String STORAGE = "store";
        String ACTION  = "action";

        var f = Flux.just("a", "b", "c")
            .flatMap(a -> Mono.deferContextual(Mono::just)
                .map(ctx -> {
                    Map<String, String> m = ctx.get(STORAGE);
                    m.put(ACTION, "action");
                    return a;
                }))
            .flatMap(a -> Mono.deferContextual(Mono::just)
                .map(ctx -> {
                    Map<String, String> m = ctx.get(STORAGE);

                    return a + " --> " + m.get(ACTION);
                }))
            .log()
            .contextWrite(ctx -> ctx.put(STORAGE, new HashMap<String, String>()));

        StepVerifier.create(f)
            .expectNextCount(3)
            .verifyComplete();
    }

    @Test
    void contextTest3() {
        String OP1 = "OP1";
        String OP2 = "OP2";
        String OP3 = "OP3";

        var f = Flux.range(1, 10)
            .flatMap(i -> Mono.deferContextual(
                ctx -> Mono.just(String.format("%s %d", ctx.get(OP1), i))))
            .flatMap(s -> Mono.deferContextual(
                ctx -> Mono.just(String.format("%s %s", ctx.get(OP2), s))))
            .log()
            .contextWrite(ctx -> ctx.put(OP1, OP1))
            .contextWrite(ctx -> ctx.put(OP2, OP2))
            .contextWrite(ctx -> ctx.put(OP3, OP3));

        StepVerifier.create(f)
            .expectNextCount(10)
            .verifyComplete();
    }
}
