package eu.electricocean.quiz

import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.caverock.androidsvg.SVG
import eu.electricocean.quiz.databinding.ActivityQuizQuestionsBinding
import java.io.FileInputStream


class QuizQuestionsActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityQuizQuestionsBinding
    private var mCurrentPosition:Int = 1
    private var mFlagList: ArrayList<Flag> = ArrayList()
    private var mSelectedOptionPosition : Int = 0
    private var mCorrectAnswers: Int = 0
    private var numQuestions = 10
    private var mUserName: String? = null
    private var numOptions: Int = 4
    private var currentOptions: Array<String> = arrayOf<String>()
    private var options: ArrayList<TextView> = ArrayList()
    private var correctOptionIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizQuestionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mUserName = intent.getStringExtra(Constants.USER_NAME)

        var dbHelper: QuizDbHelper = QuizDbHelper(this)
        mFlagList = dbHelper.  getAllFlags()
        setQuestion()
        binding.btnSubmit.setOnClickListener(this)
    }

    private fun setQuestion() {
        for(option in options) {
            binding.optionLayout.removeView(option as View)
        }
        options.clear()

        var flagIndex: Int = (1..mFlagList.size).random()
        correctOptionIndex = (1..numOptions).random()
        val flag: Flag = mFlagList.get(flagIndex - 1)
        val correctOption = flag.country.capitalizeWords()
        var fileName:String = "flag-"+flag.id+".svg"
        val fis: FileInputStream = openFileInput(fileName)
        var svg:SVG = SVG.getFromInputStream(fis)
        var imageView:ImageView = binding.ivImage

        var flagWidth = svg.documentWidth.toInt()
        var flagHeight = svg.documentHeight.toInt()

        if (flagWidth <= 0) flagWidth = imageView.drawable.intrinsicWidth
        if (flagHeight <= 0) flagHeight = imageView.drawable.intrinsicHeight

        val svgBitmap = Bitmap.createBitmap(
            flagWidth,
            flagHeight,
            Bitmap.Config.ARGB_8888
        )
        var bmcanvas = Canvas(svgBitmap)
        var questionOptions = Array(numOptions) {""}

        // Clear background to white
        bmcanvas.drawRGB(255, 255, 255)
        svg.renderToCanvas(bmcanvas)

        binding.ivImage.setImageBitmap(svgBitmap)
        binding.progressBar.progress = mCurrentPosition
        binding.tvProgress.text = "$mCurrentPosition" + "/" + binding.progressBar.max
//        binding.tvQuestion.text = question!!.question

        questionOptions[correctOptionIndex-1] = correctOption;
        var optionsRemaining = numOptions - 1
        var optionIndex = 1
        while(optionsRemaining > 0) {
            var flagOptionIndex = (1..mFlagList.size).random()
            if (flagIndex != flagOptionIndex) {
                if (optionIndex == correctOptionIndex) {
                    optionIndex++
                }
                var optionFlag: Flag = Constants.flags[flagOptionIndex-1]
                questionOptions[optionIndex-1] = optionFlag.country.capitalizeWords()
                optionsRemaining--
                optionIndex++
            }
        }

        val params: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.setMargins(10, 10, 10, 10)
        for(optionText in questionOptions) {
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
        if (mCurrentPosition == numQuestions) {
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
                        mCurrentPosition <= numQuestions -> {
                            setQuestion();
                        }
                        else -> {
                            val intent = Intent(this,ResultActivity::class.java)
                            intent.putExtra(Constants.USER_NAME,mUserName)
                            intent.putExtra(Constants.CORRECT_ANSWERS,mCorrectAnswers)
                            intent.putExtra(Constants.TOTAL_QUESTIONS,numQuestions)
                            startActivity(intent)
                            finish()
                        }
                    }
                } else {
                    if (correctOptionIndex != mSelectedOptionPosition) {
                        answerView(mSelectedOptionPosition,R.drawable.wrong_option_border_bg)
                    } else {
                        mCorrectAnswers++
                    }
                    answerView(correctOptionIndex,R.drawable.correct_option_border_bg)
                    if(mCurrentPosition == numQuestions) {
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