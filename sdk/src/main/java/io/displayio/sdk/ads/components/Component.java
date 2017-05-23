package io.displayio.sdk.ads.components;

import java.util.HashMap;

/**
 * Created by jynx on 25/12/16.
 */

public class Component {
    protected HashMap<String, String> sOptions = new HashMap<>();
    protected HashMap<String, Integer> iOptions = new HashMap<>();
    protected HashMap<String, Boolean> features = new HashMap<>();


    public void setFeature(String name, Boolean enabled) {
        features.put(name, enabled);
    }
    protected Boolean isFeatureSet(String feature) {
        return features.containsKey(feature) && features.get(feature);
    }
    public void setOption(String name, String value) {
        sOptions.put(name, value);
    }
    public void setOption(String name, int value) {
        iOptions.put(name, value);
    }
    public String getStrOption(String name) {
        return sOptions.get(name);
    }
    public int getIntOption(String name) {
        return iOptions.get(name);
    }
    protected boolean hasStringOption(String option) {
        return sOptions.containsKey(option);
    }
    protected boolean hasIntOption(String option) {
        return iOptions.containsKey(option);
    }
}
