package com.inwebo.api.sample.support;

import com.inwebo.api.sample.entities.Group;
import com.inwebo.api.sample.entities.Role;
import com.inwebo.api.sample.entities.Status;
import com.inwebo.api.sample.entities.User;
import com.inwebo.console.LoginGetGroupsResult;
import com.inwebo.console.LoginQueryResult;
import com.inwebo.console.LoginsQueryResult;
import com.inwebo.console.ServiceGroupsQueryResult;

public final class ConvertUtils {

    private ConvertUtils() {
    }

    public static User getUserFromList(final LoginsQueryResult result, final int index) {
        long userId = result.getId().get(index);
        if (userId == 0) { // due to WebServer Axis 1
            return null;
        }
        final User user = new User(userId,
                result.getLogin().get(index),
                result.getFirstname().get(index),
                result.getName().get(index),
                result.getMail().get(index),
                result.getPhone().get(index),
                Role.from(result.getRole().get(index)),
                Status.from(result.getStatus().get(index))
        );
        return user;
    }

    public static User getUser(final LoginQueryResult result, final int id) {
        return new User(
                Long.valueOf(id),
                result.getLogin(),
                result.getFirstname(),
                result.getName(),
                result.getMail(),
                result.getPhone(),
                Role.from(result.getRole()),
                Status.from(result.getStatus())
        );
    }

    public static Group getGroupFromList(final LoginGetGroupsResult groupsResult, final int index) {
        if (groupsResult.getGroupid().get(index) == 0) { // due to WebServer Axis 1
            return null;
        }
        final Group group = new Group(groupsResult.getGroupid().get(index),
                groupsResult.getName().get(index),
                groupsResult.getServicepolicy().get(index));
        return group;
    }

    public static Group getGroupFromList(final ServiceGroupsQueryResult groupsResult, final int index) {
        if (groupsResult.getId().get(index) == 0) { // due to WebServer Axis 1
            return null;
        }
        final Group group = new Group(groupsResult.getId().get(index),
                groupsResult.getName().get(index),
                groupsResult.getServicepolicy().get(index));
        return group;
    }
}
