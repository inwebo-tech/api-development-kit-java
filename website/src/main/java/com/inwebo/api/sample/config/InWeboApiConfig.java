package com.inwebo.api.sample.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.inwebo.console.ConsoleAdmin;
import com.inwebo.console.ConsoleAdminService;
import com.inwebo.service.Authentication;
import com.inwebo.service.AuthenticationService;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.ws.BindingProvider;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.concurrent.Executors;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Configuration
@EnableConfigurationProperties(InWeboProperties.class)
public class InWeboApiConfig {

    @Autowired
    private InWeboProperties properties;

    @Autowired
    private Jackson2ObjectMapperBuilder jacksonBuilder;

    @Bean
    @Order(HIGHEST_PRECEDENCE)
    protected SSLContext sslContext() throws Exception {
        //        LOG SOAP:
//        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
//        System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
//        System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
//        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");
        final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        final KeyStore keyStore = KeyStore.getInstance("PKCS12");
        InputStream keyInput = null;
        try {
            final char[] certPassword = properties.getCertificate().getPassword().toCharArray();
            keyInput = getCertificateFile();
            keyStore.load(keyInput, certPassword);
            keyManagerFactory.init(keyStore, certPassword);
            final SSLContext context = SSLContext.getInstance("TLS");
            context.init(keyManagerFactory.getKeyManagers(), null, new SecureRandom());
            SSLContext.setDefault(context);
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
            return context;
        } finally {
            if (keyInput != null) {
                try {
                    keyInput.close();
                } catch (final Exception e) {

                }
            }
        }
    }

    private InputStream getCertificateFile() throws Exception {
        final String certificatePath = properties.getCertificate().getPath();
        if (Files.exists(Paths.get(certificatePath))) {
            return Files.newInputStream(Paths.get(certificatePath));
        } else {
            return new ClassPathResource(certificatePath).getInputStream();
        }
    }

    @Bean
    public ListeningExecutorService service() {
        return MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));
    }

    @Bean
    protected ConsoleAdmin consoleAdmin() {
        final ConsoleAdminService service = new ConsoleAdminService();
        final ConsoleAdmin consoleAdmin = service.getConsoleAdmin();
        ((BindingProvider)consoleAdmin).getRequestContext().put("set-jaxb-validation-event-handler", "false");
        return consoleAdmin;
    }

    @Bean
    protected Authentication authentication() {
        final AuthenticationService as = new AuthenticationService();
        return as.getAuthentication();
    }

    @Bean
    protected WebTarget target() throws Exception {
        final Client client = ClientBuilder.newBuilder()
                                           .register(new ObjectMapperResolver())
                                           .register(JacksonFeature.class)
                                           .register(new LoggingFeature())
                                           .sslContext(sslContext())
                                           .build();
        return client.target(properties.getRestBaseUrl());
    }

    @Provider
    public class ObjectMapperResolver implements ContextResolver<ObjectMapper> {

        private final ObjectMapper objectMapper;

        public ObjectMapperResolver() {
            objectMapper = jacksonBuilder.build();
        }

        @Override
        public ObjectMapper getContext(Class<?> type) {
            return objectMapper;
        }
    }
}