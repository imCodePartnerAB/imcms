package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.model.Phone;
import com.imcode.imcms.persistence.entity.PhoneJPA;
import com.imcode.imcms.persistence.entity.PhoneTypeJPA;
import com.imcode.imcms.persistence.entity.User;
import com.imcode.imcms.persistence.repository.PhoneRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class LocalUserPhoneServiceTest {

    @Mock
    private PhoneRepository phoneRepository;

    @InjectMocks
    private LocalUserPhoneService phoneService;

    @Test
    void updateUserPhones_Expect_SaveMethodInRepositoryIsCalledOnce() {
        phoneService.updateUserPhones(Collections.emptyList(), 1);
        then(phoneRepository).should(times(1)).save(anyCollection());
    }

    @Test
    void updateUserPhones_Expect_DeleteByUserIdWasCalledAndThenSaveMethodIsCalledOnce() {
        InOrder inOrder = inOrder(phoneRepository);

        final int userId = 1;
        phoneService.updateUserPhones(Collections.emptyList(), userId);

        then(phoneRepository).should(inOrder, times(1)).deleteByUserId(userId);
        then(phoneRepository).should(inOrder, times(1)).save(anyCollection());
    }

    @Test
    void getUserPhones() {
        final int userId = 42;
        final List<PhoneJPA> phoneJPAS = new ArrayList<>();
        phoneJPAS.add(new PhoneJPA("123", mock(User.class), new PhoneTypeJPA()));
        phoneJPAS.add(new PhoneJPA("456", mock(User.class), new PhoneTypeJPA()));

        given(phoneRepository.findByUserId(userId)).willReturn(phoneJPAS);

        final List<Phone> userPhones = phoneService.getUserPhones(userId);

        Assertions.assertEquals(userPhones.size(), phoneJPAS.size());
    }
}
