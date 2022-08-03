package org.base.erbium;

import org.openqa.selenium.WebDriver;

import java.util.concurrent.TimeUnit;

import static org.base.erbium.EDriver.*;

public class EDriverTimeouts {
	
	EDriver $driver;
	
	public EDriverTimeouts(EDriver driver) {
		$driver = driver;
	}
	
	public WebDriver.Timeouts setScriptTimeout(long time, TimeUnit unit) {
		return $driver.getWebDriver().manage().timeouts().setScriptTimeout(time, unit);
	}
	
	public void setPageLoadTimeout(int time) {
		$driver.getAdvancedOptions().setOption(AdvancedOptions.PAGE_LOAD_TIMEOUT, time);
	}
	
	public void setRetryInterval(int time) {
		$driver.getPlaybackOptions().setOption(Common.RETRY_INTERVAL, time);
	}
	
	public void setResolveTimeout(int time) {
		$driver.getPlaybackOptions().setOption(Common.RESOLVE_TIMEOUT, time);
	}
	
	public void setElementVisibleTimeout(int time) {
		$driver.getPlaybackOptions().setOption(Common.VISIBLE_TIMEOUT, time);
	}
	
	public void setElementEnabledTimeout(int time) {
		$driver.getPlaybackOptions().setOption(Common.ENABLED_TIMEOUT, time);
	}
	
	public void setSearchScrollTimeout(int time) {
		$driver.getPlaybackOptions().setOption(Common.SEARCHSCROLL_TIMEOUT, time);
	}
}
