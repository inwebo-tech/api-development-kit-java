package com.inwebo.api.sample.resources;

import com.inwebo.api.sample.config.InWeboProperties;
import com.inwebo.api.sample.entities.Group;
import com.inwebo.api.sample.entities.User;
import com.inwebo.api.sample.support.ConvertUtils;
import com.inwebo.console.ConsoleAdmin;
import com.inwebo.console.LoginsQueryResult;
import com.inwebo.console.ServiceGroupsQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.inwebo.api.sample.entities.Role.USER;
import static java.lang.Integer.MAX_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class GroupsManagementController {

    @Autowired
    private ConsoleAdmin consoleAdmin;

    @Autowired
    private InWeboProperties properties;

    @RequestMapping(value = "/groups", method = GET)
    public String groupsList(final Model model) {
        final List<Group> groups = new ArrayList<>();

        // Get Groups
        final ServiceGroupsQueryResult groupsQuery = consoleAdmin.serviceGroupsQuery(0, properties.getServiceId(), 0, MAX_VALUE);
        for (int cpt = 0; cpt < groupsQuery.getId().size(); cpt++) {
            final Group group = ConvertUtils.getGroupFromList(groupsQuery, cpt);
            if (group == null) continue;

            // get logins by groupId
            final LoginsQueryResult result = consoleAdmin.loginsQueryByGroup(0, group.getId(), 0, MAX_VALUE, 1);
            for (int cpt2 = 0; cpt2 < result.getId().size(); cpt2++) {
                final User user = ConvertUtils.getUserFromList(result, cpt2);
                if (user == null) continue;
                group.getUsers().add(user);
            }
            groups.add(group);
        }


        final List<User> allUsers = new ArrayList<>();
        final LoginsQueryResult loginsQueryResult = consoleAdmin.loginsQuery(0, properties.getServiceId(), 0, MAX_VALUE, 1);
        for (int cpt = 0; cpt < loginsQueryResult.getId().size(); cpt++) {
            final User user = ConvertUtils.getUserFromList(loginsQueryResult, cpt);
            if (user == null) continue;
            allUsers.add(user);
        }

        model.addAttribute("selections", groups);
        model.addAttribute("allUsers", allUsers);
        return "groupsTest";
    }

    @RequestMapping(value = "/groups/{id}/addUser", method = POST)
    public String addUser(@PathVariable("id") final long groupId,
                          final HttpServletRequest requests) {
        final String[] loginsId = requests.getParameterValues("ids[]");
        if (loginsId != null) {
            for (final String id : loginsId) {
                consoleAdmin.groupAccountCreate(0L, groupId, Long.valueOf(id), USER.getRole());
            }
        }
        return "redirect:/groups";
    }

    @RequestMapping(value = "/groups/{id}/deleteUser", method = POST)
    public String addDelete(@PathVariable("id") final long groupId,
                            final HttpServletRequest requests) {
        final String[] loginsId = requests.getParameterValues("ids[]");
        if (loginsId != null) {
            for (final String id : loginsId) {
                consoleAdmin.groupAccountDelete(0L, groupId, Long.valueOf(id));
            }
        }
        return "redirect:/groups";
    }
}
