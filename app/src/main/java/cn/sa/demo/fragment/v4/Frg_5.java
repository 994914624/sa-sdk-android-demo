package cn.sa.demo.fragment.v4;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import cn.sa.demo.R;
import cn.sa.demo.fragment.BaseV4Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class Frg_5 extends BaseV4Fragment {


    public Frg_5() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_frg_5, container, false);
        v.findViewById(R.id.tv_frg_5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"Frg_5 点击",Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

}
