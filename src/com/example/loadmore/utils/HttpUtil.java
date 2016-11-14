package com.example.loadmore.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

//加载网上数据
public class HttpUtil {
  private String url;
  private String string;

  public HttpUtil(String url) {
      super();
      this.url = url;
  }
  public String getNewsData() throws Exception{
      HttpClient client=new DefaultHttpClient();
      HttpGet get=new HttpGet(url);
      HttpResponse execute = client.execute(get);
      if(execute.getStatusLine().getStatusCode()==200){
          HttpEntity entity = execute.getEntity();
          string = EntityUtils.toString(entity, "utf-8");
          
      }
      return string;
  }
}