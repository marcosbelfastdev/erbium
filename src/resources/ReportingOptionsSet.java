package com.github.marcosbelfastdev.erbium;

import java.util.HashMap;
import java.util.Map;

class ReportingOptionsSet {
	
	private final Map<Reporting, Object> $options;

	public ReportingOptionsSet(Map<Reporting, Object> options) {
		$options = new HashMap<>();
		$options.putAll(options);
	}

	public ReportingOptionsSet() {
		$options = new HashMap<>();
		$options.put(Reporting.REPORT_TO_TESTNG, false);
		$options.put(Reporting.LOG_CONDITIONS, false);
		$options.put(Reporting.LOG_DETAILED_CONDITIONS, false);
		$options.put(Reporting.LOG_FRAMES, false);
		$options.put(Reporting.LOG_OPTIONS, false);
		$options.put(Reporting.LOG_TEST_STEPS, true);
		$options.put(Reporting.LOG_WINDOWS_ACTIONS, false);
		$options.put(Reporting.LOG_WINDOWS_DETAILS, false);
	}

	void setOption(Reporting option, Object value) {
		
		if($options.containsKey(option))
			$options.replace(option, value);
		else
			$options.put(option, value);
		
	}
	
	Object getOption(Reporting option) {
		
		Object value = null;
		
		if($options.containsKey(option))
			value = $options.get(option);
		
		return value;
	}
	
	void clearOptions() {
		$options.clear();
	}

	public ReportingOptionsSet duplicate() {

		Map<Reporting, Object> options = new HashMap<Reporting, Object>();
		options.putAll($options);
		ReportingOptionsSet reportingOptionsSet = new ReportingOptionsSet(options);
		return reportingOptionsSet;
	}
	
}
