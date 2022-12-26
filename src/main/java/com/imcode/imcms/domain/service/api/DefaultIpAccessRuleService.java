package com.imcode.imcms.domain.service.api;

import com.googlecode.ipv6.IPv6Address;
import com.googlecode.ipv6.IPv6AddressRange;
import com.imcode.imcms.domain.component.AccessRuleValidationActionConsumer;
import com.imcode.imcms.domain.dto.IpAccessRuleDTO;
import com.imcode.imcms.domain.service.IpAccessRuleService;
import com.imcode.imcms.model.IpAccessRule;
import com.imcode.imcms.persistence.entity.IpAccessRuleJPA;
import com.imcode.imcms.persistence.repository.IpAccessRuleRepository;
import imcode.server.user.UserDomainObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DefaultIpAccessRuleService implements IpAccessRuleService {
    private static final Logger LOG = LogManager.getLogger(DefaultIpAccessRuleService.class);

    private final IpAccessRuleRepository ipAccessRuleRepository;
    private final AccessRuleValidationActionConsumer ruleValidation;
    private final ModelMapper modelMapper;

    DefaultIpAccessRuleService(IpAccessRuleRepository ipAccessRuleRepository, AccessRuleValidationActionConsumer ruleValidation, ModelMapper modelMapper) {
        this.ipAccessRuleRepository = ipAccessRuleRepository;
        this.ruleValidation = ruleValidation;
        this.modelMapper = modelMapper;
    }

    @Override
    public IpAccessRule getById(int id) {
	    return ipAccessRuleRepository.findById(id).map(IpAccessRuleDTO::new).orElse(null);
    }

    @Override
    public List<IpAccessRule> getAll() {
        return ipAccessRuleRepository.findAll().stream()
                .map(IpAccessRuleDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public IpAccessRule create(IpAccessRule rule) {

        return new IpAccessRuleDTO(
                ruleValidation.doIfValid(rule,
                        validatedRule -> ipAccessRuleRepository.save(modelMapper.map(rule, IpAccessRuleJPA.class))
                )
        );
    }

    @Override
    public IpAccessRule update(IpAccessRule rule) {
        return new IpAccessRuleDTO(
                ruleValidation.doIfValid(rule,
                        validatedRule -> ipAccessRuleRepository.save(modelMapper.map(rule, IpAccessRuleJPA.class))
                )
        );
    }

    @Override
    public void delete(int ruleId) {
	    ipAccessRuleRepository.deleteById(ruleId);
    }

    @Override
    public boolean isAllowedToAccess(InetAddress accessIp, UserDomainObject user) {
        final List<IpAccessRuleDTO> rules = ipAccessRuleRepository.findAll().stream()
                .filter(IpAccessRuleJPA::isEnabled)
                .map(IpAccessRuleDTO::new)
                .collect(Collectors.toList());

        final boolean isRestricted = rules.stream()
                .filter(IpAccessRuleDTO::isRestricted)
                .anyMatch(rule -> {
                    if (isIpInRange(accessIp, rule.getIpRange())) {
                        if (rule.getUserId() != null || rule.getRoleId() != null) {
                            return user.getId().equals(rule.getUserId()) || user.hasRoleId(rule.getRoleId());
                        } else {
                            return true;
                        }
                    }

                    return false;
                });

        if (isRestricted) {
            return false;
        }


        final List<IpAccessRuleDTO> whiteListRules = rules.stream()
                .filter(rule -> !rule.isRestricted())
                .collect(Collectors.toList());

        final List<IpAccessRuleDTO> whiteListRulesWithCurrentUserOrRoles = whiteListRules.stream().filter(rule ->
                (user.getId().equals(rule.getUserId()) || user.hasRoleId(rule.getRoleId()))).collect(Collectors.toList());

        if(!whiteListRulesWithCurrentUserOrRoles.isEmpty()){
            return whiteListRulesWithCurrentUserOrRoles.stream().allMatch(rule ->
                    isIpInRange(accessIp, rule.getIpRange())
            );
        }else{
            return whiteListRules.stream()
                    .noneMatch(rule ->
                            (rule.getUserId() == null && rule.getRoleId() == null) &&
                                    !isIpInRange(accessIp, rule.getIpRange()));
        }
    }

    private boolean isIpInRange(InetAddress ipToCheck, String ipRange) {
        List<String> ipRangeList = Arrays.asList(ipRange.split("-"));

        String ipRangeBottom = ipRangeList.get(0);
        String ipRangeTop = ipRangeList.size() > 1 ? ipRangeList.get(1) : ipRangeBottom;

        // Don't check the range if the id form of the rule and the user don't match
        if (!(ipRangeBottom.contains(".") && ipToCheck.toString().contains(".")) &&
                !(ipRangeBottom.contains(":") && ipToCheck.toString().contains(":"))) {
            return false;
        }

        try {
            if(ipRangeBottom.contains(".")){    //ipv4
                long ipTop = ipToLong(Inet4Address.getByName(ipRangeBottom));
                long ipBottom = ipToLong(Inet4Address.getByName(ipRangeTop));
                long ipToTest = ipToLong(ipToCheck);

                return (ipToTest >= ipTop && ipToTest <= ipBottom);
            }else{                              //ipv6
                IPv6Address ipv6RangeBottomAddress = IPv6Address.fromString(ipRangeBottom);
                IPv6Address ipv6RangeTopAddress = IPv6Address.fromString(ipRangeTop);
                IPv6Address ipv6RangeToCheckAddress = IPv6Address.fromInetAddress(ipToCheck);

                return IPv6AddressRange.fromFirstAndLast(ipv6RangeBottomAddress, ipv6RangeTopAddress)
                        .contains(ipv6RangeToCheckAddress);
            }
        } catch (UnknownHostException | IllegalArgumentException e) {
            LOG.debug("Can't parse IP address", e);
            return false;
        }
    }

    private long ipToLong(InetAddress ip) {
        byte[] octets = ip.getAddress();
        long result = 0;
        for (byte octet : octets) {
            result <<= 8;
            result |= octet & 0xff;
        }
        return result;
    }

}
