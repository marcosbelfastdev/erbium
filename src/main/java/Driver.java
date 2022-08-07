import org.openqa.selenium.WebDriver;

import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;

public class Driver {

    WebDriver _driver;
    AdvancedOptions _advancedOptions;
    AdvancedOptions _changedAdvancedOptions;
    PlaybackOptions _playbackOptions;
    PlaybackOptions _changedPlaybackOptions;


    public Driver(WebDriver driver) {
        _driver = driver;
        frameworkSettingsProtection();
        setAdvancedOptions();
        setPlaybackOptions();
    }

    public WebDriver getWrappedWebDriver() {
        _driver.manage().timeouts()
                .implicitlyWait((long) _playbackOptions.getOption(Common.CORE_SELENIUM_IMPLICITLY_WAIT), TimeUnit.MILLISECONDS);
        return _driver;
    }

    private void resetAdvancedOptions() {
        for (AdvancedOption advancedOption : _changedAdvancedOptions.getOptions().keySet()) {
            _advancedOptions.setOption(advancedOption, _changedAdvancedOptions.getOption(advancedOption));
            _changedAdvancedOptions.removeOption(advancedOption);
        }
    }

    private void resetPlaybackOptions() {
        for (Common playbackOption : _changedPlaybackOptions.getOptions().keySet()) {
            _playbackOptions.setOption(playbackOption, _changedPlaybackOptions.getOption(playbackOption));
            _changedPlaybackOptions.removeOption(playbackOption);
        }
    }

    private void setAdvancedOptions() {
        _advancedOptions = AdvancedOptions.init();
        _changedAdvancedOptions = new AdvancedOptions();
    }

    private void setPlaybackOptions() {
        _playbackOptions = PlaybackOptions.init();
        _changedPlaybackOptions = new PlaybackOptions();
    }

    public Driver setOption(Common playbackOption, Object value) {
        frameworkSettingsProtection();
        _changedPlaybackOptions.setOption(playbackOption, value);
        return this;
    }

    public Object getOption(Common playbackOption) {
        if (_changedPlaybackOptions.getOptions().containsKey(playbackOption))
            return _changedPlaybackOptions.getOption(playbackOption);
        else
            return _playbackOptions.getOption(playbackOption);
    }

    private void frameworkSettingsProtection() {
        try {
            if(!isNull(_driver)) {
                _driver.manage().timeouts().pageLoadTimeout((int) getOption(Common.PAGE_LOAD_TIMEOUT), TimeUnit.MILLISECONDS);
                _driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
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
     * @return
     */
    public Driver reset() {
        // Framework settings protection in case WebDriver had been exposed
        frameworkSettingsProtection();
        // Advanced Options
        resetAdvancedOptions();
        // Playback Options
        resetPlaybackOptions();
        return this;
    }

    public DriverManage manage() {
        return
    }



}
