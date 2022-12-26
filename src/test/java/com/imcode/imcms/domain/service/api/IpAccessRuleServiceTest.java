package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.domain.dto.IpAccessRuleDTO;
import com.imcode.imcms.domain.dto.RoleDTO;
import com.imcode.imcms.domain.exception.IpAccessRuleValidationException;
import com.imcode.imcms.domain.service.IpAccessRuleService;
import com.imcode.imcms.domain.service.RoleService;
import com.imcode.imcms.model.IpAccessRule;
import com.imcode.imcms.model.Role;
import com.imcode.imcms.model.Roles;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@Transactional
public class IpAccessRuleServiceTest extends WebAppSpringTestConfig {
    private final static String IP_TEMPLATE_V6_SHORT = "2002:c0a8:165::";
    private final static String IP_RANGE_TEMPLATE_IP_V4 = "192.168.1.1-192.168.2.100";
    private final static String IP_V4_TEMPLATE_WITHIN_RANGE = "192.168.1.10";
    private final static String IP_V4_TEMPLATE_OUTSIDE_RANGE = "192.168.3.1";
    private final static String IP_ANOTHER_RANGE_TEMPLATE_IP_V4 = "192.168.4.1-192.168.5.100";
    private final static String IP_RANGE_TEMPLATE_IP_V6_SHORT = "2002:c0a8:101::-2002:c0a8:264::";
    private final static String IP_RANGE_TEMPLATE_IP_V6_FULL = "2002:c0a8:0101:0000:0000:0000:0000:0000-2002:c0a8:0264:0000:0000:0000:0000:0000";
    private final static String IP_V6_TEMPLATE_WITHIN_RANGE = "2002:c0a8:0101:0000:0000:0000:0000:0010";
    private final static String IP_V6_TEMPLATE_OUTSIDE_RANGE = "2002:c0a8:0265:0000:0000:0000:0000:0000";

    private final Role testRole = new RoleDTO("testRole");

    @Autowired
    private RoleService roleService;

    @Autowired
    private IpAccessRuleService accessRuleService;

    @Test
    public void create_WhenEntityNotExist_Expect_EntityCreated() {
        assertTrue(accessRuleService.getAll().isEmpty());

        IpAccessRule rule = new IpAccessRuleDTO();

        rule.setEnabled(true);
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V6_SHORT);
        rule.setRestricted(true);

        accessRuleService.create(rule);

        List<IpAccessRule> savedRules = accessRuleService.getAll();

        assertFalse(accessRuleService.getAll().isEmpty());
        assertEquals(1, savedRules.size());

