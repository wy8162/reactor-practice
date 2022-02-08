package y.w.reactor.practice.reactor;

import reactor.core.publisher.Flux;

public class ReactorSources {

    public Flux<String> getFluxString() {
        return Flux.just("Cat", "Tiger", "Monkey");
    }
}
