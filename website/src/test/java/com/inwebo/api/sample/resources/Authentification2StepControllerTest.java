package com.inwebo.api.sample.resources;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.inwebo.api.sample.config.InWeboProperties;
import com.inwebo.api.sample.entities.Authentification2StepForm;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.context.request.async.DeferredResult;

import javax.ws.rs.client.WebTarget;

import static com.inwebo.api.sample.entities.Authentification2StepForm.GOOD_PASSWORD;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class Authentification2StepControllerTest {

    @Mock(answer = RETURNS_DEEP_STUBS)
    private WebTarget restAuthentication;

    @Mock
    private InWeboProperties properties;

    @Mock
    private ListeningExecutorService service;

    @InjectMocks
    private Authentification2StepController controller;


    @Test
    public void testSubmitForm_Bad_Password() throws Exception {
        // Given
        final Authentification2StepForm form = new Authentification2StepForm("login", "toto");

        // When
        final DeferredResult<String> result = controller.submit(form);

        // When
        verifyZeroInteractions(service);
        verifyZeroInteractions(restAuthentication);
        verifyZeroInteractions(properties);
        assertThat(result).isNotNull();
        assertThat(result.getResult()).isEqualTo("signin/unauthorized");
    }

    @Test
    public void testSubmitForm_good_password_but_NOK_IN_WEBO_API() throws Exception {
        // Given
        final Authentification2StepForm form = new Authentification2StepForm("login", GOOD_PASSWORD);
        final JSONObject jsonObject = new JSONObject("{\"err\":\"NOK:toto\"}");
        final int serviceId = 1309;

        // When
        when(properties.getServiceId()).thenReturn(serviceId);
        when(restAuthentication.path(eq("FS"))
                .queryParam(eq("action"), eq("pushAuthenticate"))
                .queryParam(eq("serviceId"), eq(serviceId))
                .queryParam(eq("userId"), eq(form.getLogin()))
                .queryParam(eq("format"), eq("json"))
                .request()
                .get(JSONObject.class)).thenReturn(jsonObject);

        final DeferredResult<String> result = controller.submit(form);

        // When
        verifyZeroInteractions(service);
        verify(restAuthentication).path(eq("FS"));
        assertThat(result).isNotNull();
        assertThat(result.getResult()).isEqualTo("signin/unauthorized");
    }
//
//    @Test
//    public void testSubmitForm_good_password_but_OK_IN_WEBO_API() throws Exception {
//        // Given
//        final String login = "login";
//        final int serviceId = 1309;
//        final String sessionId = "f958a18ff21ee48eb58b236601fd7475";
//
//        final Authentification2StepForm form = new Authentification2StepForm(login, GOOD_PASSWORD);
//        final JSONObject authPush = new JSONObject("{\"err\":\"OK\",\"name\":\"iPhone 6s\",\"alias\":\"" + sessionId + "\",\"sessionId\":\"bda0db4c40e5391efd3219342030e071\",\"type\":\"ma\",\"version\":\"4.0.14\",\"platform\":\"iphone\",\"timestamp\":\"2016-03-04 15:48:43\"}");
//        final JSONObject checkPush = new JSONObject("{\"err\":\"OK\"}");
//
//        // When
//        when(properties.getServiceId()).thenReturn(serviceId);
//        when(restAuthentication.path(eq("FS"))
//                .queryParam(eq("action"), eq("pushAuthenticate"))
//                .queryParam(eq("serviceId"), eq(serviceId))
//                .queryParam(eq("userId"), eq(form.getLogin()))
//                .queryParam(eq("format"), eq("json"))
//                .request()
//                .get(JSONObject.class)).thenReturn(authPush);
//        when(restAuthentication.path(eq("FS"))
//                .queryParam(eq("action"), eq("checkPushResult"))
//                .queryParam(eq("serviceId"), eq(serviceId))
//                .queryParam(eq("userId"), eq(login))
//                .queryParam(eq("sessionId"), eq(sessionId))
//                .queryParam(eq("format"), eq("json"))
//                .request()
//                .get(JSONObject.class)).thenReturn(checkPush);
//
//        final DeferredResult<String> result = controller.submit(form);
//
//        // When
//        verifyZeroInteractions(service);
//        verify(restAuthentication).path(eq("FS"));
//        assertThat(result).isNotNull();
//        assertThat(result.getResult()).isEqualTo("signin/success");
//    }
}