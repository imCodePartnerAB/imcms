package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.exception.UserNotExistsException;
import com.imcode.imcms.mapping.jpa.User;
import com.imcode.imcms.mapping.jpa.UserRepository;
import org.springframework.stereotype.Service;

import static java.util.Optional.ofNullable;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(int id) {
        return ofNullable(userRepository.findById(id))
                .orElseThrow(() -> new UserNotExistsException(id));
    }

}
