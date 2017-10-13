package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.mapping.jpa.User;
import com.imcode.imcms.mapping.jpa.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class UserDataInitializer extends AbstractTestDataInitializer<Integer, List<User>> {
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;

    public UserDataInitializer(UserRepository userRepository,
                               @Qualifier("dataSourceWithAutoCommit") DataSource dataSource) {
        this.userRepository = userRepository;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<User> createData(Integer userId) {
        final User user = userRepository.saveAndFlush(new User(userId, "admin", "admin", "admin@imcode.com"));
        user.setId(userId);
        return Collections.singletonList(userRepository.saveAndFlush(user));
    }

    @Override
    public List<User> createData(Integer howMuch, Integer roleId) {
        final List<User> users = IntStream.range(roleId * 100, howMuch + roleId * 100)
                .mapToObj(i -> new User("test" + i, "test" + i, i + "test@imcode.com"))
                .map(userRepository::saveAndFlush)
                .collect(Collectors.toList());

        users.forEach(user ->
                jdbcTemplate.update("INSERT INTO user_roles_crossref (user_id, role_id) VALUES (?, ?)",
                        new Object[]{user.getId(), roleId}, new int[]{Types.INTEGER, Types.INTEGER}));

        return users;
    }

    public void cleanRepositories(List<User> users) {
        users.forEach(user -> {
            jdbcTemplate.update("DELETE FROM user_roles_crossref WHERE user_id = " + user.getId());
            userRepository.delete(user.getId());
        });
    }
}
