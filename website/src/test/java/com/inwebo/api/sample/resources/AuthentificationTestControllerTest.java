package com.inwebo.api.sample.resources;

import com.inwebo.api.sample.config.InWeboProperties;
import com.inwebo.api.sample.entities.AuthentificationTestForm;
import com.inwebo.service.Authentication;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;

import javax.ws.rs.client.WebTarget;

import static com.inwebo.api.sample.entities.CallType.*;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class AuthentificationTestControllerTest {

    @InjectMocks
    private AuthentificationTestController controller;

    @Mock
    private Authentication soapAuthentication;

    @Mock(answer = RETURNS_DEEP_STUBS)
    private WebTarget restAuthentication;

    @Mock
    private InWeboProperties properties;

    @Test
    public void testSaopForm() throws Exception {
        // Given
        final Model model = mock(Model.class);

        // When
        final String result = controller.saopForm(model);

        // Then
        assertThat(result).isEqualToIgnoringCase("authentificationTest");
        verify(model).addAttribute(eq("authentificationForm"), any(AuthentificationTestForm.class));
    }

    @Test
    public void testRestForm() throws Exception {
        // Given
        final Model model = mock(Model.class);

        // When
        final String result = controller.restForm(model);

        // Then
        assertThat(result).isEqualToIgnoringCase("authentificationTest");
        verify(model).addAttribute(eq("authentificationForm"), any(AuthentificationTestForm.class));
    }

    @Test
    public void testPushForm() throws Exception {
        // Given
        final Model model = mock(Model.class);

        // When
        final String result = controller.pushForm(model);

        // Then
        assertThat(result).isEqualToIgnoringCase("push");
        verify(model).addAttribute(eq("authentificationForm"), any(AuthentificationTestForm.class));
    }

    @Test
    public void testCheckPush() throws Exception {
        // Given
        final String login = "toto";
        final String sessionId = "azerty1234";
        final int serviceId = 1309;
        final JSONObject jsonObject = new JSONObject();

        // When
        when(properties.getServiceId()).thenReturn(serviceId);
        when(restAuthentication.path(eq("FS"))
                .queryParam(eq("action"), eq("checkPushResult"))
                .queryParam(eq("serviceId"), eq(serviceId))
                .queryParam(eq("userId"), eq(login))
                .queryParam(eq("sessionId"), eq(sessionId))
                .queryParam(eq("format"), eq("json"))
                .request()
                .get(JSONObject.class)).thenReturn(jsonObject);

        final JSONObject result = controller.checkPush(login, sessionId);

        // Then
        assertThat(result).isEqualTo(jsonObject);
        verify(restAuthentication).path(eq("FS"));
    }

    @Test
    public void testSubmitSoapAuth() throws Exception {
        // Given
        final Model model = mock(Model.class);
        final AuthentificationTestForm form = new AuthentificationTestForm(SOAP);
        form.setLogin("toto");
        form.setOtp("azerty1234");
        final int serviceId = 1309;
        final String soapResponse = "soapResponse";

        //When
        when(properties.getServiceId()).thenReturn(serviceId);
        when(soapAuthentication.authenticate(form.getLogin(), String.valueOf(properties.getServiceId()), form.getOtp()))
                .thenReturn(soapResponse);

        final String result = controller.submit(form, model);

        //Then
        assertThat(result).isEqualTo("resultPage");
        verify(soapAuthentication).authenticate(form.getLogin(),
                String.valueOf(properties.getServiceId()),
                form.getOtp());
        verify(model).addAttribute(eq("result"), eq(soapResponse));
    }

    @Test
    public void testSubmitRestAuth() throws Exception {
        // Given
        final Model model = mock(Model.class);
        final AuthentificationTestForm form = new AuthentificationTestForm(REST);
        form.setLogin("toto");
        form.setOtp("azerty1234");
        final int serviceId = 1309;
        final String restResponse = "restResponse";

        //When
        when(properties.getServiceId()).thenReturn(serviceId);
        when(restAuthentication.path(eq("FS"))
                .queryParam(eq("action"), eq("authenticateExtended"))
                .queryParam(eq("serviceId"), eq(serviceId))
                .queryParam(eq("userId"), eq(form.getLogin()))
                .queryParam(eq("token"), eq(form.getOtp()))
                .queryParam(eq("format"), eq("json"))
                .request()
                .get(String.class)).thenReturn(restResponse);

        final String result = controller.submit(form, model);

        //Then
        assertThat(result).isEqualTo("resultPage");
        verify(restAuthentication).path(eq("FS"));
        verify(model).addAttribute(eq("result"), eq(restResponse));
    }

    @Test
    public void testSubmitPushAuth() throws Exception {
        // Given
        final Model model = mock(Model.class);
        final AuthentificationTestForm form = new AuthentificationTestForm(PUSH);
        form.setLogin("toto24");
        final int serviceId = 1309;
        final JSONObject jsonObject = new JSONObject("{\"err\":\"OK\",\"name\":\"iPhone 6s\",\"alias\":\"f958a18ff21ee48eb58b236601fd7475\",\"sessionId\":\"bda0db4c40e5391efd3219342030e071\",\"type\":\"ma\",\"version\":\"4.0.14\",\"platform\":\"iphone\",\"timestamp\":\"2016-03-04 15:48:43\"}");
        //When
        when(properties.getServiceId()).thenReturn(serviceId);
        when(restAuthentication.path(eq("FS"))
                .queryParam(eq("action"), eq("pushAuthenticate"))
                .queryParam(eq("serviceId"), eq(serviceId))
                .queryParam(eq("userId"), eq(form.getLogin()))
                .queryParam(eq("format"), eq("json"))
                .request()
                .get(JSONObject.class)).thenReturn(jsonObject);

        final String result = controller.submit(form, model);

        //Then
        assertThat(result).isEqualTo("resultPage");
        verify(restAuthentication).path(eq("FS"));
        verify(model).addAttribute(eq("result"), eq(jsonObject));
        verify(model).addAttribute(eq("login"), eq(form.getLogin()));
        verify(model).addAttribute(eq("sessionId"), eq("bda0db4c40e5391efd3219342030e071"));
    }

    @Test
    public void testSubmitPushAuthWithBadLogin() throws Exception {
        // Given
        final Model model = mock(Model.class);
        final AuthentificationTestForm form = new AuthentificationTestForm(PUSH);
        form.setLogin("toto24");
        final int serviceId = 1309;
        final JSONObject jsonObject = new JSONObject("{\"err\":\"NOK:account unknown\",\"name\":null,\"alias\":null,\"sessionId\":null,\"type\":null,\"version\":null,\"platform\":null,\"timestamp\":\"2016-03-04 15:39:57\"}");

        //When
        when(properties.getServiceId()).thenReturn(serviceId);
        when(restAuthentication.path(eq("FS"))
                .queryParam(eq("action"), eq("pushAuthenticate"))
                .queryParam(eq("serviceId"), eq(serviceId))
                .queryParam(eq("userId"), eq(form.getLogin()))
                .queryParam(eq("format"), eq("json"))
                .request()
                .get(JSONObject.class)).thenReturn(jsonObject);

        final String result = controller.submit(form, model);

        //Then
        assertThat(result).isEqualTo("resultPage");
        verify(restAuthentication).path(eq("FS"));
        verify(model).addAttribute(eq("result"), eq(jsonObject));
    }
}