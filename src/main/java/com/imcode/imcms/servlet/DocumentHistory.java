package com.imcode.imcms.servlet;

import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

/**
 * History of visited document ids, saves in session.
 * <p>
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 09.11.17.
 */
public class DocumentHistory implements Serializable {
    private static final long serialVersionUID = 6629766212286699564L;

    private Deque<Integer> history;

    private DocumentHistory(Deque<Integer> history) {
        this.history = history;
    }

    public static DocumentHistory from(HttpSession session) {
        @SuppressWarnings("unchecked")
        DocumentHistory history = (DocumentHistory) session.getAttribute("history");

        return Optional.ofNullable(history).orElseGet(() -> {
            final DocumentHistory newHistory = new DocumentHistory(new ArrayDeque<>());
            session.setAttribute("history", newHistory);

            return newHistory;
        });
    }

    public void pushIfNotYet(Integer docId) {
        if (history.isEmpty() || !history.peek().equals(docId)) {
            history.push(docId);
        }
    }

    public Integer pop() {
        return history.pop();
    }

    public boolean isEmpty() {
        return history.isEmpty();
    }
}
