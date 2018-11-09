package org.base.utils.excel;

import com.google.common.collect.Lists;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by xianxueliang on 16/3/9.
 */
public class ExcelDateHelper {

    public static <T> ResultData buildResultData(List<T> dataSource) {

        Class clazz = dataSource.stream().findFirst().orElse(null).getClass();
        Field[] fields = clazz.getDeclaredFields();
        //保存
        List<FieldAnnotation> fieldAnnotations = Lists.newArrayList();

        //field和注解对应
        for (Field field : fields) {
            boolean hasAnnotation = field.isAnnotationPresent(ExcelExport.class);

            if (hasAnnotation) {
                ExcelExport annotation = field.getAnnotation(ExcelExport.class);
                FieldAnnotation fieldAnnotation = new FieldAnnotation(field, annotation, annotation.index());
                fieldAnnotations.add(fieldAnnotation);
            }
            //todo 不加标签直接输出
//            else {
//
//                FieldAnnotation fieldAnnotation = new FieldAnnotation(field, field.getName());
//                fieldAnnotations.add(fieldAnnotation);
//            }
        }
        //排序 然后开始组装数据
        List<FieldAnnotation> sortedList = fieldAnnotations.stream().sorted((a, b) -> a.getIndex() - b.getIndex()).collect(Collectors.toList());
        List<Map<String, String>> result = new ArrayList<>();
        dataSource.forEach(model -> {
            //每一条数据组成一行
            Map<String, String> map = new HashMap();
            sortedList.forEach(fieldAnnotation -> {
                Field field = fieldAnnotation.getField();
                ExcelExport annotation = fieldAnnotation.getAnnotation();
                field.setAccessible(true);
                try {
                    String value = "";
                    PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
                    Method getMethod = pd.getReadMethod();//获得get方法
                    Object fieldValue = getMethod.invoke(model);//执行get方法返回一个Object
                    if (fieldValue != null)
                        value = fieldValue.toString();
                    if (annotation.isDateTime() && fieldValue != null) {

                        Date date = (Date) field.get(model);
                        java.text.DateFormat format1 = new SimpleDateFormat(annotation.dateFormat());
                        value = format1.format(date);

                    }
                    map.put(fieldAnnotation.getAnnotation().name(), value);
                } catch (Exception e) {
                }
            });
            result.add(map);
        });
        List<String> keys = sortedList.stream().map(fieldAnnotation -> fieldAnnotation.getAnnotation().name()).collect(Collectors.toList());
        ResultData resultData = new ResultData();
        resultData.setData(result);
        resultData.setKeys(keys);
        return resultData;
    }
}