        assertEquals(rule.getIpRange(), savedRules.get(0).getIpRange());
    }


    @Test
    public void create_WhenWrongIpRangeProvided_Expect_CorrectException() {
        assertTrue(accessRuleService.getAll().isEmpty());
        IpAccessRule rule = new IpAccessRuleDTO();

        rule.setEnabled(true);
        rule.setRestricted(true);

        rule.setIpRange("WrongRange");
        assertThrows(IpAccessRuleValidationException.class, () -> accessRuleService.create(rule));

        rule.setIpRange("1.1.1");
        assertThrows(IpAccessRuleValidationException.class, () -> accessRuleService.create(rule));

        rule.setIpRange("2.2.2.2-1");
        assertThrows(IpAccessRuleValidationException.class, () -> accessRuleService.create(rule));

        rule.setIpRange("300.300.11.2-1/2/");
        assertThrows(IpAccessRuleValidationException.class, () -> accessRuleService.create(rule));

        rule.setIpRange("a:::::");
        assertThrows(IpAccessRuleValidationException.class, () -> accessRuleService.create(rule));

        rule.setIpRange("125:257:0:2180-");
        assertThrows(IpAccessRuleValidationException.class, () -> accessRuleService.create(rule));

        rule.setIpRange("a:a:a:a:a--111.1.1.1");
        assertThrows(IpAccessRuleValidationException.class, () -> accessRuleService.create(rule));

        rule.setIpRange("2002:c0a8:101::-2002:c0a8:264:");
        assertThrows(IpAccessRuleValidationException.class, () -> accessRuleService.create(rule));

        assertTrue(accessRuleService.getAll().isEmpty());
    }

    @Test
    public void getById_WhenEntityExist_ExpectCorrectEntity() {
        assertTrue(accessRuleService.getAll().isEmpty());

        IpAccessRule rule = new IpAccessRuleDTO();

        rule.setEnabled(true);
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V6_SHORT);
        rule.setRestricted(true);

        IpAccessRule savedRule = accessRuleService.create(rule);

        IpAccessRule ruleToTest = accessRuleService.getById(savedRule.getId());

        assertFalse(accessRuleService.getAll().isEmpty());
        assertEquals(savedRule, ruleToTest);
    }

    @Test
    public void getAll_WhenEntitiesExist_ExpectCorrectEntitiesList() {
        assertTrue(accessRuleService.getAll().isEmpty());

        final int size = 6;
        for (int i = 0; i < size; i++) {
            final IpAccessRule rule = new IpAccessRuleDTO();
            rule.setEnabled(true);
            rule.setIpRange(i % 2 == 0 ? IP_RANGE_TEMPLATE_IP_V4 : IP_RANGE_TEMPLATE_IP_V6_FULL);
            rule.setRestricted(true);
            accessRuleService.create(rule);
        }

        final List<IpAccessRule> savedRules = accessRuleService.getAll();

        assertFalse(accessRuleService.getAll().isEmpty());
        assertEquals(size, savedRules.size());
    }

    @Test
    public void getAll_WhenNoEntitiesExist_ExpectEmptyList() {
        assertTrue(accessRuleService.getAll().isEmpty());
    }

    @Test
    public void update_WhenEntityExist_ExpectUpdatedEntity() {
        assertTrue(accessRuleService.getAll().isEmpty());

        IpAccessRule rule = new IpAccessRuleDTO();

        rule.setEnabled(true);
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V6_SHORT);
        rule.setRestricted(true);

        IpAccessRule savedRule = accessRuleService.create(rule);
        IpAccessRule ruleToTest = accessRuleService.getById(savedRule.getId());

        ruleToTest.setIpRange(IP_RANGE_TEMPLATE_IP_V6_FULL);

        assertFalse(accessRuleService.getAll().isEmpty());
        assertEquals(savedRule.getId(), ruleToTest.getId());
        assertEquals(IP_RANGE_TEMPLATE_IP_V6_FULL, ruleToTest.getIpRange());
        assertEquals(savedRule.isEnabled(), ruleToTest.isEnabled());
    }

    @Test
    public void delete_WhenEntityExist_ExpectEntityDeleted() {
        assertTrue(accessRuleService.getAll().isEmpty());

        IpAccessRule rule = new IpAccessRuleDTO();

        rule.setEnabled(true);
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V6_SHORT);
        rule.setRestricted(true);

        IpAccessRule savedRule = accessRuleService.create(rule);

        accessRuleService.delete(savedRule.getId());

        assertTrue(accessRuleService.getAll().isEmpty());
    }


    @Test
    public void delete_WhenEntityNotExist_ExpectCorrectException() {
        final int fakeId = -1;
        assertNull(accessRuleService.getById(fakeId));
        assertThrows(EmptyResultDataAccessException.class, () -> accessRuleService.delete(fakeId));
    }

    @Test
    public void isAllowedToAccess_WhenUserNotInRules_ExpectTrue() throws UnknownHostException {
        UserDomainObject defaultUser = new UserDomainObject(2);
        defaultUser.addRoleId(Roles.USER.getId());

        assertTrue(accessRuleService.isAllowedToAccess(Inet6Address.getByName(IP_TEMPLATE_V6_SHORT), defaultUser));
    }

    @Test
    public void isAllowedToAccess_When_RestrictedByUser_And_CurrentUserIsMatchedAndIsWithinRange_ExpectFalse() throws UnknownHostException {
        UserDomainObject defaultUser = new UserDomainObject(2);

        IpAccessRule rule = new IpAccessRuleDTO();
        rule.setEnabled(true);
        rule.setRestricted(true);
        rule.setUserId(defaultUser.getId());
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V4);

        accessRuleService.create(rule);

        assertFalse(accessRuleService.isAllowedToAccess(Inet4Address.getByName(IP_V4_TEMPLATE_WITHIN_RANGE), defaultUser));
    }

    @Test
    public void isAllowedToAccess_When_RestrictedRuleIsNotEnabled_And_UserIsWithinRange_ExpectTrue() throws UnknownHostException {
        UserDomainObject defaultUser = new UserDomainObject(2);

        IpAccessRule rule = new IpAccessRuleDTO();
        rule.setEnabled(false);
        rule.setRestricted(true);
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V4);

        accessRuleService.create(rule);

        assertTrue(accessRuleService.isAllowedToAccess(Inet4Address.getByName(IP_V4_TEMPLATE_WITHIN_RANGE), defaultUser));
    }

    @Test
    public void isAllowedToAccess_When_RestrictedByUser_And_CurrentUserIsNotMatchedAndIsWithinRange_ExpectTrue() throws UnknownHostException {
        UserDomainObject defaultUser = new UserDomainObject(2);
        UserDomainObject user5 = new UserDomainObject(3);

        IpAccessRule rule = new IpAccessRuleDTO();
        rule.setEnabled(true);
        rule.setRestricted(true);
        rule.setUserId(defaultUser.getId());
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V4);

        accessRuleService.create(rule);

        assertTrue(accessRuleService.isAllowedToAccess(Inet4Address.getByName(IP_V4_TEMPLATE_WITHIN_RANGE), user5));
    }

    @Test
    public void isAllowedToAccess_When_RestrictedByUser_And_CurrentUserIsMatchedAndIsNotWithinRange_ExpectTrue() throws UnknownHostException {
        UserDomainObject defaultUser = new UserDomainObject(2);

        IpAccessRule rule = new IpAccessRuleDTO();
        rule.setEnabled(true);
        rule.setRestricted(true);
        rule.setUserId(defaultUser.getId());
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V4);

        accessRuleService.create(rule);

        assertTrue(accessRuleService.isAllowedToAccess(Inet4Address.getByName(IP_V4_TEMPLATE_OUTSIDE_RANGE), defaultUser));
    }

    @Test
    public void isAllowedToAccess_When_RestrictedByRole_And_RoleIsMatchedAndIsWithinRange_ExpectFalse() throws UnknownHostException {
        final Role savedRole = roleService.save(testRole);

        UserDomainObject defaultUser = new UserDomainObject(2);
        defaultUser.addRoleId(savedRole.getId());

        IpAccessRule rule = new IpAccessRuleDTO();
        rule.setEnabled(true);
        rule.setRestricted(true);
        rule.setRoleId(savedRole.getId());
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V4);

        accessRuleService.create(rule);

        assertFalse(accessRuleService.isAllowedToAccess(Inet4Address.getByName(IP_V4_TEMPLATE_WITHIN_RANGE), defaultUser));
    }

    @Test
    public void isAllowedToAccess_When_RestrictedByRole_And_RoleIsNotMatchedAndIsWithinRange_ExpectTrue() throws UnknownHostException {
        final Role savedRole = roleService.save(testRole);

        UserDomainObject defaultUser = new UserDomainObject(2);
        defaultUser.addRoleId(Roles.USER.getId());

        IpAccessRule rule = new IpAccessRuleDTO();
        rule.setEnabled(true);
        rule.setRestricted(true);
        rule.setRoleId(savedRole.getId());
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V4);

        accessRuleService.create(rule);

        assertTrue(accessRuleService.isAllowedToAccess(Inet4Address.getByName(IP_V4_TEMPLATE_WITHIN_RANGE), defaultUser));
    }

    @Test
    public void isAllowedToAccess_When_RestrictedByRole_And_RoleIsMatchedAndIsNotWithinRange_ExpectTrue() throws UnknownHostException {
        final Role savedRole = roleService.save(testRole);

        UserDomainObject defaultUser = new UserDomainObject(2);
        defaultUser.addRoleId(savedRole.getId());

        IpAccessRule rule = new IpAccessRuleDTO();
        rule.setEnabled(true);
        rule.setRestricted(true);
        rule.setRoleId(savedRole.getId());
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V4);

        accessRuleService.create(rule);

        assertTrue(accessRuleService.isAllowedToAccess(Inet4Address.getByName(IP_V4_TEMPLATE_OUTSIDE_RANGE), defaultUser));
    }

    @Test
    public void isAllowedToAccess_When_RestrictedByDefault_UserIsWithinRange_ExpectFalse() throws UnknownHostException {
        UserDomainObject defaultUser = new UserDomainObject(2);

        IpAccessRule rule = new IpAccessRuleDTO();
        rule.setEnabled(true);
        rule.setRestricted(true);
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V4);

        accessRuleService.create(rule);

        assertFalse(accessRuleService.isAllowedToAccess(Inet4Address.getByName(IP_V4_TEMPLATE_WITHIN_RANGE), defaultUser));
    }

    @Test
    public void isAllowedToAccess_When_RestrictedByDefault_UserIsNotWithinRange_ExpectTrue() throws UnknownHostException {
        UserDomainObject defaultUser = new UserDomainObject(2);

        IpAccessRule rule = new IpAccessRuleDTO();
        rule.setEnabled(true);
        rule.setRestricted(true);
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V4);

        accessRuleService.create(rule);

        assertTrue(accessRuleService.isAllowedToAccess(Inet4Address.getByName(IP_V4_TEMPLATE_OUTSIDE_RANGE), defaultUser));
    }

    @Test
    public void isAllowedToAccess_When_FirstRestrictedRuleIsAllowed_And_SecondRestrictedRuleIsNowAllowed_ExpectFalse() throws UnknownHostException {
        final Role savedRole = roleService.save(testRole);

        UserDomainObject defaultUser = new UserDomainObject(2);
        UserDomainObject user5 = new UserDomainObject(5);
        user5.addRoleId(savedRole.getId());

        InetAddress userIpAddress = Inet4Address.getByName(IP_V4_TEMPLATE_WITHIN_RANGE);

        IpAccessRule allowedRule = new IpAccessRuleDTO();
        allowedRule.setEnabled(true);
        allowedRule.setRestricted(true);
        allowedRule.setUserId(defaultUser.getId());
        allowedRule.setIpRange(IP_RANGE_TEMPLATE_IP_V4);

        accessRuleService.create(allowedRule);

        assertTrue(accessRuleService.isAllowedToAccess(userIpAddress, user5));

        IpAccessRule notAllowedRule = new IpAccessRuleDTO();
        notAllowedRule.setEnabled(true);
        notAllowedRule.setRestricted(true);
        notAllowedRule.setRoleId(savedRole.getId());
        notAllowedRule.setIpRange(IP_RANGE_TEMPLATE_IP_V4);

        accessRuleService.create(notAllowedRule);

        assertEquals(2, accessRuleService.getAll().size());

        assertFalse(accessRuleService.isAllowedToAccess(userIpAddress, user5));
    }

    @Test
    public void isAllowedToAccess_When_UnrestrictedByDefault_And_UserIsWithinRange_ExpectTrue() throws UnknownHostException {
        UserDomainObject defaultUser = new UserDomainObject(2);

        IpAccessRule rule = new IpAccessRuleDTO();
        rule.setEnabled(true);
        rule.setRestricted(false);
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V4);

        accessRuleService.create(rule);

        assertTrue(accessRuleService.isAllowedToAccess(Inet4Address.getByName(IP_V4_TEMPLATE_WITHIN_RANGE), defaultUser));
    }

    @Test
    public void isAllowedToAccess_When_UnrestrictedRuleIsNotEnabled_And_UserIsNotWithinRange_ExpectFalse() throws UnknownHostException {
        UserDomainObject defaultUser = new UserDomainObject(2);

        IpAccessRule rule = new IpAccessRuleDTO();
        rule.setEnabled(false);
        rule.setRestricted(false);
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V4);

        accessRuleService.create(rule);

        assertTrue(accessRuleService.isAllowedToAccess(Inet4Address.getByName(IP_V4_TEMPLATE_WITHIN_RANGE), defaultUser));
    }

    @Test
    public void isAllowedToAccess_When_UnrestrictedByDefault_And_UserIsNotWithinRange_ExpectFalse() throws UnknownHostException {
        UserDomainObject defaultUser = new UserDomainObject(2);

        IpAccessRule rule = new IpAccessRuleDTO();
        rule.setEnabled(true);
        rule.setRestricted(false);
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V4);

        accessRuleService.create(rule);

        assertFalse(accessRuleService.isAllowedToAccess(Inet4Address.getByName(IP_V4_TEMPLATE_OUTSIDE_RANGE), defaultUser));
    }

    @Test
    public void isAllowedAccess_When_UnrestrictedByUser_And_CurrentUserIsMatchedAndIsWithinRange_ExpectTrue() throws UnknownHostException {
        UserDomainObject defaultUser = new UserDomainObject(2);

        IpAccessRule rule = new IpAccessRuleDTO();
        rule.setEnabled(true);
        rule.setRestricted(false);
        rule.setUserId(defaultUser.getId());
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V4);

        accessRuleService.create(rule);

        assertTrue(accessRuleService.isAllowedToAccess(Inet4Address.getByName(IP_V4_TEMPLATE_WITHIN_RANGE), defaultUser));
    }

    @Test
    public void isAllowedAccess_When_UnrestrictedByUser_And_CurrentUserIsNotMatchedAndIsNotWithinRange_ExpectTrue() throws UnknownHostException {
        UserDomainObject defaultUser = new UserDomainObject(2);
        UserDomainObject user5 = new UserDomainObject(5);

        IpAccessRule rule = new IpAccessRuleDTO();
        rule.setEnabled(true);
        rule.setRestricted(false);
        rule.setUserId(defaultUser.getId());
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V4);

        accessRuleService.create(rule);

        assertTrue(accessRuleService.isAllowedToAccess(Inet4Address.getByName(IP_V4_TEMPLATE_OUTSIDE_RANGE), user5));
    }

    @Test
    public void isAllowedAccess_When_UnrestrictedByUser_And_CurrentUserIsMatchedAndIsNotWithinRange_ExpectFalse() throws UnknownHostException {
        UserDomainObject defaultUser = new UserDomainObject(2);

        IpAccessRule rule = new IpAccessRuleDTO();
        rule.setEnabled(true);
        rule.setRestricted(false);
        rule.setUserId(defaultUser.getId());
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V4);

        accessRuleService.create(rule);

        assertFalse(accessRuleService.isAllowedToAccess(Inet4Address.getByName(IP_V4_TEMPLATE_OUTSIDE_RANGE), defaultUser));
    }

    @Test
    public void isAllowedAccess_When_UnrestrictedByRole_And_RoleIsMatchedAndIsWithinRange_ExpectTrue() throws UnknownHostException {
        final Role savedRole = roleService.save(testRole);

        UserDomainObject defaultUser = new UserDomainObject(2);
        defaultUser.addRoleId(savedRole.getId());

        IpAccessRule rule = new IpAccessRuleDTO();
        rule.setEnabled(true);
        rule.setRestricted(false);
        rule.setRoleId(savedRole.getId());
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V4);

        accessRuleService.create(rule);

        assertTrue(accessRuleService.isAllowedToAccess(Inet4Address.getByName(IP_V4_TEMPLATE_WITHIN_RANGE), defaultUser));
    }

    @Test
    public void isAllowedAccess_When_UnrestrictedByUser_And_RoleIsNotMatchedAndIsNotWithinRange_ExpectFalse() throws UnknownHostException {
        final Role savedRole = roleService.save(testRole);

        UserDomainObject defaultUser = new UserDomainObject(2);
        defaultUser.addRoleId(Roles.USER.getId());

        IpAccessRule rule = new IpAccessRuleDTO();
        rule.setEnabled(true);
        rule.setRestricted(false);
        rule.setRoleId(savedRole.getId());
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V4);

        accessRuleService.create(rule);

        assertTrue(accessRuleService.isAllowedToAccess(Inet4Address.getByName(IP_V4_TEMPLATE_OUTSIDE_RANGE), defaultUser));
    }

    @Test
    public void isAllowedAccess_When_UnrestrictedByUser_And_RoleIsMatchedAndIsNotWithinRange_ExpectFalse() throws UnknownHostException {
        final Role savedRole = roleService.save(testRole);

        UserDomainObject defaultUser = new UserDomainObject(2);
        defaultUser.addRoleId(savedRole.getId());

        IpAccessRule rule = new IpAccessRuleDTO();
        rule.setEnabled(true);
        rule.setRestricted(false);
        rule.setRoleId(savedRole.getId());
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V4);

        accessRuleService.create(rule);

        assertFalse(accessRuleService.isAllowedToAccess(Inet4Address.getByName(IP_V4_TEMPLATE_OUTSIDE_RANGE), defaultUser));
    }

    @Test
    public void isAllowedAccess_When_FirstUnrestrictedByDefaultIsAllowed_And_SecondUnrestrictedByUserIsNotAllowed_ExpectFalse() throws UnknownHostException {
        UserDomainObject defaultUser = new UserDomainObject(2);
        InetAddress userIpAddress = Inet4Address.getByName(IP_V4_TEMPLATE_WITHIN_RANGE);

        IpAccessRule allowedRuleByDefault = new IpAccessRuleDTO();
        allowedRuleByDefault.setEnabled(true);
        allowedRuleByDefault.setRestricted(false);
        allowedRuleByDefault.setIpRange(IP_RANGE_TEMPLATE_IP_V4);

        accessRuleService.create(allowedRuleByDefault);

        assertTrue(accessRuleService.isAllowedToAccess(userIpAddress, defaultUser));

        IpAccessRule notAllowedRuleByUser = new IpAccessRuleDTO();
        notAllowedRuleByUser.setEnabled(true);
        notAllowedRuleByUser.setRestricted(false);
        notAllowedRuleByUser.setUserId(defaultUser.getId());
        notAllowedRuleByUser.setIpRange(IP_ANOTHER_RANGE_TEMPLATE_IP_V4);

        accessRuleService.create(notAllowedRuleByUser);

        assertEquals(2, accessRuleService.getAll().size());

        assertFalse(accessRuleService.isAllowedToAccess(userIpAddress, defaultUser));
    }

    @Test
    public void isAllowedAccess_When_FirstUnrestrictedByDefaultIsNotAllowed_And_SecondUnrestrictedByUserIsAllowed_ExpectTrue() throws UnknownHostException {
        UserDomainObject defaultUser = new UserDomainObject(2);
        InetAddress userIpAddress = Inet4Address.getByName(IP_V4_TEMPLATE_WITHIN_RANGE);

        IpAccessRule notAllowedRuleByDefault = new IpAccessRuleDTO();
        notAllowedRuleByDefault.setEnabled(true);
        notAllowedRuleByDefault.setRestricted(false);
        notAllowedRuleByDefault.setIpRange(IP_ANOTHER_RANGE_TEMPLATE_IP_V4);

        accessRuleService.create(notAllowedRuleByDefault);

        assertFalse(accessRuleService.isAllowedToAccess(userIpAddress, defaultUser));

        IpAccessRule allowedRuleByUser = new IpAccessRuleDTO();
        allowedRuleByUser.setEnabled(true);
        allowedRuleByUser.setRestricted(false);
        allowedRuleByUser.setUserId(defaultUser.getId());
        allowedRuleByUser.setIpRange(IP_RANGE_TEMPLATE_IP_V4);

        accessRuleService.create(allowedRuleByUser);

        assertEquals(2, accessRuleService.getAll().size());

        assertTrue(accessRuleService.isAllowedToAccess(userIpAddress, defaultUser));
    }

    @Test
    public void isAllowedAccess_When_FirstUnrestrictedByDefaultIsAllowed_And_SecondUnrestrictedByRoleIsNotAllowed_ExpectFalse() throws UnknownHostException {
        final Role savedRole = roleService.save(testRole);

        UserDomainObject user5 = new UserDomainObject(5);
        user5.addRoleId(savedRole.getId());
        InetAddress userIpAddress = Inet4Address.getByName(IP_V4_TEMPLATE_WITHIN_RANGE);

        IpAccessRule allowedRuleByDefault = new IpAccessRuleDTO();
        allowedRuleByDefault.setEnabled(true);
        allowedRuleByDefault.setRestricted(false);
        allowedRuleByDefault.setIpRange(IP_RANGE_TEMPLATE_IP_V4);

        accessRuleService.create(allowedRuleByDefault);

        assertTrue(accessRuleService.isAllowedToAccess(userIpAddress, user5));

        IpAccessRule notAllowedRuleByRole = new IpAccessRuleDTO();
        notAllowedRuleByRole.setEnabled(true);
        notAllowedRuleByRole.setRestricted(false);
        notAllowedRuleByRole.setRoleId(savedRole.getId());
        notAllowedRuleByRole.setIpRange(IP_ANOTHER_RANGE_TEMPLATE_IP_V4);

        accessRuleService.create(notAllowedRuleByRole);

        assertEquals(2, accessRuleService.getAll().size());

        assertFalse(accessRuleService.isAllowedToAccess(userIpAddress, user5));
    }

    @Test
    public void isAllowedAccess_When_FirstUnrestrictedByDefaultIsNotAllowed_And_SecondUnrestrictedByRoleIsAllowed_ExpectTrue() throws UnknownHostException {
        final Role savedRole = roleService.save(testRole);

        UserDomainObject defaultUser = new UserDomainObject(2);
        defaultUser.addRoleId(savedRole.getId());
        InetAddress userIpAddress = Inet4Address.getByName(IP_V4_TEMPLATE_WITHIN_RANGE);

        IpAccessRule notAllowedRuleByDefault = new IpAccessRuleDTO();
        notAllowedRuleByDefault.setEnabled(true);
        notAllowedRuleByDefault.setRestricted(false);
        notAllowedRuleByDefault.setIpRange(IP_ANOTHER_RANGE_TEMPLATE_IP_V4);

        accessRuleService.create(notAllowedRuleByDefault);

        assertFalse(accessRuleService.isAllowedToAccess(userIpAddress, defaultUser));

        IpAccessRule allowedRuleByRole = new IpAccessRuleDTO();
        allowedRuleByRole.setEnabled(true);
        allowedRuleByRole.setRestricted(false);
        allowedRuleByRole.setRoleId(savedRole.getId());
        allowedRuleByRole.setIpRange(IP_RANGE_TEMPLATE_IP_V4);

        accessRuleService.create(allowedRuleByRole);

        assertEquals(2, accessRuleService.getAll().size());

        assertTrue(accessRuleService.isAllowedToAccess(userIpAddress, defaultUser));
    }

    @Test
    public void isAllowedAccess_When_UnrestrictedIsAllowed_And_RestrictedIsNotAllowed_ExpectFalse() throws UnknownHostException {
        final Role savedRole = roleService.save(testRole);

        UserDomainObject defaultUser = new UserDomainObject(2);
        defaultUser.addRoleId(savedRole.getId());
        InetAddress userIpAddress = Inet4Address.getByName(IP_V4_TEMPLATE_WITHIN_RANGE);

        IpAccessRule allowedUnrestrictedRule = new IpAccessRuleDTO();
        allowedUnrestrictedRule.setEnabled(true);
        allowedUnrestrictedRule.setRestricted(false);
        allowedUnrestrictedRule.setRoleId(savedRole.getId());
        allowedUnrestrictedRule.setIpRange(IP_RANGE_TEMPLATE_IP_V4);

        accessRuleService.create(allowedUnrestrictedRule);

        assertTrue(accessRuleService.isAllowedToAccess(userIpAddress, defaultUser));

        IpAccessRule notAllowedRestrictedRule = new IpAccessRuleDTO();
        notAllowedRestrictedRule.setEnabled(true);
        notAllowedRestrictedRule.setRestricted(true);
        notAllowedRestrictedRule.setUserId(defaultUser.getId());
        notAllowedRestrictedRule.setIpRange(IP_RANGE_TEMPLATE_IP_V4);

        accessRuleService.create(notAllowedRestrictedRule);

        assertEquals(2, accessRuleService.getAll().size());

        assertFalse(accessRuleService.isAllowedToAccess(userIpAddress, defaultUser));
    }

    @Test
    public void isAllowedAccess_When_FirstRestrictedIsAllowed_And_SecondUnrestrictedIsNotAllowed_ExpectFalse() throws UnknownHostException {
        final Role savedRole = roleService.save(testRole);

        UserDomainObject defaultUser = new UserDomainObject(2);
        InetAddress userIpAddress = Inet4Address.getByName(IP_V4_TEMPLATE_OUTSIDE_RANGE);

        IpAccessRule allowedRestrictedRule = new IpAccessRuleDTO();
        allowedRestrictedRule.setEnabled(true);
        allowedRestrictedRule.setRestricted(true);
        allowedRestrictedRule.setRoleId(savedRole.getId());
        allowedRestrictedRule.setIpRange(IP_RANGE_TEMPLATE_IP_V4);

        accessRuleService.create(allowedRestrictedRule);

        assertTrue(accessRuleService.isAllowedToAccess(userIpAddress, defaultUser));

        IpAccessRule notAllowedUnrestrictedRule = new IpAccessRuleDTO();
        notAllowedUnrestrictedRule.setEnabled(true);
        notAllowedUnrestrictedRule.setRestricted(false);
        notAllowedUnrestrictedRule.setUserId(defaultUser.getId());
        notAllowedUnrestrictedRule.setIpRange(IP_ANOTHER_RANGE_TEMPLATE_IP_V4);

        accessRuleService.create(notAllowedUnrestrictedRule);

        assertEquals(2, accessRuleService.getAll().size());

        assertFalse(accessRuleService.isAllowedToAccess(userIpAddress, defaultUser));
    }

    @Test
    public void isAllowedToAccess_When_RestrictedByIpv6RangeInFullForm_And_UserIsWithinRange_ExpectFalse() throws UnknownHostException {
        UserDomainObject defaultUser = new UserDomainObject(2);

        IpAccessRule rule = new IpAccessRuleDTO();
        rule.setEnabled(true);
        rule.setRestricted(true);
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V6_FULL);

        accessRuleService.create(rule);

        assertFalse(accessRuleService.isAllowedToAccess(Inet6Address.getByName(IP_V6_TEMPLATE_WITHIN_RANGE), defaultUser));
    }

    @Test
    public void isAllowedToAccess_When_RestrictedByIpv6RangeInFullForm_And_UserIsNotWithinRange_ExpectTrue() throws UnknownHostException {
        UserDomainObject defaultUser = new UserDomainObject(2);

        IpAccessRule rule = new IpAccessRuleDTO();
        rule.setEnabled(true);
        rule.setRestricted(true);
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V6_FULL);

        accessRuleService.create(rule);

        assertTrue(accessRuleService.isAllowedToAccess(Inet6Address.getByName(IP_V6_TEMPLATE_OUTSIDE_RANGE), defaultUser));
    }

    @Test
    public void isAllowedToAccess_When_RestrictedByIpv6RangeInShortForm_And_UserIsWithinRange_ExpectFalse() throws UnknownHostException {
        UserDomainObject defaultUser = new UserDomainObject(2);

        IpAccessRule rule = new IpAccessRuleDTO();
        rule.setEnabled(true);
        rule.setRestricted(true);
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V6_SHORT);

        accessRuleService.create(rule);

        assertFalse(accessRuleService.isAllowedToAccess(Inet6Address.getByName(IP_V6_TEMPLATE_WITHIN_RANGE), defaultUser));
    }

    @Test
    public void isAllowedToAccess_When_RestrictedByIpv6RangeInShortForm_And_UserIsNotWithinRange_ExpectTrue() throws UnknownHostException {
        UserDomainObject defaultUser = new UserDomainObject(2);

        IpAccessRule rule = new IpAccessRuleDTO();
        rule.setEnabled(true);
        rule.setRestricted(true);
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V6_SHORT);

        accessRuleService.create(rule);

        assertTrue(accessRuleService.isAllowedToAccess(Inet6Address.getByName(IP_V6_TEMPLATE_OUTSIDE_RANGE), defaultUser));
    }

    @Test
    public void isAllowedToAccess_When_RestrictedByIpv4Range_And_UserHasIpv6_ExpectTrue() throws UnknownHostException {
        UserDomainObject defaultUser = new UserDomainObject(2);

        IpAccessRule rule = new IpAccessRuleDTO();
        rule.setEnabled(true);
        rule.setRestricted(true);
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V4);

        accessRuleService.create(rule);

        assertTrue(accessRuleService.isAllowedToAccess(Inet6Address.getByName(IP_V6_TEMPLATE_WITHIN_RANGE), defaultUser));
    }

    @Test
    public void isAllowedToAccess_When_UnrestrictedByIpv4Range_And_UserHasIpv6_ExpectFalse() throws UnknownHostException {
        UserDomainObject defaultUser = new UserDomainObject(2);

        IpAccessRule rule = new IpAccessRuleDTO();
        rule.setEnabled(true);
        rule.setRestricted(false);
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V4);

        accessRuleService.create(rule);

        assertFalse(accessRuleService.isAllowedToAccess(Inet6Address.getByName(IP_V6_TEMPLATE_WITHIN_RANGE), defaultUser));
    }

    @Test
    public void isAllowedToAccess_When_RestrictedByIpv6Range_And_UserHasIpv4_ExpectTrue() throws UnknownHostException {
        UserDomainObject defaultUser = new UserDomainObject(2);

        IpAccessRule rule = new IpAccessRuleDTO();
        rule.setEnabled(true);
        rule.setRestricted(true);
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V6_FULL);

        accessRuleService.create(rule);

        assertTrue(accessRuleService.isAllowedToAccess(Inet6Address.getByName(IP_V4_TEMPLATE_WITHIN_RANGE), defaultUser));
    }

    @Test
    public void isAllowedToAccess_When_UnrestrictedByIpv6Range_And_UserHasIpv4_ExpectFalse() throws UnknownHostException {
        UserDomainObject defaultUser = new UserDomainObject(2);

        IpAccessRule rule = new IpAccessRuleDTO();
        rule.setEnabled(true);
        rule.setRestricted(false);
        rule.setIpRange(IP_RANGE_TEMPLATE_IP_V6_FULL);

        accessRuleService.create(rule);

        assertFalse(accessRuleService.isAllowedToAccess(Inet6Address.getByName(IP_V4_TEMPLATE_WITHIN_RANGE), defaultUser));
    }

    @Test
    public void isAllowedToAccess_When_RestrictedBySingleIpv4_ExpectFalse() throws UnknownHostException {
        String singleIpv4Template = IP_V4_TEMPLATE_WITHIN_RANGE;

        UserDomainObject defaultUser = new UserDomainObject(2);

        IpAccessRule rule = new IpAccessRuleDTO();
        rule.setEnabled(true);
        rule.setRestricted(true);
        rule.setIpRange(singleIpv4Template);

        accessRuleService.create(rule);

        assertFalse(accessRuleService.isAllowedToAccess(Inet6Address.getByName(singleIpv4Template), defaultUser));
    }

    @Test
    public void isAllowedToAccess_When_UnrestrictedBySingleIpv4_ExpectTrue() throws UnknownHostException {
        String singleIpv4Template = IP_V4_TEMPLATE_WITHIN_RANGE;

        UserDomainObject defaultUser = new UserDomainObject(2);

        IpAccessRule rule = new IpAccessRuleDTO();
        rule.setEnabled(true);
        rule.setRestricted(false);
        rule.setIpRange(singleIpv4Template);

        accessRuleService.create(rule);

        assertTrue(accessRuleService.isAllowedToAccess(Inet6Address.getByName(singleIpv4Template), defaultUser));
    }

    @Test
    public void isAllowedToAccess_When_RestrictedBySingleIpv6_ExpectFalse() throws UnknownHostException {
        String singleIpv6Template = IP_V6_TEMPLATE_WITHIN_RANGE;

        UserDomainObject defaultUser = new UserDomainObject(2);

        IpAccessRule rule = new IpAccessRuleDTO();
        rule.setEnabled(true);
        rule.setRestricted(true);
        rule.setIpRange(singleIpv6Template);

        accessRuleService.create(rule);

        assertFalse(accessRuleService.isAllowedToAccess(Inet6Address.getByName(singleIpv6Template), defaultUser));
    }

    @Test
    public void isAllowedToAccess_When_UnrestrictedBySingleIpv6_ExpectTrue() throws UnknownHostException {
        String singleIpv6Template = IP_V6_TEMPLATE_WITHIN_RANGE;

        UserDomainObject defaultUser = new UserDomainObject(2);

        IpAccessRule rule = new IpAccessRuleDTO();
        rule.setEnabled(true);
        rule.setRestricted(false);
        rule.setIpRange(singleIpv6Template);

        accessRuleService.create(rule);

        assertTrue(accessRuleService.isAllowedToAccess(Inet6Address.getByName(singleIpv6Template), defaultUser));
    }

}
