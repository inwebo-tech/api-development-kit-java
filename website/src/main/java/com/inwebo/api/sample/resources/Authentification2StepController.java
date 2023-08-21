package com.inwebo.api.sample.resources;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.inwebo.api.sample.config.InWeboProperties;
import com.inwebo.api.sample.entities.Authentification2StepForm;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.async.DeferredResult;

import javax.ws.rs.client.WebTarget;
import java.util.concurrent.Callable;

import static com.inwebo.api.sample.entities.Authentification2StepForm.GOOD_PASSWORD;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class Authentification2StepController {

    private final static Logger LOGGER = LoggerFactory.getLogger(Authentification2StepController.class);

    @Autowired
    private WebTarget restAuthentication;

    @Autowired
    private InWeboProperties properties;

    @Autowired
    private ListeningExecutorService service;


    @RequestMapping(value = "/auth2Step", method = GET)
    public String home(final Model model) {
        model.addAttribute("authentification2StepForm", new Authentification2StepForm());
        return "signin/authentification2StepSignin";
    }

    @RequestMapping(value = "/auth2Step", method = POST)
    public DeferredResult<String> submit(@ModelAttribute final Authentification2StepForm form) throws Exception {
        final DeferredResult<String> result = new DeferredResult<>(30000L, "signin/timeout");
        if (GOOD_PASSWORD.equals(form.getPassword())) {
            final JSONObject response = authentificationPush(form, result);
            if (response != null) {
                final ListenableFuture<String> future = checkPush(form, response);
                Futures.addCallback(future, new FutureCallback<String>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        LOGGER.error("Check Push Authentification Error", throwable);
                        result.setResult("signin/error");
                    }

                    @Override
                    public void onSuccess(final String res) {
                        result.setResult(res);
                    }
                });
            }
        } else {
            result.setResult("signin/unauthorized");
        }
        return result;
    }


    private ListenableFuture<String> checkPush(final Authentification2StepForm form, final JSONObject response) {
        final ListenableFuture<String> future = service.submit(new Callable<String>() {
            public String call() throws Exception {
                JSONObject result = checkPushCall(form.getLogin(), response.getString("sessionId"));
                while (result != null && "NOK:WAITING".equals(result.get("err"))) {
                    result = checkPushCall(form.getLogin(), response.getString("sessionId"));
                }
                if (result != null && "OK".equals(result.get("err"))) {
                    LOGGER.info("Success Push Authentification");
                    return "signin/success";
                } else {
                    LOGGER.error("Unauthorized Push Authentification with response {}", result.toString());
                    return "signin/unauthorized";
                }
            }
        });
        return future;
    }

    private JSONObject authentificationPush(final Authentification2StepForm form, final DeferredResult<String> result) throws JSONException {
        final JSONObject response = restAuthentication.path("FS")
                .queryParam("action", "pushAuthenticate")
                .queryParam("serviceId", properties.getServiceId())
                .queryParam("userId", form.getLogin())
                .queryParam("format", "json")
                .request()
                .get(JSONObject.class);
        if (response.has("err") && response.getString("err").contains("NOK")) {
            result.setResult("signin/unauthorized");
            return null;
        } else {
            return response;
        }
    }


    private JSONObject checkPushCall(final String login, final String sessionId) {
        return restAuthentication.path("FS")
                .queryParam("action", "checkPushResult")
                .queryParam("serviceId", properties.getServiceId())
                .queryParam("userId", login)
                .queryParam("sessionId", sessionId)
                .queryParam("format", "json")
                .request()
                .get(JSONObject.class);
    }
}
