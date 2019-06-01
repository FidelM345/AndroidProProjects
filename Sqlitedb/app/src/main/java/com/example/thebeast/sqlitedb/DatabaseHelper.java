package com.example.thebeast.sqlitedb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper  extends SQLiteOpenHelper{
//sqlite db is not case sensitive

    static final String DATABASE_NAME="student.db";
    static final String TABLE_NAME="student_table";
    static final String COL_1="id";
    static final String COL_2="name";
    static final String COL_3="surname";
    static final String COL_4="marks";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        //whenever the constructor is called the database is created
        SQLiteDatabase db=this.getWritableDatabase();//creates the table too

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //table is created when the method is called

        db.execSQL("CREATE TABLE "+TABLE_NAME+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,SURNAME TEXT, MARKS INTEGER)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //drops table if it exists in case of upgrading

        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);//it will then create the table again

    }
}
