package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.component.UserLockValidator;
import com.imcode.imcms.domain.dto.*;
import com.imcode.imcms.domain.exception.UserNotExistsException;
import com.imcode.imcms.domain.service.*;
import com.imcode.imcms.model.*;
import com.imcode.imcms.persistence.entity.PasswordReset;
import com.imcode.imcms.persistence.entity.User;
import com.imcode.imcms.persistence.repository.UserRepository;
import imcode.server.LanguageMapper;
import imcode.server.user.PhoneNumber;
import imcode.server.user.PhoneNumberType;
import imcode.util.Utility;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static imcode.server.ImcmsConstants.ENG_CODE;

@Service
@Transactional
class DefaultUserService implements UserService {

    private final static Logger log = LogManager.getLogger(DefaultUserService.class.getName());

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PhoneService phoneService;
    private final UserRolesService userRolesService;
    private final ExternalToLocalRoleLinkService externalToLocalRoleService;
    private final UserLockValidator userLockValidator;
    private final LanguageService languageService;

    @PersistenceContext
    private EntityManager entityManager;

    DefaultUserService(UserRepository userRepository,
                       RoleService roleService,
                       PhoneService phoneService,
                       UserRolesService userRolesService,
                       ExternalToLocalRoleLinkService externalToLocalRoleLinkService,
                       UserLockValidator userLockValidator,
                       LanguageService languageService) {

        this.userRepository = userRepository;
        this.roleService = roleService;
        this.phoneService = phoneService;
        this.userRolesService = userRolesService;
        this.externalToLocalRoleService = externalToLocalRoleLinkService;
        this.userLockValidator = userLockValidator;
        this.languageService = languageService;
    }

    @Override
    public User getUser(int id) throws UserNotExistsException {
        return Optional.ofNullable(userRepository.findById(id))
                .orElseThrow(() -> new UserNotExistsException(id));
    }

    @Override
    public UserDTO getUser(String login) throws UserNotExistsException {
        return Optional.ofNullable(userRepository.findByLogin(login))
                .map(UserDTO::new)
                .orElseThrow(() -> new UserNotExistsException(login));
    }

    @Override
    public void updateUser(UserDTO updateData) {
        final Integer id = updateData.getId();
        if (id == null) return;

        final UserGDPRDataDTO previousUserGDPRDataDTO = getUserGDPRDataDTO(id);

        final User user = getUser(id);
        updatePresentUserFields(user, updateData);
        userRepository.save(user);

        //log changed fields
        final UserGDPRDataDTO updatedUserGDPRDataDTO = getUserGDPRDataDTO(id);
        List<String> changedFields = Utility.findFieldsWithMismatchedValue(previousUserGDPRDataDTO, updatedUserGDPRDataDTO);
        if(!changedFields.isEmpty()) Utility.logGDPR(id, "Changed data: " + String.join(";", changedFields));
    }

    private void updatePresentUserFields(User user, UserDTO updateData) {
        updateFieldIfPresent(updateData::getId, user::setId);
        updateFieldIfPresent(updateData::getEmail, user::setEmail);
        updateFieldIfPresent(updateData::getFirstName, user::setFirstName);
        updateFieldIfPresent(updateData::getLastName, user::setLastName);
        updateFieldIfPresent(updateData::getLogin, user::setLogin);
        updateFieldIfPresent(updateData::getActive, user::setActive);
    }

    private <T> void updateFieldIfPresent(Supplier<T> fieldGetter, Consumer<T> fieldSetter) {
        Optional.ofNullable(fieldGetter.get()).ifPresent(fieldSetter);
    }

    @Override
    public int incrementUserAttempts(int id) {
        User user = userRepository.findById(id);

        final int incrementedAttempts = user.getAttempts() + 1;

        user.setAttempts(incrementedAttempts);
        userRepository.save(user);

        return incrementedAttempts;
    }

    @Override
    public void resetUserAttempts(int id) {
        User user = userRepository.findById(id);
        user.setAttempts(0);
    }

