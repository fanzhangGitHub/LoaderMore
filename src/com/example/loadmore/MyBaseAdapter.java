package com.example.loadmore;

import java.util.List;

import com.example.loadmore.bean.Bean.DataEntity;
import com.lidroid.xutils.BitmapUtils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyBaseAdapter extends BaseAdapter {
    Context context;
    List<com.example.loadmore.bean.Bean.DataEntity> data;
    public MyBaseAdapter(Context context, List<DataEntity> datas) {
        // TODO Auto-generated constructor stub
        this.context=context;
        this.data=datas;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return data.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(int arg0, View convertView, ViewGroup arg2) {
        // TODO Auto-generated method stub
        viewholder vh;
        if(convertView==null){
            convertView=LayoutInflater.from(context).inflate(R.layout.xlistview_item, null);
            vh=new viewholder();
            vh.image=(ImageView) convertView.findViewById(R.id.image_src);
            vh.title=(TextView) convertView.findViewById(R.id.text_title);
            convertView.setTag(vh);
        }else{
            vh=(viewholder) convertView.getTag();
        }
        BitmapUtils bitmapUtils=new BitmapUtils(context);
        bitmapUtils.display(vh.image, data.get(arg0).picUrl);
        vh.title.setText(data.get(arg0).title);
        return convertView;
    }
    class viewholder{
        ImageView image;
        TextView title;
    }
}
