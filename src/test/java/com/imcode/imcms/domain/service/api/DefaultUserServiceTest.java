package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.PhoneDTO;
import com.imcode.imcms.domain.dto.UserFormData;
import com.imcode.imcms.domain.service.PhoneService;
import com.imcode.imcms.domain.service.RoleService;
import com.imcode.imcms.model.Phone;
import com.imcode.imcms.model.PhoneTypes;
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
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyList;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.spy;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DefaultUserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleService roleService;
    @Mock
    private PhoneService phoneService;

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

        final int userId = 13;
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
        final int userId = 13;
        given(user.getId()).willReturn(userId);
        given(mock.getLangCode()).willReturn(ENG_CODE);
        given(userRepository.save(any(User.class))).willReturn(user);

        userService1.createUser(mock);

        then(userRepository).should().save(any(User.class));
        then(phoneService).should().updateUserPhones(anyList(), eq(userId));
    }
}
