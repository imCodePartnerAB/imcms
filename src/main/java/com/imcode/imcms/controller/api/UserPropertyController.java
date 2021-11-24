package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.UserPropertyDTO;
import com.imcode.imcms.domain.service.UserPropertyService;
import com.imcode.imcms.model.UserProperty;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/user/properties")
public class UserPropertyController {

    private final UserPropertyService userPropertyService;

    public UserPropertyController(UserPropertyService userPropertyService) {
        this.userPropertyService = userPropertyService;
    }

    @GetMapping
    public List<UserProperty> getAll() {
        return userPropertyService.getAll();
    }

    @GetMapping("/{id}")
    public List<UserProperty> getByUserId(@PathVariable Integer id) {
        return userPropertyService.getByUserId(id);
    }

    @GetMapping("/name")
    public UserProperty getByUserIdAndKeyName(@RequestParam Integer id, @RequestParam String keyName) {
        return userPropertyService.getByUserIdAndKeyName(id, keyName);
    }

    @PostMapping
    public void create(@RequestBody UserPropertyDTO userProperty) {
        userPropertyService.create(userProperty);
    }

    @PutMapping
    public UserProperty update(@RequestBody UserPropertyDTO userProperty) {
        return userPropertyService.update(userProperty);
    }

    @PostMapping("/update")
    public void update(@RequestBody HashMap<String, List<UserPropertyDTO>> data) {
        List<UserPropertyDTO> deletedProperties = data.get("deletedProperties");
        List<UserPropertyDTO> editedProperties = data.get("editedProperties");
        List<UserPropertyDTO> createdProperties = data.get("createdProperties");
        userPropertyService.update(deletedProperties, editedProperties, createdProperties);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id) {
        userPropertyService.deleteById(id);
    }
}