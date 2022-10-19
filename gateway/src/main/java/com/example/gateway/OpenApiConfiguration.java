package com.example.gateway;

import io.swagger.v3.core.filter.SpecFilter;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.OpenAPIService;
import org.springdoc.core.customizers.OpenApiBuilderCustomizer;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Configuration
public class OpenApiConfiguration {

    private static final Logger log = LoggerFactory.getLogger(OpenApiConfiguration.class);

    @Bean
    public OpenAPI openAPI() {
        log.info("openAPI Bean called for route definitions");
        return new OpenAPI().info(new Info().title("Spring Cloud Architecture API").version("1.0").description(""));
    }

    @Bean
    public List<OpenApiBuilderCustomizer> openApiCustomizer(RouteDefinitionLocator locator, OpenAPI openApi) {
        var routeDefinitions = locator.getRouteDefinitions().collectList().block();
        log.info("openApiCustomizer Bean called for route definitions: " + routeDefinitions.stream().map(RouteDefinition::getUri).map(URI::toString).collect(Collectors.joining(",")));
        return Objects.requireNonNull(routeDefinitions)
                .stream()
                .map(routeDefinition -> new GroupCustomiser(routeDefinition, openApi))
                .collect(Collectors.toList());
    }

    private class GroupCustomiser extends SpecFilter implements OpenApiBuilderCustomizer {
        private RouteDefinition routeDefinition;
        private OpenAPI openApi;

        public GroupCustomiser(RouteDefinition routeDefinition, OpenAPI openApi) {
            this.routeDefinition = routeDefinition;
            this.openApi = openApi;
        }

        @Override
        public void customise(OpenAPIService openApiService) {
            var openApiUrl = routeDefinition.getUri() + "/v3/api-docs";
            log.info("GroupCustomiser called for OpenApi uri " + openApiUrl);
            var api = new OpenAPIV3Parser().read(openApiUrl);
            this.openApi.setComponents(api.getComponents());
            openApi.setPaths(api.getPaths());
            openApi.setExtensions(api.getExtensions());
            openApi.setInfo(api.getInfo());
            openApi.setExternalDocs(api.getExternalDocs());
            openApi.setOpenapi(api.getOpenapi());
            openApi.setSecurity(api.getSecurity());
            openApi.setServers(api.getServers());
            openApi.setTags(api.getTags());
            super.removeBrokenReferenceDefinitions(openApi);
        }
    }

}
