package imcode.external.chat;

import java.util.*;
import imcode.external.diverse.*;

/**
*A Chat can contain zero or many ChatGroups.
*A Chat can contain zero or many ChatMembers.
*
*From the Chat you can create ChatGroups and ChatMembers
*/
public class Chat
{
	private int _chatId;
	private String _name;
	private Hashtable _chatMembers;
	private Hashtable _chatMsgTypes;
	private Hashtable _chatGroups;
	private static Counter _memberCounter;
//	private Counter _roomCounter;
	private int _permission; 
	private	int _updateTime = 30;
	private	int _reload = 2;
	private int _inOut = 2;
	private	int _privat = 2;
	private	int _publik = 2;
	private	int _dateTime = 2;
	private	int _font = 2;
	private String[] _authorization;
	
	static
	{
		_memberCounter = new Counter();	
	}

	//ok the constructor for the Chat
	public Chat(int metaId, Vector msgTypes, Properties params, String[] authorization) //peter says ok
	{
		//the counters
			
		
		//storing plazes
		_chatMembers = new Hashtable();
		_chatGroups = new Hashtable();
		_chatMsgTypes = new Hashtable();
		_authorization = authorization;
		//name and stuff
		 _chatId=metaId;
		 
		for(int i=0;i<msgTypes.size();i+=2)
		{
			_chatMsgTypes.put(msgTypes.get(i),msgTypes.get(i+1));
		}
		
		_name =  params.getProperty("chatName","");
		_permission = Integer.parseInt(params.getProperty("permission","3"));
		_updateTime = Integer.parseInt( params.getProperty("updateTime" , "30" ) );
		_reload = Integer.parseInt( params.getProperty("reload" , "2" ) );
	    _inOut = Integer.parseInt( params.getProperty("inOut" , "2" ) );
		_privat = Integer.parseInt( params.getProperty("privat" , "2" ) );
		_publik = Integer.parseInt( params.getProperty("publik" , "2" ) );
		_dateTime = Integer.parseInt( params.getProperty("dateTime" , "2" ) );
		_font = Integer.parseInt( params.getProperty("font" , "2" ) );
			
	}
	public synchronized void setParams(Properties params)
	{
		_name =  params.getProperty("chatName","");
		_permission = Integer.parseInt(params.getProperty("permission","3"));
		_updateTime = Integer.parseInt( params.getProperty("updateTime" , "30" ) );
		_reload = Integer.parseInt( params.getProperty("reload" , "2" ) );
	    _inOut = Integer.parseInt( params.getProperty("inOut" , "2" ) );
		_privat = Integer.parseInt( params.getProperty("privat" , "2" ) );
		_publik = Integer.parseInt( params.getProperty("publik" , "2" ) );
		_dateTime = Integer.parseInt( params.getProperty("dateTime" , "2" ) );
		_font = Integer.parseInt( params.getProperty("font" , "2" ) );
		
	}
	
	public synchronized void setAuthorizations(String[] authorization)
	{
		_authorization = authorization;
	}
	
	public String[] getAuthorizations()
	{
		return _authorization;
	}
	
	public int getPermission() //peter says ok
	{
		return _permission;
	}

	public String getChatName() //peter says ok
	{
		return _name;
	}
	
	public  int getChatId() //peter says ok
	{
		return _chatId;
	}
	/**
	*Creates and "register" a new ChatMember in this chat
	*the is initially not a member of any ChatGroup;
	*@return The Created ChatMember
	*/
	public ChatMember createChatMember() //peter says ok
	{
		_memberCounter.increment();
		int memberNumber = _memberCounter.getValue();
		ChatMember newMember = new ChatMember(memberNumber, getChatParameters(), this);
		_chatMembers.put(String.valueOf(memberNumber), newMember);
		return newMember;
	}
	
	/**
	*Removes a ChatMember from this chat
	*@param memberNumber The membernumber of the ChatMember you want to remove
	*If no ChatMember exists with the supplied memberNumber, no action is taken.
	*/
	public void removeChatMember(int memberNumber) //peter says ok
	{
		String memberNumberString = String.valueOf(memberNumber);
		_chatMembers.remove(memberNumberString);
	}
	
