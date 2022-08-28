package com.github.marcosbelfastdev.erbium.core;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Set;

public interface IDriver {
    public Driver get(String url);
    public String getCurrentUrl();
    public String getTitle();
    public List<Element> findElements(By by);
    public List<Element> syncedFindElements(By by, int minElements) throws Throwable;
    public Element findFirstElement(By... by) throws Throwable;
    public String getPageSource() throws Throwable;
    public Driver close();
    public void quit();
    public Set<String> getWindowHandles();
    public String getWindowHandle();
    public WebDriver.Navigation navigate();
    public DriverManage manage();

    public List<Driver> syncedFind(By by, int minElements);
    public List<Driver> syncedFind(By by);

}
