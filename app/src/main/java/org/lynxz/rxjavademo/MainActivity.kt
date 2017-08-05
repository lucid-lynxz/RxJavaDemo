package org.lynxz.rxjavademo

import android.content.Intent
import kotlinx.android.synthetic.main.activity_main.*
import org.lynxz.rxjavademo.activity.*
import org.lynxz.rxjavademo.base.BaseActivity

class MainActivity : BaseActivity() {
    override fun getLayoutRes() = R.layout.activity_main
    override fun afterCreate() {
        btn_basic.setOnClickListener { startActivity(Intent(this@MainActivity, BasicDemoActivity::class.java)) }
        btn_operator.setOnClickListener { startActivity(Intent(this@MainActivity, OperatorDemoActivity::class.java)) }
        btn_error.setOnClickListener { startActivity(Intent(this@MainActivity, ErrorHandlerActivity::class.java)) }
        btn_rx_binding.setOnClickListener { startActivity(Intent(this@MainActivity, RxbindingActivity::class.java)) }
        btn_rx_retrofit.setOnClickListener { startActivity(Intent(this@MainActivity, RetrofitDemoActivity::class.java)) }
    }
}
