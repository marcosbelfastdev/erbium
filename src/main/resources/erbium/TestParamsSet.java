package org.base.erbium;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class TestParamsSet {

    private final Map<TestParams, Object> $options;

    public TestParamsSet() {
        $options = new HashMap<>();
        $options.putIfAbsent(TestParams.ERBIUM_ROOT, System.getProperty("user.home") + "/Erbium Tests");
        $options.putIfAbsent(TestParams.PROJECT_NAME, "Unnamed Project");
        $options.putIfAbsent(TestParams.TEST_NAME, "Unnamed Test");
        $options.putIfAbsent(TestParams.TEST_ENVIRONMENT, "Unnamed Environment");
        $options.putIfAbsent(TestParams.PROJECT_DIRECTORY, null);
        $options.putIfAbsent(TestParams.RESULTS_DIRECTORY, null);
    }

    public TestParamsSet(Map<TestParams, Object> options) {
        $options = new HashMap<>();
        $options.putAll(options);
    }

    protected void setOption(TestParams option, Object value) {

        if($options.containsKey(option))
            $options.replace(option, value);
        else
            $options.put(option, value);

    }

    protected String getOption(TestParams option) {

        Object value = null;

        if($options.containsKey(option))
            value = $options.get(option);

        return (String) value;
    }

    protected void addOption(TestParams option, Object value) {
        $options.put(option, value);
    }

    protected Set<TestParams> keySet() {
        return $options.keySet();
    }

    protected void clearOptions() {
        $options.clear();
    }

    protected TestParamsSet duplicate() {

        Map<TestParams, Object> options = new HashMap<>();
        options.putAll($options);
        return new TestParamsSet($options);
    }

    String getErbiumRootDir() {
        return getOption(TestParams.ERBIUM_ROOT);
    }

    String getTestName() {
        return getOption(TestParams.TEST_NAME);
    }



    @Override
    public String toString() {
        return $options.toString();
    }

}
