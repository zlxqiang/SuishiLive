package com.suishi.live.app.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.suishi.live.app.R;

/**
 * 短视频
 */
public class ShortVideoFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private static ShortVideoFragment fragment=null;

    public ShortVideoFragment() {
        // Required empty public constructor
    }

    /**
     */
    public static ShortVideoFragment newInstance() {
        if(fragment==null) {
            fragment = new ShortVideoFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_short_video, container, false);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

}
