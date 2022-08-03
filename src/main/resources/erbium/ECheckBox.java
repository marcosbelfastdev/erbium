package org.base.erbium;

public class ECheckBox extends EElement {


    public ECheckBox(final EElement element) {
        super(element);
    }

    public void check() throws Exception {

        if(!isSelected())
            click();

    }

    public void uncheck() throws Exception {

        if(isSelected())
            click();
    }

    public void toggle() throws Exception {
        click();
    }

    public boolean isChecked() throws Exception {

        return(isSelected());
    }


}
