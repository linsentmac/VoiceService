package com.lenovo.lasf.test;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TestMenu extends Activity {
	Button btn;
	TextView tvMsg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_menu);
		if(!(new File( Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/lasfTest/result/")).exists()){
			(new File( Environment.getExternalStorageDirectory().getAbsolutePath()
					+ "/lasfTest/result/")).mkdirs();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add(1,0,0,"清空结果");
		menu.add(1,1,0,"关于");
		return super.onCreateOptionsMenu(menu);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.test_contacts: {
			Intent intent = new Intent(this, TestContacts.class);
			startActivity(intent);
			break;
		}
		case R.id.test_app: {
			Intent intent = new Intent(this, TestApp.class);
			startActivity(intent);
			break;
		}
		case R.id.test_vod: {
			Intent intent = new Intent(this, TestVod.class);
			startActivity(intent);
			break;
		}
		case R.id.test_was: {
			Intent intent = new Intent(this, TestWeb.class);
			startActivity(intent);
			break;
		}
		case R.id.test_all: {
			Intent intent = new Intent(this, TestAll.class);
			startActivity(intent);
			break;
		}
		case R.id.test_auto: {
			Intent intent = new Intent(this, TestAutoRun.class);
			startActivity(intent);
			break;
		}
		case R.id.TestAutoRunPcm: {
			Intent intent = new Intent(this, TestAutoRunPcm.class);
			startActivity(intent);
			break;
		}
		case R.id.EditAbleParam: {
			Intent intent = new Intent(this, EditAbleParam.class);
			startActivity(intent);
			break;
		}
		case R.id.autotest: {
			Intent intent = new Intent(this, AutoTest.class);
			startActivity(intent);
			break;
		}
		
		case R.id.test_TestBuildGrammar: {
			Intent intent = new Intent("com.lenovo.lasf.action.LASF_BUILD_SERVICE");
			intent.putExtra("<main>", new String[]{"你好", "呼叫<$CONTACT>"}); 
			startService(intent);
			break;
		}
		default:
			break;
		}
	}
}
