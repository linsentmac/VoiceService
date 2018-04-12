package cn.lenovo.voiceservice;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import cn.lenovo.voiceservice.utils.AnimationUtils;
import cn.lenovo.voiceservice.utils.StatusBarUtils;


public class SpeekHintActivity extends Activity implements View.OnClickListener, AnimationUtils.AnimationListener{

    private static final String TAG = "SC-SpeekHintActivity";

    private TTStoSpeech mTTs;

    private ImageView mic;

    private ViewFlipper viewFlipper;

    private ListView listViewPage_1;
    private ListView listViewPage_2;
    private RelativeLayout result_layout;
    private TextView result_tv;
    private TextView result_tv_hint;


    private String[] page_1 = new String[]{"唱首薛之谦的歌", "打开东东教你画", "今天天气怎么样", "讲一个小红帽的故事", "我想听灰姑娘的故事", "我想玩画画的游戏"};
    private String[] page_2 = new String[]{"我想听歌", "你好联想", "未来三天有雨么", "我想听童话故事", "打开魔幻学园", "我想玩动物卡片游戏"};


    private boolean openApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speek_hint);
        mTTs = TTStoSpeech.getInstance(this);
        Intent intent = getIntent();
        initViews(intent);
        initEvents();
        initDatas();
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatusBarUtils.hideNavgationBar(this);
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
        openApp = intent.getBooleanExtra("openApp", false);
        boolean isResult = intent.getBooleanExtra("isResult", false);
        String result = intent.getStringExtra("Result");
        String pkgName = intent.getStringExtra("pkgName");
        String appName = intent.getStringExtra("appName");
        String hintContent = intent.getStringExtra("hintContent");
        if(openApp){
            viewFlipper.setVisibility(View.GONE);
            result_layout.setVisibility(View.VISIBLE);
            result_tv.setText(result);
            result_tv_hint.setText("已帮你打开" + appName);
            PackageManager packageManager = getPackageManager();
            Intent openIntent = new Intent();
            openIntent = packageManager.getLaunchIntentForPackage(pkgName);
            if(openIntent != null){
                startActivity(openIntent);
            }
        }else {
            if(isResult && result != null){
                viewFlipper.setVisibility(View.GONE);
                result_layout.setVisibility(View.VISIBLE);
                result_tv.setText(result);
                if(hintContent != null){
                    result_tv_hint.setText(hintContent);
                    mTTs.speek(hintContent);
                }else {
                    result_tv_hint.setText("暂不支持此功能");
                    mTTs.speek("我不太明白主人的意思呀");
                }
            }
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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
        intent.addCategory(Intent.CATEGORY_HOME);
        this.startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mTTs != null
                && !openApp){
            mTTs.stopTTs();
        }
    }

}
