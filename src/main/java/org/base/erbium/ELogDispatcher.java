package org.base.erbium;
import org.testng.Reporter;

public class ELogDispatcher {

    EDriver $driver;
    private Thread $logDispatcherEngine;

    ELogDispatcher(EDriver driver) {
        $driver = driver;
    }

    Thread getThread() {
        return $logDispatcherEngine;
    }

    void finalise() {
        try {
            $logDispatcherEngine.interrupt();
        } catch (Exception ignore) {

        }
        $logDispatcherEngine = null;
    }

    public class ELogDispatcherEngine extends Thread {
        Reporting $item;
        String $text;
        ELogDispatcherEngine(Reporting item, String text) {
            $item = item;
            $text = text;
        }

        @Override
        public void run() {
            switch($item) {
                case LOG_TEST_STEPS:
                    logTestSteps($text);
                    break;
                case LOG_WINDOWS_ACTIONS:
                    logWindowsActions($text);
                    break;
                case LOG_OPTIONS:
                    logOptions($text);
                    break;
                case LOG_FRAMES:
                    logFrames($text);
                    break;
                case LOG_WINDOWS_DETAILS:
                    logWindowsDetails($text);
                    break;
                case LOG_CONDITIONS:
                    logConditions($text);
                    break;
                case LOG_DETAILED_CONDITIONS:
                    logDetailedConditions($text);
                    break;
            }
        }
    }

    void log(Reporting item, String text) {
        if($logDispatcherEngine!=null) {
            while($logDispatcherEngine.isAlive()) {

            }
            $logDispatcherEngine = null;
        }
        $logDispatcherEngine = new ELogDispatcherEngine(item, text);
        $logDispatcherEngine.start();
    }

    void logTestSteps(String text) {
        if(!(boolean) $driver.getReportingOptions().getOption(Reporting.LOG_TEST_STEPS))
            return;
        // Log to TestNG
        if($driver.shouldReportToTestng())
            Reporter.log(text);
    }

    void logWindowsActions(String text) {
        if(!(boolean) $driver.getReportingOptions().getOption(Reporting.LOG_WINDOWS_ACTIONS))
            return;
        // Log to TestNG
        if($driver.shouldReportToTestng())
            Reporter.log(text);
    }

    void logOptions(String text) {
        if(!(boolean) $driver.getReportingOptions().getOption(Reporting.LOG_OPTIONS))
            return;
        // Log to TestNG
        if($driver.shouldReportToTestng())
            Reporter.log(text);
    }

    void logFrames(String text) {
        if(!(boolean) $driver.getReportingOptions().getOption(Reporting.LOG_FRAMES))
            return;
        // Log to TestNG
        if($driver.shouldReportToTestng())
            Reporter.log(text);
    }

    void logWindowsDetails(String text) {
        if(!(boolean) $driver.getReportingOptions().getOption(Reporting.LOG_WINDOWS_DETAILS))
            return;
        // Log to TestNG
        if($driver.shouldReportToTestng())
            Reporter.log(text);
    }

    void logConditions(String text) {
        if(!(boolean) $driver.getReportingOptions().getOption(Reporting.LOG_CONDITIONS))
            return;
        // Log to TestNG
        if($driver.shouldReportToTestng())
            Reporter.log(text);
    }

    void logDetailedConditions(String text) {
        if(!(boolean) $driver.getReportingOptions().getOption(Reporting.LOG_DETAILED_CONDITIONS))
            return;
        // Log to TestNG
        if($driver.shouldReportToTestng())
            Reporter.log(text);
    }
}
