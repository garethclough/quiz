package eu.electricocean.quiz

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.caverock.androidsvg.SVG
import eu.electricocean.quiz.databinding.ActivityQuizQuestionsBinding

class QuizQuestionsActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityQuizQuestionsBinding
    private var mCurrentPosition:Int = 1
    private var mQuestionsList: ArrayList<Question>? = null
    private var mSelectedOptionPosition : Int = 0
    private var mCorrectAnswers: Int = 0
    private var mUserName: String? = null
    private var options: ArrayList<TextView> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizQuestionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mUserName = intent.getStringExtra(Constants.USER_NAME)

        var dbHelper: QuizDbHelper = QuizDbHelper(this)
        mQuestionsList = dbHelper.getAllQuestions()
        setQuestion()
        binding.btnSubmit.setOnClickListener(this)
    }

    private fun setQuestion() {
        for(option in options) {
            binding.optionLayout.removeView(option as View)
        }
        options.clear()
        val question: Question? = mQuestionsList?.get(mCurrentPosition - 1)
        val flag: Flag = Constants.flags.get(mCurrentPosition - 1)
        var fileName:String = "flag-"+flag.id+".svg"
        var svg:SVG = SVG.getFromAsset()
        binding.progressBar.progress = mCurrentPosition
        binding.tvProgress.text = "$mCurrentPosition" + "/" + binding.progressBar.max
        binding.tvQuestion.text = question!!.question
        binding.ivImage.setImageResource(question!!.image!!)

        val params: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.setMargins(10, 10, 10, 10)
        for(optionText in question.options!!) {
            val tvOption = TextView(this)
            tvOption.setId(View.generateViewId())
            tvOption.setText(optionText)
            tvOption.setTextSize(20.toFloat())
            tvOption.setTextColor(Color.BLACK)
            tvOption.setPadding(15)
            tvOption.setBackgroundResource(R.drawable.default_option_border_bg)
            tvOption.setLayoutParams(params)
            tvOption.setGravity(Gravity.CENTER)
            tvOption.setOnClickListener(this)
            binding.optionLayout.addView(tvOption)
            options?.add(tvOption)
        }

        defaultOptionsView()
        if (mCurrentPosition == mQuestionsList!!.size) {
            binding.btnSubmit.text = "FINISH"
        } else {
            binding.btnSubmit.text = "SUBMIT"
        }
    }

    private fun defaultOptionsView() {
        for (option in options) {
            option.setTextColor(Color.parseColor("#7A8089"))
            option.typeface = Typeface.DEFAULT
            option.background = ContextCompat.getDrawable(
                this,
                R.drawable.default_option_border_bg
            )
        }
    }

    override fun onClick(v: View?) {
        var optionNumber: Int = 1
        for(option in options!!) {
            if (v == option) {
                selectedOptionView(option,optionNumber)
            }
            optionNumber++
        }

        when(v?.id) {
            R.id.btn_submit ->{
                if (mSelectedOptionPosition == 0) {
                    mCurrentPosition++
                    when {
                        mCurrentPosition <= mQuestionsList!!.size -> {
                            setQuestion();
                        }
                        else -> {
                            val intent = Intent(this,ResultActivity::class.java)
                            intent.putExtra(Constants.USER_NAME,mUserName)
                            intent.putExtra(Constants.CORRECT_ANSWERS,mCorrectAnswers)
                            intent.putExtra(Constants.TOTAL_QUESTIONS,mQuestionsList!!.size)
                            startActivity(intent)
                            finish()
                        }
                    }
                } else {
                    val question = mQuestionsList?.get(mCurrentPosition - 1)
                    if (question!!.correctAnswer != mSelectedOptionPosition) {
                        answerView(mSelectedOptionPosition,R.drawable.wrong_option_border_bg)
                    } else {
                        mCorrectAnswers++
                    }
                    answerView(question.correctAnswer!!,R.drawable.correct_option_border_bg)
                    if(mCurrentPosition == mQuestionsList!!.size) {
                        binding.btnSubmit.text = "FINISH"
                    } else {
                        binding.btnSubmit.text = "GO TO NEXT QUESTION"
                    }
                    mSelectedOptionPosition = 0
                }
            }
        }
    }

    private fun answerView(answer: Int,drawableView: Int) {
        var option: TextView = options.get(answer-1)
        option.setBackground(ContextCompat.getDrawable(this,drawableView))
    }

    private fun selectedOptionView(tv: TextView, selectedOptionNum: Int) {
        defaultOptionsView()
        mSelectedOptionPosition = selectedOptionNum
        tv.setTextColor(Color.parseColor("#363A43"))
        tv.typeface = Typeface.DEFAULT_BOLD
        tv.background = ContextCompat.getDrawable(
            this,
            R.drawable.default_option_border_bg
        )
    }
}