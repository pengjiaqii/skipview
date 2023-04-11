package com.jade.testdemo;

import android.os.UserHandle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.Drawable;
import android.content.Context;

import com.jade.testdemo.util.AddAppListDB;

public class AddAppListModel {

	private String currentPackage;
    private String currentAppName;
    private String currentClassName;
    private int currentIsChecked;
    private Drawable icon;

    private static AddAppListDB mDb;

    public static ArrayList<AddAppListModel> getAllAppFromDB(Context context){
        mDb = new AddAppListDB(context);

        ArrayList<AddAppListModel> addAppListModels = new ArrayList<>();

        Cursor c = null;
        try {
            c = mDb.getWritableDatabase().query(AddAppListDB.TABLE_NAME,null,null,null,null,null,null,null);

            final int columnPackage = c.getColumnIndex(AddAppListDB.COLUMN_PACKAGE);
            final int columnAppName = c.getColumnIndex(AddAppListDB.COLUMN_APP_NAME);
            final int columnClassName = c.getColumnIndex(AddAppListDB.COLUMN_CLASS_NAME);
            final int columnIsChecked = c.getColumnIndex(AddAppListDB.COLUMN_IS_CHECKED);

            while (c.moveToNext()) {
                AddAppListModel model = new AddAppListModel();

                String currentPackage = c.getString(columnPackage);
                String currentAppName = c.getString(columnAppName);
                String currentClassName = c.getString(columnClassName);
                int currentIsChecked = c.getInt(columnIsChecked);

                Log.d("launcher692", "---getAllAppFromDB---currentPackage:" + currentPackage);
                Log.d("launcher692", "---getAllAppFromDB---currentAppName:" + currentAppName);
                Log.d("launcher692", "---getAllAppFromDB---currentClassName:" + currentClassName);
                Log.d("launcher692", "---getAllAppFromDB---currentIsChecked:" + currentIsChecked);

                model.setCurrentPackage(currentPackage);
                model.setCurrentAppName(currentAppName);
                model.setCurrentClassName(currentClassName);
                model.setCurrentIsChecked(currentIsChecked);

                addAppListModels.add(model);
            }
        } catch (SQLiteException e) {
            Log.d("launcher692", "Error reading AddAppListDB: "+ e);
            // Continue updating whatever we have read so far
        } finally {
            if (c != null) {
                c.close();
            }
        }

        Log.d("launcher692", "AddAppListModel---addAppListModels:" + addAppListModels.size());
        return addAppListModels;
    }


    public String getCurrentPackage() {
        return currentPackage;
    }

    public void setCurrentPackage(String currentPackage) {
        this.currentPackage = currentPackage;
    }

    public String getCurrentAppName() {
        return currentAppName;
    }

    public void setCurrentAppName(String currentAppName) {
        this.currentAppName = currentAppName;
    }

    public String getCurrentClassName() {
        return currentClassName;
    }

    public void setCurrentClassName(String currentClassName) {
        this.currentClassName = currentClassName;
    }


    public int getCurrentIsChecked() {
        return currentIsChecked;
    }

    public void setCurrentIsChecked(int currentIsChecked) {
        this.currentIsChecked = currentIsChecked;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

   
    @Override
    public String toString() {
        return "AddAppListModel{" +
                "currentPackage='" + currentPackage + '\'' +
                ", currentAppName='" + currentAppName + '\'' +
                ", currentIsChecked=" + currentIsChecked + '\'' +
                ", currentClassName=" + currentClassName +
                ", icon=" + icon +
                '}';
    }
}