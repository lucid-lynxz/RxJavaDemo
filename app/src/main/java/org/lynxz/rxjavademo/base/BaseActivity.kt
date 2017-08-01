package org.lynxz.rxjavademo.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_base.*
import org.lynxz.rxjavademo.R

@Suppress("UNCHECKED_CAST")
abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        val customView = layoutInflater.inflate(getLayoutRes(), null, false)
        rl_container.addView(customView)
        afterCreate()
    }

    /**
     * 指定布局文件
     * */
    abstract fun getLayoutRes(): Int

    abstract fun afterCreate()

    /**
     * 显示日志到界面上
     * */
    protected fun appendLog(msg: String?) {
        if (msg != null) {
            runOnUiThread {
                tv_logs.append("\n$msg")
            }
        }
    }

    /**
     * 封装findViewById()时强转功能
     */
    fun <T : View> findView(id: Int): T = findViewById(id) as T
}