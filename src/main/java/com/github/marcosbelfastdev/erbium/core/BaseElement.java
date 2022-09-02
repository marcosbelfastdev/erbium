package com.github.marcosbelfastdev.erbium.core;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static java.util.Objects.isNull;

public class BaseElement {

    /*
    Driver that originated the element
     */
    protected Driver _driver;

    /**
     * Base element locator
     */
    protected By _locator;

    /**
     * Wrapped WebElement
     */
    protected WebElement _webElement;

    /**
     * A record of the last element highlighted.
     * It is used in order to unhighlight an element highlighted before.
     */
    protected WebElement _lastElementHighlighted;

    /**
     * Window that is associated to an element for automatic switching
     * when the element is called.
     * A window is only associated if LOCK_TO_WINDOW is true
     * and after the element has been found once.
     */
    protected String _window;

    /**
     * Name of the element
     */
    protected String _name;

    protected Boolean switchToNewWindow;

    public BaseElement(Driver driver, By by) {
        setLocator(by);
        setDriver(driver);
    }

    public BaseElement(Driver driver, WebElement webElement) {
        setDriver(driver);
        setWrappedWebElement(webElement);
    }

    protected void setWrappedWebElement(WebElement webElement) {
        _webElement = webElement;
    }

    protected void setLocator(By by) {
        _locator = by;
    }

    protected void setDriver(Driver driver) {
        this._driver = driver;
    }


    public void setElementName(String name) {
        _name = name;
    }

    public WebElement getWrappedWebElement() {
        return _webElement;
    }

    public By getBy() {
        return _locator;
    }

}
