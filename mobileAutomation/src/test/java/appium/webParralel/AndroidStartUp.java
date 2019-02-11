package appium.webParralel;

import org.testng.annotations.Test;

public class AndroidStartUp {
   DesiredCapabilities capabilities;
	 AndroidDriver<MobileElement> driver;
	 URL url;
	static File src;
	static FileInputStream fis;
	static Properties pro;
	 AppiumServiceBuilder appiumServiceBuilder;
	 AppiumDriverLocalService appiumDriverLocalService;
	 String serviceURL;
	
	@BeforeSuite(enabled=true)
	@Parameters("portNum")
	public void configIntilization(@Optional Integer portNum) throws Exception
	{
		
		 src=new File("src/Config/objectRepo.properties");
		 
		
		 fis=new FileInputStream(src);
		 
		
		 pro=new Properties();
		 
		
		pro.load(fis);
		
	//	StartAppiumServer();
		
		//Building Service
		/*appiumServiceBuilder = new AppiumServiceBuilder();
		appiumServiceBuilder.withIPAddress("127.0.0.1");
		appiumServiceBuilder.usingAnyFreePort();*/
		
	//	appiumServiceBuilder.withCapabilities(cap);  //Work on the below
	//	appiumServiceBuilder.usingDriverExecutable(new File("/usr/local/bin/node"));
	//	appiumServiceBuilder.withAppiumJS(new File("/Users/nivassahoo/homebrew/lib/node_modules/appium/build/lib/main.js"));
		
		/*appiumServiceBuilder.withArgument(GeneralServerFlag.SESSION_OVERRIDE);
		appiumServiceBuilder.withLogFile(new File("/Users/nivassahoo/AppiumLogs.log"));*/
	//	appiumServiceBuilder.withArgument(GeneralServerFlag.LOG_LEVEL,"error");
		
		//Start the server with the builder
	
	//	appiumDriverLocalService = AppiumDriverLocalService.buildService(appiumServiceBuilder);
	
		//	appiumDriverLocalService = AppiumDriverLocalService.buildDefaultService();
		
	//	appiumDriverLocalService.start();
		
	//	serviceURL=appiumDriverLocalService.getUrl().toString();
	}
	
	@AfterSuite
	public void tearDown()
	{
	/*	appiumDriverLocalService.stop();
		runInTerminal("/usr/bin/killall -KILL node");
		runInTerminal("/usr/bin/killall -KILL chromedriver");*/
	}
	
	@AfterTest
	public void testShut()
	{
		if (driver != null) {
	        driver.quit();
	    }
	}
	
	public void StartAppiumServer()
	{
		//Building Service
 		appiumServiceBuilder = new AppiumServiceBuilder();
 		appiumServiceBuilder.withIPAddress("127.0.0.1");
 		appiumServiceBuilder.usingAnyFreePort();
 	//	appiumServiceBuilder.withCapabilities(capability);
 		
 		// 		appiumServiceBuilder.withCapabilities(cap);  //Work on the below
 	 	//	appiumServiceBuilder.usingDriverExecutable(new File("/usr/local/bin/node"));
 	 	//	appiumServiceBuilder.withAppiumJS(new File("/Users/nivassahoo/homebrew/lib/node_modules/appium/build/lib/main.js"));
 		
 		appiumServiceBuilder.withArgument(GeneralServerFlag.SESSION_OVERRIDE);
 		appiumServiceBuilder.withLogFile(new File("/Users/nivassahoo/AppiumLogs.log"));
 	//	appiumServiceBuilder.withArgument(GeneralServerFlag.LOG_LEVEL,"error");
 		
 		//Start the server with the builder
 		appiumDriverLocalService = AppiumDriverLocalService.buildService(appiumServiceBuilder);
 	//	appiumDriverLocalService = AppiumDriverLocalService.buildDefaultService();
 		appiumDriverLocalService.start();
 		serviceURL=appiumDriverLocalService.getUrl().toString();
 		
	}
	
	
	
	@Parameters({ "deviceName","version","chromePort", "portNum","chromeDriverPath" })
	@BeforeClass
	public void test(  String deviceName , String version , @Optional String chromePort, @Optional String portNum,@Optional String chromePath) throws Exception
	{
		 capabilities=DesiredCapabilities.android();
		 System.out.println("Inside Desired Capabilities of Android");
		 capabilities.setCapability(MobileCapabilityType.BROWSER_NAME,BrowserType.CHROME);
	 
		 capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME,Platform.ANDROID);
	
		 capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION,version);
		 
		 
		/* capabilities.setCapability("appPackage","com.android.chrome");
		 
		 capabilities.setCapability("appPackage","com.google.android.apps.chrome.Main");*/
		 
		 capabilities.setCapability("--session-override",true);
//		 
//		 capabilities.setCapability("systemPort", Integer.parseInt(portNum));
		 
//		 capabilities.setCapability("chromeDriverPort",Integer.parseInt(chromePort));
//		 capabilities.setCapability("bootstrapPort",Integer.parseInt(portNum));
		 capabilities.setCapability("clearSystemFiles",true);
		 
//		 capabilities.setCapability("--chromedriver-executable", "/Users/nivassahoo/Documents/workspace_nivas/nativeDigiNext/chromedriver");
		 
		/* if (chromePath != null) {
		        capabilities.setCapability(AndroidMobileCapabilityType.CHROMEDRIVER_EXECUTABLE, chromePath);
		    }*/
		 
		 
		 capabilities.setCapability(MobileCapabilityType.DEVICE_NAME,deviceName);
		 capabilities.setCapability(MobileCapabilityType.UDID,deviceName);
	//	 capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 120);
	//	 capabilities.setCapability("automationName", "uiautomator2");
	//	 capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME,"Appium");
		 
		 url= new URL("http://127.0.0.1:4723/wd/hub");
		 
		 
		 
		 
	//	 System.out.println("Service URL "+serviceURL);
	//	 url= new URL(serviceURL);
		 
		 driver = new AndroidDriver<MobileElement>(url,capabilities);
		 driver.manage().timeouts().implicitlyWait(10,TimeUnit.SECONDS);
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
	
	public void runInTerminal(String command)
	{
		StringBuffer output = new StringBuffer();

		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = 
                            new BufferedReader(new InputStreamReader(p.getInputStream()));

                        String line = "";			
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public AppiumDriver<MobileElement> getDriver()
	{
		return driver;
	}
	
	public void setProxy()
	{
		 System.out.println("Start SetUP");
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
                         + "javax.net.ssl.trustStrore: " + System.getProperty("javax.net.ssl.trustStrore") +"\n");
	}

}
