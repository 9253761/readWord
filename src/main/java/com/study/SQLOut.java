/*
 * Copyright (c) 2016-2018 fafa.com.cn. All Rights Reserved.
 */

package com.study;

import com.util.JDBCUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 数据通过poi导出
 *
 * @author Cll
 * @date 2018/11/12 16:48
 * @since 1.0.0
 */

public class SQLOut {
    public static void main(String[] args) throws SQLException {
        JDBCUtil jdbcUtil = new JDBCUtil();
        String sql = "select * from user";
        ResultSet rs = jdbcUtil.excuteQueryRS(sql, null);
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("user");
        XSSFRow row = sheet.createRow(0);
        XSSFCell cell;
        cell = row.createCell(0);
        cell.setCellValue("ID");
        cell = row.createCell(1);
        cell.setCellValue("NAME");
        cell = row.createCell(2);
        cell.setCellValue("PASSWORD");
        cell = row.createCell(3);
        cell.setCellValue("AGE");
        int i = 1;
        while (rs.next()) {
            row = sheet.createRow(i);
            cell = row.createCell(0);
            cell.setCellValue(rs.getString("id"));
            cell = row.createCell(1);
            cell.setCellValue(rs.getString("name"));
            cell = row.createCell(2);
            cell.setCellValue(rs.getString("password"));
            cell = row.createCell(3);
            cell.setCellValue(rs.getInt("age"));
            i++;
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(new File("user.xlsx"));
            workbook.write(out);
            out.close();
            System.out.println("success");
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

    }
}
