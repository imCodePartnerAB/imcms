package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.IpAccessRuleDTO;
import com.imcode.imcms.domain.service.IpAccessRuleService;
import com.imcode.imcms.model.IpAccessRule;
import com.imcode.imcms.persistence.entity.IpAccessRuleJPA;
import com.imcode.imcms.persistence.repository.IpAccessRuleRepository;
import imcode.server.user.UserDomainObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DefaultIpAccessRuleService implements IpAccessRuleService {

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
        List<IpAccessRuleJPA> foundRules = ipAccessRuleRepository.findAll().stream()
                .filter(IpAccessRule::isEnabled)
                .filter(rule -> {
                    return rule.getUserId().equals(user.getId())
                            || user.getRoleIds().contains(rule.getRoleId())
                            || isIpInRange(accessIp, rule.getIpRange());
                })
                .collect(Collectors.toList());

        return false;
    }

    private boolean isIpInRange(InetAddress ipToCheck, String ipv6Range) {

        List<String> ipRange = Arrays.asList(ipv6Range.split("-"));


        Inet6Address rangeBottom = null;
//        try {
////            rangeBottom = Inet6Address.getByName(ipRange.get(0));
////
////            Inet6Address rangeTop = ipRange.size() > 0 ? Inet6Address.getByName(ipRange.get(0)) : rangeBottom;
////
////            long ipLo = ipToLong(rangeBottom);
////            long ipHi = ipToLong(rangeTop);
////            long ipToTest = ipToLong(InetAddress.getByName(ipToCheck));
////            return (ipToTest >= ipLo && ipToTest <= ipHi);
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//            return false;
//        }
                    return false;

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
