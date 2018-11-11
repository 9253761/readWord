/*
 * Copyright (c) 2016-2018 fafa.com.cn. All Rights Reserved.
 */

package com.study;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/**
 * 学习java如何读取word目录大纲
 *
 * @author Cll
 * @date 2018/11/11 14:04
 * @since 1.0.0
 */

public class ReadWordMark {
    public static void main(String[] args) throws Exception {
        //读取的路径
        String filePath = "C:/Users/Cll/Desktop/test.doc";
        //打开word应用程序
        ActiveXComponent word = new ActiveXComponent("Word.Application");
        //设置应用操作是文档不在明面上显示，不可见打开word
        word.setProperty("Visible", new Variant(false));
        // documents表示word的所有文档窗口，（word是多文档应用程序）
        Dispatch documents = word.getProperty("Documents").toDispatch();
        // 打开word文件
        Dispatch wordFile = Dispatch.invoke(documents, "Open", Dispatch.Method,
                new Object[]{filePath, new Variant(true),/* 是否进行转换ConfirmConversions */
                        new Variant(false)/* 是否只读 */},
                new int[1]).toDispatch();
         //获取所有段落
        Dispatch paragraphs = Dispatch.get(wordFile, "Paragraphs").toDispatch();
        //获取段落总数
        int paraCount = Dispatch.get(paragraphs, "Count").getInt();

        for (int i = 0; i < paraCount; ++i) {//遍历每一段
            //格式
            Dispatch paragraph = Dispatch
                    .call(paragraphs, "Item", new Variant(i + 1)).toDispatch();
            //获取大纲等级
            int outline = Dispatch.get(paragraph, "OutlineLevel").getInt();
            if (outline <= 9) {//如果小于等于10，那就是读取整篇word文档
//                System.out.println("大纲等级：" + outline);
                //大纲等级为标题
                Dispatch paraRange = Dispatch.get(paragraph, "Range")
                        .toDispatch();
               //获取标题
                String title = Dispatch.get(paraRange, "Text").toString();

                //获取文档的页码
                int pages = Integer.parseInt(
                        Dispatch.call(paraRange, "information", 1).toString());
                System.out.println(title);

//                System.out.println("标题页码：" + pages);
            }

        }
        //关闭word文件
        Dispatch.call(wordFile, "Close", new Variant(true));
        //退出word程序
        Dispatch.call(word, "Quit");
    }

}
