package cn.sa.demo.fragment.app;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import cn.sa.demo.R;
import cn.sa.demo.fragment.BaseAppFragment;

/**
 *
 */
public class Frg_app_2 extends BaseAppFragment {


    public Frg_app_2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_frg_app_2, container, false);
        v.findViewById(R.id.tv_app_frg_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"Frg_app_2 点击",Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

}
