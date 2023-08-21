package com.inwebo.api.sample.resources;

import com.inwebo.api.sample.config.InWeboProperties;
import com.inwebo.api.sample.entities.Group;
import com.inwebo.api.sample.entities.User;
import com.inwebo.api.sample.support.ConvertUtils;
import com.inwebo.console.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

import javax.validation.Valid;
import java.util.ArrayList;

import static com.inwebo.api.sample.entities.CodeType.ACTIVATION_LINK_VALID_FOR_3_WEEKS;
import static com.inwebo.api.sample.entities.CodeType.CODE_INATIVE_VALID_FOR_3_WEEEKS;
import static java.lang.Integer.MAX_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
public class UsersManagementController {

    @Autowired
    private ConsoleAdmin consoleAdmin;

    @Autowired
    private InWeboProperties properties;

    @InitBinder
    public void setAllowedFields(final WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

    @RequestMapping(value = "/users", method = GET)
    public String usersList(final Model model) {
        final ArrayList<User> users = new ArrayList<>();

        // Get all logins / users
        final LoginsQueryResult loginsQueryResult = consoleAdmin.loginsQuery(0, properties.getServiceId(), 0, MAX_VALUE, 1);
        for (int cpt = 0; cpt < loginsQueryResult.getId().size(); cpt++) {
            final User user = ConvertUtils.getUserFromList(loginsQueryResult, cpt);
            if (user == null) continue;

            // Get groups associate to login / user
            final LoginGetGroupsResult groupsResult = consoleAdmin.loginGetGroups(0, user.getId(), 0, MAX_VALUE);
            for (int cpt2 = 0; cpt2 < groupsResult.getGroupid().size(); cpt2++) {
                final Group group = ConvertUtils.getGroupFromList(groupsResult, cpt2);
                if (group == null) continue;
                user.getGroups().add(group);
            }
            users.add(user);
        }
        model.addAttribute("selections", users);
        return "usersTest";
    }

    @RequestMapping(value = "/users/{userId}/edit", method = GET)
    public String initUpdateUserForm(@PathVariable("userId") final int id, final Model model) {
        final User user = getUser(id);
        model.addAttribute(user);
        return "createOrUpdateUserForm";
    }

    @RequestMapping(value = "/users/{userId}/edit", method = PUT)
    public String processUpdateUserForm(@PathVariable("userId") final int id, @Valid final User user, final BindingResult result, final SessionStatus status) {
        if (result.hasErrors()) {
            return "createOrUpdateUserForm";
        } else {
            consoleAdmin.loginUpdate(0,
                    properties.getServiceId(),
                    id,
                    user.getLogin(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getStatus().getStatus(),
                    user.getRole().getRole(),
                    "");
            status.setComplete();
            return "redirect:/users/{userId}";
        }
    }

    @RequestMapping(value = "/users/{userId}", method = GET)
    public String showUser(@PathVariable("userId") final int id,
                           final Model model,
                           @RequestParam(value = "code", required = false, defaultValue = "") final String code,
                           @RequestParam(value = "activeCode", required = false, defaultValue = "false") final boolean activeCode) {
        final User user = getUser(id);
        model.addAttribute(user);
        model.addAttribute("code", code);
        model.addAttribute("activeCode", activeCode);
        return "userTest";
    }

    @RequestMapping(value = "/users/{userId}/delete", method = GET)
    public String delete(@PathVariable(value = "userId") final long id) {
        consoleAdmin.loginDelete(0, properties.getServiceId(), id);
        return "redirect:/users";
    }

    @RequestMapping(value = "/users/{userId}/addDevice", method = GET)
    public String addDevice(@PathVariable(value = "userId") final long id, final Model model) {
        final String addDevice2 = consoleAdmin.loginAddDevice(0, properties.getServiceId(), id, ACTIVATION_LINK_VALID_FOR_3_WEEKS.getCodeType());
        model.addAttribute("addDeviceCodetype2", addDevice2);
//        final String addDevice0 = consoleAdmin.loginAddDevice(0, properties.getServiceId(), id, VALID_IMMEDIATELY_FOR_15_MINS.getCodeType());
//        model.addAttribute("addDeviceCodetype0", addDevice0);
        return "userDevice";
    }


    @RequestMapping(value = "/users/new", method = GET)
    public String initCreationForm(final Model model) {
        final User user = new User();
        model.addAttribute(user);
        return "createOrUpdateUserForm";
    }

    @RequestMapping(value = "/users/new", method = POST)
    public String processCreationForm(@Valid final User user, final BindingResult result, final SessionStatus status) throws Exception {
        if (result.hasErrors()) {
            return "createOrUpdateUserForm";
        } else {
            final LoginCreateResult create = consoleAdmin.loginCreate(
                    0,
                    properties.getServiceId(),
                    user.getLogin(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getStatus().getStatus(),
                    user.getRole().getRole(),
                    user.getBookmarks() ? 1 : 0,
                    user.getCodeType().getCodeType(),
                    "en" /* or fr*/,
                    ""
            );

            if (create.getErr().equalsIgnoreCase("ok")) {
                String sActiveCode = "";
                if (!CODE_INATIVE_VALID_FOR_3_WEEEKS.equals(user.getCodeType())) {
                    consoleAdmin.loginSendByMail(0, properties.getServiceId(), create.getId());
                } else {
                    sActiveCode = "&activeCode=true";
                }
                status.setComplete();
                return "redirect:/users/" + create.getId() + "?code=" + create.getCode() + sActiveCode;
            } else {
                throw new Exception(create.getErr());
            }
        }
    }

    @RequestMapping(value = "/users/{userId}/activate", method = GET)
    public String activate(@PathVariable("userId") final long id) {
        consoleAdmin.loginActivateCode(0, properties.getServiceId(), id);
        return "redirect:/users";
    }

    private User getUser(final int id) {
        final LoginQueryResult result = consoleAdmin.loginQuery(0, id);
        return ConvertUtils.getUser(result, id);
    }
}
