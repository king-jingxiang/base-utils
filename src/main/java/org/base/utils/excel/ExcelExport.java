package org.base.utils.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelExport {

    //导出的排序 升序
    int index() default 0;

    //导出的列名
    String name();

    //是否是日期
    boolean isDateTime() default false;

    //日期的格式
    String dateFormat() default "yyyy-MM-dd hh:mm:ss";
}
