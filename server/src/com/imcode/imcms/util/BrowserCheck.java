package com.imcode.imcms.util;

import org.apache.commons.lang.StringUtils;
import org.apache.oro.text.perl.Perl5Util;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by IntelliJ IDEA.
 * User: Tommy Ullberg
 * imCode Partner AB
 * Date: 2014-sep-30
 * Time: 11:18:04
 * Fixed since last version:
 * - Win 8 support
 * - IE 10-11 support
 * - Mobile device substring index bug when empty UA.
 */
public class BrowserCheck {

	private HttpServletRequest request;
	private String userAgentOriginal;
	private String userAgent;
	private String company;
	private String name;
	private String version;
	private String mainVersion;
	private String minorVersion;
	private String os;
	private String error = "";
	private double versionNbr;
	private Perl5Util re = new Perl5Util();
	private boolean isIE = true;
	private boolean isGecko = false;
	private boolean isFirefox = false;
	private boolean isSafari = false;
	private boolean isWin = true;
	private boolean isWin2k = false;
	private boolean isWinXP = false;
	private boolean isWinVista = false;
	private boolean isWin7 = false;
	private boolean isWin8 = false;
	private boolean isMac = false;

	public BrowserCheck(HttpServletRequest request) {
		this.request = request;
		this.userAgentOriginal = StringUtils.defaultString(this.request.getHeader("User-Agent"));
		setUserAgent(userAgentOriginal);
	}

	public void setUserAgent(String httpUserAgent) {
		this.userAgent = httpUserAgent.toLowerCase();
		this.setCompany();
		this.setName();
		this.setOs();
		this.setVersions();
	}

	public String getUserAgent() {
		return userAgentOriginal;
	}

	public String getUserAgentUsed() {
		return userAgent;
	}

	private String getException(Exception e) {
		String ret = "ERROR: " + e.getMessage() + "\n";
		ret += "StackTrace:\n";
		StackTraceElement[] stackTrace = e.getStackTrace();
		for (StackTraceElement trace : stackTrace) {
			ret += trace + "\n";
		}
		return ret;
	}

	private void setVersions() {
		try {
			int tmpPos;
			String tmpString;
			if (company.equals("Microsoft") && !userAgent.contains("msie") && re.match("/rv:(\\d+\\.\\d+)/", userAgent)) {// IE11plus
				this.versionNbr = Double.parseDouble(re.group(1));
			} else if (company.equals("Microsoft")) {// IE10minus
				String str = userAgent.substring(userAgent.indexOf("msie") + 5);
				this.versionNbr = Double.parseDouble(str.substring(0, str.indexOf(";")));
			} else if (re.match("/firefox\\/(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)/", userAgent)) {
				this.versionNbr = Double.parseDouble(re.group(1) + "." + re.group(2) + re.group(3) + re.group(4));
			} else {
				tmpString = (userAgent.substring(tmpPos = (userAgent.indexOf("/")) + 1, tmpPos + userAgent.indexOf(" "))).trim();
				this.versionNbr = Double.parseDouble(tmpString.substring(0, tmpString.indexOf(" ")));
			}
		} catch (Exception e) {
			this.error += company.equals("Microsoft") + "<br/>" + !userAgent.contains("msie") + "<br/>" + re.match("/rv:(\\d+\\.\\d+)/", userAgent) + "<br/>" + getException(e);
			this.versionNbr = 0.0;
		}
		try {
			this.version = String.valueOf(versionNbr);
			this.mainVersion = version.split("\\.")[0];
			this.minorVersion = version.split("\\.")[1];
		} catch (Exception e) {
			this.error += getException(e);
			this.version = "unknown";
			this.mainVersion = "unknown";
			this.minorVersion = "unknown";
		}
	}

	private void setCompany() {
		if (userAgent.contains("msie") || userAgent.contains("trident")) {
			this.company = "Microsoft";
		} else if (userAgent.contains("opera")) {
			this.company = "Opera Software";
		} else if (userAgent.contains("mozilla")) {
			this.company = "Netscape Communications";
		} else {
			this.company = "unknown";
		}
	}

	public String getError() {
		return error;
	}

	public String getCompany() {
		return company;
	}

