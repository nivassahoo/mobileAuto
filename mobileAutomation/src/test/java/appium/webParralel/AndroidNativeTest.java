package appium.webParralel;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import io.appium.java_client.MobileElement;

public class AndroidNativeTest extends AndroidNativeLaunchClass{
	
	//android.widget.TextView[@resource-id='com.snapdeal.main:id/home_tab']
	//android.widget.TextView[@resource-id='com.snapdeal.main:id/categories_tab']
	//android.widget.TextView[@resource-id='com.snapdeal.main:id/shortlist_tab']
	//android.widget.TextView[@resource-id='com.snapdeal.main:id/account_tab']
	
	//Account Page
	//android.view.View[@resource-id='com.snapdeal.main:id/toolBar']//android.widget.ImageButton
	//Hit fashion
	//(//android.view.View[@resource-id='com.snapdeal.main:id/bottom_padding']//..//..)[1]
	//choose mens fashion
	//android.widget.TextView[@text="Men's Fashion"]
	//android.view.View[@resource-id="com.snapdeal.main:id/toolbar"]//android.widget.ImageButton
	
	MobileElement element;
  @Test
  public void testMothod() throws Exception {
	 driver.findElement(By.xpath("//android.widget.TextView[@resource-id='com.snapdeal.main:id/categories_tab']")).click();
	 driver.findElement(By.xpath("//android.widget.TextView[@resource-id='com.snapdeal.main:id/shortlist_tab']")).click();
	 driver.findElement(By.xpath("//android.widget.TextView[@resource-id='com.snapdeal.main:id/account_tab']")).click();
	 
	 Thread.sleep(7000);
	 driver.findElement(By.xpath("//android.view.View[@resource-id='com.snapdeal.main:id/toolBar']//android.widget.ImageButton")).click();
	 driver.findElement(By.xpath("(//android.view.View[@resource-id='com.snapdeal.main:id/bottom_padding']//..//..)[1]")).click();
	 Thread.sleep(7000);
	 driver.findElement(By.xpath("//android.widget.TextView[@text=\"Men's Fashion\"]")).click();
	 Thread.sleep(7000);
	 driver.findElement(By.xpath("//android.view.View[@resource-id=\"com.snapdeal.main:id/toolbar\"]//android.widget.ImageButton")).click();
	 
	
  }
}
