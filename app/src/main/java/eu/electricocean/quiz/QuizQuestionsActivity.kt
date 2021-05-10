package eu.electricocean.quiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import eu.electricocean.quiz.databinding.ActivityQuizQuestionsBinding

class QuizQuestionsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuizQuestionsBinding
    private var mCurrentPosition:Int = 1
    private var mQuestionsList: ArrayList<Question>? = null
    private var mSelectedOptionPosition : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizQuestionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mQuestionsList = Constants.getQuestions()
        Log.i("Questions Size","${questionsList.size}")
        setQuestion()
    }

    private fun setQuestion() {
        val question: Question? = mQuestionsList[mCurrentPosition - 1]
        binding.progressBar.progress = mCurrentPosition
        binding.tvProgress.text = "$mCurrentPosition" + "/" + binding.progressBar.max
        binding.tvQuestion.text = question!!.question
        binding.ivImage.setImageResource(question.image)
        binding.tvOptionOne.text = question.optionOne
        binding.tvOptionTwo.text = question.optionTwo
        binding.tvOptionThree.text = question.optionThree
        binding.tvOptionFour.text = question.optionFour
    }

    private fun defaultOptionsView() {
        val options = ArrayList<TextView>()
        options.add(0,binding.tvOptionOne)
        options.add(1,binding.tvOptionTwo)
        options.add(2,binding.tvOptionThree)
        options.add(3,binding.tvOptionFour)
    }
}