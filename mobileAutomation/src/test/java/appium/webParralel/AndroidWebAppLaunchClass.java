package appium.webParralel;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;

public class AndroidWebAppLaunchClass {
  
	public AndroidDriver<MobileElement> driver;
	public DesiredCapabilities capabilities;
	public URL url;
	
	
  @Parameters({ "deviceID","version","port", "serverurl" })
  @BeforeClass
  public void beforeClass(String deviceID,String verison,@Optional String port,@Optional String serverurl) throws Exception {
	  

	// Create object of  DesiredCapabilities class and specify android platform
	  capabilities=DesiredCapabilities.android();
	 
	 
	// set the capability to execute test in chrome browser
	 	capabilities.setCapability(MobileCapabilityType.BROWSER_NAME,BrowserType.CHROME);
	 
	// set the capability to execute our test in Android Platform
	 //  capabilities.setCapability(MobileCapabilityType.PLATFORM,Platform.ANDROID);
	 
	// we need to define platform name
	  capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME,"Android");
	 
	// Set the device name as well (you can give any name)
	  capabilities.setCapability(MobileCapabilityType.DEVICE_NAME,deviceID);
	  capabilities.setCapability(MobileCapabilityType.UDID,deviceID);
	 // set the android version as well 
	  capabilities.setCapability(MobileCapabilityType.VERSION,verison);
	  capabilities.setCapability(MobileCapabilityType.FULL_RESET,false);
	  capabilities.setCapability(MobileCapabilityType.NO_RESET,true);
	  if(deviceID.contains("emulator"))
	  {
	  capabilities.setCapability("chromedriverExecutable", "C:\\Users\\Nivas_Sahoo\\eclipse-workspace\\webParralel\\chromedriver.exe");
	  }
	  // Create object of URL class and specify the appium server address
	  url= new URL("http://127.0.0.1:4723/wd/hub");
	 
	// Create object of  AndroidDriver class and pass the url and capability that we created
	  driver = new AndroidDriver<MobileElement>(url, capabilities);
	  System.out.println("Inside Desired capablilities and going to launch driver");
	// Open url
	  driver.get("http://www.google.com");
	  driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	 
	 
	
  }

  AndroidDriver<MobileElement> getDriver()
  {
	  if(driver==null)
	  {
		  driver = new AndroidDriver<MobileElement>(url, capabilities);
		  return driver;
	  }
	  else
	  {
		  return driver;
	  }
	  
  }


  @BeforeSuite
  public void beforeSuite() {
  }

  @AfterSuite
  public void afterSuite() {
  }
  

  
  

}
