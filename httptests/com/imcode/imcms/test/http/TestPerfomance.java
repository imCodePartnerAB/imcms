package com.imcode.imcms.test.http;

import junit.framework.TestCase;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;

import java.io.IOException;
import java.util.Date;

import org.xml.sax.SAXException;

/**
 * Created by IntelliJ IDEA.
 * User: Hasse
 * Date: 2004-mar-08
 * Time: 18:23:08
 * To change this template use File | Settings | File Templates.
 */
public class TestPerfomance extends TestCase {
//    private static String HOST_URI = "http://localhost:8080/1_8-BRANCH";
    private static String GET_DOC_PATH = "/servlet/GetDoc?meta_id=";
    private String URL_TO_HELLOWORLD_HTML_PAGE = HOST_URI + "/imcms/test/helloworld.html";


    /* To generate the pages to measure.
     * Go to the API samples page and find the link at the bottom createPerformanceTestData.jsp
     * Run that page and fill in the numbers below.
    */
    // Volvo RATATOSK
    private static String HOST_URI = "http://localhost:8080/imcms";
    private static int HELLO_WORLD_TEXT_PAGE_ID = 2090;
    private static int TEXT_PAGE_WITH_SOME_TEXTS_ID = 2091;
    private static int TEXT_PAGE_WITH_ONE_INCLUDE_ID = 2093;
    private static int TEXT_PAGE_WITH_THREE_INCLUDES_ID = 2094;

/*
    private static int HELLO_WORLD_TEXT_PAGE_ID = 1002;
    private static int TEXT_PAGE_WITH_SOME_TEXTS_ID = 1003;
    private static int TEXT_PAGE_WITH_ONE_INCLUDE_ID = 1005;
    private static int TEXT_PAGE_WITH_THREE_INCLUDES_ID = 1006;
*/
    public void testAllPerformance() throws IOException, SAXException {
        getPageTimedTestAndDisplayResult( "Hello world html page ", URL_TO_HELLOWORLD_HTML_PAGE );
        runTimedPageRetrieval("Textdocument (" + HELLO_WORLD_TEXT_PAGE_ID + ") \'Hello World\' Page [ms] :  ", HELLO_WORLD_TEXT_PAGE_ID);
        runTimedPageRetrieval("Textdocument (" + TEXT_PAGE_WITH_SOME_TEXTS_ID + ") with some texts [ms] :   ", TEXT_PAGE_WITH_SOME_TEXTS_ID);
        runTimedPageRetrieval("Textdocument (" + TEXT_PAGE_WITH_ONE_INCLUDE_ID + ") with one include [ms] : ", TEXT_PAGE_WITH_ONE_INCLUDE_ID);
        runTimedPageRetrieval("Textdocument (" + TEXT_PAGE_WITH_THREE_INCLUDES_ID + ") with three includes [ms] :   ", TEXT_PAGE_WITH_THREE_INCLUDES_ID);
    }

    public void testAllMultipleTimesToSeeImprovmentsOverTime() throws IOException, SAXException {
        for( int i = 0; i < 10 ; i++ ) {
            testAllPerformance();
        }
    }

    public void testConcurrentPageRetrievalsMultipleTimesToSePerformanceImprovmentOverTime() throws InterruptedException {
        for( int i = 0; i < 100 ; i++ ) {
            testConcurrentPageRetrievals();
        }
    }

    public void testMultipleTimedRetrievalsOfTextPageWithThreeIncludes() throws IOException, SAXException {
        int count = 30;
        long total = testMultipleTimedRetrievalsOfTextPageWithThreeIncludes( count );
        System.out.println("Average [ms] : " + total/count );
    }

    public void testConcurrentPageRetrievals() throws InterruptedException {
        final int noOfConcurrentThreads = 5;
        final int noOfTestsInEachThread = 1;
        long totalTime = testConcurrentPageRetrievals( noOfConcurrentThreads, noOfTestsInEachThread );
        System.out.println("Average [ms] " + totalTime/noOfTestsInEachThread/noOfConcurrentThreads );
    }

    public void testMultipleConcurrentPageRetrievals() throws InterruptedException {
        final int noOfConcurrentThreads = 5;
        final int noOfTestsInEachThread = 1;
        final int noRuns = 4;
        for( int i = 0; i < noRuns ; i++ ) {
            int timeForOneRun = 0;
            timeForOneRun += testConcurrentPageRetrievals( noOfConcurrentThreads, noOfTestsInEachThread );
            System.out.println("Average [ms] " + timeForOneRun/noOfTestsInEachThread/noOfConcurrentThreads );
        }
    }

    private static long testMultipleTimedRetrievalsOfTextPageWithThreeIncludes( int count ) throws IOException, SAXException {
        long total = 0;
        for( int i = 0; i < count ; i++ ) {
            total += runTimedPageRetrieval("Textdocument (" + TEXT_PAGE_WITH_THREE_INCLUDES_ID + ") with three includes [ms] :  ", TEXT_PAGE_WITH_THREE_INCLUDES_ID);
        }
        return total;
    }

    private static long testConcurrentPageRetrievals( final int noOfConcurrentThreads, final int noOfTestsInEachThread ) throws InterruptedException {

        Thread[] clientReads = new Thread[noOfConcurrentThreads];
        PerformanceTestRunner[] performanceTestRunners = new PerformanceTestRunner[noOfConcurrentThreads];
        for (int i = 0; i < clientReads.length; i++) {
            performanceTestRunners[i] =  new PerformanceTestRunner( noOfTestsInEachThread );
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

    private static class PerformanceTestRunner implements Runnable {
        long totalTime;
        int count;

        PerformanceTestRunner( int count ) {
            this.count = count;
        }

        public void run() {
            try {
                totalTime += testMultipleTimedRetrievalsOfTextPageWithThreeIncludes(count);
            } catch (Exception e) {
                System.out.println("" + e + e.getMessage());
            }
        }
    }

    private static long runTimedPageRetrieval(String testDescription, int documentId ) throws IOException, SAXException {
        String getDoc1001 = HOST_URI + GET_DOC_PATH + documentId;
        return getPageTimedTestAndDisplayResult(testDescription, getDoc1001);
    }

    private static long getPageTimedTestAndDisplayResult(String testDescription, String url) throws IOException, SAXException {
        long time = getPageTimedTest(url);
        System.out.println( testDescription + time );
        return time;
    }

    private static long getPageTimedTest(String url) throws IOException, SAXException {
        WebConversation wc = new WebConversation();
        Date start = new Date();
        WebResponse resp = wc.getResponse(url);
        Date end = new Date();
        long delta = end.getTime() - start.getTime();
        assertFalse( url, "The page is missing!".equalsIgnoreCase( resp.getTitle() ) );
        return delta;
    }
}
