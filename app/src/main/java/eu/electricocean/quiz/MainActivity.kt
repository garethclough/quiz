package eu.electricocean.quiz

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import eu.electricocean.quiz.databinding.ActivityMainBinding
import org.json.JSONException
import org.json.JSONObject
import java.io.OutputStream
import java.io.PrintStream


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var importFlagsDone: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        var dbHelper: QuizDbHelper = QuizDbHelper(this)

        Log.d(Constants.LOGTAG,"On Create")
        val version = dbHelper.getVersion()
        Log.d(Constants.LOGTAG,"Version "+version)
        super.onCreate(savedInstanceState)

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)

        val url = "http://quiz.electricocean.eu/getFlags"

        val handler = Handler()
        val delay = 1000 // 1000 milliseconds == 1 second
        var that = this


        handler.postDelayed(object : Runnable {
            override fun run() {
                var keepRunning = true
                if (importFlagsDone) {
                    var flagsNotDownloaded = 0
                    for (flag in Constants.flags) {
                        if (!flag.loaded) {
                            flagsNotDownloaded++
                        }
                    }
                    if (flagsNotDownloaded == 0) {
                        keepRunning = false
                    }
                    Log.d(Constants.LOGTAG,flagsNotDownloaded.toString()+" flags not downloaded")
                }
                if (keepRunning) {
                    handler.postDelayed(this, delay.toLong())
                } else {
                    val intent = Intent(that,QuizQuestionsActivity::class.java)
                    //  intent.putExtra(Constants.USER_NAME,binding.etName.text.toString())
                    startActivity(intent)
                    finish()
                }
            }
        }, delay.toLong())


        // Request a string response from the provided URL.
        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                try {
                    Log.d(Constants.LOGTAG,"Received response:"+url)
                    val flagJsonArray = response.getJSONArray("flags")
                    val quizJsonObject = response.getJSONObject("quiz")
                    val latestVersion: Int = quizJsonObject.getInt("quiz_version")
                    Log.d(Constants.LOGTAG,"Latest version: "+latestVersion)
                    if (latestVersion != version) {
                        dbHelper.clearFlags()
                        for (i in 0 until flagJsonArray.length()) {
                            val flagJson: JSONObject = flagJsonArray.getJSONObject(i)
                            val country: String = flagJson.getString("country_name")
                            val flagId: Int = flagJson.getInt("flag_id")
                            var flag = Flag(flagId, country)
                            Constants.flags.add(flag)
                            dbHelper.addFlag(flag)
                            Log.d(Constants.LOGTAG, country)
                        }
                        for (flag in Constants.flags) {
                            val flagImageUrl: String =
                                "http://quiz.electricocean.eu/flag/" + flag.id + ".svg"
                            val flagImageRequest = StringRequest(
                                Request.Method.GET,
                                flagImageUrl,
                                Response.Listener<String> { response ->
                                    if (response != null) {
                                        var fileName: String = "flag-" + flag.id + ".svg"
                                        var outputStream: OutputStream = openFileOutput(
                                            fileName,
                                            Context.MODE_PRIVATE
                                        )
                                        var printStream: PrintStream = PrintStream(outputStream)
                                        printStream.print(response)
                                        printStream.close()
                                        flag.loaded = true
                                        dbHelper.setFlagDownloaded(flag)
                                    }
                                },
                                Response.ErrorListener {
                                    Log.d(Constants.LOGTAG, "that didnt work")
                                }
                            )
                            queue.add(flagImageRequest)
                        }
                        dbHelper.setVersion(latestVersion)
                    }

                    importFlagsDone = true
                } catch (e: JSONException) {
                    Log.d(Constants.LOGTAG,e.message!!)
                }
            },
            Response.ErrorListener {
                Log.d(Constants.LOGTAG, "request error")
            }
        )

// Add the request to the RequestQueue.
        request.setRetryPolicy(
            DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )
        queue.add(request)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
/*
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
 */
    }
}
