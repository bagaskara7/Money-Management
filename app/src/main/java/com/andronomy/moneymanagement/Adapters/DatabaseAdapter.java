package com.andronomy.moneymanagement.Adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.andronomy.moneymanagement.Lists.Accounts;
import com.andronomy.moneymanagement.Lists.ItemData;
import com.andronomy.moneymanagement.R;
import com.github.mikephil.charting.data.Entry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.andronomy.moneymanagement.Lists.Histories;

/**
 * Created by bagaskara on 7/3/2015.
 * Money Management Project
 */
public class DatabaseAdapter {
    DatabaseHelper databaseHelper;
    Context context;

    public DatabaseAdapter(Context context) {
        databaseHelper = new DatabaseHelper(context);
        this.context = context;
    }

    // Transaction Main
    public long insertTransData(String type, String account, String category, String amount, String notes, String date) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.ACCOUNT, account);
        contentValues.put(DatabaseHelper.CATEGORY, category);
        contentValues.put(DatabaseHelper.AMOUNT, amount);
        contentValues.put(DatabaseHelper.NOTES, notes);
        contentValues.put(DatabaseHelper.TYPE, type);
        contentValues.put(DatabaseHelper.DATE, date);

        return db.insert(DatabaseHelper.TABLE_TRANS, null, contentValues);
    }

    public ArrayList<Entry> getChart(String type) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        String query = "SELECT SUM(`amount`) AS `amount`, strftime('%d', DATE(`date`)) AS `day` FROM `trans_main` WHERE `type` = ? AND strftime('%m', `date`) = ? GROUP BY DATE(`date`) ORDER BY `date` ASC";
        String[] args = {type, sdf.format(new Date(System.currentTimeMillis()))};
        Cursor cursor = db.rawQuery(query, args);
        ArrayList<Entry> array = new ArrayList<>();

        while(cursor.moveToNext()) {
            array.add(new Entry(cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.AMOUNT)), Integer.valueOf(cursor.getString(cursor.getColumnIndex("day"))) - 1));
        }

        cursor.close();
        return array;
    }

    public ArrayList<Entry> getPieChart() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        String query = "SELECT SUM(`t`.`amount`) AS `amount`, `c`.`_id` FROM `trans_main` `t` INNER JOIN `ref_categories` `c` ON (`t`.`category` = `c`.`_id`) WHERE strftime('%m', `date`) = ? AND `c`.`type` = 'o' GROUP BY DATE(`t`.`category`) ORDER BY `t`.`date` ASC";
        String[] args = { sdf.format(new Date(System.currentTimeMillis())) };
        Cursor cursor = db.rawQuery(query, args);
        ArrayList<Entry> array = new ArrayList<>();

        while (cursor.moveToNext()) {
            array.add(new Entry(cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.AMOUNT)), cursor.getInt(cursor.getColumnIndex(DatabaseHelper.UID))));
        }

        cursor.close();
        return array;
    }

    public ArrayList<String> getPieChartTitle(Context context) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        String query = "SELECT `c`.`name` AS `cat_name`, `c`.`_id` AS `id` FROM `trans_main` `t` INNER JOIN `ref_categories` `c` ON (`t`.`category` = `c`.`_id`) WHERE strftime('%m', `date`) = ? AND `c`.`type` = 'o' GROUP BY DATE(`t`.`category`) ORDER BY `t`.`date` ASC";
        String[] args = { sdf.format(new Date(System.currentTimeMillis())) };
        Cursor cursor = db.rawQuery(query, args);
        ArrayList<String> array = new ArrayList<>();

        String name = null;
        while (cursor.moveToNext()) {
            if (cursor.getInt(cursor.getColumnIndex("id")) < 20) {
                int names = context.getResources().getIdentifier("category_" + cursor.getString(cursor.getColumnIndex("cat_name")), "string", context.getPackageName());
                name = context.getString(names);
            } else {
                char[] names = cursor.getString(cursor.getColumnIndex("cat_name")).toCharArray();
                names[0] = Character.toUpperCase(names[0]);

                name = new String(names);
            }
            array.add(name);
        }

        cursor.close();
        return array;
    }

    public float getTransaction(String type) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String query = "SELECT SUM(`amount`) AS `amount` FROM `trans_main` WHERE `type` = ?";
        String[] args = {type};
        Cursor cursor = db.rawQuery(query, args);
        float amount;

        cursor.moveToFirst();
        amount = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.AMOUNT));

        cursor.close();
        return amount;
    }

    public float getTransaction(String type, String account) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String query = "SELECT SUM(`amount`) AS `amount` FROM `trans_main` WHERE `type` = ? AND `account` = ?";
        String[] args = {type, account};
        Cursor cursor = db.rawQuery(query, args);
        float amount;

        cursor.moveToFirst();
        amount = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.AMOUNT));

        cursor.close();
        return amount;
    }

    public List<Histories> getTransData(String type) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String SEL_QUERY = "SELECT *, rc.name AS category, ra.name AS account, rc.image AS image FROM trans_main tm INNER JOIN ref_categories rc ON tm.category = rc._id INNER JOIN ref_accounts ra ON tm.account = ra._id WHERE rc.status = ? AND tm.type = ? ORDER BY tm.date DESC";

        String[] args = {"1", type};
        Cursor cursor = db.rawQuery(SEL_QUERY, args);

        List<Histories> data = new ArrayList<>();

        while(cursor.moveToNext()) {
            Histories current = new Histories();
            current.iconId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.IMAGE));
            current.account = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ACCOUNT));
            current.category = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CATEGORY));
            current.amount = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.AMOUNT));
            current.notes = cursor.getString(cursor.getColumnIndex(DatabaseHelper.NOTES));
            current.date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATE));
            data.add(current);
        }

        cursor.close();
        return data;
    }

    public List<Accounts> getAccountsData() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String SEL_QUERY = "SELECT * FROM `ref_accounts`";
        String SEL_QUERY_DATE = "SELECT * FROM `trans_main` WHERE `account` = ? ORDER BY `date` DESC";
        String SEL_QUERY_BAL = "SELECT SUM(`amount`) AS `amount` FROM `trans_main` WHERE `account` = ? AND `type` = ? ORDER BY `date` DESC";
        String SEL_QUERY_TRANS_FROM = "SELECT SUM(`amount`) AS `amount` FROM `trans_transfer` WHERE `account_from` = ?";
        String SEL_QUERY_TRANS_TO = "SELECT SUM(`amount`) AS `amount` FROM `trans_transfer` WHERE `account_to` = ?";

        Cursor cursor = db.rawQuery(SEL_QUERY, null);

        List<Accounts> data = new ArrayList<>();

        while(cursor.moveToNext()) {
            String date;
            float balance;
            Accounts current = new Accounts();
            Cursor cursor1 = db.rawQuery(SEL_QUERY_DATE, new String[] {cursor.getInt(cursor.getColumnIndex(DatabaseHelper.UID)) + ""});

            cursor1.moveToFirst();
            if (cursor1.isBeforeFirst()) {
                date = context.getString(R.string.never_str);
            } else {
                date = cursor1.getString(cursor1.getColumnIndex(DatabaseHelper.DATE));
            }

            Cursor cursorIncome = db.rawQuery(SEL_QUERY_BAL, new String[] { cursor.getInt(cursor.getColumnIndex(DatabaseHelper.UID)) + "", "i" });
            Cursor cursorExpense = db.rawQuery(SEL_QUERY_BAL, new String[] { cursor.getInt(cursor.getColumnIndex(DatabaseHelper.UID)) + "", "o" });
            Cursor cursorTransFrom = db.rawQuery(SEL_QUERY_TRANS_FROM, new String[] { cursor.getInt(cursor.getColumnIndex(DatabaseHelper.UID)) + "" });
            Cursor cursorTransTo = db.rawQuery(SEL_QUERY_TRANS_TO, new String[] { cursor.getInt(cursor.getColumnIndex(DatabaseHelper.UID)) + "" });
            cursorIncome.moveToFirst();
            cursorExpense.moveToFirst();
            cursorTransFrom.moveToFirst();
            cursorTransTo.moveToFirst();

            balance = cursorIncome.getFloat(cursorIncome.getColumnIndex(DatabaseHelper.AMOUNT)) - cursorExpense.getFloat(cursorExpense.getColumnIndex(DatabaseHelper.AMOUNT)) - cursorTransFrom.getFloat(cursorTransFrom.getColumnIndex(DatabaseHelper.AMOUNT)) + cursorTransTo.getFloat(cursorTransTo.getColumnIndex(DatabaseHelper.AMOUNT));

            current.acc_id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.UID));
            current.name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME));
            current.image = cursor.getString(cursor.getColumnIndex(DatabaseHelper.IMAGE));
            current.date = date;
            current.balance = balance;
            data.add(current);

            cursor1.close();
            cursorIncome.close();
            cursorExpense.close();
            cursorTransFrom.close();
            cursorTransTo.close();
        }

        cursor.close();
        return data;
    }

    public ArrayList<ItemData> getCatsData(String type) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        String[] columns = {DatabaseHelper.UID, DatabaseHelper.NAME, DatabaseHelper.IMAGE};

        ArrayList<ItemData> data = new ArrayList<>();
        Cursor cursor = db.query(DatabaseHelper.TABLE_CATEGORY, columns, DatabaseHelper.TYPE + "= '" + type + "'", null, null, null, DatabaseHelper.NAME + " ASC");

        while(cursor.moveToNext()) {
            data.add(new ItemData(cursor.getString(1), cursor.getString(2), cursor.getInt(0)));
        }

        cursor.close();
        return data;
    }

    public ArrayList<ItemData> getAccsData() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String[] columns = {DatabaseHelper.UID, DatabaseHelper.NAME, DatabaseHelper.IMAGE};
        ArrayList<ItemData> data = new ArrayList<>();
        Cursor cursor = db.query(DatabaseHelper.TABLE_ACCOUNT, columns, DatabaseHelper.STATUS + "= 1", null, null, null, DatabaseHelper.NAME + " ASC");

        while(cursor.moveToNext()) {
            data.add(new ItemData(cursor.getString(1), cursor.getString(2), cursor.getInt(0)));
        }

        cursor.close();
        return data;
    }
    // End of Transaction Main

    // Transaction Main
    public long insertTransferData(String acc_from, String acc_to, String amount, String date) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.ACC_FROM, acc_from);
        contentValues.put(DatabaseHelper.ACC_TO, acc_to);
        contentValues.put(DatabaseHelper.AMOUNT, amount);
        contentValues.put(DatabaseHelper.DATE, date);

        return db.insert(DatabaseHelper.TABLE_TRANSFER, null, contentValues);
    }

    public List<Histories> getTransferData() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        List<Histories> data = new ArrayList<>();
        String query = "SELECT * FROM `trans_transfer`";
        String querys = "SELECT * FROM `ref_accounts` WHERE `_id` = ?";

        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            Histories current = new Histories();
            Cursor cursor1 = db.rawQuery(querys, new String[]{ cursor.getString(cursor.getColumnIndex(DatabaseHelper.ACC_FROM)) });
            Cursor cursor2 = db.rawQuery(querys, new String[]{ cursor.getString(cursor.getColumnIndex(DatabaseHelper.ACC_TO)) });

            cursor1.moveToFirst();
            cursor2.moveToFirst();

            current.iconId = cursor1.getString(cursor1.getColumnIndex(DatabaseHelper.IMAGE));
            current.iconIdTo = cursor2.getString(cursor2.getColumnIndex(DatabaseHelper.IMAGE));
            current.acc_from = cursor1.getString(cursor1.getColumnIndex(DatabaseHelper.NAME));
            current.acc_to = cursor2.getString(cursor2.getColumnIndex(DatabaseHelper.NAME));
            current.amount = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.AMOUNT));
            current.date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATE));
            data.add(current);

            cursor1.close();
            cursor2.close();
        }

        cursor.close();
        return data;
    }
    // End of Transaction Main

    // Locale
    public ArrayList<ItemData> getLocales(String type) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        if (type.equals("lang")) {
            String[] columns = { DatabaseHelper.UID, DatabaseHelper.CODE, DatabaseHelper.NAME, DatabaseHelper.ACTIVE_LANG };
            Cursor cursor = db.query(DatabaseHelper.TABLE_LOCALE, columns, DatabaseHelper.LANG + " = 1 AND " + DatabaseHelper.STATUS + " = 1", null, null, null, null);

            ArrayList<ItemData> data = new ArrayList<>();
            while (cursor.moveToNext()) {
                ItemData loc = new ItemData(cursor.getString(2).toLowerCase() + "_" + cursor.getString(1).toLowerCase(), cursor.getString(2).toLowerCase() + "_" + cursor.getString(1).toLowerCase(), cursor.getInt(0));

                data.add(loc);
            }

            cursor.close();
            return data;
        } else if (type.equals("currency")) {
            String[] columns = { DatabaseHelper.UID, DatabaseHelper.CODE, DatabaseHelper.NAME, DatabaseHelper.CURRENCY_CODE, DatabaseHelper.ACTIVE };
            Cursor cursor = db.query(DatabaseHelper.TABLE_LOCALE, columns, DatabaseHelper.STATUS + " = 1", null, null, null, null);

            ArrayList<ItemData> data = new ArrayList<>();
            while (cursor.moveToNext()) {
                ItemData loc = new ItemData(cursor.getString(3).toLowerCase(), cursor.getString(2).toLowerCase() + "_" + cursor.getString(1).toLowerCase(), cursor.getInt(0));

                data.add(loc);
            }

            cursor.close();
            return data;
        }
        return null;
    }

    public String getActiveLang() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM `ref_locales` WHERE `lang_active` = 1", null);

        cursor.moveToFirst();
        String lang = cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)) + "_" + cursor.getString(cursor.getColumnIndex(DatabaseHelper.CODE));

        cursor.close();
        return lang;
    }

    public String getActiveCurr() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM `ref_locales` WHERE `active` = 1", null);

        cursor.moveToFirst();