    @Override
    public void updateUserBlockDate(Date blockDate, int id) {
        User user = userRepository.findById(id);
        user.setBlockedDate(blockDate);
    }

    @Override
    public void updateLastLoginDate(Date lastLoginDate, int id) {
        User user = userRepository.findById(id);
        user.setLastLoginDate(lastLoginDate);
    }

    @Override
    public List<UserDTO> getAdminUsers() {
        return toDTO(userRepository.findUsersWithRoleIds(Roles.SUPER_ADMIN.getId()));
    }

    @Override
    public List<UserDTO> getAllActiveUsers() {
        return toDTO(userRepository.findByActiveIsTrue());
    }

    @Override
    public List<UserDTO> getUsersByEmail(String email) {
        return toDTO(userRepository.findByEmail(email));
    }

    private List<UserDTO> toDTO(Collection<User> users) {
        return users.stream().map(UserDTO::new).collect(Collectors.toList());
    }

    @Override
    public UserFormData getUserData(int userId) throws UserNotExistsException {
        final User userJPA = getUser(userId);
        final UserFormData userFormData = new UserFormData(userJPA);

        setUserPhones(userFormData, userId);
        setUserRoles(userFormData, userId);

        return userFormData;
    }

    private void setUserPhones(UserFormData userFormData, int userId) {
        final List<Phone> userPhones = phoneService.getUserPhones(userId);
        final List<String> userPhoneNumbers = new ArrayList<>();
        final List<Integer> userPhoneNumberTypes = new ArrayList<>();

        for (Phone userPhone : userPhones) {
            userPhoneNumbers.add(userPhone.getNumber());
            userPhoneNumberTypes.add(userPhone.getPhoneType().getId());
        }

        userFormData.setUserPhoneNumber(userPhoneNumbers.toArray(new String[0]));
        userFormData.setUserPhoneNumberType(userPhoneNumberTypes.toArray(new Integer[0]));
    }

    private void setUserRoles(UserFormData userFormData, int userId) {
        final int[] userRoleIds = toRoleIds(userRolesService.getRolesByUser(userId));
        userFormData.setRoleIds(userRoleIds);
    }

    private int[] toRoleIds(Collection<Role> roles) {
        return roles.stream()
                .filter(Predicate.isEqual(Roles.USER).negate())
                .mapToInt(Role::getId)
                .toArray();
    }

