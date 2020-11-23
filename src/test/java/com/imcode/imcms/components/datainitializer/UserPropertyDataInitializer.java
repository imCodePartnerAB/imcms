package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.domain.dto.UserPropertyDTO;
import com.imcode.imcms.model.UserProperty;
import com.imcode.imcms.persistence.entity.UserPropertyJPA;
import com.imcode.imcms.persistence.repository.UserPropertyRepository;
import org.springframework.stereotype.Component;

@Component
public class UserPropertyDataInitializer extends TestDataCleaner{

    private final UserPropertyRepository userPropertyRepository;

    public UserPropertyDataInitializer(UserPropertyRepository userPropertyRepository){
        this.userPropertyRepository = userPropertyRepository;
    }

    public UserProperty createData(Integer userId, String keyName, String value){
        UserProperty userProperty = new UserPropertyDTO();

        userProperty.setUserId(userId);
        userProperty.setKeyName(keyName);
        userProperty.setValue(value);

        return new UserPropertyDTO(userPropertyRepository.save(new UserPropertyJPA(userProperty)));
    }

//    public UserProperty createData(String keyName, String value){
//
//        return new UserPropertyDTO(userPropertyRepository.save(new UserPropertyJPA(userProperty)));
///       return new UserPropertyDTO(id, keyName, value);
//    }
//

//    public List<UserPropertyDTO> createData(int count, String value, String keyName, Integer userId) {
//        final List<UserPropertyDTO> listUserPropertyDTO = new ArrayList<>();
//
//        for(int i = 0; i < count; i++){
//            listUserPropertyDTO.add(createData(value));
//        }
//        return listUserPropertyDTO;
//    }

}
