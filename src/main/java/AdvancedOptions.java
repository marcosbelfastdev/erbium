import java.util.HashMap;
import java.util.Map;

class AdvancedOptions {
	
	private final Map<AdvancedOption, Object> _options;

	/**
	 * Import constructor
	 * @param options
	 */
	public AdvancedOptions(Map<AdvancedOption, Object> options) {
		_options = new HashMap<>(options);
	}

	/**
	 * Default
	 */
	public AdvancedOptions() {
		_options = new HashMap<>();
	}

	public static AdvancedOptions init() {
		Map<AdvancedOption, Object> options = new HashMap<>();
		options.put(AdvancedOption.LOAD_ON_DEMAND, true);
		options.put(AdvancedOption.PAGE_LOAD_TIMEOUT, 120000);
		options.put(AdvancedOption.MINIMUM_PAGE_TIME, 0);
		return new AdvancedOptions(options);
	}

	public void setOption(AdvancedOption option, Object value) {
		
		if(_options.containsKey(option))
			_options.replace(option, value);
		else
			_options.put(option, value);
		
	}
	
	public Object getOption(AdvancedOption option) {
		
		Object value = null;
		
		if(_options.containsKey(option))
			value = _options.get(option);
		
		return value;
	}

	public void removeOption(AdvancedOption advancedOption) {
		_options.remove(advancedOption);
	}

	public Map<AdvancedOption, Object> getOptions() {
		return _options;
	}

	public AdvancedOptions duplicate() {
		Map<AdvancedOption, Object> options = new HashMap<AdvancedOption, Object>(_options);
		return new AdvancedOptions(options);
	}
	
}
