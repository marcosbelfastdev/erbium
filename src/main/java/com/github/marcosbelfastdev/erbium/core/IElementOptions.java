package com.github.marcosbelfastdev.erbium.core;

import org.openqa.selenium.WebDriver;

public interface IElementOptions {
    void setPlaybackOptions();
    ElementOptions setOption(Common common, Object option);
    Object getOption(Common playbackOption);
    ElementOptions reset();
    ElementOptions resetOptions();
    Boolean shouldLoad();
    Long delayBefore();
    Long resolve();
    Long retryInterval();
    Boolean shouldHighlight();
    Long highlightAfter();
    Boolean shouldSuppressDelays();
}
