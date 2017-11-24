package com.lenovo.lasf.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class EditAbleParam extends Activity {
	Button start_btn, cancle_btn;
	EditText domain_text, local_text, sce_text, vdm_text;
	CheckBox checkBox_domain, checkBox_vdm, checkBox_sce, checkBox_local;
	TextView tvMsg;
	TextView voice;
	static String names[];
	static String apps[];
	ComponentName com = new ComponentName("com.lenovo.lasf",
			"com.lenovo.lasf.speech.LasfService");
	SpeechRecognizer r = SpeechRecognizer.createSpeechRecognizer(this, com);;
	boolean hasdomain = false;
	boolean hasvdm = false;
	boolean hassce = false;
	boolean haslocal = false;
	boolean stop = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_able_param);

		start_btn = (Button) findViewById(R.id.start_btn);
		cancle_btn = (Button) findViewById(R.id.cancle_btn);
		// 俩按钮
		domain_text = (EditText) findViewById(R.id.domain_text);
		vdm_text = (EditText) findViewById(R.id.vdm_text);
		sce_text = (EditText) findViewById(R.id.sce_text);
		local_text = (EditText) findViewById(R.id.local_text);
		// 以上是输入框

		checkBox_domain = (CheckBox) findViewById(R.id.checkBox_domain);
		checkBox_vdm = (CheckBox) findViewById(R.id.checkBox_vdm);
		checkBox_sce = (CheckBox) findViewById(R.id.checkBox_sce);
		checkBox_local = (CheckBox) findViewById(R.id.checkBox_local);
		// 四个复选框

		tvMsg = (TextView) findViewById(R.id.msg);
		voice = (TextView) findViewById(R.id.voice);

		checkBox_domain
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton btn,
							boolean value) {
						hasdomain = value;
						Log.i("选择", "domain " + hasdomain);
						// value为CheckBox的值
					}
				});
		checkBox_vdm
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton btn,
							boolean value) {
						hasvdm = value;
						Log.i("选择", "vdm " + hasvdm);
						// value为CheckBox的值
					}
				});
		checkBox_sce
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton btn,
							boolean value) {
						hassce = value;
						Log.i("选择", "sce " + hassce);
						// value为CheckBox的值
					}
				});
		checkBox_local
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton btn,
							boolean value) {
						haslocal = value;
						Log.i("选择", "local " + haslocal);
						// value为CheckBox的值
					}
				});

		start_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!stop) {

					tvMsg.setText("");
					start();
					start_btn.setText("停止收音");
					stop = true;
				} else {
					r.stopListening();
					start_btn.setText("启动收音");
					stop = false;
				}
			}
		});
		cancle_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				r.cancel();
				tvMsg.setText("取消");
			}
		});
	}

	private void start() {

		// ComponentName com = new ComponentName("com.lenovo.lasf",
		// "com.lenovo.lasf.speech.LasfService");
		// r = SpeechRecognizer.createSpeechRecognizer(this, com);
		r.setRecognitionListener(mReListener);
		Intent recognizerIntent = new Intent();

		if (hasdomain) {
			recognizerIntent.putExtra("speech_domain", domain_text
					.getEditableText().toString());
			Log.i("添加domain", domain_text.getEditableText().toString());
		}
		if (hasvdm) {
			recognizerIntent.putExtra("speech_vdm", vdm_text.getEditableText()
					.toString());
			Log.i("添加vdm", vdm_text.getEditableText().toString());
		}
		if (hassce) {
			recognizerIntent.putExtra("speech_sce", sce_text.getEditableText()
					.toString());
			Log.i("添加sce", sce_text.getEditableText().toString());
		}

		if (haslocal) {
			// recognizerIntent.putExtra("<main>",
			// StringFilter(local_text.getEditableText().toString())
			// .split("，")); //过滤特殊字符

			recognizerIntent.putExtra("<main>", local_text.getEditableText()
					.toString().split("，")); // 不过滤特殊字符
			Log.i("添加local", local_text.getEditableText().toString());
		}
		r.startListening(recognizerIntent);
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
					voice.setText("" + rmsdB);
				}
			});
		}

		@Override
		public void onResults(Bundle results) {
			// endtime = System.currentTimeMillis();
			List<String> rr = results
					.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

			List<String> list = results
					.getStringArrayList("results_recognition");
			float[] score = results.getFloatArray("confidence_scores");

			String json = results.getString("nlp_result_origin");

			String eng = "";
			if ("com.lenovo.lasf.speech.ThinkitLocalDecoder".equals(results.getString("engine_fullname"))) {
				eng = "本地识别";
			} else {
				eng = "网络识别";
			}

			Log.i("返回结果", "" + rr.size() + list);

			if (rr.size() > 0) {
				StringBuffer tt = new StringBuffer();
				for (int m = 0; m < rr.size(); m++) {
					tt.append(rr.get(m)).append("\r\n");
					Log.i("tt", tt.toString());
				}

				final String t = tt.toString();

				tvMsg.append("onResults---:" + t + "\t" + score[0] + "    "
						+ eng + "\r\n" + "使用时间" + "\r\n" + json);

			}
			stop = false;
			start_btn.setText("启动收音");

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
						Log.i("部分", "part" + t);
						tvMsg.append("part " + t + "\r\n");
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
			stop = false;
			start_btn.setText("启动收音");
		}

		@Override
		public void onEndOfSpeech() {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// begintime = System.currentTimeMillis();
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

	protected void onDestroy() {

		Log.i("lasfTest", "销毁识别");
		super.onDestroy();
		r.destroy();
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_able_param, menu);
		return true;
	}

	// 过滤特殊字符
	public static String StringFilter(String str) throws PatternSyntaxException {
		// 只允许字母和数字
		// String regEx = "[^a-zA-Z0-9]";
		// 清除掉所有特殊字符
		String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

}
