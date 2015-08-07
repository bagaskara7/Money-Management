package com.andronomy.moneymanagement.Adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by bagaskara on 7/2/2015.
 */
public class CategoryAdapter {

    DatabaseCategory databaseCategory;

    public CategoryAdapter(Context context) {
        databaseCategory = new DatabaseCategory(context);
    }

    public long insertData(String name, String image, String type, String status) {
        SQLiteDatabase db = databaseCategory.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseCategory.NAME, name);
        contentValues.put(DatabaseCategory.IMAGE, image);
        contentValues.put(DatabaseCategory.TYPE, type);
        contentValues.put(DatabaseCategory.STATUS, status);

        return db.insert(DatabaseCategory.TABLE_NAME, null, contentValues);
    }

    static class DatabaseCategory extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "MoneyManagement";
        private static final int DATABASE_VERSION = 1;
        private static final String TABLE_NAME = "ref_categories";
        private static final String UID = "_id";
        private static final String NAME = "name";
        private static final String IMAGE = "image";
        private static final String TYPE = "type";
        private static final String STATUS = "status";
        private static final String QUERY_CREATE = "CREATE TABLE " + TABLE_NAME + " (" + UID + " INTEGER PRIMARY KEY, " + NAME + " VARCHAR(100), " + IMAGE + " VARCHAR(255), " + TYPE + " TINYINT(1), " + STATUS + " TINNYINT(1))";
        private static final String QUERY_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public DatabaseCategory(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(QUERY_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(QUERY_DROP);
            onCreate(db);
        }
    }
}
