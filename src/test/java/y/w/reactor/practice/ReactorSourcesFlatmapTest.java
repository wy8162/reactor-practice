package y.w.reactor.practice;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import y.w.reactor.practice.reactor.ReactorSources;

class ReactorSourcesFlatmapTest {

    private final ReactorSources sources = new ReactorSources();

    @Test
    void getFluxStringFilter() {
        var flux = sources.getFluxString()
            .filter(s -> !s.equals("Cat"));

        StepVerifier.create(flux)
            .expectNext("Tiger", "Monkey")
            .verifyComplete();
    }

    @Test
    void testFlatmap() {
        var flux = sources.getFluxString()
            .map(s -> s.toUpperCase())
            .flatMap(s -> Flux.fromArray(s.split("")))
            .log();

        flux.subscribe(System.out::println);
    }

    @Test
    void testFlatmapExploringAsync() throws InterruptedException {
        var flux = sources.getFluxString()
            .map(s -> s.toUpperCase())
            .flatMap(this::delayedTransform)
            .log();

        // The flatmap runs asynchronously.
        flux.subscribe(s -> {
            System.out.println(
                String.format("Thread %s, ==>%s", Thread.currentThread().getName(), s));
        });

        Thread.sleep(5000);
    }

    @Test
    void testCatmapExploringAsync() throws InterruptedException {
        var flux = sources.getFluxString()
            .map(s -> s.toUpperCase())
            .concatMap(this::delayedTransform)
            .log();

        // The concatMap runs asynchronously but preserves the ordering.
        // concatMap is slower than flatMap because concatMap waits for
        // the completion of maps to preserve orderings.
        flux.subscribe(s -> {
            System.out.println(
                String.format("Thread %s, ==>%s", Thread.currentThread().getName(), s));
        });

        Thread.sleep(5000);
    }

    /**
     * Transform a Mono<T> to Mono<List<T>>
     */
    @Test
    public void monoFlatMap() {
        var stringListMono = Mono.just("123")
            .flatMap(s -> Mono.just(s.split("")));

        stringListMono.log().subscribe();
    }

    /**
     * Transform a Mono<T> to Flux<R>
     */
    @Test
    public void monoFlatMapMany() {
        Mono.just("123")
            .flatMapMany(s -> Flux.fromArray(s.split("")))
            .log()
            .subscribe();

        Mono.just("A")
            .flatMapMany(s -> Flux.fromIterable(List.of(s,s,s)))
            .log()
            .subscribe();
    }

    /**
     * Transform one type to the other.
     */
    @Test
    void transform() {
        Flux.just("a", "aa", "aaa")
            .transform(s -> s.map(String::toUpperCase).filter(ss -> ss.length() >= 2))
            .log()
            .subscribe();
    }

    private Flux<String> delayedTransform(String s) {
        var delay = new Random().nextInt(1000);

        return Flux.fromArray(s.split(""))
            .delayElements(Duration.ofMillis(delay));
    }
}