package org.base.erbium;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Coordinates;
import org.openqa.selenium.interactions.Locatable;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.base.erbium.EDriver.*;


public class EElement {

	EDriver $driver;
	// used to track highlighted elements and unhighlight them
	private WebElement $lastElementHighlighted;
	private final String EMPTY_STRING = "";
	By $locator;
	WebElement $webElement;

	private PlaybackOptionsSet $playbackOptionsSet = new PlaybackOptionsSet();
	private EFrame $frame;
	
	// Somehow volatile fields
	private String $window;
	private String $startWindowHandle;
	private final List<String> $knownWindowHandles = new ArrayList<>();

	// Screenshot settings
	private ScreenshotArea $screenShotArea;
	private ScreenshotMode $screenShotMode;
	private ScreenshotPoint $screenShotPoint;

	// other properties
	String $name;
	ELogDispatcher $dispatcher;
	EScreenShotDispatcher $screenShotDispatcher;
	

	EElement(EDriver driver) {
		$driver = driver;
	}

	EElement(EDriver driver, By locator) {
		$driver = driver;
		$locator = locator;
	}

	EElement(EDriver driver, WebElement webElement) {
		$driver = driver;
		$webElement = webElement;
	}
	
	public EElement(EElement element) {
		/* This constructor should normally be used
		for classes extending erbium elements
		 */
		$driver = element.getDriver();
		$webElement = element.getWebElement();
		$locator = element.getBy();
		$frame = element.getFrame();
		$playbackOptionsSet = element.getOptions();
	}
	
	protected EElement setBy(By locator) {
		$locator = locator;
		return this;
	}

	public void setName(String name) {
		$name = name;
	}

	public EDriver getDriver() {
		return $driver;
	}

	public String getName() {

		/*
		Provides a short description to inform what the element is.
		There is a list of preferred names in this order:
		- mapped name if not null
		- custom description if not null
		- locator if not null
		- tag + one of the following properties: id, name, getText();
		 */
		StringBuilder builder = new StringBuilder();
		String attribute;
		try {
			if ($name != null)
				return "(" + $name + ")";
			if ($locator != null)
				return "(" + $locator.toString() + ")";
			builder.append("(");
			builder.append(getTagName());
			attribute = getAttribute("id");
			if (attribute.length() > 0)
				builder.append(":" + attribute + "]");
			attribute = getAttribute("name");
			if (attribute.length() > 0)
				builder.append(":" + attribute + "]");
			attribute = getAttribute("class");
			if (attribute.length() > 0)
				builder.append(":" + attribute + "]");
			builder.append(")");
		} catch (Exception ignore) {}

		String name = builder.toString();
		if(name.equals(EMPTY_STRING))
			name = "(Unnamed)";
		return name;
	}

	void log(Reporting item, String message) {
		if($dispatcher==null) {
			$dispatcher = new ELogDispatcher($driver);
		}
		$dispatcher.log(item, message);
	}

	void screenshot(ScreenshotMode mode, String file) {
		if($screenShotDispatcher==null) {
			$screenShotDispatcher = new EScreenShotDispatcher($driver);
		}
		$screenShotDispatcher.screenshot(mode, file);
	}

	void screenshot(ScreenshotPoint point) {
		// check if screenshots are not enabled at all
		if(! (boolean) getOption(Common.ENABLE_SCREENSHOTS))
			return;

		if(!(boolean) $driver.getReportingOptions().getOption(Reporting.REPORT_TO_TESTNG) &&
				$driver.getScreenShotMode()==ScreenshotMode.InLine) {
			// no inline mode and not reporting to TestNG - only reporting tool
			// up to now, so obviously not screenshoting.
			return;
		}
		boolean shouldScreenshot = false;
		var screenshotPointState = getScreenShotPoint();
		switch(screenshotPointState) {
			case Before:
				if(point.equals(ScreenshotPoint.Before) || point.equals(ScreenshotPoint.BeforeAndAfter)) {
					shouldScreenshot = true;
				}
				break;
			case After:
				if(point.equals(ScreenshotPoint.After) || point.equals(ScreenshotPoint.BeforeAndAfter)) {
					shouldScreenshot = true;
				}
				break;
			case BeforeAndAfter:
				shouldScreenshot = true;
				break;
			case Error:
				shouldScreenshot = true;
		}
		if(shouldScreenshot) {
			screenshot(getScreenShotMode(), String.valueOf(this.hashCode()));
		}
	}

