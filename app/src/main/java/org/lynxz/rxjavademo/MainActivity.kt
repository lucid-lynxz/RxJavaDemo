package org.lynxz.rxjavademo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.lynxz.rxjavademo.activity.BasicDemoActivity
import org.lynxz.rxjavademo.activity.ErrorHandlerActivity
import org.lynxz.rxjavademo.activity.OperatorDemoActivity
import org.lynxz.rxjavademo.activity.RxbindingActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_basic.setOnClickListener { startActivity(Intent(this@MainActivity, BasicDemoActivity::class.java)) }
        btn_operator.setOnClickListener { startActivity(Intent(this@MainActivity, OperatorDemoActivity::class.java)) }
        btn_error.setOnClickListener { startActivity(Intent(this@MainActivity, ErrorHandlerActivity::class.java)) }
        btn_rx_binding.setOnClickListener { startActivity(Intent(this@MainActivity, RxbindingActivity::class.java)) }
    }
}