//        String curr = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CURRENCY_CODE));
        String curr = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CURRENCY_CODE));
        cursor.close();
        return curr;
    }

    public int getActiveLang(String s) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM `ref_locales` WHERE `lang_active` = 1", null);

        cursor.moveToFirst();
        int lang = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.UID));

        cursor.close();
        return lang - 1;
    }

    public int getActiveCurr(String s) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM `ref_locales` WHERE `active` = 1", null);

        cursor.moveToFirst();
        int curr = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.UID));
        cursor.close();

        int minus = 1;
        if (curr > 132) {
            minus = 8;
        } else if (curr < 133 && curr > 114) {
            minus = 7;
        } else if (curr < 115 && curr > 101) {
            minus = 6;
        } else if (curr < 102 && curr > 64) {
            minus = 5;
        } else if (curr < 65 && curr > 61) {
            minus = 4;
        } else if(curr < 62 && curr > 42) {
            minus = 3;
        } else if (curr < 43 && curr > 15) {
            minus = 2;
        }

        return curr - minus;
    }

    public void setLangAsActive(String id) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.ACTIVE_LANG, 1);

        ContentValues contentValues2 = new ContentValues();
        contentValues2.put(DatabaseHelper.ACTIVE_LANG, 0);

        db.update(DatabaseHelper.TABLE_LOCALE, contentValues2, null, null);
        db.update(DatabaseHelper.TABLE_LOCALE, contentValues, DatabaseHelper.UID + " = ?", new String[]{id});
    }

    public void setCurrAsActive(String id) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.ACTIVE, 1);

        ContentValues contentValues2 = new ContentValues();
        contentValues2.put(DatabaseHelper.ACTIVE, 0);

        db.update(DatabaseHelper.TABLE_LOCALE, contentValues2, null, null);
        db.update(DatabaseHelper.TABLE_LOCALE, contentValues, DatabaseHelper.UID + " = ?", new String[]{id});
    }
    // End of Locale

    /*
     * TODO: GET RID OF THESE FUCKIN' THINGS! BRING IT ON!
     */
    static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "MoneyManagement";
        private static final int DATABASE_VERSION = 2;

        // Transaction Main Table
        private static final String TABLE_TRANS = "trans_main";
        private static final String UID = "_id";
        private static final String ACCOUNT = "account";
        private static final String CATEGORY = "category";
        private static final String AMOUNT = "amount";
        private static final String NOTES = "notes";
        private static final String TYPE = "type";
        private static final String DATE = "date";
        private static final String IN_DATE = "date_in";
        private static final String QUERY_CREATE_TRANS = createTableQuery(TABLE_TRANS, UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ACCOUNT + " INTEGER, " + CATEGORY + " INTEGER, " + AMOUNT + " DOUBLE, " + NOTES + " TEXT, " + TYPE+ " CHAR(1), " + DATE + " DATETIME, " + IN_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP");
        // End of Transaction Main Table

        // Transaction Transfer Table
        private static final String TABLE_TRANSFER = "trans_transfer";
        private static final String ACC_FROM = "account_from";
        private static final String ACC_TO = "account_to";
        private static final String QUERY_CREATE_TRANSFER = createTableQuery(TABLE_TRANSFER, UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ACC_FROM + " INTEGER, " + ACC_TO + " INTEGER, " + AMOUNT + " DOUBLE, " + DATE + " DATETIME, " + IN_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP");
        // End of Transaction Transfer Table

        // Currencies Table
        private static final String TABLE_LOCALE = "ref_locales";
        private static final String CODE = "code";
        private static final String CURRENCY_CODE = "currency_code";
        private static final String NAME = "name";
        private static final String STATUS = "status";
        private static final String LANG = "lang";
        private static final String ACTIVE_LANG = "lang_active";
        private static final String ACTIVE = "active";
        private static final String QUERY_DROP_LOCALE = dropTableQuery(TABLE_LOCALE);
        private static final String QUERY_CREATE_LOCALE = createTableQuery(TABLE_LOCALE, UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CODE + " VARCHAR(10) NULL, " + CURRENCY_CODE + " VARCHAR(3) NULL, " + NAME + " VARCHAR(50) NULL, " + LANG + " TINYINT(1), " + STATUS + " TINYINT(1), " + ACTIVE + " TINYINT(1), " + ACTIVE_LANG + " TINYINT(1)");
        private static final String QUERY_INSERT_LOCALE = "INSERT INTO " + TABLE_LOCALE + " (" +
                NAME + ", " + CODE + ", " + CURRENCY_CODE + ", " + STATUS + ", " + ACTIVE + ", " + LANG + ", " + ACTIVE_LANG + ") VALUES " +
                "('ar', 'SA', 'AED', 1, 0, 0, 0), " +
                "('af', 'ZA', 'AFN', 1, 0, 0, 0), " +
                "('sq', 'AL', 'ALL', 1, 0, 0, 0), " +
                "('en', 'AN', 'ANG', 1, 0, 0, 0), " +
                "('en', 'AO', 'AOA', 1, 0, 0, 0), " +
                "('es', 'AR', 'ARS', 1, 0, 0, 0), " +
                "('en', 'AU', 'AUD', 1, 0, 0, 0), " +
                "('de', 'AT', '', 0, 0, 1, 0), " +
                "('aw', 'AW', 'AWG', 1, 0, 0, 0), " +
                "('az', 'AZ', 'AZN', 1, 0, 0, 0), " +
                "('ba', 'BA', 'BAM', 1, 0, 0, 0), " +
                "('bb', 'BB', 'BBD', 1, 0, 0, 0), " +
                "('', '', 'AMD', 1, 0, 0, 0), " +
                "('', '', 'BDT', 1, 0, 0, 0), " +
                "('bg', 'BG', '', 0, 0, 1, 0), " +
                "('', '', 'BGN', 1, 0, 0, 0), " +
                "('', '', 'BTN', 1, 0, 0, 0), " +
                "('', '', 'BWP', 1, 0, 0, 0), " +
                "('', '', 'BYR', 1, 0, 0, 0), " +
                "('', '', 'BZD', 1, 0, 0, 0), " +
                "('', '', 'CAD', 1, 0, 0, 0), " +
                "('', '', 'CDF', 1, 0, 0, 0), " +
                "('de', 'CH', 'CHF', 1, 0, 0, 0), " +
                "('', '', 'CLP', 1, 0, 0, 0), " +
                "('zh', 'CN', 'CNY', 1, 0, 0, 0), " +
                "('', '', 'COP', 1, 0, 0, 0), " +
                "('', '', 'CRC', 1, 0, 0, 0), " +
                "('', '', 'CUC', 1, 0, 0, 0), " +
                "('', '', 'CUP', 1, 0, 0, 0), " +
                "('', '', 'CVE', 1, 0, 0, 0), " +
                "('cs', 'CZ', 'CZK', 1, 0, 0, 0), " +
                "('', '', 'DJF', 1, 0, 0, 0), " +
                "('da', 'DK', 'DKK', 1, 0, 0, 0), " +
                "('', '', 'DOP', 1, 0, 0, 0), " +
                "('', '', 'DZD', 1, 0, 0, 0), " +
                "('ar', 'EG', 'EGP', 1, 0, 0, 0), " +
                " ('', '', 'ERN', 1, 0, 0, 0), " +
                "('', '', 'ETB', 1, 0, 0, 0), " +
                "('eu', 'EU', 'EUR', 1, 0, 0, 0), " +
                "('', '', 'FJD', 1, 0, 0, 0), " +
                "('', '', 'FKP', 1, 0, 0, 0), " +
                "('fr', 'FR', '', 0, 0, 1, 0), " +
                "('en', 'GB', 'GBP', 1, 0, 0, 0), " +
                "('', '', 'GEL', 1, 0, 0, 0), " +
                "('', '', 'GHS', 1, 0, 0, 0), " +
                "('', '', 'GIP', 1, 0, 0, 0), " +
                "('', '', 'GMD', 1, 0, 0, 0), " +
                "('', '', 'GNF', 1, 0, 0, 0), " +
                "('', '', 'GTQ', 1, 0, 0, 0), " +
                "('', '', 'GWP', 1, 0, 0, 0), " +
                "('', '', 'GYD', 1, 0, 0, 0), " +
                "('', '', 'HKD', 1, 0, 0, 0), " +
                "('', '', 'HNL', 1, 0, 0, 0), " +
                "('', '', 'HRK', 1, 0, 0, 0), " +
                "('', '', 'HTG', 1, 0, 0, 0), " +
                "('', '', 'HUF', 1, 0, 0, 0), " +
                "('id', 'ID', 'IDR', 1, 0, 1, 0), " +
                "('he', 'IL', 'ILS', 1, 0, 0, 0), " +
                "('hi', 'IN', 'INR', 1, 0, 0, 0), " +
                "('', '', 'IQD', 1, 0, 0, 0), " +
                "('', '', 'IRR', 1, 0, 0, 0), " +
                "('', '', 'ISK', 1, 0, 0, 0), " +
                "('it', 'IT', '', 0, 0, 1, 0), " +
                "('', '', 'JMD', 1, 0, 0, 0), " +
                "('', '', 'JOD', 1, 0, 0, 0), " +
                "('ja', 'JP', 'JPY', 1, 0, 0, 0), " +
                "('', '', 'KES', 1, 0, 0, 0), " +
                "('', '', 'KGS', 1, 0, 0, 0), " +
                "('', '', 'KHR', 1, 0, 0, 0), " +
                "('', '', 'KMF', 1, 0, 0, 0), " +
                "('', '', 'KPW', 1, 0, 0, 0), " +
                "('', '', 'KRW', 1, 0, 0, 0), " +
                "('', '', 'KWD', 1, 0, 0, 0), " +
                "('', '', 'KYD', 1, 0, 0, 0), " +
                "('', '', 'KZT', 1, 0, 0, 0), " +
                "('', '', 'LAK', 1, 0, 0, 0), " +
                "('', '', 'LBP', 1, 0, 0, 0), " +
                "('', '', 'LKR', 1, 0, 0, 0), " +
                "('', '', 'LRD', 1, 0, 0, 0), " +
                "('', '', 'LSL', 1, 0, 0, 0), " +
                "('', '', 'LTL', 1, 0, 0, 0), " +
                "('', '', 'LVL', 1, 0, 0, 0), " +
                "('', '', 'LYD', 1, 0, 0, 0), " +
                "('', '', 'MAD', 1, 0, 0, 0), " +
                "('', '', 'MDL', 1, 0, 0, 0), " +
                "('', '', 'MGA', 1, 0, 0, 0), " +
                "('', '', 'MKD', 1, 0, 0, 0), " +
                "('', '', 'MMK', 1, 0, 0, 0), " +
                "('', '', 'MNT', 1, 0, 0, 0), " +
                "('', '', 'MOP', 1, 0, 0, 0), " +
                "('', '', 'MRO', 1, 0, 0, 0), " +
                "('', '', 'MUR', 1, 0, 0, 0), " +
                "('', '', 'MVR', 1, 0, 0, 0), " +
                "('', '', 'MWK', 1, 0, 0, 0), " +
                "('', '', 'MXN', 1, 0, 0, 0), " +
                "('', '', 'MYR', 1, 0, 0, 0), " +
                "('', '', 'MZE', 1, 0, 0, 0), " +
                "('', '', 'MZN', 1, 0, 0, 0), " +
                "('', '', 'NAD', 1, 0, 0, 0), " +
                "('nl', 'NL', '', 0, 0, 1, 0), " +
                "('', '', 'NGN', 1, 0, 0, 0), " +
                "('', '', 'NIO', 1, 0, 0, 0), " +
                "('', '', 'NOK', 1, 0, 0, 0), " +
                "('', '', 'NPS', 1, 0, 0, 0), " +
                "('', '', 'NZD', 1, 0, 0, 0), " +
                "('', '', 'OMR', 1, 0, 0, 0), " +
                "('', '', 'PAB', 1, 0, 0, 0), " +
                "('', '', 'PEN', 1, 0, 0, 0), " +
                "('', '', 'PGK', 1, 0, 0, 0), " +
                "('', '', 'PHP', 1, 0, 0, 0), " +
                "('', '', 'PKR', 1, 0, 0, 0), " +
                "('', '', 'PLN', 1, 0, 0, 0), " +
                "('pt', 'PT', '', 0, 0, 1, 0), " +
                "('', '', 'PYG', 1, 0, 0, 0), " +
                "('', '', 'QAR', 1, 0, 0, 0), " +
                "('ro', 'RO', 'RON', 1, 0, 0, 0), " +
                "('sr', 'RS', 'RSD', 1, 0, 0, 0), " +
                "('ru', 'RU', 'RUB', 1, 0, 0, 0), " +
                "('', '', 'RWF', 1, 0, 0, 0), " +
                "('', '', 'SAR', 1, 0, 0, 0), " +
                "('', '', 'SBD', 1, 0, 0, 0), " +
                "('', '', 'SCR', 1, 0, 0, 0), " +
                "('', '', 'SDG', 1, 0, 0, 0), " +
                "('sv', 'SE', 'SEK', 1, 0, 0, 0), " +
                "('en', 'SG', 'SGD', 1, 0, 0, 0), " +
                "('', '', 'SHP', 1, 0, 0, 0), " +
                "('', '', 'SKK', 1, 0, 0, 0), " +
                "('', '', 'SLL', 1, 0, 0, 0), " +
                "('', '', 'SOS', 1, 0, 0, 0), " +
                "('es', 'ES', '', 0, 0, 1, 0), " +
                "('', '', 'SRD', 1, 0, 0, 0), " +
                "('', '', 'SSP', 1, 0, 0, 0), " +
                "('', '', 'STD', 1, 0, 0, 0), " +
                "('', '', 'SVC', 1, 0, 0, 0), " +
                "('', '', 'SYP', 1, 0, 0, 0), " +
                "('', '', 'SZL', 1, 0, 0, 0), " +
                "('th', 'TH', 'THB', 1, 0, 0, 0), " +
                "('', '', 'TJS', 1, 0, 0, 0), " +
                "('', '', 'TMT', 1, 0, 0, 0), " +
                "('', '', 'TND', 1, 0, 0, 0), " +
                "('', '', 'TOP', 1, 0, 0, 0), " +
                "('', '', 'TRY', 1, 0, 0, 0), " +
                "('', '', 'TTD', 1, 0, 0, 0), " +
                "('zh', 'TW', 'TWD', 1, 0, 0, 0), " +
                "('', '', 'TZS', 1, 0, 0, 0), " +
                "('uk', 'UA', 'UAH', 1, 0, 0, 0), " +
                "('', '', 'UGX', 1, 0, 0, 0), " +
                "('en', 'US', 'USD', 1, 1, 1, 1), " +
                "('', '', 'UYU', 1, 0, 0, 0), " +
                "('', '', 'UZS', 1, 0, 0, 0), " +
                "('', '', 'VEF', 1, 0, 0, 0), " +
                "('vi', 'VE', 'VND', 1, 0, 0, 0), " +
                "('', '', 'VUV', 1, 0, 0, 0), " +
                "('', '', 'WST', 1, 0, 0, 0), " +
                "('', '', 'XAF', 1, 0, 0, 0), " +
                "('', '', 'XCD', 1, 0, 0, 0), " +
                "('', '', 'XOF', 1, 0, 0, 0), " +
                "('', '', 'XPF', 1, 0, 0, 0), " +
                "('', '', 'YER', 1, 0, 0, 0), " +
                "('', '', 'ZAR', 1, 0, 0, 0), " +
                "('', '', 'ZMW', 1, 0, 0, 0)";
        // End of Currencies Table

        // Categories Table
        private static final String TABLE_CATEGORY = "ref_categories";
        private static final String IMAGE = "image";
        private static final String QUERY_DROP_CATEGORY = dropTableQuery(TABLE_CATEGORY);
        private static final String QUERY_CREATE_CATEGORY = createTableQuery(TABLE_CATEGORY, UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " VARCHAR(50), " + IMAGE + " VARCHAR(100), " + TYPE + " CHAR(1), " + IN_DATE + " DATETME DEFAULT CURRENT_TIMESTAMP, " + STATUS + " TINYINT(1)");
        private static final String QUERY_INSERT_CATEGORY = "INSERT INTO " + TABLE_CATEGORY + " (`" + NAME + "`, `" + IMAGE + "`, `" + TYPE + "`, `" + STATUS + "`) VALUES ('deposit', 'ic_income_deposit', 'i', 1), ('reward', 'ic_action_achievement', 'i', 1), ('salary', 'ic_income_salary', 'i', 1), ('savings', 'ic_income_savings', 'i', 1), ('bills', 'ic_action_dollar', 'o', 1), ('car', 'ic_action_car', 'o', 1), ('clothes', 'ic_action_tshirt', 'o', 1), ('communication', 'ic_action_phone_start', 'o', 1), ('computer', 'ic_action_monitor', 'o', 1), ('drinks', 'ic_action_coffee', 'o', 1), ('eating', 'ic_action_restaurant', 'o', 1), ('entertainment', 'ic_action_joypad', 'o', 1), ('food', 'ic_expense_snack', 'o', 1), ('health', 'ic_action_heart', 'o', 1), ('house', 'ic_action_home', 'o', 1), ('pets', 'ic_expense_pets', 'o', 1), ('sport', 'ic_action_ball', 'o', 1), ('toiletry', 'ic_expense_toiletry', 'o', 1), ('transportation', 'ic_action_bus', 'o', 1)";
        // End of Categories Table

        // Accounts Table
        private static final String TABLE_ACCOUNT = "ref_accounts";
        private static final String QUERY_DROP_ACCOUNT = dropTableQuery(TABLE_ACCOUNT);
        private static final String QUERY_CREATE_ACCOUNT = createTableQuery(TABLE_ACCOUNT, UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " VARCHAR(50), " + IMAGE + " VARCHAR(100), " + IN_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, " + STATUS + " TINYINT(1)");
        private static final String QUERY_INSERT_ACCOUNTS = "INSERT INTO ref_accounts (" + NAME + ", " + IMAGE + ", " + STATUS + ") values('cash', 'ic_action_dollar', 1), ('payment_card', 'ic_action_creditcard', 1)";
        // End of Accounts Table

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // CREATE QUERIES
            db.execSQL(QUERY_CREATE_TRANS);
            db.execSQL(QUERY_CREATE_TRANSFER);
            db.execSQL(QUERY_CREATE_CATEGORY);
            db.execSQL(QUERY_CREATE_LOCALE);
            db.execSQL(QUERY_CREATE_ACCOUNT);
            // INSERT QUERIES
            db.execSQL(QUERY_INSERT_CATEGORY);
            db.execSQL(QUERY_INSERT_LOCALE);
            db.execSQL(QUERY_INSERT_ACCOUNTS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            /*
             * TODO: REMOVE THESE 2 LINES IN VERSION 3!
             */
            // -----------------------------------------
            db.execSQL(dropTableQuery(TABLE_TRANS));
            db.execSQL(dropTableQuery(TABLE_TRANSFER));
            // ------------------------------------------
            db.execSQL(QUERY_DROP_CATEGORY);
            db.execSQL(QUERY_DROP_LOCALE);
            db.execSQL(QUERY_DROP_ACCOUNT);
            onCreate(db);
        }

        public static String createTableQuery(String tableName, String columns) {
            return "CREATE TABLE IF NOT EXISTS " + tableName + " (" + columns + ")";
        }

        public static String dropTableQuery(String tableName) {
            return "DROP TABLE IF EXISTS " + tableName;
        }
    }
}