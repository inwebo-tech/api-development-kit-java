package com.inwebo.api.sample;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpStatus.OK;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebSiteApplication.class)
@WebIntegrationTest(randomPort = true)
@DirtiesContext
public class WebSiteApplicationTests {

    @Value("${local.server.port}")
    private int port;

    @Test
    public void should_access_index() throws Exception {
        final ResponseEntity<String> entity = new TestRestTemplate()
                .getForEntity("http://localhost:" + this.port, String.class);
        assertEquals(OK, entity.getStatusCode());
    }

    @Test
    public void should_access_authentification_soap() throws Exception {
        final ResponseEntity<String> entity = new TestRestTemplate()
                .getForEntity("http://localhost:" + this.port + "/soap", String.class);
        assertEquals(OK, entity.getStatusCode());
    }

    @Test
    public void should_access_authentification_rest() throws Exception {
        ResponseEntity<String> entity = new TestRestTemplate()
                .getForEntity("http://localhost:" + this.port + "/rest", String.class);
        assertEquals(OK, entity.getStatusCode());
    }

    @Test
    public void should_access_authentification_push() throws Exception {
        final ResponseEntity<String> entity = new TestRestTemplate()
                .getForEntity("http://localhost:" + this.port + "/push", String.class);
        assertEquals(OK, entity.getStatusCode());
    }

    @Test
    public void should_access_authentification_checkPush() throws Exception {
        final ResponseEntity<String> entity = new TestRestTemplate()
                .getForEntity("http://localhost:" + this.port + "/checkPush?login=toto@toto.com&sessionId=12", String.class);
        assertEquals(OK, entity.getStatusCode());
    }

    @Test
    public void should_access_authentification_2Step() throws Exception {
        ResponseEntity<String> entity = new TestRestTemplate()
                .getForEntity("http://localhost:" + this.port + "/auth2Step", String.class);
        assertEquals(OK, entity.getStatusCode());
    }
}
