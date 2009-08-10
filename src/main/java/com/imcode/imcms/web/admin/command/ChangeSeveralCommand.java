package com.imcode.imcms.web.admin.command;

import java.util.EnumSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imcode.imcms.web.admin.NameValuePair;

public class ChangeSeveralCommand {
	public static final Log log = LogFactory.getLog(ChangeSeveralCommand.class);



	private ActionCommand<RolesAction, ProfileRoleCommand> roleOnPage;
	private ActionCommand<CategoriesAction, Categories> categories;

	private ActionCommand<LockAction, String> profile;
	private ActionCommand<LockAction, String> publicationStatus;

	private ActionCommand<LockAction, DateTime> publicationDateTime;
	private ActionCommand<LockAction, DateTime> archiveDateTime;
	private ActionCommand<LockAction, DateTime> expiredDateTime;
	private ActionCommand<LockAction, DateTime> publishOnDateTime;
	private ActionCommand<LockAction, DateTime> lastChangeDateTime;

	private ActionCommand<LockAction, Persons> persons;

	private ActionCommand<LockAction, Text> text;

	public ChangeSeveralCommand() {
		roleOnPage = new ActionCommand<RolesAction, ProfileRoleCommand>(new ProfileRoleCommand());
		categories = new ActionCommand<CategoriesAction, Categories>(new Categories());
		
		profile = new ActionCommand<LockAction, String>();
		publicationStatus = new ActionCommand<LockAction, String>();

		publicationDateTime = new ActionCommand<LockAction, DateTime>(new DateTime());
		archiveDateTime = new ActionCommand<LockAction, DateTime>(new DateTime());
		expiredDateTime = new ActionCommand<LockAction, DateTime>(new DateTime());
		publishOnDateTime = new ActionCommand<LockAction, DateTime>(new DateTime());
		lastChangeDateTime = new ActionCommand<LockAction, DateTime>(new DateTime());

		persons = new ActionCommand<LockAction, Persons>(new Persons());
		
		text = new ActionCommand<LockAction, Text>(new Text());
	}

	public EnumSet<LockAction> getSimpleActions() {
		return EnumSet.allOf(LockAction.class);
	}

	public EnumSet<RolesAction> getRoleActions() {
		return EnumSet.allOf(RolesAction.class);
	}

	public EnumSet<CategoriesAction> getCategoryActions() {
		return EnumSet.allOf(CategoriesAction.class);
	}
	
	public EnumSet<TextEditMode> getTextEditModes() {
		return EnumSet.allOf(TextEditMode.class);
	}
	
	public ActionCommand<LockAction, String> getProfile() {
		return profile;
	}

	public void setProfile(ActionCommand<LockAction, String> profile) {
		this.profile = profile;
	}

	public ActionCommand<LockAction, String> getPublicationStatus() {
		return publicationStatus;
	}

	public void setPublicationStatus(
			ActionCommand<LockAction, String> publicationStatus) {
		this.publicationStatus = publicationStatus;
	}

	public ActionCommand<LockAction, DateTime> getPublicationDateTime() {
		return publicationDateTime;
	}

	public void setPublicationDateTime(
			ActionCommand<LockAction, DateTime> publicationDateTime) {
		this.publicationDateTime = publicationDateTime;
	}

	public ActionCommand<LockAction, DateTime> getArchiveDateTime() {
		return archiveDateTime;
	}

	public void setArchiveDateTime(
			ActionCommand<LockAction, DateTime> archiveDateTime) {
		this.archiveDateTime = archiveDateTime;
	}

	public ActionCommand<LockAction, DateTime> getExpiredDateTime() {
		return expiredDateTime;
	}

	public void setExpiredDateTime(
			ActionCommand<LockAction, DateTime> expiredDateTime) {
		this.expiredDateTime = expiredDateTime;
	}

	public ActionCommand<LockAction, DateTime> getPublishOnDateTime() {
		return publishOnDateTime;
	}

	public void setPublishOnDateTime(
			ActionCommand<LockAction, DateTime> publishOnDateTime) {
		this.publishOnDateTime = publishOnDateTime;
	}

