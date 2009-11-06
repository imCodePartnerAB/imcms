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
import com.imcode.imcms.api.ContentIndexes;
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
			@RequestParam("metaId") int metaId,
			@RequestParam("loopIndex") int loopIndex,
			@RequestParam("loopBaseIndex") int loopBaseIndex,
			@RequestParam("contentIndex") int contentIndex,
			@RequestParam("flags") int flags) {
						
		Command command = getCommand(cmd);
		DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
		TextDocumentDomainObject document = (TextDocumentDomainObject)documentMapper.getDocument(metaId);
		ContentLoop loop = document.getContentLoop(loopIndex);
		
        try {
            if (loop.getId() == null) {
                loop = contentLoopDao.saveContentLoop(loop);
            }
            
            Long loopId = loop.getId();

            int contentsCount = loop.getContents().size();

            switch (command) {
            case MOVE_UP:
                if (contentsCount > 1)
                    contentLoopDao.moveContentUp(loopId, contentIndex);
                break;

            case MOVE_DOWN:
                if (contentsCount > 1)
                    contentLoopDao.moveContentDown(loopId, contentIndex);
                break;

            case ADD_BEFORE:
                contentLoopDao.insertNewContentBefore(loopId, contentIndex);
                break;

            case ADD_AFTER:
                contentLoopDao.insertNewContentAfter(loopId, contentIndex);
                break;

            case ADD_FISRT:
                contentLoopDao.addFisrtContent(loopId);
                break;

            case ADD_LAST:
                contentLoopDao.addLastContent(loopId);
                break;

            case DELETE:
                if (contentsCount > 1)
                    contentLoopDao.deleteContent(loopId, contentIndex);

                break;
            }
        } finally {
            documentMapper.invalidateDocument(document);
        }
		
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