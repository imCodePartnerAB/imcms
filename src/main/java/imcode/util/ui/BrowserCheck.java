package imcode.util.ui;

import org.apache.commons.lang.StringUtils;
import org.apache.oro.text.perl.Perl5Util;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by IntelliJ IDEA.
 * User: Tommy Ullberg
 * imCode Partner AB
 * Date: 2009-okt-28
 * Time: 07:54:43
 */
public class BrowserCheck {
	// TODO: This is not ready, but it works. Can detect IE, Firefox and Gecko versions
	HttpServletRequest request;
	String userAgent;
	String company;
	String name;
	String version;
	String mainVersion;
	String minorVersion;
	String os;
	double versionNbr;
	Perl5Util re = new Perl5Util() ;
	boolean isWin2k = false ;
	boolean isWinXP = false ;
	boolean isWinVista = false ;
	boolean isWin7 = false ;
	
	public BrowserCheck(HttpServletRequest request) {
		this.request = request;
		this.setUserAgent(StringUtils.defaultString(this.request.getHeader("User-Agent")));
		this.setCompany();
		this.setName();
		this.setVersion();
		this.setMainVersion();
		this.setMinorVersion();
		this.setVersionNumber();
		this.setOs();
	}
	
	public void setUserAgent(String httpUserAgent) {
		this.userAgent = httpUserAgent.toLowerCase();
	}
	
	public String getUserAgent () {
		return userAgent;
	}
	
	private void setCompany() {
		if (this.userAgent.contains("msie")) {
			this.company = "Microsoft";
		} else if (this.userAgent.contains("opera")) {
			this.company = "Opera Software";
		} else if (this.userAgent.contains("mozilla")) {
			this.company = "Netscape Communications";
		} else {
			this.company = "unknown";
		}
	}
	
	
	public String getCompany() {
		return company;
	}
	
	private void setName() {
		if (this.company.equals("Microsoft")) {
			this.name = "Microsoft Internet Explorer";
		} else if (this.company.equals("Netscape Communications")) {
			this.name = "Netscape Navigator";
		} else if (this.company.equals("Operasoftware")) {
			this.name = "Operasoftware Opera";
		} else {
			this.name = "unknown";
		}
	}
	
	public String getName() {
		return name;
	}
	
	private void setVersion() {
		try {
			int tmpPos;
			String tmpString;
			if (this.company.equals("Microsoft")) {
				String str = this.userAgent.substring(this.userAgent.indexOf("msie") + 5);
				this.version = str.substring(0, str.indexOf(";"));
			} else {
				tmpString = (this.userAgent.substring(tmpPos = (this.userAgent.indexOf("/")) + 1, 
				tmpPos + this.userAgent.indexOf(" "))).trim();
				this.version = tmpString.substring(0, tmpString.indexOf(" "));
			}
		} catch (Exception e) {
			this.version = "unknown" ;
		}
	}
	
	public String getVersion() {
		return version;
	}
	
	private void setMainVersion() {
		try {
			this.mainVersion = this.version.substring(0, this.version.indexOf(".")) ;
		} catch (Exception e) {
			this.mainVersion = "unknown" ;
		}
	}
	
	public String getMainVersion() {
		return mainVersion;
	}
	
	private void setMinorVersion() {
		try {
			this.minorVersion = this.version.substring(this.version.indexOf(".") + 1).trim() ;
		} catch (Exception e) {
			this.minorVersion = "unknown" ;
		}
	}
	
	public String getMinorVersion() {
		return minorVersion;
	}
	
	private void setVersionNumber() {
		try {
			int tmpPos;
			String tmpString;
			if (this.company.equals("Microsoft")) {
				String str = this.userAgent.substring(this.userAgent.indexOf("msie") + 5);
				this.versionNbr = Double.parseDouble(str.substring(0, str.indexOf(";")));
			} else if (re.match("/firefox\\/(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)/", userAgent)) {
				this.versionNbr = Double.parseDouble(re.group(1) + "." + re.group(2) + re.group(3) + re.group(4));
			} else {
				tmpString = (this.userAgent.substring(tmpPos = (this.userAgent.indexOf("/")) + 1, 
				tmpPos + this.userAgent.indexOf(" "))).trim();
				this.versionNbr = Double.parseDouble(tmpString.substring(0, tmpString.indexOf(" ")));
			}
		} catch (Exception e) {
			this.versionNbr = 5.0 ;
		}
	}
	
