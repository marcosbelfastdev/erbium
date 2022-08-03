package com.github.marcosbelfastdev.erbium;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;


class ConditionsEvaluator {

	private final EDriver $driver;
	private final Condition $condition;
	
	ConditionsEvaluator(EDriver driver, Condition condition) {
		$driver = driver;
		$condition = condition;
	}
	
	boolean evalCondition() {
		
		boolean result = false;
		
		switch ($condition.getConditionOption()) {
			case NoneOfElementsPresent:
				result = evalConditionNoneOfElementsPresent();
				break;
			case AllElementsPresent:
				result = evalConditionAllElementsPresent();
				break;
			case AnyElementPresent:
				result = evalConditionAnyElementPresent();
				break;
		}
		
		return result;
	}
	
	private boolean evalConditionNoneOfElementsPresent() {
		
		boolean result = true;
		WebElement webElement = null;
		for(By locator : $condition.getLocators()) {
			try {
				webElement =  $driver.getWebDriver().findElement(locator);
			} catch (NoSuchElementException ignored) { }
			/* if one of the elements are indeed present
			evaluation is false.
			 */
			if(webElement != null) {
				result = false;
				break;
			}
		}
		
		return result;
	}
	
	private boolean evalConditionAllElementsPresent() {
		
		WebElement webElement = null;
		for(By locator : $condition.getLocators()) {
			
			try {
				webElement = $driver.getWebDriver().findElement(locator);
			} catch (NoSuchElementException nsee) {
				return false;
			}
			
		}
		
		return true;
	}
	
	private boolean evalConditionAnyElementPresent() {
		boolean result = false;
		WebElement webElement = null;
		for(By locator : $condition.getLocators()) {
			try {
				//put("AnyOfElementsPresent: searching... " + locator.toString());
				webElement = $driver.getWebDriver().findElement(locator);
			} catch (NoSuchElementException ignored) {}
			/* if one of the elements are indeed present
			evaluation is false.
			 */
			if(webElement != null) {
				result = true;
				break;
			}
		}
		return result;
	}
}
