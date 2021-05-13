package eu.electricocean.quiz

import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase
import android.content.Context
import android.content.ContentValues

class QuizDbHelper(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {
    companion object {
        private val DATABASE_NAME = "quiz.db"
        private val DATABASE_VERSION = 1
        private val TABLE_QUESTION = "question"
        private val TABLE_QUESTION_OPTION = "question_option"
        private val KEY_OPTION_ID = "option_id"
        private val KEY_QUESTION = "question"
        private val KEY_OPTION = "option"
        private val KEY_QUESTION_ID = "question_id"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_CONTACTS_TABLE: String = "CREATE TABLE "+TABLE_QUESTION+"("+ KEY_QUESTION_ID+" INTEGER PRIMARY KEY,"+KEY_QUESTION+" TEXT)"
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS "+TABLE_QUESTION)
        onCreate(db)
    }

    fun addQuestion(q: Question): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_QUESTION_ID,q.id)
        contentValues.put(KEY_QUESTION,q.question)
        val success = db.insert(TABLE_QUESTION,null,contentValues)
        db.close()
        return success
    }
}