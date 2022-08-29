package com.github.marcosbelfastdev.erbium.core;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Map;

import static java.util.Objects.isNull;

public class ElementOptions extends BaseElement implements IElementOptions {

    private PlaybackOptions _playbackOptions;

    public ElementOptions(Driver driver, By by) {
        super(driver, by);
        setPlaybackOptions();
    }

    public ElementOptions(Driver driver, WebElement webElement) {
        super(driver, webElement);
    }

    public Map<Common, Object> getOptions() {
        return _playbackOptions.getOptionsMap();
    }

    @Override
    public Object getOption(Common option) {
        Object value;
        value = _playbackOptions.getOption(option);
        if (isNull(value))
            value = _driver.getOption(option);
        return value;
    }

    @Override
    public ElementOptions setOption(Common option, Object value) throws Throwable {
        _playbackOptions.setOption(option, value);
        return this;
    }

    @Override
    public void setPlaybackOptions() {
        _playbackOptions = new PlaybackOptions();
    }


    @Override
    public Element reset() {
        return null;
    }

    @Override
    public ElementOptions resetOptions() {
        _playbackOptions = new PlaybackOptions();
        return this;
    }

    @Override
    public Boolean shouldLoad() {
        return (boolean) getOption(Common.LOAD_ON_DEMAND);
    }

    @Override
    public Long delayBefore() {
        return (long) getOption(Common.INTERACT_DELAY_BEFORE);
    }

    @Override
    public Long resolve() {
        return (long) getOption(Common.RESOLVE_TIMEOUT);
    }

    @Override
    public Long retryInterval() {
        return (long) getOption(Common.RETRY_INTERVAL);
    }

    protected boolean isExecutorEnabled() {
        return true;
    }

    protected int getSearchScrollTimeout() {
        return (int) getOption(Common.SEARCHSCROLL_TIMEOUT);
    }

    protected boolean shouldHandleAlerts() {
        return (boolean) getOption(Common.HANDLE_ALERTS);
    }


    protected boolean shouldScroll() {
        return (boolean) getOption(Common.SCROLL_TO_ELEMENTS);
    }

    public Boolean shouldHighlight() {
        return (boolean) getOption(Common.HIGHLIGHT_ELEMENTS);
    }

    @Override
    public Long highlightAfter() {
        return (long) getOption(Common.HIGHLIGHT_DELAY_AFTER);
    }

    @Override
    public Boolean shouldSuppressDelays() {
        return (boolean) getOption(Common.SUPPRESS_DELAYS);
    }

    public long getResolveTimeout() {
        return (long) getOption(Common.RESOLVE_TIMEOUT);
    }

    public boolean requiresExecutorClick() {
        return (boolean) getOption(Common.EXECUTOR_CLICKS);
    }

    public boolean shouldFallbackToExecutor() {
        return (boolean) getOption(Common.FALLBACK_TO_EXECUTOR);
    }

    public long getRetryInterval() {
        return (long) getOption(Common.RETRY_INTERVAL);
    }

    public boolean requiresElementVisible() {
        return (boolean) getOption(Common.REQUIRE_VISIBLE);
    }

    public long getElementVisibleTimeout() {
        return (long) getOption(Common.VISIBLE_TIMEOUT);
    }

    public boolean requiresElementEnabled() {
        return (boolean) getOption(Common.REQUIRE_ENABLED);
    }

    public long getElementEnabledTimeout() {
        return (long) getOption(Common.ENABLED_TIMEOUT);
    }

    public boolean shouldHighlightFrames() {
        return (boolean) getOption(Common.HIGHLIGHT_FRAMES);
    }

    public boolean shouldLockToWindow() {
        return (boolean) getOption(Common.WINDOW_LOCKING);
    }

    public boolean requireExecutorClear() {
        return (boolean) getOption(Common.EXECUTOR_CLEAR);
    }

    public boolean requireExecutorSetText() {
        return (boolean) getOption(Common.EXECUTOR_SETTEXT);
    }

    public boolean isScreenshotEnabled() {
        return (boolean) getOption(Common.ENABLE_SCREENSHOTS);
    }

    public String getHighLightStyle() {
        return (String) getOption(Common.HIGHLIGHT_STYLE);
    }

    public long getAfterHighlightDelay() {
        return (long) getOption(Common.HIGHLIGHT_DELAY_AFTER);
    }

    public boolean allowDelays() {
        return !shouldSuppressDelays();
    }

}
