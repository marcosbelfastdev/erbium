package com.github.marcosbelfastdev.erbium.core;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.interactions.internal.Locatable;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.marcosbelfastdev.erbium.core.Timer.sleep;
import static java.util.Objects.isNull;


public class Element {

	Driver _driver;
	// used to track highlighted elements and unhighlight them
	private WebElement _lastElementHighlighted;
	By _locator;
	WebElement _webElement;
	private PlaybackOptions _playbackOptions;
	private StopWatch stopWatch;

	// Somehow volatile fields
	private String _window;
	private String $startWindowHandle;
	private final List<String> $knownWindowHandles = new ArrayList<>();

	// other properties
	String _elementName;

	public Element(By by) {
		setStopWatch();
		setLocator(by);
		setPlaybackOptions();
	}

	private void setStopWatch() {
		this.stopWatch = new StopWatch();
	}

	private void setLocator(By by) {
		_locator = by;
	}

	private void setPlaybackOptions() {
		_playbackOptions = new PlaybackOptions();
	}

	public void setElementName(String name) {
		_elementName = name;
	}


	@SuppressWarnings("all")
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


	public Map<Common, Object> getOptions() {
		return _playbackOptions.getOptions();
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
				if (requiresExecutorClick() || isDisabled() || !isDisplayed()) {
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
		stopWatch.reset();
		startPointWindowLocking();
		handleDiplayedStatus();
		handleEnabledStatus();
		handleAutoScrolling();
		handleHighlight();
		handleDelayToInteract(stopWatch.elapsedTime());
	}

	private void startPointWindowLocking() {

		// Option must be active
		// and window must have been set by reload() or lockToWindow()
		// set a window if null

		if (shouldLockToWindow()) {
			if (isNull(_window)) {
				try {
					_driver.getWrappedWebDriver().switchTo().window(_window);
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
		if (isDisplayed())
			if (shouldHighlight())
				highlight();
	}


	protected void handleDelayToInteract(long elapsed) {
		// deduct any previous delay from desired delay to interact
		if (allowDelays())
			sleep(getDelayToInteractBefore() - elapsed);
	}

	protected void handleDelayToInteractAfter() {
		if (allowDelays())
			Timer.sleep((long)getOption(Common.INTERACT_DELAY_AFTER));
	}

	private long getDelayToInteractBefore() {
		return (long) getOption(Common.INTERACT_DELAY_BEFORE);
	}


	void healthCheck()  {
		if(isNull(_webElement))
			reload();

		try {
			_webElement.isDisplayed();
		} catch (Exception e) {
			reload();
		}
	}

	// forces reload of element
	// especially when OPT_LOAD_ON_DEMAND is on.
	void reload()  {
		if (isNull(_webElement))
			_webElement = _driver.getWrappedWebDriver().findElement(_locator);
		else {
			List<WebElement> _webElements =_driver.getWrappedWebDriver().findElements(_locator);
			if (_webElements.size() > 0)
				_webElement = _webElements.get(0);
		}

		if (isNull(_window))
			lockToWindow();
	}

	public String getHomeWindow() {
		return _window;
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
		if (requiresElementEnabled() && isDisabled()) {
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
		int attempts = 0;

		do {
			// this will chase any new windows opened during
			// the course of closing them, such as when new windows
			// are opened quickly
			attempts += 1;
			for (String windowHandle : windows) {
				if (!windowHandle.equals(_window))
					try {
						_driver.getWrappedWebDriver().switchTo().window(windowHandle);
						_driver.getWrappedWebDriver().close();
					} catch (Exception ignore) {
					}
			}
			windows.clear();
			windows.addAll(_driver.getWrappedWebDriver().getWindowHandles());

		} while (windows.size() > 1 && attempts <= 50);

		_driver.getWrappedWebDriver().switchTo().window(_window);

	}

	public void closeWindow() {
		try {
			_driver.getWrappedWebDriver().switchTo().window(_window);
			_driver.getWrappedWebDriver().close();
			_driver.frameworkSettingsProtection();
		} catch (Exception ignore) {
		}
	}

	private void switchToNewWindowOpened() {
//		try {
//			Set<String> windows = _driver.getWindowHandles();
//			windows.remove(_window);
//			_driver.getWrappedWebDriver().switchTo().window(_newWindowOrder);
//		} catch (Exception ignored) {
//
//		}
	}

	private boolean shouldHighlightFrames() {
		return (boolean) getOption(Common.HIGHLIGHT_FRAMES);
	}

	private boolean shouldLockToWindow() {
		return (boolean) getOption(Common.WINDOW_LOCKING);
	}

	public void hide() {
		boolean suppressDelays = (boolean) getOption(Common.SUPPRESS_DELAYS);
		setOption(Common.SUPPRESS_DELAYS, true);
		startPoint();
		jsHide();
		setOption(Common.SUPPRESS_DELAYS, suppressDelays);
		exitPoint();
	}

	private void jsHide() {
		_driver.executeScript("arguments[0].style.display = \"none\";", _webElement);
	}

    public Element dragAndDrop(By by) {
		Element element = new Element(by);
        return dragAndDrop(element);
    }

    public Element dragAndDrop(Element target) {
	    startPoint();
		target.startPoint();
        new Actions(_driver.getWrappedWebDriver()).dragAndDrop(this.getWrappedWebElement(), target.getWrappedWebElement()).perform();
        target.exitPoint();
	    exitPoint();
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

	private boolean requireExecutorClear() {
		return (boolean) getOption(Common.EXECUTOR_CLEAR);
	}

	private boolean requireExecutorSetText() {
		return (boolean) getOption(Common.EXECUTOR_SETTEXT);
	}

	public Element setPassword(String text) {
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
		}
		healthCheck();
		// if Javascript is enforced or we know beforehand
		// element is disabled or not visible (therefore Javascript fallback)
		try {
			if ((requireExecutorSetText() || !isDisplayed() || isDisabled())
					|| ((!requireExecutorSetText() && shouldFallbackToExecutor()))) {
				jsSetValue(text);
				exitPoint();
				return this;
			}
			// finally, Javascript is not enforced in options
			// nor required due to element not enabled or visible
			if (!requireExecutorSetText()) {
				try {
					_webElement.sendKeys(text);
				} catch (Exception e) {
					// fallback to javascript
					if (shouldFallbackToExecutor()) {
						jsSetValue(text);
					}
				}
			}
		} catch (Exception e) {
			String message = "An error occurred trying to enter password on " + getName();
		}
		exitPoint();
		return this;
	}

	public Element clear() {
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
		} catch (Exception e) {
			String message = "An error occurred trying to clear text on " + getName();
		}
		return this;
	}

	boolean isScreenshotEnabled() {
		return (boolean) getOption(Common.ENABLE_SCREENSHOTS);
	}

	void disableScreenshots() {
		setOption(Common.ENABLE_SCREENSHOTS, false);
	}

	public Element setText(String text) {
		String logText = "Entered data '" + text + "' in " + getName();
		class JsSetValue {
			void run() {
				healthCheck();
				try {
					jsSetValue(text);
				} catch (Exception e) {
					recoverActionInterceptor();
					healthCheck();
					jsSetValue(text);
				}
				exitPoint();
			}
		}
		class SendKeys {
			void run()  {
				healthCheck();
				try {
					_webElement.sendKeys(text);
				} catch (Exception e) {
					recoverActionInterceptor();
					healthCheck();
					_webElement.sendKeys(text);
				}
				exitPoint();
			}
		}
		class SetTextError {
			void run(Exception e) {
				String message = "An error occurred trying to enter data on " + getName();
				e.printStackTrace();
			}
		}
		startPoint();
		// if Javascript is enforced or we know beforehand
		// element is disabled or not visible (therefore Javascript fallback)
		try {
			if ((requireExecutorSetText() || !isDisplayed() || isDisabled())
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

	protected Element jsScroll() {
		_driver.executeScript("arguments[0].scrollIntoView({behavior: \"smooth\", block: \"center\"});", _webElement);
		if (allowDelays())
			StopWatch.sleep((long)getOption(Common.SCROLL_DELAY_AFTER));
		return this;
	}

//	public Element scrollDown() {
//		return scroll();
//	}
//
//	Element scroll() {
//		return scroll(SearchScroll.SCROLL_TO_TOP_FIRST, SearchScroll.SCROLL_FACTOR);
//	}
//
//	EElement scroll(SearchScroll direction, SearchScroll options) {
//		if (!isExecutorEnabled())
//			return this;
//
//		double searchScrollFactor = (double) getOption(Common.SEARCHSCROLL_FACTOR);
//		int searchScrollHeight = (int) getOption(Common.SEARCHSCROLL_HEIGHT);
//		int searchScrollDelay = (int) getOption(Common.SEARCHSCROLL_RESOLVE);
//		int signal = (direction == SearchScroll.SCROLL_DOWN || direction == SearchScroll.SCROLL_TO_TOP_FIRST) ? 1 : -1;
//		int offset = (options == SearchScroll.SCROLL_FACTOR)
//				? (int) ((double) _driver.getWebDriver().manage().window().getSize().height * searchScrollFactor)
//				: searchScrollHeight;
//		if (direction == SearchScroll.SCROLL_TO_TOP_FIRST)
//			_driver.executeScript("window.scrollTo(0,0);");
//		long start = System.currentTimeMillis();
//		long elapsed = 0;
//		WebElement webElement = null;
//		while (elapsed < getSearchScrollTimeout()) {
//			try {
//				screenshot(ScreenshotPoint.Before);
//				try {
//					if (_webElement.isDisplayed()) {
//						break;
//					}
//				} catch (Exception ignore) {}
//				for (int i = 0; i <= offset; i = i + 7) {
//					_driver.executeScript("window.scrollBy(0, " + 7 * signal + ");");
//				}
//				try {
//					webElement = new WebDriverWait(_driver.getWebDriver(), searchScrollDelay, getRetryInterval())
//							             .until(ExpectedConditions.presenceOfElementLocated(_locator));
//				} catch (Exception ignore) {}
//				screenshot(ScreenshotPoint.After);
//				if (webElement != null) {
//					_webElement = webElement;
//				}
//			} catch (Exception ignore) {}
//			elapsed = System.currentTimeMillis() - start;
//		}
//		return this;
//	}

	public Object getOption(Common option) {
		Object value;
		try {
			value = _playbackOptions.getOption(option);
		} catch (Exception e) {
			value = _driver.getOption(option);
		}
		return value;
	}

//	public EElement scrollUp() {
//		return scroll(EDriver.SearchScroll.SCROLL_UP, EDriver.SearchScroll.SCROLL_FACTOR);
//	}

//	public boolean isImageLoaded() {
//		long start = System.nanoTime();
//		boolean isDisplayed = false;
//		do {
//			isDisplayed = (boolean) _driver.executeScript("return arguments[0].complete " + "" +
//					                                                   "&& typeof arguments[0].naturalWidth != \"undefined\" && arguments[0].naturalWidth > 0", _webElement);
//		} while ((System.nanoTime() - start) / Math.pow(10, 6) < (int) getOption(Common.VISIBLE_TIMEOUT));
//		if(isDisplayed)
//			log(Reporting.LOG_TEST_STEPS, "Image " + getName() + " was found.");
//		else
//			log(Reporting.LOG_TEST_STEPS, "Image " + getName() + " was not found.");
//		return isDisplayed;
//	}

	protected Element highlight(HighlightOptions... options) {
		return highlight(getHighLightStyle(), options); //  deepskyblue border-radius: 1px;
	}

	public Element highlight() {
		return highlight(null, null);
	}

	public Element highlight(String style, HighlightOptions... options) {
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
		if (getAfterHighlightDelay() > 0 && allowDelays()) {
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
		try {
			if (getLastElementHighlighted() != null)
				_driver.executeScript("arguments[0].setAttribute('style', arguments[1]);",
						getLastElementHighlighted(), "");
		} catch (Exception ignored) {} // as long as it has removed the border!
	}

	private int getAfterHighlightDelay() {
		return (int) getOption(Common.HIGHLIGHT_DELAY_AFTER);
	}

	private boolean allowDelays() {
		return !((boolean) getOption(Common.SUPPRESS_DELAYS));
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
		exitPoint();
	}

	public void load() {
		reload();
	}

	public void lockToWindow() {
		if (!shouldLockToWindow())
			return;
		try {
			lockToWindow(_driver.getWrappedWebDriver().getWindowHandle());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void lockToWindow(String handle) {
		_window = handle;
	}

	public Element setFocus() {
		startPoint();
		healthCheck();
		jsSetFocus();
		exitPoint();
		return this;
	}

	private void jsSetFocus() {
		try {
			_driver.executeScript("arguments[0].focus();", _webElement);
		} catch (Exception ignore) {

		}
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

	public boolean isDisabled() {
		healthCheck();
		return !_webElement.isEnabled();
	}

	public String getText() {
		healthCheck();
		String value = null;
		startPoint();
		value = _webElement.getText();
		exitPoint();
		return value;
	}

	public List<Element> findElements(By locator) {
		List<WebElement> webElements = _webElement.findElements(locator);
		List<Element> elements = new ArrayList<>();
		for(WebElement webElement : webElements)
			elements.add(new Element(locator));
		return elements;
	}

	public List<Element> minFindElements(By locator, int minElements) {
		// TO DO
		// Create logic to wait until a minimum number of elements exit
		return findElements(locator);
	}

	public boolean isDisplayed() {
		healthCheck();
		return _webElement.isDisplayed();
	}

	public boolean isFullyDisplayed() {
		healthCheck();
		Rectangle rectangle = _webElement.getRect();
		Dimension dimension = _driver.getWrappedWebDriver().manage().window().getSize();
		return rectangle.getX() >= 0 &&
				rectangle.getY() >= 0 &&
				rectangle.getX() + rectangle.width <= dimension.width &&
				rectangle.getX() + rectangle.height <= dimension.height;
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
		return ((Locatable) _driver.getWrappedWebDriver()).getCoordinates();
	}

	public enum HighlightOptions {
		DoNotUnhighlight,
		UnhighlightAfter
	}
}
