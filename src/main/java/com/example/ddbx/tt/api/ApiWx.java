/*
 * @Description: 快车道-万巡对接群,万巡自动呼叫API
 * @Admin ²⁰¹⁹ ,用call服务器测试好了：
 * 第一步：获取token
 * https://call.vanxtec.com/admin/api/token?secret=fe91f9fe7a4e17f64bc21bb28dc5f461&apiid=285
 * 第二步：新建呼叫任务
 * https://call.vanxtec.com/admin/api/pushPh?accessToken=d548fd6b60683afd579611df59bf6040b401a90e
 * 其中body数据
 * @Author: tt
 * @Date: 2019-01-21 13:38:26
 * @LastEditTime: 2019-02-12 17:18:34
 * @LastEditors: tt
 */
package com.example.ddbx.tt.api;

import com.example.ddbx.tt.data.TtMap;
import com.example.ddbx.tt.tool.HttpTools;
import com.example.ddbx.tt.tool.Tools;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ApiWx {
  public final String apiid = "285";
  public final String secret = "fe91f9fe7a4e17f64bc21bb28dc5f461";
  /**
   * @description: 获取token
   * @param {type} 
   * @return: 
   */
  public Object getToken(){
    Object result = null;
    String url = "https://call.vanxtec.com/admin/api/token";
    Map<String,Object> data = new HashMap<>();
    data.put("apiid", apiid);
    data.put("secret", secret);
    String mpStr = HttpTools.httpClientGet(url, data, "UTF-8",null);
    System.out.println(mpStr);
    result = Tools.jsonDeCode(mpStr);
    return result;
  }
  /**
   * @description: 呼叫
   * @param {type} 
   * @return: 
   */
  public Object docall(String token){
    String url = "https://call.vanxtec.com/admin/api/pushPh?accessToken=";
    url = url +token;
    Map<String,Object> map = new HashMap<>();
    map.put("total",1);
    map.put("cid", 880);
    ArrayList<Object> details = new ArrayList<>();
    String [] ss= {"13559130130","18950388428","18106060029"};//要呼叫的号码列表
    for (String s :ss){
      TtMap smp = new TtMap();
      smp.put("phone", s);
      details.add(smp);
    }
    map.put("detail", details);
    TtMap headers = new TtMap();
    headers.put("Access-Control-Allow-Origin","*");
    headers.put("Access-Control-Allow-Headers","X-Requested-With,Content-Type");
    headers.put("Content-Type","raw");
    String mpstr = HttpTools.httpClientPost_String(url, Tools.jsonEncode(map), "UTF-8", headers);
    System.out.println(mpstr);
    return mpstr; 
  }
}