package com.imcode.imcms.test.http;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebResponse;
import junit.framework.TestCase;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author kreiger
 */
public class TestDocInfo extends TestCase {

    public void testHeadLine() throws IOException, SAXException, ClassNotFoundException {

        WebConversation webConversation = new WebConversation() ;
        GetMethodWebRequest getRequest = new GetMethodWebRequest("http://localhost:8080/imcms1.8/servlet/AdminDoc");
        getRequest.setParameter("flags", "1");
        getRequest.setParameter("meta_id", "1001");
        WebResponse response = webConversation.getResponse(getRequest);
        WebForm form = response.getForms()[0] ;
        final String headlineField = form.getParameterValue("meta_headline");
        assertNotNull(headlineField) ;
        assertFalse("".equals(headlineField)) ;
    }

}
