package imcode.external.chat;

import java.util.*;
import imcode.server.User;
/**
*A Chat can contain zero or many ChatGroups.
*A Chat can contain zero or many ChatMembers.
*
*From the Chat you can create ChatGroups and ChatMembers
*/
public class Chat{

	private int _chatId;	
	private String _name = "";
	private Hashtable _chatMembers;
	private Vector _chatMsgTypes;
	private ChatGroup chatGroup=new ChatGroup();
	private static Counter _memberCounter;
	private static Counter _msgTypeCounter;

	private	int _updateTime = 30;
	private	int _reload = 3;
	private int _inOut = 3;
	private	int _privat = 3;
	private	int _publik = 3;
	private	int _dateTime = 3;
	private	int _font = 3;
    private int _fontSize = 2;

	private Vector _authorization;
	private Vector _authorizationSelected;

	static{
		_memberCounter = new Counter();	
	}
	
	public Chat() {
	}
	
	public Chat(int metaId,Vector authorization,Vector msgTypes) {
		_chatId = metaId;
		
		_authorization = authorization;
		 _chatMsgTypes = msgTypes;
				
		_chatMembers = new Hashtable();
    	_authorizationSelected = new Vector();
		
		_msgTypeCounter = new Counter();
		_msgTypeCounter.setStartValue(200);
	}
	
	public void setSelectedAuto(String[] arr) {
		_authorizationSelected.clear();
		if (arr != null) {
			for(int i=0;i<arr.length;i++) {
				_authorizationSelected.add(arr[i]);
			}
		}
	}
	public Vector getSelectedAuto() {
		return (Vector)	_authorizationSelected.clone();
	}
	
	public boolean settingsPage() {
		if (_reload 	== 3 ||
			_inOut	== 3 ||
			_privat	== 3 ||
			_publik	== 3 ||
			_dateTime==3 ||
			_font	== 3) {
			return true;
		}else {
			return false;
		}
	}
	public int getRefreshTime() {
		return _updateTime;
	}
	public void setRefreshTime(int time) {
		_updateTime = time;
	}
	
	public int isAutoRefreshEnabled() {
		return _reload;
	}		
	public void setAutoRefreshEnabled(int reload) {
		_reload = reload;
	}
	
	public int isShowEnterAndLeaveMessagesEnabled() {
		return _inOut;
	}
	public void setShowEnterAndLeaveMessagesEnabled(int inOut) {
		_inOut = inOut;
	}
	
	public int isShowPrivateMessagesEnabled() {
		return _privat;
	}
	public void setShowPrivateMessagesEnabled(int privat) {
		_privat = privat;
	}
	
	public int isShowPublicMessagesEnabled() {
		return _publik;
	}
	public void setShowPublicMessagesEnabled(int publik) {
		_publik = publik;
	}
	
	public int isShowDateTimesEnabled() {
		return _dateTime;
	}
	public void setShowDateTimesEnabled(int dateTime) {
		_dateTime = dateTime;
	}
	
	public int getfont() {
		return _font;
	}
	public void setFontSize(int font) {
		_font = font;
	}

    public int getfontSize() {
		return _fontSize;
	}
	public void setfontSize(int fontSize) {
		_font = fontSize;
	}

	public void setAuthorizations(Vector authorization){
		_authorization = authorization;
	}
	
	public Vector getAuthorizations(){
		return (Vector)_authorization.clone();
	}
	

	public String getChatName(){
		return _name == null?"":_name;
	}

	public  int getChatId() {
		return _chatId;
	}
	
	public  String getChatIdStr() {
		return new Integer(_chatId).toString();
	}
	
	
	public ChatMember createChatMember(User user, int referrerMetaId) {
		_memberCounter.increment();
		int memberNumber = _memberCounter.getValue();
		ChatMember newMember = new ChatMember(memberNumber, this, user, referrerMetaId);
		_chatMembers.put(String.valueOf(memberNumber), newMember);
		return newMember;
	}
	 
	public boolean hasMemberName(String name) {
		Enumeration enum = _chatMembers.elements();
		while (enum.hasMoreElements()) {
			ChatMember temp = (ChatMember) enum.nextElement();
			if( name.equalsIgnoreCase(temp.getName()) ){
				return true ;
			}
		}
		return false;
	}
	
	public void removeChatMember(int memberNumber) {
		String memberNumberString = String.valueOf(memberNumber);
		_chatMembers.remove(memberNumberString);
	}

	
	public ChatMember getChatMember(int memberNumber) {
		String memberNumberString = String.valueOf(memberNumber);
		return (ChatMember)_chatMembers.get(memberNumberString);
	}


    public ChatGroup getChatGroup(){
		return (ChatGroup) chatGroup;
	}

	
	public String toString(){
		return "ChatId: " + _chatId + " ChatName: " + _name;
	}


	public Vector getMsgTypes(){
  		return _chatMsgTypes;
 	}
	
	
	public void  setMsgTypes(Vector v){
		_chatMsgTypes = v;
	}

	public void removeMsgType(int msgTypeId){
		int nr = _chatMsgTypes.indexOf(Integer.toString(msgTypeId));
		_chatMsgTypes.remove(nr);
		_chatMsgTypes.remove(nr);
	}

	public void addMsgType(int msgTypeId, String newType){		
		_chatMsgTypes.add(Integer.toString(msgTypeId));
		_chatMsgTypes.add(newType);
	}
	
	public void addMsgType(String newType){
		_msgTypeCounter.increment();
		_chatMsgTypes.add(Integer.toString(_msgTypeCounter.getValue()));
		_chatMsgTypes.add(newType);
	}
	
	public void addRoom(String room) {
		
	}
}//end class
