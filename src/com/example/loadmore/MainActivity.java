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
       //�ҿؼ�
        findViewById();
       //ֱ��ճ
        my_xlist.setPullLoadEnable(true);//��������ˢ��
        my_xlist.setPullRefreshEnable(true);//���ü����¼�����д��������
        my_xlist.setXListViewListener(this);//��������ˢ��



        //�ս������ҳ��
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
//�ҿؼ�
    private void findViewById() {
        // TODO Auto-generated method stub
        my_xlist = (XListView)findViewById(R.id.my_xlist);
        my_xlist.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
            
                //�˴���ת��ֵ
              //ע�����Ҫ��ʾ���� ��ֵʱҪposition-1;������ΪXlistview����������ˢ��ռ����һ��item,�����������صģ����Դ�ֵʱע�����1��
           Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
           intent.putExtra("web", datas.get(position-1).url);
           startActivity(intent);
            }
        });
    }
    //����ˢ�����������Ǹ�����
    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
      //������������ˢ��ʱ��������handler
        new Thread(){
            @Override
            public void run() {
                try {
                    //ˢ�¼���
                    getdata();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
       //��ǰʱ��
        onLoad();
    }
   //�鿴�����Ǹ�
    @Override
    public void onLoadMore() {
        // TODO Auto-generated method stub
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                   //���ظ���
                    getdataflush();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //ˢ��
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }.start();
        //��ǰʱ��
        onLoad();
    }
    /*
     * 
     * ����ʱ��
     */
    @SuppressLint("SimpleDateFormat") 
    private void onLoad() {
        my_xlist.stopRefresh();
        my_xlist.stopLoadMore();
        // �������ڸ�ʽ
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // ��ȡ��ǰϵͳʱ��
        String nowTime = df.format(new Date(System.currentTimeMillis()));
        // �ͷ�ʱ��ʾ����ˢ��ʱ�ĵ�ǰʱ��
        my_xlist.setRefreshTime(nowTime);
    }
    /*
     * 
     * ������ݣ�
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
                 //Xlistview  ����

                adapter=new MyBaseAdapter(MainActivity.this,datas);
                my_xlist.setAdapter(adapter);
            }
        });

    }
    /**
     * 
     * ��������
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
