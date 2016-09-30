package com.yuqf.fengmomusic.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.interfaces.OnRecyclerViewItemClickListener;
import com.yuqf.fengmomusic.interfaces.PlayIndexChangedListener;
import com.yuqf.fengmomusic.media.Music;
import com.yuqf.fengmomusic.media.MusicPlayer;
import com.yuqf.fengmomusic.ui.adapter.LinearLayoutItemDecoration;
import com.yuqf.fengmomusic.ui.adapter.MusicRecyclerViewAdapter;

public class PlaylistFragment extends Fragment implements PlayIndexChangedListener {
    private final String logTag = "MusicListFragment";
    private View rootView;
    private RecyclerView recyclerView;
    private MusicRecyclerViewAdapter adapter;
    private Music music;

    public PlaylistFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MusicPlayer.getInstance().addChangedListener(this);
        adapter = new MusicRecyclerViewAdapter(false, false);
        adapter.addMusicList(MusicPlayer.getInstance().getPlayingMusics());
        adapter.setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                MusicPlayer.getInstance().setPlayingMusics(adapter.getMusicList());
                MusicPlayer.getInstance().play(position);
            }

            @Override
            public void onItemDownloadClick(View view, int position) {

            }
        });
    }

    @Override
    public void onPlayingIndexChange(Music music, int curPosition, int oldPosition) {
        adapter.updateItemState(music);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_playlist, container, false);
            recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.addItemDecoration(new LinearLayoutItemDecoration(false));
            final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setAdapter(adapter);
            adapter.updateItemState(music);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        } else {
            ViewGroup parentView = (ViewGroup) rootView.getParent();
            if (parentView != null) {
                parentView.removeView(rootView);
            }
        }
        return rootView;
    }

    public void setMusic(Music music) {
        this.music = music;
        if(rootView!=null)
            adapter.updateItemState(music);
    }
}
