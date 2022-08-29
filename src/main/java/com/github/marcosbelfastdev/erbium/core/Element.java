package com.github.marcosbelfastdev.erbium.core;

import com.github.marcosbelfastdev.erbium.exceptions.ErrorLockingToWindow;
import com.github.marcosbelfastdev.erbium.exceptions.NoElementFound;
import com.github.marcosbelfastdev.erbium.exceptions.SyncedFindElementsError;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.interactions.internal.Locatable;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.github.marcosbelfastdev.erbium.core.ErrorHandling.end;
import static com.github.marcosbelfastdev.erbium.core.Timer.sleep;
import static java.util.Objects.isNull;


public class Element extends ElementOptions {


	public Element(Driver driver, By by) {
		super(driver, by);
	}

	public Element(Driver driver, WebElement webElement) {
		super(driver, webElement);
	}

	private void doSelfCheck() throws Throwable {
		if(isNull(_webElement)) {
			load();
			return;
		}

		try {
			_webElement.isDisplayed();
		} catch (Exception e) {
			load();
		}

	}

	public String getTagName() throws Throwable {
		doSelfCheck();
		return _webElement.getTagName();
	}

	public String getAttribute(String s) throws Throwable {
		doSelfCheck();
		return _webElement.getAttribute(s);
	}


	@SuppressWarnings("all")
	public String getElementName() {

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
			if (_name != null)
				return "(" + _name + ")";
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
		} catch (Throwable ignore) {}

