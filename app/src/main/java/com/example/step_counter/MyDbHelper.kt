package com.example.step_counter

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDbHelper(context: Context) : SQLiteOpenHelper(context, "USERDB", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE USER(DATE TEXT PRIMARY KEY, STEPS INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
    fun allData(): Cursor? {
        val db = this.writableDatabase
        return db.rawQuery("select * from USER", null)
    }

    fun queryXData(): ArrayList<String> {
        val db: SQLiteDatabase = this.writableDatabase
        val xData = ArrayList<String>()
        val query = "SELECT DATE FROM USER"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            xData.add(cursor.getString(0))
            cursor.moveToNext()
        }
        cursor.close()
        return xData
    }

    fun queryYData(): ArrayList<String> {
        val db: SQLiteDatabase = this.writableDatabase
        val yData = ArrayList<String>()
        val query = "SELECT STEPS FROM USER"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            yData.add(cursor.getString(0))
            cursor.moveToNext()
        }
        cursor.close()
        return yData
    }
}