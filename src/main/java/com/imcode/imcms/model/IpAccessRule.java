package com.imcode.imcms.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public abstract class IpAccessRule implements Serializable {
    private static final long serialVersionUID = -7136110444914457556L;

    private Integer id;
    private boolean isEnabled;
    private boolean isRestricted;
    private String ipRange;
    private Integer roleId;
    private Integer userId;

    protected IpAccessRule(IpAccessRule from) {
        setId(from.getId());
        setEnabled(from.isEnabled());
        setRestricted(from.isRestricted());
        setIpRange(from.getIpRange());
        setUserId(from.getUserId());
        setRoleId(from.getRoleId());
    }
}
