package appium.webParralel;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

public class iOSNativeTest extends iOSNativeLaunchClass{
   WebDriverWait wait;
	
  @Test
  public void test() throws Exception
  {
	  wait = new WebDriverWait(driver,60);
	  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//XCUIElementTypeStaticText[@name='Login']"))).click();
	  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//XCUIElementTypeSecureTextField"))).sendKeys("dbs123");
	  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//XCUIElementTypeButton[@name='LOGIN']"))).click();
	  Thread.sleep(15000);
	  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//XCUIElementTypeNavigationBar[@name='dbid_ios.HomeView']/XCUIElementTypeButton[1]"))).click();
	  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//XCUIElementTypeStaticText[@name='Transaksi']//.."))).click();
	  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//XCUIElementTypeStaticText[@name='Rekening saya']//.."))).click();
	  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//XCUIElementTypeStaticText[@name='Transfer']//.."))).click();
	  Thread.sleep(15000);
  }
}

