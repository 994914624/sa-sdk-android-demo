package cn.sa.demo.activity;


import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.tabs.TabLayout;
import com.growingio.android.sdk.collection.AbstractGrowingIO;
import com.growingio.android.sdk.collection.GrowingIO;
//import com.sensorsdata.risk_control.api.SARiskControlAPI;
//import com.sensorsdata.risk_control.api.callback.EditTextListener;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sa.demo.R;
import cn.sa.demo.utils.AccessibilityUtil;

/**
 * 常用控件的点击
 *
 * (Button、TextView、ImageView、ImageButton、CheckedTextView、SeekBar、RatingBar、Spinner、
 * SwitchCompat、ToggleButton、CheckBox、RadioButton、RadioGroup、Dialog、ContextMenu、OptionsMenu、
 * TabLayout、TabHost、LinearLayout、ListView、GridView、ExpandableListView、RecycleView)
 */
public class ViewActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        this.setTitle("常用控件的点击");
        initView();
    }

    private void initView() {
        // Button
        findViewById(R.id.view_btn).setOnClickListener(this);
        findViewById(R.id.view_btn).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // 长按 发出无障碍事件
                Toast.makeText(ViewActivity.this, "发出无障碍事件，点击当前页面的控件", Toast.LENGTH_SHORT).show();
                AccessibilityUtil.sendAccessibilityEvent(ViewActivity.this);
                return false;
            }
        });
        //EditText
        initEditText();
        // TextView
        findViewById(R.id.view_tv).setOnClickListener(this);
        // ImageView
        findViewById(R.id.view_img).setOnClickListener(this);
        // ImageButton
        findViewById(R.id.view_img_btn).setOnClickListener(this);
        // CheckedTextView
        initCheckedTextView();
        // SeekBar
        initSeekBar();
        // RatingBar
        initRatingBar();
        // Spinner
        initSpinner();
        // SwitchCompat
        initSwitchCompat();
        // ToggleButton
        initToggleButton();
        // CheckBox
        initCheckBox();
        // RadioButton
        initRadioButton();
        // RadioGroup
        initRadioGroup();
        // Dialog
        initDialog();
        // ContextMenu
        initContextMenu();
        // TabLayout
        initTabLayout();
        initTabLayout2();
        // TabHost
        initTabHost();
        initTabHost2();
        // LinearLayout
        initLinearLayout();
        // ListView
        initListView();
        // GridView
        initGridView();
        // ExpandableListView
        initExpandableListView();
        // RecycleView
        initRecycleView();
    }

    EditText mEditText = null;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initEditText() {
         mEditText = findViewById(R.id.view_edt);
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    return true;
                }

                return false;
            }
        });

        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });
        //追踪 EditText
        //GrowingIO.getInstance().trackEditText(mEditText);
        //mEditText.setTag(AbstractGrowingIO.GROWING_TRACK_TEXT, Boolean.valueOf(true));
        mEditText.setShowSoftInputOnFocus(false);//屏蔽软键盘
        handleEditText(mEditText);
    }

    /**
     * 风控 SDK 处理 EditText
     * @param editText EditText
     */
    private void handleEditText(EditText editText) {
//        final EditTextListener editTextListener = SARiskControlAPI.sharedInstance().trackEditTextEvent(this, editText, "输入框");
//        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    editTextListener.onFocused();
//                } else {
//                    editTextListener.deFocused();
//                }
//            }
//        });
//
//        editText.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                editTextListener.onKey(v, keyCode, event);
//                return false;
//            }
//        });
    }

    /*
     * CheckedTextView
     */
    private void initCheckedTextView() {
        final CheckedTextView checkedTextView = findViewById(R.id.view_ctv);
        checkedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ViewActivity.this, "CheckedTextView", Toast.LENGTH_SHORT).show();
                checkedTextView.toggle();
            }
        });
    }

    /**
     * SeekBar
     */
    private void initSeekBar() {
        SeekBar seekBar = findViewById(R.id.view_sb);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * RatingBar
     */
    private void initRatingBar() {
        RatingBar ratingBar = findViewById(R.id.view_ratingbar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                Toast.makeText(ViewActivity.this, "RatingBar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Spinner
     */
    private void initSpinner() {
        Spinner spinner = findViewById(R.id.view_spinner);
        String[] mItems = {"北京", "上海", "深圳", "美国", "清华"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ViewActivity.this, android.R.layout.simple_spinner_item, mItems);
        spinner.setAdapter(adapter);
        //spinner.setSelection(0,false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(ViewActivity.this, "Spinner:" + i, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * SwitchCompat
     */
    private void initSwitchCompat() {
        final SwitchCompat switchCompat1 = findViewById(R.id.view_sc_1);
        switchCompat1.setOnClickListener(new View.OnClickListener() {
            @Override
            @TargetApi(14)
            public void onClick(View view) {
                Toast.makeText(ViewActivity.this, "SwitchCompat1", Toast.LENGTH_SHORT).show();
                switchCompat1.setChecked(switchCompat1.isChecked());
            }
        });

        SwitchCompat switchCompat2 = findViewById(R.id.view_sc_2);
        switchCompat2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(ViewActivity.this, "SwitchCompat2", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * ToggleButton
     */
    private void initToggleButton() {
        final ToggleButton toggleButton = (ToggleButton) findViewById(R.id.view_tb);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ViewActivity.this, "" + toggleButton.isChecked(), Toast.LENGTH_SHORT).show();
                toggleButton.setChecked(toggleButton.isChecked());
            }
        });

        ToggleButton toggleButton2 = findViewById(R.id.view_tb_2);
        toggleButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(ViewActivity.this, "" + b, Toast.LENGTH_SHORT).show();

            }
        });
    }

    /**
     * CheckBox
     */
    private void initCheckBox() {
        findViewById(R.id.view_cb).setOnClickListener(this);
        CheckBox checkBox2 = findViewById(R.id.view_cb_2);
        checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(ViewActivity.this, "CheckBox2", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * RadioButton
     */
    private void initRadioButton() {
        final RadioButton radioButton1 = findViewById(R.id.view_rb);
        radioButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ViewActivity.this, "RadioButton", Toast.LENGTH_SHORT).show();
            }
        });

        final RadioButton radioButton2 = findViewById(R.id.view_rb_2);
        radioButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(ViewActivity.this, "RadioButton2", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * RadioGroup
     */
    private void initRadioGroup() {
        RadioGroup radioGroup = findViewById(R.id.view_rg);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Toast.makeText(ViewActivity.this, "RadioGroup:" + i, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     *
     */
    private void initDialog() {
        findViewById(R.id.view_tv_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        findViewById(R.id.view_tv_dialog_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog2();
            }
        });
    }

    /*
     * Dialog
     */
    Dialog dialog1 = null;
    private void showDialog() {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(ViewActivity.this);

        normalDialog.setIcon(R.mipmap.ic_launcher);
        normalDialog.setTitle("我是一个普通 Dialog");
        normalDialog.setMessage("你要点击哪一个按钮呢?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        dialog1 =normalDialog.show();
        dialog1.setOwnerActivity(ViewActivity.this);
    }

    /*
     * Dialog2
     */
    Dialog dialog2 = null;
    private void showDialog2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewActivity.this);  //先得到构造器
        //builder.create().setOwnerActivity(this);
        builder.setTitle("提示"); //设置标题
        builder.setMessage("是否确认退出?"); //设置内容
        builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); //关闭dialog
                Toast.makeText(ViewActivity.this, "确认" + which, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(ViewActivity.this, "取消" + which, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNeutralButton("忽略", new DialogInterface.OnClickListener() {//设置忽略按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(ViewActivity.this, "忽略" + which, Toast.LENGTH_SHORT).show();
            }
        });
        dialog2 = builder.create();
        dialog2.show();
    }

    /**
     * ContextMenu
     */
    private void initContextMenu() {
        TextView tvContextMenu = findViewById(R.id.view_tv_menu);
        registerForContextMenu(tvContextMenu);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("文件操作");
        // add context options_menu item
        menu.add(0, 1, Menu.NONE, "发送");
        menu.add(0, 2, Menu.NONE, "标记为重要");
        menu.add(0, 3, Menu.NONE, "重命名");
        menu.add(0, 4, Menu.NONE, "删除");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Toast.makeText(ViewActivity.this, "ContextMenu:" + item.getTitle(), Toast.LENGTH_SHORT).show();
        return super.onContextItemSelected(item);
    }


    /**
     * OptionsMenu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(ViewActivity.this, "OptionsMenu", Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }


    /**
     * TabLayout
     */
    private void initTabLayout() {
        TabLayout tabLayout = findViewById(R.id.view_tabLayout_1);
        String[] arr ={"首页", "分类", "朋友","我"};
        for (String title : arr) {
            TabLayout.Tab tab = tabLayout.newTab();
            tab.setText(title);
            tabLayout.addTab(tab);
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Toast.makeText(ViewActivity.this,"TabLayout:"+tab.getText(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    /**
     * TabLayout (custom tab)
     */
    private void initTabLayout2() {
        TabLayout tabLayout2 = findViewById(R.id.view_tabLayout_2);
        String[] arrTitle ={"首页", "分类", "朋友","我"};
        int[] arrImg = new int[]{R.drawable.selector_home, R.drawable.selector_classify,R.drawable.selector_friend,R.drawable.selector_my};

        for (int i = 0;i< arrTitle.length;i++) {
            TabLayout.Tab tab = tabLayout2.newTab();
            View view = LayoutInflater.from(this).inflate(R.layout.tab_custom, null);
            tab.setCustomView(view);
            TextView tvTitle = (TextView) view.findViewById(R.id.tv_tab);
            tvTitle.setText(arrTitle[i]);
            ImageView imgTab = (ImageView) view.findViewById(R.id.img_tab);
            imgTab.setImageResource(arrImg[i]);
            tabLayout2.addTab(tab);
        }
        tabLayout2.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Toast.makeText(ViewActivity.this,"TabLayout2:"+tab.getText(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * TabHost
     */
    private void initTabHost() {
        TabHost tabHost = findViewById(R.id.view_tabHost);
        tabHost.setup();
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("选项一").setContent(R.id.view_tabHost_ll_1));
        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("选项二").setContent(R.id.view_tabHost_ll_2));
        tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("选项三").setContent(R.id.view_tabHost_ll_3));
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                Toast.makeText(ViewActivity.this, "TabHost", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * TabHost2 (custom tab)
     */
    private void initTabHost2() {
        TabHost tabHost = findViewById(R.id.view_tabHost_2);
        tabHost.setup();
        String[] arrTitle ={"选项一", "选项二", "选项三"};
        int[] arrImg = new int[]{R.drawable.selector_home, R.drawable.selector_classify,R.drawable.selector_friend};
        for (int i = 0;i< arrTitle.length;i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.tab_custom, null);
            TextView tvTitle = view.findViewById(R.id.tv_tab);
            tvTitle.setText(arrTitle[i]);
            ImageView imgTab = view.findViewById(R.id.img_tab);
            imgTab.setImageResource(arrImg[i]);
            int contentId = R.id.view_tabHost2_ll_1;
            if(i==1) contentId = R.id.view_tabHost2_ll_2;
            if(i==2) contentId = R.id.view_tabHost2_ll_3;
            int j = i+1;
            tabHost.addTab(tabHost.newTabSpec("tab"+j).setIndicator(view).setContent(contentId));
        }
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                Toast.makeText(ViewActivity.this, "TabHost", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * LinearLayout
     */
    private void initLinearLayout() {
        findViewById(R.id.view_ll).setOnClickListener(this);
    }

    /**
     * ListView
     */
    private void initListView() {
        ListView listView = findViewById(R.id.view_listView);
        String[] arr = {"ListView item1", "ListView item2", "ListView item3"};
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(ViewActivity.this, android.R.layout.simple_expandable_list_item_1, arr);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ViewActivity.this, "ListView:" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * GridView
     */
    private void initGridView() {
        String[] arr2 = {"GridView item1", "GridView item2", "GridView item3"};
        GridView gridView = findViewById(R.id.view_gridView);
        gridView.setAdapter(new ArrayAdapter<String>(ViewActivity.this, android.R.layout.simple_expandable_list_item_1, arr2));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ViewActivity.this, "GridView:" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }


    ExpandableListView expandableListView = null;
    List<String> groupList = null;
    Map<String, List<String>> map = null;

    /**
     * ExpandableListView
     */
    private void initExpandableListView() {
        expandableListView = findViewById(R.id.view_expandablelistview);
        initData();
        expandableListView.setAdapter(new MyBaseExpandableListAdapter());
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                Toast.makeText(ViewActivity.this, "onChildClick", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                Toast.makeText(ViewActivity.this, "onGroupClick", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    /*
     * ExpandableListView、初始化数据
     *
     */
    public void initData() {
        groupList = new ArrayList<String>();
        groupList.add("Group1");
        groupList.add("Group2");
        groupList.add("Group3");

        map = new HashMap<String, List<String>>();
        List<String> list1 = new ArrayList<String>();
        list1.add("child1-1");
        list1.add("child1-2");
        list1.add("child1-3");

        map.put("Group1", list1);
        List<String> list2 = new ArrayList<String>();
        list2.add("child2-1");
        list2.add("child2-2");
        list2.add("child2-3");

        map.put("Group2", list2);
        List<String> list3 = new ArrayList<String>();
        list3.add("child3-1");
        list3.add("child3-2");
        list3.add("child3-3");
        map.put("Group3", list3);
    }

    /*
     * ExpandableListView Adapter
     *
     */
    class MyBaseExpandableListAdapter extends BaseExpandableListAdapter {

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            String key = groupList.get(groupPosition);
            return (map.get(key).get(childPosition));
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup par) {
            String key = groupList.get(groupPosition);
            String info = map.get(key).get(childPosition);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) ViewActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.expandable_child, null);
            }
            TextView tv = (TextView) convertView.findViewById(R.id.yang_child_textview);
            tv.setText(info);
            return tv;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            String key = groupList.get(groupPosition);
            return map.get(key).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groupList.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return groupList.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup par) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) ViewActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.expandable_group, null);
            }
            TextView tv = (TextView) convertView.findViewById(R.id.yang_parent_textview);
            tv.setText(groupList.get(groupPosition));
            return tv;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    /**
     * RecycleView
     */
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private void initRecycleView() {
        mLayoutManager = new LinearLayoutManager(ViewActivity.this, RecyclerView.VERTICAL, false);
        ArrayList<String> data = new ArrayList<>();
        data.add("RecyclerView item1");
        data.add("RecyclerView item2");
        data.add("RecyclerView item3");
        mAdapter = new MyRecyclerViewAdapter(data);
        mRecyclerView = findViewById(R.id.view_recycleView);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
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
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            holder.mTv.setText(mData.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "RecyclerView click", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView mTv;
            ViewHolder(View itemView) {
                super(itemView);
                mTv = (TextView) itemView.findViewById(R.id.tv_recycleView_item);
            }
        }
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(ViewActivity.this, "点击", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(dialog1!= null){
            dialog1.dismiss();
        }
        if(dialog2!= null){
            dialog2.dismiss();
        }
    }
}
