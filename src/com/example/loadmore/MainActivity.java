package com.example.loadmore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.example.loadmore.bean.Bean;
import com.example.loadmore.bean.Bean.DataEntity;
import com.example.loadmore.utils.HttpUtil;
import com.example.loadmore.views.XListView;
import com.example.loadmore.views.XListView.IXListViewListener;
import com.google.gson.Gson;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity implements IXListViewListener{
    private XListView my_xlist;
    private int pageIndex = 10;
    private int n;
    private MyBaseAdapter adapter;
    private List<DataEntity> datas;
   private SharedPreferences spf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spf = getSharedPreferences("config", MODE_PRIVATE);
        n = spf.getInt("key", 1);                                     
       //找控件
        findViewById();
       //直接粘
        my_xlist.setPullLoadEnable(true);//设置下拉刷新
        my_xlist.setPullRefreshEnable(true);//设置监听事件，重写两个方法
        my_xlist.setXListViewListener(this);//设置上拉刷新



        //刚进入加载页面
        new Thread() {
            public void run() {
                try {
                    getdata();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        }.start();
    }
//找控件
    private void findViewById() {
        // TODO Auto-generated method stub
        my_xlist = (XListView)findViewById(R.id.my_xlist);
        my_xlist.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
            
                //此处跳转传值
              //注意如果要显示详情 传值时要position-1;解释因为Xlistview最上面下拉刷新占用了一个item,不过他是隐藏的，所以传值时注意减了1；
           Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
           intent.putExtra("web", datas.get(position-1).url);
           startActivity(intent);
            }
        });
    }
    //下拉刷新上拉加载那个方法
    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
      //如果想添加上拉刷新时间再在这handler
        new Thread(){
            @Override
            public void run() {
                try {
                    //刷新加载
                    getdata();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
       //当前时间
        onLoad();
    }
   //查看更多那个
    @Override
    public void onLoadMore() {
        // TODO Auto-generated method stub
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                   //加载更多
                    getdataflush();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //刷新
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }.start();
        //当前时间
        onLoad();
    }
    /*
     * 
     * 设置时间
     */
    @SuppressLint("SimpleDateFormat") 
    private void onLoad() {
        my_xlist.stopRefresh();
        my_xlist.stopLoadMore();
        // 设置日期格式
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 获取当前系统时间
        String nowTime = df.format(new Date(System.currentTimeMillis()));
        // 释放时提示正在刷新时的当前时间
        my_xlist.setRefreshTime(nowTime);
    }
    /*
     * 
     * 获得数据；
     */
    private void getdata() throws Exception {
        String URL = "http://api.avatardata.cn/WorldNews/Query?key=dd9e7cf06c6c4f589a7772b0d778d50a&page="+n+"&rows=10";
        HttpUtil util=new HttpUtil(URL);
        String newsData = util.getNewsData();
        Editor edit = spf.edit();
        edit.putInt("key", n);
        edit.commit();
        n=n+1;
        Gson gson=new Gson();
        Bean bean = gson.fromJson(newsData, Bean.class);
      
        List<DataEntity> data = bean.result;
        datas=data;
        runOnUiThread(new Runnable() {
            
            @Override
            public void run() {
                // TODO Auto-generated method stub
                 //Xlistview  适配

                adapter=new MyBaseAdapter(MainActivity.this,datas);
                my_xlist.setAdapter(adapter);
            }
        });

    }
    /**
     * 
     * 加载数据
     * @throws Exception 
     * 
     */
    private void getdataflush() throws Exception {
        pageIndex = pageIndex+1;
        String URL = "http://api.avatardata.cn/WorldNews/Query?key=dd9e7cf06c6c4f589a7772b0d778d50a&page=1&rows="+pageIndex;
        HttpUtil util=new HttpUtil(URL);
        String newsData = util.getNewsData();
        Gson gson=new Gson();
        Bean bean = gson.fromJson(newsData, Bean.class);
        List<DataEntity> dataload = bean.result;
        datas.addAll(dataload);
    }
    
}
