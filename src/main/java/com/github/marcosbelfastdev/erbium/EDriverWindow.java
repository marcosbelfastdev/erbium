package com.github.marcosbelfastdev.erbium;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;

public class EDriverWindow {
	
	EDriver $driver;
	
	public EDriverWindow(EDriver driver) {
		$driver = driver;
	}
	
	public void setAutoWindowLocking(boolean value) {
		$driver.getPlaybackOptions().setOption(Common.WINDOW_LOCKING, value);
	}
	
	public void setAutoWindowSearch(boolean value) {
		$driver.getPlaybackOptions().setOption(Common.WINDOW_SEARCH, true);
	}
	
	public void setSize(Dimension targetSize) {
		$driver.getWebDriver().manage().window().setSize(targetSize);
	}
	
	
	public void setPosition(Point targetPosition) {
		$driver.getWebDriver().manage().window().setPosition(targetPosition);
	}
	
	
	public Dimension getSize() {
		return $driver.getWebDriver().manage().window().getSize();
	}
	
	
	public Point getPosition() {
		return $driver.getWebDriver().manage().window().getPosition();
	}
	
	
	public void maximize() {
		$driver.getWebDriver().manage().window().maximize();
	}
	
	public void fullscreen() {
		$driver.getWebDriver().manage().window().fullscreen();
	}
	
}