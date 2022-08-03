package org.base.erbium;

public class ErbiumPageObject {

    protected EDriver $driver;

    public ErbiumPageObject(EDriver driver) {
        // include logic for page objects
        PageObjectsOptions option = (PageObjectsOptions) driver.getOption(AdvancedOptions.PAGE_OBJECT_OPTIONS);
        switch (option) {
            case FORK_RESET: {
                $driver = new EDriver(driver);
                PlaybackOptionsSet playbackOptionsSet = new PlaybackOptionsSet();
                playbackOptionsSet.populate();
                $driver.setOptionsSet(playbackOptionsSet);
                $driver.setOptionsSet(new AdvancedOptionsSet());
                $driver.setOptionsSet(new ReportingOptionsSet());
                break;
            }
            case FORK_COPY: {
                $driver = new EDriver(driver);
                PlaybackOptionsSet playbackOptionsSet = driver.getPlaybackOptions().duplicate();
                $driver.setOptionsSet(playbackOptionsSet);
                $driver.setOptionsSet(driver.getAdvancedOptions().duplicate());
                $driver.setOptionsSet(driver.getReportingOptions().duplicate());
                break;
            }
            case SHARED: {
                $driver = driver;
            }
        }
    }

    public EDriver getDriver() {
        return $driver;
    }

}
