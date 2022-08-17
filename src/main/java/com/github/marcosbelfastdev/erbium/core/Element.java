package com.github.marcosbelfastdev.erbium.core;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.interactions.internal.Locatable;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.github.marcosbelfastdev.erbium.core.Timer.sleep;


public class Element {

	Driver _driver;
	// used to track highlighted elements and unhighlight them
	private WebElement _lastElementHighlighted;
	By _locator;
	WebElement _webElement;
	private PlaybackOptions _playbackOptions;

	// Somehow volatile fields
	private String $window;
	private String $startWindowHandle;
	private final List<String> $knownWindowHandles = new ArrayList<>();

	// other properties
	String _elementName;

	public Element() {
		setPlaybackOptions();
	}

	private void setPlaybackOptions() {
		_playbackOptions = new PlaybackOptions();
	}


	public void setElementName(String name) {
		_elementName = name;
	}

	private Driver getDriver() {
		return _driver;
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
			if (_elementName != null)
				return "(" + _elementName + ")";
			if (_locator != null)
				return "(" + _locator.toString() + ")";
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
		if(name.equals(""))
			name = "(Unnamed)";
		return name;
	}


	public WebElement getWrappedWebElement() {
		return _webElement;
	}

	public By getBy() {
		return _locator;
	}


	public PlaybackOptions getOptions() {
		return _playbackOptions;
	}


	public Element setOption(Common option, Object value) {
		_playbackOptions.setOption(option, value);
		return this;
	}


