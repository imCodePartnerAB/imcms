package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.api.exception.DataIsNotValidException;
import com.imcode.imcms.domain.dto.UserPropertyDTO;
import com.imcode.imcms.domain.service.UserPropertyService;
import com.imcode.imcms.model.UserProperty;
import com.imcode.imcms.persistence.entity.UserPropertyJPA;
import com.imcode.imcms.persistence.repository.UserPropertyRepository;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.apache.cxf.interceptor.security.AccessDeniedException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class DefaultUserPropertyService implements UserPropertyService {

    private final UserPropertyRepository userPropertyRepository;

    DefaultUserPropertyService(UserPropertyRepository userPropertyRepository) {
        this.userPropertyRepository = userPropertyRepository;
    }

    @Override
    public List<UserProperty> getAll() {
        final UserDomainObject user = Imcms.getUser();
        if (!user.isSuperAdmin()) {
            throw new AccessDeniedException("Current user doesn't has access with loginName: " + user.getLogin());
        }

        return userPropertyRepository.findAll()
                .stream()
                .map(UserPropertyDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserProperty> getByUserId(Integer id) {
        final UserDomainObject user = Imcms.getUser();
        if (!user.isSuperAdmin()) {
            throw new AccessDeniedException("Current user doesn't has access with loginName: " + user.getLogin());
        }
        return userPropertyRepository.findByUserId(id);
    }

    @Override
    public UserProperty getByUserIdAndKeyName(Integer userId, String keyName) {
        final UserDomainObject user = Imcms.getUser();
        if (!user.isSuperAdmin()) {
            throw new AccessDeniedException("Current user doesn't has access with loginName: " + user.getLogin());
        }
        return Optional.ofNullable(userPropertyRepository.findByUserIdAndKeyName(userId, keyName)).map(UserPropertyDTO::new)
                .orElseThrow(() -> new EmptyResultDataAccessException(keyName, userId));
    }

    @Override
    public List<UserProperty> getByUserIdAndValue(Integer userId, String value) {
        final UserDomainObject user = Imcms.getUser();
        if (!user.isSuperAdmin()) {
            throw new AccessDeniedException("Current user doesn't has access with loginName: " + user.getLogin());
        }
        return userPropertyRepository.findByUserIdAndValue(userId, value);
    }

    @Override
    public void create(List<UserPropertyDTO> userProperties) {
        final UserDomainObject user = Imcms.getUser();
        if (!user.isSuperAdmin()) {
            throw new AccessDeniedException("Current user doesn't has access with loginName: " + user.getLogin());
        }

        userProperties.forEach(userProperty -> {

            if (userProperty.getKeyName().isEmpty() || userProperty.getValue().isEmpty()) {
                throw new DataIsNotValidException();
            }

            userPropertyRepository.save(new UserPropertyJPA(userProperty));
        });

    }

    @Override
    public UserProperty update(UserProperty userProperty) {
        final UserDomainObject user = Imcms.getUser();
        if (!user.isSuperAdmin()) {
            throw new AccessDeniedException("Current user doesn't has access with loginName: " + user.getLogin());
        }

        UserProperty receivedUserProperty = userPropertyRepository.getOne(userProperty.getId());
        if (userProperty.getKeyName().isEmpty() || userProperty.getValue().isEmpty()) {
            throw new DataIsNotValidException();
        }else{
            receivedUserProperty.setKeyName(userProperty.getKeyName());
            receivedUserProperty.setValue(userProperty.getValue());
        }
        return new UserPropertyDTO(userPropertyRepository.save(new UserPropertyJPA(receivedUserProperty)));

    }

    @Override
    public void deleteById(Integer id) {
        final UserDomainObject user = Imcms.getUser();
        if (!user.isSuperAdmin()) {
            throw new AccessDeniedException("Current user doesn't has access with loginName: " + user.getLogin());
        }

        userPropertyRepository.delete(id);
    }
}
