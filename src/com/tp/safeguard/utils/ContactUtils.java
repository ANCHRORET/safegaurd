package com.tp.safeguard.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.tp.safeguard.bean.ContactBean;

public class ContactUtils {

	private static final String TAG = "ContactUtils";

	/**
	 * 查询到所有的联系人数据
	 * 
	 * @param context
	 *            上下文变量
	 * @return 返回包含联系人信息的List集合
	 */
	public static List<ContactBean> queryAll(Context context) {
		// 获取到所有的联系人信息{_ID,name,num,icon}
		ContentResolver resolver = context.getContentResolver();

		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String[] projection = new String[] {
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.NUMBER,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID };
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = null;
		Cursor cursor = resolver.query(uri, projection, selection,
				selectionArgs, sortOrder);
		List<ContactBean> list = null;
		if (cursor != null) {
			list = new ArrayList<ContactBean>();
			ContactBean bean;
			while (cursor.moveToNext()) {
				String name = cursor.getString(0);
				String num = cursor.getString(1);
				long contactID = cursor.getLong(2);
				bean = new ContactBean();
				bean.name = name;
				bean.num = num;
				bean.contactID = contactID;
				list.add(bean);
			}
			cursor.close();
		}
		return list;
	}

	/**
	 * 根据id查询到联系人的图片数据,
	 * 
	 * @param context
	 *            所需的上下文变量
	 * @return 返回一张联系人的Bitmap图片
	 */

	public static Bitmap getContactPhoto(Context context, long contactID) {
		ContentResolver resolver = context.getContentResolver();
		//上面的uri里找不到图片,所以在这里
		Uri uri = Uri.withAppendedPath(
				ContactsContract.Contacts.CONTENT_URI, ""
						+ contactID);
		InputStream is = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
		return BitmapFactory.decodeStream(is);
	}
}
