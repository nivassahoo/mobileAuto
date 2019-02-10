package appium.webParralel;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.testng.annotations.Test;

public class AndroidWebAppTest extends AndroidWebAppLaunchClass{
	
	
	
	
  @Test
  public void testMothod() throws Exception {
	 
	  driver.findElement(By.xpath("//input[@name='q']")).sendKeys("Appium"+Keys.ENTER);
//	  driver.findElement(By.xpath("//input[@aria-label='Google Search']")).click();
	  driver.findElement(By.xpath("//span[text()='appium.io']")).click();
	  System.out.println("Page Title on 1st load "+driver.getTitle());
	  driver.navigate().back();
	  Thread.sleep(5000);
	  driver.findElement(By.xpath("//span[text()='appium.io']")).click();
	  System.out.println("Page Title on 2nd load "+driver.getTitle());
	  driver.navigate().back();
	  Thread.sleep(5000);
	  driver.findElement(By.xpath("//span[text()='appium.io']")).click();
	  System.out.println("Page Title on 3rd load "+driver.getTitle());
  }
}
