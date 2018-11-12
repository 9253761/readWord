/*
 * Copyright (c) 2016-2018 fafa.com.cn. All Rights Reserved.
 */

package com.study;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * 这是用poi来得到word的目录结构
 *
 * @author Cll
 * @date 2018/11/12 10:51
 * @since 1.0.0
 */

public class PoiGetResult {


    public static String ReadWord(String filePath) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            //创建存储word文档的对象
            HWPFDocument doc = new HWPFDocument(new FileInputStream(filePath));
            //用来获得word文档内容
            Range range = doc.getRange();
            //获取段落
            int garagraph = range.numParagraphs();
            //遍历段落获取数据
            for (int i = 0; i < garagraph; i++) {
                //获取整段内容
                Paragraph pph = range.getParagraph(i);
                //获取段落大纲
                int level = pph.getLvl();
                if (level<=8){//标题等级从0开始，默认9级，9以上则把所有的文档输出
                        stringBuffer.append(pph.text());
                        System.out.println(stringBuffer.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuffer.toString().trim();
    }
}
