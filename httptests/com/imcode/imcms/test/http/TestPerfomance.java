package com.imcode.imcms.test.http;

import junit.framework.TestCase;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;

import java.io.IOException;
import java.util.Date;

import org.xml.sax.SAXException;

/**
 * /* To generate the pages to measure:
 * Run createPerformanceTestData.jsp witch is found in imcms/test
 * and change the constanst for the page id:s below.
 */

public class TestPerfomance extends TestCase {

    /*
    private static int HELLO_WORLD_TEXT_PAGE_ID = 1002;
    private static int TEXT_PAGE_WITH_TWO_SMALL_TEXTS_ID = 1003;
    private static int TEXT_PAGE_WITH_ONE_LARGER_TEXT_ID = 1004;
    private static int TEXT_PAGE_WITH_THREE_INCLUDES_ID = 1005;

    private final static String HOST = "http://localhost:8080/";
    private final static String WEBAPP = "1_8-BRANCH";
    private static String ROOT_URI = HOST + WEBAPP;
    private static String GET_DOC_PATH = "/servlet/GetDoc?meta_id=";
    private String URL_TO_HELLOWORLD_HTML_PAGE = ROOT_URI + "/imcms/test/helloworld.html";
    */

    private static int HELLO_WORLD_TEXT_PAGE_ID = 2090;
    private static int TEXT_PAGE_WITH_TWO_SMALL_TEXTS_ID = 2091;
    private static int TEXT_PAGE_WITH_ONE_LARGER_TEXT_ID = 2092;
    private static int TEXT_PAGE_WITH_THREE_INCLUDES_ID = 2094;

    private final static String HOST = "http://localhost:8080/";
    private final static String WEBAPP = "imcms";
    private static String ROOT_URI = HOST + WEBAPP;
    private static String GET_DOC_PATH = "/servlet/GetDoc?meta_id=";
    private String URL_TO_HELLOWORLD_HTML_PAGE = ROOT_URI + "/imcms/test/helloworld.html";

    private static final long noTestRuns = 30;

    public void testRunTestAsUserUser() throws IOException, SAXException {
        WebConversation wc = logOnUserUser();
        helloWorldHtmlPage( wc );
        textDocumentHelloWorld( wc );
        textPageWithTwoSmallTexts( wc );
        //textPageWithOneLargerText( wc );
        textPageWithTreeIncludes( wc );
    }

    public void helloWorldHtmlPage( WebConversation wc ) throws IOException, SAXException {
        runRepeatingTestsAndPrintResult( "testHelloWorldHtmlPage", URL_TO_HELLOWORLD_HTML_PAGE, true, wc );
    }

    public void textDocumentHelloWorld( WebConversation wc ) throws IOException, SAXException {
        runRepeatingTestsAndPrintResult( "testTextDocumentHelloWorld", getDocUrl( HELLO_WORLD_TEXT_PAGE_ID ), true, wc );
    }

    public void textPageWithTwoSmallTexts( WebConversation wc ) throws IOException, SAXException {
        runRepeatingTestsAndPrintResult( "testTextPageWithTwoSmallTexts", getDocUrl( TEXT_PAGE_WITH_TWO_SMALL_TEXTS_ID ), true, wc );
    }

    public void textPageWithOneLargerText( WebConversation wc ) throws IOException, SAXException {
        runRepeatingTestsAndPrintResult( "testTextPageWithOneLargerText", getDocUrl( TEXT_PAGE_WITH_ONE_LARGER_TEXT_ID ), true, wc );
    }

    public void textPageWithTreeIncludes( WebConversation wc ) throws IOException, SAXException {
        runRepeatingTestsAndPrintResult( "testTextPageWithThreeIncludes", getDocUrl( TEXT_PAGE_WITH_THREE_INCLUDES_ID ), true, wc );
    }

    public void testConcurrentTreeIncludes() throws InterruptedException {
        final int noOfConcurrentThreads = 5;
        System.out.println( "" + noOfConcurrentThreads + " concurrent threads, testing a page with 3 includes" );
        int timeForOneRun = 0;
        while(true) {
            timeForOneRun += testConcurrentPageRetrievals( noOfConcurrentThreads );
        }
        //printAverage( timeForOneRun / noOfConcurrentThreads / noTestRuns );
    }

    private long testConcurrentPageRetrievals( final int noOfConcurrentThreads ) throws InterruptedException {

        Thread[] clientReads = new Thread[noOfConcurrentThreads];
        PerformanceTestRunner[] performanceTestRunners = new PerformanceTestRunner[noOfConcurrentThreads];
        for (int i = 0; i < clientReads.length; i++) {
            performanceTestRunners[i] = new PerformanceTestRunner();
            clientReads[i] = new Thread( performanceTestRunners[i] );
            clientReads[i].start();
        }

        long totalTime = 0;
        for (int i = 0; i < clientReads.length; i++) {
            clientReads[i].join();
            totalTime += performanceTestRunners[i].totalTime;
        }

        return totalTime;
    }

    private class PerformanceTestRunner implements Runnable {
        long totalTime;

        public void run() {
            try {
                WebConversation wc = logOnUserUser();
                totalTime += runRepeatingTestsAndPrintResult( "testTextPageWithThreeIncludes", getDocUrl( TEXT_PAGE_WITH_THREE_INCLUDES_ID ), false, wc );
            } catch (Exception e) {
                System.out.println( "" + e + e.getMessage() );
            }
        }
    }


    public void setUp() throws IOException, SAXException {
        System.out.print( "Running some load to get the server acting like it's been running for a while." );
        for (int i = 0; i < 100; i++) {
            if( i%10 == 0 ) {
                System.out.print( "." );
            }
            WebConversation wc = logOnUserUser();
            getPageTimedTest( wc, getDocUrl( TEXT_PAGE_WITH_THREE_INCLUDES_ID ) );
        }
        System.out.println( "Finished setting up." );
    }


    private long runRepeatingTestsAndPrintResult( String heading, String url, boolean printResult, WebConversation wc ) throws IOException, SAXException {
        if (printResult) {
            System.out.println( url );
            System.out.println( heading );
        }
        long totalTime = 0;
        for (int i = 0; i < noTestRuns; i++) {
            totalTime += getPageTimedTestAndPrintResult( wc, url );
        }
        if (printResult) {
            printAverage( totalTime / noTestRuns );
        }
        return totalTime;
    }

    private long getPageTimedTestAndPrintResult( WebConversation wc, String url ) throws IOException, SAXException {
        long time = getPageTimedTest( wc, url );
        System.out.println( time );
        return time;
    }

    private static long getPageTimedTest( WebConversation wc, String url ) throws IOException, SAXException {
        Date start = new Date();
        WebResponse resp = wc.getResponse( url );
        Date end = new Date();
        long delta = end.getTime() - start.getTime();
        assertFalse( url, "The page is missing!".equalsIgnoreCase( resp.getTitle() ) );
        return delta;
    }

    private static void printAverage( long averageTime ) {
        System.out.println( "Average " + averageTime + " [ms]\n" );
    }

    private static String getDocUrl( int documentId ) {
        return ROOT_URI + GET_DOC_PATH + documentId;
    }

    private WebConversation logOnUserUser() {
        WebConversation wc = new WebConversation();
        return wc;
    }

}
