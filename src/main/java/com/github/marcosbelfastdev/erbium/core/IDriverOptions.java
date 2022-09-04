package com.github.marcosbelfastdev.erbium.core;

import org.openqa.selenium.WebDriver;

public interface IDriverOptions {
    WebDriver getWrappedWebDriver();
    void resetPlaybackOptions() throws Throwable;
    void setPlaybackOptions();
    void setPlaybackOptions(PlaybackOptions playbackOptions);
    DriverOptions setOption(Common common, Object option) throws Throwable;
    Object getOption(Common playbackOption);
    DriverOptions reset() throws Throwable;
    Boolean shouldLoad();
    Long delayBefore();
    Long resolve();
    Long retryInterval();
    Boolean shouldHighlight();
    Long highlightAfter();
    Boolean shouldSuppressDelays();
}
