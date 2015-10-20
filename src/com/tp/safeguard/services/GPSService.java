package com.tp.safeguard.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.tp.safeguard.utils.Constants;
import com.tp.safeguard.utils.PreferenceUtils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

public class GPSService extends Service {

	protected static final String TAG = "GPSService";
	private LocationManager mlm;
	private LocationListener mllistener;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// 获得位置管理器
		mlm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// 位置监听器
		mllistener = new LocationListener() {

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			@Override
			public void onProviderEnabled(String provider) {
			}

			@Override
			public void onProviderDisabled(String provider) {
			}

			@Override
			public void onLocationChanged(Location location) {
				// 当位置改变时调用,主要是有代表位置的参数传进来
				double longitude = location.getLongitude();// 获得经度
				double latitude = location.getLatitude();// 获得纬度

				sendRealLocation(latitude, longitude);
			}
		};
		// 为位置管理器设置监听器,记得移出监听器
		mlm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				mllistener);
	}

	protected void sendRealLocation(final double latitude,
			final double longitude) {
		// 发送获得的真实位置给安全号码
		new Thread() {
			@Override
			public void run() {
				// 通过网络获取实际地址,并通过短信发送给安全号码
				InputStream is = null;
				ByteArrayOutputStream baos = null;
				// "http://lbs.juhe.cn/api/getaddressbylngb?lngx="
				// + longitude + "&lngy=" + latitude
				try {
					URL url = new URL(
							"http://lbs.juhe.cn/api/getaddressbylngb?lngx="
									+ longitude + "&lngy=" + latitude);
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setConnectTimeout(5 * 1000);
					conn.setReadTimeout(5 * 1000);

					int code = conn.getResponseCode();
					if (code == 200) {
						is = conn.getInputStream();
						baos = new ByteArrayOutputStream();
						byte[] bys = new byte[1024];
						int len = 0;
						while ((len = is.read(bys)) != -1) {
							baos.write(bys, 0, len);
						}
						String json = baos.toString("utf-8");
						JSONObject jsonObject = new JSONObject(json);
						JSONObject jsonObjectRow = jsonObject
								.getJSONObject("row");
						JSONObject jsonObjectResult = jsonObjectRow
								.getJSONObject("result");
						String address = jsonObjectResult
								.getString("formatted_address");
						sendSms(address);
					} else {
						// 没有正确获得位置信息,发送经纬度
						sendSms("longitude:" + longitude + "---latitude:"
								+ latitude);
					}
				} catch (Exception e) {
					// 没有正确获得位置信息,发送经纬度
					sendSms("longitude:" + longitude + "---latitude:"
							+ latitude);
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
					if (baos != null) {
						try {
							baos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						baos = null;
					}
					stopSelf();
				}
			}
		}.start();

	}

	/**
	 * 将地址信息发送给安全号码
	 * 
	 * @param address
	 *            地址信息
	 */
	protected void sendSms(String address) {
		SmsManager smsManager = SmsManager.getDefault();
		String destinationAddress = PreferenceUtils.getString(GPSService.this,
				Constants.SJFD_SAFENUM, "");
		smsManager.sendTextMessage(destinationAddress, null, address, null,
				null);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Log.d(TAG, "GPS服务停止");
		mlm.removeUpdates(mllistener);

	}
}
