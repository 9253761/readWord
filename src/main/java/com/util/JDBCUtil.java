/*
 * Copyright (c) 2016-2018 fafa.com.cn. All Rights Reserved.
 */

package com.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 封装连接数据库的类
 *
 * @author Cll
 * @date 2018/11/12 14:55
 * @since 1.0.0
 */

public class JDBCUtil {
    private CallableStatement callableStatement; //创建CallableStatement对象
    private Connection conn;
    private PreparedStatement pst; /*继承自PreparedStatement,支持带参数的SQL操作;
                                          支持调用存储过程,提供了对输出和输入/输出参数(INOUT)的支持*/
    private ResultSet res;

    /**
     * 建立数据库连接
     *
     * @return 数据库连接
     */
    public static Connection getConn() {
        PropertiesUtils.loadFile("application.properties");
        String driver = PropertiesUtils.getPropertyValue("driverClassName");
        String url = PropertiesUtils.getPropertyValue("url");
        String username = PropertiesUtils.getPropertyValue("username");
        String password = PropertiesUtils.getPropertyValue("password");
        Connection conn = null;
        try {
            //加载数据库驱动连接
            Class.forName(driver);
            //获取连接
            conn = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            System.out.println("加载驱动错误");
            System.out.println(e.getMessage());
            e.printStackTrace();

        } catch (SQLException e) {
            System.out.println("连接失败，密码或用户名错误");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 更新的语句
     *
     * @param sql    sql的语句
     * @param params 参数数组，没有则为null
     * @return 受影响的行数
     */
    public int excuteUpdate(String sql, Object[] params) {
        //受影响的行数
        int affectedLine = 0;
        try {

            //获得连接
            conn = this.getConn();
            //执行sql语句,调用的sql语句
            pst = conn.prepareStatement(sql);
            //参数赋值
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    pst.setObject(i + 1, params[i]);
                }
            }
            //执行语句
            affectedLine = pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //释放资源
            try {
                closeAll();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
        return affectedLine;
    }

    /**
     * SQL 查询将查询结果直接放入ResultSet中
     *
     * @param sql    sql语句
     * @param params 参数数组，没有则为null
     * @return 返回一个结果集
     */
    public ResultSet excuteQueryRS(String sql, Object[] params) {
        //连接数据库
        conn = this.getConn();
        try {
            //    执行数据库语句
            pst = conn.prepareStatement(sql);
            //参数赋值
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    pst.setObject(i + 1, params[i]);
                }
            }
            //执行语句
            res = pst.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return res;
    }

    /**
     * 查询单个
     *
     * @param sql    sql语句
     * @param params 参数数组，没有则null
     * @return 返回一个结果对象
     */
    public Object excuteQuerySingle(String sql, Object[] params) {
        Object obj = null;
        //连接数据库
        conn = this.getConn();
        try {
            //调用sql
            pst = conn.prepareStatement(sql);
            //参数赋值
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    pst.setObject(i + 1, params[i]);
                }
                res = pst.executeQuery();
                if (res.next()) {
                    obj = res.getObject(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
            try {
                closeAll();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
        return obj;
    }

    /**
     * 获取结果集，并将结果放在List中
     *
     * @param sql    sql语句
     * @param params 参数数组，没有则为null
     * @return list 结果集
     */
    public List<Object> excuteQuery(String sql, Object[] params) {
        //执行sql得到结果集
        ResultSet rs = excuteQueryRS(sql, params);
        //创建一个ResultSetMetaData对象
        ResultSetMetaData rmd = null; /*这个类完成了查询结果信息和结果中的列的各种信息*/
        //收集列数
        int columnCount = 0;
        try {
            rmd = rs.getMetaData();
            //获得结果集列数
            columnCount = rmd.getColumnCount();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        List<Object> list = new ArrayList<Object>();
        //将ResultSet的结果放入list中
        try {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<String, Object>();
                for (int i = 1; i <= columnCount; i++) {
                    map.put(rmd.getColumnLabel(i), rs.getObject(i));
                }
                list.add(map);//每一个map代表一条记录，把所有记录存在list中

            }
        } catch (SQLException e1) {
            e1.printStackTrace();
            System.out.println(e1.getMessage());
        } finally {
            //释放资源
            try {
                closeAll();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 存储过程带有一个输出参数的方法
     *
     * @param sql         存储过程语句
     * @param params      参数数组
     * @param outParamPos 输出参数位置
     * @param SqlType     输出参数类型
     * @return 输出参数的值
     */
    public Object excuteQuery(String sql, Object[] params, int outParamPos, int SqlType) {
        Object object = null;
        conn = this.getConn();
        try {
            // 调用存储过程
            // prepareCall:创建一个 CallableStatement 对象来调用数据库存储过程。
            callableStatement = conn.prepareCall(sql);

            // 给参数赋值
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    callableStatement.setObject(i + 1, params[i]);
                }
            }

            // 注册输出参数
            callableStatement.registerOutParameter(outParamPos, SqlType);

            // 执行
            callableStatement.execute();

            // 得到输出参数
            object = callableStatement.getObject(outParamPos);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            // 释放资源
            try {
                closeAll();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }

        return object;
    }


    /**
     * 关闭所有连接
     *
     * @throws SQLException 异常处理
     */
    private void closeAll() throws SQLException {
        //关闭结果集对象
        if (res != null) {
            res.close();
        }
        //关闭PreparedStatement
        if (pst != null) {
            pst.close();
        }

        //关闭callableStatement对象
        if (callableStatement != null) {
            callableStatement.close();
        }
        //关闭连接
        if (conn != null) {
            conn.close();
        }
    }
}