		String name = builder.toString();
		if(name.equals(""))
			name = "(Unnamed)";
		return name;
	}

	public Element click() throws Throwable {

		class JsClick {
			void run() throws Throwable {
				try {
					jsClick();
				} catch (Exception e) {
					doSelfCheck();
					jsClick();
				}
				exitPoint();
			}
		}

		class Click {
			void run() throws Throwable {
				try {
					_webElement.click();
				} catch (Exception e) {
					doSelfCheck();
					_webElement.click();
				}
				exitPoint();
			}
		}

		class ClickError {
			void run(Exception e) throws Throwable {
				end(ClickError.class);
			}
		}

		// process before any usage
		// startPoint is mandatory

		entry();

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

	void entry() throws Throwable {
		var sw = new StopWatch();
		doSelfCheck();
		doSwitchWindow();
		doDisplayedCheck();
		doEnabledCheck();
		doAutoScrolling();
		doHighlighting();
		doDelayBefore(sw.elapsedTime());
	}

	private void doSwitchWindow() throws Throwable {
		if (!isNull(_window)) {
			try {
				_driver.getWrappedWebDriver().switchTo().window(_window);
				return;
			} catch (Exception e) {
				end(e.getClass());
			}
		}
	}

	protected void doAutoScrolling() throws Throwable {
		if (isDisplayed()) {
			if (shouldScroll()) {
				jsScroll();
			}
		}
	}

	protected void doHighlighting() throws Throwable {
		if (isDisplayed())
			if (shouldHighlight())
				highlight();
	}


	protected void doDelayBefore(long elapsed) {
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




	public String getHomeWindow() {
		return _window;
	}

	protected void doDisplayedCheck() throws Throwable {
		if (requiresElementVisible() && !isDisplayed()) {
			WebDriverWait waitVisible = new WebDriverWait(_driver.getWrappedWebDriver(),
										(long) getElementVisibleTimeout() / 1000, getRetryInterval());
			waitVisible.until(ExpectedConditions.visibilityOf(_webElement));
		}
	}


	protected void doEnabledCheck() throws Throwable {
		if (requiresElementEnabled() && isDisabled()) {
			WebDriverWait waitEnabled = new WebDriverWait(_driver.getWrappedWebDriver(),
										(long) getElementEnabledTimeout() / 1000, getRetryInterval());
			waitEnabled.until(ExpectedConditions.elementToBeClickable(_webElement));
		}
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


	public void hide() throws Throwable {
		boolean suppressDelays = (boolean) getOption(Common.SUPPRESS_DELAYS);
		setOption(Common.SUPPRESS_DELAYS, true);
		entry();
		jsHide();
		setOption(Common.SUPPRESS_DELAYS, suppressDelays);
		exitPoint();
	}

	private void jsHide() {
		_driver.executeScript("arguments[0].style.display = \"none\";", _webElement);
	}

    public Element dragAndDrop(By by) throws Throwable {
		Element element = new Element(_driver, by);
        return dragAndDrop(element);
    }

    public Element dragAndDrop(Element target) throws Throwable {
	    entry();
		target.entry();
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


	public Element setPassword(String text) throws Throwable {
		String encText;
		if((boolean) getOption(Common.HIDE_PASSWORDS))
			encText = "******";
		else
			encText = text;
		String logText = "Entered data '" + encText + "' in " + getElementName();
	    entry();
		if (isExecutorEnabled()) {
			try {
				_driver.executeScript("arguments[0].type=\"password\"", _webElement);
			} catch (Exception e) {
				_driver.executeScript("arguments[0].type=\"password\"", _webElement);
			}
		} else {
			String message = "SetPassword() requires Javascript, which has been disabled.";
		}
		doSelfCheck();
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
			String message = "An error occurred trying to enter password on " + getElementName();
		}
		exitPoint();
		return this;
	}

	public Element clear() {
		String logText = "Cleared text on " + getElementName();
		try {
			if ((requireExecutorClear() || !_webElement.isDisplayed() || !_webElement.isEnabled())
					|| ((!requireExecutorClear() && shouldFallbackToExecutor()))) {
				jsClear();
			} else {
				try {
					_webElement.clear();
				} catch (Exception e) {
					if (shouldFallbackToExecutor()) {
						doSelfCheck();
						jsClear();
					}
				}
			}
		} catch (Throwable e) {
			String message = "An error occurred trying to clear text on " + getElementName();
		}
		return this;
	}


	void disableScreenshots() throws Throwable {
		setOption(Common.ENABLE_SCREENSHOTS, false);
	}

	public Element setText(String text) throws Throwable {
		String logText = "Entered data '" + text + "' in " + getElementName();
		class JsSetValue {
			void run() throws Throwable {
				doSelfCheck();
				try {
					jsSetValue(text);
				} catch (Exception e) {
					doSelfCheck();
					jsSetValue(text);
				}
				exitPoint();
			}
		}
		class SendKeys {
			void run() throws Throwable {
				doSelfCheck();
				try {
					_webElement.sendKeys(text);
				} catch (Exception e) {
					doSelfCheck();
					_webElement.sendKeys(text);
				}
				exitPoint();
			}
		}
		class SetTextError {
			void run(Exception e) {
				String message = "An error occurred trying to enter data on " + getElementName();
				e.printStackTrace();
			}
		}
		entry();
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

	/**
	 * Resets an element to its original state.
	 * @return
	 */
	@Override
	public Element reset() {
		setHomeWindow(null);
		_webElement = null;
		return this;
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

	public Element highlight() throws Throwable {
		unhighlight();
		this.doSelfCheck();
		_driver.executeScript("arguments[0].setAttribute('style', arguments[1]);", _webElement, getHighLightStyle());
		var sw = new StopWatch();
		_lastElementHighlighted = _webElement;
		if (getAfterHighlightDelay() > 0 && allowDelays()) {
			Timer.sleep(getAfterHighlightDelay() - sw.elapsedTime());
		}
		return this;
	}


	void unhighlight() {
		try {
			if (getLastElementHighlighted() != null)
				_driver.executeScript("arguments[0].setAttribute('style', arguments[1]);",
						getLastElementHighlighted(), "");
		} catch (Exception ignored) {} // as long as it has removed the border!
	}


	private WebElement getLastElementHighlighted() {
		return _lastElementHighlighted;
	}

	private void setLastElementHighlighted(WebElement element) {
		_lastElementHighlighted = element;
	}

	public void submit() throws Throwable {
		entry();
		_webElement.submit();
		exitPoint();
	}

	public void load() throws Throwable {
		if (isNull(_webElement)) {
			Wait<WebDriver> wait = new FluentWait<WebDriver>(_driver.getWrappedWebDriver())
					.withTimeout(resolve(), TimeUnit.MILLISECONDS)
					.pollingEvery(retryInterval(), TimeUnit.MILLISECONDS)
					.ignoring(NoSuchElementException.class);

			_webElement = wait.until(new Function<WebDriver, WebElement>() {
				public WebElement apply(WebDriver driver) {
					return driver.findElement(_locator);
				}
			});
		}

		if (isNull(_webElement))
			end(NoElementFound.class);

		if (isNull(_window))
			lockToWindow();
	}

	public Element reload() throws Throwable {
		if (isNull(_webElement)) {
			load();
		} else {
			try {
				_webElement = _driver.getWrappedWebDriver().findElements(_locator).get(0);
			} catch (Exception e) {
				end(NoElementFound.class);
			}
		}
		return this;
	}

	public void lockToWindow() throws Throwable {
		if (!shouldLockToWindow())
			return;
		try {
			setHomeWindow(_driver.getWindowHandle());
		} catch (Exception e) {
			end(ErrorLockingToWindow.class);
		}
	}

	private Element setHomeWindow(String handle) {
		_window = handle;
		return this;
	}

	public Element setFocus() throws Throwable {
		entry();
		doSelfCheck();
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

	public boolean isSelected() throws Throwable {
		doSelfCheck();
		return _webElement.isSelected();
	}

	public boolean isDisabled() throws Throwable {
		doSelfCheck();
		return !_webElement.isEnabled();
	}

	public String getText() throws Throwable {
		doSelfCheck();
		String value = null;
		entry();
		value = _webElement.getText();
		exitPoint();
		return value;
	}

	public List<Element> findElements(By locator) {
		List<WebElement> webElements = _webElement.findElements(locator);
		List<Element> elements = new ArrayList<>();
		for(WebElement webElement : webElements)
			elements.add(new Element(_driver, locator));
		return elements;
	}


	/**
	 * Searches for elements within the element hierarchy
	 * and returns a list with the minimum number of elements required.
	 * @param by
	 * @param minElements
	 * @return
	 * @throws Throwable
	 */
	public List<Element> syncedFind(By by, int minElements) throws Throwable {
		Timer timer = new Timer(resolve());
		List<Element> elements = new ArrayList<>();
		while (elements.size() < minElements && !timer.timedOut()) {
			elements = _driver.findElements(by);
		}
		if (elements.size() < minElements) {
			end(SyncedFindElementsError.class);
		}
		return elements;
	}


	public boolean isDisplayed() throws Throwable {
		boolean value;
		try {
			value = _webElement.isDisplayed() && _webElement.getRect().width > 0;
		} catch (Exception e) {
			doSelfCheck();
			value = _webElement.isDisplayed() && _webElement.getRect().width > 0;
		}
		return value;
	}

	public boolean isFullyDisplayed() throws Throwable {
		doSelfCheck();
		try {
			Rectangle rectangle = _webElement.getRect();
			Dimension dimension = _driver.getWrappedWebDriver().manage().window().getSize();
			return rectangle.getX() >= 0 &&
					rectangle.getY() >= 0 &&
					rectangle.getX() + rectangle.width <= dimension.width &&
					rectangle.getX() + rectangle.height <= dimension.height &&
					isDisplayed();
		} catch (Exception e) {
			end(e.getClass());
		}
		return false;
	}

	public Point getLocation() throws Throwable {
		Point value;
		try {
			value = _webElement.getLocation();
		} catch (Exception ignored) {
			doSelfCheck();
			value = _webElement.getLocation();
		}
		return value;
	}

	public Dimension getSize() throws Throwable {
		doSelfCheck();
		return _webElement.getSize();
	}

	public Rectangle getRect() throws Throwable {
		Rectangle value;
		try {
			value = _webElement.getRect();
		} catch (Exception ignored) {
			doSelfCheck();
			value = _webElement.getRect();
		}
		return value;
	}

	public String getCssValue(String s) throws Throwable {
		String value;
		try {
			value = _webElement.getCssValue(s);
		} catch (Exception ignored) {
			doSelfCheck();
			value = _webElement.getCssValue(s);
		}
		return value;
	}

	public Coordinates getCoordinates() throws Throwable {
		Coordinates value;
		try {
			value = ((Locatable) _driver.getWrappedWebDriver()).getCoordinates();
		} catch (Exception ignored) {
			doSelfCheck();
			value = ((Locatable) _driver.getWrappedWebDriver()).getCoordinates();
		}
		return value;
	}
}
