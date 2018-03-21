package com.imcode.imcms.flow;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RedirectCommand implements DispatchCommand {
    private final String returnUrl;

    public RedirectCommand(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    @Override
    public void dispatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(returnUrl);
    }
}
