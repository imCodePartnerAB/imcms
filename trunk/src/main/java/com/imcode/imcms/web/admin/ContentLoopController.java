package com.imcode.imcms.web.admin;

import imcode.server.Imcms;
import imcode.server.document.textdocument.TextDocumentDomainObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.imcode.imcms.api.ContentLoop;
import com.imcode.imcms.dao.ContentLoopDao;
import com.imcode.imcms.mapping.DocumentMapper;

/**
 * Working prototype - REFACTOR & OPTIMIZE & Add permission check 
 */
@Controller
public class ContentLoopController {
	
	public static enum Command {
		MOVE_UP,
		MOVE_DOWN,
		ADD_BEFORE,
		ADD_AFTER,
		ADD_FISRT,
		ADD_LAST,
		DELETE		
	}	
	
	
	private String view = "forward:/servlet/AdminDoc?meta_id=%s&flags=%s";
	
	private ContentLoopDao contentLoopDao;
	
	@Autowired
	public void setContentLoopDao(ContentLoopDao contentLoopDao) {
		this.contentLoopDao = contentLoopDao;
	}
	
	@RequestMapping(value="/contentloop", method = RequestMethod.POST)
	public String processCommand(
			@RequestParam("cmd") int cmd,
			@RequestParam("metaId") int metaId,
			@RequestParam("loopIndex") int loopIndex,
			@RequestParam("loopBaseIndex") int loopBaseIndex,
			@RequestParam("contentIndex") int contentIndex,
			@RequestParam("flags") int flags) {
						
		Command command = getCommand(cmd);
		DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
		TextDocumentDomainObject document = (TextDocumentDomainObject)documentMapper.getDocument(metaId);
		ContentLoop loop = document.getContentLoop(loopIndex);
		
		// Recreates loop - remove after functionality is fully tested 
		contentLoopDao.saveContentLoop(metaId, loop);
		
		int contentsCount = loop.getContents().size();
		
		switch (command) {
		case MOVE_UP:
			if (contentsCount > 1)
				contentLoopDao.moveContentUp(loop, contentIndex);
			break;
			
		case MOVE_DOWN:
			if (contentsCount > 1)
				contentLoopDao.moveContentDown(loop, contentIndex);
			break;
			
		case ADD_BEFORE:
			contentLoopDao.insertNewContentBefore(loop, contentIndex);			
			break;
			
		case ADD_AFTER:
			contentLoopDao.insertNewContentAfter(loop, contentIndex);
			break;
			
		case ADD_FISRT:
			contentLoopDao.addFisrtContent(loop);
			break;
			
		case ADD_LAST:
			contentLoopDao.addLastContent(loop);
			break;
			
		case DELETE:
			if (contentsCount > 1)
				contentLoopDao.deleteContent(loop, contentIndex);
			
			break;
		}
		
		documentMapper.invalidateDocument(document);
		
		return String.format(view, metaId, flags);
	}	
	
	
	private Command getCommand(int ordinal) {
		for (Command command: Command.values()) {
			if (command.ordinal() == ordinal) {
				return command;
			}
		}
		
		throw new IllegalArgumentException(String.format("Wrong command '%d'", ordinal)); 
	}
}