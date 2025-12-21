package org.example.apigateway.filter;

import io.jsonwebtoken.Claims;
import org.example.apigateway.jwt.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);
    @Autowired
    private RouteValidator validator;

    @Autowired
    private JwtUtils jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Collections.singletonList("requiredRole");
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
                    Claims claims = jwtUtil.validateToken(token);
                    //for simplicity and testing each user will have only 1 role
                    String role = ((Map<?, ?>) ((List<?>) claims.get("Role")).getFirst()).get("name").toString();
                    log.info(role);
                    log.info("checking the role required {}",config.getRequiredRole());
                    if(config.getRequiredRole() != null ){
                        log.info("inside a route that has rbac");
                        if(role== null || !role.equals(config.getRequiredRole())){
                            return onError(exchange, "Not enough permission to perform such actions", HttpStatus.FORBIDDEN);
                        }

                    }
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
        private String requiredRole;

        public String getRequiredRole() {
            return requiredRole;
        }

        public void setRequiredRole(String requiredRole) {
            this.requiredRole = requiredRole;
        }
    }
}