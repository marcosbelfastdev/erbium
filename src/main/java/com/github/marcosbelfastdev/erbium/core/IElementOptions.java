package com.github.marcosbelfastdev.erbium.core;

import java.time.Duration;

public interface IElementOptions {
    void setPlaybackOptions();
    Element setOption(Common common, Object option) throws Throwable;
    Object getOption(Common playbackOption);
    Element reset();
    Element resetOptions();
    Boolean shouldLoad();
    Long delayBefore();
    Duration resolve();
    Long retryInterval();
    Boolean shouldHighlight();
    Long highlightAfter();
    Boolean shouldSuppressDelays();
}
