package com.imcode.imcms.web.admin;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Prototype class
 * 
 */
public class PermissionGroup {

	public enum Permission {
		READ(0), PAGE_INFO(1), PROFILE(2), PUBLISH(3), TEXT(4), IMAGE(5), CONTENT_LOOP(
				6), ADMIN_TEXT(7), ADMIN_IMAGE(8), EXTERNAL_LINK(9), INTERNAL_LINK(
				10), TEXTDOC(11), FILE(12), HTML(13), ADMIN_MENU(14);

		private static long fullPermisionsSum = 0;
		private final long permissionId;

		static {
			for (Permission p : EnumSet.allOf(Permission.class)) {
				fullPermisionsSum += p.getPermissionId();
			}
		}

		private Permission(int permissionLvl) {
			this.permissionId = 1 << permissionLvl;
		}

		public long getPermissionId() {
			return permissionId;
		}

		private static long getFullPermissionsSum() {
			return fullPermisionsSum;
		}
	}

	/**
	 * Field contains a group name to display on a web page Should be a string
	 * or message key from a language file.
	 */
	private String name;
	private Object id;
	private boolean defaulf; // systemDefined

	private long permissionsSum;
	private static Map<Object, PermissionGroup> instances = new HashMap<Object, PermissionGroup>();

	
	public PermissionGroup() {
		this.permissionsSum = 0;
		
	}
	// Instance-controlled
	private PermissionGroup(long permissionsSum) {
		this.permissionsSum = permissionsSum;
	}

	public synchronized void grantPermission(Permission permission) {
		if (!isPermission(permission)) {
			permissionsSum += permission.getPermissionId();
		}
	}

	public synchronized void revokePermission(Permission permission) {
		if (isPermission(permission)) {
			permissionsSum -= permission.getPermissionId();
		}
	}

	public static PermissionGroup fullPermissionsGroup(Object id) {
		return getInstance(Permission.getFullPermissionsSum(), id);
	}

	public static PermissionGroup withoutPermissionsGroup(Object id) {
		return getInstance(0, id);
	}

	private boolean isPermission(Permission permission) {
		long permissionId = permission.getPermissionId();

		return (permissionsSum & permissionId) == permissionId;
	}
	
	private void setPermission(Permission permission, boolean grant) {
		if (grant) {
			grantPermission(permission);
		} else {
			revokePermission(permission);
		}
	}

	private static PermissionGroup getInstance(long permissionsSum, Object id) {
		boolean exists = instances.get(id) != null;

		if (exists) {
			throw new RuntimeException(String.format(
					"Permission group with id %s already exists!", id));
		}

		PermissionGroup pg = new PermissionGroup(permissionsSum);
		pg.setId(id);

		instances.put(id, pg);

		return pg;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("PermissionGroup\n");
		sb.append(String.format("Hash code: %s;\n", hashCode()));
		sb.append(String.format("Id: %s;\n", id));
		
		sb.append(String.format("Read: %s;\n",         isRead()));
		sb.append(String.format("PageInfo: %s;\n",     isPageInfo()));
		sb.append(String.format("Profile: %s;\n",      isProfile()));
		sb.append(String.format("Publish: %s;\n",      isPublish()));
		sb.append(String.format("Text: %s;\n",         isText()));
		sb.append(String.format("Image: %s;\n",        isImage()));
		sb.append(String.format("ContentLoop: %s;\n",  isContentLoop()));
		sb.append(String.format("AdminText: %s;\n",    isAdminText()));
		sb.append(String.format("AdminImage: %s;\n",   isAdminImage()));
		sb.append(String.format("ExternalLink: %s;\n", isExternalLink()));
		sb.append(String.format("InternalLink: %s;\n", isInternalLink()));
		sb.append(String.format("Textdoc: %s;\n",      isTextdoc()));
		sb.append(String.format("File: %s;\n",         isFile()));
		sb.append(String.format("html: %s;\n",         isHtml()));
		sb.append(String.format("AdminMenu: %s;\n",    isAdminMenu()));

		return sb.toString();
	}
	
	public boolean isRead() {
		return isPermission(Permission.READ);
	}
	
	public void setRead(boolean read) {
		setPermission(Permission.READ, read);
	}

	
	public boolean isText() {
		return isPermission(Permission.TEXT);
	}
	
	public void setText(boolean text) {
		setPermission(Permission.TEXT, text);
	}

	
	public boolean isTextdoc() {
		return isPermission(Permission.TEXTDOC);
	}
	
	public void setTextDoc(boolean textDoc) {
		setPermission(Permission.TEXTDOC, textDoc);
	}
	

	public boolean isImage() {
		return isPermission(Permission.IMAGE);
	}
	
	public void setImage(boolean image) {
		setPermission(Permission.IMAGE, image);
	}
	
	
	public boolean isContentLoop() {
		return isPermission(Permission.CONTENT_LOOP);
	}
	
	public void setContentLoop(boolean contentLoop) {
		setPermission(Permission.CONTENT_LOOP, contentLoop);
	}
	

	public boolean isFile() {
		return isPermission(Permission.FILE);
	}
	
	public void setFile(boolean file) {
		setPermission(Permission.FILE, file);
	}
	

	public boolean isHtml() {
		return isPermission(Permission.HTML);
	}
	
	public void setHtml(boolean html) {
		setPermission(Permission.HTML, html);
	}
	
	
	public boolean isPageInfo() {
		return isPermission(Permission.PAGE_INFO);
	}
	
	public void setPageInfo(boolean pageInfo) {
		setPermission(Permission.PAGE_INFO, pageInfo);
	}

	
	public boolean isProfile() {
		return isPermission(Permission.PROFILE);
	}
	
	public void setProfile(boolean profile) {
		setPermission(Permission.PROFILE, profile);
	}

	
	public boolean isPublish() {
		return isPermission(Permission.PUBLISH);
	}
	
	public void setPublish(boolean publish) {
		setPermission(Permission.PUBLISH, publish);
	}

	
	public boolean isInternalLink() {
		return isPermission(Permission.INTERNAL_LINK);
	}
	
	public void setInternalLink(boolean internalLink) {
		setPermission(Permission.INTERNAL_LINK, internalLink);
	}
	

	public boolean isExternalLink() {
		return isPermission(Permission.EXTERNAL_LINK);
	}
	
	public void setExternalLink(boolean externalLink) {
		setPermission(Permission.EXTERNAL_LINK, externalLink);
	}

	
	public boolean isAdminImage() {
		return isPermission(Permission.ADMIN_IMAGE);
	}
	
	public void setAdminImage(boolean adminImage) {
		setPermission(Permission.ADMIN_IMAGE, adminImage);
	}
	

	public boolean isAdminText() {
		return isPermission(Permission.ADMIN_TEXT);
	}
	
	public void setAdminText(boolean adminText) {
		setPermission(Permission.ADMIN_TEXT, adminText);
	}

	
	public boolean isAdminMenu() {
		return isPermission(Permission.ADMIN_MENU);
	}
	
	public void setAdminMenu(boolean adminMenu) {
		setPermission(Permission.ADMIN_MENU, adminMenu);
	}

	public boolean isDefault() {
		return this.defaulf;
	}
	
	public void setDefault(boolean def) {
		this.defaulf = def;
	}

	public void setDefault() {
		this.defaulf = true;
	}

	public void setNotDefault() {
		this.defaulf = false;
	}

	public Object getId() {
		return id;
	}

	public void setId(Object id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
