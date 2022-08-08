package org.base.erbium;

import org.openqa.selenium.WebElement;
import org.base.erbium.EElement.*;

public class EFrame extends ElementCompositor {
    
    HighlightOptions $highLightOption = HighlightOptions.DoNotUnhighlight;

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

    public void setHighLightOption(HighlightOptions option) {
        $highLightOption = option;
    }

    protected HighlightOptions getHighlightOption() {
        return $highLightOption;
    }

    public WebElement getWebElement() {
        return $eElement.getWebElement();
    }
    
    /*public void setOption(com.github.marcosbelfastdev.erbium.core.Common option, Object value) {
        $labElement.setOption(option, value);
    }
    
    public Object getOption(com.github.marcosbelfastdev.erbium.core.Common option) {
        return _getOption($labElement.getDriverIndex(), option);
    }*/
   
    
}
