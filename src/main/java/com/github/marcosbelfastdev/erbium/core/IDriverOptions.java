package com.github.marcosbelfastdev.erbium.core;

public interface IDriverOptions {
    public Boolean shouldLoad();
    public Long delayBefore();
    public Long resolve();
    public Long coreImplicitly();
    public Long retryInterval();
    public Boolean shouldHighlight();
    public Long highlightAfter();
    public Boolean shouldSuppressDelays();
}
