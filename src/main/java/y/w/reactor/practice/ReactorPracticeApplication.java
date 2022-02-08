package y.w.reactor.practice;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@RequiredArgsConstructor
@SpringBootApplication
public class ReactorPracticeApplication {
	private final ReactorServiceHandler serviceHandler;

	@Bean
	RouterFunction<ServerResponse> routes() {
		return route()
			.GET("api/reactor", accept(MediaType.APPLICATION_JSON), serviceHandler::runReactor)
			.route(RequestPredicates.all(), r -> ServerResponse.status(HttpStatus.NOT_FOUND).body(
				BodyInserters.fromValue("NOT FOUND")))
			.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(ReactorPracticeApplication.class, args);
	}

}
