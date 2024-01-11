package com.google.codelabs.buildyourfirstmap

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.codelabs.buildyourfirstmap.classes.User

class DbHelper(val context: Context, val factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context,"app", factory, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val query = "CREATE TABLE users (id INT PRIMARY KEY, login TEXT, password TEXT)"
        db!!.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun addUser(user: User){
        val values = ContentValues()
        values.put("login", user.login)
        values.put("password", user.password)

        val db = this.writableDatabase
        db.insert("users", null, values)
        db.close()
    }
}