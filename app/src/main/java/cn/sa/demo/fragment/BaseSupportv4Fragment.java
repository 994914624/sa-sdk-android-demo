package cn.sa.demo.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.sa.demo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseSupportv4Fragment extends Fragment {


    public BaseSupportv4Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_base_supportv4, container, false);
    }

}
