package com.dbs.actions;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.dbs.commons.ElementFinder;
import com.dbs.commons.ImageProcessor;
import com.dbs.commons.Reporter;
import com.dbs.commons.RunDetails;
import com.dbs.commons.StepListener;
import com.dbs.config.Config;
import com.dbs.drivers.DriverManagerFactory;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.touch.LongPressOptions;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.ElementOption;
import io.appium.java_client.touch.offset.PointOption;

public class MB_Actions {

	private static final Logger logger = Logger.getLogger(MB_Actions.class);
	private static final ThreadLocal<MobileDriver> mobileDriverForScreenshot = new InheritableThreadLocal<>();
	AndroidDriver androidDriver;
	AppiumDriver appiumDriver;
	Dimension dim;
	MobileElement element;
	ElementFinder elementFinder = new ElementFinder();
	private By elementProperty;
	IOSDriver iosDriver;
	MobileDriver mobileDriver;
	PointOption pointOption;
	WebDriver proxiedDriver;
	Reporter reporter = Reporter.getCurrentReporter();
	TouchAction touchAction;
	int X_AXIS;
	int Y_AXIS;
	String page;
	private ImageProcessor imgProcessor;

	public MB_Actions(MobileElement object, AppiumDriver driver,String pageName) {
		this.element = object;
		this.mobileDriver = (MobileDriver) driver;
		this.touchAction = new TouchAction(driver);
		this.dim = getScreenResolution();
		this.Y_AXIS = dim.height;
		this.X_AXIS = dim.width;
		this.page = pageName;
		mobileDriverForScreenshot.set((MobileDriver) driver);
	}

	public MB_Actions(MobileElement object, By objProp, AppiumDriver driver,String pageName) {
		this.element = object;
		this.elementProperty = objProp;
		this.mobileDriver = (MobileDriver) driver;
		this.touchAction = new TouchAction(driver);
		this.dim = getScreenResolution();
		this.Y_AXIS = dim.height;
		this.X_AXIS = dim.width;
		this.page = pageName;
		mobileDriverForScreenshot.set((MobileDriver) driver);
	}
	
	public MB_Actions(MobileElement object, PointOption pointOption, AppiumDriver driver,String pageName) {
		this.element = object;
		this.mobileDriver = (MobileDriver) driver;
		this.touchAction = new TouchAction(driver);
		this.dim = getScreenResolution();
		this.Y_AXIS = dim.height;
		this.X_AXIS = dim.width;
		this.pointOption = pointOption;		
		this.page = pageName;
		mobileDriverForScreenshot.set((MobileDriver) driver);
	}
	
	public MB_Actions(MobileElement object, ElementFinder elementFinder, AppiumDriver driver,String pageName) {
		this.element = object;
		this.mobileDriver = (MobileDriver) driver;
		this.touchAction = new TouchAction(driver);
		this.dim = getScreenResolution();
		this.Y_AXIS = dim.height;
		this.X_AXIS = dim.width;
		this.elementFinder = elementFinder;
		/*this.imgProcessor = elementFinder.getImageProps();
		if(imgProcessor.getSearchFlag())
			this.pointOption = imgProcessor.getPointOption();*/
		this.page = pageName;
		mobileDriverForScreenshot.set((MobileDriver) driver);
	}

	public void clear() {
		if (element != null) {
			String text = element.getText();
			for (int i = 0; i < text.length(); i++) {
				element.click();
				TouchAction ta = new TouchAction(appiumDriver);
				ta.longPress(PointOption.point(element.getLocation().getX(), element.getLocation().getY()));
				appiumDriver.getKeyboard().sendKeys(Keys.DELETE);
				if (element.getText().length() == 0)
					break;
			}
		}

	}
	
	
	public static MobileDriver getDriverForScreenshot() {
		return mobileDriverForScreenshot.get();
	}

