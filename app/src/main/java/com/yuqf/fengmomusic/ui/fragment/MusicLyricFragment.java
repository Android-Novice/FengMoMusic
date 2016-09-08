package com.yuqf.fengmomusic.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yuqf.fengmomusic.R;

public class MusicLyricFragment extends Fragment {

    public MusicLyricFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_music_lyric, container, false);
//        ReflectionImageView reflectionImageView = (ReflectionImageView) rootView.findViewById(R.id.reflection_image_view);
//        reflectionImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.single_music_bkg));
//        ReflectionImageView reflectionImageView1 = (ReflectionImageView) rootView.findViewById(R.id.reflection_image_view1);
//        reflectionImageView1.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.single_music_bkg));
        return rootView;
    }
}
