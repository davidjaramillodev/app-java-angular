package com.example.gateway;

import java.util.UUID;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {
    private static final String HEADER_NAME = "X-Correlation-ID";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String incomingId = exchange.getRequest().getHeaders().getFirst(HEADER_NAME);
        String correlationId = incomingId == null || incomingId.isBlank()
                ? UUID.randomUUID().toString()
                : incomingId;

        ServerHttpRequest request = exchange.getRequest().mutate()
                .headers(headers -> headers.set(HEADER_NAME, correlationId))
                .build();
        exchange.getResponse().getHeaders().set(HEADER_NAME, correlationId);
        return chain.filter(exchange.mutate().request(request).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}