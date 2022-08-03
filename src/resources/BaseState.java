package com.github.marcosbelfastdev.erbium;

import org.openqa.selenium.WebDriver;

import java.text.SimpleDateFormat;


public class BaseState {

    // base state fields
    private EDriver $driver;
    private String $baseStateUrl;
    private Boolean $javaHook;
    private final TestParamsSet $testParamsSet;

    public BaseState() {
        $testParamsSet = new TestParamsSet();
        $javaHook = true;
        $baseStateUrl = "about:blank";
    }

    public void noJavaHook() {
        $javaHook = false;
    }

    public void commitParams() {

        String projectDir = $testParamsSet.getErbiumRootDir() + "/" + $testParamsSet.getOption(TestParams.PROJECT_NAME) + "/";
        EDriver.createDirectory(projectDir);
        $testParamsSet.setOption(TestParams.PROJECT_DIRECTORY, projectDir);
        System.out.println("Project directory is: " + $testParamsSet.getOption(TestParams.PROJECT_DIRECTORY));
        
        if($testParamsSet.getOption(TestParams.TEST_NAME) == null)
            $testParamsSet.setOption(TestParams.TEST_NAME, "Unnamed Tests");
        
        if($testParamsSet.getOption(TestParams.TEST_ENVIRONMENT) == null)
            $testParamsSet.setOption(TestParams.TEST_ENVIRONMENT, "Unnamed Environment");

        if($testParamsSet.getOption(TestParams.RESULTS_DIRECTORY) == null) {
            System.out.println("Results directory is null... Working on it...");
            // maybe in the future, add options for directory name formation.
            String resultsDir = EDriver.createDirectory($testParamsSet.getOption(TestParams.PROJECT_DIRECTORY) +
                    $testParamsSet.getOption(TestParams.TEST_ENVIRONMENT) + "." +
                    $testParamsSet.getOption(TestParams.TEST_NAME) + "_" +
                    (new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss")).format(new java.util.Date()));
                    $testParamsSet.setOption(TestParams.RESULTS_DIRECTORY, resultsDir);
        }
    }
    
    public EDriver execute(WebDriver driver) {

        commitParams();
        $driver = new EDriver(driver);
        $driver.initDriverProperties();
        $driver.setTestParams($testParamsSet);

        if($baseStateUrl!=null)
            $driver.load($baseStateUrl);

        return $driver;
    }

    public String getBaseStateUrl() {
        return $baseStateUrl;
    }

    public void setBaseStateUrl(String url) {
        $baseStateUrl = url;
    }

    public void setErbiumRootDirectory(String directory) {
        $testParamsSet.setOption(TestParams.ERBIUM_ROOT, directory);
    }

    public void setProjectName(String name) {
        $testParamsSet.setOption(TestParams.PROJECT_NAME, name);
    }

    public void setTestName(String name) {
        $testParamsSet.setOption(TestParams.TEST_NAME, name);
    }

    public void setEnvironment(String environment) {
        $testParamsSet.setOption(TestParams.TEST_ENVIRONMENT, environment);
    }

}

