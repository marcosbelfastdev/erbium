import org.openqa.selenium.WebDriver;

import java.util.concurrent.TimeUnit;

import static org.base.erbium.EDriver.*;

public class DriverTimeouts {
	
	EDriver _driver;
	
	public DriverTimeouts(EDriver driver) {
		_driver = driver;
	}
	
	public WebDriver.Timeouts setScriptTimeout(long time, TimeUnit unit) {
		return _driver.getWebDriver().manage().timeouts().setScriptTimeout(time, unit);
	}
	
	public void setPageLoadTimeout(int time) {
		_driver.getAdvancedOptions().setOption(AdvancedOptions.PAGE_LOAD_TIMEOUT, time);
	}
	
	public void setRetryInterval(int time) {
		_driver.getPlaybackOptions().setOption(Common.RETRY_INTERVAL, time);
	}
	
	public void setResolveTimeout(int time) {
		_driver.getPlaybackOptions().setOption(Common.RESOLVE_TIMEOUT, time);
	}
	
	public void setElementVisibleTimeout(int time) {
		_driver.getPlaybackOptions().setOption(Common.VISIBLE_TIMEOUT, time);
	}
	
	public void setElementEnabledTimeout(int time) {
		_driver.getPlaybackOptions().setOption(Common.ENABLED_TIMEOUT, time);
	}
	
	public void setSearchScrollTimeout(int time) {
		_driver.getPlaybackOptions().setOption(Common.SEARCHSCROLL_TIMEOUT, time);
	}
}
