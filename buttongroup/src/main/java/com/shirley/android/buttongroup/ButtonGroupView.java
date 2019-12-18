package com.shirley.android.buttongroup;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * 自定义ButtonGroupView
 */
public class ButtonGroupView extends GridView {

    ButtonGroupView buttonGroupView;
    ButtonGroupAdapter adapter;
    ButtonOnClickListener buttonOnClickListener;

    Context context;
    List<ButtonGroupItemBean> data;

    public ButtonGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        buttonGroupView = this;
    }

    public ButtonGroupView(Context context) {
        super(context);
        this.context = context;
        buttonGroupView = this;
    }

    public ButtonGroupView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        buttonGroupView = this;
    }

    public void setButtonOnClickListener(ButtonOnClickListener buttonOnClickListener) {
        this.buttonOnClickListener = buttonOnClickListener;
    }

    /**
     * 对外接口，设置数据以及监听点击事件
     *
     * @param dataList 数据
     */
    public void setData(final List<ButtonGroupItemBean> dataList) {

        data = dataList;

        adapter = new ButtonGroupAdapter((Activity) context, data);
        buttonGroupView.setAdapter(adapter);

        // 设置button group的列数
        this.setNumColumns(data.size() > 5 ? 5 : data.size());

        // 执行异步线程加载图片
        new Task().execute(data);

        this.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                buttonOnClickListener.setButtonClickListener(position, data.get(position));
            }
        });
    }

    public interface ButtonOnClickListener {
        void setButtonClickListener(int position, ButtonGroupItemBean bean);
    }

    /**
     * 异步线程加载图片
     */
    class Task extends AsyncTask<List<ButtonGroupItemBean>, Integer, Void> {

        @Override
        protected Void doInBackground(List<ButtonGroupItemBean>[] lists) {
            for (int i = 0; i < lists[0].size(); i++){
                data.get(Integer.valueOf(i)).setImg(getImageInputStream(lists[0].get(i).getUrl(), i));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Message message = new Message();
            message.what = 0x123;
            handler.sendMessage(message);
        }
    }

    /**
     * 获取网络图片
     *
     * @param imageUrl 图片网络地址
     * @param position 当前处理的bean在dataList中的位置
     * @return Bitmap 返回位图
     */
    private Bitmap getImageInputStream(String imageUrl, int position) {
        String path = Environment.getExternalStorageDirectory().getPath() + "/Test";
        File file = new File(path);
        // 文件夹不存在，则创建它
        if (!file.exists()) {
            file.mkdir();
        }
        // 判断当前image是否存在
        File[] files = file.listFiles();
        boolean exist = false;
        if (files != null) {
            for (File f : files) {
                if (f.getName().indexOf(data.get(position).getImgName()) >= 0) {
                    // 已存在此文件
                    exist = true;
                    break;
                }
            }
        }

        Bitmap bitmap = null;
        if (!exist) {
            // 如果不存在，则重新获取
            URL url;
            HttpURLConnection connection;
            try {
                url = new URL(imageUrl);
                connection = (HttpURLConnection) url.openConnection();
                // 超时设置
                connection.setConnectTimeout(6000);
                connection.setDoInput(true);
                // 设置不适用缓存
                connection.setUseCaches(false);
                InputStream inputStream = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                saveImage(bitmap, path, position);
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // 本地存在，则直接获取，不用下载
            try {
                bitmap = BitmapFactory.decodeFile(path + "/" + data.get(position).getImgName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return bitmap;
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                buttonGroupView.setAdapter(adapter);
            }
        }
    };

    /**
     * 保存位图到本地
     *
     * @param bitmap
     * @param path   本地路径
     */
    private void saveImage(Bitmap bitmap, String path, int position) {

        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(path + "/" + data.get(position).getImgName());
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}