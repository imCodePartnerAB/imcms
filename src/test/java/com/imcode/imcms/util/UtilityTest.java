package com.imcode.imcms.util;

import imcode.util.Utility;
import junit.framework.TestCase;

public class UtilityTest extends TestCase {

    public void testIpConvert_Expect_CorrectConverting() {
        final String ip = "150.150.140.130";
        final long ipLong = Utility.ipStringToLong(ip);
        final String convertedIp = Utility.ipLongToString(ipLong);

        assertEquals(ip, convertedIp);
    }

    public void test_IpStringToLong_When_LastDecadeDiffers_Expect_OneLessThanOther() {
        final String lowerIp = "100.100.100.100";
        final String higherIp = "100.100.100.101";

        final long lowerIpLong = Utility.ipStringToLong(lowerIp);
        final long higherIpLong = Utility.ipStringToLong(higherIp);

        assertTrue(lowerIpLong < higherIpLong);
    }

    public void test_IpStringToLong_When_LastDecadeDiffers_Expect_IpBetweenTwoOthers() {
        final String lowerIp = "100.100.100.100";
        final String middleIp = "100.100.100.101";
        final String higherIp = "100.100.100.102";

        final long lowerIpLong = Utility.ipStringToLong(lowerIp);
        final long middleIpLong = Utility.ipStringToLong(middleIp);
        final long higherIpLong = Utility.ipStringToLong(higherIp);

        assertTrue((lowerIpLong < middleIpLong) && (middleIpLong < higherIpLong));
    }

    public void test_IpStringToLong_When_ThirdDecadeDiffers_Expect_IpBetweenTwoOthers() {
        final String lowerIp = "100.100.100.100";
        final String middleIp = "100.100.101.100";
        final String higherIp = "100.100.102.100";

        final long lowerIpLong = Utility.ipStringToLong(lowerIp);
        final long middleIpLong = Utility.ipStringToLong(middleIp);
        final long higherIpLong = Utility.ipStringToLong(higherIp);

        assertTrue((lowerIpLong < middleIpLong) && (middleIpLong < higherIpLong));
    }

    public void test_IpStringToLong_When_SecondDecadeDiffers_Expect_IpBetweenTwoOthers() {
        final String lowerIp = "100.100.100.100";
        final String middleIp = "100.101.100.100";
        final String higherIp = "100.102.100.100";

        final long lowerIpLong = Utility.ipStringToLong(lowerIp);
        final long middleIpLong = Utility.ipStringToLong(middleIp);
        final long higherIpLong = Utility.ipStringToLong(higherIp);

        assertTrue((lowerIpLong < middleIpLong) && (middleIpLong < higherIpLong));
    }

    public void test_IpStringToLong_When_FirstDecadeDiffers_Expect_IpBetweenTwoOthers() {
        final String lowerIp = "100.100.100.100";
        final String middleIp = "101.100.100.100";
        final String higherIp = "102.100.100.100";

        final long lowerIpLong = Utility.ipStringToLong(lowerIp);
        final long middleIpLong = Utility.ipStringToLong(middleIp);
        final long higherIpLong = Utility.ipStringToLong(higherIp);

        assertTrue((lowerIpLong < middleIpLong) && (middleIpLong < higherIpLong));
    }

    public void test_IpStringToLong_When_AllDecadesDiffers_Expect_IpBetweenTwoOthers() {
        final String lowerIp = "100.100.100.100";
        final String middleIp = "101.101.101.101";
        final String higherIp = "102.102.102.102";

        final long lowerIpLong = Utility.ipStringToLong(lowerIp);
        final long middleIpLong = Utility.ipStringToLong(middleIp);
        final long higherIpLong = Utility.ipStringToLong(higherIp);

        assertTrue((lowerIpLong < middleIpLong) && (middleIpLong < higherIpLong));
    }

}
