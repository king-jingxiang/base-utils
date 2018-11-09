package org.base.utils.excel;

import org.apache.commons.io.FileUtils;
import org.base.utils.time.DateUtil;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ExcelUtilTest {

    @Test
    public void renderToExcel() throws IOException {
        List<Person> dataSource = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            dataSource.add(new Person());
        }
        BufferedInputStream bufferedInputStream = ExcelUtil.renderToExcelByAnnotation(dataSource);
        FileUtils.copyInputStreamToFile(bufferedInputStream, new File("person.xls"));
    }

    @Test
    public void renderToExcelByAnnotation() {
    }

    @Test
    public void parseExcel() throws Exception{
        List<Person> person = ExcelUtil.parseExcel(FileUtils.openInputStream(new File("person.xls")), "xls", Person.class);
        person.forEach(t -> System.out.println(t));
    }

    @Test
    public void parseExcelStream() {
    }
}

class Person {
    @ExcelExport(name = "姓名", index = 0)
    String name;
    @ExcelExport(name = "年龄")
    Integer age;
    @ExcelExport(name = "资产")
    Double asset;
    @ExcelExport(name = "地址")
    String addr;
    @ExcelExport(name = "生日", isDateTime = true, dateFormat = "yyyy-MM-dd")
    Date birth;
    @ExcelExport(name = "身份")
    Boolean admin;

    public Person() {
        int i = new Random().nextInt(100);
        this.name = "name" + i;
        this.age = i;
        this.asset = i * i * 25.5;
        this.addr = "addr" + i;
        this.birth = DateUtil.subDays(new Date(), i);
        this.admin = (i % 2 == 0);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Double getAsset() {
        return asset;
    }

    public void setAsset(Double asset) {
        this.asset = asset;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", asset=" + asset +
                ", addr='" + addr + '\'' +
                ", birth=" + birth +
                ", admin=" + admin +
                '}';
    }
}