	/**
	*Gets the ChatMember with the supplied memberNumber
	*@param memberNumber
	*@return The ChatMember with the supplied memberNumber or 
	*null if no registerd ChatMember matches the given memberNumber
	*/
	public ChatMember getChatMember(int memberNumber) //peter says ok
	{
		String memberNumberString = String.valueOf(memberNumber);		
		return (ChatMember)_chatMembers.get(memberNumberString);
	}

	/**
	*Creates and "register" a new ChatGroup in this chat
	*the ChatGroup is initially not a member of any chatRoom;
	*@return The Created ChatGroup
	*/
	public void createNewChatGroup(int groupNr, String groupName)
	{
		ChatGroup newGroup = new ChatGroup(groupNr,groupName);
		_chatGroups.put( String.valueOf(groupNr),newGroup );
	}

	/**
	*Removes a ChatGroup from this chat
	*@param groupNumber The groupNumber of the ChatGroup you want to remove
	*If no ChatGroup exists with the supplied groupNumber, no action is taken.
	*/
	public void removeChatGroup(int groupNumber)
	{
		String groupNumberString = String.valueOf(groupNumber);
		_chatGroups.remove(groupNumberString);
	}
	
	/**Gets all groups
	*@return An Enumeration of all avalible groups
	*/
	public Enumeration getAllChatGroups()
	{
		return _chatGroups.elements();
	}
	
	
	/**
	*Gets the 
	*@param groupNumber
	*@return The ChatGroup with the supplied groupNumber or 
	*null if no registerd ChatGroup matches the given groupNumber
	*/
	public ChatGroup getChatGroup(int groupNumber)
	{
		String groupNumberString = String.valueOf(groupNumber);
		return (ChatGroup) _chatGroups.get(groupNumberString);	
	}
	
	//toString
	public String toString()
	{
		return "ChatId: " + _chatId + " ChatName: " + _name;
	}
	
	public Properties getChatParameters()
	{
		Properties params = new Properties();		
		
		params.setProperty("updateTime" , Integer.toString(_updateTime) );
		params.setProperty("reload" , Integer.toString(_reload) );
		params.setProperty("inOut" , Integer.toString(_inOut) );
		params.setProperty("privat" , Integer.toString(_privat) );
		params.setProperty("publik" , Integer.toString(_publik) );
		params.setProperty("dateTime" ,Integer.toString(_dateTime) );
		params.setProperty("font" ,Integer.toString( _font) );
		params.setProperty("chatName", _name);
		
		return params;
	}
	
	public Vector getMsgTypes()
 	{
  		Vector temp = new Vector();
  		Enumeration enum = _chatMsgTypes.keys();
  		while (enum.hasMoreElements())
  		{
   			
			String key = (String) enum.nextElement();

   			temp.addElement(key);
   			temp.addElement( _chatMsgTypes.get(key) );
  		}
  		return temp;
 	}
	public void setMsgTypes(Vector v)
	{
		//get rid of the ones not in use
		Enumeration enum = _chatMsgTypes.keys();
		while (enum.hasMoreElements())
		{
			String key = (String)enum.nextElement();
			boolean found=false;
			for(int i=0;i<v.size();i+=2)
			{
				if(key.equals((String)v.get(i)))
				{
					found = true;
				}
			}
			if (!found)
			{
				_chatMsgTypes.remove(key);
			}
		}
		//add the new ones
		for(int i=0;i<v.size();i+=2)
		{
			_chatMsgTypes.put(v.get(i),v.get(i+1));
		}
		
	}
	public void removeMsgType(int msgTypeId)
	{
		_chatMsgTypes.remove(Integer.toString(msgTypeId));
	}
	
	public void addMsgType(int msgTypeId, String newType)
	{
		_chatMsgTypes.put(Integer.toString(msgTypeId),newType);
		//	_chatMsgTypes.put(msgTypes.get(i),msgTypes.get(i+1));
	}


}
