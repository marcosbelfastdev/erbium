package org.base.erbium;


public class ElementCompositor {
	
	protected EElement $eElement;
	
	public ElementCompositor(EElement eElement) {
		$eElement = eElement;
	}
	
	public EElement getEElement() {
		return $eElement;
	}
	
}
