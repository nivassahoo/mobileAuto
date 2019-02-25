package appium.webParralel;

import org.openqa.selenium.ScreenOrientation;
import org.testng.annotations.Test;

public class screenOrientation extends AndroidNativeLaunchClass{



 @Test
  public void orientation()
  {
	  System.out.println("*--*--*-- Current screen orientation Is : " + driver.getOrientation());
	  driver.rotate(ScreenOrientation.LANDSCAPE);
	  System.out.println("*--*--*-- Now screen orientation Is : "+ driver.getOrientation());
  }
  
  
}
