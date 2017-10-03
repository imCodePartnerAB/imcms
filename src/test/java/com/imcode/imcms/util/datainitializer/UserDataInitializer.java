package com.imcode.imcms.util.datainitializer;

import com.imcode.imcms.mapping.jpa.User;
import com.imcode.imcms.mapping.jpa.UserRepository;
import com.imcode.imcms.util.RepositoryTestDataCleaner;
import org.springframework.stereotype.Component;

@Component
public class UserDataInitializer extends RepositoryTestDataCleaner {
    private final UserRepository userRepository;

    public UserDataInitializer(UserRepository userRepository) {
        super(userRepository);
        this.userRepository = userRepository;
    }

    User createData(int userId) {
        final User user = userRepository.saveAndFlush(new User(userId, "admin", "admin", "admin@imcode.com"));
        user.setId(userId);
        return userRepository.saveAndFlush(user);
    }
}
