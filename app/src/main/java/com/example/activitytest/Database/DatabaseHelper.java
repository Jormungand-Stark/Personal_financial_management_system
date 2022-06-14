package com.example.activitytest.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.activitytest.Util.InOutcome;
import com.example.activitytest.Util.Loan;
import com.example.activitytest.Util.Memo;

import java.util.ArrayList;
import java.util.List;

/** Memo 数据库辅助类 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";

    private static DatabaseHelper databaseHelper;
    // 类没有实例化,是不能用作父类构造器的参数,必须声明为静态
    private static final String NAME = "CMYDatabase.db"; // 数据库名称
    private static final int VERSION = 1; // 数据库版本

    public static final String CREATE_INCOME = "create table Income ("
            + "id integer primary key autoincrement,"
            + "kind text,"
            + "time text,"
            + "value real)";

    public static final String CREATE_OUTCOME = "create table Outcome ("
            + "id integer primary key autoincrement,"
            + "kind text,"
            + "time text,"
            + "value real)";

    public static final String CREATE_LOAN = "create table Loan ("
            + "id integer primary key autoincrement,"
            + "source text,"
            + "time text,"
            + "value real)";

    public static final String CREATE_MEMO = "create table Memo ("
            + "id integer primary key autoincrement,"
            + "title text,"
            + "content text)";

    public DatabaseHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_INCOME);
        db.execSQL(CREATE_OUTCOME);
        db.execSQL(CREATE_LOAN);
        db.execSQL(CREATE_MEMO);
        Log.e(TAG, "数据库创建语句已执行");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Income");
        db.execSQL("drop table if exists Outcome");
        db.execSQL("drop table if exists Loan");
        db.execSQL("drop table if exists Memo");
        onCreate(db);
    }

    public static void Instance(Context context) {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(context);
        }
    }

    // 增加单条memo数据
    public static void add_memoDB(String title, String content){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("content", content);
        db.insert("Memo", null, contentValues);
        db.close();
        Log.e(TAG, "add_memoDB: over");
    }

    public static void update_memoDB(String id, String title, String content){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("content", content);
        db.update("Memo", contentValues, "id = ?", new String[] {id});
        db.close();
        Log.e(TAG, "update_memoDB: over");
    }

    public static void delete_DB(String table, String id){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.delete(table, "id = ?", new String[] {id});
        db.close();
        Log.e(TAG, "delete_DB: over");
    }

    public void query_memoDB(String title){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query("Memo", new String[]{"title"}, "title = ?",
                new String[]{title}, null, null, null);
        if(cursor.moveToFirst()){
            do{
                String res = "";
                /*res += cursor.getString(cursor.getColumnIndex("class_name")) + " ";
                res += cursor.getString(cursor.getColumnIndex("class_num"));*/
            }while(cursor.moveToNext());
        }
        cursor.close();
    }

    @NonNull
    public static List<Memo> Memo_ALL() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query("Memo", null, null, null,
                null, null, null);
        int size = cursor.getCount();
        List<Memo> MemoList = new ArrayList<>();
        int i = 0; // 辅助添加Memo子项
        // 调用 moveToFirst方法 将数据指针移动到第一行的位置；
        if(cursor.moveToFirst()){
            do{
                int IndId = cursor.getColumnIndex("id");
                int IndTitle = cursor.getColumnIndex("title");
                int IndContent = cursor.getColumnIndex("content");
                String id = cursor.getString(IndId);
                String title = cursor.getString(IndTitle);
                String content = cursor.getString(IndContent);
                Log.e(TAG, "Memo_ALL title: " + title);
                Log.e(TAG, "Memo_ALL content: " + content);
                Memo memo = new Memo(title, content);
                memo.setInd(size - i - 1); // 将Memo对象与其在MemoList中的下标对应
                memo.setId(id); // memo对象由数据库中某条数据生成，因此需要将memo对象与这条数据的id绑定
                MemoList.add(0, memo); // 在索引0处插入新项，头插
                i++;
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return  MemoList;
    }

    // 增加单条 InOutcome 数据
    public static long add_InOutcomeDB(String table, String kind, String time, double value){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("kind", kind);
        contentValues.put("time", time);
        contentValues.put("value", value);
        long res = db.insert(table, null, contentValues);
        Log.e(TAG, "add_InOutcomeDB 最后一个id: " + res);
        db.close();
        Log.e(TAG, "add_InOutcomeDB: over");
        return res;
    }

    public static void update_LoanDB
            (String table, String id, String kind, String time, double value){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("kind", kind);
        contentValues.put("time", time);
        contentValues.put("value", value);
        db.update(table, contentValues, "id = ?", new String[] {id});
        db.close();
        Log.e(TAG, "update_LoanDB: over");
    }

    // 将数据库中的数据加载到数组中
    @NonNull
    public static List<InOutcome> InOutcome_ALL(String tableName) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(tableName, null, null, null,
                null, null, null);
        int size = cursor.getCount();
        List<InOutcome> inOutcomeList = new ArrayList<>();
        int i = 0; // 辅助添加账目子项
        // 调用 moveToFirst方法 将数据指针移动到第一行的位置；
        if(cursor.moveToFirst()){
            do{
                int IndId = cursor.getColumnIndex("id");
                int IndKind = cursor.getColumnIndex("kind");
                int IndTime = cursor.getColumnIndex("time");
                int IndValue = cursor.getColumnIndex("value");
                String id = cursor.getString(IndId);
                String kind = cursor.getString(IndKind);
                String time = cursor.getString(IndTime);
                double value = cursor.getDouble(IndValue);
                Log.e(TAG, "Income_ALL kind: " + kind);
                Log.e(TAG, "Income_ALL time: " + time);
                Log.e(TAG, "Income_ALL value: " + value);
                InOutcome inOutcome = new InOutcome(kind, time, value);
                inOutcome.setInd(size - i - 1); // 将 InOutcome对象 与其在 inOutcomeList 中的下标对应
                inOutcome.setId(id); // 将 InOutcome对象 与这条数据的id绑定
                inOutcomeList.add(0, inOutcome); // 在索引0处插入新项，头插
                i++;
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return inOutcomeList;
    }

    public static long add_LoanDB(String source, String time, double value){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("source", source);
        contentValues.put("time", time);
        contentValues.put("value", value);
        long res = db.insert("Loan", null, contentValues);
        Log.e(TAG, "add_LoanDB 最后一个id: " + res);
        db.close();
        return res;
    }

    public static void update_LoanDB(String id, String source, String time, double value){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("source", source);
        contentValues.put("time", time);
        contentValues.put("value", value);
        db.update("Loan", contentValues, "id = ?", new String[] {id});
        db.close();
        Log.e(TAG, "update_LoanDB: over");
    }

    @NonNull
    public static List<Loan> Loan_ALL() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query("Loan", null, null, null,
                null, null, null);
        int size = cursor.getCount();
        List<Loan> loanList = new ArrayList<>();
        int i = 0; // 辅助添加账目子项
        // 调用 moveToFirst方法 将数据指针移动到第一行的位置；
        if(cursor.moveToFirst()){
            do{
                int IndId = cursor.getColumnIndex("id");
                int IndSource = cursor.getColumnIndex("source");
                int IndTime = cursor.getColumnIndex("time");
                int IndValue = cursor.getColumnIndex("value");
                String id = cursor.getString(IndId);
                String source = cursor.getString(IndSource);
                String time = cursor.getString(IndTime);
                double value = cursor.getDouble(IndValue);
                Log.e(TAG, "Loan_ALL source: " + source);
                Log.e(TAG, "Loan_ALL time: " + time);
                Log.e(TAG, "Loan_ALL value: " + value);
                Loan loan = new Loan(source, time, value);
                loan.setInd(size - i - 1); // 将 Loan对象 与其在 loanList 中的下标对应
                loan.setId(id); // 将 Loan对象 与这条数据的id绑定
                loanList.add(0, loan); // 在索引0处插入新项，头插
                i++;
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return loanList;
    }
}
