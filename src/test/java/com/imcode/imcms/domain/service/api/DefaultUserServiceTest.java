package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.PhoneDTO;
import com.imcode.imcms.domain.dto.RoleDTO;
import com.imcode.imcms.domain.dto.UserFormData;
import com.imcode.imcms.domain.service.PhoneService;
import com.imcode.imcms.domain.service.RoleService;
import com.imcode.imcms.domain.service.UserAdminRolesService;
import com.imcode.imcms.domain.service.UserRolesService;
import com.imcode.imcms.model.Phone;
import com.imcode.imcms.model.PhoneTypes;
import com.imcode.imcms.model.Role;
import com.imcode.imcms.persistence.entity.User;
import com.imcode.imcms.persistence.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static imcode.server.ImcmsConstants.ENG_CODE;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultUserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleService roleService;
    @Mock
    private PhoneService phoneService;
    @Mock
    private UserRolesService userRolesService;
    @Mock
    private UserAdminRolesService userAdminRolesService;

    @InjectMocks
    private DefaultUserService userService;

    @Test
    void collectPhoneNumbers_When_EmptyPhonesAndTypes_Expect_EmptyCollection() {
        final UserFormData userData = new UserFormData();
        userData.setUserPhoneNumber(new String[0]);
        userData.setUserPhoneNumberType(new Integer[0]);

        final List<Phone> phones = userService.collectPhoneNumbers(userData, mock(User.class));

        Assertions.assertTrue(phones.isEmpty());
    }

    @Test
    void collectPhoneNumbers_When_NullPhonesAndTypes_Expect_EmptyCollection() {
        final UserFormData userData = new UserFormData();
        userData.setUserPhoneNumber(null);
        userData.setUserPhoneNumberType(null);

        final List<Phone> phones = userService.collectPhoneNumbers(userData, mock(User.class));

        Assertions.assertTrue(phones.isEmpty());
    }

    @Test
    void collectPhoneNumbers_When_SomeValidData_Expect_CorrectPhonesInResult() {
        final String[] userPhoneNumbers = {
                "123",
                "456",
                "789",
        };
        final Integer[] userPhoneNumberTypes = {
                PhoneTypes.OTHER.getId(),
                PhoneTypes.FAX.getId(),
                PhoneTypes.MOBILE.getId(),
        };

        final User user = mock(User.class);
        final UserFormData userData = new UserFormData();
        userData.setUserPhoneNumber(userPhoneNumbers);
        userData.setUserPhoneNumberType(userPhoneNumberTypes);

        final List<Phone> phones = userService.collectPhoneNumbers(userData, user);

        final String[] phoneNumbers = phones.stream()
                .map(Phone::getNumber)
                .toArray(String[]::new);

        final Integer[] phoneTypes = phones.stream()
                .map(phone -> phone.getPhoneType().getId())
                .toArray(Integer[]::new);

        Assertions.assertArrayEquals(userPhoneNumbers, phoneNumbers);
        Assertions.assertArrayEquals(userPhoneNumberTypes, phoneTypes);
    }

    @Test
    void updateUserPhones_When_SomeValidData_Expect_CorrectPhonesSaved() {
        final String userNumber = "123";
        final PhoneTypes phoneType = PhoneTypes.OTHER;

        final int userId = 42;
        final User user = mock(User.class);
        given(user.getId()).willReturn(userId);

        final Phone phone = new PhoneDTO(userNumber, user, phoneType);

        final UserFormData userData = new UserFormData();
        userData.setUserPhoneNumber(new String[]{userNumber});
        userData.setUserPhoneNumberType(new Integer[]{phoneType.getId()});

        userService.updateUserPhones(userData, user);

        then(phoneService).should().updateUserPhones(Collections.singletonList(phone), userId);
    }

    @Test
    void createUser_Expect_UserSavedAndPhonesUpdated() {
        final DefaultUserService userService1 = spy(userService);
        final UserFormData mock = mock(UserFormData.class);
        final User user = mock(User.class);
        final int userId = 42;
        given(user.getId()).willReturn(userId);
        given(mock.getLangCode()).willReturn(ENG_CODE);
        given(userRepository.save(any(User.class))).willReturn(user);

        userService1.createUser(mock);

        then(userRepository).should().save(any(User.class));
        then(phoneService).should().updateUserPhones(anyList(), eq(userId));
        then(userRolesService).should().updateUserRoles(anyList(), eq(user));
        then(userAdminRolesService).should().updateUserAdminRoles(anyList(), eq(user));
    }

    @Test
    void collectRoles_When_EmptyArray_Expect_EmptyList() {
        final List<Role> roles = userService.collectRoles(new int[]{});
        Assertions.assertTrue(roles.isEmpty());
    }

    @Test
    void collectRoles_When_Null_Expect_EmptyList() {
        final List<Role> roles = userService.collectRoles(null);
        Assertions.assertTrue(roles.isEmpty());
    }

    @Test
    void collectRoles_When_ValuesPresent_Expect_EmptyList() {
        final Integer roleId = 42;
        final String roleName = "name";
        final Role role = new RoleDTO(roleId, roleName);

        given(roleService.getById(roleId)).willReturn(role);

        final List<Role> roles = userService.collectRoles(new int[]{roleId});

        Assertions.assertEquals(roles.size(), 1);
        Assertions.assertEquals(roles.get(0), role);
    }
}
