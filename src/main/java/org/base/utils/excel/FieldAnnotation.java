package org.base.utils.excel;

import java.lang.reflect.Field;

public class FieldAnnotation {

    private Field field;
    private ExcelExport annotation;
    private Integer index;

    public FieldAnnotation(Field field, ExcelExport annotation, Integer index) {
        this.field = field;
        this.annotation = annotation;
        this.index = index;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public ExcelExport getAnnotation() {
        return annotation;
    }

    public void setAnnotation(ExcelExport annotation) {
        this.annotation = annotation;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

}
