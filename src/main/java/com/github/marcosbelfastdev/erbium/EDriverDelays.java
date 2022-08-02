package com.github.marcosbelfastdev.erbium;


public class EDriverDelays {
	
	EDriver $driver;
	
	public EDriverDelays(EDriver driver) {
		$driver = driver;
	}
	
	//@Override
	public void setDelayBeforeInteraction(int time) {
		$driver.getPlaybackOptions().setOption(Common.INTERACT_DELAY_BEFORE, time);
	}
	
	public void setDelayAfterScroll(int time) {
		$driver.getPlaybackOptions().setOption(Common.SCROLL_DELAY_AFTER, time);
	}
	
	public void setDelayAfterHighlight(int time) {
		$driver.getPlaybackOptions().setOption(Common.HIGHLIGHT_DELAY_AFTER, time);
	}
	
	public void setDelayAfterClick(int time) {
		$driver.getPlaybackOptions().setOption(Common.INTERACT_DELAY_AFTER, time);
	}
	
	public void setSuppressDelays(boolean value) {
		$driver.getPlaybackOptions().setOption(Common.SUPPRESS_DELAYS, value);
	}
	
	public void setSearchScrollDelay(int time) {
		$driver.getPlaybackOptions().setOption(Common.SEARCHSCROLL_RESOLVE, time);
	}
	
}