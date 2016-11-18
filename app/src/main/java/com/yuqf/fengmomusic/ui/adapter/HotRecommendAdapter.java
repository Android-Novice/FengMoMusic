package com.yuqf.fengmomusic.ui.adapter;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.base.MyApplication;
import com.yuqf.fengmomusic.interfaces.OnRecyclerViewItemClickListener;
import com.yuqf.fengmomusic.ui.entity.GSonHotRecommend;
import com.yuqf.fengmomusic.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class HotRecommendAdapter extends RecyclerView.Adapter<HotRecommendAdapter.HotRecommendHolder> {

    private List<GSonHotRecommend.HotRecommendSecond.HotRecommendList.HotRecommendItem> hotRecommendItemList;
    private LayoutInflater inflater;
    private OnRecyclerViewItemClickListener itemClickListener;

    public HotRecommendAdapter() {
        super();
        hotRecommendItemList = new ArrayList<>();
        inflater = LayoutInflater.from(MyApplication.getContext());
    }

    @Override
    public HotRecommendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recommend_recycler_view_item, parent, false);
        HotRecommendHolder holder = new HotRecommendHolder(itemView);
        itemView.setClickable(true);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(v, (int) v.getTag());
                }
            }
        });
        return holder;
    }

    public void setItemClickListener(OnRecyclerViewItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onBindViewHolder(HotRecommendHolder holder, int position) {
        GSonHotRecommend.HotRecommendSecond.HotRecommendList.HotRecommendItem item = hotRecommendItemList.get(position);
        holder.titleTV.setText(item.getName());
        holder.countTV.setText(item.getInFo());
        holder.itemView.setTag(position);
//        holder.gifImageView.setImageResource(R.drawable.file_download_white);
        final HotRecommendHolder curHolder = holder;
        new AsyncTask<String, Void, File>() {
            @Override
            protected File doInBackground(String... params) {
                String filePath = FileUtils.getHotRecommendCoverPath(params[0]);
                File file = new File(filePath);
                if (!file.exists()) {
                    HttpURLConnection connection = null;
                    try {
                        FileOutputStream fileStream = new FileOutputStream(file);

                        URL url = new URL(params[0]);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setConnectTimeout(20000);
                        connection.setReadTimeout(10000);
                        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            InputStream stream = connection.getInputStream();

                            byte[] bytes = new byte[1024];
                            int len;
                            while ((len = stream.read(bytes)) != -1) {
                                fileStream.write(bytes, 0, len);
                            }
                            stream.close();
                            fileStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        connection.disconnect();
                    }
                }
                return file;
            }

            @Override
            protected void onPostExecute(File file) {
                curHolder.gifImageView.setImageURI(Uri.fromFile(file));
            }
        }.execute(item.getPic());
    }

    @Override
    public int getItemCount() {
        return hotRecommendItemList.size();
    }

    public void setHotRecommendItemList(List<GSonHotRecommend.HotRecommendSecond.HotRecommendList.HotRecommendItem> hotRecommendItemList) {
        this.hotRecommendItemList = hotRecommendItemList;
        notifyDataSetChanged();
    }

    class HotRecommendHolder extends RecyclerView.ViewHolder {
        public GifImageView gifImageView;
        private TextView titleTV;
        private TextView countTV;

        public HotRecommendHolder(View itemView) {
            super(itemView);
            gifImageView = (GifImageView) itemView.findViewById(R.id.gif_iv_recommend);
            titleTV = (TextView) itemView.findViewById(R.id.recommend_item_title_tv);
            countTV = (TextView) itemView.findViewById(R.id.recommend_item_info_tv);
        }
    }
}
