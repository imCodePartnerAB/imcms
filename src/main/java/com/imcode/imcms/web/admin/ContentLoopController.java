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

    @RequestMapping(value = "/contentloop", method = RequestMethod.POST)
    public String processCommand(
            @RequestParam("cmd") int cmd,
            @RequestParam("docId") int docId,
            @RequestParam("no") Integer no,
            final @RequestParam("contentIndex") int contentIndex,
            @RequestParam("flags") int flags) {

        DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
        TextDocumentDomainObject document = (TextDocumentDomainObject) documentMapper.getDocument(docId);
        final Command command = getCommand(cmd);
        final ContentLoop loop = document.getContentLoop(no);

        ContentLoop updatedLoop = new Object() {
            ContentLoop updateLoop() {
                switch (command) {
                    case MOVE_UP:
                        return loop.moveContentBackward(contentIndex);

                    case MOVE_DOWN:
                        return loop.moveContentForward(contentIndex);

                    case ADD_BEFORE:
                        return loop.addContentBefore(contentIndex)._1();

                    case ADD_AFTER:
                        return loop.addContentAfter(contentIndex)._1();

                    case ADD_FISRT:
                        return loop.addFirstContent()._1();

                    case ADD_LAST:
                        return loop.addLastContent()._1();

                    case DELETE:
                        return loop.disableContent(contentIndex);

                    default:
                        return loop;
                }
            }
        }.updateLoop();

        try {
            contentLoopDao.saveLoop(updatedLoop);
        } finally {
            documentMapper.invalidateDocument(document);
        }

        return String.format(view, docId, flags);
    }


    private Command getCommand(int ordinal) {
        for (Command command : Command.values()) {
            if (command.ordinal() == ordinal) {
                return command;
            }
        }

        throw new IllegalArgumentException(String.format("Wrong command '%d'", ordinal));
    }
}