	public void clickDoneOnKeyBoard() throws Throwable {

		try {
			if (DriverManagerFactory.getDriverData("Platform Name").equalsIgnoreCase("Android"))
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.ENTER));
			else
				logger.info("Clicked on Done on keypad");
		} catch (Throwable t) {
			logger.error("Exception in click method " + t.getLocalizedMessage());
			Config.gracefulEnd(t, logger);
			throw t;
		}
	}

	public void click() throws Throwable {
		if (null == element)
			throw new Throwable(": MB Actions : Element is null");
		try {
			if (isStale(element)) {
				updateReporterLog(StepListener.getListener().getStepName(), "clicked", "");
				element.click();
				logger.info("Clicked on object" + element);
			} else
				logger.error("Element is stale and not clickable");
		} catch (Throwable t) {
			logger.error("Exception in click method " + t.getLocalizedMessage());
			Config.gracefulEnd(t, logger);
			throw t;
		}
	}

	public void clickBasedOnCoordinates(int heightY, int widthX) throws Throwable {
		// if(null == element)
		// throw new Throwable(": MB Actions : Element is null");
		try {
			// touchAction.tap(widthX, heightY).perform();
			touchAction = new TouchAction(mobileDriver);
			touchAction.tap(pointOption.withCoordinates(heightY, widthX)).perform();
			logger.info("Clicked based on coordinates x: " + widthX + " ,y: " + heightY);
			updateReporterLog(StepListener.getListener().getStepName(), "clicked", "");
		} catch (Throwable t) {
			logger.error("Exception in click method " + t.getLocalizedMessage());
			Config.gracefulEnd(t, logger);
		}
	}

	public void clickBasedOnElementLocation(int xplus, int yplus) throws Throwable {
		int x = getElementLocation().getX();
		int y = getElementLocation().getY();
		if (null == element)
			throw new Throwable(": MB Actions : Element is null");
		logger.info("clicking based on Element location X : "+(x+xplus)+" Y :"+(y+yplus) );
		try {
			if (isStale(element)) {
				touchAction = new TouchAction(mobileDriver);
				pointOption = new PointOption();
				 //touchAction.tap(x+xplus, y+yplus).perform();
				touchAction.tap(PointOption.point(x + xplus, y + yplus)).perform().release();
				//touchAction.tap(PointOption.point(130, 450)).perform().release();
				//touchAction.press(pointOption.withCoordinates(x, y)).perform().release();
				updateReporterLog(StepListener.getListener().getStepName(), "clicked", "");
				logger.info("Clicked on object" + element);
			} else
				logger.error("Element is stale and not clickable");
		} catch (Throwable t) {
			logger.error("Exception in click method " + t.getLocalizedMessage());
			Config.gracefulEnd(t, logger);
			throw t;
		}
	}

	public void enterCharacters(String character) throws Exception {
		logger.info("Trying to find alphabet '" + character + "' and click");

		List<MobileElement> mbElelst = elementFinder.find_MB_Elmtns(getAppiumDriverDriver(),
				"//*[@name='" + character + "']", 2, 4);

		if (mbElelst.size() == 0) {
			logger.info("no character found of " + character);
			;
			// highlightElement(driver, WebElement);
		} else if (mbElelst.size() == 1) {
			mbElelst.get(0).click();
			// highlightElement(driver, WebElement);
		} else {
			logger.error("Current element locator/selector matches multiple elements on the page");
			throw new Exception(
					"Current element locator/selector matches multiple elements on the page and size of elements "
							+ mbElelst.size());
		}
	}

	public void enterCharactersOnKeyPad(String text) throws Throwable {

		if (null == element)
			throw new Throwable(": MB Actions : Element is null");
		logger.info("Entering numerics on " + element + " " + text);
		try {
			// String numArray[] = number.split("(?!^)");
			char[] numArray = text.toCharArray();
			for (char eachCharacter : numArray) {
				enterCharacters(Character.toString(eachCharacter));
			}
		} catch (Throwable t) {
			logger.error("Error while entering numerics " + text);
			Config.gracefulEnd(t, logger);
		}

	}

	private void enterNumber(String number) throws Throwable {
		if (null == element)
			throw new Throwable(": MB Actions : Element is null");
		if (DriverManagerFactory.getDriverData("Platform Name").equalsIgnoreCase("ANDROID")) {
			switch (number) {
			case "1":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_1);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.NUMPAD_1));
				logger.info("Entered :: " + number + " on " + element);
				break;
			case "2":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_2);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.NUMPAD_2));
				logger.info("Entered :: " + number + " on " + element);
				break;
			case "3":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_3);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.NUMPAD_3));
				logger.info("Entered :: " + number + " on " + element);
				break;
			case "4":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_4);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.NUMPAD_4));
				logger.info("Entered :: " + number + " on " + element);
				break;
			case "5":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_5);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.NUMPAD_5));
				logger.info("Entered :: " + number + " on " + element);
				break;
			case "6":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_6);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.NUMPAD_6));
				logger.info("Entered :: " + number + " on " + element);
				break;
			case "7":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_7);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.NUMPAD_7));
				logger.info("Entered :: " + number + " on " + element);
				break;
			case "8":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_8);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.NUMPAD_8));
				logger.info("Entered :: " + number + " on " + element);
				break;
			case "9":
				// (AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_9);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.NUMPAD_9));
				logger.info("Entered :: " + number + " on " + element);
				break;
			case "0":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_0);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.NUMPAD_0));
				logger.info("Entered :: " + number + " on " + element);
				break;
			}
		} else if (DriverManagerFactory.getDriverData("Platform Name").equalsIgnoreCase("IOS")) {
		}
	}

	public void enterNumericsOnNumPad(String number) throws Throwable {
		if (null == element)
			throw new Throwable(": MB Actions : Element is null");
		logger.info("Entering numerics on " + element + " " + number);
		try {
			// String numArray[] = number.split("(?!^)");
			char[] numArray = number.toCharArray();
			for (char eachNumber : numArray) {
				enterNumber(Character.toString(eachNumber));
			}
		} catch (Throwable t) {
			logger.error("Error while entering numerics " + number);
			Config.gracefulEnd(t, logger);
		}
	}

	public void enterText(String value) throws Throwable {
		if (null == element)
			throw new Throwable(": MB Actions : Element is null");
		String actionName = "entered '" + value + "' ";
		try {
			element.clear();
			element.sendKeys(value);
			logger.info("Entered text" + value + "on object" + element);
			updateReporterLog(StepListener.getListener().getStepName(), actionName, value);
		} catch (Throwable t) {
			logger.error("Exception in enterText method " + t.getLocalizedMessage());
			Config.gracefulEnd(t, logger);
			throw t;
		}
	}

	public boolean exists() {
		if (null == element)
			return false;
		try {
			String actionName = Thread.currentThread().getStackTrace()[1].getMethodName();
			updateReporterLog(StepListener.getListener().getStepName(), actionName, "");
			element.isDisplayed();
			logger.info("object exists " + element);
			return true;
		} catch (Throwable t) {
			logger.error("Exception in enterText method " + t.getLocalizedMessage());
			Config.gracefulEnd(t, logger);
			return false;
		}
	}

	public AppiumDriver getAppiumDriverDriver() {
		return this.appiumDriver;
	}

	public MobileElement getElement() {
		return this.element;
	}

	public Point getElementLocation() {
		return element.getLocation();
	}

	public MobileDriver getMobileDriver() {
		return this.mobileDriver;
	}

	private Dimension getScreenResolution() {
		Dimension resolution = mobileDriver.manage().window().getSize();
		return resolution;
	}

	public String getText() throws Throwable {
		if (null == element)
			throw new Throwable(": MB Actions : Element is null");
		String elem_txt = "";
		try {
			elem_txt = element.getText();
			return elem_txt;
		} catch (Throwable t) {
			logger.error("Exception in enterText method " + t.getLocalizedMessage());
			Config.gracefulEnd(t, logger);
			return elem_txt;
		}
	}

	public void hideKeyboard() throws Throwable {
		/*
		 * if (null == element) throw new Throwable(": MB Actions : Element is null");
		 */
		try {
			if (DriverManagerFactory.getDriverData("Platform Name").equalsIgnoreCase("Android")) {
				mobileDriver.hideKeyboard();
			} else if (DriverManagerFactory.getDriverData("Platform Name").equalsIgnoreCase("iOS")) {
				mobileDriver.hideKeyboard();
			} else {
				if (DriverManagerFactory.getDriverData("Run On Cloud").equalsIgnoreCase("true")) {
					mobileDriver.hideKeyboard();
				} else {
					try {
						elementFinder.verify_MB_Element(appiumDriver, "commonObjects", "lblReturn", "").click();
					} catch (Throwable t) {
						elementFinder.verify_MB_Element(appiumDriver, "OtpPage", "lblDone", "").click();
					}
				}

			}

		} catch (Exception e) {
			/* If the exception is thrown, then keyboard is not present */
			return;
		}
		return;
	}

	public boolean isDisabled() throws Throwable {
		if (null == element)
			throw new Throwable(": MB Actions : Element is null");
		try {
			if (!element.isEnabled()) {
				updateReporterLog(StepListener.getListener().getStepName(), "isDisabled", "");
				return true;
			}
		} catch (Throwable e) {
			logger.error("Element is not available/null ");
			updateReporterLog(StepListener.getListener().getStepName(), "isDisabled", "");
			return false;
		}
		return false;
	}

	/*
	 * This method will return true if the element is enabled(Stale)
	 */
	public boolean isEnabled() throws Throwable {
		if (null == element)
			throw new Throwable(": MB Actions : Element is null");
		try {
			for (int i = 0; i <= Config.WAIT_FOR_ELEMENT_COUNTER; i++) {
				try {
					updateReporterLog(StepListener.getListener().getStepName(), "isEnabled", "");
					element.isEnabled();
					return true;
				} catch (StaleElementReferenceException e) {
					logger.error("Element is stale and not clickable " + e.getLocalizedMessage()
							+ "\r\n trying again... for " + i + " times");
				}
			}
			return false;
		} catch (Throwable t) {
			logger.error("Exception in isStale method " + t.getLocalizedMessage());
			Config.gracefulEnd(t, logger);
			return false;
		}
	}

	public boolean isKeyboardOpen() throws Throwable {
		try {
			DriverManagerFactory.getMobileManager().getMobileDriver().hideKeyboard();
		} catch (Exception e) {
			/* If the exception is thrown, then keyboard is not present */
			return true;
		}
		return false;
	}

	public boolean isStale(WebElement element) throws Throwable {
		if (null == element)
			throw new Throwable(": MB Actions : Element is null");
		try {
			for (int i = 0; i <= Config.WAIT_FOR_ELEMENT_COUNTER; i++) {
				try {
					element.isEnabled();
					return true;
				} catch (StaleElementReferenceException e) {
					logger.error("Element is stale and not clickable " + e.getLocalizedMessage()
							+ "\r\n trying again... for " + i + " times");
				}
			}
			return false;
		} catch (Throwable t) {
			logger.error("Exception in isStale method " + t.getLocalizedMessage());
			Config.gracefulEnd(t, logger);
			return false;
		}
	}

	public boolean notExists() {
		if (null == element)
			return true;
		try {
			String actionName = Thread.currentThread().getStackTrace()[1].getMethodName();
			updateReporterLog(StepListener.getListener().getStepName(), actionName, "");
			element.isDisplayed();
			return false;
		} catch (Throwable t) {
			return true;
		}
	}

	// android
	public boolean scroll_and_tap_byText_Android(String text) throws Throwable {
		try {
			mobileDriver.findElement(MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView("
					+ "new UiSelector().text(\"" + text + "\"));"));
			// tap on it
		} catch (Throwable t) {
			logger.error("Exception in scroll_and_tap_byText_Android method " + t.getLocalizedMessage());
			Config.gracefulEnd(t, logger);
			return false;
		}
		return true;
	}

	// ios
	public boolean scroll_and_tap_byText_IOS(String text) { // works ONLY inside scrollView
		JavascriptExecutor js = (JavascriptExecutor) iosDriver;
		HashMap<String, String> scrollObject = new HashMap<>();
		scrollObject.put("predicateString", "value == '" + text + "'");
		js.executeScript("mobile: scroll", scrollObject);
		WebElement el = iosDriver.findElementByIosUIAutomation("value = '" + text + "'");
		el.click();
		// iosDriver.tap(1, el, 500);
		return true;
	}

	public void selectDropDownbyValue(String textToIdentify) throws Throwable {
		if (null == element)
			throw new Throwable(": MB Actions : Element is null");
		Select dropDown = new Select(element);
		// dropDown.selectByVisibleText(textToIdentify);
		dropDown.selectByValue(textToIdentify);
	}

	/**
	 * <h4>Swipe</h4> This method performs swipe action based on co-ordinates be
	 * cautious while passing params
	 * 
	 * @param startHeightFrom
	 *            : Height of the screen resolution is divided by this value to form
	 *            a starting Y-Axis point
	 * @param startWidthFrom
	 *            : Width of the screen resolution is divided by this value to form
	 *            a starting X-Axis point
	 * @param endHeightAt
	 *            : Height of the screen resolution is divided by this value to form
	 *            a ending Y-Axis point
	 * @param entWidthAt
	 *            : Width of the screen resolution is divided by this value to form
	 *            a ending X-Axis point
	 * @return boolean
	 */

	protected boolean Swipe(int startHeightFrom, int startWidthFrom, int endHeightAt, int entWidthAt) {
		try {
			// mobileDriver.swipe(X_AXIS / startWidthFrom, Y_AXIS / startHeightFrom, X_AXIS
			// / entWidthAt,Y_AXIS / endHeightAt, 750);

			dim = getScreenResolution();
			new TouchAction(mobileDriver)
					.press(PointOption.point(dim.getWidth() / startWidthFrom, dim.getHeight() / startHeightFrom))
					.waitAction(WaitOptions.waitOptions(Duration.ofMillis(750)))
					.moveTo(PointOption.point(dim.getWidth() / entWidthAt, dim.getHeight() / endHeightAt)).release()
					.perform();
		} catch (Throwable t) {
			Config.gracefulEnd(t, logger);
			logger.error("Returning 'false', Error while swiping :: " + t.getLocalizedMessage());
			return false;
		}
		return true;
	}

	public void swipeBasedOnText(String strText) throws Throwable {
		try {
			if (DriverManagerFactory.getDriverData("Platform Name").trim().equalsIgnoreCase("ANDROID")) {
				appiumDriver.findElement(MobileBy.AndroidUIAutomator(
						"new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textMatches(\""
								+ strText + "\").instance(0))"));
				logger.info("scrolled to the element with text :: " + strText);
			} else if (DriverManagerFactory.getDriverData("Platform Name").trim().equalsIgnoreCase("IOS")) {
				appiumDriver.findElement(MobileBy.IosUIAutomation(
						"new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textMatches(\""
								+ strText + "\").instance(0))"));
				logger.info("scrolled to the element with text :: " + strText);
			}
		} catch (Throwable t) {
			logger.error(
					"Error while scrolling to the element with text :: " + strText + " " + t.getLocalizedMessage());
			Config.gracefulEnd(t, logger);
		}
	}

	/*
	 * <h1>swipeFromElement</h1> This method begin swiping from an element end
	 * co-orndiates to end of the screen
	 *
	 * @author sudharsanRaju
	 * 
	 * @version 1.0
	 */
	public void swipeFromElement(String direction) throws Throwable {
		if (element == null)
			throw new Throwable(": MB Actions : Element is null");
		int leftX = element.getLocation().getX();
		int rightX = leftX + element.getSize().getWidth();
		int middleX = (rightX + leftX) / 2;
		int upperY = element.getLocation().getY();
		int lowerY = upperY + element.getSize().getHeight();
		int middleY = (upperY + lowerY) / 2;

		Dimension size = getScreenResolution();
		// point which is at right side of screen
		int width = (int) (size.width);
		int height = (int) (size.height);

		switch (direction.toUpperCase()) {
		case "RIGHT":
			logger.info(" :swipeFromElement: Swiping from [" + rightX + "," + middleY + "] to [" + leftX + "," + middleY
					+ "]");
			new TouchAction(mobileDriver).press(PointOption.point(leftX, middleY))
					.waitAction(WaitOptions.waitOptions(Duration.ofMillis(750)))
					.moveTo(PointOption.point(size.getWidth(), middleY)).release().perform();
			break;
		case "LEFT":
			logger.info(" :swipeFromElement: Swiping from [" + leftX + "," + middleY + "] to [" + rightX + "," + middleY
					+ "]");
			new TouchAction(mobileDriver).press(PointOption.point(rightX, middleY))
					.waitAction(WaitOptions.waitOptions(Duration.ofMillis(750))).moveTo(PointOption.point(0, middleY))
					.release().perform();
			break;
		case "UP":
			logger.info(" :swipeFromElement: Swiping from [" + leftX + "," + middleY + "] to [" + rightX + "," + middleY
					+ "]");
			new TouchAction(mobileDriver).press(PointOption.point(middleX, middleY))
					.waitAction(WaitOptions.waitOptions(Duration.ofMillis(750))).moveTo(PointOption.point(middleX, 0))
					.release().perform();
			break;
		case "DOWN":
			logger.info(" :swipeFromElement: Swiping from [" + leftX + "," + middleY + "] to [" + rightX + "," + middleY
					+ "]");
			new TouchAction(mobileDriver).press(PointOption.point(middleX, middleY))
					.waitAction(WaitOptions.waitOptions(Duration.ofMillis(750)))
					.moveTo(PointOption.point(middleX, size.getHeight())).release().perform();
			break;
		default:
			logger.error(" :swipeFromElement: Unknown direction");

		}
	}

	/*
	 * private void updateReporter(String ActionName) { String sql =
	 * "INSERT INTO `reports`.`t_step_exec_details` \r\n" +
	 * "(`runId`, `TestCaseID`, `TestStepName`, `TestStepID`, `Status`, `referencePath`, `requestDetail`, `responseDetail`, `stepStart`, `stepEnd`) \r\n"
	 * + "VALUES \r\n" +
	 * "('autoNum', 'autoNum', 'ValidStepName', 'sequenceStepId', 'Pass', 'paht to screenshots', 'request XML or JSON etc', 'response XML or JSON etc', '2018-06-18 16:26:37', '2018-06-18 16:26:38');\r\n"
	 * + ""; }
	 */

	public void swipeHorizontal(String direction) throws Throwable {
		if (element == null)
			throw new Throwable(": MB Actions : Element is null");
		try {
			Dimension size = getScreenResolution();
			// point which is at right side of screen
			int rightdim = (int) (size.width * 0.20);
			// point which is at left side of screen.
			int leftdim = (int) (size.width * 0.80);

			switch (direction.toUpperCase()) {
			case "RIGHT":
				// touchAction.longPress(element).moveTo(rightdim, 580).release().perform();
				// touchAction.longPress(PointOption.point(rightdim,
				// rightdim)).waitAction(WaitOptions.waitOptions(Duration.ofMillis(500))).release().perform();
				touchAction.longPress(LongPressOptions.longPressOptions().withElement(ElementOption.element(element)))
						.moveTo(PointOption.point(rightdim, 580)).release().perform();
				break;

			case "LEFT":
				// touchAction.longPress(element).moveTo(leftdim, 580).release().perform();
				touchAction.longPress(LongPressOptions.longPressOptions().withElement(ElementOption.element(element)))
						.moveTo(PointOption.point(leftdim, 580)).release().perform();
				break;
			}
		} catch (Throwable t) {
			logger.error("Exception in swipeHorizontal method " + t.getLocalizedMessage());
			Config.gracefulEnd(t, logger);
			throw t;
		}

	}

	public void SwipeScreenOnDirecton(String direction) throws Throwable {

		try {
			Thread.sleep(Config.SHORT_WAIT * 1000);
			switch (direction.toUpperCase()) {
			case "UP":
				logger.info(":SwipeScreenOnDirecton: Scrolling up the screen by half");
				Swipe(2, 2, 4, 2);
				break;
			case "DOWN":
				logger.info(":SwipeScreenOnDirecton: Scrolling down the screen by half");
				Swipe(1, 2, 2, 2);
				break;
			case "RIGHT":
				logger.info(":SwipeScreenOnDirecton: Scrolling right the screen by half");
				Swipe(2, 1, 2, 4);
				break;
			case "LEFT":
				logger.info(":SwipeScreenOnDirecton: Scrolling right the screen by half");
				Swipe(2, 4, 2, 1);
				break;
			}
		} catch (Throwable t) {
			logger.error("Exception in SwipeScreenOnDirecton method " + t.getLocalizedMessage());
			Config.gracefulEnd(t, logger);
		}
	}

	public void swipeTillFound(String direction, String keyText) throws Throwable {
		if (element == null)
			throw new Throwable(": MB Actions : Element is null");
		int leftX = element.getLocation().getX();
		int rightX = leftX + element.getSize().getWidth();
		int middleX = (rightX + leftX) / 2;
		int upperY = element.getLocation().getY();
		int lowerY = upperY + element.getSize().getHeight();
		int middleY = (upperY + lowerY) / 2;

		Dimension size = getScreenResolution();
		// point which is at right side of screen
		int width = (int) (size.width);
		int height = (int) (size.height);

		By searchElemProp = MobileBy.xpath("//*[@text='" + keyText + "'");

		for (int i = 0; i <= Config.SWIPE_COUNTER; i++) {
			logger.info("Swiping on " + direction + " for " + i + " time(s)");
			switch (direction.toUpperCase()) {
			case "RIGHT":
				logger.info(" :swipeFromElement: Swiping from [" + rightX + "," + middleY + "] to [" + leftX + ","
						+ middleY + "]");
				new TouchAction(mobileDriver).press(PointOption.point(leftX, middleY))
						.waitAction(WaitOptions.waitOptions(Duration.ofMillis(750)))
						.moveTo(PointOption.point(size.getWidth(), middleY)).release().perform();
				break;
			case "LEFT":
				logger.info(" :swipeFromElement: Swiping from [" + leftX + "," + middleY + "] to [" + rightX + ","
						+ middleY + "]");
				new TouchAction(mobileDriver).press(PointOption.point(rightX, middleY))
						.waitAction(WaitOptions.waitOptions(Duration.ofMillis(750)))
						.moveTo(PointOption.point(0, middleY)).release().perform();
				break;
			case "UP":
				logger.info(" :swipeFromElement: Swiping from [" + leftX + "," + middleY + "] to [" + rightX + ","
						+ middleY + "]");
				new TouchAction(mobileDriver).press(PointOption.point(middleX, middleY))
						.waitAction(WaitOptions.waitOptions(Duration.ofMillis(750)))
						.moveTo(PointOption.point(middleX, 0)).release().perform();
				break;
			case "DOWN":
				logger.info(" :swipeFromElement: Swiping from [" + leftX + "," + middleY + "] to [" + rightX + ","
						+ middleY + "]");
				new TouchAction(mobileDriver).press(PointOption.point(middleX, middleY))
						.waitAction(WaitOptions.waitOptions(Duration.ofMillis(750)))
						.moveTo(PointOption.point(middleX, size.getHeight())).release().perform();
				break;
			default:
				logger.error(" :swipeFromElement: Unknown direction");

			}
			try {
				List<MobileElement> mbelst = elementFinder.find_MB_Elmtns((AppiumDriver) mobileDriver, searchElemProp,
						2);
				if (mbelst == null)
					continue;
				else {
					element = mbelst.get(0);
					if (mbelst.get(0).isDisplayed())
						break;
				}
			} catch (Exception e) {
				logger.error("SwipeTillFound : exception " + i + " /" + Config.SWIPE_COUNTER);
				logger.error(e.getLocalizedMessage());
				continue;
			}
		}
	}

	/*
	 * Operation performed on center of screen co-ordinates For swiping on a
	 * specific element use swipteBasedOnText
	 */
	public boolean SwipeTillFound(String direction) throws Exception {
		try {
			for (int i = 0; i <= Config.SWIPE_COUNTER; i++) {
				try {
					logger.info("Swiping on " + direction + " for " + i + " time(s)");
					SwipeScreenOnDirecton(direction);
					List<MobileElement> mbelst = elementFinder.find_MB_Elmtns((AppiumDriver) mobileDriver,
							this.elementProperty, Config.SHORT_WAIT / 3);
					if (mbelst == null)
						continue;
					element = mbelst.get(0);
					logger.info("After swiping " + i + " time(s), element exists ? " + exists());
				} catch (Exception e) {
					logger.error("SwipeTillFound : exception " + i + " /" + Config.SWIPE_COUNTER);
					logger.error(e.getLocalizedMessage());
					continue;
				}
			}
			return true;
		} catch (Throwable t) {
			logger.error("Exception in SwipeTillFound method " + t.getLocalizedMessage());
			Config.gracefulEnd(t, logger);
			return false;
		}
	}

	/*
	 * <h1>swipeWithInElement</h1> This method begin swiping from an element end
	 * co-orndiates to end of the screen
	 *
	 * @author sudharsanRaju
	 * 
	 * @version 1.0
	 */
	public void swipeWithInElement(String direction) throws Throwable {
		if (element == null)
			throw new Throwable(": MB Actions : Element is null");
		int leftX = element.getLocation().getX();
		int rightX = leftX + element.getSize().getWidth();
		int middleX = (rightX + leftX) / 2;
		int upperY = element.getLocation().getY();
		int lowerY = upperY + element.getSize().getHeight();
		int middleY = (upperY + lowerY) / 2;

		Dimension size = getScreenResolution();
		// point which is at right side of screen
		int width = (int) (size.width);
		int height = (int) (size.height);

		switch (direction.toUpperCase()) {
		case "RIGHT":
			logger.info(" :swipeFromElement: Swiping from [" + rightX + "," + middleY + "] to [" + leftX + "," + middleY
					+ "]");
			new TouchAction(mobileDriver).press(PointOption.point(leftX, middleY))
					.waitAction(WaitOptions.waitOptions(Duration.ofMillis(750)))
					.moveTo(PointOption.point(rightX, middleY)).release().perform();
			break;
		case "LEFT":
			logger.info(" :swipeFromElement: Swiping from [" + leftX + "," + middleY + "] to [" + rightX + "," + middleY
					+ "]");
			new TouchAction(mobileDriver).press(PointOption.point(rightX, middleY))
					.waitAction(WaitOptions.waitOptions(Duration.ofMillis(750)))
					.moveTo(PointOption.point(leftX, middleY)).release().perform();
			break;
		case "UP":
			logger.info(" :swipeFromElement: Swiping from [" + middleX + "," + middleY + "] to [" + middleX + "," + upperY
					+ "]");
			/*
			 * change by hareesh as from press(PointOption.point(middleX, lowerY) to press(PointOption.point(middleX, middleY)
			 * as it says coordinates out of element rectangle when element lowerY is equal to device height
			 * review if necessary
			 * */
			new TouchAction(mobileDriver).press(PointOption.point(middleX, middleY))
					.waitAction(WaitOptions.waitOptions(Duration.ofMillis(750)))
					.moveTo(PointOption.point(middleX, upperY)).release().perform();
			break;
		case "DOWN":
			logger.info(" :swipeFromElement: Swiping from [" + leftX + "," + middleY + "] to [" + rightX + "," + middleY
					+ "]");
			new TouchAction(mobileDriver).press(PointOption.point(middleX, upperY))
					.waitAction(WaitOptions.waitOptions(Duration.ofMillis(750)))
					.moveTo(PointOption.point(middleX, lowerY)).release().perform();
			break;
		default:
			logger.error(" :swipeFromElement: Unknown direction");

		}
	}

	public void updateReporterLog(String stepName, String Action, String value) {
		String step;
		String action = Thread.currentThread().getStackTrace()[1].getMethodName().toUpperCase();
		if(this.pointOption != null) {
			logger.info("Performed " + Action + " on Matched Image");
			step = "Performed " + Action + " on Matched Image";
		}
		else {
			String eleText = element.getText(),eleTag = element.getTagName();
			logger.info("Performed " + Action + " on object : " + this.element.getId() + "," + this.element.toString()
			+ " Text : " + eleText + "," + eleTag);

			if(eleTag == null || eleTag == "" && eleText != null)
				step = "Performed " + Action + " on object : "+ eleText;
			else if(eleText == null || eleText == "" && eleTag != null)
				step = "Performed " + Action + " on object : " + eleTag;
			else if(eleText != null && eleTag != null)
				step = "Performed " + Action + " on object : " + eleTag + "~" + eleText;
			else
				step = "Performed " + Action + " on object";
		}
		Reporter.setCurrentStep(step);

		if (Config.TAKESCREENSHOT_FOREACH_ACTION) {
			//reporter.takeScreenshot();
		} else {
			reporter.childlog.info(step);
			reporter.zypherlog.info(step);

			if (!Config.LOCAL_RUN) {
				String stepSeqId = RunDetails.getStepSeqonScenarioSeq();
				RunDetails.currentStepSeqId = stepSeqId;
				RunDetails.insertActionsIntoDB(stepSeqId, step, value, "");
				logger.info("Call to save screenshot completed");
			}

		}

		// reporter.takeScreenshot(mobileDriver, "Performed "+ Action + " on object : "
		// +element.getTagName()+"~"+element.getText() , value);

	}
	
	
	public void enterCharactersOnAndroidKeyPad(String text) throws Throwable {
		if (null == element)
			throw new Throwable(": MB Actions : Element is null");
		else 
			element.click();
			
		for (char eachCharacter : text.toCharArray()) {
			
		//	String character = Character.toString(eachCharacter);
			
	        if( (eachCharacter >= 'a' && eachCharacter <= 'z') || (eachCharacter >= 'A' && eachCharacter <= 'Z')) {
	            logger.info(eachCharacter+" is an alphabet");
	            enterAndroidChar(eachCharacter);
	        }
	        else {
	            logger.info(eachCharacter+" is not an alphabet");
	           // enterNumber(Character.toString(eachCharacter));
	            enterAndroidChar(eachCharacter);
	        }
		}
		
	}
	

	public void enterAlphabetsOnKeyPad(String text) throws Throwable {

		for (char eachCharacter : text.toCharArray()) {
			enterAndroidChar(eachCharacter);
		}

	}
	
	


	public void enterAndroidChar(char eachCharacter) throws Throwable {

		if (null == element)
			throw new Throwable(": MB Actions : Element is null");
		if (DriverManagerFactory.getDriverData("Platform Name").equalsIgnoreCase("ANDROID")) {
			switch (Character.toString(eachCharacter).toUpperCase()) {
			case "A":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_1);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.A));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;

			case "B":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_1);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.B));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;
			case "C":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_1);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.C));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;
			case "D":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_1);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.D));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;
			case "E":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_1);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.E));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;
			case "F":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_1);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.F));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;
			case "G":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_1);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.G));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;
			case "H":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_1);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.H));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;
			case "I":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_1);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.I));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;
			case "J":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_1);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.J));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;
			case "K":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_1);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.K));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;
			case "L":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_1);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.L));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;
			case "M":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_1);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.M));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;
			case "N":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_1);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.N));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;
			case "O":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_1);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.O));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;
			case "P":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_1);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.P));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;
			case "Q":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_1);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.Q));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;
				
			case "R":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_1);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.R));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;
			case "S":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_1);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.S));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;
			case "T":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_1);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.T));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;
			case "U":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_1);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.U));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;
			case "V":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_1);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.V));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;
			case "W":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_1);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.W));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;
			case "X":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_1);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.X));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;
			case "Y":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_1);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.Y));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;
			case "Z":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_1);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.Z));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;
				
			case "1":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_1);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.DIGIT_1));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;
			case "2":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_2);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.DIGIT_2));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;
			case "3":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_3);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.DIGIT_3));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;
			case "4":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_4);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.DIGIT_4));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;
			case "5":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_5);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.DIGIT_5));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;
			case "6":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_6);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.DIGIT_6));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;
			case "7":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_7);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.DIGIT_7));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;
			case "8":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_8);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.DIGIT_8));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;
			case "9":
				// (AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_9);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.DIGIT_9));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;
			case "0":
				// ((AndroidDriver) mobileDriver).pressKeyCode(AndroidKeyCode.KEYCODE_NUMPAD_0);
				((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.DIGIT_0));
				logger.info("Entered :: " + eachCharacter + " on " + element);
				break;	

			}

		}

	}


	public void capturePhotoAndroid() throws InterruptedException {  
		((AndroidDriver) mobileDriver).pressKey(new KeyEvent(AndroidKey.CAMERA));
		Thread.sleep(5000);
		
		
	//	((AndroidDriver) mobileDriver).findElement(By.xpath("//android.view.View[@resource-id='com.motorola.cameraonemtk:id/preview_surface']"));
		
	}
}


	
