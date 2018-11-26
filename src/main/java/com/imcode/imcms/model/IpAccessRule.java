package com.imcode.imcms.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public abstract class IpAccessRule implements Serializable {
    private static final long serialVersionUID = -7136110444914457556L;

    protected Integer id;
    protected boolean isEnabled;
    protected boolean isRestricted;
    protected String ipRange;
    protected Integer roleId;
    protected Integer userId;

    protected IpAccessRule(IpAccessRule from) {
        setId(from.getId());
        setEnabled(from.isEnabled());
        setRestricted(from.isRestricted());
        setIpRange(from.getIpRange());
        setUserId(from.getUserId());
        setRoleId(from.getRoleId());
    }
}
