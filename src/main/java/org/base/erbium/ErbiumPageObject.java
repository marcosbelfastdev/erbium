package org.base.erbium;

public class ErbiumPageObject {

    protected EDriver $driver;

    public ErbiumPageObject(EDriver driver) {
        $driver = driver;
        // include logic for page objects
        PageObjectsOptions option = (PageObjectsOptions) $driver.getOption(AdvancedOptions.PAGE_OBJECT_OPTIONS);
        switch (option) {
            case SHARED:
                break;
            case FORK_RESET: {

                break;
            }
            case FORK_COPY: {

                break;
            }
        }
    }

    public EDriver getDriver() {
        return $driver;
    }

}
