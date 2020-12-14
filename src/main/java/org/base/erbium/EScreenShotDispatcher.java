package org.base.erbium;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.Reporter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EScreenShotDispatcher {

    private final EDriver $driver;
    private String $file;
    private Thread $fileThread;
    private Thread $base64Thread;

    enum ThreadType {
        File,
        Base64
    }


    EScreenShotDispatcher(EDriver driver) {
        $driver = driver;
    }

    Thread getThread(ThreadType type) {
        Thread thread = null;
        switch(type) {
            case File:
                thread = $fileThread;
                break;
            case Base64:
                thread = $base64Thread;
                break;
        }
        return thread;
    }

    void finalise(ThreadType type) {
        Thread thread = null;
        switch(type) {
            case File:
                thread = $fileThread;
                break;
            case Base64:
                thread = $base64Thread;
                break;
        }
        try {
            thread.interrupt();
        } catch (Exception ignore) {

        }
        if(type==ThreadType.File)
            $fileThread = null;
        else
            $base64Thread = null;
    }

    public class EScreenShotFileDispatcherEngine extends Thread {
        @Override
        public void run() {
            WebDriver webDriver = $driver.getWebDriver();
            TakesScreenshot ts = ((TakesScreenshot) webDriver);
            String outputDir = $driver.getTestsResultsDir() + "/screenshots/";
            $file = $file + "_" +
                    (new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS")).format(new Date()) + ".png";
            File source = ts.getScreenshotAs(OutputType.FILE);
            try {
                FileUtils.copyFile(source, new File(outputDir + $file));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if ($driver.shouldReportToTestng()) {
                Reporter.log("Saved screenshot named '" + $file + "' to results directory.");
                // TO DO: add link to physical file on system
            }
        }

    }

    public class EScreenShotBase64DispatcherEngine extends Thread {
        @Override
        public void run() {
            WebDriver webDriver = $driver.getWebDriver();
            TakesScreenshot ts = ((TakesScreenshot) webDriver);
            String base64File = ts.getScreenshotAs(OutputType.BASE64);
            if ($driver.shouldReportToTestng()) {
                Reporter.log("<div><p>Screenshot</p>" +
                        "<img src=\"data:image/png;base64, " + base64File + "\" alt=\"Screenshot\"" +
                        "width=100%; height=100%; object-fit: contain; />" + //
                        "</div>"); //object-fit: contain
            }
        }
    }

    void screenshot(ScreenshotMode mode) {
        screenshot(mode, "unnamed-");
    }

    void screenshot(ScreenshotMode mode, String file) {
        boolean $isInLineAndFileOrFileOnly = mode.equals(ScreenshotMode.InLineAndFile)
                || mode.equals(ScreenshotMode.File);
        boolean $isInLineAndFileOrInLineOnly = mode.equals(ScreenshotMode.InLineAndFile)
                || mode.equals(ScreenshotMode.InLine);
        String base64File = null;
        WebDriver webDriver = $driver.getWebDriver();
        TakesScreenshot ts = ((TakesScreenshot) webDriver);
        if ($isInLineAndFileOrFileOnly) {
            $file = file;
            $fileThread = new EScreenShotFileDispatcherEngine();
            $fileThread.start();
        }
        if ($isInLineAndFileOrInLineOnly) {
            $base64Thread = new EScreenShotBase64DispatcherEngine();
            $base64Thread.start();
        }
        while(!($fileThread.isAlive() || $base64Thread.isAlive())) {}
        $fileThread = null;
        $base64Thread = null;
    }

}
