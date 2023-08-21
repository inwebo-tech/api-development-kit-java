package com.inwebo.api.sample.resources;

import com.inwebo.api.sample.config.InWeboProperties;
import com.inwebo.api.sample.entities.Group;
import com.inwebo.console.ConsoleAdmin;
import com.inwebo.console.LoginsQueryResult;
import com.inwebo.console.ServiceGroupsQueryResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

import static com.inwebo.api.sample.entities.Role.USER;
import static java.lang.Integer.MAX_VALUE;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GroupsManagementControllerTest {

    @Mock
    private ConsoleAdmin admin;

    @Mock
    private InWeboProperties properties;

    @InjectMocks
    private GroupsManagementController controller;

    @Test
    public void testGroupsList() throws Exception {
        // Given
        final Model model = mock(Model.class);
        final int serviceId = 1309;
        long groupId = 2L;

        final ServiceGroupsQueryResult groupsQuery = new ServiceGroupsQueryResult();
        groupsQuery.getId().add(0L);
        groupsQuery.getId().add(groupId);

        groupsQuery.getName().add("");
        groupsQuery.getName().add("testGoup");

        groupsQuery.getServicepolicy().add(0L);
        groupsQuery.getServicepolicy().add(2L);

        final LoginsQueryResult soapResult = new LoginsQueryResult();
        soapResult.getId().add(0L);
        soapResult.getId().add(5L);

        soapResult.getLogin().add("login1");
        soapResult.getLogin().add("login2");

        soapResult.getFirstname().add("firstName1");
        soapResult.getFirstname().add("firstName2");

        soapResult.getName().add("name1");
        soapResult.getName().add("name2");

        soapResult.getRole().add(0L);
        soapResult.getRole().add(2L);

        soapResult.getStatus().add(0L);
        soapResult.getStatus().add(0L);

        soapResult.getMail().add("");
        soapResult.getMail().add("email@email.com");

        soapResult.getPhone().add("");
        soapResult.getPhone().add("+33510458656");

        // When
        when(properties.getServiceId()).thenReturn(serviceId);
        when(admin.serviceGroupsQuery(0, properties.getServiceId(), 0, MAX_VALUE)).thenReturn(groupsQuery);
        when(admin.loginsQueryByGroup(0, groupId, 0, MAX_VALUE, 1)).thenReturn(soapResult);
        when(admin.loginsQuery(0, properties.getServiceId(), 0, MAX_VALUE, 1)).thenReturn(soapResult);

        final String result = controller.groupsList(model);

        // Then
        verify(model).addAttribute(eq("selections"), anyListOf(Group.class));
        assertThat(result).isEqualToIgnoringCase("groupsTest");
    }

    @Test
    public void testAddUser() throws Exception {
        // Given
        final HttpServletRequest requests = mock(HttpServletRequest.class);
        final long groupId = 2L;

        // When
        when(requests.getParameterValues("ids[]")).thenReturn(new String[]{"1", "3"});
        final String result = controller.addUser(groupId, requests);

        // Then
        verify(admin, times(2)).groupAccountCreate(eq(0L), eq(groupId), anyLong(), eq(USER.getRole()));
        assertThat(result).isEqualToIgnoringCase("redirect:/groups");
    }

    @Test
    public void testAddUser_no_http_request_param() throws Exception {
        // Given
        final HttpServletRequest requests = mock(HttpServletRequest.class);
        final long groupId = 2L;

        // When
        when(requests.getParameterValues("ids[]")).thenReturn(null);
        final String result = controller.addUser(groupId, requests);

        // Then
        verifyNoMoreInteractions(admin);
        assertThat(result).isEqualToIgnoringCase("redirect:/groups");
    }


    @Test
    public void testDeleteUser() throws Exception {
        // Given
        final HttpServletRequest requests = mock(HttpServletRequest.class);
        final long groupId = 2L;

        // When
        when(requests.getParameterValues("ids[]")).thenReturn(new String[]{"1", "3"});
        final String result = controller.addDelete(groupId, requests);

        // Then
        verify(admin, times(2)).groupAccountDelete(eq(0L), eq(groupId), anyLong());
        assertThat(result).isEqualToIgnoringCase("redirect:/groups");
    }

    @Test
    public void testDeleteUser_no_http_request_param() throws Exception {
        // Given
        final HttpServletRequest requests = mock(HttpServletRequest.class);
        final long groupId = 2L;

        // When
        when(requests.getParameterValues("ids[]")).thenReturn(null);
        final String result = controller.addDelete(groupId, requests);

        // Then
        verifyNoMoreInteractions(admin);
        assertThat(result).isEqualToIgnoringCase("redirect:/groups");
    }
}