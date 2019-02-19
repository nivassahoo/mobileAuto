package appium.webParralel;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;

import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;

public class AndroidStartUp2 {
  static DesiredCapabilities capabilities;
	static AppiumDriver<MobileElement> driver;
	static URL url;
	static File src;
	static FileInputStream fis;
	static Properties pro;
	static AppiumServiceBuilder appiumServiceBuilder;
	static AppiumDriverLocalService appiumDriverLocalService;
	static String serviceURL;
	
	@BeforeSuite(enabled=false)
	public void configIntilization() throws Exception
	{
		
		 src=new File("src/Config/objectRepo.properties");
		 
		
		 fis=new FileInputStream(src);
		 
		
		 pro=new Properties();
		 
		
		pro.load(fis);
		

	}
	
	
	
	@BeforeTest(enabled=false)
	@Parameters({ "deviceName","version","chromePort", "portNum" })
	public void test(String deviceName,String version,String chromePort,@Optional String portNum) throws Exception
	{
		 capabilities=DesiredCapabilities.android();
		 System.out.println("Inside Desired Capabilities STartUP2");
		 capabilities.setCapability(MobileCapabilityType.BROWSER_NAME,BrowserType.CHROME);
		 
	/*	 System.out.println("Start SetUP");
         System.setProperty("proxy.authentication.mode","NTLM");
         System.setProperty("http.auth.ntlm.domain","REG1");
         System.setProperty("https.proxyHost","bcproxy.sgp.dbs.com");
         System.setProperty("https.proxyPort","8080");
         System.setProperty("https.proxyUser",System.getProperty("user.name"));
         System.setProperty("http.proxyHost","bcproxy.sgp.dbs.com");
         System.setProperty("http.proxyPort","8080");
         System.setProperty("http.proxyUser",System.getProperty("user.name"));
         // System.setProperty("javax.net.ssl.trustStrore", "resources\\jssecacerts");
         // System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
         System.out.println("\nProvided Proxy Details:\n"
                         + "https.proxyHost: " + System.getProperty("https.proxyHost") +"\n"
                         + "https.proxyPort: " + System.getProperty("https.proxyPort") +"\n"
                         + "https.proxyUser: " + System.getProperty("https.proxyUser") +"\n"
                         + "http.proxyHost: " + System.getProperty("http.proxyHost") +"\n"
                         + "http.proxyPort: " + System.getProperty("http.proxyPort") +"\n"
                         + "http.proxyUser: " + System.getProperty("http.proxyUser") +"\n"
                         + "javax.net.ssl.trustStrore: " + System.getProperty("javax.net.ssl.trustStrore") +"\n");*/
		 
		 capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME,Platform.ANDROID);
	
		 capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION,version);
		 
		/* capabilities.setCapability("appPackage","com.android.chrome");
		 
		 capabilities.setCapability("appPackage","com.google.android.apps.chrome.Main");*/
		 
//		 capabilities.setCapability("--session-override",true);
//		 
//		 capabilities.setCapability("systemPort", Integer.parseInt(chromePort));
		 
		 capabilities.setCapability("chromeDriverPort", Integer.parseInt(chromePort));
		 
		 capabilities.setCapability("clearSystemFiles", true);
		 
//		 capabilities.setCapability("--chromedriver-executable", "/Users/nivassahoo/Documents/workspace_nivas/nativeDigiNext/chromedriver");
		 
		 capabilities.setCapability(MobileCapabilityType.DEVICE_NAME,deviceName);
		 capabilities.setCapability(MobileCapabilityType.UDID,deviceName);
	//	 capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 120);
		 capabilities.setCapability("automationName", "uiautomator2");
	//	 capabilities.setCapability("automationName", "appium");
		
		 appiumServiceBuilder = new AppiumServiceBuilder();
			appiumServiceBuilder.withIPAddress("127.0.0.1");
			appiumServiceBuilder.usingAnyFreePort();
			appiumServiceBuilder.withArgument(GeneralServerFlag.SESSION_OVERRIDE);
			appiumServiceBuilder.withArgument(GeneralServerFlag.LOG_LEVEL,"error");
	//		appiumServiceBuilder.withCapabilities(capabilities);
		 
			appiumDriverLocalService = AppiumDriverLocalService.buildService(appiumServiceBuilder);
			appiumDriverLocalService.start();
		 url= new URL("http://127.0.0.1:4723/wd/hub");
	//	 System.out.println("Service URL "+serviceURL);
		 url= new URL(serviceURL);
		 
		 driver = new AppiumDriver<MobileElement>(appiumDriverLocalService.getUrl(),capabilities);
		 
		 driver.get("https://www.google.com/");
		 System.out.println(driver.getSessionDetails());
		 
	}
	
	/*public void startServer() {
		
		
		//Build the Appium service
		appiumServiceBuilder = new AppiumServiceBuilder();
		appiumServiceBuilder.withIPAddress("127.0.0.1");
		appiumServiceBuilder.usingPort(4723);
		appiumServiceBuilder.withCapabilities(cap);
		appiumServiceBuilder.withArgument(GeneralServerFlag.SESSION_OVERRIDE);
		appiumServiceBuilder.withArgument(GeneralServerFlag.LOG_LEVEL,"error");
		
		//Start the server with the builder
		appiumDriverLocalService = AppiumDriverLocalService.buildService(appiumServiceBuilder);
		appiumDriverLocalService.start();
	}*/
	
	/*public void stopServer() {
		service.stop();
	}*/
	
	
	
	public AppiumDriver<MobileElement> getDriver()
	{
		return driver;
	}

}
