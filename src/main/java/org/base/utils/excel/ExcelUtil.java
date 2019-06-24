package org.base.utils.excel;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.base.utils.mapper.JacksonUtil;
import org.base.utils.time.DateFormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ExcelUtil {

    private static final Logger logger = LoggerFactory.getLogger(ExcelUtil.class);
    private static Map<String, HSSFCellStyle> styles;

    private static Map<String, HSSFCellStyle> createStyles(HSSFWorkbook wb) {
        Map<String, HSSFCellStyle> styles = new HashMap<String, HSSFCellStyle>();
        HSSFCellStyle styleBorder = createBorderedStyle(wb);
        styles.put("styleBorder", styleBorder);
        HSSFCellStyle styleHeader = createHeaderStyle(wb);
        styles.put("styleHeader", styleHeader);
        return styles;
    }

    private static HSSFCellStyle createBorderedStyle(HSSFWorkbook wb) {
        HSSFCellStyle style = wb.createCellStyle();
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());

        return style;
    }

    private static HSSFCellStyle createHeaderStyle(HSSFWorkbook wb) {
        HSSFCellStyle style = createBorderedStyle(wb);
        HSSFFont font = wb.createFont();
        font.setFontHeightInPoints((short) 14);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

        style.setFont(font);
        return style;
    }

    /**
     * data转excel文件流
     *
     * @param keys
     * @param data
     * @return
     */
    public static BufferedInputStream renderToExcel(List<String> keys, List<Map<String, String>> data) {
        if (keys == null || keys.isEmpty() || data == null || data.isEmpty()) {
            return null;
        }

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("data");
        styles = createStyles(wb);
        HSSFRow row = sheet.createRow((short) 0);
        for (int i = 0; i < keys.size(); i++) {
            String fieldName = keys.get(i);
            HSSFCell cell = row.createCell(i);
            cell.setCellValue(fieldName);
            cell.setCellStyle((CellStyle) styles.get("styleHeader"));
        }

        for (int j = 0; j < data.size(); j++) {
            Map<String, String> map = data.get(j);
            HSSFRow r = sheet.createRow(j + 1);

            for (int i = 0; i < keys.size(); i++) {
                String fieldName = keys.get(i);
                Object value = map.get(fieldName);
                HSSFCell cell = r.createCell(i);
                cell.setCellValue(value != null ? value.toString() : null);

            }
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            wb.write(os);
        } catch (IOException e) {
            logger.error("Write WorkSheet Error", e);
        }

        byte[] content = os.toByteArray();
        InputStream is = new ByteArrayInputStream(content);
        BufferedInputStream bis = null;
        return new BufferedInputStream(is);

    }


    /**
     * T 所有属性需要添加ExcelExport标签
     *
     * @param dataSource
     * @param <T>
     * @throws IOException
     */
    public static <T> BufferedInputStream renderToExcelByAnnotation(List<T> dataSource) throws IOException {

        if (dataSource == null || dataSource.isEmpty()) {
            return null;
        }
        ResultData resultData = ExcelDateHelper.buildResultData(dataSource);
        return renderToExcel(resultData.getKeys(), resultData.getData());
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
        return renderToExcel(resultData.getKeys(), resultData.getData());
    }
    /**
     * 读取excel转obj
     *
     * @param is
     * @param ext   xls还是xlsx
     * @param clazz
     * @return
     */
    public static <T> List<T> parseExcel(InputStream is, String ext, Class<T> clazz) throws IOException, IllegalAccessException, InstantiationException {
        // 结果集
        List<T> result = new ArrayList<>();
        // 字段属性
        Field[] fields = clazz.getDeclaredFields();
        //日期映射关系
        Map<String, String> fieldDateMap = Arrays.stream(fields)
                .collect(Collectors.toMap(k -> k.getAnnotation(ExcelExport.class).name(), v -> v.getAnnotation(ExcelExport.class).dateFormat()));
        // 字段映射关系
        Map<String, Field> fieldNameMap = Arrays.stream(fields).collect(Collectors.toMap(k -> k.getAnnotation(ExcelExport.class).name(),
                v -> v));

        Workbook wb = null;
        if ("xlsx".equals(ext))
            wb = new XSSFWorkbook(is);
        else
            wb = new HSSFWorkbook(is);

        Sheet sheet = wb.getSheetAt(0);
        int n = sheet.getPhysicalNumberOfRows();

        Row header = sheet.getRow(0);
        List<String> column = parseRow(header);
        if (column == null || column.isEmpty())
            return null;
        for (int i = 1; i < n; i++) {
            Row row = sheet.getRow(i);
            List<String> values = parseRow(row);
            if (values == null || values.isEmpty())
                continue;
            Map<String, String> rowDataMap = new HashMap<>();
            for (int j = 0; j < column.size(); j++) {
                String excelField = column.get(j);
                String value = values.size() >= j + 1 ? values.get(j) : null;
                Field objField = fieldNameMap.getOrDefault(excelField, null);
                rowDataMap.put(objField.getName(), value);
            }
            T obj = JacksonUtil.fromMapToObj(rowDataMap, clazz);
            result.add(obj);
        }
        return result;
    }

    /**
     * excel文件转List<Map<K,V>>
     *
     * @param is
     * @param ext
     * @return
     */
    public static List<Map<String, String>> parseExcelStream(InputStream is, String ext) {
        List<Map<String, String>> ret = new ArrayList<Map<String, String>>();

        Workbook wb = null;
        try {
            if ("xlsx".equals(ext))
                wb = new XSSFWorkbook(is);
            else
                wb = new HSSFWorkbook(is);

            Sheet sheet = wb.getSheetAt(0);
            int n = sheet.getPhysicalNumberOfRows();

            Row header = sheet.getRow(0);
            List<String> fields = parseRow(header);
            if (fields == null || fields.isEmpty())
                return null;

            for (int i = 1; i < n; i++) {
                Row row = sheet.getRow(i);
                List<String> values = parseRow(row);
                if (values == null || values.isEmpty())
                    continue;

                Map<String, String> map = new HashMap<String, String>();
                for (int j = 0; j < fields.size(); j++) {
                    String field = fields.get(j);
                    String value = values.size() >= j + 1 ? values.get(j) : null;
                    map.put(field, value);
                }
                ret.add(map);
            }
            return ret;
        } catch (IOException e) {
            logger.error("创建WorkSheet错误", e);
            return null;
        }
    }

    private static List<String> parseRow(Row row) {
        if (row == null)
            return null;

        List<String> ret = new ArrayList<String>();

        //Iterator<Cell> it = row.cellIterator();
        int colNum = row.getPhysicalNumberOfCells();
        /*while (it.hasNext()) {*/
        for (int i = 0; i < colNum; i++) {
            Cell cell = row.getCell(i);
            if (null == cell) {
                ret.add("");
                continue;
            }
            String responseValue = null;

            switch (cell.getCellType()) { // 根据cell中的类型来输出数据
                case HSSFCell.CELL_TYPE_NUMERIC:// 数字类型
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式
                        SimpleDateFormat sdf = null;
                        if (cell.getCellStyle().getDataFormat() == 22) {
                            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        } else {// 日期
                            sdf = new SimpleDateFormat("yyyy-MM-dd");
                        }
                        Date date = cell.getDateCellValue();
                        responseValue = sdf.format(date);

                    } else {
                        double value = cell.getNumericCellValue();
                        CellStyle style = cell.getCellStyle();
                        DecimalFormat format = new DecimalFormat();
                        String temp = style.getDataFormatString();
                        // 单元格设置成常规
                        if (temp.equals("General")) {
                            format.applyPattern("#");
                        }
                        responseValue = format.format(value);
                    }
                    break;
                case HSSFCell.CELL_TYPE_STRING:
                    responseValue = cell.getStringCellValue();
                    break;
                case HSSFCell.CELL_TYPE_BOOLEAN:
                    responseValue = Boolean.toString(cell.getBooleanCellValue());
                    break;
                case HSSFCell.CELL_TYPE_FORMULA:
                    responseValue = cell.getCellFormula();
                    break;
                default:
                    responseValue = "";
                    break;
            }
            ret.add(responseValue);
        }

        return ret;
    }
}
