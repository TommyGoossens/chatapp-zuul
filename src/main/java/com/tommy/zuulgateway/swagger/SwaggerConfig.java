package com.tommy.zuulgateway.swagger;

import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Primary
public class SwaggerConfig implements SwaggerResourcesProvider {

    private RouteLocator routeLocator;

    public SwaggerConfig(RouteLocator routeLocator) {
        this.routeLocator = routeLocator;
    }

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.tommy.zuulgayeway")) //Gets all endpoints in this package
                .paths(PathSelectors.any()) //No filters
                .build()
                .apiInfo(apiInfo()); //Sets the info of this API page
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Zuul proxy gateway")
                .description("Micro-service endpoints")
                .version("1.0.0")
                .build();
    }

    /**
     * For every micro service that's configured with zuul, a swagger resource will be generated
     * Each individual micro service needs to have it's own swagger2 implementation.
     * @return A List of SwaggerResources that contain the location to the swagger JSON file of each route
     */
    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>();
        routeLocator.getRoutes().forEach(route -> {
            resources.add(swaggerResource(route.getId(), route.getFullPath()));
        });
        return resources;
    }

    /**
     * Creates a single SwaggerResource, which will be used to view the specific micro service API docs.
     * @param name The name of the micro service (Will come the service-id in zuul config)
     * @param location The location of the API docs of that micro service ( Will be build using the full path in zuul config)
     * @return A single SwaggerResource object containing the name and location of the API to be rendered.
     */
    private SwaggerResource swaggerResource(String name, String location) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(locationBuilder(location));
        swaggerResource.setSwaggerVersion("2.0");
        return swaggerResource;
    }

    /**
     * Because the getFullPath() function doesn't return a valid URL, edit it to become a valid url
     * @param location The URL coming from zuul ( http://url:port//** )
     * @return The URL used to find the micro service's api-docs ( http://url:port/v2/api-docs )
     */
    private String locationBuilder(String location){
        location = location.replace("/**", "");
        return location + "/v2/api-docs";
    }
}
