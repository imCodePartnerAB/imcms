package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.service.SystemPropertyService;
import com.imcode.imcms.mapping.jpa.SystemProperty;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/properties")
public class SystemPropertyController {

    private final SystemPropertyService systemPropertyService;

    public SystemPropertyController(SystemPropertyService systemPropertyService) {
        this.systemPropertyService = systemPropertyService;
    }

    @GetMapping
    public List<SystemProperty> findAll() {
        return systemPropertyService.findAll();
    }

    @GetMapping("/{name}")
    public SystemProperty findByName(@PathVariable String name) {
        return systemPropertyService.findByName(name);
    }

    @PostMapping
    public SystemProperty update(@RequestBody SystemProperty systemProperty) {
        return systemPropertyService.update(systemProperty);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id) {
        systemPropertyService.deleteById(id);
    }


}
