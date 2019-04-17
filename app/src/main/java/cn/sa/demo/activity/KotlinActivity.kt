package cn.sa.demo.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import cn.sa.demo.R
import kotlinx.android.synthetic.main.activity_kotlin.*

class KotlinActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)
        this.setTitle("Kotlin")
        // Kotlin 设置点击
        btn_kotlin.setOnClickListener {
            Toast.makeText(this,"方式9(Kotlin)", Toast.LENGTH_SHORT).show();
        }
    }
}
