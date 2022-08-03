package com.github.marcosbelfastdev.erbium;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.Logs;

import java.util.Set;

public class EDriverManage {
	
	EDriver $driver;
	
	public EDriverManage(EDriver driver) {
		$driver = driver;
	}

	public void reportToTestng(boolean value) {
		$driver.getReportingOptions().setOption(Reporting.REPORT_TO_TESTNG, true);
	}
	
	public void setAutoHandleAlerts(boolean value) {
		$driver.getPlaybackOptions().setOption(Common.HANDLE_ALERTS, true);
	}
	
	public void setAutoHandleAlertsResponse(AlertOptions option) {
		$driver.getPlaybackOptions().setOption(Common.ALERTS_ACTION, option);
	}
	
	public void requireElementsVisible(boolean value) {
		$driver.getPlaybackOptions().setOption(Common.REQUIRE_VISIBLE, value);
	}
	
	public void requireElementsEnabled(boolean value) {
		$driver.getPlaybackOptions().setOption(Common.REQUIRE_ENABLED, value);
	}
	
	public void scrollToElements(boolean value) {
		$driver.getPlaybackOptions().setOption(Common.SCROLL_TO_ELEMENTS, value);
	}
	
	public void highlightElements(boolean value) {
		$driver.getPlaybackOptions().setOption(Common.HIGHLIGHT_ELEMENTS, value);
	}
	
	public void setHighlightStyle(String style) {
		$driver.getPlaybackOptions().setOption(Common.HIGHLIGHT_STYLE, style);
	}
	
	public void setDefaultExecutorClicks(boolean value) {
		$driver.getPlaybackOptions().setOption(Common.EXECUTOR_CLICKS, value);
	}
	
	public void setDefaultExecutorClear(boolean value) {
		$driver.getPlaybackOptions().setOption(Common.EXECUTOR_CLEAR, value);
	}
	
	public void setDefaultExecutorSetText(boolean value) {
		$driver.getPlaybackOptions().setOption(Common.EXECUTOR_SETTEXT, value);
	}
	
	public void fallbackToExecutor(boolean value) {
		$driver.getPlaybackOptions().setOption(Common.FALLBACK_TO_EXECUTOR, value);
	}
	
	public void highlightFrames(boolean value) {
		$driver.getPlaybackOptions().setOption(Common.HIGHLIGHT_FRAMES, value);
	}
	
	public void setSearchScrollFactor(double factor) {
		$driver.getPlaybackOptions().setOption(Common.SEARCHSCROLL_FACTOR, factor);
	}
	
	public void setSearchScrollHeight(int height) {
		$driver.getPlaybackOptions().setOption(Common.SEARCHSCROLL_HEIGHT, height);
	}
	
	public void loadOnDemand(boolean value) {
		$driver.getAdvancedOptions().setOption(AdvancedOptions.LOAD_ON_DEMAND, value);
	}
	
	/*public void enableExecutor(boolean value) {
		$playbackOptions.get($sid).setOption(Common.ENABLE_EXECUTOR, value);
	}*/
	
	public void enableTimers(boolean value) {
		$driver.getPlaybackOptions().setOption(Common.ENABLE_TIMERS, value);
	}
	
	public void addCookie(Cookie cookie) {
		$driver.getWebDriver().manage().addCookie(cookie);
	}
	
	public void deleteCookieNamed(String name) {
		$driver.getWebDriver().manage().deleteCookieNamed(name);
	}
	
	
	public void deleteCookie(Cookie cookie) {
		$driver.getWebDriver().manage().deleteCookie(cookie);
	}
	
	
	public void deleteAllCookies() {
		$driver.getWebDriver().manage().deleteAllCookies();
	}
	
	
	public Set<Cookie> getCookies() {
		return $driver.getWebDriver().manage().getCookies();
	}
	
	
	public Cookie getCookieNamed(String name) {
		return $driver.getWebDriver().manage().getCookieNamed(name);
	}
	
	
	public EDriverTimeouts timeouts() {
		return new EDriverTimeouts($driver);
	}
	
	public EDriverDelays delays() {
		return new EDriverDelays($driver);
	}
	
	
	public WebDriver.ImeHandler ime() {
		return $driver.getWebDriver().manage().ime();
	}
	
	
	public EDriverWindow window() {
		return new EDriverWindow($driver);
	}
	
	
	public Logs logs() {
		return $driver.getWebDriver().manage().logs();
	}
}
