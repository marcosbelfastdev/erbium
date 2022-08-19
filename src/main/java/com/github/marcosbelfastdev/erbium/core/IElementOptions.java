package com.github.marcosbelfastdev.erbium.core;

import org.openqa.selenium.WebDriver;

public interface IElementOptions {
    WebDriver getWrappedWebDriver();
    void resetPlaybackOptions();
    void setPlaybackOptions();
    Element setOption(Common common, Object option);
    Object getOption(Common playbackOption);
    Element reset();
    Boolean shouldLoad();
    Long delayBefore();
    Long resolve();
    Long coreImplicitly();
    Long retryInterval();
    Boolean shouldHighlight();
    Long highlightAfter();
    Boolean shouldSuppressDelays();
}
