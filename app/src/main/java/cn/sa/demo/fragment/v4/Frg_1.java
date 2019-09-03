package cn.sa.demo.fragment.v4;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import cn.sa.demo.R;
import cn.sa.demo.fragment.BaseV4Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class Frg_1 extends BaseV4Fragment {


    public Frg_1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_frg_1, container, false);
        v.findViewById(R.id.tv_frg_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"Frg_1 点击",Toast.LENGTH_SHORT).show();
            }
        });

        initRecycleView(v);
        return v;
    }

    /**
     * RecycleView
     */
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private void initRecycleView(View view) {
        mLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        ArrayList<String> data = new ArrayList<>();
        data.add("RecyclerView item1");
        data.add("RecyclerView item2");
        data.add("RecyclerView item3");
        mAdapter = new MyRecyclerViewAdapter(data);
        mRecyclerView = view.findViewById(R.id.view_recycleView);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
    }


    public static class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

        private ArrayList<String> mData;

        MyRecyclerViewAdapter(ArrayList<String> data) {
            this.mData = data;
        }

        public void updateData(ArrayList<String> data) {
            this.mData = data;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public MyRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item, parent, false);
            setViewFragmentName(v,"cn.sa.demo.fragment.v4.Frg_1");
            return new MyRecyclerViewAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mTv.setText(mData.get(position));
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(v.getContext(), "RecyclerView click", Toast.LENGTH_SHORT).show();
//                }
//            });
            holder.mTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            holder.mTv2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView mTv2;
            TextView mTv;
            ViewHolder(View itemView) {
                super(itemView);
                mTv = (TextView) itemView.findViewById(R.id.tv_recycleView_item);
                mTv2 = (TextView) itemView.findViewById(R.id.tv_recycleView_item_2);
            }
        }
    }

    public static void setViewFragmentName(View view,String fragmentName) {
        try {
            if (TextUtils.isEmpty(fragmentName)||view == null) {
                return;
            }
            view.setTag(R.id.sensors_analytics_tag_view_fragment_name, fragmentName);
            ViewGroup viewGroup;
            if(view instanceof ViewGroup){
                viewGroup = (ViewGroup) view;
            } else {
                return;// 非 ViewGroup return
            }

            final int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; ++i) {
                final View child = viewGroup.getChildAt(i);
                child.setTag(R.id.sensors_analytics_tag_view_fragment_name, fragmentName);
                if (child instanceof ViewGroup && !(child instanceof ListView ||
                        child instanceof GridView ||
                        child instanceof Spinner ||
                        child instanceof RadioGroup)) {
                    setViewFragmentName(child, fragmentName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