	public void reset() {
		_playbackOptions = new PlaybackOptions();
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

	public Element click()  {

		class JsClick {
			void run() {
				healthCheck();
				try {
					jsClick();
				} catch (Exception e) {
					recoverActionInterceptor();
					healthCheck();
					jsClick();
				}
				exitPoint();
			}
		}

		class Click {
			void run()  {
				healthCheck();
				try {
					_webElement.click();
				} catch (Exception e) {
					recoverActionInterceptor();
					healthCheck();
					_webElement.click();
				}
				exitPoint();
			}
		}

		class ClickError {
			void run(Exception e) {
				String message = "An error occurred trying to click on " + getName();
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
		//_driver.frameworkSettingsProtection();
		Timer delayToInteract = new Timer(getDelayToInteractBefore());
		startPointWindowLocking();
		handleDiplayedStatus();
		handleEnabledStatus();
		handleAutoScrolling();
		handleHighlight();
		handleDelayToInteract(delayToInteract.elapsedTime());
	}

	private void startPointWindowLocking() {

		// Option must be active
		// and window must have been set by reload() or lockToWindow()
		// set a window if null

		if (shouldLockToWindow()) {
			if ($window!=null) {
				try {
					_driver.getWrappedWebDriver().switchTo().window($window);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				// assign a home window
				lockToWindow();
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


	protected void handleDelayToInteract(long elapsed) {
		// deduct any previous delay from desired delay to interact
		if (!shouldSuppressAllDelays())
			sleep(getDelayToInteractBefore() - elapsed);
	}

	protected void handleDelayToInteractAfter() {
		if (!shouldSuppressAllDelays())
			Timer.sleep((long)getOption(Common.INTERACT_DELAY_AFTER));
	}

	private long getDelayToInteractBefore() {
		return (long) getOption(Common.INTERACT_DELAY_BEFORE);
	}


	void healthCheck()  {

		if(_webElement == null) {
			reload();
		}

		try {
			_webElement.isDisplayed();
		} catch (Exception e) {
			reload();
		}

	}

	// forces reload of element
	// especially when OPT_LOAD_ON_DEMAND is on.
	void reload()  {

		PlaybackOptions options = new PlaybackOptions();
		options.setOption(Common.RESOLVE_TIMEOUT, getResolveTimeout());
		options.setOption(Common.RETRY_INTERVAL, getRetryInterval());

		Finder finder = new Finder();
		finder.setFinderOptions(options);
		try {
			_webElement = finder.findElement(_driver, _locator);
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
			WebDriverWait waitVisible = new WebDriverWait(_driver.getWrappedWebDriver(),
										(long) getElementVisibleTimeout() / 1000, getRetryInterval());
			waitVisible.until(ExpectedConditions.visibilityOf(_webElement));
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
			WebDriverWait waitEnabled = new WebDriverWait(_driver.getWrappedWebDriver(),
										(long) getElementEnabledTimeout() / 1000, getRetryInterval());
			waitEnabled.until(ExpectedConditions.elementToBeClickable(_webElement));
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

	}

	public void closeForeignWindows() {

		List<String> windows = new ArrayList<>(_driver.getWrappedWebDriver().getWindowHandles());
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
						_driver.getWrappedWebDriver().switchTo().window(windowHandle);
						_driver.getWrappedWebDriver().close();
					} catch (Exception ignore) {
					}
			}
			windows.clear();
			windows.addAll(_driver.getWrappedWebDriver().getWindowHandles());

		} while (windows.size() > 1 && attempts <= 50);

		_driver.getWrappedWebDriver().switchTo().window($window);

	}

	public void closeWindow() {
		try {
			_driver.getWrappedWebDriver().switchTo().window($window);
			_driver.getWrappedWebDriver().close();
			_driver.frameworkSettingsProtection();
		} catch (Exception ignore) {
		}
	}

	private void switchToNewWindowOpened() {

		if(!shouldControlWindows())
			return;


		for (String windowHandle : _driver.getWebDriver().getWindowHandles()) {

			if (!windowHandle.equals($startWindowHandle)) {
				if (!$knownWindowHandles.contains(windowHandle)) {
					unhighlight(); // just not to leave element highlighted as we come back to this window
					_driver.getWebDriver().switchTo().window(windowHandle);
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
			_driver.getWebDriver().switchTo().defaultContent();
			frameEElement.healthCheck();
			frameEElement.jsScroll();
			if(frameEElement.shouldHighlightFrames())
				frameEElement.highlight();
			_driver.getWebDriver().switchTo().frame(frameEElement.getWebElement());

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
		_driver.executeScript("arguments[0].style.display = \"none\";", _webElement);
	}

    public EElement dragAndDrop(By by) {
		EElement element = new EElement(_driver, by);
        return dragAndDrop(element);
    }

    public EElement dragAndDrop(EElement target) {
	    startPoint();
		target.startPoint();
        new Actions(_driver.getWebDriver()).dragAndDrop(this.getWrappedWebElement(), target.getWebElement()).perform();
        target.exitPoint();
	    exitPoint();
	    log(Reporting.LOG_TEST_STEPS, "Element " + getName() + " was dropped onto element " + target.getName());
        return this;
    }

	private void jsClear() {
		jsSetValue("");
	}

	private void jsClick() {
		_driver.executeScript("arguments[0].click();", _webElement);
	}

	private void jsSetValue(String value) {
		_driver.executeScript("arguments[0].value = '" + value + "';", _webElement);
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
				_driver.executeScript("arguments[0].type=\"password\"", _webElement);
			} catch (Exception e) {
				_driver.executeScript("arguments[0].type=\"password\"", _webElement);
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
					_webElement.sendKeys(text);
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
			if ((requireExecutorClear() || !_webElement.isDisplayed() || !_webElement.isEnabled())
					|| ((!requireExecutorClear() && shouldFallbackToExecutor()))) {
				jsClear();
			} else {
				try {
					_webElement.clear();
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
					_webElement.sendKeys(text);
				} catch (Exception e) {
					recoverActionInterceptor();
					healthCheck();
					_webElement.sendKeys(text);
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
			_driver.executeScript("arguments[0].scrollIntoView({behavior: \"smooth\", block: \"center\"});", _webElement);
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
				? (int) ((double) _driver.getWebDriver().manage().window().getSize().height * searchScrollFactor)
				: searchScrollHeight;
		if (direction == SearchScroll.SCROLL_TO_TOP_FIRST)
			_driver.executeScript("window.scrollTo(0,0);");
		long start = System.currentTimeMillis();
		long elapsed = 0;
		WebElement webElement = null;
		while (elapsed < getSearchScrollTimeout()) {
			try {
				screenshot(ScreenshotPoint.Before);
				try {
					if (_webElement.isDisplayed()) {
						break;
					}
				} catch (Exception ignore) {}
				for (int i = 0; i <= offset; i = i + 7) {
					_driver.executeScript("window.scrollBy(0, " + 7 * signal + ");");
				}
				try {
					webElement = new WebDriverWait(_driver.getWebDriver(), searchScrollDelay, getRetryInterval())
							             .until(ExpectedConditions.presenceOfElementLocated(_locator));
				} catch (Exception ignore) {}
				screenshot(ScreenshotPoint.After);
				if (webElement != null) {
					_webElement = webElement;
				}
			} catch (Exception ignore) {}
			elapsed = System.currentTimeMillis() - start;
		}
		return this;
	}

	public Object getOption(Common option) {
		Object value;
		try {
			value = _playbackOptions.getOption(option);
		} catch (Exception e) {
			value = _driver.getOption(option);
		}
		return value;
	}

	public EElement scrollUp() {
		return scroll(EDriver.SearchScroll.SCROLL_UP, EDriver.SearchScroll.SCROLL_FACTOR);
	}

	public boolean isImageLoaded() {
		long start = System.nanoTime();
		boolean isDisplayed = false;
		do {
			isDisplayed = (boolean) _driver.executeScript("return arguments[0].complete " + "" +
					                                                   "&& typeof arguments[0].naturalWidth != \"undefined\" && arguments[0].naturalWidth > 0", _webElement);
		} while ((System.nanoTime() - start) / Math.pow(10, 6) < (int) getOption(Common.VISIBLE_TIMEOUT));
		if(isDisplayed)
			log(Reporting.LOG_TEST_STEPS, "Image " + getName() + " was found.");
		else
			log(Reporting.LOG_TEST_STEPS, "Image " + getName() + " was not found.");
		return isDisplayed;
	}

	protected EElement highlight(HighlightOptions... options) {
		return highlight(getHighLightStyle(), options); //  deepskyblue border-radius: 1px;
	}

	public EElement highlight() {
		return highlight(null, null);
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
		_driver.executeScript("arguments[0].setAttribute('style', arguments[1]);", _webElement, style);
		if (!leaveHighlighted)
			setLastElementHighlighted(_webElement);
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
				_driver.executeScript("arguments[0].setAttribute('style', arguments[1]);",
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
		return _lastElementHighlighted;
	}

	private void setLastElementHighlighted(WebElement element) {
		_lastElementHighlighted = element;
	}

	public void submit() {
		startPoint();
		_webElement.submit();
		log(Reporting.LOG_TEST_STEPS, "A form was submitted by " + getName());
		exitPoint();
	}

	public void load() {
		reload();
		log(Reporting.LOG_TEST_STEPS, "Loaded element " + getName());
	}

	public void lockToWindow() {
		try {
			lockToWindow(_driver.getWebDriver().getWindowHandle());
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
			_driver.executeScript("arguments[0].focus();", _webElement);
		} catch (Exception ignore) {}
	}

	public String getTagName() {
		healthCheck();
		return _webElement.getTagName();
	}

	public String getAttribute(String s) {
		healthCheck();
		return _webElement.getAttribute(s);
	}

	public boolean isSelected() {
		healthCheck();
		return _webElement.isSelected();
	}

	public boolean isEnabled() {
		healthCheck();
		return _webElement.isEnabled();
	}

	public String getText() {
		healthCheck();
		String value = null;
		startPoint();
		value = _webElement.getText();
		exitPoint();
		return value;
	}

	void setWebElement(WebElement webElement) {
		_webElement = webElement;
	}

	public EElement ifind(By locator) {
		return _driver.ifind(locator);
	}

	public EElement find(By locator) {
		return _driver.find(locator);
	}

	public List<EElement> findElements(By locator) {
		healthCheck();
		_driver.getWebDriver().manage().timeouts().implicitlyWait(getResolveTimeout(), TimeUnit.MILLISECONDS);
		List<WebElement> webElements = _webElement.findElements(locator);
		List<EElement> elements = new ArrayList<>();
		for(WebElement current : webElements) {
			EElement element = new EElement(_driver);
			element.setWebElement(current);
			elements.add(element);
		}
		_driver.frameworkSettingsProtection();
		return elements;
	}

	public boolean isDisplayed() {
		healthCheck();
		return _webElement.isDisplayed();
	}

	public Point getLocation() {
		healthCheck();
		return _webElement.getLocation();
	}

	public Dimension getSize() {
		healthCheck();
		return _webElement.getSize();
	}

	public Rectangle getRect() {
		healthCheck();
		return _webElement.getRect();
	}

	public String getCssValue(String s) {
		healthCheck();
		return _webElement.getCssValue(s);
	}

	public Coordinates getCoordinates() {
		return ((Locatable) _driver.getWebDriver()).getCoordinates();
	}

	protected boolean shouldControlWindows() {
		return (boolean) getOption(Common.WINDOW_SEARCH);
	}

	public enum HighlightOptions {
		DoNotUnhighlight,
		UnhighlightAfter
	}
}
