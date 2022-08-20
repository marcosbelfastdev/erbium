package com.github.marcosbelfastdev.erbium.core;

import com.github.marcosbelfastdev.erbium.exceptions.PageSourceError;
import org.openqa.selenium.*;
import java.util.List;
import java.util.Set;

import static com.github.marcosbelfastdev.erbium.core.ErrorHandling.end;
import static java.util.Objects.isNull;

public class Driver extends DriverOptions implements IDriver, IDriverScreenshot {

    public Driver(WebDriver driver) {
        super(driver);
    }

    public Driver executeScript(String script, Object... args) {
        ((JavascriptExecutor)_driver).executeScript(script, args);
        return this;
    }

    @Override
    public Driver get(String url) {
        _driver.get(url);
        return this;
    }

    @Override
    public String getCurrentUrl() {
        return _driver.getCurrentUrl();
    }

    @Override
    public String getTitle() {
        return _driver.getTitle();
    }

    @Override
    public List<Driver> findElements(By by) {
        return null;
    }

    @Override
    public Driver findElement(By by) {
        return null;
    }

    @Override
    public String getPageSource() {
        String source = null;
        try {
            source = _driver.getPageSource();
            var timer = new Timer(resolve());
            while (isNull(source) && !timer.timedOut()) {
                source = _driver.getPageSource();
                Timer.sleep(retryInterval());
            }
            if (isNull(source))
                end(PageSourceError.class);
        } catch (Exception ignored) {

        }
        return source;
    }

    @Override
    public Driver close() {
        _driver.close();
        return this;
    }

    @Override
    public void quit() {
        _driver.quit();
    }

    @Override
    public Set<String> getWindowHandles() {
        return _driver.getWindowHandles();
    }

    @Override
    public String getWindowHandle() {
        return _driver.getWindowHandle();
    }

    @Override
    public WebDriver.Navigation navigate() {
        return _driver.navigate();
    }

    @Override
    public DriverManage manage() {
        return new DriverManage(this);
    }

    @Override
    public List<Driver> syncedFind(By by, int minElements) {
        return null;
    }

    @Override
    public List<Driver> syncedFind(By by) {
        return null;
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target) {
        return ((TakesScreenshot) _driver).getScreenshotAs(target);
    }
}
