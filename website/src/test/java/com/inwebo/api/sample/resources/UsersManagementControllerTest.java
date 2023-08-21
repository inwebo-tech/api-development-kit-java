package com.inwebo.api.sample.resources;

import com.inwebo.api.sample.config.InWeboProperties;
import com.inwebo.api.sample.entities.Role;
import com.inwebo.api.sample.entities.Status;
import com.inwebo.api.sample.entities.User;
import com.inwebo.console.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.support.SessionStatus;

import static com.inwebo.api.sample.entities.CodeType.ACTIVATION_LINK_VALID_FOR_3_WEEKS;
import static com.inwebo.api.sample.entities.CodeType.CODE_INATIVE_VALID_FOR_3_WEEEKS;
import static java.lang.Integer.MAX_VALUE;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UsersManagementControllerTest {

    @Mock
    private ConsoleAdmin admin;

    @Mock
    private InWeboProperties properties;

    @InjectMocks
    private UsersManagementController controller;

    @Test
    public void testUsersList() throws Exception {
        // Given
        final Model model = mock(Model.class);
        final int serviceId = 1309;
        final long userId = 12L;

        final LoginGetGroupsResult groupsResult = new LoginGetGroupsResult();
        groupsResult.getGroupid().add(1L);
        groupsResult.getName().add("groupName");
        groupsResult.getServicepolicy().add(2L);

        final LoginsQueryResult soapResult = new LoginsQueryResult();
        soapResult.getId().add(0L);
        soapResult.getId().add(userId);

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
        when(admin.loginsQuery(0, properties.getServiceId(), 0, MAX_VALUE, 1)).thenReturn(soapResult);
        when(admin.loginGetGroups(0, userId, 0, MAX_VALUE)).thenReturn(groupsResult);
        final String result = controller.usersList(model);

        // Then
        verify(admin).loginsQuery(eq(0L), eq(Long.valueOf(serviceId)), eq(0L), eq(Long.valueOf(MAX_VALUE)), eq(1L));
        verify(admin).loginGetGroups(eq(0L), eq(Long.valueOf(userId)), eq(0L), eq(Long.valueOf(MAX_VALUE)));
        verify(model).addAttribute(eq("selections"), anyListOf(User.class));
        assertThat(result).isEqualToIgnoringCase("usersTest");
    }

    @Test
    public void testInitUpdateUserForm() throws Exception {
        //Given
        final int userId = 2;
        final Model model = mock(Model.class);

        // When
        when(admin.loginQuery(0, userId)).thenReturn(new LoginQueryResult());
        final String result = controller.initUpdateUserForm(userId, model);

        // Then
        verify(admin).loginQuery(eq(0L), eq(Long.valueOf(userId)));
        assertThat(result).isEqualToIgnoringCase("createOrUpdateUserForm");
    }

    @Test
    public void testProcessUpdateUserFormWhenError() throws Exception {
        //Given
        final Long userId = 2L;
        final User user = new User(userId, "login", "firstName", "lastName", "email", "phone", Role.ADMIN, Status.ACTIF);
        final BindingResult result = mock(BindingResult.class);
        final SessionStatus status = mock(SessionStatus.class);

        // When
        when(result.hasErrors()).thenReturn(true);
        final String s = controller.processUpdateUserForm(userId.intValue(), user, result, status);

        // Then
        verifyNoMoreInteractions(admin);
        assertThat(s).isEqualToIgnoringCase("createOrUpdateUserForm");
    }

    @Test
    public void testProcessUpdateUserForm() throws Exception {
        //Given
        final Long userId = 2L;
        final int serviceId = 1309;
        final User user = new User(userId, "login", "firstName", "lastName", "email", "phone", Role.ADMIN, Status.ACTIF);
        final BindingResult result = mock(BindingResult.class);
        final SessionStatus status = mock(SessionStatus.class);

        // When
        when(result.hasErrors()).thenReturn(false);
        when(properties.getServiceId()).thenReturn(serviceId);
        when(admin.loginUpdate(
                0,
                serviceId,
                user.getId(),
                user.getLogin(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getStatus().getStatus(),
                user.getRole().getRole(),
                ""
        )).thenReturn("OK");
        final String s = controller.processUpdateUserForm(userId.intValue(), user, result, status);

        // Then
        verify(status).setComplete();
        assertThat(s).isEqualToIgnoringCase("redirect:/users/{userId}");
    }

    @Test
    public void testActivate() throws Exception {
        //Given
        final long userId = 2L;
        final int serviceId = 1309;

        //When
        when(properties.getServiceId()).thenReturn(serviceId);
        final String result = controller.activate(userId);

        // When
        verify(admin).loginActivateCode(eq(0L), eq(Long.valueOf(serviceId)), eq(userId));
        assertThat(result).isEqualToIgnoringCase("redirect:/users");


    }


    @Test
    public void testProcesCreationFormWhenError() throws Exception {
        //Given
        final long userId = 2L;
        final User user = new User("login", "firstName", "lastName", "email", "phone", Role.ADMIN, Status.ACTIF);
        user.setBookmarks(true);
        user.setCodeType(ACTIVATION_LINK_VALID_FOR_3_WEEKS);
        final BindingResult result = mock(BindingResult.class);
        final SessionStatus status = mock(SessionStatus.class);

        // When
        when(result.hasErrors()).thenReturn(true);
        final String s = controller.processCreationForm(user, result, status);

        // Then
        verifyNoMoreInteractions(admin);
        assertThat(s).isEqualToIgnoringCase("createOrUpdateUserForm");
    }

    @Test
    public void testProcesCreationFormWith_ACTIVATION_LINK_VALID_FOR_3_WEEKS_AND_CREATE_OK() throws Exception {
        //Given
        final int serviceId = 1309;
        final long userId = 123;
        final User user = new User("login", "firstName", "lastName", "email", "phone", Role.ADMIN, Status.ACTIF);
        user.setBookmarks(true);
        user.setCodeType(ACTIVATION_LINK_VALID_FOR_3_WEEKS);
        final BindingResult result = mock(BindingResult.class);
        final SessionStatus status = mock(SessionStatus.class);

        final LoginCreateResult result1 = new LoginCreateResult();

        result1.setId(userId);
        result1.setErr("ok");
        result1.setCode("091114390");

        // When
        when(result.hasErrors()).thenReturn(false);
        when(properties.getServiceId()).thenReturn(serviceId);
        when(admin.loginCreate(
                0,
                serviceId,
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
        )).thenReturn(result1);
        final String s = controller.processCreationForm(user, result, status);

        // Then
        verify(admin).loginSendByMail(eq(0L), eq(Long.valueOf(serviceId)), eq(Long.valueOf(userId)));
        assertThat(s).isEqualToIgnoringCase("redirect:/users/123?code=091114390");
    }

    @Test
    public void testProcesCreationFormWith_CODE_INATIVE_VALID_FOR_3_WEEEKS_AND_CREATE_OK() throws Exception {
        //Given
        final int serviceId = 1309;
        final long userId = 123;
        final User user = new User("login", "firstName", "lastName", "email", "phone", Role.ADMIN, Status.ACTIF);
        user.setBookmarks(true);
        user.setCodeType(CODE_INATIVE_VALID_FOR_3_WEEEKS);
        final BindingResult result = mock(BindingResult.class);
        final SessionStatus status = mock(SessionStatus.class);

        final LoginCreateResult result1 = new LoginCreateResult();

        result1.setId(userId);
        result1.setErr("ok");
        result1.setCode("091114390");

        // When
        when(result.hasErrors()).thenReturn(false);
        when(properties.getServiceId()).thenReturn(serviceId);
        when(admin.loginCreate(
                0,
                serviceId,
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
        )).thenReturn(result1);
        final String s = controller.processCreationForm(user, result, status);

        // Then
        verify(admin, times(0)).loginSendByMail(eq(0L), eq(Long.valueOf(serviceId)), eq(Long.valueOf(userId)));
        assertThat(s).isEqualToIgnoringCase("redirect:/users/123?code=091114390&activeCode=true");
    }

    @Test(expected = Exception.class)
    public void testProcesCreationFormWithCREATE_KO() throws Exception {
        //Given
        final int serviceId = 1309;
        final User user = new User("login", "firstName", "lastName", "email", "phone", Role.ADMIN, Status.ACTIF);
        user.setBookmarks(true);
        user.setCodeType(CODE_INATIVE_VALID_FOR_3_WEEEKS);
        final BindingResult result = mock(BindingResult.class);
        final SessionStatus status = mock(SessionStatus.class);

        final LoginCreateResult result1 = new LoginCreateResult();
        result1.setErr("ko");

        // When
        when(result.hasErrors()).thenReturn(false);
        when(properties.getServiceId()).thenReturn(serviceId);
        when(admin.loginCreate(
                0,
                serviceId,
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
        )).thenReturn(result1);
        controller.processCreationForm(user, result, status);

        // Then
    }

    @Test
    public void testDelete() throws Exception {
        //Given
        final int serviceId = 1309;
        final long userId = 123;

        // When
        when(properties.getServiceId()).thenReturn(serviceId);
        final String result = controller.delete(userId);

        // Then
        verify(admin).loginDelete(eq(0L), eq(Long.valueOf(serviceId)), eq(userId));
        assertThat(result).isEqualToIgnoringCase("redirect:/users");

    }

    @Test
    public void testShowUser() throws Exception {
        //Given
        final int userId = 2;
        final Model model = mock(Model.class);

        // When
        when(admin.loginQuery(0, userId)).thenReturn(new LoginQueryResult());
        final String result = controller.showUser(userId, model, "", false);

        // Then
        verify(admin).loginQuery(eq(0L), eq(Long.valueOf(userId)));
        assertThat(result).isEqualToIgnoringCase("userTest");
    }

    @Test
    public void testInitCreationForm() throws Exception {
        //Given
        final Model model = mock(Model.class);

        // When
        final String result = controller.initCreationForm(model);

        // Then
        verifyNoMoreInteractions(admin);
        assertThat(result).isEqualToIgnoringCase("createOrUpdateUserForm");
    }
}