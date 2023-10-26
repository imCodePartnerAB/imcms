package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.PhoneDTO;
import com.imcode.imcms.domain.dto.RoleDTO;
import com.imcode.imcms.domain.dto.UserDTO;
import com.imcode.imcms.domain.dto.UserFormData;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.domain.service.PhoneService;
import com.imcode.imcms.domain.service.RoleService;
import com.imcode.imcms.domain.service.UserRolesService;
import com.imcode.imcms.model.Phone;
import com.imcode.imcms.model.PhoneTypes;
import com.imcode.imcms.model.Role;
import com.imcode.imcms.persistence.entity.User;
import com.imcode.imcms.persistence.repository.UserRepository;
import imcode.util.Utility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private LanguageService languageService;

    @InjectMocks
    private DefaultUserService userService;

    @Test
    void collectPhoneNumbers_When_EmptyPhonesAndTypes_Expect_EmptyCollection() {
        final UserFormData userData = new UserFormData();
        userData.setUserPhoneNumber(new String[0]);
        userData.setUserPhoneNumberType(new Integer[0]);

        final List<Phone> phones = Utility.collectPhoneNumbers(userData, mock(User.class));

        Assertions.assertTrue(phones.isEmpty());
    }

    @Test
    void collectPhoneNumbers_When_NullPhonesAndTypes_Expect_EmptyCollection() {
        final UserFormData userData = new UserFormData();
        userData.setUserPhoneNumber(null);
        userData.setUserPhoneNumberType(null);

        final List<Phone> phones = Utility.collectPhoneNumbers(userData, mock(User.class));

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

        final List<Phone> phones = Utility.collectPhoneNumbers(userData, user);

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
        given(mock.getId()).willReturn(null);
        given(user.getId()).willReturn(userId);
        given(mock.getLangCode()).willReturn(ENG_CODE);
        given(userRepository.save(any(User.class))).willReturn(user);

        userService1.saveUser(mock);

        then(userRepository).should().save(any(User.class));
        then(phoneService).should().updateUserPhones(anyList(), eq(userId));
        then(userRolesService).should().updateUserRoles(anyList(), eq(user));
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
        final int roleId = 42;
        final String roleName = "name";
        final Role role = new RoleDTO(roleId, roleName);

        given(roleService.getById(roleId)).willReturn(role);

        final List<Role> roles = userService.collectRoles(new int[]{roleId});

        Assertions.assertEquals(roles.size(), 1);
        Assertions.assertEquals(roles.get(0), role);
    }

    @Test
    void updateUser_When_CorrectDataReceivedAndUserExist_Expect_UserSaved() {
        final int userId = 42;
        final User existingUser = mock(User.class);
        final UserDTO updateData = mock(UserDTO.class);

        given(updateData.getId()).willReturn(userId);
        given(updateData.getActive()).willReturn(false);
        given(userRepository.findById(userId)).willReturn(existingUser);

        userService.updateUser(updateData);

        then(updateData).should().getActive();
        then(existingUser).should().setActive(false);
        then(userRepository).should().save(any(User.class));
    }

    @Test
    void searchUsers_When_NullRolesAndActiveUsersOnly_Expect_CorrespondingMethodCalled() {
        final String searchTerm = "";
        userService.searchUsers(searchTerm, null, false);
        then(userRepository).should().searchActiveUsers(searchTerm);
    }

    @Test
    void searchUsers_When_EmptyRolesAndActiveUsersOnly_Expect_CorrespondingMethodCalled() {
        final String searchTerm = "";
        userService.searchUsers(searchTerm, new HashSet<>(), false);
        then(userRepository).should().searchActiveUsers(searchTerm);
    }

    @Test
    void searchUsers_When_NotEmptyRolesAndActiveUsersOnly_Expect_CorrespondingMethodCalled() {
        final String searchTerm = "";
        final Set<Integer> roles = new HashSet<>();
        roles.add(42);

        userService.searchUsers(searchTerm, roles, false);
        then(userRepository).should().searchActiveUsers(searchTerm, roles);
    }

    @Test
    void searchUsers_When_NullRolesAndAllUsers_Expect_CorrespondingMethodCalled() {
        final String searchTerm = "";
        userService.searchUsers(searchTerm, null, true);
        then(userRepository).should().searchUsers(searchTerm);
    }

    @Test
    void searchUsers_When_EmptyRolesAndAllUsers_Expect_CorrespondingMethodCalled() {
        final String searchTerm = "";
        userService.searchUsers(searchTerm, new HashSet<>(), true);
        then(userRepository).should().searchUsers(searchTerm);
    }

    @Test
    void searchUsers_When_NotEmptyRolesAndAllUsers_Expect_CorrespondingMethodCalled() {
        final String searchTerm = "";
        final Set<Integer> roles = new HashSet<>();
        roles.add(42);

        userService.searchUsers(searchTerm, roles, true);
        then(userRepository).should().searchUsers(searchTerm, roles);
    }
}
