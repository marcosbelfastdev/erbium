package com.github.marcosbelfastdev.erbium;

import org.openqa.selenium.By;

import java.io.*;
import java.util.*;

public class ObjectMap {

    private Properties $prop = null;
    private Map<String, String> $rawMap = new HashMap<>();
    private Map<String,By> $locatorMap = new HashMap<>();

    public ObjectMap(String file) throws Exception {

        try {
            this.$prop = new Properties();
            InputStream is = new FileInputStream(file);
            $prop.load(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        $rawMap = buildRawMap();
        $locatorMap = buildLocatorMap();

    }

    public ObjectMap(File file) throws Exception {

        try {
            this.$prop = new Properties();
            InputStream is = new FileInputStream(file);
            $prop.load(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        $rawMap = buildRawMap();
        $locatorMap = buildLocatorMap();

    }

    public By get(String mappedName) {
        return $locatorMap.get(mappedName);
    }


    public String getLocatorString(String mappedName) {
        String[] rawLocatorStringParts = $rawMap.get(mappedName).split("::");
        return rawLocatorStringParts[0];
    }

    private Set<Object> getAllKeys() {
        Set<Object> keys = $prop.keySet();
        return keys;
    }

    private String getPropertyValue(String key) throws Exception {
        String result = null;
        
        if($prop.containsKey(key))
            result = $prop.getProperty(key);
        else
            throw new Exception("This mapped locator entry does not exist in the object map.");
        return result;
    }
    
    private Map<String, String> buildRawMap() throws Exception {
        Map<String,String> rawMap = new HashMap<>();
        Set<Object> keys = getAllKeys();
        for(Object key : keys) {
            rawMap.put(key.toString(), getPropertyValue(key.toString()));
            //System.out.println(key.toString() + ":" + getPropertyValue(key.toString()));
        }
        return rawMap;
    }

    private Map<String,By> buildLocatorMap() {
        Map<String,By> locatorMap = new HashMap<>();

        Iterator it = $rawMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();

            // get value and split
            String[] value = ((String) pair.getValue()).split("::");
            
            /* debug
            for(int i=0; i<value.length; i++) {
                System.out.println("i="+i+", value="+value[i]);
            }
            // end debug*/

            // get both parts of the value: the locator string and the locator type
            String locatorString = value[0];
            String locatorType = value[1];

            // create an actual locator
            By locator = null;
            switch (locatorType) {
                case "xpath":
                    locator = By.xpath(locatorString);
                    break;
                case "link":
                    locator = By.linkText(locatorString);
                    break;
                case "id":
                    locator = By.id(locatorString);
                    break;
                case "css":
                    locator = By.cssSelector(locatorString);
                    break;
            }

            // add the mapped name and the actual locator to the hashmap
            String mappedNameKey = (String) pair.getKey();
            locatorMap.put(mappedNameKey, locator);

            //it.remove(); // avoids a ConcurrentModificationException
        }
        return locatorMap;
    }

    void printRawMap() {
        System.out.println($rawMap.toString());
    }

    void printLocatorMap() {
        System.out.println($locatorMap.toString());
    }

}
