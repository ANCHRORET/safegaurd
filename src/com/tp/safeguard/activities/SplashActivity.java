package com.tp.safeguard.activities;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.tp.safeguard.R;
import com.tp.safeguard.utils.Constants;
import com.tp.safeguard.utils.FileUtils;
import com.tp.safeguard.utils.PackageInfoUtils;
import com.tp.safeguard.utils.PreferenceUtils;

public class SplashActivity extends Activity {

	private static final int WHAT_SHOW_UDPATE = 1;// 代表显示提示更新的对话框
	private static final int REQUEST_INSTALL_CODE = 101;// 代表发送安装意图的请求码
	private static final int WHAT_NET_ERROR = -1;// 代表网络有问题
	public static final int WHAT_RESPONSE_ERROR = -2;// 代表访问网络响应码不是200
	public static final String TAG = "SplashActivity";
	private TextView tv_version;
	private int versionCode;// 网络上最新版本号
	private String versionDesc;// 网络上最新版本信息
	private String url;// 网络上最新版本的下载地址
	private Handler handler = new Handler() {// TODO 为什么会有警告信息
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_SHOW_UDPATE:
				showUpdate();
				break;
			case WHAT_RESPONSE_ERROR:
				// 提示用户,并进入主页面
			case WHAT_NET_ERROR:
				// 提示用户,并进入主页面
				Toast.makeText(SplashActivity.this, msg.obj.toString(),
						Toast.LENGTH_SHORT).show();
				load2Main();

				break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		tv_version = (TextView) findViewById(R.id.tv_version);
		initData();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_INSTALL_CODE) {
			switch (resultCode) {
			case Activity.RESULT_OK:
				break;
			case Activity.RESULT_CANCELED:
				Toast.makeText(this, "安装失败", Toast.LENGTH_SHORT).show();
				load2Main();
				break;
			default:
				break;
			}
		}

	}

	// 提醒系统更新UI
	protected void updateApp(File file) {
		// 提醒系统更新app

		// TODO 查看老师怎么查找的
		// <intent-filter>
		// <action android:name="android.intent.action.VIEW" />
		// <category android:name="android.intent.category.DEFAULT" />
		// <data android:scheme="content" />
		// <data android:scheme="file" />
		// <data android:mimeType="application/vnd.android.package-archive" />
		// </intent-filter>
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		startActivityForResult(intent, REQUEST_INSTALL_CODE);
	}

	private void initData() {
		// 根据清单的版本设置版本信息.
		String versionName = PackageInfoUtils.getVersionName(this);
		tv_version.setText("版本号:" + versionName);

		// 加载号码归属地数据库文件
		loadNumAddressDB();

		// 加载常用号码的数据库文件
		loadCommonNumDB();
		if (PreferenceUtils.getBoolean(this, Constants.AUTOUPDATE, true)) {
			// 检测网络版本和本地版本,并做一些处理
			new Thread(new CheckVersionTask()).start();
		} else {
			load2Main();
		}

	}

	private void loadCommonNumDB() {
		new Thread() {
			@Override
			public void run() {
				File file = new File(getFilesDir(), "commonnum.db");
				if (file.exists()) {
					// TODO 怎么更新
				} else {
					AssetManager am = getAssets();
					InputStream is = null;
					FileOutputStream fos = null;
					try {
						is = am.open("commonnum.db");
						fos = new FileOutputStream(file);
						byte[] bys = new byte[1024];
						int len = -1;
						while ((len = is.read(bys)) != -1) {
							fos.write(bys, 0, len);
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if (is != null) {
							try {
								is.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							is = null;
						}
						if (fos != null) {
							try {
								fos.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							fos = null;
						}
					}

				}
			}
		}.start();
	}

	private void loadNumAddressDB() {
		new Thread() {
			@Override
			public void run() {
				File file = new File(getFilesDir(), "address.db");
				if (file.exists()) {
					// TODO 怎么更新
				} else {
					AssetManager am = getAssets();
					try {
						InputStream is = am.open("address.zip");
						FileOutputStream fos = new FileOutputStream(file);
						FileUtils.unzip(is, fos);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	public class CheckVersionTask implements Runnable {

		@Override
		public void run() {
			// 去服务器获得最新的版本号
			// 服务器:提供最新的版本信息

			AndroidHttpClient client = AndroidHttpClient.newInstance("tp");
			HttpGet get = new HttpGet("http://192.168.0.5:8080/update.json");
			// 代表设置参数的类
			HttpParams params = client.getParams();
			// 设置连接超时时长
			HttpConnectionParams.setConnectionTimeout(params, 10);
			// 设置相应超时时长
			HttpConnectionParams.setSoTimeout(params, 10);
			// 发送请求
			try {
				HttpResponse response = client.execute(get);
				int status = response.getStatusLine().getStatusCode();
				Log.d(TAG, "status:" + status);
				if (200 == status) {
					// 获取网络版本信息
					String json = EntityUtils.toString(response.getEntity(),
							"utf-8");
					// {"versionCode":2,"versionDesc":"描述","url":"http://192.168.0.5:8080/mobilesafe.apk"}
					Log.d(TAG, json);
					JSONObject jsonObject = new JSONObject(json);
					versionCode = jsonObject.getInt("versionCode");
					int localCode = getPackageManager().getPackageInfo(
							getPackageName(), 0).versionCode;
					if (versionCode > localCode) {
						// 更新 app
						versionDesc = jsonObject.getString("versionDesc");
						url = jsonObject.getString("url");
						Message msg = handler.obtainMessage(WHAT_SHOW_UDPATE);
						msg.sendToTarget();
						// showUpdate();//子线程不能更新ui
					} else {
						// 进入主页面
						load2Main();
					}
				} else {
					Message msg = handler.obtainMessage(WHAT_RESPONSE_ERROR);
					msg.obj = "Error:" + status;// 连接网络响应码不是200
					msg.sendToTarget();
				}
				// TODO 为什么和老师的异常不同
			} catch (IOException e) {
				// 没有连接好网络
				Message msg = handler.obtainMessage(WHAT_NET_ERROR);
				msg.obj = "Error:1002";// 没能和服务器连接上
				msg.sendToTarget();

			} catch (JSONException e) {

				Message msg = handler.obtainMessage(WHAT_NET_ERROR);
				msg.obj = "Error:1003";// 获得的数据没能解析好
				msg.sendToTarget();

			} catch (NameNotFoundException e) {

				Message msg = handler.obtainMessage(WHAT_NET_ERROR);
				msg.obj = "Error:1004";// 获得的数据没能解析好
				msg.sendToTarget();
			}
			client.close();// AndroidHttpClient需要关闭
		}

	}

	/**
	 * 跳到主页面
	 */
	public void load2Main() {
		new Thread() {
			@Override
			public void run() {
				SystemClock.sleep(2000);
//				try {
//					Thread.sleep(2000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}// 让用户看1s的Logo
				Intent intent = new Intent(SplashActivity.this,
						HomeActivity.class);
				startActivity(intent);
				finish();
			}
		}.start();
	}

	/**
	 * 弹出对话框询问用户是否要更新
	 */
	public void showUpdate() {
		Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle("更新信息");
		builder.setMessage(versionDesc);
		builder.setPositiveButton("确定更新", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 下载最新的apk
				downloadUpdate();
			}
		});
		builder.setNegativeButton("稍后再说", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 不更新

				Toast.makeText(getApplicationContext(), "有新的版本未更新...",
						Toast.LENGTH_SHORT).show();
				load2Main();
			}
		});
		builder.show();
	}

	/**
	 * 下载更新
	 */
	protected void downloadUpdate() {
		// 创建一个进度条来表示下载
		final ProgressDialog proDialog = new ProgressDialog(this);
		proDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		proDialog.setCancelable(false);
		proDialog.show();// TODO 为什么不能在线程内让其显示???
		// 开启新线程下载更新
		new Thread() {
			@Override
			public void run() {
				AndroidHttpClient client = AndroidHttpClient.newInstance("tp");
				HttpGet get = new HttpGet(url);
				HttpParams params = client.getParams();
				HttpConnectionParams.setConnectionTimeout(params, 5 * 1000);
				HttpConnectionParams.setSoTimeout(params, 5 * 1000);
				InputStream is = null;
				FileOutputStream fos = null;
				// 发送请求
				try {
					HttpResponse response = client.execute(get);
					int status = response.getStatusLine().getStatusCode();
					Log.d(TAG, "下载status:" + status);
					if (200 == status) {
						int max = (int) response.getEntity().getContentLength();
						proDialog.setMax(max);
						int progress = 0;
						proDialog.setProgress(progress);
						// 处理流中的数据
						is = response.getEntity().getContent();
						String path = null;
						String state = Environment.getExternalStorageState();
						if (Environment.MEDIA_MOUNTED.equals(state)) {
							path = Environment.getExternalStorageDirectory()
									.getAbsolutePath()
									+ "/"
									+ System.currentTimeMillis() + ".apk";
						} else {
							// TODO 如果没安装sd卡
						}
						fos = new FileOutputStream(path);
						byte[] bys = new byte[1024];
						int len = 0;
						while ((len = is.read(bys)) > 0) {
							fos.write(bys, 0, len);
							progress += len;
							proDialog.setProgress(progress);
						}
						proDialog.dismiss();
						// 提醒系统更新app
						updateApp(new File(path));
					} else {
						proDialog.dismiss();
						Message msg = handler
								.obtainMessage(WHAT_RESPONSE_ERROR);
						msg.obj = "Error:" + status;// 连接网络响应码不是200
						msg.sendToTarget();
					}
				} catch (IOException e) {// 还是和老师的异常不同
					// 没有连接好网络
					Message msg = handler.obtainMessage(WHAT_NET_ERROR);
					msg.obj = "Error:1002";// 没能和服务器连接上
					msg.sendToTarget();
				} finally {
					closeIO(is);
					closeIO(fos);
				}
				client.close();

			}
		}.start();
	}

	protected void closeIO(Closeable closeable) {
		// 关闭可关闭的资源
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			closeable = null;
		}
	}

}
