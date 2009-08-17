package com.imcode.imcms.web.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * 
 * Prototype class
 * 
 */
public final class DefaultPermissionGroups {
	/**
	 * Values from a locale file
	 */
	private static final String EDITOR_PERMISSION_GROUP_NAME     = "Redaktör";
	private static final String FULL_PERMISSION_GROUP_NAME       = "Full";
	private static final String TEXT_IMAGE_PERMISSION_GROUP_NAME = "TextImage";
	private static final String TEXT_PERMISSION_GROUP_NAME       = "Text";
	private static final String IMAGE_PERMISSION_GROUP_NAME      = "Image";
	
	private static PermissionGroup editorPermissions;
	private static PermissionGroup fullPermissions;
	private static PermissionGroup textImagePermissions;
	private static PermissionGroup textPermissions;
	private static PermissionGroup imagePermissions;
	
	private static long id;
	
	private static Map<Object, PermissionGroup> allDefaultPermissionGroups = new HashMap<Object, PermissionGroup>();
	
	static {
		allDefaultPermissionGroups.put(getEditorPermissions().getId(), editorPermissions);
		allDefaultPermissionGroups.put(getFullPermissions().getId(), fullPermissions);
		allDefaultPermissionGroups.put(getTextImagePermissions().getId(), textImagePermissions);
		allDefaultPermissionGroups.put(getTextPermissions().getId(), textPermissions);
		allDefaultPermissionGroups.put(getImagePermissions().getId(), imagePermissions);
	}
	
	
	
	public static PermissionGroup getEditorPermissions() {
		if (editorPermissions == null) {
			editorPermissions = PermissionGroup.withoutPermissionsGroup(generateDefaultGroupId());
			editorPermissions.setName(EDITOR_PERMISSION_GROUP_NAME);
			editorPermissions.setDefault();
			
			// Grant permissions
			editorPermissions.grantPermission(PermissionGroup.Permission.TEXT);
			editorPermissions.grantPermission(PermissionGroup.Permission.TEXTDOC);
			editorPermissions.grantPermission(PermissionGroup.Permission.IMAGE);
			editorPermissions.grantPermission(PermissionGroup.Permission.FILE);
			editorPermissions.grantPermission(PermissionGroup.Permission.PAGE_INFO);
			editorPermissions.grantPermission(PermissionGroup.Permission.INTERNAL_LINK);
			editorPermissions.grantPermission(PermissionGroup.Permission.EXTERNAL_LINK);
		}
		
		return editorPermissions;
	}
	
	public static PermissionGroup getFullPermissions() {
		if (fullPermissions == null) {
			fullPermissions = PermissionGroup.fullPermissionsGroup(generateDefaultGroupId());
			fullPermissions.setName(FULL_PERMISSION_GROUP_NAME);
			fullPermissions.setDefault();
		}
		
		return fullPermissions;
	}
	
	public static PermissionGroup getTextImagePermissions() {
		if (textImagePermissions == null) {
			textImagePermissions = PermissionGroup.withoutPermissionsGroup(generateDefaultGroupId());
			textImagePermissions.setName(TEXT_IMAGE_PERMISSION_GROUP_NAME);
			textImagePermissions.setDefault();
			
			// Grant permissions
			textImagePermissions.grantPermission(PermissionGroup.Permission.TEXT);
			textImagePermissions.grantPermission(PermissionGroup.Permission.IMAGE);
		}
		
		return textImagePermissions;
	}
	
	public static PermissionGroup getTextPermissions() {
		if (textPermissions == null) {
			textPermissions = PermissionGroup.withoutPermissionsGroup(generateDefaultGroupId());
			textPermissions.setName(TEXT_PERMISSION_GROUP_NAME);
			textPermissions.setDefault();
			
			// Grant permissions
			textPermissions.grantPermission(PermissionGroup.Permission.TEXT);
		}
		
		return textPermissions;
	}
	
	public static PermissionGroup getImagePermissions() {
		if (imagePermissions == null) {
			imagePermissions  = PermissionGroup.withoutPermissionsGroup(generateDefaultGroupId());
			imagePermissions.setName(IMAGE_PERMISSION_GROUP_NAME);
			imagePermissions.setDefault();
			
			// Grant permissions
			imagePermissions.grantPermission(PermissionGroup.Permission.IMAGE);
		}
		
		return imagePermissions;
	}
	
	public static Map<Object, PermissionGroup> getAllDefaultPermissionGroups() {
		return allDefaultPermissionGroups;
	}
	
	private static Object generateDefaultGroupId() {
		return ++id;
	}
}
