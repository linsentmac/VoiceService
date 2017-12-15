package com.lenovo.smartcastvoice.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.lenovo.smartcastvoice.R;
import com.lenovo.smartcastvoice.utils.AnimationUtils;
import com.lenovo.smartcastvoice.utils.StatusBarUtils;

import java.util.ArrayList;

public class SpeekHintActivity extends Activity implements View.OnClickListener, AnimationUtils.AnimationListener{

    private static final String TAG = "SC-SpeekHintActivity";
    private ImageView mic;

    private ViewFlipper viewFlipper;

    private ListView listViewPage_1;
    private ListView listViewPage_2;
    private RelativeLayout result_layout;
    private TextView result_tv;
    private TextView result_tv_hint;


    private String[] page_1 = new String[]{"唱首周杰伦的歌", "打开东东教你画", "今天天气怎么样", "讲一个丑小鸭的故事", "我想听皇帝的新装的故事", "我想玩画画的游戏"};
    private String[] page_2 = new String[]{"我想听歌", "你好联想", "世界八大奇迹是哪些", "我想听童话故事", "打开魔幻学园","我想玩动物卡片游戏"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speek_hint);
        StatusBarUtils.hideNavgationBar(this);
        Intent intent = getIntent();
        initViews(intent);
        initEvents();
        initDatas();


    }

    private void initDatas() {
        listViewPage_1.setAdapter(new ListViewAdapter(page_1));
        listViewPage_2.setAdapter(new ListViewAdapter(page_2));
    }

    private void initViews(Intent intent) {
        mic = findViewById(R.id.mic_iv);
        viewFlipper = findViewById(R.id.viewFlipper);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view1 = inflater.inflate(R.layout.viewpager_layout, null);
        listViewPage_1 = view1.findViewById(R.id.viewpager_listview);
        View view2 = inflater.inflate(R.layout.viewpager_layout, null);
        listViewPage_2 = view2.findViewById(R.id.viewpager_listview);
        listViewPage_1.setEnabled(false);
        listViewPage_2.setEnabled(false);
        viewFlipper.addView(view1);
        viewFlipper.addView(view2);
        result_layout = findViewById(R.id.result_layout);
        result_tv = findViewById(R.id.result_tv);
        result_tv_hint = findViewById(R.id.result_tv_hint);
        boolean isResult = intent.getBooleanExtra("isResult", false);
        String result = intent.getStringExtra("Result");
        if(isResult && result != null){
            viewFlipper.setVisibility(View.GONE);
            result_layout.setVisibility(View.VISIBLE);
            result_tv.setText(result);
            result_tv_hint.setText("我不知道你在说些什么");
        }
    }

    private void initEvents() {
        mic.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mic_iv:
                AnimationUtils.playAnimation(mic, this);
                break;
        }
    }

    @Override
    public void EndAnimation(View view) {
        startActivity(new Intent(SpeekHintActivity.this, RecordActivity.class));
        finish();
    }


    private class ListViewAdapter extends BaseAdapter{

        private String[] mStr;

        public ListViewAdapter(String[] str){
            mStr = str;
        }

        @Override
        public int getCount() {
            return mStr.length;
        }

        @Override
        public Object getItem(int position) {
            return mStr[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null){
                holder = new ViewHolder();
                convertView = LayoutInflater.from(SpeekHintActivity.this).inflate(R.layout.listview_item, parent, false);
                holder.tv = convertView.findViewById(R.id.page_list_item_tv);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv.setText(mStr[position]);
            return convertView;
        }

        class ViewHolder{
            private TextView tv;
        }
    }

}
