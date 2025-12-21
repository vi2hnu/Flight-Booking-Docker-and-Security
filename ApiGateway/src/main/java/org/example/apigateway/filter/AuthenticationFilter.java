package org.example.apigateway.filter;

import org.example.apigateway.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator validator;

    @Autowired
    private JwtUtils jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (validator.isSecure.test(exchange.getRequest())) {

                String token = exchange.getRequest()
                        .getCookies()
                        .getFirst("cookie") != null
                        ? exchange.getRequest().getCookies().getFirst("cookie").getValue()
                        : null;

                if (token == null) {
                    return onError(exchange, "Missing JWT Cookie", HttpStatus.UNAUTHORIZED);
                }

                try {
                    jwtUtil.validateToken(token);
                } catch (Exception e) {
                    return onError(exchange, "Invalid Token", HttpStatus.UNAUTHORIZED);
                }
            }
            return chain.filter(exchange);
        };
    }


    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }

    public static class Config {
    }
}