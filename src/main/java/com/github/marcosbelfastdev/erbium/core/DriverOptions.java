package com.github.marcosbelfastdev.erbium.core;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;

import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;

public class DriverOptions implements IDriverOptions {

    protected WebDriver _driver;
    protected PlaybackOptions _playbackOptions;
    protected PlaybackOptions _changedPlaybackOptions;


    public DriverOptions(WebDriver driver) {
        _driver = driver;
        setPlaybackOptions();
        frameworkSettingsProtection();
    }

    public WebDriver getWrappedWebDriver() {
        return _driver;
    }

    @Override
    public void resetPlaybackOptions() throws Throwable {
        for (Common playbackOption : _changedPlaybackOptions.getOptionsMap().keySet()) {
            _changedPlaybackOptions.setOption(playbackOption, _playbackOptions.getOption(playbackOption));
            _changedPlaybackOptions.removeOption(playbackOption);
        }
    }

    @Override
    public void setPlaybackOptions() {
        _playbackOptions = PlaybackOptions.init();
        _changedPlaybackOptions = new PlaybackOptions();
    }

    @Override
    public DriverOptions setOption(Common playbackOption, Object value) throws Throwable {
        if (!_playbackOptions.getOptionsMap().containsKey(playbackOption))
           _playbackOptions.setOption(playbackOption, value);
        else
            _changedPlaybackOptions.setOption(playbackOption, value);
        frameworkSettingsProtection();
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getOption(Common playbackOption) {
        Object value;
        if (_changedPlaybackOptions.getOptionsMap().containsKey(playbackOption))
            value = _changedPlaybackOptions.getOption(playbackOption);
        else
            value = _playbackOptions.getOption(playbackOption);
        return value;
    }

    protected void frameworkSettingsProtection() {
        try {
            if(!isNull(_driver)) {
                _driver.manage().timeouts().pageLoadTimeout((long) getOption(Common.PAGE_LOAD_TIMEOUT), TimeUnit.MILLISECONDS);
                _driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
                _driver.manage().timeouts().setScriptTimeout((long) getOption(Common.RESOLVE_TIMEOUT), TimeUnit.MILLISECONDS);
            }
        } catch (Exception ignored) {

        }
    }

    /**
     * Resets all options that were changed since the driver was started (or since last reset).
     * Some framework protections are also reset, irrespective of whether or not they were changed.
     * These settings are:
     * Selenium core page load timeout that was set at the driver start;
     * The Selenium core implicitly wait is set to 0.
     */
    @Override
    public DriverOptions reset() throws Throwable {
        // Playback Options
        resetPlaybackOptions();
        // Framework settings protection in case WebDriver had been exposed
        frameworkSettingsProtection();
        return this;
    }

    @Override
    public Boolean shouldLoad() {
        return (Boolean) getOption(Common.LOAD_ON_DEMAND);
    }

    @Override
    public Long delayBefore() {
        return (Long) getOption(Common.INTERACT_DELAY_BEFORE);
    }

    @Override
    public Long resolve() {
        return (Long) getOption(Common.RESOLVE_TIMEOUT);
    }

    @Override
    public Long retryInterval() {
        return (Long) getOption(Common.RETRY_INTERVAL);
    }

    @Override
    public Boolean shouldHighlight() {
        return (Boolean) getOption(Common.HIGHLIGHT_ELEMENTS);
    }

    @Override
    public Long highlightAfter() {
        return (Long) getOption(Common.HIGHLIGHT_DELAY_AFTER);
    }

    @Override
    public Boolean shouldSuppressDelays() {
        return (Boolean) getOption(Common.SUPPRESS_DELAYS);
    }
}
