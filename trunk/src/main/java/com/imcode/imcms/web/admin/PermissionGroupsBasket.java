package com.imcode.imcms.web.admin;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

/**
 *
 * Prototype class
 *
 */
public class PermissionGroupsBasket {

	public static class PermissionGroupToPairTransformer implements Transformer {

		public Object transform(Object o) {
			NameValuePair pair = null;
			PermissionGroup pg = (PermissionGroup) o;

			if (pg != null) {
				pair = new NameValuePair(pg.getId().toString(), pg.getName());
			}

			return pair;
		}
	}
	
	//public 

	public static Map<Object, PermissionGroup> getAllPermissionGroups() {
		Map<Object, PermissionGroup> permGroupsList = new HashMap<Object, PermissionGroup>();

		permGroupsList.putAll(DefaultPermissionGroups.getAllDefaultPermissionGroups());
		permGroupsList.putAll(getNotDefaultPermissionGroups());

		return permGroupsList;
	}

	public static Map<Object, PermissionGroup> getNotDefaultPermissionGroups() {
		Map<Object, PermissionGroup> permGroupList = new HashMap<Object, PermissionGroup>();

		return permGroupList;
	}

	public static List<NameValuePair> toNameValueOptions(
			List<PermissionGroup> permissionGroups) {
		List<NameValuePair> nameValueOptions = new ArrayList<NameValuePair>();

		CollectionUtils.collect(permissionGroups,
				new PermissionGroupToPairTransformer(), nameValueOptions);

		return nameValueOptions;
	}
	
	public static PermissionGroup getPermissionGroupById(Object id) {
		return getAllPermissionGroups().get(id);
	}

}
