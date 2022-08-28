package com.github.marcosbelfastdev.erbium.core;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;

public class DriverWindow {
	
	Driver _driver;
	
	public DriverWindow(Driver driver) {
		_driver = driver;
	}
	
	public void setAutoWindowLocking(boolean value) throws Throwable {
		_driver.setOption(Common.WINDOW_LOCKING, value);
	}
	
	public void setAutoWindowSearch(boolean value) throws Throwable {
		_driver.setOption(Common.WINDOW_SEARCH, true);
	}
	
	public void setSize(Dimension targetSize) throws Throwable {
		_driver.setOption(Common.SCREEN_SIZE, targetSize);
		_driver.getWrappedWebDriver().manage().window().setSize(targetSize);
	}

	public void setPosition(Point targetPosition) throws Throwable {
		_driver.setOption(Common.SCREEN_POSITION, targetPosition);
		_driver.getWrappedWebDriver().manage().window().setPosition(targetPosition);
	}

	public Dimension getSize() {
		return _driver.getWrappedWebDriver().manage().window().getSize();
	}

	public Point getPosition() {
		return _driver.getWrappedWebDriver().manage().window().getPosition();
	}

//	public void maximize() {
//		_driver.getWrappedWebDriver().manage().window().maximize();
//		_driver.setOption(Common.SCREEN_SIZE, _driver.getWrappedWebDriver().manage().window().getSize());
//	}
//
//	public void fullscreen() {
//		_driver.getWrappedWebDriver().manage().window().fullscreen();
//		_driver.setOption(Common.FULLSCREEN, true);
//	}
	
}