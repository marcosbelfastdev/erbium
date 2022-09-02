package com.github.marcosbelfastdev.erbium.core;

import com.github.marcosbelfastdev.erbium.exceptions.*;
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
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.github.marcosbelfastdev.erbium.core.ErrorHandling.alert;
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

	private WebElement getLastElementHighlighted() {
		return _lastElementHighlighted;
	}

	private void setLastElementHighlighted(WebElement element) {
		_lastElementHighlighted = element;
	}

	private void entry() throws Throwable {
		var sw = new StopWatch();
		reload();
		doAutoScrolling();
		doHighlighting();
		doDelayBefore(sw.elapsedTime());
	}

	private void doDelayBefore(long elapsed) {
		// deduct any previous delay from desired delay to interact
		if (allowDelays())
			sleep(getDelayToInteractBefore() - elapsed);
	}

	private void doDelayAfter() {
		if (allowDelays())
			Timer.sleep((long)getOption(Common.INTERACT_DELAY_AFTER));
	}

	private long getDelayToInteractBefore() {
		return (long) getOption(Common.INTERACT_DELAY_BEFORE);
	}

	private void doSwitchWindow() throws Throwable {
		if (isNull(_window))
			return;
		if (!_window.equals(_driver.getWindowHandle())) {
			try {
				_driver.getWrappedWebDriver().switchTo().window(_window);
			} catch (Exception e) {
				end(e.getClass());
			}
		}
	}

	public Element prepareToSwitchToNewWindow() {
		switchToNewWindow = true;
		return this;
	}

	private void switchToNewWindowOpened() {
		if (isNull(switchToNewWindow)) {
			return;
		}
		switchToNewWindow = null;
		boolean switchNext = false;
		try {
			Set<String> windows = _driver.getWindowHandles();
			for (String window : windows) {
				if (!window.equals(_window)) {
					switchNext = true;
				}
				if (switchNext)
					_driver.getWrappedWebDriver().switchTo().window(window);
			}
		} catch (Exception ignored) {
			alert(CannotSwitchToNewWindow.class);
		}

		if(!switchNext) {
			alert(CannotSwitchToNewWindow.class);
		}
	}

	private boolean shouldForceFullReload() {
		return (boolean) getOption(Common.FORCE_FULL_RELOAD);
	}

	private void jsHide() {
		_driver.executeScript("arguments[0].style.display = \"none\";", _webElement);
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

	public String getTagName() throws Throwable {
		reload();
		try {
			return _webElement.getTagName();
		} catch (Exception e) {
			return null;
		}
	}

	public String getAttribute(String s) throws Throwable {
		reload();
		try {
			return _webElement.getAttribute(s);
		} catch (Exception e) {
			return null;
		}
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

	protected void doAutoScrolling() throws Throwable {
		if (isVisible()) {
			if (shouldScroll()) {
				try {
					jsScroll();
				} catch (Exception e) {
					alert(CannotScrollAlert.class);
				}
			}
		}
	}

	protected void doHighlighting() throws Throwable {
		if (isVisible())
			if (shouldHighlight())
				highlight();
	}

	public String getHomeWindow() {
		return _window;
	}

	protected void exit() {
		doDelayAfter();
		switchToNewWindowOpened();
	}

	public void closeAlienWindows() {

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
		String currentWindow = _driver.getWindowHandle();
		if (_window.equals(currentWindow)) {
			alert(CannotCloseActiveWindow.class);
		}
		try {
			_driver.getWrappedWebDriver().switchTo().window(_window);
			_driver.getWrappedWebDriver().close();
			_driver.frameworkSettingsProtection();
		} catch (Exception ignore) {

		}
		try {
			_driver.getWrappedWebDriver().switchTo().window(currentWindow);
		} catch (Exception e) {
			alert(CannotSwitchBackToWindow.class);
		}
	}


	public void hide() throws Throwable {
		if (!isExecutorEnabled())
			alert(CannotUseJavaScript.class);
		entry();
		try {
			jsHide();
		} catch (Exception e) {

		}
		exit();
	}

	public Element click() throws Throwable {

		entry();

		if (requiresExecutorClick()) {
			jsClick();
			exit();
			return this;
		}


		try {
			_webElement.click();
		} catch (Exception e) {
			if (shouldFallbackToExecutor()) {
				jsClick();
			}
		}

		exit();
		return this;
	}


	public Element setPassword(String text) throws Throwable {
		entry();

		_driver.executeScript("arguments[0].type=\"password\"", _webElement);

		if (requireExecutorSetText()) {
			jsSetValue(text);
			exit();
			return this;
		}

		try {
			_webElement.sendKeys(text);
		} catch (Exception e) {
			if (shouldFallbackToExecutor()) {
				jsSetValue(text);
			}
		}

		exit();
		return this;
	}

	public Element clear() throws Throwable {
		entry();

		if (requireExecutorClear()) {
			jsClear();
			exit();
			return this;
		}

		try {
			_webElement.clear();
		} catch (Exception e) {
			if (shouldFallbackToExecutor()) {
				jsClear();
			}
		}

		exit();
		return this;
	}


	public void disableScreenshots() throws Throwable {
		setOption(Common.ENABLE_SCREENSHOTS, false);
	}

	public Element setText(String text) throws Throwable {
		entry();
		if (requireExecutorSetText()) {
			try {
				jsSetValue(text);
			} catch (Exception e) {
				end(SetTextError.class);
			}
			exit();
			return this;
		}

		try {
			_webElement.sendKeys(text);
		} catch (Exception e) {
			alert(SetTextError.class);
			if (shouldFallbackToExecutor()) {
				try {
					jsSetValue(text);
				} catch (Exception e2) {
					end(SetTextError.class);
				}
			}
		}

		exit();
		return this;
	}

	protected Element centerScroll() throws Throwable {
		load();
		jsScroll();
		highlight();
		if (allowDelays())
			StopWatch.sleep((long)getOption(Common.SCROLL_DELAY_AFTER));
		return this;
	}

	private void jsScroll() throws Throwable {
		_driver.executeScript("arguments[0].scrollIntoView({behavior: \"smooth\", block: \"center\"});", _webElement);
	}

	public Element scrollDownTo() throws Throwable {

		double searchScrollFactor =  (double) getOption(Common.SEARCHSCROLL_FACTOR);
		int searchScrollDelay = (int) getOption(Common.SEARCHSCROLL_RESOLVE);
		int signal = 1;
		int offset =  (int) ((double) _driver.getWrappedWebDriver().manage().window().getSize().height * searchScrollFactor);

		Timer timer = new Timer(getSearchScrollTimeout());
		WebElement webElement = null;
		while (timer.hasTimeLeft() && isNull(_webElement)) {
			for (int i = 0; i <= offset; i = i + 7) {
				_driver.executeScript("window.scrollBy(0, " + 7 * signal + ");");
			}
			try {
				webElement = new WebDriverWait(_driver.getWrappedWebDriver(), searchScrollDelay, getRetryInterval())
						.until(ExpectedConditions.presenceOfElementLocated(_locator));
				_webElement = webElement;
			} catch (Exception ignored) {

			}
		}

		if (!isNull(_webElement))
			jsScroll();

		return this;
	}

	/**
	 * Resets an element to its original state.
	 * @return
	 */
	@Override
	public Element reset() {
		setHomeWindow(null);
		setLocator(null);
		setWrappedWebElement(null);
		return this;
	}

	public Element highlight() throws Throwable {
		unhighlight();
		_driver.executeScript("arguments[0].setAttribute('style', arguments[1]);", _webElement, getHighLightStyle());
		var sw = new StopWatch();
		setLastElementHighlighted(_webElement);
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


	public void submit() throws Throwable {
		entry();
		_webElement.submit();
		exit();
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
		else
			doSwitchWindow();

		if (requiresElementVisible() && !isVisible()) {
			WebDriverWait waitVisible = new WebDriverWait(_driver.getWrappedWebDriver(),
					(long) getElementVisibleTimeout() / 1000, getRetryInterval());
			try {
				waitVisible.until(ExpectedConditions.visibilityOf(_webElement));
			} catch (Exception e) {
				end(DisplayedStatusError.class);
			}
		}

		if (requiresElementEnabled() && isDisabled()) {
			WebDriverWait waitEnabled = new WebDriverWait(_driver.getWrappedWebDriver(),
					(long) getElementEnabledTimeout() / 1000, getRetryInterval());
			try {
				_webElement = waitEnabled.until(ExpectedConditions.elementToBeClickable(_webElement));
			} catch (Exception e) {
				end(EnabledStatusError.class);
			}
		}

	}

	public Element reload() throws Throwable {
		return reload(false);
	}

	public Element reload(boolean force) throws Throwable {
		if (force || shouldForceFullReload() || isNull(_webElement)) {
			load();
			return this;
		}

		doSwitchWindow();

		try {
			_webElement = _driver.getWrappedWebDriver().findElements(_locator).get(0);
		} catch (Exception e) {
			end(NoElementFound.class);
		}
		if (requiresElementVisible() && !(_webElement.isDisplayed() && _webElement.getRect().height > 0))
			end(DisplayedStatusError.class);
		if (requiresElementEnabled() && !_webElement.isEnabled())
			end(EnabledStatusError.class);

		return this;
	}

	private void lockToWindow() throws Throwable {
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
		jsSetFocus();
		unhighlight();
		return this;
	}

	private void jsSetFocus() {
		try {
			_driver.executeScript("arguments[0].focus();", _webElement);
		} catch (Exception ignore) {

		}
	}

	public boolean isSelected() throws Throwable {
		reload();
		return _webElement.isSelected();
	}

	public boolean isDisabled() throws Throwable {
		reload();
		return !_webElement.isEnabled();
	}

	public String getText() throws Throwable {
		reload();
		String value = null;
		value = _webElement.getText();
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

	public boolean isVisible() throws Throwable {
		reload();
		boolean value;
		try {
			value = _webElement.isDisplayed() && _webElement.getRect().height > 0;
		} catch (Exception e) {
			return false;
		}
		return value;
	}

	public boolean isFullyDisplayed() throws Throwable {
		reload();
		try {
			Rectangle rectangle = _webElement.getRect();
			Dimension dimension = _driver.getWrappedWebDriver().manage().window().getSize();
			return rectangle.getX() >= 0 &&
					rectangle.getY() >= 0 &&
					rectangle.getX() + rectangle.width <= dimension.width &&
					rectangle.getX() + rectangle.height <= dimension.height &&
					_webElement.isDisplayed() && _webElement.getRect().height > 0;
		} catch (Exception e) {
			return false;
		}
	}

	public Point getLocation() throws Throwable {
		reload();
		Point value;
		try {
			value = _webElement.getLocation();
		} catch (Exception ignored) {
			return null;
		}
		return value;
	}

	public Dimension getSize() throws Throwable {
		reload();
		Dimension value;
		try {
			value = _webElement.getSize();
		} catch (Exception ignored) {
			return null;
		}
		return value;
	}

	public Rectangle getRect() throws Throwable {
		reload();
		Rectangle value;
		try {
			value = _webElement.getRect();
		} catch (Exception ignored) {
			return null;
		}
		return value;
	}

	public String getCssValue(String s) throws Throwable {
		reload();
		String value;
		try {
			value = _webElement.getCssValue(s);
		} catch (Exception ignored) {
			return null;
		}
		return value;
	}

	public Coordinates getCoordinates() throws Throwable {
		reload();
		Coordinates value;
		try {
			value = ((Locatable) _driver.getWrappedWebDriver()).getCoordinates();
		} catch (Exception ignored) {
			return null;
		}
		return value;
	}
}
