package com.example.helloapplication;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;

import java.util.Date;

/**
 * Created by yuhai.
 */

public class ContactUtils {

    public static void readPhoneContactList(Context context) {

        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts._ID + " ASC");
        try {
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    // 取得联系人名字
                    String contact = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    // 联系人最后修改时间
                    String lastUpdatedTime = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        lastUpdatedTime = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP));
                    }

                    Date date = new Date(Long.parseLong(lastUpdatedTime));

                    // 取得联系人Id
                    String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                    Cursor idCursor = cr.query(ContactsContract.Data.CONTENT_URI, null, ContactsContract.Data.CONTACT_ID + "=" + contactId, null, ContactsContract.Data._ID + " ASC");
                    try {
                        if (idCursor != null && idCursor.getCount() > 0) {
                            while (idCursor.moveToNext()) {
                                // 获取联系人详情类型
                                String mimetype = idCursor.getString(idCursor.getColumnIndex(ContactsContract.Data.MIMETYPE));

                                // 获取联系人详情标签
                                int type = idCursor.getInt(idCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                                String customLabel = idCursor.getString(idCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));

                                switch (mimetype) {
                                    case ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE:
                                        continue;
                                    case ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE:
                                        // label = (String) ContactsContract.CommonDataKinds.Organization.getTypeLabel(context.getResources(), type, customLabel);
                                        break;
                                    case ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE:
                                        break;
                                    case ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE:
                                        break;
                                    case ContactsContract.CommonDataKinds.Relation.CONTENT_ITEM_TYPE:
                                        break;
                                    case ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE:
                                        break;
                                    case ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE:
                                        break;
                                    case ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE:
                                        break;
                                    case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE:
                                        break;
                                }

                                // 获取联系人当前Label对应的详情内容
                                String value = idCursor.getString(idCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
                            }
                        }
                    } finally {
                        if (idCursor != null) {
                            idCursor.close();
                        }
                    }
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
