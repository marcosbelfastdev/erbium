import exceptions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class PlaybackOptions {
	
	private final Map<Common, Object> _options;

	public PlaybackOptions() {
		_options = new HashMap<>();

	}

	public PlaybackOptions(Map<Common, Object> options) {
		this._options = new HashMap<>(options);
	}

	public static PlaybackOptions init() {
		Map<Common, Object> options = new HashMap<>();
		options.put(Common.LOAD_ON_DEMAND, true);
		options.put(Common.RESOLVE_TIMEOUT, 120000L);
		options.put(Common.CORE_SELENIUM_IMPLICITLY_WAIT, 120000L);
		options.put(Common.INTERACT_DELAY_BEFORE, 0);
		options.put(Common.SCROLL_DELAY_AFTER, 0);
		options.put(Common.HIGHLIGHT_STYLE, "border: 2px solid springgreen; border-radius: 5px;");
		options.put(Common.INTERACT_DELAY_AFTER, 0);
		options.put(Common.RETRY_INTERVAL, 250L);
		options.put(Common.HIGHLIGHT_ELEMENTS, true);
		options.put(Common.HIGHLIGHT_DELAY_AFTER, 0);
		options.put(Common.HIGHLIGHT_FRAMES, false);
		options.put(Common.SCROLL_TO_ELEMENTS, true);
		options.put(Common.REQUIRE_ENABLED, false);
		options.put(Common.REQUIRE_VISIBLE, false);
		options.put(Common.ENABLED_TIMEOUT, 10000L);
		options.put(Common.VISIBLE_TIMEOUT, 10000L);
		options.put(Common.EXECUTOR_CLICKS, true);
		options.put(Common.EXECUTOR_SETTEXT, true);
		options.put(Common.EXECUTOR_CLEAR, true);
		options.put(Common.PAGE_LOAD_TIMEOUT, 120000L); // replaces native driver pageLoadTimeout
		options.put(Common.SUPPRESS_DELAYS, false);
		options.put(Common.HANDLE_ALERTS, false);
		options.put(Common.ALERTS_ACTION, AlertOption.DISMISS);
		options.put(Common.SEARCHSCROLL_RESOLVE, 2);
		options.put(Common.SEARCHSCROLL_FACTOR, 0.85d);
		options.put(Common.SEARCHSCROLL_HEIGHT, 500L);
		options.put(Common.SEARCHSCROLL_TIMEOUT, options.get(Common.RESOLVE_TIMEOUT));
		options.put(Common.FALLBACK_TO_EXECUTOR, true);
		options.put(Common.WINDOW_LOCKING, true);
		options.put(Common.WINDOW_SEARCH, true);
//		options.put(Common.ENABLE_TIMERS, false);
//		options.put(Common.WAITFOR_INITIAL_TIME, 200);
//		options.put(Common.REPORT_TO_TESTNG, false);
		options.put(Common.HIDE_PASSWORDS, true);
		options.put(Common.ENABLE_SCREENSHOTS, true);
		return new PlaybackOptions(options);
	}

	public void evaluateOptionChange(Common option, Object value) {

		if ((long) value < 0) {
			ErrorHandling.end(OptionValueTooSmall.class);
		}

		Common[] common = {
				Common.RESOLVE_TIMEOUT,
				Common.ENABLED_TIMEOUT,
				Common.RETRY_INTERVAL,
				Common.VISIBLE_TIMEOUT,
				Common.SEARCHSCROLL_RESOLVE
		};

		if (!Arrays.asList(common).contains(option)) {
			return;
		}

		switch (option) {
			case RETRY_INTERVAL:
				if ((long) value < 20) {
					ErrorHandling.end(RetryIntervalTooSmall.class);
				}
				if ((Long) value > ((Long)_options.get(Common.RESOLVE_TIMEOUT) / 3)) {
					ErrorHandling.end(RetryIntervalTooBig.class);
				}
				if ((Long) value > ((Long)_options.get(Common.ENABLED_TIMEOUT))) {
					ErrorHandling.end(RetryIntervalTooBig.class);
				}
				if ((Long) value > ((Long)_options.get(Common.VISIBLE_TIMEOUT))) {
					ErrorHandling.end(RetryIntervalTooBig.class);
				}
				break;
			case RESOLVE_TIMEOUT:
				if ((long) value < 2000) {
					ErrorHandling.end(ResolveTimeoutTooSmall.class);
				}
				if ((long) value < ((Long)_options.get(Common.RETRY_INTERVAL) * 3)) {
					ErrorHandling.end(ResolveTimeoutTooSmall.class);
				}
				break;
			case ENABLED_TIMEOUT:
				if ((long) value < 100) {
					ErrorHandling.end(EnabledTimeoutTooSmall.class);
				}
				if ((long) value > (long) _options.get(Common.RESOLVE_TIMEOUT)) {
					ErrorHandling.end(EnabledTimeoutTooBig.class);
				}
				break;
			case VISIBLE_TIMEOUT:
				if ((long) value < 100) {
					ErrorHandling.end(VisibleTimeoutTooSmall.class);
				}
				if ((long) value > (long) _options.get(Common.RESOLVE_TIMEOUT)) {
					ErrorHandling.end(VisibleTimeoutTooBig.class);
				}
				break;
			case SEARCHSCROLL_RESOLVE:
				if ((long) value < 1) {
					ErrorHandling.end(SearchScrollTimeoutTooSmall.class);
				}
				break;
		}
	}


	protected void setOption(Common option, Object value) {
		evaluateOptionChange(option, value);
		if(_options.containsKey(option))
			_options.replace(option, value);
		else
			_options.put(option, value);
		
	}
	
	protected Object getOption(Common option) {
		
		Object value = null;
		
		if(_options.containsKey(option))
			value = _options.get(option);
		
		return value;
	}

	public void removeOption(Common playbackOption) {
		_options.remove(playbackOption);
	}
	
	protected Map<Common, Object> getOptions() {
		return _options;
	}

	protected PlaybackOptions duplicate() {

		Map<Common, Object> options = new HashMap<Common, Object>(_options);
		return new PlaybackOptions(options);
	}

	@Override
	public String toString() {
		return _options.toString();
	}
	
}
