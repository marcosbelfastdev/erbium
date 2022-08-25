package com.github.marcosbelfastdev.erbium.core;

import com.github.marcosbelfastdev.erbium.exceptions.PageSourceError;
import com.github.marcosbelfastdev.erbium.exceptions.SyncedFindElementsError;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

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

    public Element findElement(By by) {
        // Waiting 30 seconds for an element to be present on the page, checking
        // for its presence once every 5 seconds.


    }

    @Override
    public List<Element> findElements(By by) {
        List<Element> elements = new ArrayList<>();
        List<WebElement> webElements = _driver.findElements(by);
        for (WebElement webElement : webElements) {
            elements.add(new Element(by));
        }
        return elements;
    }

    @Override
    public List<Element> syncedFindElements(By by, int minElements) {
        Timer timer = new Timer(resolve());
        List<Element> elements = new ArrayList<>();
        while (elements.size() < minElements && !timer.timedOut()) {
            elements = findElements(by);
        }
        if (elements.size() < minElements) {
            end(SyncedFindElementsError.class);
        }
        return elements;

    }

    /**
     * Finds the first possible element out of a list of locators
     * @param bys
     * @return
     */
    @Override
    public Element findFirstElement(By... bys) {
        Timer timer = new Timer(resolve());
        List<WebElement> webElements = new ArrayList<>();
        outter: while (!timer.timedOut()) {
            for (By by : bys) {
                webElements = _driver.findElements(by);
                if (webElements.size() > 0) {
                    break outter;
                }
                Timer.sleep(retryInterval());
            }
        }

        if (webElements.size() < 1) {
            end(SyncedFindElementsError.class);
        }
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
            end(PageSourceError.class);
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
