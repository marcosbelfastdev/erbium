package main.java.org.base.erbium;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class AdvancedOptionsSet {
	
	private final Map<org.base.erbium.AdvancedOptions, Object> $options;

	public AdvancedOptionsSet(Map<AdvancedOptions, Object> options) {
		$options = new HashMap<>();
		$options.putAll(options);
	}

	public AdvancedOptionsSet() {
		$options = new HashMap<>();
		$options.put(AdvancedOptions.LOAD_ON_DEMAND, true);
		$options.put(AdvancedOptions.PAGE_LOAD_TIMEOUT, 120000);
		$options.put(AdvancedOptions.MINIMUM_PAGE_TIME, 0);
		$options.put(AdvancedOptions.PAGE_OBJECT_OPTIONS, PageObjectsOptions.SHARED);
	}

	protected void setOption(AdvancedOptions option, Object value) {
		
		if($options.containsKey(option))
			$options.replace(option, value);
		else
			$options.put(option, value);
		
	}
	
	protected Object getOption(AdvancedOptions option) {
		
		Object value = null;
		
		if($options.containsKey(option))
			value = $options.get(option);
		
		return value;
	}
	
	protected void addOption(AdvancedOptions option, Object value) {
		$options.put(option, value);
	}
	
	protected Set<AdvancedOptions> keySet() {
		return $options.keySet();
	}
	
	protected void clearOptions() {
		$options.clear();
	}

	protected AdvancedOptionsSet duplicate() {
		Map<AdvancedOptions, Object> options = new HashMap<AdvancedOptions, Object>();
		options.putAll($options);
		AdvancedOptionsSet advancedOptionsSet = new AdvancedOptionsSet(options);
		return advancedOptionsSet;
	}
	
}
