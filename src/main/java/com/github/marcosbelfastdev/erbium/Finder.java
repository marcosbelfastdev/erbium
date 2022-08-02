package com.github.marcosbelfastdev.erbium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Finder {
	
	PlaybackOptionsSet $findOptionsSet;
	
	Finder setFinderOptions(PlaybackOptionsSet options) {
		$findOptionsSet = options;
		return this;
	}
	
	WebElement findElement(EDriver driver, By locator) throws Exception {
		if(locator==null)
			throw new Exception("Locator has not been passed in correctly by a previous step determining the locator.");
		int findTimeout = driver.getResolveTimeout();
		int retryInterval = driver.getRetryInterval();
		// process each of the passed in settings now
		// this may override some of inits above
		if($findOptionsSet!=null) {
			for(Common option : $findOptionsSet.keySet()) {
				switch(option) {
					case RESOLVE_TIMEOUT:
						findTimeout = (int) $findOptionsSet.getOption(option);
						break;
					case RETRY_INTERVAL:
						retryInterval = (int) $findOptionsSet.getOption(option);
						break;
				}
			}
		}
		WebElement webElement = new WebDriverWait(driver.getWebDriver(), findTimeout/1000, retryInterval)
				                        .until(ExpectedConditions.presenceOfElementLocated(locator));
		return webElement;
	}
	
}
