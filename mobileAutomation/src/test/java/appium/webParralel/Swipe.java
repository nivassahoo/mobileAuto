package appium.webParralel;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;

public class Swipe extends AndroidNativeLaunchClass {
	
	static Dimension size;
	WebDriverWait wait;
	static MobileElement element;
	
  @Test
  public void swipe(){
	  wait=new WebDriverWait(driver, 30);
	  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//android.widget.TextView[@resource-id="
	  		+ "'com.snapdeal.main:id/categories_tab']")));
	  //swipeBasedOnDirection("DOWN", 5);
	  //scrollToUiselector("Jordan Football Design Sportswear");
  }
  
  
  @SuppressWarnings("rawtypes")
void swipeBasedOnDirection(String direction,int iteration)
  {
	  size=driver.manage().window().getSize();
	  int startX,startY,endX,endY;
	  while(iteration>0)
	  {
		  switch(direction.toUpperCase())
	  
	  {
	  
      case "LEFT":
          startY = (int) (size.height / 2);
          startX = (int) (size.width * 0.90);
          endX = (int) (size.width * 0.10);
          new TouchAction(driver)
          .longPress(PointOption.point(startX, startY))
          .waitAction(WaitOptions.waitOptions(Duration.ofMillis(2000)))
          .moveTo(PointOption.point(endX, startY))
          .release()
          .perform();
                  
          break;

      case "RIGHT":
          startY = (int) (size.height / 2);
          startX = (int) (size.width * 0.20);
          endX = (int) (size.width * 0.90);
          new TouchAction(driver)
          .press(PointOption.point(startX,startY))
          .waitAction(WaitOptions.waitOptions(Duration.ofMillis(2000)))
          .moveTo(PointOption.point(endX, startY))
          .release()
          .perform();
          break;

      case "UP":
          endY = (int) (size.height * 0.90);
          startY = (int) (size.height * 0.20);
          startX = (size.width / 2);
          new TouchAction(driver)
          .press(PointOption.point(startX,startY))
          .waitAction(WaitOptions.waitOptions(Duration.ofMillis(2000)))
          .moveTo(PointOption.point(startX, endY))
          .release()
          .perform();
          break;


      case "DOWN":
          startY = (int) (size.height * 0.70);
          endY = (int) (size.height * 0.20);
          startX = (size.width / 2);
          new TouchAction(driver)
          .longPress(PointOption.point(startX,startY))
          .waitAction(WaitOptions.waitOptions(Duration.ofMillis(2000)))
          .moveTo(PointOption.point(startX, endY))
          .release()
          .perform();

          break;
	  }
	  iteration--;
	  }
  }

  void scrollToUiselector(String text)
  {
	  driver.findElementByAndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains(\""+text+"\").instance(0))");
	  driver.findElementByAndroidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(new UiSelector().text(\""+text+"\"))");
  }

  void scrollToExactText(String text)
  {
	  
  }
	
 @SuppressWarnings("rawtypes")
	public static void swipeIOS(String direction,int iteration,AppiumDriver<MobileElement> driver)
	{
		  size=driver.manage().window().getSize();
		  int startX,startY,endX,endY;
		  while(iteration>0)
		  {
			  switch(direction.toUpperCase())
		  
		  {
		  
	      case "LEFT":
	          startY = (int) (size.height / 2);
	          startX = (int) (size.width * 0.90);
	          endX = (int) (size.width * 0.10);
	          new TouchAction(driver)
	          .longPress(PointOption.point(startX, startY))
	          .waitAction(WaitOptions.waitOptions(Duration.ofMillis(2000)))
	          .moveTo(PointOption.point(endX, startY))
	          .release()
	          .perform();
	                  
	          break;

	      case "RIGHT":
	          startY = (int) (size.height / 2);
	          startX = (int) (size.width * 0.20);
	          endX = (int) (size.width * 0.90);
	          new TouchAction(driver)
	          .press(PointOption.point(startX,startY))
	          .waitAction(WaitOptions.waitOptions(Duration.ofMillis(2000)))
	          .moveTo(PointOption.point(endX, startY))
	          .release()
	          .perform();
	          break;

	      case "UP":
	          endY = (int) (size.height * 0.90);
	          startY = (int) (size.height * 0.20);
	          startX = (size.width / 2);
	          new TouchAction(driver)
	          .press(PointOption.point(startX,startY))
	          .waitAction(WaitOptions.waitOptions(Duration.ofMillis(2000)))
	          .moveTo(PointOption.point(startX, endY))
	          .release()
	          .perform();
	          break;


	      case "DOWN":
	          startY = (int) (size.height * 0.80);
	          endY = (int) (size.height * 0.20);
	          startX = (size.width / 2);
	          new TouchAction(driver)
	          .longPress(PointOption.point(startX,startY))
	          .waitAction(WaitOptions.waitOptions(Duration.ofMillis(2000)))
	          .moveTo(PointOption.point(startX, endY))
	          .release()
	          .perform();

	          break;
		  }
		  iteration--;
		  }
	}

	public static void swipeUiSelectorIOS(String strText,AppiumDriver<MobileElement> driver)
	{
		driver.findElement(MobileBy.IosUIAutomation(
				"new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textMatches(\""
						+ strText + "\").instance(0))"));
	}
	
	@SuppressWarnings("rawtypes")
	public static void swipeUntilTextFound(String strText,AppiumDriver<MobileElement> driver,String direction)
	{
		 size=driver.manage().window().getSize();
		  int startX,startY,endX,endY;
		  
		//	while(driver.findElement(By.xpath("//*[contains(@name,'"+strText+"')]")).getAttribute("visible").equalsIgnoreCase("false"))
			while(!driver.findElement(By.xpath("//*[contains(@name,'"+strText+"')]")).isDisplayed())	
			{
			
				
		    switch(direction.toUpperCase())
		  {
		      case "LEFT":
	          startY = (int) (size.height / 2);
	          startX = (int) (size.width * 0.90);
	          endX = (int) (size.width * 0.10);
	          new TouchAction(driver)
	          .longPress(PointOption.point(startX, startY))
	          .waitAction(WaitOptions.waitOptions(Duration.ofMillis(2000)))
	          .moveTo(PointOption.point(endX, startY))
	          .release()
	          .perform();
	                  
	          break;

	      case "RIGHT":
	          startY = (int) (size.height / 2);
	          startX = (int) (size.width * 0.20);
	          endX = (int) (size.width * 0.90);
	          new TouchAction(driver)
	          .press(PointOption.point(startX,startY))
	          .waitAction(WaitOptions.waitOptions(Duration.ofMillis(2000)))
	          .moveTo(PointOption.point(endX, startY))
	          .release()
	          .perform();
	          break;

	      case "UP":
	          endY = (int) (size.height * 0.60);
	          startY = (int) (size.height * 0.20);
	          startX = (size.width / 2);
	          new TouchAction(driver)
	          .press(PointOption.point(startX,startY))
	          .waitAction(WaitOptions.waitOptions(Duration.ofMillis(2000)))
	          .moveTo(PointOption.point(startX, endY))
	          .release()
	          .perform();
	          break;


	      case "DOWN":
	          startY = (int) (size.height * 0.60);
	          endY = (int) (size.height * 0.20);
	          startX = (size.width / 2);
	          new TouchAction(driver)
	          .longPress(PointOption.point(startX,startY))
	          .waitAction(WaitOptions.waitOptions(Duration.ofMillis(2000)))
	          .moveTo(PointOption.point(startX, endY))
	          .release()
	          .perform();

	          break;
		  }
		  
		    System.out.println("Visibility is "+driver.findElement(By.xpath("//*[contains(@name,'"+strText+"')]")).getAttribute("visible"));
		   
		  }
	}	

}
