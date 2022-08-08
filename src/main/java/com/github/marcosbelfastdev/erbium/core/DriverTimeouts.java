package com.github.marcosbelfastdev.erbium.core;

import org.openqa.selenium.WebDriver;

import java.util.concurrent.TimeUnit;


public class DriverTimeouts {
	
	Driver _driver;
	
	public DriverTimeouts(Driver driver) {
		_driver = driver;
	}
	
	public WebDriver.Timeouts setScriptTimeout(long time, TimeUnit unit) {
		return _driver.getWrappedWebDriver().manage().timeouts().setScriptTimeout(time, unit);
	}
	
	public void setPageLoadTimeout(int time) {
		_driver.setOption(Common.PAGE_LOAD_TIMEOUT, time);
	}
	
	public void setRetryInterval(int time) {
		_driver.setOption(Common.RETRY_INTERVAL, time);
	}
	
	public void setResolveTimeout(int time) {
		_driver.setOption(Common.RESOLVE_TIMEOUT, time);
	}
	
	public void setElementVisibleTimeout(int time) {
		_driver.setOption(Common.VISIBLE_TIMEOUT, time);
	}
	
	public void setElementEnabledTimeout(int time) {
		_driver.setOption(Common.ENABLED_TIMEOUT, time);
	}
	
	public void setSearchScrollTimeout(int time) {
		_driver.setOption(Common.SEARCHSCROLL_TIMEOUT, time);
	}
}