	public double getVersionNumber () {
		return versionNbr;
	}
	
	private void  setOs() {
		try {
			if (isWin()) {
				if (this.userAgent.matches(".+Windows NT 6\\.[1-9]") || this.userAgent.contains("Windows NT 7")) {
					this.os = "Windows 7";
					this.isWin7 = true ;
				} else if (this.userAgent.contains("Windows NT 6.0")) {
					this.os = "Windows Vista";
					this.isWinVista = true ;
				} else if (this.userAgent.contains("Windows NT 5.1")) {
					this.os = "Windows XP";
					this.isWinXP = true ;
				} else if (this.userAgent.contains("Windows NT 5.0")) {
					this.os = "Windows 2000";
					this.isWin2k = true ;
				} else if (this.userAgent.contains("windows 98") || this.userAgent.contains("win98")) {
					this.os = "Windows 98";
				} else if (this.userAgent.contains("windows 95") || this.userAgent.contains("win95")) {
					this.os = "Windows 95";
				} else if (this.userAgent.contains("winnt")) {
					this.os = "Windows NT";
				} else if (this.userAgent.contains("win16") || this.userAgent.contains("windows 3.")) {
					this.os = "Windows 3.x";
				}
			}
		} catch (Exception e) {
			this.os = "unknown" ;
		}
	}
	
	public String getOs() {
		return os;
	}
	
	public boolean isIE () {
		return userAgent.contains("msie");
	}
	public boolean isIE55plus () {
		return isIE() && versionNbr >= 5.5;
	}
	public boolean isIE6plus () {
		return isIE() && versionNbr >= 6.0;
	}
	public boolean isIE6 () {
		return isIE() && versionNbr >= 6.0 && versionNbr < 7.0;
	}
	public boolean isIE7plus () {
		return isIE() && versionNbr >= 7.0;
	}
	public boolean isIE7 () {
		return isIE() && versionNbr >= 7.0 && versionNbr < 8.0;
	}
	public boolean isIE8plus () {
		return isIE() && versionNbr >= 8.0;
	}
	public boolean isIE8 () {
		return isIE() && versionNbr >= 8.0 && versionNbr < 9.0;
	}
	public boolean isIE9plus () {
		return isIE() && versionNbr >= 9.0;
	}
	public boolean isIE9 () {
		return isIE() && versionNbr >= 9.0 && versionNbr < 10.0;
	}
	
	public boolean isGecko () {
		return userAgent.contains("gecko");
	}
	public boolean isGecko5plus () {
		return isGecko() && new Double(version) >= 5.0;
	}
	
	public boolean isFirefox () {
		return userAgent.contains("firefox");
	}
	public boolean isFirefox2plus () {
		return isFirefox() && versionNbr >= 2.0;
	}
	public boolean isFirefox3plus () {
		return isFirefox() && versionNbr >= 3.0;
	}
	public boolean isFirefox35plus () {
		return isFirefox() && versionNbr >= 3.5;
	}
	
	public boolean isSafari () {
		return userAgent.contains("safari");
	}
	
	public boolean isWebKit () {
		return userAgent.contains("webkit");
	}
	
	public boolean isMac () {
		return userAgent.contains("mac");
	}
	
	public boolean isWin () {
		return !isMac();
	}

	public boolean isWin2k () {
		return isWin2k;
	}

	public boolean isWinXP () {
		return isWinXP;
	}

	public boolean isWinVista () {
		return isWinVista;
	}

	public boolean isWin7 () {
		return isWin7;
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
	out.print("<br/>browser.isSafari():         " + browser.isSafari());
	out.print("<br/>browser.isWebKit():         " + browser.isWebKit());
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
