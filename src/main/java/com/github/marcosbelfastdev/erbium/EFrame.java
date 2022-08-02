package com.github.marcosbelfastdev.erbium;

import org.openqa.selenium.WebElement;

public class EFrame extends ElementCompositor {
    
    EElement.HighlightOptions $highLightOption = EElement.HighlightOptions.DoNotUnhighlight;

    public EFrame(EElement eElement) {
        super(eElement);
    }
    
    public void highlight() throws Exception {
        $eElement.highlight("border: 1px solid springgreen;",
                EElement.HighlightOptions.DoNotUnhighlight);
    }

    public void scroll() throws Exception {
        $eElement.healthCheck();
        $eElement.jsScroll();
    }

    public void setHighLightOption(EElement.HighlightOptions option) {
        $highLightOption = option;
    }

    protected EElement.HighlightOptions getHighlightOption() {
        return $highLightOption;
    }

    public WebElement getWebElement() {
        return $eElement.getWebElement();
    }
    
    /*public void setOption(Common option, Object value) {
        $labElement.setOption(option, value);
    }
    
    public Object getOption(Common option) {
        return _getOption($labElement.getDriverIndex(), option);
    }*/
   
    
}
