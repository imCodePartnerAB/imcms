///////////////////////////////////////////////////////////
//
//  ChatMsg.java
//  Implementation of the Class ChatMsg
//  Generated by Enterprise Architect
//  Created on:      2001-07-11
//  Original author: 
//  
///////////////////////////////////////////////////////////
//  Modification history:
//  
//
///////////////////////////////////////////////////////////

package imcode.external.chat;

import java.util.*;

public class ChatMsg
{

	private int _sender;
	private int _msgType;
	private String _chatMsg;
	private int _reciever;
	private int _number;
	private String _dateTime;
	//	private Date date;


	/**
	*Default constructor
	*/
	public ChatMsg(String chatMsg, int reciever, int msgType, int sender, String dateTime)
	{
		_chatMsg = chatMsg;
		_reciever =	reciever;
		_msgType = msgType;
		_sender = sender;
		_dateTime = dateTime;
	}

	protected void setIdNumber(int number)
	{
		_number = number;
	}
	public int getIdNumber()
	{
		return _number;
	}
	
	public String getDateTime()
	{
		return _dateTime;
	}

	public String toString()
	{
		return null;

	}

	public String getMessage()
	{
		return _chatMsg;

	}
	
		
	public int getMsgType()
	{
		return _msgType;

	}

	public int getReciever()
	{
		return _reciever;

	}

	public int getSender()
	{
		return _sender;

	}


}