	//@Override
	public WebElement getWebElement() {
		return $webElement;
	}
	
	public By getBy() {
		return $locator;
	}
	
	protected EFrame getFrame() {
		return $frame;
	}
	
	public PlaybackOptionsSet getOptions() {
		return $playbackOptionsSet;
	}
	
	public void setFrame(EFrame eFrame) {
		$frame = eFrame;
		log(Reporting.LOG_FRAMES, "Assigned frame " + eFrame.getEElement().getName() + " to " + getName());
	}

	public EElement setOption(Common option, Object value) {
		$playbackOptionsSet.setOption(option, value);
		log(Reporting.LOG_OPTIONS, "Element common " + option.toString() +
				" was set to " + value.toString() + " in " + getName());
		return this;
	}
	
	public EElement setOption(Common option) {
		return setOption(option, true);
	}

	public void setOption(ScreenshotArea area) {
		$screenShotArea = area;
	}

	public void setOption(ScreenshotMode mode) {
		$screenShotMode = mode;
	}
	
	public void clearOptions() {
		$playbackOptionsSet.clearOptions();
		$screenShotMode = null;
		$screenShotArea = null;
		log(Reporting.LOG_OPTIONS, "Cleared all options in " + getName());
	}
	
	protected boolean shouldUseTimers() {
		return (boolean) getOption(Common.ENABLE_TIMERS);
	}
	
	protected boolean isExecutorEnabled() {
		return true;
	}
	
	protected int getSearchScrollTimeout() {
		return (int) getOption(Common.SEARCHSCROLL_TIMEOUT);
	}
	
	protected boolean shouldHandleAlerts() {
		return (boolean) getOption(Common.HANDLE_ALERTS);
	}

	
	protected boolean shouldScroll() {
		return (boolean) getOption(Common.SCROLL_TO_ELEMENTS);
	}
	
	protected boolean shouldHighlight() {
		return (boolean) getOption(Common.HIGHLIGHT_ELEMENTS);
	}
	
	protected int getResolveTimeout() {
		return (int) getOption(Common.RESOLVE_TIMEOUT);
	}
	
	protected boolean requiresExecutorClick() {
		return (boolean) getOption(Common.EXECUTOR_CLICKS);
	}
	
	protected boolean shouldFallbackToExecutor() {
		return (boolean) getOption(Common.FALLBACK_TO_EXECUTOR);
	}

	void recoverActionInterceptor() {

	}
	
	public EElement click()  {

		class JsClick {
			void run() {
				screenshot(ScreenshotPoint.Before);
				healthCheck();
				try {
					jsClick();
				} catch (Exception e) {
					recoverActionInterceptor();
					healthCheck();
					jsClick();
				}
				log(Reporting.LOG_TEST_STEPS, "Clicked " + getName());
				screenshot(ScreenshotPoint.After);
				exitPoint();
			}
		}

		class Click {
			void run()  {
				screenshot(ScreenshotPoint.Before);
				healthCheck();
				try {
					$webElement.click();
				} catch (Exception e) {
					recoverActionInterceptor();
					healthCheck();
					$webElement.click();
				}
				log(Reporting.LOG_TEST_STEPS, "Clicked " + getName());
				screenshot(ScreenshotPoint.After);
				exitPoint();
			}
		}

		class ClickError {
			void run(Exception e) {
				String message = "An error occurred trying to click on " + getName();
				log(Reporting.LOG_TEST_STEPS, message);
				screenshot(ScreenshotPoint.Error);
				e.printStackTrace();
			}
		}

		// process before any usage
		// startPoint is mandatory

		startPoint();

        /*
        If Javascript injection is enabled and
        If Javascript is required or (element is not visible or not enabled) and
        If, in case Javascript is not required, and a fallback to Javascript is allowed
        Then, use Javascript to click
        */

        try {
			if (isExecutorEnabled()) {
				/* If javascript is required or element is not visible nor enabled */
				if (requiresExecutorClick() || !isEnabled() || !isDisplayed()) {
					/* Or if */
					if ((!requiresExecutorClick() && shouldFallbackToExecutor()) || requiresExecutorClick()) {
						new JsClick().run();
						return this;
					}
				}
			}
		} catch (Exception e) {
        	new ClickError().run(e);
		}
		
		// Click using Selenium click()
		try {
			try {
				// verify object is clickable indeed
				new Click().run();

			} catch (Exception e) {
				// fallback to javascript in case of any error
				// but only if a fallback and injection are both allowed
				if (shouldFallbackToExecutor() && isExecutorEnabled())
					new JsClick().run();
			}
		} catch (Exception e) {
			new ClickError().run(e);
		}
		
		return this;
		
	}
	
