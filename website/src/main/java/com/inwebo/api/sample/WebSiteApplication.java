package com.inwebo.api.sample;

import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.*;

@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class})
public class WebSiteApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        new SpringApplicationBuilder(WebSiteApplication.class)
                .registerShutdownHook(true)
                .logStartupInfo(true)
                .showBanner(false)
                .run(args);
    }

//    @Bean
//    public ServletRegistrationBean dispatcherServlet() {
//        final ServletRegistrationBean registration = new ServletRegistrationBean(new DispatcherServlet(), "/");
//        registration.setAsyncSupported(true);
//        return registration;
//    }

    @Bean
    protected Jackson2ObjectMapperBuilder jacksonBuilder() {
        final Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder()
                .serializationInclusion(NON_NULL)
                .modules(new JsonOrgModule())
                .indentOutput(false)
                .featuresToDisable(WRITE_NULL_MAP_VALUES,
                        WRITE_EMPTY_JSON_ARRAYS,
                        FAIL_ON_EMPTY_BEANS,
                        FAIL_ON_UNKNOWN_PROPERTIES)
                .featuresToEnable(WRITE_DATES_AS_TIMESTAMPS, ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        return builder;
    }

    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
        return application.sources(WebSiteApplication.class);
    }
}
