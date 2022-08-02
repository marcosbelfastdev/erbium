package com.github.marcosbelfastdev.erbium;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

public class ESelect {

    EElement $element;
    Select $select;

    public ESelect(EElement element) throws Exception {
        $element = element;
        reload();
    }

    void reload() throws Exception {
        $element.reload();
        $select = new Select($element.getWebElement());
    }

    public void invert() throws Exception {
        $element.startPoint();
        if (!$select.isMultiple())
            return;

        for (WebElement webElement : $select.getOptions()) {
            webElement.click();
        }
        $element.exitPoint();
    }


    public boolean isMultiple() throws Exception {
        boolean result;
        try {
            result = $select.isMultiple();
        } catch (StaleElementReferenceException sere) {
            reload();
            result = $select.isMultiple();
        }
        return result;
    }


    public List<EElement> getAllSelectedOptions() throws Exception {
        List<WebElement> webElements;
        try {
            webElements = $select.getAllSelectedOptions();
        } catch (StaleElementReferenceException sere) {
            reload();
            webElements = $select.getAllSelectedOptions();
        }
        List<EElement> elements = new ArrayList<>();
        for(WebElement webElement : webElements) {
            var element = new EElement($element.getDriver(), webElement);
            elements.add(element);
        }
        return elements;
    }


    public EElement getFirstSelectedOption() throws Exception {
        WebElement webElement;
        try {
            webElement = $select.getFirstSelectedOption();
        } catch (StaleElementReferenceException sere) {
            reload();
            webElement = $select.getFirstSelectedOption();
        }
        var element = new EElement($element.getDriver(), webElement);
        return element;
    }


    public void selectByVisibleText(String text) throws Exception {
        try {
            $select.selectByVisibleText(text);
        } catch (StaleElementReferenceException sere) {
            reload();
            $select.selectByVisibleText(text);
        }
    }


    public void selectByIndex(int index) throws Exception {
        try {
            $select.selectByIndex(index);
        } catch (StaleElementReferenceException sere) {
            reload();
            $select.selectByIndex(index);
        }
    }


    public void selectByValue(String value) throws Exception {
        try {
            $select.selectByValue(value);
        } catch (StaleElementReferenceException sere) {
            reload();
            $select.selectByValue(value);
        }
    }

    public void deselectAll() throws Exception {
        try {
            $select.deselectAll();
        } catch (StaleElementReferenceException sere) {
            reload();
            $select.deselectAll();
        }
    }

    public void deselectByValue(String value) throws Exception {
        try {
            $select.deselectByValue(value);
        } catch (StaleElementReferenceException sere) {
            reload();
            $select.deselectByValue(value);
        }
    }


    public void deselectByIndex(int index) throws Exception {
        try {
            $select.deselectByIndex(index);
        } catch (StaleElementReferenceException sere) {
            reload();
            $select.deselectByIndex(index);
        }
    }

    public void deselectByVisibleText(String text) throws Exception {
        try {
            $select.deselectByVisibleText(text);
        } catch (StaleElementReferenceException sere) {
            reload();
            $select.deselectByVisibleText(text);
        }
    }
}
