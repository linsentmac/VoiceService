package com.lenovo.lasf.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class TestApp extends Activity {
	Button btn_net, btn_mu, btn_weather;
	TextView tvMsg;
	TextView voice;
	long begintime, endtime;
	private ProgressBar pb;
	String names[];
	String apps[];
	SpeechRecognizer r;
	
	static Map<String,String> appinfo = new HashMap<String, String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		voice = (TextView)findViewById(R.id.voice);

		btn_net = (Button) findViewById(R.id.start_net); // 网络识别按钮

		btn_mu = (Button) findViewById(R.id.start_mu); // 本地+网络识别按钮

		btn_weather = findViewById(R.id.btn_weather);

		pb = (ProgressBar) findViewById(R.id.progressBar1);
		btn_net.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				tvMsg.setText("");
				start();
			}
		});

		btn_mu.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				tvMsg.setText("");
				startmu();
			}
		});

		btn_weather.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				sendRequestWithHttpClient();
			}
		});

		tvMsg = (TextView) findViewById(R.id.msg);
	}

	/**
	 * 开启一次识别任务
	 */
	private void start() {
		ComponentName com = new ComponentName("com.lenovo.lasf",
				"com.lenovo.lasf.speech.LasfService");
		r = SpeechRecognizer.createSpeechRecognizer(this, com);
		r.setRecognitionListener(mReListener);
		Intent recognizerIntent = new Intent();
		// recognizerIntent.putExtra("<main>", new String[] { "呼叫<name>",
		// "打电话给<name>" });
		// recognizerIntent.putExtra("<name>", new String[] { "王小虎", "王晓虎" });
		// recognizerIntent.putExtra("<place>", new String[] { "上地", "联想" }); ,
		// "从<place>到<place>怎么走"
		recognizerIntent.putExtra("speech_sce", "cmd");
		recognizerIntent.putExtra("speech_domain", "app"); // 识别联系人领域

		r.startListening(recognizerIntent);
	}

	private void startmu() {
		long time1 = System.currentTimeMillis();
		
		readAllContacts();		//获取本机联系人
		getInstalledApps();		//获取本机程序
		
		Log.i("读取联系人时间", "" + (System.currentTimeMillis() - time1));
		ComponentName com = new ComponentName("com.lenovo.lasf",
				"com.lenovo.lasf.speech.LasfService");
		r = SpeechRecognizer.createSpeechRecognizer(this, com);
		r.setRecognitionListener(mReListener);
		Intent recognizerIntent = new Intent();
		recognizerIntent.putExtra("<main>", new String[] { "呼叫<name>",
				"<name>", "打电话给<name>","打开<apps>","<apps>","<was>网","<was>","<vod>","<order>" });
		
		recognizerIntent.putExtra("<name>", names);
		recognizerIntent.putExtra("<apps>", apps);
		recognizerIntent.putExtra("<order>", new String[]{"拍照","茄子"});
		recognizerIntent.putExtra("<was>", new String[]{"百度","新浪","人人","网易"});
		recognizerIntent.putExtra("<vod>", new String[]{"小时代","功夫","致我们终将逝去的青春","霍比特人"});
		
		
		// recognizerIntent.putExtra("<place>", new String[] { "上地", "联想" }); ,
		// "从<place>到<place>怎么走"

		recognizerIntent.putExtra("speech_domain", "app"); // 识别联系人领域

		r.startListening(recognizerIntent);
	}

	private void sendRequestWithHttpClient() {
		new Thread(new Runnable() {

			@Override
             public void run() {
				 //用HttpClient发送请求，分为五步
				 //第一步：创建HttpClient对象
				 HttpClient httpCient = new DefaultHttpClient();
				 //第二步：创建代表请求的对象,参数是访问的服务器地址
				 HttpGet httpGet = new HttpGet("http://10.100.217.233:15000/?sentence=今天有雨么&userid=123&city=上海");

				 try {
						 //第三步：执行请求，获取服务器发还的相应对象
						 HttpResponse httpResponse = httpCient.execute(httpGet);
						 //第四步：检查相应的状态是否正常：检查状态码的值是200表示正常
						 if (httpResponse.getStatusLine().getStatusCode() == 200) {
								 //第五步：从相应对象当中取出数据，放到entity当中
								 HttpEntity entity = httpResponse.getEntity();
								 String response = EntityUtils.toString(entity,"utf-8");//将entity当中的数据转换为字符串
							     Log.d("LS-", "http response = " + response);

						 }else {
							 Log.d("LS-", "http response error = " + httpResponse.getStatusLine().getStatusCode());
						 }

				 } catch (Exception e) {
					 // TODO Auto-generated catch block
					 e.printStackTrace();
					 Log.d("LS-", "exception = " + e.getMessage());
				 }
			}
         }).start();//这个start()方法不要忘记了
	}

	private RecognitionListener mReListener = new RecognitionListener() {

		@Override
		public void onRmsChanged(final float rmsdB) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					final android.view.ViewGroup.LayoutParams lp = voice.getLayoutParams();
					
					lp.width = (int) (rmsdB / 20);
					voice.setLayoutParams(lp);
//					voice.setWidth((int) (rmsdB / 100));
					pb.setProgress((int) (rmsdB / 100));
					// tvMsg.append("当前音量" + rmsdB + "\r\n");
				}
			});
		}

		@Override
		public void onResults(Bundle results) {

			List<String> rr = results
					.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

			List<String> list = results
					.getStringArrayList("results_recognition");
			String jo = results.getString("nlp_result_origin");

			Log.i("返回结果", "" + rr.size() + jo + list);

			if (rr.size() > 0) {
				StringBuffer tt = new StringBuffer();
				for (int m = 0; m < rr.size(); m++) {
					tt.append(rr.get(m)).append("\r\n");
					Log.i("tt", tt.toString());
				}

				final String t = tt.toString();
				endtime = System.currentTimeMillis();
				
//				if(t.contains("打开")){
//					String app=t.replace("打开", "");
//					Log.i("打开", app);
//					String pn=appinfo.get(app.trim());
//
//					Log.i("打开程序","size: "+appinfo.size());
//
//							openApp(pn);
//				}
				
				long time = endtime - begintime;
				tvMsg.append("onResults---:" + t +"\r\n"+ jo +"\r\n" + "使用时间"
						+ time);
//				runOnUiThread(new Runnable() {
//
//					@Override
//					public void run() {						
//						endtime = System.currentTimeMillis();
//						
//						if(t.contains("打开")){
//							String app=t.replace("打开", "");
//							Log.i("打开", app);
//							String pn=appinfo.get(app);
//						Log.i("打开程序",pn);
////							openApp(pm);
//						}
//
//						long time = endtime - begintime;
//						tvMsg.append("onResults---:" + t + "\r\n" + "使用时间"
//								+ time);
//					}
//				});
			}

			Bundle bundle = new Bundle();

		}

		@Override
		public void onReadyForSpeech(Bundle params) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					tvMsg.append("onReadyForSpeech	可以说话了\r\n");
				}
			});
		}

		@Override
		public void onPartialResults(Bundle partialResults) {
			List<String> rr = partialResults
					.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

			if (rr.size() > 0) {
				final String t = rr.get(0);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						tvMsg.append(t + "\r\n");
					}
				});
			}
		}

		@Override
		public void onEvent(int eventType, Bundle params) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onError(final int error) {
			// TODO Auto-generated method stub

			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					switch (error) {
					case 1:
						tvMsg.append("出错了 " + error + "网络超时\r\n");
						break;
					case 2:
						tvMsg.append("出错了 " + error + "网络错误\r\n");
						break;
					case 3:
						tvMsg.append("出错了 " + error + "录音出错\r\n");
						break;
					case 4:
						tvMsg.append("出错了 " + error + "服务器返回错误状态\r\n");
						break;
					case 5:
						tvMsg.append("出错了 " + error
								+ "客户端调用错误，如识别结果返回之前再次请求识别\r\n");
						break;
					case 6:
						tvMsg.append("出错了 " + error + "无语音输入\r\n");
						break;
					case 7:
						tvMsg.append("出错了 " + error + "没有与输入的语音匹配的识别结果\r\n");
						break;
					case 8:
						tvMsg.append("出错了 " + error + "引擎忙\r\n");
						break;
					default:
						tvMsg.append("出错了 " + error + "");

					}
				}
			});
		}

		@Override
		public void onEndOfSpeech() {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					begintime = System.currentTimeMillis();
					tvMsg.append("onEndOfSpeech 收音结束" + "\r\n");
				}
			});
		}

		@Override
		public void onBufferReceived(byte[] buffer) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onBeginningOfSpeech() {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					tvMsg.append("onBeginningOfSpeech " + "\r\n");
				}
			});
		}
	};

	public void readAllContacts() {
		ArrayList<String> contacts = new ArrayList<String>();

		Cursor cursor = this
				.getBaseContext()
				.getContentResolver()
				.query(ContactsContract.Contacts.CONTENT_URI, null, null, null,
						null);
		int contactIdIndex = 0;
		int nameIndex = 0;

		if (cursor.getCount() > 0) {
			contactIdIndex = cursor
					.getColumnIndex(ContactsContract.Contacts._ID);
			nameIndex = cursor
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
		}
		while (cursor.moveToNext()) {
			String contactId = cursor.getString(contactIdIndex);
			String name = cursor.getString(nameIndex);
			contacts.add(name);
		}
		names = new String[contacts.size()];
		for (int i = 0, j = contacts.size(); i < j; i++) {
			names[i] = contacts.get(i);
		}
		Log.i("本机程序", ""+names.toString());
	}

	/***
	 * 获取本地程序列表
	 * **/
	private void getInstalledApps() {
		List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
		List<Map<String, Object>> listMap = new ArrayList<Map<String,Object>>(packages.size());
		
		ArrayList<String> app=new ArrayList<String>();
		for (int j = 0; j < packages.size(); j++) {
			Map<String, Object> map = new HashMap<String, Object>();
			
			PackageInfo packageInfo = packages.get(j);
			//显示非系统软件
			if((packageInfo.applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)==0){
				map.put("img", packageInfo.applicationInfo.loadIcon(getPackageManager()).getCurrent());
				map.put("name", packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
				app.add(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
				
				appinfo.put(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString(), packageInfo.packageName);
				
				Log.i("chengxu", packageInfo.applicationInfo.loadLabel(getPackageManager()).toString()+packageInfo.packageName);
				map.put("desc", packageInfo.packageName);
				listMap.add(map);
			}
		}
		apps = new String[app.size()];
		for (int i = 0, j = app.size(); i < j; i++) {
			apps[i] = app.get(i);
		}
//		Log.i("本机程序", ""+apps.toString());
		//		return listMap;
	}
	private void openApp(String packageName) {
		PackageManager packageManager = TestApp.this.getPackageManager();
		Intent intent=new Intent();
		intent =packageManager.getLaunchIntentForPackage(packageName);
		startActivity(intent);
		}  
	
	protected void onDestroy() {
		super.onDestroy();
		r.destroy();
	};


}
