package appium.webParralel;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.HideKeyboardStrategy;

public class hideKeyboard {
	
	
	
	public void hideKeyboardAndroid(AppiumDriver<MobileElement> driver)
	{
		((AndroidDriver<MobileElement>) driver).pressKey(new KeyEvent(AndroidKey.BACK));
		((AndroidDriver<MobileElement>) driver).hideKeyboard();
		
	}
	
	
	public static void hideKeyboardiOS(AppiumDriver<MobileElement> driver,String keyType)
	{
		 if(keyType.length()<5)
		 {
		((IOSDriver<MobileElement>)driver).hideKeyboard(HideKeyboardStrategy.PRESS_KEY, keyType);
		 }
		 else if(keyType.equals("")){
		 ((IOSDriver<MobileElement>)driver).hideKeyboard(HideKeyboardStrategy.PRESS_KEY, "Next");
		 ((IOSDriver<MobileElement>)driver).hideKeyboard(HideKeyboardStrategy.PRESS_KEY, "Done");
		 }
		 else
		 {
		 ((IOSDriver<MobileElement>)driver).hideKeyboard();
		 }
		 
	}
}
