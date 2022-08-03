package org.base.erbium;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class PlaybackOptionsSet {
	
	private final Map<Common, Object> $options= new HashMap<>();

	public PlaybackOptionsSet() {

	}

	public PlaybackOptionsSet(Map<Common, Object> options) {
		$options.putAll(options);
	}

	void populate() {
		$options.put(Common.RESOLVE_TIMEOUT, 120000);
		$options.put(Common.INTERACT_DELAY_BEFORE, 0);
		$options.put(Common.SCROLL_DELAY_AFTER, 0);
		$options.put(Common.HIGHLIGHT_STYLE, "border: 2px solid springgreen; border-radius: 5px;");
		$options.put(Common.INTERACT_DELAY_AFTER, 0);
		$options.put(Common.RETRY_INTERVAL, 150);
		$options.put(Common.HIGHLIGHT_ELEMENTS, false);
		$options.put(Common.HIGHLIGHT_DELAY_AFTER, 0);
		$options.put(Common.HIGHLIGHT_FRAMES, false);
		$options.put(Common.SCROLL_TO_ELEMENTS, true);
		$options.put(Common.REQUIRE_ENABLED, false);
		$options.put(Common.REQUIRE_VISIBLE, false);
		$options.put(Common.ENABLED_TIMEOUT, 10000);
		$options.put(Common.VISIBLE_TIMEOUT, 10000);
		$options.put(Common.EXECUTOR_CLICKS, true);
		$options.put(Common.EXECUTOR_SETTEXT, true);
		$options.put(Common.EXECUTOR_CLEAR, true);
		$options.put(Common.PAGE_LOAD_TIMEOUT, 120000); // replaces native driver pageLoadTimeout
		$options.put(Common.SUPPRESS_DELAYS, false);
		$options.put(Common.HANDLE_ALERTS, false);
		$options.put(Common.ALERTS_ACTION, AlertOptions.DISMISS);
		$options.put(Common.SEARCHSCROLL_RESOLVE, 2);
		$options.put(Common.SEARCHSCROLL_FACTOR, 0.85d);
		$options.put(Common.SEARCHSCROLL_HEIGHT, 500);
		$options.put(Common.SEARCHSCROLL_TIMEOUT, 120000);
		$options.put(Common.FALLBACK_TO_EXECUTOR, true);
		$options.put(Common.WINDOW_LOCKING, true);
		$options.put(Common.WINDOW_SEARCH, true);
		$options.put(Common.ENABLE_TIMERS, false);
		$options.put(Common.WAITFOR_INITIAL_TIME, 200);
		$options.put(Common.REPORT_TO_TESTNG, false);
		$options.put(Common.HIDE_PASSWORDS, true);
		$options.put(Common.ENABLE_SCREENSHOTS, true);
	}

	
	protected void setOption(Common option, Object value) {
		
		if($options.containsKey(option))
			$options.replace(option, value);
		else
			$options.put(option, value);
		
	}
	
	protected Object getOption(Common option) {
		
		Object value = null;
		
		if($options.containsKey(option))
			value = $options.get(option);
		
		return value;
	}
	
	protected void addOption(Common option, Object value) {
		$options.put(option, value);
	}
	
	protected Set<Common> keySet() {
		return $options.keySet();
	}
	
	protected void clearOptions() {
		$options.clear();
	}

	protected PlaybackOptionsSet duplicate() {

		Map<Common, Object> options = new HashMap<Common, Object>();
		options.putAll($options);
		PlaybackOptionsSet playbackOptionsSet = new PlaybackOptionsSet(options);
		return playbackOptionsSet;
	}

	@Override
	public String toString() {
		return $options.toString();
	}
	
}
