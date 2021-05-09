package eu.electricocean.quiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        btn_start.setOnClickListener {
            Toast.makeText(this,"Please enter your name",Toast.LENGTH_SHORT)
            if(et_name.text.toString().isEmpty()) {
                Toast.makeText(this,"Please enter your name",Toast.LENGTH_SHORT)
            }
        }
    }
}