package com.inwebo.api.sample.resources;

import com.inwebo.api.sample.config.InWeboProperties;
import com.inwebo.api.sample.entities.AuthentificationTestForm;
import com.inwebo.service.Authentication;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.ws.rs.client.WebTarget;

import static com.inwebo.api.sample.entities.CallType.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class AuthentificationTestController {

    @Autowired
    private Authentication soapAuthentication;

    @Autowired
    private WebTarget restAuthentication;

    @Autowired
    private InWeboProperties properties;

    @RequestMapping(value = "/soap", method = GET)
    public String saopForm(final Model model) {
        model.addAttribute("authentificationForm", new AuthentificationTestForm(SOAP));
        return "authentificationTest";
    }

    @RequestMapping(value = "/rest", method = GET)
    public String restForm(final Model model) {
        model.addAttribute("authentificationForm", new AuthentificationTestForm(REST));
        return "authentificationTest";
    }

    @RequestMapping(value = "/push", method = GET)
    public String pushForm(final Model model) {
        model.addAttribute("authentificationForm", new AuthentificationTestForm(PUSH));
        return "push";
    }

    @RequestMapping(value = "/checkPush", method = GET)
    @ResponseBody
    public JSONObject checkPush(@RequestParam(value = "login", required = true) final String login,
                                @RequestParam(value = "sessionId", required = true) final String sessionId) {
        return restAuthentication.path("FS")
                .queryParam("action", "checkPushResult")
                .queryParam("serviceId", properties.getServiceId())
                .queryParam("userId", login)
                .queryParam("sessionId", sessionId)
                .queryParam("format", "json")
                .request()
                .get(JSONObject.class);
    }

    @RequestMapping(value = "/authentification", method = POST)
    public String submit(@ModelAttribute final AuthentificationTestForm form, final Model model) throws Exception {
        if (SOAP.equals(form.getCallType())) {
            model.addAttribute("result", soapCall(form));
        } else if (REST.equals(form.getCallType())) {
            model.addAttribute("result", restCall(form));
        } else if (PUSH.equals(form.getCallType())) {
            final JSONObject jsonObject = pushCall(form);
            model.addAttribute("result", jsonObject);
            if (jsonObject.has("sessionId") && !"null".equals(jsonObject.getString("sessionId"))) {
                model.addAttribute("login", form.getLogin());
                model.addAttribute("sessionId", jsonObject.getString("sessionId"));
            }
        }
        return "resultPage";
    }

    private JSONObject pushCall(final AuthentificationTestForm form) {
        return restAuthentication.path("FS")
                .queryParam("action", "pushAuthenticate")
                .queryParam("serviceId", properties.getServiceId())
                .queryParam("userId", form.getLogin())
                .queryParam("format", "json")
                .request()
                .get(JSONObject.class);
    }

    private String restCall(final AuthentificationTestForm form) {
        return restAuthentication.path("FS")
                .queryParam("action", "authenticateExtended")
                .queryParam("serviceId", properties.getServiceId())
                .queryParam("userId", form.getLogin())
                .queryParam("token", form.getOtp())
                .queryParam("format", "json")
                .request()
                .get(String.class);
    }

    private String soapCall(final AuthentificationTestForm form) {
        return soapAuthentication.authenticate(form.getLogin(),
                String.valueOf(properties.getServiceId()),
                form.getOtp());
    }
}
