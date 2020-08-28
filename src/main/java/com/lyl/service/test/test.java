package com.lyl.service.test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lyl.service.util.PureNetUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

public class test {


    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    @Test
    public void testForValue1(){
        String key = "zszxz";
        String value = "知识追寻者";
        redisTemplate.opsForValue().set(key, value);
    }


    public static void main(String[] args) {






//        System.out.println(getIDBycityName("香港"));
        System.out.println(GetTodayTemperatureByCity("苏州"));
    }
    public static String excute(){
        String url="http://v.juhe.cn/weather/citys?key=4e3ee7c3cd86815832c01a2f5379b1e0";//接口URL
        //PureNetUtil是一个封装了get和post方法获取网络请求数据的工具类
        return PureNetUtil.get(url);//使用get方法
    }

    public static String excute(String cityName){
        String url=//此处以返回json格式数据示例,所以format=2,以根据城市名称为例,cityName传入中文
                "http://v.juhe.cn/weather/index?cityname="+cityName+"&key=4e3ee7c3cd86815832c01a2f5379b1e0";
        return PureNetUtil.get(url);//通过工具类获取返回数据
    }

    public static String GetTodayTemperatureByCity(String city) {
        String result=excute(city);
        if(result!=null){
            JSONObject obj=JSONObject.parseObject(result);
            /*获取返回状态码*/
            result=obj.getString("resultcode");
            /*如果状态码是200说明返回数据成功*/
            if(result!=null&&result.equals("200")){
                result=obj.getString("result");
                //此时result中数据有多个key,可以对其key进行遍历,得到对个属性
                obj=JSONObject.parseObject(result);
                //今日温度对应的key是today
                result=obj.getString("today");
                obj=JSONObject.parseObject(result);
                //今日温度对应当key是temperature
                result=obj.getString("temperature");
                return result;
            }
        }
        return result;
    }


    public static String getIDBycityName(String cityName) {
        String result=excute();//返回接口结果,得到json格式数据
        if(result!=null){
            JSONObject obj=JSONObject.parseObject(result);
            result=obj.getString("resultcode");//得到返回状态码
            if(result!=null&&result.equals("200")){//200表示成功返回数据
                result=obj.getString("result");//得到城市列表的json格式字符串数组
                JSONArray arr=JSONArray.parseArray(result);
                for(Object o:arr){//对arr进行遍历
                    //将数组中的一个json个数字符串进行解析
                    obj=JSONObject.parseObject(o.toString());
                    /*此时obj如 {"id":"2","province":"北京","city":"北京","district":"海淀"}*/
                    //以city这个key为线索判断所需要寻找的这条记录
                    result=obj.getString("district");
                    //防止输入城市名不全,如苏州市输入为苏州,类似与模糊查询
                    if(result.equals(cityName)||result.contains(cityName)){
                        result=obj.getString("id");//得到ID
                        return result;
                    }
                }
            }
        }
        return result;
    }
}
