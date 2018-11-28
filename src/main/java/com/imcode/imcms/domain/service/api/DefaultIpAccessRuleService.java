package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.IpAccessRuleDTO;
import com.imcode.imcms.domain.service.IpAccessRuleService;
import com.imcode.imcms.model.IpAccessRule;
import com.imcode.imcms.persistence.entity.IpAccessRuleJPA;
import com.imcode.imcms.persistence.repository.IpAccessRuleRepository;
import imcode.server.user.UserDomainObject;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class DefaultIpAccessRuleService implements IpAccessRuleService {
    private static final Logger LOG = Logger.getLogger(DefaultIpAccessRuleService.class);

    private final IpAccessRuleRepository ipAccessRuleRepository;

    DefaultIpAccessRuleService(IpAccessRuleRepository ipAccessRuleRepository) {
        this.ipAccessRuleRepository = ipAccessRuleRepository;
    }

    @Override
    public IpAccessRule getById(int id) {
        final IpAccessRuleJPA rule = ipAccessRuleRepository.findOne(id);

        return (null == rule) ? null : new IpAccessRuleDTO(rule);
    }

    @Override
    public List<IpAccessRule> getAll() {
        return ipAccessRuleRepository.findAll().stream()
                .map(IpAccessRuleDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public IpAccessRule create(IpAccessRule rule) {
        return new IpAccessRuleDTO(ipAccessRuleRepository.save(new IpAccessRuleJPA(rule)));
    }

    @Override
    public IpAccessRule update(IpAccessRule rule) {
        return new IpAccessRuleDTO(ipAccessRuleRepository.save(new IpAccessRuleJPA(rule)));
    }

    @Override
    public void delete(int ruleId) {
        ipAccessRuleRepository.delete(ruleId);
    }

    @Override
    public boolean isAllowedToAccess(InetAddress accessIp, UserDomainObject user) {
        boolean isAllowed = !ipAccessRuleRepository.findAll().stream()
                .filter(IpAccessRule::isEnabled)
                .filter(rule -> Objects.equals(rule.getUserId(), user.getId())
                        || user.getRoleIds().contains(rule.getRoleId())
                        || isIpInRange(accessIp, rule.getIpRange()))
                .min(Comparator.comparing(IpAccessRuleJPA::isRestricted))
                .orElseGet(IpAccessRuleJPA::new)
                .isRestricted();

        return isAllowed;
    }

    private boolean isIpInRange(InetAddress ipToCheck, String ipv6Range) {
        List<String> ipRange = Arrays.asList(ipv6Range.split("-"));
        try {
            InetAddress rangeBottom = null;
            rangeBottom = getInetAddressFromIpString(ipRange.get(0));

            InetAddress rangeTop;
            if (ipRange.size() > 1) {
                rangeTop = getInetAddressFromIpString(ipRange.get(1));
            } else {
                rangeTop = rangeBottom;
            }

            long ipTop = ipToLong(rangeBottom);
            long ipBottom = ipToLong(rangeTop);
            long ipToTest = ipToLong(ipToCheck);
            return (ipToTest >= ipTop && ipToTest <= ipBottom);
        } catch (UnknownHostException e) {
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

    private InetAddress getInetAddressFromIpString(String ipString) throws UnknownHostException {
        final boolean isIPv4 = ipString.contains(".");
        return isIPv4 ? Inet4Address.getByName(ipString) : Inet6Address.getByName(ipString);
    }


}
