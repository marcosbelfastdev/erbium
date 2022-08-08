package com.github.marcosbelfastdev.erbium.core;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.util.List;
import java.util.Set;

public interface IDriverScreenshot {
    public <X> X getScreenshotAs(OutputType<X> target);
}
