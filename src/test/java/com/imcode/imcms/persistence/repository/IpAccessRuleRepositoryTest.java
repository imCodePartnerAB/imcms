package com.imcode.imcms.persistence.repository;


import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.persistence.entity.IpAccessRuleJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class IpAccessRuleRepositoryTest extends WebAppSpringTestConfig {

    private final static String IP_RANGE_TEMPLATE_IP_V4 = "192.168.0.1-192.168.0.2";
    private final static String IP_RANGE_TEMPLATE_IP_V6_SHORT = "2001:db8::ff00:42:8329-12001:db8::ff00:42:8330";
    private final static String IP_RANGE_TEMPLATE_IP_V6_FULL = "2001:0db8:0000:0000:0000:ff00:0042:8329-2001:0db8:0000:0000:0000:ff00:0042:8330";

    @Autowired
    private IpAccessRuleRepository ipAccessRuleRepository;

    @BeforeEach
    public void setUp() {
        ipAccessRuleRepository.deleteAll();
        assertTrue(ipAccessRuleRepository.findAll().isEmpty());
    }

    @Test
    public void save_WhenEntityNotExist_Expect_EntityCreated() {
        final IpAccessRuleJPA rule = new IpAccessRuleJPA();
        rule.setEnabled(true);
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V4);
        rule.setRestricted(true);

        ipAccessRuleRepository.save(rule);

        assertNotNull(rule.getId());
    }

    @Test
    public void saveCollection_WhenEntitiesNotExist_ExpectEntitiesSaved() {
        final int size = 4;
        final List<IpAccessRuleJPA> rulesToSave = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            final IpAccessRuleJPA rule = new IpAccessRuleJPA();
            rule.setEnabled(true);
            rule.setIpRange(i % 2 == 0 ? IP_RANGE_TEMPLATE_IP_V4 : IP_RANGE_TEMPLATE_IP_V6_FULL);
            rule.setRestricted(true);
            rulesToSave.add(rule);
        }

        final List<IpAccessRuleJPA> saved = ipAccessRuleRepository.save(rulesToSave);

        assertNotNull(saved);
        assertEquals(rulesToSave.size(), saved.size());
    }

    @Test
    public void findOne_WhenEntityExist_ExpectCorrectEntity() {
        final IpAccessRuleJPA rule = new IpAccessRuleJPA();
        rule.setEnabled(true);
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V4);
        rule.setRestricted(true);

        ipAccessRuleRepository.save(rule);

        assertNotNull(ipAccessRuleRepository.findOne(rule.getId()));
    }

    @Test
    public void findAll_WhenEntitiesExist_ExpectCorrectEntities() {
        final int size = 4;
        final List<IpAccessRuleJPA> rulesToSave = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            final IpAccessRuleJPA rule = new IpAccessRuleJPA();
            rule.setEnabled(true);
            rule.setIpRange(i % 2 == 0 ? IP_RANGE_TEMPLATE_IP_V4 : IP_RANGE_TEMPLATE_IP_V6_FULL);
            rule.setRestricted(true);
            rulesToSave.add(rule);
        }

        ipAccessRuleRepository.save(rulesToSave);

        final List<IpAccessRuleJPA> all = ipAccessRuleRepository.findAll();

        assertEquals(size, all.size());

    }

    @Test
    public void update_WhenEntityExist_ExpectUpdatedEntity() {
        final IpAccessRuleJPA rule = new IpAccessRuleJPA();
        rule.setEnabled(true);
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V4);
        rule.setRestricted(true);

        ipAccessRuleRepository.save(rule);

        assertNotNull(rule.getId());


        final IpAccessRuleJPA ruleToUpdate = ipAccessRuleRepository.findOne(rule.getId());
        ruleToUpdate.setIpRange(IP_RANGE_TEMPLATE_IP_V6_SHORT);
        rule.setEnabled(false);

        ipAccessRuleRepository.save(ruleToUpdate);

        assertEquals(1, ipAccessRuleRepository.findAll().size());
        assertEquals(IP_RANGE_TEMPLATE_IP_V6_SHORT, ipAccessRuleRepository.findOne(rule.getId()).getIpRange());
    }

    @Test
    public void delete_WhenEntityExist_ExpectEntityDeleted() {
        final IpAccessRuleJPA rule = new IpAccessRuleJPA();
        rule.setEnabled(true);
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V6_FULL);
        rule.setRestricted(true);

        ipAccessRuleRepository.save(rule);
        ipAccessRuleRepository.delete(rule.getId());

        assertNull(ipAccessRuleRepository.findOne(rule.getId()));
    }

    @Test
    public void delete_WhenEntityNotExist_ExpectCorrectException() {
        final int fakeId = -1;
        assertNull(ipAccessRuleRepository.findOne(fakeId));
        assertThrows(EmptyResultDataAccessException.class, () -> ipAccessRuleRepository.delete(fakeId));
    }

}
