package com.tp.safeguard.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tp.safeguard.bean.SmsBean;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;

public class SmsUtils {

	public interface OnOperateProgressListener {

		public abstract void onGetMax(int max);

		void onProcessUpdate(int progress);

		public abstract void onOperateFinish();

		public abstract void onErrorOccur(Exception e);

	}

	/**
	 * 将数据备份到sd卡中
	 * 
	 * @param context
	 *            传入上下文变量
	 * @param listener
	 *            设置监听器,null代表不设置监听器
	 */
	public static void backupSms(final Context context,
			final OnOperateProgressListener listener) {
		new AsyncTask<Void, Integer, Exception>() {
			@Override
			protected void onPreExecute() {
			};

			@Override
			protected void onPostExecute(Exception result) {
				if (listener != null) {
					if (result != null) {
						listener.onErrorOccur(result);
					} else {
						listener.onOperateFinish();
					}
				}
			};

			@Override
			protected void onProgressUpdate(Integer... values) {
				if (listener != null) {

					if (values[0] == 0) {
						listener.onGetMax(values[1]);
					} else if (values[0] == 1) {
						listener.onProcessUpdate(values[1]);
					}
				}
			};

			@Override
			protected Exception doInBackground(Void... params) {

				ContentResolver resolver = context.getContentResolver();
				Uri uri = Uri.parse("content://sms");
				String[] projection = { "address", "date", "read", "type",
						"body" };
				Cursor cursor = resolver.query(uri, projection, null, null,
						null);

				List<SmsBean> list = null;
				if (cursor != null) {
					publishProgress(0, cursor.getCount());
					list = new ArrayList<SmsBean>();
					while (cursor.moveToNext()) {
						String smsAddress = cursor.getString(0);
						Long smsDate = cursor.getLong(1);
						int smsRead = cursor.getInt(2);
						int smsType = cursor.getInt(3);
						String smsBody = cursor.getString(4);
						SmsBean bean = new SmsBean();
						bean.smsAddress = smsAddress;
						bean.smsBody = smsBody;
						bean.smsDate = smsDate;
						bean.smsRead = smsRead;
						bean.smsType = smsType;
						list.add(bean);
						SystemClock.sleep(100);
						publishProgress(1, list.size());
					}
					cursor.close();
				}

				Gson gson = new Gson();
				String json = gson.toJson(list);
				File file = new File(Environment.getExternalStorageDirectory(),
						"sms.json");
				FileWriter writer = null;
				try {
					writer = new FileWriter(file);
					writer.write(json.toCharArray());
				} catch (IOException e) {
					e.printStackTrace();
					return e;
				} finally {
					if (writer != null) {
						try {
							writer.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						writer = null;
					}
				}
				return null;

			}
		}.execute();
	}

	/**
	 * 将短信内容从备份的时间还原
	 * 
	 * @param context
	 *            传入的上下文变量
	 * @param listener
	 *            设置监听器,null代表不设置监听器
	 */
	public static void smsRestore(final Context context,
			final OnOperateProgressListener listener) {

		new AsyncTask<Void, Integer, Exception>() {

			@Override
			protected void onProgressUpdate(Integer... values) {
				super.onProgressUpdate(values);
				if (listener != null) {
					if (values[0] == 0) {
						listener.onGetMax(values[1]);
					} else if (values[0] == 1) {
						listener.onProcessUpdate(values[1]);
					}
				}
			}

			@Override
			protected void onPostExecute(Exception result) {
				super.onPostExecute(result);
				if (listener != null) {
					if (result != null) {
						listener.onErrorOccur(result);
					} else {
						listener.onOperateFinish();
					}
				}

			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected Exception doInBackground(Void... params) {
				File file = new File(Environment.getExternalStorageDirectory(),
						"sms.json");
				if (!file.exists()) {
					return new Exception("不存在备份文件");
				}

				BufferedReader reader = null;
				try {
					reader = new BufferedReader(new FileReader(file));
					String json = reader.readLine();
					Gson gson = new Gson();
					List<SmsBean> list = gson.fromJson(json,
							new TypeToken<List<SmsBean>>() {
							}.getType());

					if (list == null) {
						return new Exception("短信备份内容为空");
					}
					publishProgress(0, list.size());

					ContentResolver resolver = context.getContentResolver();
					Uri url = Uri.parse("content://sms");
					for (int i = 1; i < list.size(); i++) {
						SmsBean bean = list.get(i);
						ContentValues values = new ContentValues();
						// "address", "date", "read", "type","body"
						values.put("address", bean.smsAddress);
						values.put("date", bean.smsDate);
						values.put("read", bean.smsRead);
						values.put("type", bean.smsType);
						values.put("body", bean.smsBody);
						resolver.insert(url, values);
						publishProgress(1, i);
					}

				} catch (Exception e) {
					e.printStackTrace();
					return e;
				}
				return null;
			}
		}.execute();

	}
}