	public ActionCommand<LockAction, DateTime> getLastChangeDateTime() {
		return lastChangeDateTime;
	}

	public void setLastChangeDateTime(
			ActionCommand<LockAction, DateTime> lastChangeDateTime) {
		this.lastChangeDateTime = lastChangeDateTime;
	}

	public ActionCommand<LockAction, Persons> getPersons() {
		return persons;
	}

	public void setPersons(ActionCommand<LockAction, Persons> persons) {
		this.persons = persons;
	}

	public ActionCommand<RolesAction, ProfileRoleCommand> getRoleOnPage() {
		return roleOnPage;
	}

	public void setRoleOnPage(
			ActionCommand<RolesAction, ProfileRoleCommand> roleOnPage) {
		this.roleOnPage = roleOnPage;
	}

	public ActionCommand<CategoriesAction, Categories> getCategories() {
		return categories;
	}

	public void setCategories(ActionCommand<CategoriesAction, Categories> categories) {
		this.categories = categories;
	}

	public ActionCommand<LockAction, Text> getText() {
		return text;
	}

	public void setText(ActionCommand<LockAction, Text> text) {
		this.text = text;
	}

	
	public class ActionCommand<E, T> {
		private T value;
		private E action;

		public ActionCommand() {
		}

		public ActionCommand(T value) {
			this.value = value;
		}

		public T getValue() {
			return value;
		}

		public void setValue(T value) {
			this.value = value;
		}

		public E getAction() {
			return action;
		}

		public void setAction(E action) {
			this.action = action;
		}
	}

	
	
	public enum LockAction {
		LOCK, OPEN;

		public String getName() {
			return this.name();
		}
	}

	public enum RolesAction {
		LOCK, ADD, REMOVE;

		public String getName() {
			return this.name();
		}
	}

	public enum CategoriesAction {
		LOCK, ADD, REPLACE_ALL, REMOVE;

		public String getName() {
			return this.name();
		}
	}

	public enum TextEditMode {
		REPLACE, BEFORE, AFTER;

		public String getName() {
			return this.name();
		}
	}

	
	
	public class DateTime {
		private String date;
		private String time;

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}
	}

	public class Persons {
		private String creator;
		private String publisher;

		public String getCreator() {
			return creator;
		}

		public void setCreator(String creator) {
			this.creator = creator;
		}

		public String getPublisher() {
			return publisher;
		}

		public void setPublisher(String publisher) {
			this.publisher = publisher;
		}
	}

	public class Text {
		private String chnageIn;
		private String changeInNo;

		private String useTextIn;
		private String useTextInNo;

		private TextEditMode textEditMode;
		private String text;

		public String getChnageIn() {
			return chnageIn;
		}

		public void setChnageIn(String chnageIn) {
			this.chnageIn = chnageIn;
		}

		public String getChangeInNo() {
			return changeInNo;
		}

		public void setChangeInNo(String changeInNo) {
			this.changeInNo = changeInNo;
		}

		public String getUseTextIn() {
			return useTextIn;
		}

		public void setUseTextIn(String useTextIn) {
			this.useTextIn = useTextIn;
		}

		public String getUseTextInNo() {
			return useTextInNo;
		}

		public void setUseTextInNo(String useTextInNo) {
			this.useTextInNo = useTextInNo;
		}

		public TextEditMode getTextEditMode() {
			return textEditMode;
		}

		public void setTextEditMode(TextEditMode textEditMode) {
			this.textEditMode = textEditMode;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}
	}

	public class Categories {
		private List<NameValuePair> unselectedColumns;
		private List<NameValuePair> selectedColumns;

		public List<NameValuePair> getUnselectedColumns() {
			return unselectedColumns;
		}

		public void setUnselectedColumns(List<NameValuePair> unselectedColumns) {
			this.unselectedColumns = unselectedColumns;
		}

		public List<NameValuePair> getSelectedColumns() {
			return selectedColumns;
		}

		public void setSelectedColumns(List<NameValuePair> selectedColumns) {
			this.selectedColumns = selectedColumns;
		}
	}
}
