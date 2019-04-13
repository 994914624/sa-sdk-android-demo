package cn.sa.demo.activity;


import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sa.demo.R;

/**
 * 常用控件
 */
public class ViewActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        this.setTitle("常用控件");
        initView();
    }

    private void initView() {
        // Button
        findViewById(R.id.view_btn).setOnClickListener(this);
        // TextView
        findViewById(R.id.view_tv).setOnClickListener(this);
        // ImageView
        findViewById(R.id.view_img).setOnClickListener(this);
        // ImageButton
        findViewById(R.id.view_img_btn).setOnClickListener(this);
        // CheckedTextView
        initCheckedTextView();
        initSeekBar();
        initRatingBar();
        initSpinner();
        // SwitchCompat
        initSwitchCompat();
        // ToggleButton
        initToggleButton();
        // CheckBox
        initCheckBox();
        // RadioButton
        initRadioButton();
        initRadioGroup();
        initDialog();
        initContextMenu();
        initListView();
        initGridView();
        initExpandableListView();
    }



    /*
     * CheckedTextView
     */
    private void initCheckedTextView() {

        final CheckedTextView checkedTextView = findViewById(R.id.view_ctv);
        checkedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ViewActivity.this,"CheckedTextView",Toast.LENGTH_SHORT).show();
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
        final SwitchCompat switchCompat1= findViewById(R.id.view_sc_1);
        switchCompat1.setOnClickListener(new View.OnClickListener() {
            @Override
            @TargetApi(14)
            public void onClick(View view) {
                Toast.makeText(ViewActivity.this,"SwitchCompat1",Toast.LENGTH_SHORT).show();
                switchCompat1.setChecked(switchCompat1.isChecked());
            }
        });

        SwitchCompat switchCompat2= findViewById(R.id.view_sc_2);
        switchCompat2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(ViewActivity.this,"SwitchCompat2",Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * ToggleButton
     */
    private void initToggleButton() {
        final ToggleButton toggleButton = (ToggleButton)findViewById(R.id.view_tb_1);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ViewActivity.this,""+toggleButton.isChecked(),Toast.LENGTH_SHORT).show();
                toggleButton.setChecked(toggleButton.isChecked());
            }
        });

        ToggleButton toggleButton2= findViewById(R.id.view_tb_2);
        toggleButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(ViewActivity.this,""+b,Toast.LENGTH_SHORT).show();

            }
        });
    }

    /**
     * CheckBox
     */
    private void initCheckBox() {
        findViewById(R.id.view_cb_1).setOnClickListener(this);
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
        final RadioButton radioButton1 = findViewById(R.id.view_rb_1);
        radioButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ViewActivity.this,"RadioButton1",Toast.LENGTH_SHORT).show();
            }
        });

        final RadioButton radioButton2 = findViewById(R.id.view_rb_2);
        radioButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(ViewActivity.this,"RadioButton2",Toast.LENGTH_SHORT).show();
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
        findViewById(R.id.view_tv_dialog_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog1();
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
     * Dialog1
     */
    private void showDialog1() {
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
        normalDialog.show().setOwnerActivity(ViewActivity.this);
    }

    /*
     * Dialog2
     */
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
        Dialog dia=builder.create();
        dia.show();
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
        Toast.makeText(ViewActivity.this,"ContextMenu:"+item.getTitle(),Toast.LENGTH_SHORT).show();
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
     * ListView
     */
    private void initListView() {
        ListView listView=  findViewById(R.id.view_listView);
        String [] arr={"ListView item1","ListView item2","ListView item3"};
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(ViewActivity.this, android.R.layout.simple_expandable_list_item_1,arr);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ViewActivity.this,"ListView:"+position,Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * GridView
     */
    private void initGridView() {
        String [] arr2={"GridView item1","GridView item2","GridView item3"};
        GridView gridView= findViewById(R.id.view_gridView);
        gridView.setAdapter(new ArrayAdapter<String>(ViewActivity.this, android.R.layout.simple_expandable_list_item_1,arr2));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ViewActivity.this,"GridView:"+position,Toast.LENGTH_SHORT).show();
            }
        });
    }


    ExpandableListView expandableListView = null;
    List<String> parent = null;
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
                Toast.makeText(ViewActivity.this,"onChildClick",Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                Toast.makeText(ViewActivity.this,"onGroupClick",Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    /*
     * ExpandableListView、初始化数据
     *
     */
    public void initData() {
        parent = new ArrayList<String>();
        parent.add("Group1");
        parent.add("Group2");
        parent.add("Group3");

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
    class MyBaseExpandableListAdapter extends BaseExpandableListAdapter   {

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            String key = parent.get(groupPosition);
            return (map.get(key).get(childPosition));
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup par) {
            String key = parent.get(groupPosition);
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
            String key = parent.get(groupPosition);
            int size = map.get(key).size();
            return size;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return parent.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return parent.size();
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
            tv.setText(parent.get(groupPosition));
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



    @Override
    public void onClick(View v) {
        Toast.makeText(ViewActivity.this,"点击",Toast.LENGTH_SHORT).show();
    }
}
