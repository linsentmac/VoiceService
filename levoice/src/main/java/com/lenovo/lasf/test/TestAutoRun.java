package com.lenovo.lasf.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class TestAutoRun extends Activity implements OnInitListener {

	public static final int STARTMU = 1001;
	public static final int START = 1002;
	public static final int CANCLE = 1000;

	Button btn_net, btn_mu;
	TextView tvMsg;
	TextView voice;
	long begintime, endtime;
	private ProgressBar pb;
	String names[];
	boolean flag = true;
	private int i = 0;
	Button cancle, stop;
	private TextToSpeech mTts;
	private int trytime = 0;
	SpeechRecognizer r;
	ComponentName com = new ComponentName("com.lenovo.lasf",
			"com.lenovo.lasf.speech.LasfService");

	Handler mHandler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case STARTMU:
				tvMsg.setText("");
				startmu();
				break;
			case START:
				speech();
				break;
			default:
				break;
			}
			super.dispatchMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		voice = (TextView) findViewById(R.id.voice);

		btn_net = (Button) findViewById(R.id.start_net); // 网络识别按钮

		btn_mu = (Button) findViewById(R.id.start_mu); // 本地+网络识别按钮
		tvMsg = (TextView) findViewById(R.id.msg);
		pb = (ProgressBar) findViewById(R.id.progressBar1);
		cancle = (Button) findViewById(R.id.btn_cancle);
		stop = (Button) findViewById(R.id.btn_stop);
		mTts = new TextToSpeech(this, this, "com.lenovo.lasf.tts");
		cancle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				flag = false;
				mHandler.sendEmptyMessageDelayed(CANCLE, 10);
				r.cancel();
			}
		});

		stop.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				flag = false;
				mHandler.sendEmptyMessageDelayed(CANCLE, 10);
				r.stopListening();
			}
		});

		btn_net.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				start();

			}
		});

		btn_mu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				flag = true;
				tvMsg.setText("第" + i + "次\n\r");
				mHandler.sendEmptyMessage(STARTMU);
			}
		});

	}

	/**
	 * 开启一次识别任务
	 */
	private void start() {

		r = SpeechRecognizer.createSpeechRecognizer(this, com);
		r.setRecognitionListener(mReListener);
		Intent recognizerIntent = new Intent();
		recognizerIntent.putExtra("speech_domain", "contacts"); // 识别联系人领域

		r.startListening(recognizerIntent);

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void startmu() {

		tvMsg.setText("");

		long time1 = System.currentTimeMillis();

		readAllContacts();

		speech();
		Log.i("读取联系人时间", "" + (System.currentTimeMillis() - time1));

	}

	private void speech() {
		tvMsg.setText("");
		r = SpeechRecognizer.createSpeechRecognizer(this, com);
		r.setRecognitionListener(mReListener);
		Intent recognizerIntent = new Intent();
		recognizerIntent.putExtra("<main>", new String[] { "呼叫<name>",
				"<name>", "打电话给<name>" });
		recognizerIntent.putExtra("<name>", names);
		recognizerIntent.putExtra("speech_domain", "contacts"); // 识别联系人领域

		r.startListening(recognizerIntent);
		// });

	}

	private RecognitionListener mReListener = new RecognitionListener() {

		@Override
		public void onRmsChanged(final float rmsdB) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					final android.view.ViewGroup.LayoutParams lp = voice
							.getLayoutParams();

					lp.width = (int) (rmsdB / 20);
					voice.setLayoutParams(lp);
					// voice.setWidth((int) (rmsdB / 100));
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
			float score = results.getFloat("confidence_scores");

			String eng = "";
			if ("engine_type_local".equals(results.getString("engine_type"))) {
				eng = "本地识别";
			} else {
				eng = "网络识别";
			}

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

				/*** 以下是打开程序的代码 **/
				// if(t.contains("打开")){
				// String app=t.replace("打开", "");
				// Log.i("打开", app);
				// String pn=appinfo.get(app.trim());
				//
				// Log.i("打开程序","size: "+appinfo.size());
				//
				// openApp(pn);
				// }

				long time = endtime - begintime;
				tvMsg.append("onResults---:" + t + "\t" + score + eng + "\r\n"
						+ jo + "\r\n" + "使用时间" + time + "\r\n");
				// runOnUiThread(new Runnable() {
				//
				// @Override
				// public void run() {
				// endtime = System.currentTimeMillis();
				//
				// if(t.contains("打开")){
				// String app=t.replace("打开", "");
				// Log.i("打开", app);
				// String pn=appinfo.get(app);
				// Log.i("打开程序",pn);
				// // openApp(pm);
				// }
				//
				// long time = endtime - begintime;
				// tvMsg.append("onResults---:" + t + "\r\n" + "使用时间"
				// + time);
				// }
				// });

				/**** 识别完后开启下一次识别 ****/
				if (flag) {
					mHandler.sendEmptyMessageDelayed(START, 1000);
				}
				i++;
				// startmu();

			}

		}

		@Override
		public void onReadyForSpeech(Bundle params) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					tvMsg.append("onReadyForSpeech	可以说话了\r\n");
					sp(names[i]);
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
					if (flag) {
						mHandler.sendEmptyMessageDelayed(START, 1000);
					}
				}

			});
			trytime++;
			if (trytime == 3) {
				i++;
				trytime = 0;
			}
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
					tvMsg.append("onBeginningOfSpeech :  检测到语音输入" + "\r\n");
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
		Log.i("本机程序", "" + names.toString());
	}

	private void sp(String str) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, UUID.randomUUID()
				.toString());
		// String str = "你好啊，hello 我是TTS";
		mTts.speak(str, TextToSpeech.QUEUE_FLUSH, map);
	}

	@Override
	public void onInit(int status) {
		if (status != TextToSpeech.SUCCESS) {
			Toast.makeText(this, "tts初始化失败", 1).show();
		}
	}

}
