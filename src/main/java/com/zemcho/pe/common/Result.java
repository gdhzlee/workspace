package com.zemcho.pe.common;

import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.*;

/*
 * http请求返回的最外层对象
 */
@Data
public class Result {

    //错误码
    private int code;

    //提示信息
    private String msg;

    //具体的内容, total,pageNum,list
    private Object info;

    public Result(int code, String msg, Object info) {
        this.code = code;
        this.msg = msg;
        this.info = info;
    }

    public Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.info = new HashMap<>();
    }

    public Result() {

    }

    public Result(Message message){
        this.code = message.getCode();
        this.msg = message.getMessage();
        this.info = -1;
    }

    public Result(Message message, Object info){
        this.code = message.getCode();
        this.msg = message.getMessage();
        this.info = info;
    }


    public static <T,K>  Map<String, Object> constructData(PageInfo<T> pageInfo, List<K> list) {
        Map<String, Object> data = new TreeMap<>();

        data.put("total", pageInfo.getTotal());
        data.put("pageNum", pageInfo.getPageNum());

        if (list == null) {
            data.put("list", new ArrayList<>());
        } else {
            data.put("list", list);
        }

        return data;
    }


    public static <T> Map<String, Object> constructData(PageInfo<T> pageInfo) {
        Map<String, Object> data = new TreeMap<>();

        data.put("total", pageInfo.getTotal());
        data.put("pageNum", pageInfo.getPageNum());

        if (pageInfo.getList() == null || pageInfo.getList().size() == 0) {

            data.put("list", new ArrayList<>());
        } else {

            data.put("list", pageInfo.getList());
        }

        return data;
    }

    public static <T> List<T> subList(List<T> list, int pageNum, int pageSize) {
        int size = list.size();
        int start = (pageNum - 1) * pageSize;
        int end = (pageNum - 1) * pageSize + pageSize;

        if (start > size) {
            start = size;
        }

        if (end > size) {
            end = size;
        }

        return list.subList(start, end);
    }

    public static <T> void initObjectId(List<T> list,int pageNum,int pageSize) {

        for (int i = 0; i < list.size(); i++) {

            T t = list.get(i);
            Class<?> o = t.getClass();
            try {
                Field id = o.getDeclaredField("id");
                id.setAccessible(true);
                id.set(t, (i + 1));
                list.set(i, t);
            } catch (NoSuchFieldException e) {
                try {
                    o = o.getSuperclass();
                    Field id = o.getDeclaredField("id");
                    id.setAccessible(true);
                    try {
                        id.set(t, (i + 1));
                    } catch (IllegalAccessException e1) {
                        e1.printStackTrace();
                    }
                    list.set(i, t);
                } catch (NoSuchFieldException e1) {
                    e1.printStackTrace();
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static <T> void initObjectId(List<T> list) {

        for (int i = 0; i < list.size(); i++) {

            T t = list.get(i);
            Class<?> o = t.getClass();
            try {
                Field id = o.getDeclaredField("id");
                id.setAccessible(true);
                id.set(t, (i + 1));
                list.set(i, t);
            } catch (NoSuchFieldException e) {
                try {
                    o = o.getSuperclass();
                    Field id = o.getDeclaredField("id");
                    id.setAccessible(true);
                    try {
                        id.set(t, (i + 1));
                    } catch (IllegalAccessException e1) {
                        e1.printStackTrace();
                    }
                    list.set(i, t);
                } catch (NoSuchFieldException e1) {
                    e1.printStackTrace();
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static <T> void initObjectId(T t) {

        Class<?> o = t.getClass();
        try {
            Field id = o.getDeclaredField("id");
            id.setAccessible(true);
            id.set(t, 1);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
