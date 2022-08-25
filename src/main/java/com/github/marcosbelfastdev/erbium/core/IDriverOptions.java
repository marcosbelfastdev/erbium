package com.github.marcosbelfastdev.erbium.core;

import org.openqa.selenium.WebDriver;

public interface IDriverOptions {
    WebDriver getWrappedWebDriver();
    void resetPlaybackOptions();
    void setPlaybackOptions();
    DriverOptions setOption(Common common, Object option);
    Object getOption(Common playbackOption);
    DriverOptions reset();
    Boolean shouldLoad();
    Long delayBefore();
    Long resolve();
    Long retryInterval();
    Boolean shouldHighlight();
    Long highlightAfter();
    Boolean shouldSuppressDelays();
}
