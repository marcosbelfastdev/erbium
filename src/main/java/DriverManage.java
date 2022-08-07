import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.Logs;

import java.util.Set;


public class DriverManage {
	
	Driver _driver;
	
	public DriverManage(Driver driver) {
		_driver = driver;
	}
	
	public void setAutoHandleAlerts(boolean value) {
		_driver.setOption(Common.HANDLE_ALERTS, true);
	}
	
	public void setAutoHandleAlertsResponse(AlertOption option) {
		_driver.setOption(Common.ALERTS_ACTION, option);
	}
	
	public void requireElementsVisible(boolean value) {
		_driver.setOption(Common.REQUIRE_VISIBLE, value);
	}
	
	public void requireElementsEnabled(boolean value) {
		_driver.setOption(Common.REQUIRE_ENABLED, value);
	}
	
	public void scrollToElements(boolean value) {
		_driver.setOption(Common.SCROLL_TO_ELEMENTS, value);
	}
	
	public void highlightElements(boolean value) {
		_driver.setOption(Common.HIGHLIGHT_ELEMENTS, value);
	}
	
	public void setHighlightStyle(String style) {
		_driver.setOption(Common.HIGHLIGHT_STYLE, style);
	}
	
	public void setDefaultExecutorClicks(boolean value) {
		_driver.setOption(Common.EXECUTOR_CLICKS, value);
	}
	
	public void setDefaultExecutorClear(boolean value) {
		_driver.setOption(Common.EXECUTOR_CLEAR, value);
	}
	
	public void setDefaultExecutorSetText(boolean value) {
		_driver.setOption(Common.EXECUTOR_SETTEXT, value);
	}
	
	public void fallbackToExecutor(boolean value) {
		_driver.setOption(Common.FALLBACK_TO_EXECUTOR, value);
	}
	
	public void highlightFrames(boolean value) {
		_driver.setOption(Common.HIGHLIGHT_FRAMES, value);
	}
	
	public void setSearchScrollFactor(double factor) {
		_driver.setOption(Common.SEARCHSCROLL_FACTOR, factor);
	}
	
	public void setSearchScrollHeight(int height) {
		_driver.setOption(Common.SEARCHSCROLL_HEIGHT, height);
	}
	
	public void loadOnDemand(boolean value) {
		_driver.setOption(Common.LOAD_ON_DEMAND, value);
	}

	public void addCookie(Cookie cookie) {
		_driver.getWrappedWebDriver().manage().addCookie(cookie);
	}
	
	public void deleteCookieNamed(String name) {
		_driver.manage().deleteCookieNamed(name);
	}

	public void deleteCookie(Cookie cookie) {
		_driver.getWrappedWebDriver().manage().deleteCookie(cookie);
	}
	
	
	public void deleteAllCookies() {
		_driver.getWrappedWebDriver().manage().deleteAllCookies();
	}
	
	
	public Set<Cookie> getCookies() {
		return _driver.getWrappedWebDriver().manage().getCookies();
	}
	
	
	public Cookie getCookieNamed(String name) {
		return _driver.getWrappedWebDriver().manage().getCookieNamed(name);
	}
	
	
	public DriverTimeouts timeouts() {
		return new DriverTimeouts(_driver);
	}

	
	public WebDriver.ImeHandler ime() {
		return _driver.getWrappedWebDriver().manage().ime();
	}
	
	
	public DriverWindow window() {
		return new DriverWindow(_driver);
	}
	
	
	public Logs logs() {
		return _driver.getWrappedWebDriver().manage().logs();
	}
}
