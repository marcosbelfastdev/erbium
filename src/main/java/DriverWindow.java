import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;

public class DriverWindow {
	
	Driver _driver;
	
	public DriverWindow(Driver driver) {
		_driver = driver;
	}
	
	public void setAutoWindowLocking(boolean value) {
		_driver.setOption(Common.WINDOW_LOCKING, value);
	}
	
	public void setAutoWindowSearch(boolean value) {
		_driver.setOption(Common.WINDOW_SEARCH, true);
	}
	
	public void setSize(Dimension targetSize) {
		_driver.getWebDriver().manage().window().setSize(targetSize);
	}
	
	
	public void setPosition(Point targetPosition) {
		_driver.getWebDriver().manage().window().setPosition(targetPosition);
	}
	
	
	public Dimension getSize() {
		return _driver.getWebDriver().manage().window().getSize();
	}
	
	
	public Point getPosition() {
		return _driver.getWebDriver().manage().window().getPosition();
	}
	
	
	public void maximize() {
		_driver.getWebDriver().manage().window().maximize();
	}
	
	public void fullscreen() {
		_driver.getWebDriver().manage().window().fullscreen();
	}
	
}