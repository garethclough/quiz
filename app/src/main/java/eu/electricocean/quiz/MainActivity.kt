package eu.electricocean.quiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import eu.electricocean.quiz.databinding.ActivityMainBinding
import org.json.JSONException
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "http://quiz.electricocean.eu/get"

        // Request a string response from the provided URL.
        val request = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                try {
                    for (i in 0 until response.length()) {
                        val question: JSONObject = response.getJSONObject(i)
                        val questionString: String = question.getString("question")
                        Log.d(Constants.LOGTAG,questionString)
                    }
                } catch (e: JSONException) {
                    Log.d(Constants.LOGTAG,e.message!!)
                }

            },
            Response.ErrorListener {
                Log.d(Constants.LOGTAG, "request error")
            }
        )

// Add the request to the RequestQueue.
        queue.add(request)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        binding.btnStart.setOnClickListener {
            if(binding.etName.text.toString().isEmpty()) {
                Toast.makeText(this,"Please enter your name",Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this,QuizQuestionsActivity::class.java)
                intent.putExtra(Constants.USER_NAME,binding.etName.text.toString())
                startActivity(intent)
                finish()
            }
        }
    }
}
