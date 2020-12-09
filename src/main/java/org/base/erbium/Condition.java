package org.base.erbium;

import org.openqa.selenium.By;

public class Condition {

    private String $sid;
    private final ConditionOption $option;
    private final By[] $locators;

    public Condition(ConditionOption option, EElement... elements) {
        $option = option;
        $locators = getLocators(elements);
    }

    public Condition(ConditionOption option, By... locators) {
        $option = option;
        $locators = locators;
    }

    ConditionOption getConditionOption() {
        return $option;
    }

    By[] getLocators() {
        return $locators;
    }

    By[] getLocators(EElement... elements) {
        By[] locators = new By[elements.length];
        for(int i = 0; i < locators.length; i++ ) {
            locators[i] = elements[i].getBy();
        }
        return locators;
    }

}
