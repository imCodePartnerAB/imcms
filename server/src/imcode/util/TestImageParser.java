package imcode.util;

/**
 * @author kreiger
 */

import junit.framework.TestCase;

import java.io.File;

public class TestImageParser extends TestCase {

    ImageParser imageParser = new ImageParser();

    public void testParseImageStream() throws Exception {
        File gifImageFile = new File( "web/imcms/lang/images/admin/1x1.gif");
        ImageSize imageSize = imageParser.parseImageFile( gifImageFile );
        assertTrue( imageSize.getHeight() > 0);
        assertTrue( imageSize.getWidth() > 0 );
    }
}