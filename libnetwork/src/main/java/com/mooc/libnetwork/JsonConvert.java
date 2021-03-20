package com.mooc.libnetwork;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Type;

public class JsonConvert implements Convert {
    //默认的Json转 Java Bean的转换器
    @Override
    public Object convert(String response, Type type) {
        JSONObject jsonObject = JSON.parseObject(response);      //fastjson: parseObject(String) ==> 指定类型解析
        JSONObject data = jsonObject.getJSONObject("data");
        if (data != null) {
            Object data1 = data.get("data");
            return JSON.parseObject(data1.toString(), type);     //parseObject(String, Type) ==> 指定类型解析
        }
        return null;
    }
}