	private void setName() {
		if (company.equals("Microsoft")) {
			this.name = "Microsoft Internet Explorer";
		} else if (company.equals("Netscape Communications")) {
			this.name = "Netscape Navigator";
		} else if (company.equals("Operasoftware")) {
			this.name = "Operasoftware Opera";
		} else {
			this.name = "unknown";
		}
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String getMainVersion() {
		return mainVersion;
	}

	public String getMinorVersion() {
		return minorVersion;
	}

	public double getVersionNumber() {
		return versionNbr;
	}

	private void setOs() {
		try {
			if (isWin()) {
				if (userAgent.matches(".+Windows NT 6\\.[23]") || userAgent.contains("Windows NT 8")) {
					this.os = "Windows 8";
					this.isWin8 = true;
				} else if (userAgent.matches(".+Windows NT 6\\.1") || userAgent.contains("Windows NT 7")) {
					this.os = "Windows 7";
					this.isWin7 = true;
				} else if (userAgent.contains("Windows NT 6.0")) {
					this.os = "Windows Vista";
					this.isWinVista = true;
				} else if (userAgent.contains("Windows NT 5.1")) {
					this.os = "Windows XP";
					this.isWinXP = true;
				} else if (userAgent.contains("Windows NT 5.0")) {
					this.os = "Windows 2000";
					this.isWin2k = true;
				} else if (userAgent.contains("windows 98") || userAgent.contains("win98")) {
					this.os = "Windows 98";
				} else if (userAgent.contains("windows 95") || userAgent.contains("win95")) {
					this.os = "Windows 95";
				} else if (userAgent.contains("winnt")) {
					this.os = "Windows NT";
				} else if (userAgent.contains("win16") || userAgent.contains("windows 3.")) {
					this.os = "Windows 3.x";
				}
			}
		} catch (Exception e) {
			this.error += getException(e);
			this.os = "unknown";
		}
	}

	public String getOs() {
		return os;
	}

	public boolean isIE() {
		return userAgent.contains("msie") || userAgent.contains("trident");
	}

	public boolean isIE55plus() {
		return isIE() && versionNbr >= 5.5;
	}

	public boolean isIE6plus() {
		return isIE() && versionNbr >= 6.0;
	}

	public boolean isIE6() {
		return isIE() && versionNbr >= 6.0 && versionNbr < 7.0;
	}

	public boolean isIE7plus() {
		return isIE() && versionNbr >= 7.0;
	}

	public boolean isIE7minus() {
		return isIE() && versionNbr < 8.0;
	}

	public boolean isIE7() {
		return isIE() && versionNbr >= 7.0 && versionNbr < 8.0;
	}

	public boolean isIE7compatMode() {
		return isIE7compatModeIE8() || isIE7compatModeIE9();
	}

	public boolean isIE7compatModeIE8() {
		return isIE7() && userAgent.contains("trident/4");
	}

	public boolean isIE7compatModeIE9() {
		return isIE7() && userAgent.contains("trident/5");
	}

	public boolean isIE8plus() {
		return isIE() && versionNbr >= 8.0;
	}

	public boolean isIE8minus() {
		return isIE() && versionNbr < 9.0;
	}

	public boolean isIE8() {
		return isIE() && versionNbr >= 8.0 && versionNbr < 9.0;
	}

	public boolean isIE9plus() {
		return isIE() && versionNbr >= 9.0;
	}

	public boolean isIE9() {
		return isIE() && versionNbr >= 9.0 && versionNbr < 10.0;
	}

	public boolean isIE10plus() {
		return isIE() && versionNbr >= 10.0;
	}

	public boolean isIE10() {
		return isIE() && versionNbr >= 10.0 && versionNbr < 11.0;
	}

	public boolean isIE11plus() {
		return isIE() && versionNbr >= 11.0;
	}

	public boolean isIE11() {
		return isIE() && versionNbr >= 11.0 && versionNbr < 12.0;
	}

	public boolean isGecko() {
		return userAgent.contains("gecko") && !userAgent.contains("like gecko");
	}

	public boolean isGecko5plus() {
		return isGecko() && new Double(version) >= 5.0;
	}

	public boolean isFirefox() {
		return userAgent.contains("firefox");
	}

	public boolean isFirefox2plus() {
		return isFirefox() && versionNbr >= 2.0;
	}

	public boolean isFirefox3plus() {
		return isFirefox() && versionNbr >= 3.0;
	}

	public boolean isFirefox35plus() {
		return isFirefox() && versionNbr >= 3.5;
	}

	public boolean isSafari() {
		return userAgent.contains("safari");
	}

	public boolean isWebKit() {
		return userAgent.contains("webkit");
	}

	public boolean isIphone() {
		return userAgent.contains("iphone");
	}

	public boolean isIpad() {
		return userAgent.contains("ipad");
	}

	public boolean isAndroid() {
		return userAgent.contains("android");
	}

	public boolean isMobileDevice() {
		// From http://detectmobilebrowsers.com/ Modified by Tommy Ullberg
		return isIphone() || isIpad() || isAndroid() || userAgent.matches("(?i).*(android.+mobile|avantgo|bada\\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od|ad)|iris|kindle|lge |maemo|meego.+mobile|midp|mmp|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\\.(browser|link)|vodafone|wap|windows (ce|phone)|xda|xiino).*") || (userAgent + "xxxxx").substring(0, 4).matches("(?i)1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\\-(n|u)|c55\\/|capi|ccwa|cdm\\-|cell|chtm|cldc|cmd\\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\\-s|devi|dica|dmob|do(c|p)o|ds(12|\\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\\-|_)|g1 u|g560|gene|gf\\-5|g\\-mo|go(\\.w|od)|gr(ad|un)|haie|hcit|hd\\-(m|p|t)|hei\\-|hi(pt|ta)|hp( i|ip)|hs\\-c|ht(c(\\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\\-(20|go|ma)|i230|iac( |\\-|\\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\\/)|klon|kpt |kwc\\-|kyo(c|k)|le(no|xi)|lg( g|\\/(k|l|u)|50|54|\\-[a-w])|libw|lynx|m1\\-w|m3ga|m50\\/|ma(te|ui|xo)|mc(01|21|ca)|m\\-cr|me(di|rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\\-2|po(ck|rt|se)|prox|psio|pt\\-g|qa\\-a|qc(07|12|21|32|60|\\-[2-7]|i\\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\\-|oo|p\\-)|sdk\\/|se(c(\\-|0|1)|47|mc|nd|ri)|sgh\\-|shar|sie(\\-|m)|sk\\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\\-|v\\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\\-|tdg\\-|tel(i|m)|tim\\-|t\\-mo|to(pl|sh)|ts(70|m\\-|m3|m5)|tx\\-9|up(\\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\\-|your|zeto|zte\\-");
	}

	public boolean isMac() {
		return userAgent.contains("mac");
	}

	public boolean isWin() {
		return !isMac();
	}

	public boolean isWin2k() {
		return isWin2k;
	}

	public boolean isWinXP() {
		return isWinXP;
	}

	public boolean isWinVista() {
		return isWinVista;
	}

	public boolean isWin7() {
		return isWin7;
	}

	public boolean isWin8() {
		return isWin8;
	}

	/*
	<pre style="font-size:11px;">
	out.print("<br/>browser.getUserAgent():     " + browser.getUserAgent());
	out.print("<br/>browser.getName():          " + browser.getName());
	out.print("<br/>browser.getOs():            " + browser.getOs());
	out.print("<br/>browser.getCompany():       " + browser.getCompany());
	out.print("<br/>browser.getVersion():       " + browser.getVersion());
	out.print("<br/>browser.getMainVersion():   " + browser.getMainVersion());
	out.print("<br/>browser.getMinorVersion():  " + browser.getMinorVersion());
	out.print("<br/>browser.getVersionNumber(): " + browser.getVersionNumber());
	out.print("<br/>browser.isWin():            " + browser.isWin());
	out.print("<br/>browser.isMac():            " + browser.isMac());
	out.print("<br/>browser.isGecko():          " + browser.isGecko());
	out.print("<br/>browser.isGecko5plus():     " + browser.isGecko5plus());
	out.print("<br/>browser.isFirefox():        " + browser.isFirefox());
	out.print("<br/>browser.isFirefox2plus():   " + browser.isFirefox2plus());
	out.print("<br/>browser.isFirefox3plus():   " + browser.isFirefox3plus());
	out.print("<br/>browser.isFirefox35plus():  " + browser.isFirefox35plus());
	out.print("<br/>browser.isIE():             " + browser.isIE());
	out.print("<br/>browser.isIE55plus():       " + browser.isIE55plus());
	out.print("<br/>browser.isIE6():            " + browser.isIE6());
	out.print("<br/>browser.isIE6plus():        " + browser.isIE6plus());
	out.print("<br/>browser.isIE7():            " + browser.isIE7());
	out.print("<br/>browser.isIE7plus():        " + browser.isIE7plus());
	out.print("<br/>browser.isIE8():            " + browser.isIE8());
	out.print("<br/>browser.isIE8plus():        " + browser.isIE8plus());
	</pre> */
}