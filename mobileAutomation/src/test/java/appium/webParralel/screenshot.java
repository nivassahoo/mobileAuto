package appium.webParralel;
import java.io.File;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.annotations.Test;

import com.aventstack.extentreports.MediaEntityBuilder;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;

public class screenshot extends AndroidStartUp{
	 File dest;
	 static String destBase64;
	
	public static String getScreenshot(AppiumDriver<MobileElement> driver,String screenshotName)
	{
	try
	{
	TakesScreenshot ts=(TakesScreenshot)driver;
	File source=ts.getScreenshotAs(OutputType.FILE);
	 destBase64=ts.getScreenshotAs(OutputType.BASE64);
	
	FileUtils.copyFile(source,new File("./Screenshots/"+screenshotName+".png"));
	System.out.println("Screenshot taken");
	} 
	catch (Exception e)
	{
	System.out.println("Exception while taking screenshot "+e.getMessage());
	}
	//return "./Screenshots/"+screenshotName+".png";
	return destBase64;
	}
	
	  @Test
	  public void testExtentReport() throws Exception
	  {	
		  
		   test=extent.createTest("Test Name lies here", "Test Direction lies here");	
		  
		  
		  System.out.println("Platform Name is "+platformNm);
		  driver.findElement(By.xpath("//*[@text='Login']")).click();
		  
		  test.pass("Screenshot one",MediaEntityBuilder.createScreenCaptureFromBase64String(screenshot.getScreenshot(driver, "Screenshotname_1")).build());
		  driver.findElement(By.xpath("//android.widget.EditText[@resource-id='com.dbs.id.dbsdigibank:id/password']")).sendKeys("dbs123");
		  test.pass("Screenshot two",MediaEntityBuilder.createScreenCaptureFromBase64String(screenshot.getScreenshot(driver, "Screenshotname_2")).build());
		  driver.findElement(By.xpath("//android.widget.Button[@resource-id='com.dbs.id.dbsdigibank:id/login_btn']")).click();
		  test.pass("Screenshot three",MediaEntityBuilder.createScreenCaptureFromBase64String(screenshot.getScreenshot(driver, "Screenshotname_3")).build());
		  driver.findElement(By.xpath("//*[contains(@text,'Bayar')]")).click();
		//  swipe.swipeUntilTextFoundAndroid("XL25", driver, "DOWN");
		  
	  }
	
}


