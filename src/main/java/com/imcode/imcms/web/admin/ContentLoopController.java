package com.imcode.imcms.web.admin;

import imcode.server.Imcms;
import imcode.server.document.textdocument.TextDocumentDomainObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.imcode.imcms.api.ContentLoop;
import com.imcode.imcms.api.Content;
import com.imcode.imcms.dao.ContentLoopDao;
import com.imcode.imcms.mapping.DocumentMapper;
import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;


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
	public String processCommand (
			@RequestParam("cmd") int cmd,
			@RequestParam("docId") int docId,
			@RequestParam("no") int no,
			@RequestParam("contentNo") int contentNo,
			@RequestParam("flags") int flags) {
						
		Command command = getCommand(cmd);
		DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
		TextDocumentDomainObject document = (TextDocumentDomainObject)documentMapper.getDocument(docId);
		ContentLoop loop = document.getContentLoop(no);
		
        try {
            switch (command) {
            case MOVE_UP:
                loop.moveContentBackward(contentNo);
                break;

            case MOVE_DOWN:
                loop.moveContentForward(contentNo);
                break;

            case ADD_BEFORE:
                loop.insertContentBefore(contentNo);
                break;

            case ADD_AFTER:
                loop.insertContentAfter(contentNo);
                break;

            case ADD_FISRT:
                loop.addFirstContent();
                break;

            case ADD_LAST:
                loop.addLastContent();
                break;

            case DELETE:
                loop.disableContent(contentNo);
                break;
            }

            loop = contentLoopDao.saveContentLoop(loop);
        } finally {
            documentMapper.invalidateDocument(document);
        }
		
		return String.format(view, docId, flags);
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