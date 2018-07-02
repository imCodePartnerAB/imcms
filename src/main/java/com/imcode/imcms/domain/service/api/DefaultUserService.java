package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.UserDTO;
import com.imcode.imcms.domain.dto.UserData;
import com.imcode.imcms.domain.exception.UserNotExistsException;
import com.imcode.imcms.domain.service.RoleService;
import com.imcode.imcms.domain.service.UserService;
import com.imcode.imcms.model.Role;
import com.imcode.imcms.persistence.entity.User;
import com.imcode.imcms.persistence.repository.UserRepository;
import imcode.server.LanguageMapper;
import imcode.server.user.PhoneNumber;
import imcode.server.user.PhoneNumberType;
import imcode.server.user.RoleId;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
class DefaultUserService implements UserService {

    private final static Logger log = Logger.getLogger(DefaultUserService.class.getName());

    private final UserRepository userRepository;
    private final RoleService roleService;

    @PersistenceContext
    private EntityManager entityManager;

    DefaultUserService(UserRepository userRepository, RoleService roleService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
    }

    @Override
    public User getUser(int id) {
        return Optional.ofNullable(userRepository.findById(id))
                .orElseThrow(() -> new UserNotExistsException(id));
    }

    @Override
    public User getUser(String login) {
        return Optional.ofNullable(userRepository.findByLogin(login))
                .orElseThrow(() -> new UserNotExistsException(login));
    }

    @Override
    public List<UserDTO> getAdminUsers() {
        return toDTO(userRepository.findUsersWithRoleIds(RoleId.USERADMIN_ID, RoleId.SUPERADMIN_ID));
    }

    @Override
    public List<UserDTO> getAllActiveUsers() {
        return toDTO(userRepository.findByActiveIsTrue());
    }

    @Override
    public List<UserDTO> getUsersByEmail(String email) {
        return toDTO(userRepository.findByEmail(email));
    }

    @Override
    public void createUser(UserData userData) {
        final User user = new User(userData.getLoginName(), userData.getPassword1(), userData.getEmail());
        user.setFirstName(userData.getFirstName());
        user.setLastName(userData.getLastName());
        user.setTitle(userData.getTitle());
        user.setCompany(userData.getCompany());
        user.setAddress(userData.getAddress());
        user.setZip(userData.getZip());
        user.setCity(userData.getCity());
        user.setProvince(userData.getProvince());
        user.setCountry(userData.getCountry());
        user.setLanguageIso639_2(LanguageMapper.convert639_1to639_2(userData.getLangCode()));

        final List<PhoneNumber> phoneNumbers = collectPhoneNumbers(userData);
        final List<Role> userRoles = collectRoles(userData.getRoleIds());
        final List<Role> administrateRoles = collectRoles(userData.getUserAdminRoleIds());
    }

    private List<Role> collectRoles(int[] roleIdsInt) {
        if (roleIdsInt == null) return Collections.emptyList();

        return Arrays.stream(roleIdsInt)
                .mapToObj(roleService::getById)
                .collect(Collectors.toList());
    }

    private List<PhoneNumber> collectPhoneNumbers(UserData userData) {

        final String[] userPhoneNumbers = userData.getUserPhoneNumber();
        final Integer[] userPhoneNumberTypes = userData.getUserPhoneNumberType();

        if ((userPhoneNumbers == null)
                || (userPhoneNumberTypes == null)
                || (userPhoneNumbers.length <= 0)
                || (userPhoneNumberTypes.length <= 0)
                || (userPhoneNumbers.length != userPhoneNumberTypes.length))
        {
            return Collections.emptyList();
        }

        List<PhoneNumber> numbers = new ArrayList<>();

        for (int i = 0; i < userPhoneNumbers.length; i++) {
            try {
                final String userPhoneNumber = userPhoneNumbers[i];
                final PhoneNumberType numberType = PhoneNumberType.getPhoneNumberTypeById(userPhoneNumberTypes[i]);
                numbers.add(new PhoneNumber(userPhoneNumber, numberType));

            } catch (Exception e) {
                log.error("Something wrong with phone numbers.", e);
            }
        }

        return numbers;
    }

    private List<UserDTO> toDTO(Collection<User> users) {
        return users.stream().map(UserDTO::new).collect(Collectors.toList());
    }

    // TODO: 13.10.17 Was moved. Rewrite to human code.
    @Override
    public List<User> findAll(boolean includeExternal, boolean includeInactive) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> c = cb.createQuery(User.class);
        Root<User> user = c.from(User.class);

        Predicate criteria = cb.conjunction();

        if (!includeExternal) {
            criteria = cb.and(criteria, cb.notEqual(user.get("external"), 2));
        }

        if (!includeInactive) {
            criteria = cb.and(criteria, cb.isTrue(user.get("active")));
        }

        c.select(user).where(criteria);

        return entityManager.createQuery(c).getResultList();
    }

    // TODO: 13.10.17 Was moved. Rewrite to human code.
    @Override
    public List<User> findByNamePrefix(String prefix, boolean includeInactive) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> c = cb.createQuery(User.class);
        Root<User> user = c.from(User.class);

        Predicate criteria = cb.notEqual(user.get("external"), 2);

        if (!includeInactive) {
            criteria = cb.and(criteria, cb.isTrue(user.get("active")));
        }

        criteria = cb.and(
                criteria,
                cb.or(
                        cb.like(user.get("login"), prefix),
                        cb.like(user.get("email"), prefix),
                        cb.like(user.get("firstName"), prefix),
                        cb.like(user.get("lastName"), prefix),
                        cb.like(user.get("title"), prefix),
                        cb.like(user.get("company"), prefix)
                )
        );

        c.select(user).where(criteria).orderBy(cb.asc(user.get("lastName")), cb.asc(user.get("firstName")));

        return entityManager.createQuery(c).getResultList();
    }

}
