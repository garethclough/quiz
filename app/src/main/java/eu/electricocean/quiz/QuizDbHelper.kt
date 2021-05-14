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
        private val TABLE_QUESTION = "question"
        private val TABLE_QUESTION_OPTION = "question_option"
        private val KEY_OPTION_ID = "option_id"
        private val KEY_QUESTION = "question"
        private val KEY_QUESTION_IMAGE_ID = "question_image_id"
        private val KEY_CORRECT_ANSWER = "correct_answer"
        private val KEY_OPTION = "option"
        private val KEY_QUESTION_ID = "question_id"
        private val KEY_OPTION_NUMBER = "option_number"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_QUESTION_TABLE: String = "CREATE TABLE "+TABLE_QUESTION+"("+ KEY_QUESTION_ID+" INTEGER PRIMARY KEY,"+KEY_QUESTION+" TEXT,"+ KEY_CORRECT_ANSWER+" INTEGER,"+ KEY_QUESTION_IMAGE_ID+" INTEGER)"
        db?.execSQL(CREATE_QUESTION_TABLE)
        val CREATE_OPTION_TABLE: String = "CREATE TABLE "+TABLE_QUESTION_OPTION+"("+ KEY_OPTION_ID+" INTEGER PRIMARY KEY,"+ KEY_QUESTION_ID+" INTEGER,"+KEY_OPTION+" TEXT,"+ KEY_OPTION_NUMBER+" INTEGER)"
        db?.execSQL(CREATE_OPTION_TABLE)
        addQuestions(db,Constants.getQuestions())
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS "+TABLE_QUESTION)
        onCreate(db)
    }

    fun addQuestions(db: SQLiteDatabase?,questionList: ArrayList<Question>) {
        for(question in questionList) {
            addQuestion(db,question)
        }
    }

    fun addQuestion(db: SQLiteDatabase?,q: Question): Long {
        var contentValues = ContentValues()
        contentValues.put(KEY_QUESTION_ID,q.id)
        contentValues.put(KEY_QUESTION,q.question)
        contentValues.put(KEY_CORRECT_ANSWER,q.correctAnswer)
        contentValues.put(KEY_QUESTION_IMAGE_ID,q.image)
        var success = db!!.insert(TABLE_QUESTION,null,contentValues)

        var counter = 1
        for(option in q.options!!) {
            val contentValues = ContentValues()
            contentValues.put(KEY_OPTION,option)
            contentValues.put(KEY_QUESTION_ID,q.id)
            contentValues.put(KEY_OPTION_NUMBER,counter)
            db.insert(TABLE_QUESTION_OPTION,null,contentValues)
            counter++
        }
        return success
    }

    fun getAllQuestions(): ArrayList<Question> {
        val questionList = ArrayList<Question>()
        val db = getReadableDatabase()
        val c:Cursor = db.rawQuery("SELECT * FROM " + TABLE_QUESTION, null);
        if (c.moveToFirst()) {
            do {
                var question: Question = Question()
                question.question = c.getString(c.getColumnIndex(KEY_QUESTION))
                question.id = c.getInt(c.getColumnIndex(KEY_QUESTION_ID))
                question.correctAnswer = c.getInt(c.getColumnIndex(KEY_CORRECT_ANSWER))
                question.image = c.getInt(c.getColumnIndex(KEY_QUESTION_IMAGE_ID))
                var optionCursor:Cursor = db.rawQuery("SELECT * FROM " + TABLE_QUESTION_OPTION+" WHERE "+KEY_QUESTION_ID+" = "+question.id, null);
                if (optionCursor.moveToFirst()) {
                    do {
                        var option: String = optionCursor.getString(optionCursor.getColumnIndex(KEY_OPTION))
                        question.options?.add(option)
                    } while (optionCursor.moveToNext())
                }
                optionCursor.close()

                questionList.add(question)
            } while (c.moveToNext())
        }
        c.close()
        return questionList
    }
}