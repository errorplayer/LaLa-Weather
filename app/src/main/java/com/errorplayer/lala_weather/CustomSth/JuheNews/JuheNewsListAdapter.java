package com.errorplayer.lala_weather.CustomSth.JuheNews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.errorplayer.lala_weather.R;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by ErrorPlayer on 2018/5/23.
 */

public class JuheNewsListAdapter extends ArrayAdapter<JuheNewsListItem> {

    private int resourceId;

    public JuheNewsListAdapter(@NonNull Context context,
                               int textViewResourceId,
                               List<JuheNewsListItem> objects
                              )
    {
        super(context,  textViewResourceId,objects);
        this.resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position,
                        View convertView,
                        ViewGroup parent)
    {
        JuheNewsListItem jhni = getItem(position);
        View view;
        if (convertView == null)
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        else
            view = convertView;

        ImageView NewsPic = (ImageView)view.findViewById(R.id.juheNews_itemPic);
        TextView NewsContent = (TextView)view.findViewById(R.id.juheNews_itemContent);




        //NewsPic.setImageBitmap(bitmap);
        Picasso.get()
                .load(jhni.getImageId())
                //.placeholder(R.drawable.questionmark)
                //.error(R.drawable.error404)
                //.resize(10, 10)
                .into(NewsPic);
        //NewsPic.setImageResource(jhni.getImageId());
        NewsContent.setText(jhni.getNews_titleString());
        return view;

    }

    public Bitmap getHttpBitmap(String url){
        URL myFileURL;
        Bitmap bitmap=null;
        try{
            myFileURL = new URL(url);
            //获得连接
            HttpURLConnection conn=(HttpURLConnection)myFileURL.openConnection();
            //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
            conn.setConnectTimeout(6000);
            //连接设置获得数据流
            conn.setDoInput(true);
            //不使用缓存
            conn.setUseCaches(false);
            //这句可有可无，没有影响
            //conn.connect();
            //得到数据流
            InputStream is = conn.getInputStream();
            //解析得到图片
            bitmap = BitmapFactory.decodeStream(is);
            //关闭数据流
            is.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return bitmap;

    }
}
