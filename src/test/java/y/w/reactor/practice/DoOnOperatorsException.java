package y.w.reactor.practice;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class DoOnOperatorsException {

    @Test
    void doOnOperatorTest() {
        var flux = Flux.range(1, 5)
            .doOnNext(i -> System.out.println("doOnNext : " + i))
            .map(i -> i > 3 ? i/0 : i * i)
            .doOnError(i -> System.out.println(" Divided by zero."))
            .log();

        StepVerifier.create(flux)
            .expectNext(1, 4, 9)
            .expectError(ArithmeticException.class)
            .verify();

        StepVerifier.create(flux)
            .expectNext(1, 4, 9)
            .expectError()
            .verify();

        StepVerifier.create(Flux.just(1,2,3).zipWith(Flux.error(new ArithmeticException("Divided by zero"))))
            .expectErrorMessage("Divided by zero")
            .verify();
    }

    @Test
    void onErrorReturn() {

        var flux = Flux.range(1, 5)
            .doOnNext(i -> System.out.println("doOnNext : " + i))
            .map(i -> i > 3 ? i/0 : i * i)
            .onErrorReturn(99)
            .log();

        // Exception terminate the stream and then replace it with one value.
        StepVerifier.create(flux)
            .expectNext(1,4,9,99)
            .verifyComplete();
    }

    @Test
    void onErrorResume() {

        var flux = Flux.range(1, 5)
            .doOnNext(i -> System.out.println("doOnNext : " + i))
            .map(i -> i > 3 ? i/0 : i * i)
            .onErrorResume(e -> {
                System.out.printf(e.getMessage());
                return Flux.just(11,22);
            })
            .log();

        // Exception terminate the stream and then replace it with one value.
        StepVerifier.create(flux)
            .expectNext(1,4,9,11,22)
            .verifyComplete();
    }

    /**
     * onErrorContinue takes precedence over onErrorResume.
     */
    @Test
    void onErrorContinue() {

        var flux = Flux.range(1, 5)
            .doOnNext(i -> System.out.println("doOnNext : " + i))
            .map(i -> i == 3 ? i/0 : i * i)
            .onErrorResume(e -> { return Flux.just(55,66); }) // No effect.
            .onErrorContinue((e, i) -> {
                System.out.printf("Got problem: " + i);
            })
            .log();

        // Exception terminate the stream and then replace it with one value.
        StepVerifier.create(flux)
            .expectNext(1,4,16,25)
            .verifyComplete();
    }


    // Transform one Exception to another type.
    @Test
    void onErrorMap() {

        var flux = Flux.range(1, 5)
            .doOnNext(i -> System.out.println("doOnNext : " + i))
            .map(i -> i == 3 ? i/0 : i * i)
            .onErrorMap((e) -> {
                System.out.printf("Got problem: " + e.getMessage());

                return new RuntimeException();
            })
            .log();

        // Exception terminate the stream and then replace it with one value.
        StepVerifier.create(flux)
            .expectNext(1,4)
            .expectError(RuntimeException.class)
            .verify();
    }


    @Test
    void doOnError() {

        var flux = Flux.range(1, 5)
            .doOnNext(i -> System.out.println("doOnNext : " + i))
            .map(i -> i == 3 ? i/0 : i * i)
            .doOnError(e -> {
                System.out.println(e.getMessage());
            })
            .log();

        // Exception terminate the stream and then replace it with one value.
        StepVerifier.create(flux)
            .expectNext(1,4)
            .verifyError();
    }
}
