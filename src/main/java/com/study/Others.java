/*
 * Copyright (c) 2016-2018 fafa.com.cn. All Rights Reserved.
 */

package com.study;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/**
 * @author Cll
 * @date 2018/11/11 19:20
 * @since 1.0.0
 */

public class Others {
    public static void readBookMarksByWordOrWps(String filename, String strChoose) {
        ActiveXComponent app = null;
        Dispatch doc = null;
        try {
            //microsoft Office 方式
            if ("2".equals(strChoose)) {
                app = new ActiveXComponent("Word.Application");
            }
            //WPS 方式
            if ("3".equals(strChoose)) {
                app = new ActiveXComponent("KWPS.Application");
            }
            app.setProperty("Visible", new Variant(false));
            Dispatch docs = app.getProperty("Documents").toDispatch();
            doc = Dispatch.call(docs, "Open", filename).toDispatch();

            //所有表格
            Dispatch tables = Dispatch.get(doc, "Tables").toDispatch();
            //获取表格总数
            int tableCount = Dispatch.get(tables, "Count").getInt();
            System.out.println("表格数" + tableCount);
            Dispatch table = null;
            //删除所有表格（删除第一个表格后，第二个表格会变成第一表格）
            for (int i = 0; i < tableCount; i++) {
                table = Dispatch.call(tables, "Item", new Variant(1)).toDispatch();
                Dispatch.call(table, "Delete");
            }
            // 获取大纲列表
            Dispatch paragraphs = Dispatch.get(doc, "Paragraphs").toDispatch();
            int count = Dispatch.get(paragraphs, "Count").getInt();//大纲数量
            //当前大纲等级层次
            int tempLevel = 0;
            //从前往后获取大纲
            for (int i = 0; i <= count; i++) {
                //当前大纲项
                Dispatch paragraph = Dispatch.call(paragraphs, "Item", new Variant(i + 1)).toDispatch();
                //大纲等级
                int level = Dispatch.get(paragraph, "OutlineLevel").getInt();
                Dispatch paragraphRange = Dispatch.get(paragraph, "Range").toDispatch();
                //一般目录不会超过4级，4级往后是内容，可以跳过
                if (level <= 4) {
                    if (tempLevel == 0) {
                        tempLevel = level;
                    }
                    //标题编号
                    Dispatch listFormat = Dispatch.get(paragraphRange, "ListFormat").toDispatch();
                    String listString = "";
                    try {
                        listString = Dispatch.get(listFormat, "ListString").toString();
                    } catch (Exception e) {
                        System.out.println("没有listFormat属性的标题：" + Dispatch.get(paragraphRange, "Text").toString().replaceAll("\\\r|\\\f", ""));
                    }
                    //标题
                    String title = Dispatch.get(paragraphRange, "Text").toString().replaceAll("\\\r|\\\f", "");
                    //可能会存在一些为空的隐藏的大纲，text是为空的
                    if (title == null || ("").equals(title)) {
                        continue;
                    }
                    title = listString + title;
                    //索引的页码
                    int page = Dispatch.call(paragraphRange, "information", 1).getInt();

                    System.out.println(title + "…………" + page + "level:" + level);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                Dispatch.call(doc, "Close", false);
                if (app != null) {
                    app.invoke("Quit", new Variant[]{});
                    app = null;
                }
            } catch (Exception e2) {
                System.out.println("关闭ActiveXComponent异常");
            }
        }
    }

}
