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

	void entry() throws Throwable {
		var sw = new StopWatch();
		reload();
		doAutoScrolling();
		doHighlighting();
		doDelayBefore(sw.elapsedTime());
	}

	private void doSwitchWindow() throws Throwable {
		if (!isNull(_window)) {
			try {
				_driver.getWrappedWebDriver().switchTo().window(_window);
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

	protected void doDelayAfter() {
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
			try {
				waitVisible.until(ExpectedConditions.visibilityOf(_webElement));
			} catch (Exception e) {
				end(DisplayedStatusError.class);
			}
		}
		if (isNull(_webElement))
			end(DisplayedStatusError.class);
	}


	protected void doEnabledCheck() throws Throwable {
		if (requiresElementEnabled() && isDisabled()) {
			WebDriverWait waitEnabled = new WebDriverWait(_driver.getWrappedWebDriver(),
										(long) getElementEnabledTimeout() / 1000, getRetryInterval());
			try {
				_webElement = waitEnabled.until(ExpectedConditions.elementToBeClickable(_webElement));
			} catch (Exception e) {
				end(EnabledStatusError.class);
			}
		}
		if (isNull(_webElement))
			end(EnabledStatusError.class);
	}


	protected void exit() {
		doDelayAfter();
		//switchToNewWindowOpened();
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
		boolean supress = (boolean) getOption(Common.SUPPRESS_DELAYS);
		entry();
		jsHide();
		setOption(Common.SUPPRESS_DELAYS, supress);
		exit();
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
        target.exit();
	    exit();
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

	public Element click() throws Throwable {

		reload();


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
			jsSetValue("");
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


	void disableScreenshots() throws Throwable {
		setOption(Common.ENABLE_SCREENSHOTS, false);
	}

	public Element setText(String text) throws Throwable {
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

		if (!isExecutorEnabled())
			return this;

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
		this.doSelfCheck();
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


	private WebElement getLastElementHighlighted() {
		return _lastElementHighlighted;
	}

	private void setLastElementHighlighted(WebElement element) {
		_lastElementHighlighted = element;
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

		if (requiresElementVisible() && !isDisplayed()) {
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
//		if (!isNull(_window)) {
//			try {
//				_driver.getWrappedWebDriver().switchTo().window(_window);
//				return;
//			} catch (Exception e) {
//				end(e.getClass());
//			}
//		}
	}

	public boolean shouldForceFullReload() {
		return (boolean) getOption(Common.FORCE_FULL_RELOAD);
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
		try {
			load();
		} catch (Exception ignored) {

		}
		return _webElement.isSelected();
	}

	public boolean isDisabled() throws Throwable {
		try {
			load();
		} catch (Exception ignored) {

		}
		return !_webElement.isEnabled();
	}

	public String getText() throws Throwable {
		try {
			load();
		} catch (Exception ignored) {

		}
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


	public boolean isDisplayed() throws Throwable {
		try {
			load();
		} catch (Exception ignored) {

		}
		boolean value;
		try {
			value = _webElement.isDisplayed() && _webElement.getRect().height > 0;
		} catch (Exception e) {
			return false;
		}
		return value;
	}

	public boolean isFullyDisplayed() throws Throwable {
		try {
			load();
		} catch (Exception ignored) {

		}
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
		try {
			load();
		} catch (Exception ignored) {

		}
		Point value;
		try {
			value = _webElement.getLocation();
		} catch (Exception ignored) {
			return null;
		}
		return value;
	}

	public Dimension getSize() throws Throwable {
		try {
			load();
		} catch (Exception ignored) {

		}
		Dimension value;
		try {
			value = _webElement.getSize();
		} catch (Exception ignored) {
			return null;
		}
		return value;
	}

	public Rectangle getRect() throws Throwable {
		try {
			load();
		} catch (Exception ignored) {

		}
		Rectangle value;
		try {
			value = _webElement.getRect();
		} catch (Exception ignored) {
			return null;
		}
		return value;
	}

	public String getCssValue(String s) throws Throwable {
		try {
			load();
		} catch (Exception ignored) {

		}
		String value;
		try {
			value = _webElement.getCssValue(s);
		} catch (Exception ignored) {
			return null;
		}
		return value;
	}

	public Coordinates getCoordinates() throws Throwable {
		try {
			load();
		} catch (Exception ignored) {

		}
		Coordinates value;
		try {
			value = ((Locatable) _driver.getWrappedWebDriver()).getCoordinates();
		} catch (Exception ignored) {
			return null;
		}
		return value;
	}
}
