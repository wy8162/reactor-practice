package y.w.reactor.practice;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Service
public class ReactorServiceHandler {

    public Mono<ServerResponse> runReactor(ServerRequest request) {
        return ServerResponse.ok().body(BodyInserters.fromValue("Hello"));
    }

}
