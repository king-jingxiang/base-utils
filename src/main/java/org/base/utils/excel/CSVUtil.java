package org.base.utils.excel;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.base.utils.text.CsvUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * @Auther: jinxiang
 * @Date: 2018/12/3 15:27
 * @Description:
 */
public class CSVUtil {

    private static final Logger logger = LoggerFactory.getLogger(CSVUtil.class);


    /**
     * data转csv文件流
     *
     * @param keys
     * @param data
     * @return
     */
    public synchronized static BufferedInputStream renderToCSV(List<String> keys, List<Map<String, String>> data) throws IOException {
        if (keys == null || keys.isEmpty() || data == null || data.isEmpty()) {
            return null;
        }
        // todo 并发问题
        BufferedWriter writer = Files.newBufferedWriter(Paths.get("tmp.csv"));
        String[] head = new String[keys.size()];
        keys.toArray(head);
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(head));
        //data
        data.forEach(t -> {
            try {
                Object[] objects = keys.stream().map(k -> t.getOrDefault(k, "")).toArray();
                csvPrinter.printRecord(objects);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        });
        csvPrinter.flush();
        return new BufferedInputStream(new FileInputStream("tmp.csv"));
    }

    /**
     * data转csv文件流
     *
     * @param keys
     * @param data
     * @return
     */
    public static BufferedInputStream renderToString(List<String> keys, List<Map<String, String>> data) throws IOException {
        if (keys == null || keys.isEmpty() || data == null || data.isEmpty()) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        //head
        String[] head = new String[keys.size()];
        keys.toArray(head);
        sb.append(CsvUtil.toCsvString(head));
        //data
        data.forEach(t -> {
            Object[] objects = keys.stream().map(k -> t.getOrDefault(k, "")).toArray();
            sb.append("\n");
            sb.append(CsvUtil.toCsvString(objects));

        });
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(sb.toString().getBytes());
        return new BufferedInputStream(byteArrayInputStream);
    }


    /**
     * T 所有属性需要添加ExcelExport标签
     *
     * @param dataSource
     * @param <T>
     * @throws IOException
     */
    public static <T> BufferedInputStream renderToExcelNoAnnotation(List<T> dataSource) throws IOException {

        if (dataSource == null || dataSource.isEmpty()) {
            return null;
        }
        ResultData resultData = ExcelDateHelper.buildResultDataNoAnnotation(dataSource);
        return renderToString(resultData.getKeys(), resultData.getData());
    }

}
