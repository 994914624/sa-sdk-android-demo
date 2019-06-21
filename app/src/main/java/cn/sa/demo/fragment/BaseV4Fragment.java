package cn.sa.demo.fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.sa.demo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseV4Fragment extends Fragment {


    private static final String TAG = "nice ";//过滤关键字 nice
    private static final String TAG_2 = " ---> :  ";
    public BaseV4Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_base_supportv4, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setTag(R.id.fragment_root_view,this);
        Log.i(TAG+getClass().getSimpleName()+TAG_2,"onViewCreated");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG+getClass().getSimpleName()+TAG_2,"onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG+getClass().getSimpleName()+TAG_2,"onPause");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.i(TAG+getClass().getSimpleName()+TAG_2,"onHiddenChanged");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.i(TAG+getClass().getSimpleName()+TAG_2,"setUserVisibleHint");
    }

}
