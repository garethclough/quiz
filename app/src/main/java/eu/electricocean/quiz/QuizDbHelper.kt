package eu.electricocean.quiz

import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase
import android.content.Context
import android.content.ContentValues
import android.database.Cursor;

class QuizDbHelper(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {
    companion object {
        private val DATABASE_NAME = "quiz.db"
        private val DATABASE_VERSION = 1
        private val TABLE_FLAG = "flag"
        private val KEY_FLAG_ID = "flag_id"
        private val KEY_FLAG_COUNTRY = "flag_country"
        private val KEY_FLAG_DOWNLOADED = "flag_downloaded"
        private val TABLE_VERSION = "quiz_version"
        private val KEY_VERSION = "version_number"
        private val KEY_UPDATED = "version_update"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_VERSION_TABLE: String = "CREATE TABLE "+TABLE_VERSION+"("+ KEY_VERSION+" INTEGER PRIMARY KEY,"+ KEY_UPDATED+" TEXT)"
        db?.execSQL(CREATE_VERSION_TABLE)
        val CREATE_FLAG_TABLE: String = "CREATE TABLE "+TABLE_FLAG+"("+ KEY_FLAG_ID+" INTEGER PRIMARY KEY,"+ KEY_FLAG_COUNTRY+" TEXT,"+ KEY_FLAG_DOWNLOADED+" INTEGER)"
        db?.execSQL(CREATE_FLAG_TABLE)
        db?.execSQL("INSERT INTO "+TABLE_VERSION+" ("+KEY_VERSION+","+KEY_UPDATED+") VALUES (0,time('now'))")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS "+TABLE_FLAG)
        onCreate(db)
        db!!.execSQL("DROP TABLE IF EXISTS "+TABLE_VERSION)
        onCreate(db)
    }

    fun setVersion(version: Int) {
        val db = getReadableDatabase()
        db.execSQL("UPDATE "+TABLE_VERSION+" SET "+KEY_VERSION+" = "+version+","+ KEY_UPDATED+" = NOW()")
    }

    fun getVersion(): Int
    {
        val db = getReadableDatabase()
        var version: Int = 0
        val c:Cursor = db.rawQuery("SELECT * FROM " + TABLE_VERSION, null);
        if (c.moveToFirst()) {
            version = c.getInt(c.getColumnIndex(KEY_VERSION))
        }
        c.close()
        return version
    }

    fun addFlag(f: Flag): Long {
        val db = getReadableDatabase()
        var contentValues = ContentValues()
        contentValues.put(KEY_FLAG_ID,f.id)
        contentValues.put(KEY_FLAG_COUNTRY,f.country)
        contentValues.put(KEY_FLAG_DOWNLOADED,false)
        var success = db!!.insert(TABLE_FLAG,null,contentValues)

        return success
    }

    fun getAllFlags(): ArrayList<Flag> {
        val flagList = ArrayList<Flag>()
        val db = getReadableDatabase()
        val c:Cursor = db.rawQuery("SELECT * FROM " + TABLE_FLAG, null);
        if (c.moveToFirst()) {
            do {
                var country = c.getString(c.getColumnIndex(KEY_FLAG_COUNTRY))
                var id = c.getInt(c.getColumnIndex(KEY_FLAG_ID))
                var loaded = c.getInt(c.getColumnIndex(KEY_FLAG_DOWNLOADED)) == 1
                var flag: Flag =Flag(id,country,loaded)
                flagList.add(flag)
            } while (c.moveToNext())
        }
        c.close()
        return flagList
    }
}