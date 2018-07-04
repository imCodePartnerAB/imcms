package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.persistence.repository.PhoneRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.mockito.BDDMockito.anyCollection;
import static org.mockito.BDDMockito.inOrder;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;

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
}