package com.example.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.kubernetes.discovery.KubernetesDiscoveryClientProperties;
import org.springframework.cloud.kubernetes.discovery.KubernetesReactiveDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    public ReactiveDiscoveryClient kubernetesReactiveDiscoveryClient(WebClient.Builder webClientBuilder,
            KubernetesDiscoveryClientProperties properties) {
        return new KubernetesReactiveDiscoveryClient(webClientBuilder, properties);
    }
}
