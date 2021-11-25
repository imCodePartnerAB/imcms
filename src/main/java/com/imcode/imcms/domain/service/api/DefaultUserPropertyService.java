package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.api.exception.DataIsNotValidException;
import com.imcode.imcms.domain.dto.UserPropertyDTO;
import com.imcode.imcms.domain.service.UserPropertyService;
import com.imcode.imcms.model.UserProperty;
import com.imcode.imcms.persistence.entity.UserPropertyJPA;
import com.imcode.imcms.persistence.repository.UserPropertyRepository;
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
        return userPropertyRepository.findAll()
                .stream()
                .map(UserPropertyDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserProperty> getByUserId(Integer id) {
        return userPropertyRepository.findByUserId(id);
    }

    @Override
    public UserProperty getByUserIdAndKeyName(Integer userId, String keyName) {
        return Optional.ofNullable(userPropertyRepository.findByUserIdAndKeyName(userId, keyName)).map(UserPropertyDTO::new)
                .orElseThrow(() -> new EmptyResultDataAccessException(keyName, userId));
    }

    @Override
    public void create(UserPropertyDTO userProperty) {
        if (userProperty.getKeyName().isEmpty() || userProperty.getValue().isEmpty()) {
            throw new DataIsNotValidException();
        }
        userPropertyRepository.saveAndFlush(new UserPropertyJPA(userProperty));
    }

    @Override
    public UserProperty update(UserProperty userProperty) {
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
    public void update(List<UserPropertyDTO> deletedProperties, List<UserPropertyDTO> editedProperties, List<UserPropertyDTO> createdProperties) {
        deletedProperties.forEach((userProperty -> deleteById(userProperty.getId())));
        editedProperties.forEach(this::update);
        createdProperties.forEach(this::create);
    }

    @Override
    public void deleteById(Integer id) {
        userPropertyRepository.delete(id);
        userPropertyRepository.flush();
    }
}
