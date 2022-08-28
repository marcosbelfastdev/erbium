package com.github.marcosbelfastdev.erbium.core;

import org.openqa.selenium.By;

import java.util.Map;

public class ElementOptions extends BaseElement implements IElementOptions {

    private PlaybackOptions _playbackOptions;

    public ElementOptions(By by) {
        super(by);
        setPlaybackOptions();
    }

    public Map<Common, Object> getOptions() {
        return _playbackOptions.getOptionsMap();
    }

    @Override
    public Object getOption(Common option) {
        Object value;
        try {
            value = _playbackOptions.getOption(option);
        } catch (Exception e) {
            value = _driver.getOption(option);
        }
        return value;
    }

    @Override
    public ElementOptions setOption(Common option, Object value) {
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
        return null;
    }

    @Override
    public Long delayBefore() {
        return null;
    }

    @Override
    public Long resolve() {
        return null;
    }

    @Override
    public Long retryInterval() {
        return null;
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
        return null;
    }

    @Override
    public Boolean shouldSuppressDelays() {
        return null;
    }

    public int getResolveTimeout() {
        return (int) getOption(Common.RESOLVE_TIMEOUT);
    }

    public boolean requiresExecutorClick() {
        return (boolean) getOption(Common.EXECUTOR_CLICKS);
    }

    public boolean shouldFallbackToExecutor() {
        return (boolean) getOption(Common.FALLBACK_TO_EXECUTOR);
    }

    public int getRetryInterval() {
        return (int) getOption(Common.RETRY_INTERVAL);
    }

    public boolean requiresElementVisible() {
        return (boolean) getOption(Common.REQUIRE_VISIBLE);
    }

    public int getElementVisibleTimeout() {
        return (int) getOption(Common.VISIBLE_TIMEOUT);
    }

    public boolean requiresElementEnabled() {
        return (boolean) getOption(Common.REQUIRE_ENABLED);
    }

    public int getElementEnabledTimeout() {
        return (int) getOption(Common.ENABLED_TIMEOUT);
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

    public int getAfterHighlightDelay() {
        return (int) getOption(Common.HIGHLIGHT_DELAY_AFTER);
    }

    public boolean allowDelays() {
        return !((boolean) getOption(Common.SUPPRESS_DELAYS));
    }

}
