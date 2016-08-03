package com.imcode.imcms.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Shadowgun on 09.01.2015.
 */
public class JSONUtils {

    public static void defaultJSONAnswer(HttpServletResponse response, Object answer) throws IOException {
        response.setContentType("application/json");
        // Get the printwriter object from response to write the required json object to the output stream
        PrintWriter out = response.getWriter();
        // Assuming your json object is **jsonObject**, perform the following, it will return your json object
        out.print(new ObjectMapper().writeValueAsString(answer));
        out.flush();
        out.close();
    }
}
