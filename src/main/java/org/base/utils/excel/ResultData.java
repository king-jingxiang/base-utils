package org.base.utils.excel;

import java.util.List;
import java.util.Map;

public class ResultData {
    private List<Map<String, String>> data;
    private List<String> keys;

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    public List<Map<String, String>> getData() {
        return data;
    }

    public void setData(List<Map<String, String>> data) {
        this.data = data;
    }
}
