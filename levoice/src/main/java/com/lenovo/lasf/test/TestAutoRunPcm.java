package com.lenovo.lasf.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioFormat;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class TestAutoRunPcm extends Activity {

	public static final int STARTMU = 1001;
	public static final int START = 1002;
	public static final int CANCLE = 1000;

	Button btn_net, btn_mu;
	TextView tvMsg;
	TextView voice;
	ScrollView sc;
	long begintime, endtime;
	private ProgressBar pb;
	String names[];
	boolean flag = true;
	boolean onlynet = true;
	private int i = 0;
	Button cancle;
	private AudioPlayer mAudioPlayer; // 播放器
	private Handler mHandler1;
	String filePath = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/pcm_Test/";
	private int f = 0;
	File files[];
	int msglen = 0;
	int trytime = 0;
	Properties prop;
	File list = new File(Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/list/list.properties");
	BufferedWriter writer;
	ComponentName com = new ComponentName("com.lenovo.lasf",
			"com.lenovo.lasf.speech.LasfService");
	SpeechRecognizer r;

	String result = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ "/list/result.csv";
	static Map<String, String> appinfo = new HashMap<String, String>();

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
		sc = (ScrollView) findViewById(R.id.msgsc);
		prop = new Properties();

		FileInputStream in;
		try {
			in = new FileInputStream(list);
			try {
				prop.load(in);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		files = new File(filePath).listFiles(); //

		cancle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				flag = false;
				mHandler.sendEmptyMessageDelayed(CANCLE, 10);
			}
		});

		btn_net.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// i = 0;
				// tvMsg.setText("第" + i + "次");
				//
				// start();
				mHandler.sendEmptyMessage(START);

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

		onlynet = true;

		
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
		onlynet=false;

		if (msglen == 9) {
			tvMsg.setText("");
			msglen = 0;
		}
		long time1 = System.currentTimeMillis();

		readAllContacts(); // 获取本机联系人
		speech();
		Log.i("读取联系人时间", "" + (System.currentTimeMillis() - time1));

	}

	private void speech() {

		initLogic();
		if (msglen == 9) {
			tvMsg.setText("");
			msglen = 0;
		}
		

		r = SpeechRecognizer.createSpeechRecognizer(this, com);
		r.setRecognitionListener(mReListener);
		Intent recognizerIntent = new Intent();

		if (!onlynet) {
			recognizerIntent.putExtra("<main>", new String[] { "呼叫<name>",
					"<name>", "打电话给<name>", "给<name>打电话" });

			recognizerIntent.putExtra("<name>", names);
			recognizerIntent.putExtra("speech_domain", "contacts"); // 识别联系人领域
		}
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
			float[] score = results.getFloatArray("confidence_scores");

			String eng = "";
			if ("engine_type_local".equals(results.getString("engine_type"))) {
				eng = "本地识别";
			} else {
				eng = "网络识别";
			}

			String jo = results.getString("nlp_result_origin");
			
			if(jo!=null){
				JSONObject result;
				try {
					result = new JSONObject(jo);
					String rawText;
					//获取原始识别文本和置信度
					try {
						rawText = result.getString("rawText");
						String confidence = result.getString("confidence");
						try {
							writer.write(rawText + "," + confidence + ",");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
												
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//上面是获取原始识别文本和置信度
					
					try{

						JSONObject nlp = new JSONObject(result.get("result").toString());
						
						StringBuffer name=new StringBuffer();
						StringBuffer cm=new StringBuffer();

						org.json.JSONArray names = nlp.getJSONArray("object");
						if (names.length() > 0) {
							int s = names.length();
							for (int i = 0; i < s; i++) {
								JSONObject na = names.getJSONObject(i);
								name.append(na.get("name"));

								cm.append(na.getDouble("cm"));
								
								
							}
						}
						try {
							writer.write(name.toString() + "," + cm.toString() + ",");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						}catch(JSONException e){
							try {
								writer.write("没有nlp,");
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					
					
				} catch (JSONException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				
				
				
				
				
				
				
				try {
					writer.write("\r\n");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

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

				long time = endtime - begintime;
				tvMsg.append("onResults---:" + t + "\t置信度  " + score[0]
						+ "      " + eng + "\r\n" + jo + "\r\n" + "使用时间" + time
						+ "\r\n");

				/**** 识别完后开启下一次识别 ****/
				msglen++; // 控制输入栏

				String order = (String) prop.get(files[i].getName()
						.replaceAll(".pcm", "").trim());			//被识别的文件名、原始命令
				String resultName = "";
				try {
					resultName = new String(order.getBytes("ISO-8859-1"),
							"utf-8");								//将命令转化为utf8编码
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				
//				//原来的写文件代码
				if (resultName.equals(t.trim().replaceAll("。", ""))) {
					try {
						BufferedWriter writer = new BufferedWriter(
								new FileWriter(new File(result), true));
						writer.write(""
								+ files[i].getName().replaceAll(".pcm", "")
								+ "," + resultName + ","
								+ t.trim().replace("\r\n", "") + ","
								+ "pass\r\n");
						writer.close();
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}

					tvMsg.append("Pass  " + resultName + "		" + t + "\r\n");
				} else {
					try {
						BufferedWriter writer = new BufferedWriter(
								new FileWriter(new File(result), true));
						writer.write(""
								+ files[i].getName().replaceAll(".pcm", "")
								+ "," + resultName + ","
								+ t.trim().replace("\r\n", "") + ","
								+ "fail\r\n");
						writer.close();
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					tvMsg.append("Fail	" + resultName + "		" + t + "\r\n");
				}                                    //原来的写文件代码

				if (i < files.length) {
					i++;
				}
				if (trytime > 0) {
					tvMsg.append("重试 " + trytime + "次\r\n");
					trytime = 0;
				}
				sc.scrollTo(0, tvMsg.getHeight());

				if (flag) {
					mHandler.sendEmptyMessageDelayed(START, 1000);
				}

			}

		}

		@Override
		public void onReadyForSpeech(Bundle params) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					tvMsg.append("onReadyForSpeech	可以说话了\r\n");
					play();
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
					r.cancel();
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
						tvMsg.append("出错了 " + error + "\r\n");

					}
					trytime++;
					if (trytime == 3) {
						String order = (String) prop.get(files[i].getName()
								.replaceAll(".pcm", "").trim());
						String resultName = "";
						try {
							BufferedWriter writer = new BufferedWriter(
									new FileWriter(new File(result), true));
							writer.write(""
									+ files[i].getName().replaceAll(".pcm", "")
									+ "," + resultName + "," + "重试3次失败" + ","
									+ "fail\r\n");
							writer.close();
						} catch (IOException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						if (i < files.length) {
							i++;
						}
						tvMsg.append("重试 " + trytime + "次\r\n");
						trytime = 0;
					}

					sc.scrollTo(0, tvMsg.getHeight());

					msglen++;
					if (flag) {
						mHandler.sendEmptyMessageDelayed(START, 3000);
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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		try{
		mAudioPlayer.release();
		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void initLogic() {
		mHandler1 = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case AudioPlayer.STATE_MSG_ID:
					showState((Integer) msg.obj);
					break;
				}
			}

		};

		mAudioPlayer = new AudioPlayer(mHandler1);
		// 获取音频参数
		AudioParam audioParam = getAudioParam();
		mAudioPlayer.setAudioParam(audioParam);

		// 获取音频数据
		Log.i("文件名", files[i].getName());
		byte[] data = getPCMData(files[i]);
		mAudioPlayer.setDataSource(data);

		// 音频源就绪
		mAudioPlayer.prepare();

		if (data == null) {
			// mTextViewState.setText(filePath + "：该路径下不存在文件！");
		}
	}

	public void play() {
		mAudioPlayer.play();
	}

	public void pause() {
		mAudioPlayer.pause();
	}

	public void stop() {
		mAudioPlayer.stop();
	}

	public void showState(int state) {
		String showString = "";

		switch (state) {
		case PlayState.MPS_UNINIT:
			showString = "MPS_UNINIT";
			break;
		case PlayState.MPS_PREPARE:
			showString = "MPS_PREPARE";
			break;
		case PlayState.MPS_PLAYING:
			showString = "MPS_PLAYING";
			break;
		case PlayState.MPS_PAUSE:
			showString = "MPS_PAUSE";
			break;
		}

		showState(showString);
	}

	public void showState(String str) {
		// mTextViewState.setText(str);
	}

	/*
	 * 获得PCM音频数据参数
	 */
	public AudioParam getAudioParam() {
		AudioParam audioParam = new AudioParam();
		audioParam.mFrequency = 8000;
		audioParam.mChannel = 3;
		audioParam.mSampBit = AudioFormat.ENCODING_PCM_16BIT;

		return audioParam;
	}

	// String filePath = Environment.getExternalStorageDirectory()
	// .getAbsolutePath() + "/pcm_Test/10001.pcm";

	/*
	 * 获得PCM音频数据
	 */
	public byte[] getPCMData(File file) {
		try {
			writer= new BufferedWriter(new FileWriter(new File(
					file.getParentFile().getParentFile().getAbsolutePath() + "/" + file.getParentFile().getName() + "_res.txt"),true));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			writer.write(file.getName() + ",");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// File file = new File(filePath);
		if (file == null) {
			return null;
		}

		FileInputStream inStream;
		try {
			inStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}

		byte[] data_pack = null;
		if (inStream != null) {
			long size = file.length();

			data_pack = new byte[(int) size];
			try {
				inStream.read(data_pack);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}

		}

		return data_pack;
	}

}
