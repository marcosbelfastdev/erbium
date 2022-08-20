package com.github.marcosbelfastdev.erbium.core;

import com.github.marcosbelfastdev.erbium.exceptions.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.github.marcosbelfastdev.erbium.core.ErrorHandling.end;

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
		options.put(Common.SCROLL_TO_ELEMENTS, true);
		options.put(Common.REQUIRE_ENABLED, false);
		options.put(Common.REQUIRE_VISIBLE, false);
		options.put(Common.ENABLED_TIMEOUT, 10000L);
		options.put(Common.VISIBLE_TIMEOUT, 10000L);
		options.put(Common.EXECUTOR_CLICKS, false);
		options.put(Common.EXECUTOR_SETTEXT, false);
		options.put(Common.EXECUTOR_CLEAR, false);
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
		options.put(Common.HIDE_PASSWORDS, true);
		options.put(Common.ENABLE_SCREENSHOTS, true);
		return new PlaybackOptions(options);
	}

	public void evaluateOptionChange(Common option, Object value) {

		if (value instanceof Integer ||
			value instanceof Double ||
			value instanceof Long ||
			value instanceof Float)
			if ((double) value < 0d) {
				end(OptionValueTooSmall.class);
		}

		Common[] common = {
				Common.RESOLVE_TIMEOUT,
				Common.ENABLED_TIMEOUT,
				Common.RETRY_INTERVAL,
				Common.VISIBLE_TIMEOUT,
				Common.SEARCHSCROLL_RESOLVE,
				Common.PAGE_LOAD_TIMEOUT
		};

		if (!Arrays.asList(common).contains(option)) {
			return;
		}

		switch (option) {
			case RETRY_INTERVAL:
				if ((double) value < 20.0d) {
					end(RetryIntervalTooSmall.class);
				}
				if ((long) value > ((long)_options.get(Common.RESOLVE_TIMEOUT))/3) {
					end(RetryIntervalTooBig.class);
				}
				if ((long) value > ((long) _options.get(Common.ENABLED_TIMEOUT))) {
					end(RetryIntervalTooBig.class);
				}
				if ((long) value > ((long) _options.get(Common.VISIBLE_TIMEOUT))) {
					end(RetryIntervalTooBig.class);
				}
				break;
			case RESOLVE_TIMEOUT:
				if ((long) value < 2000) {
					end(ResolveTimeoutTooSmall.class);
				}
				if ((long) value < ((Long) _options.get(Common.RETRY_INTERVAL) * 3)) {
					end(ResolveTimeoutTooSmall.class);
				}
				break;
			case ENABLED_TIMEOUT:
				if ((long) value < 100) {
					end(EnabledTimeoutTooSmall.class);
				}
				if ((long) value > (long) _options.get(Common.RESOLVE_TIMEOUT)) {
					end(EnabledTimeoutTooBig.class);
				}
				break;
			case VISIBLE_TIMEOUT:
				if ((long) value < 100) {
					end(VisibleTimeoutTooSmall.class);
				}
				if ((long) value > (long) _options.get(Common.RESOLVE_TIMEOUT)) {
					end(VisibleTimeoutTooBig.class);
				}
				break;
			case SEARCHSCROLL_RESOLVE:
				if ((long) value < 1) {
					end(SearchScrollTimeoutTooSmall.class);
				}
				break;
			case PAGE_LOAD_TIMEOUT:
				if ((long) value < (long) _options.get(Common.RESOLVE_TIMEOUT)) {
					end(PageLoadTimeoutTooSmall.class);
				}
				break;
		}
	}

	protected void setOption(Common option, Object value) {
		if (_options.containsKey(option)) {
			if (!getOption(option).getClass().equals(value.getClass()))
				end(IncompatibleOptionTypes.class);
			evaluateOptionChange(option, value);
			_options.replace(option, value);
		} else
			_options.put(option, value);
	}

	protected Object getOption(Common option) {

		Object value = null;

		if (_options.containsKey(option))
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
