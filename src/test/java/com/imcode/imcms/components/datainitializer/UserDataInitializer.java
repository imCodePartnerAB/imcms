package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.persistence.entity.User;
import com.imcode.imcms.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class UserDataInitializer extends TestDataCleaner {
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;

    public UserDataInitializer(UserRepository userRepository,
                               @Qualifier("dataSource") DataSource dataSource) {

        this.userRepository = userRepository;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public User createData(String login) {
        final User user = userRepository.findByLogin(login);

        if (user != null) {
            return user;
        }

        return userRepository.saveAndFlush(new User(login, "dummy_pass", login + "@imcode.com"));
    }

    public List<User> createData(Integer howMuch, Integer... roleIds) {
        final List<User> users = IntStream.range(roleIds[0] * 100, howMuch + roleIds[0] * 100)
                .mapToObj(i -> new User("test" + i, "test" + i, i + "test@imcode.com"))
                .map(userRepository::saveAndFlush)
                .collect(Collectors.toList());

        users.forEach(user -> {
            for (Integer roleId : roleIds) {
                jdbcTemplate.update("INSERT INTO user_roles_crossref (user_id, role_id) VALUES (?, ?)",
                        new Object[]{user.getId(), roleId}, new int[]{Types.INTEGER, Types.INTEGER});
            }
        });

        return users;
    }
}