    @Override
    public void saveUser(UserFormData userData) {
        final UserGDPRDataDTO previousUserGDPRDataDTO = userData.getId() != null ? getUserGDPRDataDTO(userData.getId()) : null;

        final User user = saveAndGetUser(userData);
        updateUserData(userData, user);

        //log changed fields
        if(previousUserGDPRDataDTO == null){
            Utility.logGDPR(user.getId(), "Created new user");
        }else{
            UserGDPRDataDTO updatedUserGDPRDataDTO = getUserGDPRDataDTO(user.getId());
            List<String> changedFields = Utility.findFieldsWithMismatchedValue(previousUserGDPRDataDTO, updatedUserGDPRDataDTO);
            if(!changedFields.isEmpty()) Utility.logGDPR(user.getId(), "Changed data: " + String.join(";", changedFields));
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected User saveAndGetUser(UserFormData userData) {
        final String userLangCode = userData.getLangCode();
        if (!languageService.isAdminLanguage(userLangCode)) {
            log.info(String.format("Language  as %s does not support in admin lang, so will set to %s", userLangCode, ENG_CODE));
            userData.setLangCode(ENG_CODE);
        }

        final User user = toUserJPA(userData);

        if (userData.getId() != null) {
	        final User existingUser = userRepository.getOne(userData.getId());
            if (StringUtils.isBlank(user.getPassword())) user.setPassword(existingUser.getPassword());
            if (user.getPasswordReset() == null) user.setPasswordReset(existingUser.getPasswordReset());
            user.setSessionId(existingUser.getSessionId());
            user.setRememberCd(existingUser.getRememberCd());
            user.setPasswordType(existingUser.getPasswordType());
            user.setExternal(existingUser.isExternal());

            if (userData.isFlagOfBlocking() && userLockValidator.isUserBlocked(toUserFormData(existingUser))) {
                user.setBlockedDate(existingUser.getCreateDate());
            }
        }

        return userRepository.save(user);
    }

    private UserGDPRDataDTO getUserGDPRDataDTO(int id){
        User oldUser = userRepository.findById(id);
        Set<Integer> roleIds = userRolesService.getRoleIdsByUser(id);
        Set<PhoneNumber> phoneNumbers = phoneService.getUserPhones(id).stream()
                .map(phone -> new PhoneNumber(phone.getNumber(), PhoneNumberType.getPhoneNumberTypeById(phone.getPhoneType().getId())))
                .collect(Collectors.toSet());

        return new UserGDPRDataDTO(oldUser, roleIds, phoneNumbers);
    }

    private UserFormData toUserFormData(User user) {
        UserFormData userFormData = new UserFormData(user);
        userFormData.setExternal(user.isExternal());

        return userFormData;
    }

    private User toUserJPA(UserFormData userData) {
        final User user = new User(userData);
        user.setExternal(userData.isExternal());
        user.setLanguageIso639_2(LanguageMapper.convert639_1to639_2(userData.getLangCode()));

        PasswordResetDTO passwordResetDTO = userData.getPasswordReset();
        if(passwordResetDTO != null){
            user.setPasswordReset(new PasswordReset(passwordResetDTO.getId(), passwordResetDTO.getTime()));
        }

        return user;
    }

    private void updateUserData(UserFormData userData, User user) {
        updateUserPhones(userData, user);
        updateUserRoles(userData, user);
    }

    void updateUserPhones(UserFormData userData, User user) {
        final List<Phone> phoneNumbers = Utility.collectPhoneNumbers(userData, user);
        phoneService.updateUserPhones(phoneNumbers, user.getId());
    }

    private void updateUserRoles(UserFormData userData, User user) {
        final List<Role> userRoles = collectRoles(userData.getRoleIds());
        userRolesService.updateUserRoles(userRoles, user);
    }

    List<Role> collectRoles(int[] roleIdsInt) {
        if (roleIdsInt == null || roleIdsInt.length == 0) return Collections.emptyList();

        return Arrays.stream(roleIdsInt)
                .mapToObj(roleService::getById)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> searchUsers(String searchTerm, Set<Integer> withRoles, boolean includeInactive) {
        final List<User> users;
        searchTerm = StringUtils.defaultString(searchTerm).toLowerCase();

        if ((withRoles == null) || (withRoles.isEmpty())) {
            users = includeInactive
                    ? userRepository.searchUsers(searchTerm)
                    : userRepository.searchActiveUsers(searchTerm);
        } else {
            users = includeInactive
                    ? userRepository.searchUsers(searchTerm, withRoles)
                    : userRepository.searchActiveUsers(searchTerm, withRoles);
        }

        return toDTO(users);
    }

    // TODO: 13.10.17 Was moved. Rewrite to human code.
    @Override
    public List<User> findAll(boolean includeExternal, boolean includeInactive) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> c = cb.createQuery(User.class);
        Root<User> user = c.from(User.class);

        javax.persistence.criteria.Predicate criteria = cb.conjunction();

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

        javax.persistence.criteria.Predicate criteria = cb.notEqual(user.get("external"), 2);

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

    @Override
    public ExternalUser saveExternalUser(ExternalUser user) {
        final Set<Integer> linkedLocalRoleIds = externalToLocalRoleService.toLinkedLocalRoles(user.getExternalRoles())
                .stream()
                .map(Role::getId)
                .collect(Collectors.toSet());

        user.setRoleIds(linkedLocalRoleIds);

        final User savedUser = userRepository.findByLogin(user.getLogin());

        if (savedUser != null) {
            user.setId(savedUser.getId());
        }

        saveUser(new UserFormData(user));

        return user;
    }
}
