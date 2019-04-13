package cn.sa.demo.fragment;


import android.app.Fragment;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.sa.demo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseAppFragment extends Fragment {


    public BaseAppFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_base_app, container, false);
    }

}
