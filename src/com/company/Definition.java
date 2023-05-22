package com.company;

import java.util.Arrays;

public class Definition {
    private String dict;
    private String dictType;
    private int year;
    private String[] text;

    public Definition(String dict, String dictType, int year, String[] text) {
        this.dict = dict;
        this.dictType = dictType;
        this.year = year;
        this.text = text;
    }

    public String getDict() {
        return dict;
    }

    public String getDictType() {
        return dictType;
    }

    public String[] getText() {
        return text;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Definition{");
        sb.append("dict='").append(dict).append('\'');
        sb.append(",\ndictType='").append(dictType).append('\'');
        sb.append(",\nyear=").append(year);
        sb.append(",\ntext=").append(text == null ? "null" : Arrays.asList(text).toString());
        sb.append("}\n");
        return sb.toString();
    }
}
