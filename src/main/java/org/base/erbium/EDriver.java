package org.base.erbium;

import org.openqa.selenium.*;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class EDriver {

    private final WebDriver $webDriver;
    private PlaybackOptionsSet $playbackOptionsSet;
    private ReportingOptionsSet $reportingOptionsSet;
    private AdvancedOptionsSet $advancedOptionsSet;
    ScreenshotMode $screenshotMode;
    ScreenshotPoint $screenshotPoint;
    EScreenShotDispatcher $screenshotDispatcher;
    private TestParamsSet $testParamsSet;
    private String $resultsDir;
    private static String $DESKey;
    private ELogDispatcher $dispatcher;
    private boolean $hooking;


    public EDriver(WebDriver webDriver) {

        $webDriver = webDriver;
        $hooking = true;
        $playbackOptionsSet = new PlaybackOptionsSet();
        $playbackOptionsSet.populate();
        $reportingOptionsSet = new ReportingOptionsSet();
        $advancedOptionsSet = new AdvancedOptionsSet();

        if($hooking) {
            class Quit extends Thread {
                @Override
                public void run() {
                    $webDriver.quit();
                }
            }
            Runtime.getRuntime().addShutdownHook(new Quit());
        }

        frameworkSettingsProtection();
    }

    public EDriver(EDriver driver) {
        // when the driver is copied for page object support
        $webDriver = driver.getWebDriver();
        $hooking = driver.$hooking;


        if($hooking) {
            class Quit extends Thread {
                @Override
                public void run() {
                    $webDriver.quit();
                }
            }
            Runtime.getRuntime().addShutdownHook(new Quit());
        }

        //frameworkSettingsProtection();
        initDriverProperties();
        setTestParams(driver.$testParamsSet);

    }

    void setOptionsSet(PlaybackOptionsSet playbackOptionsSet) {
        $playbackOptionsSet = playbackOptionsSet;
    }

    void setOptionsSet(AdvancedOptionsSet advancedOptionsSet) {
        $advancedOptionsSet = advancedOptionsSet;
    }

    void setOptionsSet(ReportingOptionsSet reportingOptionsSet) {
        $reportingOptionsSet = reportingOptionsSet;
    }

    void initDriverProperties() {
        $screenshotMode = ScreenshotMode.InLine;
        $screenshotPoint = ScreenshotPoint.Error;
        $screenshotDispatcher = new EScreenShotDispatcher(this);
        $dispatcher = new ELogDispatcher(this);
    }

    void setTestParams(TestParamsSet testParamsSet) {
        $testParamsSet = testParamsSet;
    }

    private void setHooking(boolean hooking) {
        $hooking = hooking;
    }
    
    public static String getErbiumVersion() {
        return "1.6.9";
    }

    protected WebDriver getWebDriver() {
        return $webDriver;
    }

    private void setELogDispatcher(String sid) {
        $dispatcher = new ELogDispatcher(this);
    }


    public PlaybackOptionsSet getPlaybackOptions() {
        return $playbackOptionsSet;
    }

    public AdvancedOptionsSet getAdvancedOptions() {
        return $advancedOptionsSet;
    }

    public ReportingOptionsSet getReportingOptions() {
        return $reportingOptionsSet;
    }


    private JavascriptExecutor getJse(String sid) {
        return (JavascriptExecutor) $webDriver;
    }
    
    public EventFiringWebDriver getEventFiringWebDriver() {
        return (EventFiringWebDriver) $webDriver;
    }

    public static void setDESKey(String key) {
        $DESKey = key;
    }

    public static String generateDESKey() {
        return new Crypto().generateKey();
    }
    
    public boolean checkIf(ConditionOption option, By... locators) {
        Condition condition = new Condition(option, locators);
        return checkIf(condition);
    }
    
    public boolean checkIf(ConditionOption option, EElement... elements) {
        Condition condition = new Condition(option, elements);
        return checkIf(condition);
    }

    public boolean checkIf(Condition condition) {
        ConditionsEvaluator evaluator = new ConditionsEvaluator(this, condition);
        return evaluator.evalCondition();
    }

    public boolean waitFor(Condition condition) {

        int timeout = getResolveTimeout();
        int retry = getRetryInterval();
        ConditionsEvaluator evaluator = new ConditionsEvaluator(this, condition);
        if(condition.getConditionOption()==ConditionOption.NoneOfElementsPresent) {
            if(checkIf(condition)) {
                try {
                    sleep( (int) getOption(Common.WAITFOR_INITIAL_TIME));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        int elapsed = 0;
        boolean result = false;
        while(!result && elapsed < timeout) {
            result = evaluator.evalCondition();
            try {
                sleep(retry);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            elapsed += retry;
        }
        return result;
    }


    public boolean waitFor(ConditionOption option, EElement... elements) {
        return waitFor(new Condition(option, elements));
    }

    public boolean waitFor(ConditionOption option, By... locators) {
        return waitFor(new Condition(option, locators));
    }
    
    void frameworkSettingsProtection() {
        try {
            if($webDriver instanceof WebDriver) {
                $webDriver.manage().timeouts().pageLoadTimeout(getPageLoadTimeout(), TimeUnit.MILLISECONDS);
                $webDriver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void screenshot() {
        screenshot($screenshotMode);
    }

    public void screenshot(ScreenshotMode mode) {
        screenshot(mode, "unnamed-");
    }

    public void screenshot(ScreenshotMode mode, String file) {
        $screenshotDispatcher = new EScreenShotDispatcher(this);
        try {
            $screenshotDispatcher.screenshot(mode, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load(String url) {
        
        frameworkSettingsProtection();
        
        long start = System.currentTimeMillis();
        $webDriver.navigate().to(url);

        if(!shouldSuppressAllDelays()) {
            int timeToDelay = (int) (getMinimumPageLoadTime() - (start - System.currentTimeMillis()));
            if (timeToDelay > 0) {
                try {
                    sleep(timeToDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        
    }
    
    public void setOption(Common option, Object value) {
        $playbackOptionsSet.setOption(option, value);
        log(Reporting.LOG_OPTIONS, "Global common option " + option.toString() +
                " was set to " + value.toString() + ".");
    }
    
    public void setOption(Common option) {
        setOption(option, true);
    }

    public void setOptions(Common... options) {
        for(Common option : options) {
            setOption(option, true);
        }
    }

    public void setOption(ScreenshotMode mode) {
            $screenshotMode = mode;
    }

    public void setOption(ScreenshotPoint point) {
        $screenshotPoint = point;
    }


    public void setOption(AdvancedOptions option, Object value) {
        $advancedOptionsSet.setOption(option, value);
        log(Reporting.LOG_OPTIONS, "Advanced option " + option.toString() +
                " was set to " + value.toString() + ".");
    }

    public void setOption(AdvancedOptions option) {
        setOption(option, true);
    }

    public void setOptions(AdvancedOptions... options) {
        for(AdvancedOptions option : options) {
            setOption(option, true);
        }
    }

    private void log(Reporting item, String text) {
        $dispatcher.log(item, text);
    }

    public void setOption(Reporting option, Object value) {
        $reportingOptionsSet.setOption(option, value);
    }

    public void setOption(Reporting option) {
        $reportingOptionsSet.setOption(option, true);
    }

    public void setOptions(Reporting... options) {
        for(Reporting option : options) {
            setOption(option, true);
        }
    }

    public ScreenshotMode getScreenShotMode() {
        return $screenshotMode;
    }

    /*static ScreenshotArea _getScreenShotArea(String sid) {
        return $screenShotArea.get(sid);
    }*/

    public ScreenshotPoint getScreenShotPoint() {
        return $screenshotPoint;
    }
    
    public Object getOption(Common option) {
        return $playbackOptionsSet.getOption(option);
    }

    public Object getOption(Reporting option) {
        return $reportingOptionsSet.getOption(option);
    }

    public Object getOption(AdvancedOptions option) {
        return $advancedOptionsSet.getOption(option);
    }

    boolean shouldReportToTestng() {
        return (boolean) $reportingOptionsSet.getOption(Reporting.REPORT_TO_TESTNG);
    }
    
    private boolean shouldSuppressAllDelays() {
        return (boolean) $playbackOptionsSet.getOption(Common.SUPPRESS_DELAYS);
    }

    public int getPageLoadTimeout() {
        return (int) $advancedOptionsSet.getOption(AdvancedOptions.PAGE_LOAD_TIMEOUT);
    }

    public int getMinimumPageLoadTime() {
        return (int) $advancedOptionsSet.getOption(AdvancedOptions.MINIMUM_PAGE_TIME);
    }
    
    private boolean requireElementVisible() {
        return (boolean) $playbackOptionsSet.getOption(Common.REQUIRE_VISIBLE);
    }

    
    public int getResolveTimeout() {
        return (int) $playbackOptionsSet.getOption(Common.RESOLVE_TIMEOUT);
    }

    public int getRetryInterval() {
        return (int) $playbackOptionsSet.getOption(Common.RETRY_INTERVAL);
    }

    private boolean shouldLockToWindow() {
        return (boolean) $playbackOptionsSet.getOption(Common.WINDOW_LOCKING);
    }
    
    private int getSearchScrollDelay() {
        return (int) $playbackOptionsSet.getOption(Common.SEARCHSCROLL_RESOLVE);
    }
    
    private double getSearchScrollFactor() {
        return (double) $playbackOptionsSet.getOption(Common.SEARCHSCROLL_FACTOR);
    }
    
    private int getSearchScrollHeight() {
        return (int) $playbackOptionsSet.getOption(Common.SEARCHSCROLL_HEIGHT);
    }
    
    private boolean isLoadOnDemand() {
        return (boolean) $advancedOptionsSet.getOption(AdvancedOptions.LOAD_ON_DEMAND);
    }
    
    private boolean shouldHandleAlerts() {
        return (boolean) $playbackOptionsSet.getOption(Common.HANDLE_ALERTS);
    }
    
    private AlertOptions getAlertAction() {
        return (AlertOptions) $playbackOptionsSet.getOption(Common.ALERTS_ACTION);
    }

    static String createDirectory(String directory) {
        // create directory from
        (new File(directory)).mkdirs();
        return directory + "/";
    }

    public static String decryptDES(String string) {
        Crypto crypto = new Crypto();
        crypto.setKey($DESKey);
        return (crypto.decryptDES(string));
    }

    public static String encryptDES(String string) {
        Crypto crypto = new Crypto();
        crypto.setKey($DESKey);
        return crypto.encryptDES(string);
    }
    
    public EElement findElement(By locator) {
        return find(locator);
    }

    public List<EElement> findElements(By locator) {

        getWebDriver().manage().timeouts().implicitlyWait(getResolveTimeout(), TimeUnit.MILLISECONDS);

        List<WebElement> webElements = getWebDriver().findElements(locator);
        List<EElement> elements = new ArrayList<>();
        
        for(WebElement webElement : webElements) {
            
            EElement element = new EElement(this);
            element.setWebElement(webElement);
            
            elements.add(element);
        }

        frameworkSettingsProtection();
        
        return elements;
    }

    public EElement find(By locator) {
        return new EElement(this, locator);
    }

    public EElement ifind(By locator) {
        EElement element = new EElement(this, locator);
        element.load();
        return element;
    }

    public String getTestsResultsDir() {
        return $resultsDir;
    }

    private boolean handleAlerts() {

        try {
            Alert alert = getWebDriver().switchTo().alert();
            if(getAlertAction()== AlertOptions.DISMISS)
                alert.dismiss();
            else
                alert.accept();

        } catch (Exception e) {}

        return true;
    }
    
    public String getErbiumRootDir() {
        return $testParamsSet.getOption(TestParams.ERBIUM_ROOT);
    }
    
    public String getProjectName() {
        return $testParamsSet.getOption(TestParams.PROJECT_NAME);
    }
    
    public String getProjectDirectory() {
        return $testParamsSet.getOption(TestParams.PROJECT_DIRECTORY);
    }

    public String getTestEnvironment() {
        return $testParamsSet.getOption(TestParams.TEST_ENVIRONMENT);
    }

    public String getTestName() {
        return $testParamsSet.getOption(TestParams.TEST_NAME);
    }

    public String getTestsResultsDirectory() {
        return $testParamsSet.getOption(TestParams.RESULTS_DIRECTORY);
    }

    public String getTestsEnvironment() {
        return $testParamsSet.getOption(TestParams.TEST_ENVIRONMENT);
    }

    //@Override
    public Object executeScript(String s, Object... objects) {
        return ((JavascriptExecutor) getWebDriver()).executeScript(s, objects);
    }

    
    //@Override
    public Object executeAsyncScript(String s, Object... objects) {
        return ((JavascriptExecutor) getWebDriver()).executeAsyncScript(s, objects);
    }
    

    //@Override
    public EDriverManage manage() {
        return new EDriverManage(this);
    }
    
    //@Override
    public void get(String url) {
        try {
            load(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //@Override
    public WebDriver.Navigation navigate() {
        return getWebDriver().navigate();
    }
    
    //@Override
    public String getCurrentUrl() {
        return getWebDriver().getCurrentUrl();
    }
    
    //@Override
    public String getTitle() {
        return getWebDriver().getTitle();
    }
    
    //@Override
    public String getPageSource() {
        return getWebDriver().getPageSource();
    }
    
    //@Override
    public void quit() {
        getWebDriver().quit();
    }
    
    //@Override
    public String getWindowHandle() {
        return getWebDriver().getWindowHandle();
    }
    
    //@Override
    public Set<String> getWindowHandles() {
        return getWebDriver().getWindowHandles();
    }
    
    //@Override
    public WebDriver.TargetLocator switchTo() {
        return getWebDriver().switchTo();
    }

    public enum SearchScroll {
        SCROLL_FACTOR,
        SCROLL_DOWN,
        SCROLL_UP,
        SCROLL_TO_TOP_FIRST
    }
}
