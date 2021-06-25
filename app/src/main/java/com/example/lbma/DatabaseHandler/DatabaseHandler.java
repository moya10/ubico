package com.example.lbma.DatabaseHandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.lbma.Models.ActivityModel;
import com.example.lbma.Models.ApiModel;
import com.example.lbma.Models.BatteryModel;
import com.example.lbma.Models.ConnectionModel;
import com.example.lbma.Models.InfoModel;
import com.example.lbma.Models.PlaceModel;
import com.example.lbma.Models.UserModel;
import com.example.lbma.Variables.Constants;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    public static final String USER_TABLE = "USER_TABLE";
    public static final String COLUMN_USER_ID = "ID";
    public static final String COLUMN_FULL_NAME = "COLUMN_FULL_NAME";
    public static final String COLUMN_AGE = "COLUMN_AGE";
    public static final String COLUMN_GENDER = "COLUMN_GENDER";
    public static final String COLUMN_EMAIL = "COLUMN_EMAIL";
    public static final String COLUMN_TELEPHONE = "COLUMN_TELEPHONE";
    public static final String COLUMN_DEVICE = "COLUMN_DEVICE";
    public static final String COLUMN_BATTERY_STATUS = "COLUMN_BATTERY_STATUS";
    public static final String API_TABLE = "API_TABLE";
    public static final String COLUMN_API_ID = "COLUMN_API_ID";
    public static final String COLUMN_DEVICE_ID = "COLUMN_DEVICE_ID";
    public static final String COLUMN_DEVICE_CREDENTIALS = "COLUMN_DEVICE_CREDENTIALS";
    public static final String COLUMN_DEVICE_TOKEN = "COLUMN_DEVICE_TOKEN";


    public DatabaseHandler(@Nullable Context context) {
        super(context, "device.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String user_query = "CREATE TABLE " + USER_TABLE + " (" + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_FULL_NAME + " TEXT, " + COLUMN_GENDER + " TEXT, " + COLUMN_AGE + " TEXT, " + COLUMN_EMAIL + " TEXT, " + COLUMN_TELEPHONE + " INT)";
        String api = "CREATE TABLE " + API_TABLE + " (" + COLUMN_API_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_DEVICE + " TEXT, " + COLUMN_DEVICE_ID + " TEXT, " + COLUMN_DEVICE_CREDENTIALS + " TEXT, " + COLUMN_DEVICE_TOKEN + " TEXT)";

        db.execSQL(user_query);
        db.execSQL(api);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addProfile(UserModel userModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_FULL_NAME, userModel.getFullname());
        cv.put(COLUMN_GENDER, userModel.getGender());
        cv.put(COLUMN_AGE, userModel.getAge());
        cv.put(COLUMN_EMAIL, userModel.getE_mail());
        cv.put(COLUMN_TELEPHONE, userModel.getTelephone());
        db.insert(USER_TABLE, null, cv);
        db.close();
    }

    public List<UserModel> getAllUser() {
        List<UserModel> returnList = new ArrayList<>();
        String queryString = "SELECT * FROM " + USER_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(0);
                    String user_name = cursor.getString(1);
                    String user_sex = cursor.getString(2);
                    String user_age = cursor.getString(3);
                    String email = cursor.getString(4);
                    int tele = cursor.getInt(5);
                    UserModel userModel = new UserModel(id, user_name, user_sex, user_age, email, tele);
                    returnList.add(userModel);
                } while (cursor.moveToNext());
            } else {
                Log.e("Add profile", "");
            }
        }
        cursor.close();
        db.close();
        return returnList;
    }

    public int updateProfile(UserModel userModel, int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_FULL_NAME, userModel.getFullname());
        cv.put(COLUMN_GENDER, userModel.getGender());
        cv.put(COLUMN_AGE, userModel.getAge());
        cv.put(COLUMN_EMAIL, userModel.getE_mail());
        cv.put(COLUMN_TELEPHONE, userModel.getTelephone());

        return db.update(USER_TABLE, cv, COLUMN_USER_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void addApi(ApiModel apiModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_DEVICE,apiModel.getDEVICE());
        cv.put(COLUMN_DEVICE_ID,apiModel.getDEVICE_ID());
        cv.put(COLUMN_DEVICE_CREDENTIALS,apiModel.getDEVICE_CREDENTIALS());
        cv.put(COLUMN_DEVICE_TOKEN,apiModel.getDEVICE_TOKEN());
        db.insert(API_TABLE, null, cv);
        db.close();
    }

    public List<ApiModel> getAllApi() {
        List<ApiModel> returnList = new ArrayList<>();
        String queryString = "SELECT * FROM " + API_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(0);
                    Constants.DEVICE = cursor.getString(1);
                    Constants.DEVICE_ID = cursor.getString(2);
                    Constants.DEVICE_CREDENTIALS = cursor.getString(3);
                    Constants.DEVICE_TOKEN = cursor.getString(4);
                    ApiModel apiModel = new ApiModel(id,Constants.DEVICE,Constants.DEVICE_ID,Constants.DEVICE_CREDENTIALS,Constants.DEVICE_TOKEN);
                    returnList.add(apiModel);
                } while (cursor.moveToNext());
            } else {
                Log.e("Add api", "error");
            }
        }
        cursor.close();
        db.close();
        return returnList;
    }
}
