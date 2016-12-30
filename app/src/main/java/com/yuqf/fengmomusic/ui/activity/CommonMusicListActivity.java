package com.yuqf.fengmomusic.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.base.BaseActivity;
import com.yuqf.fengmomusic.download.DownloaderNew;
import com.yuqf.fengmomusic.interfaces.OnRecyclerViewItemClickListener;
import com.yuqf.fengmomusic.media.Music;
import com.yuqf.fengmomusic.media.MusicPlayer;
import com.yuqf.fengmomusic.ui.adapter.LinearLayoutItemDecoration;
import com.yuqf.fengmomusic.ui.adapter.MusicRecyclerViewAdapter;
import com.yuqf.fengmomusic.ui.entity.GSonHotMusicList;
import com.yuqf.fengmomusic.ui.entity.RetrofitServices;
import com.yuqf.fengmomusic.ui.widget.MiniMusicPlayerView;
import com.yuqf.fengmomusic.utils.Global;
import com.yuqf.fengmomusic.utils.UrlHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.yuqf.fengmomusic.base.MyApplication.getContext;

public class CommonMusicListActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private MiniMusicPlayerView musicPlayer;
    private MusicRecyclerViewAdapter adapter;
    private final String logTag = "CommonMusicListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_common_music_list);

        initTopBars();
        showToolBar();
        setToolbarHomeAsUp();

        musicPlayer = (MiniMusicPlayerView) findViewById(R.id.media_player);
        MusicPlayer.getInstance().addPlayerListener(musicPlayer);

        Intent intent = getIntent();
        String kind = intent.getStringExtra(Global.INTENT_LOAD_MUSIC_KIND);
        Log.d(logTag, "the transferred kind: " + kind);
        if (kind.equals(Global.INTENT_LOAD_HOTRECOMMEND)) {
            Log.d(logTag, "start load hot recommend music list ...");
            final String itemId = intent.getStringExtra(Global.INTENT_HR_ITEM_ID);
            String name = intent.getStringExtra(Global.INTENT_HR_ITEM_NAME);

            if (!TextUtils.isEmpty(name)) {
                setToolbarTitle(name);
            }

            initRecycler();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    getHotMusicListById(itemId);
                }
            }).start();
        } else if (kind == Global.INTENT_LOAD_LOCAL) {
            /*本地音乐不再从这里获取*/
        }
    }

    private void initRecycler() {
        adapter = new MusicRecyclerViewAdapter(false, false);
        adapter.setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                MusicPlayer.getInstance().setPlayingMusics(adapter.getMusicList());
                MusicPlayer.getInstance().play(position);
            }

            @Override
            public void onItemDownloadClick(View view, int position) {
                Music music = adapter.getMusicList().get(position);
                DownloaderNew downloader = new DownloaderNew(music);
                Intent intent = new Intent(getContext(), DownloadActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new LinearLayoutItemDecoration(false));
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void getHotMusicListById(String itemId) {
        if (TextUtils.isEmpty(itemId)) return;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UrlHelper.Hot_Recommend_Music_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitServices.SearchService service = retrofit.create(RetrofitServices.SearchService.class);
        Call<GSonHotMusicList> call = service.getHotMusicList(itemId);
        call.enqueue(new Callback<GSonHotMusicList>() {
            @Override
            public void onResponse(Call<GSonHotMusicList> call, Response<GSonHotMusicList> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        adapter.addHotMusicList(response.body().getMusiclist());
                    }
                }
            }

            @Override
            public void onFailure(Call<GSonHotMusicList> call, Throwable t) {

            }
        });
    }
}