	void startPoint() {
		startPoint(null, null, null);
	}
	
	void startPoint(String timer, Timer type) {
		startPoint(timer, type, null);
	}
	
	void startPoint(String timer, Timer type, By locator) {
		$driver.frameworkSettingsProtection();
		startPointWindowLocking();
		startPointWindowChase();
		startPointHandleFrame();
		handleDiplayedStatus();
		handleEnabledStatus();
		handleAutoScrolling();
		handleHighlight();
		handleDelayToInteract();
	}
	
	private void startPointWindowLocking() {
		
		// Option must be active
		// and window must have been set by reload() or lockToWindow()
		// set a window if null
		
		if (shouldLockToWindow()) {
			if ($window!=null) {
				try {
					$driver.getWebDriver().switchTo().window($window);
					log(Reporting.LOG_WINDOWS_ACTIONS, "Switched to " + getName() + " home window.");
					log(Reporting.LOG_WINDOWS_DETAILS, "Window handle for " + getName() + " is " + $window);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				// assign a home window
				lockToWindow();
			}
		}
		
	}
	
	private void startPointWindowChase() {
		
		// this helps to control a new window opened
		// that is to be created by this action
		// this goes irrespective of window locking for an element
		
		if (shouldControlWindows()) {
			if ($window == null)
				$window = $driver.getWebDriver().getWindowHandle();
			$startWindowHandle = $window;
			$knownWindowHandles.clear();
			$knownWindowHandles.addAll($driver.getWebDriver().getWindowHandles());
		}
	}
	
	private void startPointHandleFrame() {
		if ($frame != null) {
			try {
				frameHealthCheck(); // this will switch to frame
				// needs to go back to default content in exitPoint
			} catch (Exception e) {
				//throw new Exception("The frame holding this element could not be found.");
				System.out.println(e.toString());
			}
		}
	}
	
	protected void handleAutoScrolling() {
		if (isDisplayed()) {
			if (shouldScroll()) {
				jsScroll();
			}
		}
	}
	
	protected void handleHighlight() {
		if (isDisplayed()) {
			if (shouldHighlight()) {
				highlight();
			}
		}
	}
	
	
	protected void handleDelayToInteract() {
		
		long start = System.currentTimeMillis();
		
		// deduct any previous delay from desired delay to interact
		if (!shouldSuppressAllDelays()) {
			int diff = (int) (getDelayToInteract() - (System.currentTimeMillis() - start));
			if (diff > 0) {
				try {
					Thread.sleep(diff);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected void handleDelayToInteractAfter() {
		try {
			Thread.sleep((int)getOption(Common.INTERACT_DELAY_AFTER));
		} catch (Exception ignore) {

		}
	}
	
	private int getDelayToInteract() {
		return (int) getOption(Common.INTERACT_DELAY_BEFORE);
	}
	
	
	void healthCheck()  {

		if($webElement == null) {
			reload();
		}

		try {
			$webElement.isDisplayed();
		} catch (Exception e) {
			reload();
		}

	}
	
	// forces reload of element
	// especially when OPT_LOAD_ON_DEMAND is on.
	void reload()  {
		
		PlaybackOptionsSet options = new PlaybackOptionsSet();
		options.setOption(Common.RESOLVE_TIMEOUT, getResolveTimeout());
		options.setOption(Common.RETRY_INTERVAL, getRetryInterval());
		
		Finder finder = new Finder();
		finder.setFinderOptions(options);
		try {
			$webElement = finder.findElement($driver, $locator);
		} catch (Exception ignore) {

		}

		if(shouldLockToWindow()) {
			if ($window == null) {
				lockToWindow();
			}
		}
	}
	
	public String getHomeWindow() {
		return $window;
	}
	
	protected void handleDiplayedStatus() {
		
		if (requiresElementVisible() && !isDisplayed()) {
			WebDriverWait waitVisible = new WebDriverWait($driver.getWebDriver(),
										(long) getElementVisibleTimeout() / 1000, getRetryInterval());
			waitVisible.until(ExpectedConditions.visibilityOf($webElement));
		}
	}
	
	public int getRetryInterval() {
		return (int) getOption(Common.RETRY_INTERVAL);
	}
	
	public boolean requiresElementVisible() {
		return (boolean) getOption(Common.REQUIRE_VISIBLE);
	}
	
	public int getElementVisibleTimeout() {
		return (int) getOption(Common.VISIBLE_TIMEOUT);
	}
	
	protected void handleEnabledStatus() {
		if (requiresElementEnabled() && !isEnabled()) {
			WebDriverWait waitEnabled = new WebDriverWait($driver.getWebDriver(),
										(long) getElementEnabledTimeout() / 1000, getRetryInterval());
			waitEnabled.until(ExpectedConditions.elementToBeClickable($webElement));
		}
	}
	
	public boolean requiresElementEnabled() {
		return (boolean) getOption(Common.REQUIRE_ENABLED);
	}
	
	public int getElementEnabledTimeout() {
		return (int) getOption(Common.ENABLED_TIMEOUT);
	}
	
	void exitPoint() {

		handleDelayToInteractAfter();
		switchToNewWindowOpened();

		if($dispatcher!=null) {
			while ($dispatcher.getThread().isAlive()) {

			}
			$dispatcher.finalise();
		}
		$dispatcher = null;

		if($screenShotDispatcher!=null) {
			$screenShotDispatcher.finalise(EScreenShotDispatcher.ThreadType.File);
			$screenShotDispatcher.finalise(EScreenShotDispatcher.ThreadType.Base64);
		}

        $screenShotDispatcher = null;
		
	}
	
	public void closeForeignWindows() {
		
		List<String> windows = new ArrayList<>($driver.getWebDriver().getWindowHandles());
		//windows.addAll($drivers[$d].getWindowHandles());
		int attempts = 0;
		
		do {
			// this will chase any new windows opened during
			// the course of closing them, such as when new windows
			// are opened quickly
			attempts += 1;
			for (String windowHandle : windows) {
				if (!windowHandle.equals($window))
					try {
						$driver.getWebDriver().switchTo().window(windowHandle);
						$driver.getWebDriver().close();
						log(Reporting.LOG_WINDOWS_ACTIONS, "A foreign window was closed by " + getName());
					} catch (Exception ignore) {
					}
			}
			windows.clear();
			windows.addAll($driver.getWebDriver().getWindowHandles());
			
		} while (windows.size() > 1 && attempts <= 50);

		$driver.getWebDriver().switchTo().window($window);
		
	}
	
	public void closeWindow() {
		try {
			$driver.getWebDriver().switchTo().window($window);
			$driver.getWebDriver().close();
			log(Reporting.LOG_WINDOWS_ACTIONS, "Home window closed by " + getName());
			log(Reporting.LOG_WINDOWS_ACTIONS, "Window handle was " + $window);
		} catch (Exception ignore) {
		}
	}
	
	private void switchToNewWindowOpened() {
		
		if(!shouldControlWindows())
			return;
		
		
		for (String windowHandle : $driver.getWebDriver().getWindowHandles()) {
			
			if (!windowHandle.equals($startWindowHandle)) {
				if (!$knownWindowHandles.contains(windowHandle)) {
					unhighlight(); // just not to leave element highlighted as we come back to this window
					$driver.getWebDriver().switchTo().window(windowHandle);
					log(Reporting.LOG_WINDOWS_ACTIONS, "Switched to a new window opened.");
					log(Reporting.LOG_WINDOWS_ACTIONS, "Window handle is " + windowHandle);
					break; // by criterion: move to first opened window
				}
			}
		}
		
		$knownWindowHandles.clear();
	}
	
	private void frameHealthCheck() {
		
		if($frame != null) {
			EElement frameEElement = $frame.getEElement();
			$driver.getWebDriver().switchTo().defaultContent();
			frameEElement.healthCheck();
			frameEElement.jsScroll();
			if(frameEElement.shouldHighlightFrames())
				frameEElement.highlight();
			$driver.getWebDriver().switchTo().frame(frameEElement.getWebElement());
			
		}
	}
	
	private boolean shouldHighlightFrames() {
		return (boolean) getOption(Common.HIGHLIGHT_FRAMES);
	}
	
	private boolean shouldLockToWindow() {
		return (boolean) getOption(Common.WINDOW_LOCKING);
	}
	
	public void hide() {
		
		if (!isExecutorEnabled())
			return;
		
		boolean suppressDelays = (boolean) getOption(Common.SUPPRESS_DELAYS);
		
		setOption(Common.SUPPRESS_DELAYS, true);
		startPoint();
		
		jsHide();
		log(Reporting.LOG_TEST_STEPS, "Element " + getName() + " has been hidden.");
		
		setOption(Common.SUPPRESS_DELAYS, suppressDelays);
		exitPoint();
	}
	
	private void jsHide() {
		$driver.executeScript("arguments[0].style.display = \"none\";", $webElement);
	}

    public EElement dragAndDrop(By by) {
		EElement element = new EElement($driver, by);
        return dragAndDrop(element);
    }

    public EElement dragAndDrop(EElement target) {
	    startPoint();
		target.startPoint();
        new Actions($driver.getWebDriver()).dragAndDrop(this.getWebElement(), target.getWebElement()).perform();
        target.exitPoint();
	    exitPoint();
	    log(Reporting.LOG_TEST_STEPS, "Element " + getName() + " was dropped onto element " + target.getName());
        return this;
    }
	
	private void jsClear() {
		jsSetValue("");
	}
	
	private void jsClick() {
		$driver.executeScript("arguments[0].click();", $webElement);
	}
	
	private void jsSetValue(String value) {
		$driver.executeScript("arguments[0].value = '" + value + "';", $webElement);
	}

	void logInterception(String text) {
		log(Reporting.LOG_TEST_STEPS, text);
	}
	
	private boolean requireExecutorClear() {
		return (boolean) getOption(Common.EXECUTOR_CLEAR);
	}
	
	private boolean requireExecutorSetText() {
		return (boolean) getOption(Common.EXECUTOR_SETTEXT);
	}
	
	public EElement setPassword(String text) {
		String encText;
		if((boolean) getOption(Common.HIDE_PASSWORDS))
			encText = "******";
		else
			encText = text;
		String logText = "Entered data '" + encText + "' in " + getName();
	    startPoint();
		if (isExecutorEnabled()) {
			try {
				$driver.executeScript("arguments[0].type=\"password\"", $webElement);
			} catch (Exception e) {
				$driver.executeScript("arguments[0].type=\"password\"", $webElement);
			}
		} else {
			String message = "SetPassword() requires Javascript, which has been disabled.";
			log(Reporting.LOG_TEST_STEPS, message);
			screenshot(ScreenshotPoint.Error);
		}
		healthCheck();
		// if Javascript is enforced or we know beforehand
		// element is disabled or not visible (therefore Javascript fallback)
		try {
			if ((requireExecutorSetText() || !isDisplayed() || !isEnabled())
					|| ((!requireExecutorSetText() && shouldFallbackToExecutor()))) {
				screenshot(ScreenshotPoint.Before);
				jsSetValue(text);
				log(Reporting.LOG_TEST_STEPS, logText);
				screenshot(ScreenshotPoint.After);
				exitPoint();
				return this;
			}
			// finally, Javascript is not enforced in options
			// nor required due to element not enabled or visible
			if (!requireExecutorSetText()) {
				try {
					screenshot(ScreenshotPoint.Before);
					$webElement.sendKeys(text);
					log(Reporting.LOG_TEST_STEPS, logText);
				} catch (Exception e) {
					// fallback to javascript
					if (shouldFallbackToExecutor()) {
						screenshot(ScreenshotPoint.Before);
						jsSetValue(text);
						log(Reporting.LOG_TEST_STEPS, logText);
						screenshot(ScreenshotPoint.After);
					}
				}
			}
		} catch (Exception e) {
			String message = "An error occurred trying to enter password on " + getName();
			log(Reporting.LOG_TEST_STEPS, message);
			screenshot(ScreenshotPoint.Error);
		}
		exitPoint();
		return this;
	}

	public EElement clear() {
		String logText = "Cleared text on " + getName();
		try {
			if ((requireExecutorClear() || !$webElement.isDisplayed() || !$webElement.isEnabled())
					|| ((!requireExecutorClear() && shouldFallbackToExecutor()))) {
				jsClear();
			} else {
				try {
					$webElement.clear();
				} catch (Exception e) {
					if (shouldFallbackToExecutor()) {
						healthCheck();
						jsClear();
					}
				}
			}
			log(Reporting.LOG_TEST_STEPS, logText);
		} catch (Exception e) {
			String message = "An error occurred trying to clear text on " + getName();
			log(Reporting.LOG_TEST_STEPS, message);
			screenshot(ScreenshotPoint.Error);
		}
		return this;
	}

	boolean isScreenshotEnabled() {
		return (boolean) getOption(Common.ENABLE_SCREENSHOTS);
	}

	void disableScreenshots() {
		setOption(Common.ENABLE_SCREENSHOTS, false);
	}
	
	public EElement setText(String text) {
		String logText = "Entered data '" + text + "' in " + getName();
		class JsSetValue {
			void run() {
				screenshot(ScreenshotPoint.Before);
				healthCheck();
				try {
					jsSetValue(text);
				} catch (Exception e) {
					recoverActionInterceptor();
					healthCheck();
					jsSetValue(text);
				}
				log(Reporting.LOG_TEST_STEPS, logText);
				screenshot(ScreenshotPoint.After);
				exitPoint();
			}
		}
		class SendKeys {
			void run()  {
				screenshot(ScreenshotPoint.Before);
				healthCheck();
				try {
					$webElement.sendKeys(text);
				} catch (Exception e) {
					recoverActionInterceptor();
					healthCheck();
					$webElement.sendKeys(text);
				}
				log(Reporting.LOG_TEST_STEPS, logText);
				screenshot(ScreenshotPoint.After);
				exitPoint();
			}
		}
		class SetTextError {
			void run(Exception e) {
				String message = "An error occurred trying to enter data on " + getName();
				log(Reporting.LOG_TEST_STEPS, message);
				screenshot(ScreenshotPoint.Error);
				e.printStackTrace();
			}
		}
		startPoint();
		// if Javascript is enforced or we know beforehand
		// element is disabled or not visible (therefore Javascript fallback)
		try {
			if ((requireExecutorSetText() || !isDisplayed() || !isEnabled())
					|| ((!requireExecutorSetText() && shouldFallbackToExecutor()) && isExecutorEnabled())) {
				new JsSetValue().run();
				return this;
			}
			// finally, Javascript is not enforced in options
			// nor required due to element not enabled or visible
			if (!requireExecutorSetText()) {
				//System.out.println("Doing setText without JavaScript a priori (2)");
				try {
					new SendKeys().run();
				} catch (Exception e) {
					// fallback to javascript
					if (shouldFallbackToExecutor() && isExecutorEnabled())
						new JsSetValue().run();
				}
			}

		} catch (Exception e) {
			new SetTextError().run(e);
		}
		exitPoint();
		return this;
	}
	
	protected EElement jsScroll() {
		if (!isExecutorEnabled())
			return this;
		try {
			$driver.executeScript("arguments[0].scrollIntoView({behavior: \"smooth\", block: \"center\"});", $webElement);
			int delay = (int) getOption(Common.SCROLL_DELAY_AFTER);
			if (!shouldSuppressAllDelays() && delay > 0) {
				try {
					Thread.sleep(delay);
					healthCheck();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (ElementNotInteractableException ignore) {}
		return this;
	}
	
	public EElement scrollDown() {
		return scroll();
	}
	
	EElement scroll() {
		return scroll(SearchScroll.SCROLL_TO_TOP_FIRST, SearchScroll.SCROLL_FACTOR);
	}
	
	EElement scroll(SearchScroll direction, SearchScroll options) {
		if (!isExecutorEnabled())
			return this;

		double searchScrollFactor = (double) getOption(Common.SEARCHSCROLL_FACTOR);
		int searchScrollHeight = (int) getOption(Common.SEARCHSCROLL_HEIGHT);
		int searchScrollDelay = (int) getOption(Common.SEARCHSCROLL_RESOLVE);
		int signal = (direction == SearchScroll.SCROLL_DOWN || direction == SearchScroll.SCROLL_TO_TOP_FIRST) ? 1 : -1;
		int offset = (options == SearchScroll.SCROLL_FACTOR)
				? (int) ((double) $driver.getWebDriver().manage().window().getSize().height * searchScrollFactor)
				: searchScrollHeight;
		if (direction == SearchScroll.SCROLL_TO_TOP_FIRST)
			$driver.executeScript("window.scrollTo(0,0);");
		long start = System.currentTimeMillis();
		long elapsed = 0;
		WebElement webElement = null;
		while (elapsed < getSearchScrollTimeout()) {
			try {
				screenshot(ScreenshotPoint.Before);
				try {
					if ($webElement.isDisplayed()) {
						break;
					}
				} catch (Exception ignore) {}
				for (int i = 0; i <= offset; i = i + 7) {
					$driver.executeScript("window.scrollBy(0, " + 7 * signal + ");");
				}
				try {
					webElement = new WebDriverWait($driver.getWebDriver(), searchScrollDelay, getRetryInterval())
							             .until(ExpectedConditions.presenceOfElementLocated($locator));
				} catch (Exception ignore) {}
				screenshot(ScreenshotPoint.After);
				if (webElement != null) {
					$webElement = webElement;
				}
			} catch (Exception ignore) {}
			elapsed = System.currentTimeMillis() - start;
		}
		return this;
	}

	public Object getOption(Common option) {
		Object value = $playbackOptionsSet.getOption(option);
		if(value==null)
			value = $driver.getOption(option);
		return value;
	}

	public ScreenshotMode getScreenShotMode() {
		ScreenshotMode value = $screenShotMode;
		if(value==null)
			value = $driver.getScreenShotMode();
		return value;
	}

	public ScreenshotPoint getScreenShotPoint() {
		ScreenshotPoint value = $screenShotPoint;
		if(value==null)
			value = $driver.getScreenShotPoint();
		return value;
	}

	
	public EElement scrollUp() {
		return scroll(EDriver.SearchScroll.SCROLL_UP, EDriver.SearchScroll.SCROLL_FACTOR);
	}
	
	public boolean isImageLoaded() {
		long start = System.nanoTime();
		boolean isDisplayed = false;
		do {
			isDisplayed = (boolean) $driver.executeScript("return arguments[0].complete " + "" +
					                                                   "&& typeof arguments[0].naturalWidth != \"undefined\" && arguments[0].naturalWidth > 0", $webElement);
		} while ((System.nanoTime() - start) / Math.pow(10, 6) < (int) getOption(Common.VISIBLE_TIMEOUT));
		if(isDisplayed)
			log(Reporting.LOG_TEST_STEPS, "Image " + getName() + " was found.");
		else
			log(Reporting.LOG_TEST_STEPS, "Image " + getName() + " was not found.");
		return isDisplayed;
	}

	void highlight(HighlightOptions... options) {
		highlight(getHighLightStyle(), options); //  deepskyblue border-radius: 1px;
	}
	
	public EElement highlight(String style, HighlightOptions... options) {
		if (!isExecutorEnabled())
			return this;
		if (style == null)
			style = getHighLightStyle();
		//process highlight options
		boolean leaveHighlighted = false;
		if (options != null) {
			for (HighlightOptions option : options) {
				if (option == HighlightOptions.DoNotUnhighlight) {
					leaveHighlighted = true;
					break;
				}
			}
		}
		unhighlight();
		this.healthCheck();
		$driver.executeScript("arguments[0].setAttribute('style', arguments[1]);", $webElement, style);
		if (!leaveHighlighted)
			setLastElementHighlighted($webElement);
		if (getAfterHighlightDelay() > 0 && !shouldSuppressAllDelays()) {
			try {
				Thread.sleep(getAfterHighlightDelay());
				healthCheck();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return this;
	}
	
	private String getHighLightStyle() {
		return (String) getOption(Common.HIGHLIGHT_STYLE);
	}
	
	void unhighlight() {
		if (!isExecutorEnabled())
			return;
		try {
			if (getLastElementHighlighted() != null)
				$driver.executeScript("arguments[0].setAttribute('style', arguments[1]);",
						getLastElementHighlighted(), "");
		} catch (Exception ignored) {} // as long as it has removed the border!
	}
	
	private int getAfterHighlightDelay() {
		return (int) getOption(Common.HIGHLIGHT_DELAY_AFTER);
	}
	
	private boolean shouldSuppressAllDelays() {
		return (boolean) getOption(Common.SUPPRESS_DELAYS);
	}
	
	private WebElement getLastElementHighlighted() {
		return $lastElementHighlighted;
	}
	
	private void setLastElementHighlighted(WebElement element) {
		$lastElementHighlighted = element;
	}

	public void submit() {
		startPoint();
		$webElement.submit();
		log(Reporting.LOG_TEST_STEPS, "A form was submitted by " + getName());
		exitPoint();
	}
	
	public void load() {
		reload();
		log(Reporting.LOG_TEST_STEPS, "Loaded element " + getName());
	}
	
	public void lockToWindow() {
		try {
			lockToWindow($driver.getWebDriver().getWindowHandle());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void lockToWindow(String handle) {
		$window = handle;
		log(Reporting.LOG_WINDOWS_ACTIONS, "Assigned a home window to " + getName());
		log(Reporting.LOG_WINDOWS_DETAILS, "Home window handle is " + $window);
	}
	
	public EElement setFocus() {
		if (!isExecutorEnabled()) {
			return this;
		}
		startPoint();
		healthCheck();
		jsSetFocus();
		log(Reporting.LOG_TEST_STEPS, "Set focus on " + getName());
		exitPoint();
		return this;
	}
	
	private void jsSetFocus() {
		try {
			$driver.executeScript("arguments[0].focus();", $webElement);
		} catch (Exception ignore) {}
	}

	public String getTagName() {
		healthCheck();
		return $webElement.getTagName();
	}

	public String getAttribute(String s) {
		healthCheck();
		return $webElement.getAttribute(s);
	}

	public boolean isSelected() {
		healthCheck();
		return $webElement.isSelected();
	}

	public boolean isEnabled() {
		healthCheck();
		return $webElement.isEnabled();
	}

	public String getText() {
		healthCheck();
		String value = null;
		startPoint();
		value = $webElement.getText();
		exitPoint();
		return value;
	}

	void setWebElement(WebElement webElement) {
		$webElement = webElement;
	}

	public EElement ifind(By locator) {
		return $driver.ifind(locator);
	}

	public EElement find(By locator) {
		return $driver.find(locator);
	}
	
	public List<EElement> findElements(By locator) {
		healthCheck();
		$driver.getWebDriver().manage().timeouts().implicitlyWait(getResolveTimeout(), TimeUnit.MILLISECONDS);
		List<WebElement> webElements = $webElement.findElements(locator);
		List<EElement> elements = new ArrayList<>();
		for(WebElement current : webElements) {
			EElement element = new EElement($driver);
			element.setWebElement(current);
			elements.add(element);
		}
		$driver.frameworkSettingsProtection();
		return elements;
	}

	public boolean isDisplayed() {
		healthCheck();
		return $webElement.isDisplayed();
	}

	public Point getLocation() {
		healthCheck();
		return $webElement.getLocation();
	}

	public Dimension getSize() {
		healthCheck();
		return $webElement.getSize();
	}

	public Rectangle getRect() {
		healthCheck();
		return $webElement.getRect();
	}

	public String getCssValue(String s) {
		healthCheck();
		return $webElement.getCssValue(s);
	}

	public Coordinates getCoordinates() {
		return ((Locatable)$driver.getWebDriver()).getCoordinates();
	}
	
	protected boolean shouldControlWindows() {
		return (boolean) getOption(Common.WINDOW_SEARCH);
	}

	public enum HighlightOptions {
		DoNotUnhighlight,
		UnhighlightAfter
	}
}
