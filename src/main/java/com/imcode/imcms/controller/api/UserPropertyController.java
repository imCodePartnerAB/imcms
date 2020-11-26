package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.UserPropertyDTO;
import com.imcode.imcms.domain.service.UserPropertyService;
import com.imcode.imcms.model.UserProperty;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.*;

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
        return userPropertyService.getByUserIdAndKeyName(id, keyName).orElseThrow(()-> new EmptyResultDataAccessException(keyName, id));
    }

    @GetMapping("/value")
    public List<UserProperty> getByUserIdAndValue(@RequestParam Integer id, @RequestParam String value) {
        return userPropertyService.getByUserIdAndValue(id, value);
    }

    @PostMapping
    public UserProperty create(@RequestBody UserPropertyDTO userProperty) {
        return userPropertyService.create(userProperty);
    }

    @PutMapping
    public UserProperty update(@RequestBody UserPropertyDTO userProperty) {
        return userPropertyService.update(userProperty);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id) {
        userPropertyService.deleteById(id);
    }
}