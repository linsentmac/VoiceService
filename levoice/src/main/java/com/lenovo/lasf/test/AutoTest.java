package com.lenovo.lasf.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.media.AudioFormat;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.Settings.Secure;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AutoTest extends Activity implements OnInitListener {
	public static final int START = 1001;

	public static final int CANCLE = 1000;
	// 上面是用来标记状态，一次任务结束后再次调起新任务

	int msglen = 0;

	private int i = 0; // pcm文件数，与pcmNum协同作用

	private boolean manSpeak = true; // 默认使用人工朗读
	private boolean useTTS = false; // 默认不使用TTS
	private boolean usePcm = false; // 默认不使用PCM
	private Handler mHandler1; // 播放pcm用的
	private AudioPlayer mAudioPlayer; // PCm播放器
	File files[]; // PCM文件列表
	int pcmNum = 0;
	// int pcmtesttime=0;

	Map mapPcm; // PCM属性
	File pcmProp; // pcm属性文件
	boolean pcmCompare = false; // 是否进行比较结果
	File compareResult; // 自动播放pcm时比较结果文件
	BufferedWriter comWriter; // 写入对比结果

	boolean flag = false; //

	String names[]; // 联系人数组，可以从本机联系人获取

	long begintime, endtime; // 用来计算识别时间
	long readytime, autoendtime; // 计算无语音输入时间
	long click, start, ready; // 点击时间

	private TextToSpeech mTts; // 文本转语音
	Properties propTTS; // TTS属性
	private int ttsNum = 0; // 朗读的次数
	private int startNum;

	private int trytime = 0; // 失败重试次数
	SpeechRecognizer r; // 识别
	ComponentName com = new ComponentName("com.lenovo.lasf",
			"com.lenovo.lasf.speech.LasfService");

	ScrollView sc; // 滚动区域
	private TextView textResult; // 显示结果的区域，可以滚动

	TextView voice; // 用来展示声音振幅
	private Button buttonStart, buttonStop; // 开始和停止按钮
	private Spinner spinnerDomain; // 下拉菜单
	private ArrayAdapter adapter; // 还是下拉菜单用的
	private String selectedItem, domain, sce; // 识别领域
	File ttsfile = new File(Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/lasfTest/ttsFile.properties"); // 要播放的tts文本
	String pcmPath = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/lasfTest/pcms/";
	File result = new File(Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/lasfTest/result.txt"); // 测试结果

	String vodResult = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/lasfTest/result/vodresult.txt";
	String vodCompareResult = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/lasfTest/result/vodCompareResult.txt";

	String wasResult = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/lasfTest/result/wasresult.txt";
	String wasCompareResult = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/lasfTest/result/wasCompareResult.txt";

	String noteResult = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/lasfTest/result/noteresult.txt";

	File pcm; // 保存pcm文件
	FileOutputStream out; // 保存pcm文件

	BufferedWriter writer; // 用来向SD卡写入结果
	BufferedWriter vodWriter; // 用来向SD卡写入VOD结果
	BufferedWriter wasWriter; // 用来向SD卡写入was结果
	BufferedWriter noteWriter; // 用来向SD卡写入连续识别结果

	private static final String[] selectedItems = { "联系人       ",
			"应用         ", "视频         ", "网站         ", "乐服务           ",
			"全部           ", "连续识别      "

	}; // 用来显示在下拉菜单里面的
	private static final String[] domains = { "contacts", "app", "vod", "was",
			"les", "all", "all" }; // 识别领域待选项，用来和下拉菜单选项对应

	private static final String[] sces = { "cmd", "cmd", "cmd", "cmd", "iat",
			"cmd", "iat" }; // 识别领域待选项，

	Handler mHandler = new Handler() { // 设置是否需要继续调起
		@Override
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case START:

				speech();
				break;
			case CANCLE:

			default:
				break;
			}
			super.dispatchMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auto_test);
		sc = (ScrollView) findViewById(R.id.sc_res);
		textResult = (TextView) findViewById(R.id.textResult);
		voice = (TextView) findViewById(R.id.voice);
		buttonStart = (Button) findViewById(R.id.buttonStart);
		buttonStop = (Button) findViewById(R.id.buttonStop);
		spinnerDomain = (Spinner) findViewById(R.id.spinnerDomain);

		adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,
				selectedItems);

		mTts = new TextToSpeech(this, this, "com.lenovo.lasf.tts");
		/* myspinner_dropdown为自定义下拉菜单样式定义在res/layout目录下 */
		adapter.setDropDownViewResource(R.layout.myspinner_dropdown);
		/* 将ArrayAdapter加入Spinner对象中 */
		spinnerDomain.setAdapter(adapter);
		/* 将mySpinner加入OnItemSelectedListener */
		spinnerDomain
				.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						selectedItem = selectedItems[arg2];
						domain = domains[arg2];
						sce = sces[arg2];
						/* 将所选mySpinner的值带入myTextView中 */
						textResult.setText("选择的是" + selectedItem + "  "
								+ domain);
						textResult
								.append("\r\n\r\n 如果SD卡/lasfTest/pcms/"
										+ domain
										+ "/目录下存放了pcm文件,点击开始后可以自动播放声音识别\r\n"
										+ "如果相同目录下存在list.txt文件则可以对比识别结果正确性\r\n文件格式为：\r\n文件名,文件内容\r\n如："
										+ "\r\n0001,环太平洋" + "\r\n\r\n应使用英文逗号");
						files = null;
						i = 0;

						useTTS = false;
						usePcm = false;
						manSpeak = true;

						File pcmdir = new File(Environment
								// 创建录音文件保存路径
								.getExternalStorageDirectory()
								.getAbsolutePath()
								+ "/lasfTest/record/"
								+ domain + "/");

						if (!pcmdir.exists()) {
							pcmdir.mkdirs();
						}

						mapPcm = null; // 清空 PCM属性
						pcmProp = null; // pcm属性文件
						pcmCompare = false; // 是否进行比较结果
						compareResult = null; // 自动播放pcm时比较结果文件
						comWriter = null; // 写入对比结果
						/* 将mySpinner显示 */
						arg0.setVisibility(View.VISIBLE);
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}
				});

		buttonStart.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				click = System.currentTimeMillis();
				// TODO Auto-generated method stub
				if (!(new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/lasfTest/record/")).exists()) {
					(new File(Environment.getExternalStorageDirectory()
							.getAbsolutePath() + "/lasfTest/record/")).mkdirs();
				}

				pcm = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath()
						+ "/lasfTest/record/"
						+ domain
						+ "/"
						+ System.currentTimeMillis() + ".pcm");
				try {
					out = new FileOutputStream(pcm);
				} catch (FileNotFoundException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

				if (ttsfile.exists()) {
					useTTS = true;
					usePcm = false;
					manSpeak = false;

					propTTS = new Properties();

					FileInputStream in;
					try {
						in = new FileInputStream(ttsfile);
						try {
							propTTS.load(in);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					ttsNum = Integer.parseInt((propTTS.get("total")).toString()
							.trim());
					startNum = Integer.parseInt((propTTS.get("startFrom"))
							.toString().trim());

				}
				if ((new File(pcmPath + domain + "/")).exists()
						&& (new File(pcmPath + domain + "/"))
								.listFiles(new FileNameSelector("pcm")).length > 0) {
					useTTS = false;
					usePcm = true;
					manSpeak = false;
					flag = true;
					files = (new File(pcmPath + domain + "/")
							.listFiles(new FileNameSelector("pcm")));

					pcmNum = files.length;

					Log.i("文件", "" + pcmNum);

					if ((new File(pcmPath + domain + "/" + "list.txt")) // 如果有文件属性列表则进行结果对比
							.exists()) {
						pcmCompare = true; // 如果有对比文件则进队对比
						pcmProp = new File(pcmPath + domain + "/" + "list.txt");
						readTxtFile(pcmProp);

						// FileInputStream in;
						// try {
						// in = new FileInputStream(pcmProp); // 读取属性文件
						// try {
						// propPCM.load(in);
						// } catch (IOException e) {
						// // TODO Auto-generated catch block
						// e.printStackTrace();
						// }
						// } catch (FileNotFoundException e1) {
						// // TODO Auto-generated catch block
						// e1.printStackTrace();
						// }
					}
				}

				textResult.setText("");
				textResult.append("点击: " + click + "\r\n");
				speech();

				// String android_id = Secure.getString(getBaseContext()
				// .getContentResolver(), Secure.ANDROID_ID);
				// textResult.setText(android_id);
			}
		});
		buttonStop.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (r != null) {
					r.cancel();
					stop();
				}
				flag = false;
				mHandler.sendEmptyMessageDelayed(CANCLE, 100);

				String android_id = Secure.getString(getBaseContext()
						.getContentResolver(), Secure.ANDROID_ID);
				textResult.setText(android_id);
			}
		});

	}

	private void speech() {

		if (msglen == 9) {
			textResult.setText("");
			msglen = 0;
		}

		if (usePcm == true) {
			initLogic();
		}

		r = SpeechRecognizer.createSpeechRecognizer(this, com);
		r.setRecognitionListener(mReListener);
		Intent recognizerIntent = new Intent();
		if (domain.equals("contacts")) { // 如果是联系人领域把本机联系人加上
			readAllContacts();
			recognizerIntent.putExtra("<main>", new String[] { "呼叫<name>",
					"<name>", "打电话给<name>" });
			recognizerIntent.putExtra("<name>", names);
		}
		recognizerIntent.putExtra("speech_sce", sce);
		recognizerIntent.putExtra("speech_vdm", domain); // 识别联系人领域
		start = System.currentTimeMillis();
		textResult.append("启动:	" + start + "\r\n");
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
				}
			});
		}

		@Override
		public void onResults(Bundle results) {
			endtime = System.currentTimeMillis();

			List<String> rr = results
					.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

			List<String> list = results
					.getStringArrayList("results_recognition");
			float score = results.getFloat("confidence_scores");

			String eng = "";
			if ("com.lenovo.lasf.speech.ThinkitLocalDecoder".equals(results.getString("engine_fullname"))) {
				eng = "本地识别";
			} else {
				eng = "网络识别";
			}

			String jo = results.getString("nlp_result_origin");

			if (domain.equals("vod")) {
				writeVod(jo);
			}
			if (domain.equals("was")) {
				writeWas(jo);
			}
			if (domain.equals("")) {
				try {
					noteWriter = new BufferedWriter(new FileWriter(new File(
							noteResult), true));
					noteWriter.write("\r\n");
					noteWriter.close();

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
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

				// if (domain.equals("")) {

				if (rr.get(0).length() > 10) {
					pcm.renameTo(new File(Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ "/lasfTest/record/"
							+ domain
							+ "/"
							+ System.currentTimeMillis()
							+ "_"
							+ rr.get(0).substring(0, 10).replaceAll("[。，]", "")
							+ ".pcm"));
				} else {
					pcm.renameTo(new File(Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ "/lasfTest/record/"
							+ domain
							+ "/"
							+ System.currentTimeMillis()
							+ "_"
							+ rr.get(0).replaceAll("[。，]", "") + ".pcm"));
				}
				// }

				long time = endtime - begintime;

				if (pcmCompare == true) {
					try {
						comWriter = new BufferedWriter(new FileWriter(new File(
								Environment.getExternalStorageDirectory()
										.getAbsolutePath()
										+ "/lasfTest/result/"
										+ domain
										+ "CompareResult.txt"), true));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					String comres = "";
					// 被识别的文件名、原始命令
					String resultName = (String) mapPcm.get(files[i].getName()
							.replace(".pcm", "").trim());

					if (t.trim().equals(resultName)) {
						comres = "pass";
					} else {
						comres = "fail";
					}
					try {
						comWriter.write(files[i].getName() + "," + resultName
								+ "," + t.replace("\r\n", "").trim() + ","
								+ comres + "\r\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						comWriter.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					textResult.append("语音：" + resultName + "     识别： "
							+ t.replace("\r\n", "") + "   结果：" + comres
							+ "\r\n");
				}

				textResult.append("onResults---:" + t + "\t" + score + eng
						+ "\r\n" + jo + "\r\n" + "使用时间" + time + "\r\n");

				/**** 识别完后开启下一次识别 ****/

				if (i < pcmNum - 1) {
					i++;
				} else {
					mHandler.sendEmptyMessageDelayed(CANCLE, 1000);
					flag = false;
				}
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
					readytime = System.currentTimeMillis();
					long spend = readytime - start;
					textResult.append("ready:	" + readytime + "   耗时：  "
							+ spend + "\r\n");
					textResult.append("onReadyForSpeech	可以说话了\r\n");

					if (useTTS == true) {
						// sp(names[i]);
						sp(propTTS.get(startNum).toString());
					}
					if (usePcm == true) {
						play();

					}
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
						writenote(t);
						textResult.append("part  " + t + "\r\n");
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
						textResult.append("出错了 " + error + "网络超时\r\n");
						break;
					case 2:
						textResult.append("出错了 " + error + "网络错误\r\n");
						break;
					case 3:
						textResult.append("出错了 " + error + "录音出错\r\n");
						break;
					case 4:
						textResult.append("出错了 " + error + "服务器返回错误状态\r\n");
						break;
					case 5:
						textResult.append("出错了 " + error
								+ "客户端调用错误，如识别结果返回之前再次请求识别\r\n");
						break;
					case 6:
						long time = System.currentTimeMillis() - readytime;
						textResult.append("出错了 " + error + "   " + time
								+ "ms 无语音输入\r\n");
						break;
					case 7:
						textResult.append("出错了 " + error
								+ "没有与输入的语音匹配的识别结果\r\n");
						break;
					case 8:
						textResult.append("出错了 " + error + "引擎忙\r\n");
						break;
					default:
						textResult.append("出错了 " + error + "");

					}
					if (flag) {
						mHandler.sendEmptyMessageDelayed(START, 1000);
					}
				}

			});
			trytime++;
			if (trytime == 3) {
				if (i < pcmNum) {
					i++;
				}
				trytime = 0;
			}
		}

		@Override
		public void onEndOfSpeech() {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					try {
						out.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					begintime = System.currentTimeMillis();
					long speaktime = begintime - readytime;
					textResult.append("onEndOfSpeech 收音结束  " + "说话时长  "
							+ speaktime + "ms \r\n");
				}
			});
		}

		@Override
		public void onBufferReceived(final byte[] buffer) {

			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					try {
						out.write(buffer);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// textResult.append("onbuffer");
					// try {
					// textResult.append("onbuffer" + new
					// String(buffer,"UTF-8"));
					// } catch (UnsupportedEncodingException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }
				}
			});

			// TODO Auto-generated method stub

		}

		@Override
		public void onBeginningOfSpeech() {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					textResult
							.append("onBeginningOfSpeech :  检测到语音输入" + "\r\n");
				}
			});
		}
	};

	private void sp(String str) { // 这里实现播放tts
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, UUID.randomUUID()
				.toString());
		// String str = "你好啊，hello 我是TTS";
		mTts.speak(str, TextToSpeech.QUEUE_FLUSH, map);
	}

	@Override
	public void onInit(int status) { // 实现tts一个方法
		if (status != TextToSpeech.SUCCESS) {
			Toast.makeText(this, "tts初始化失败", 2).show();
		}
	}

	public void readAllContacts() { // 读取本机通讯录
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
		Log.i("文件数", "" + i);
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
		try {
			mAudioPlayer.stop();
		} catch (Exception e) {
		}

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

	/*
	 * 获得PCM音频数据
	 */
	public byte[] getPCMData(File file) {
		// try {
		// writer= new BufferedWriter(new FileWriter(new File(
		// file.getParentFile().getParentFile().getAbsolutePath() + "/" +
		// file.getParentFile().getName() + "_res.txt"),true));
		// } catch (IOException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		// try {
		// writer.write(file.getName() + ",");
		// } catch (IOException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }

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

	public void writeVod(String jo) {
		try {
			vodWriter = new BufferedWriter(new FileWriter(new File(vodResult),
					true));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			JSONObject ba = new JSONObject(jo);

			JSONObject result = ba.getJSONObject("result");
			JSONArray objects = result.getJSONArray("object");
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < objects.length(); i++) {
				sb.append(((JSONObject) objects.getJSONObject(i)).get("name"))
						.append("|");
			}
			long time = endtime - begintime;
			try {
				vodWriter.write("置信度:" + ba.getString("confidence") + ",");
				vodWriter.write("原始文本:" + ba.getString("rawText") + ","
						+ sb.toString() + ",识别时间：" + time + "," + jo + "\r\n");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			vodWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writenote(String part) {
		try {
			noteWriter = new BufferedWriter(new FileWriter(
					new File(noteResult), true));
			noteWriter.write(part);
			noteWriter.close();

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void writeWas(String jo) {
		long time = endtime - begintime;
		try {
			wasWriter = new BufferedWriter(new FileWriter(new File(wasResult),
					true));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			JSONObject ba = new JSONObject(jo);

			JSONObject result = ba.getJSONObject("result");
			JSONArray objects = result.getJSONArray("object");
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < objects.length(); i++) {
				if (i > 0) {
					sb.append("	")
							.append("	")
							.append(((JSONObject) objects.getJSONObject(i))
									.get("name"))
							.append("	")
							.append(((JSONObject) objects.getJSONObject(i))
									.get("url"))
							.append("	")
							.append(((JSONObject) objects.getJSONObject(i))
									.get("type"))
							.append("	")
							.append(((JSONObject) objects.getJSONObject(i))
									.get("category")).append("	");
				} else {
					sb.append(
							((JSONObject) objects.getJSONObject(i)).get("name"))
							.append("	")
							.append(((JSONObject) objects.getJSONObject(i))
									.get("url"))
							.append("	")
							.append(((JSONObject) objects.getJSONObject(i))
									.get("type"))
							.append("	")
							.append(((JSONObject) objects.getJSONObject(i))
									.get("category")).append("	")
							.append(result.get("opration")).append("	")
							.append("识别时间：").append(time).append("	")
							.append(jo).append("	\r\n");

				}

			}

			try {
				wasWriter.write("置信度:" + ba.getString("confidence") + "	");
				wasWriter.write("原始文本:" + ba.getString("rawText") + "	"
						+ sb.toString() + "\r\n");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			wasWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void readTxtFile(File file) {
		try {
			mapPcm = new HashMap<String, String>();
			String encoding = "GBK";
			// File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					String kv[] = lineTxt.split(",");
					mapPcm.put(kv[0], kv[1]);

				}
				read.close();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.auto_test, menu);
		menu.add(0, 0, 0, "清空结果（删除文件-慎用）");
		menu.add(0, 1, 1, "关于");
		return super.onCreateOptionsMenu(menu);
		// return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		// 响应每个菜单项(通过菜单项的ID)
		case 0:
			Log.i("菜单", "删除结果");
			new File(noteResult).delete();
			break;
		case 1:
			Log.i("菜单", "关于");
			// new File(noteResult).delete();
			// do something here
			break;
		case 3:
			// do something here
			break;
		case 4:
			// do something here
			break;
		default:
			// 对没有处理的事件，交给父类来处理
			return super.onOptionsItemSelected(item);
		}
		// 返回true表示处理完菜单项的事件，不需要将该事件继续传播下去了
		return true;
	}
}

class FileNameSelector implements FilenameFilter {
	String extension = ".";

	public FileNameSelector(String fileExtensionNoDot) {
		extension += fileExtensionNoDot;
	}

	public boolean accept(File dir, String name) {
		return name.endsWith(extension);
	}

}
