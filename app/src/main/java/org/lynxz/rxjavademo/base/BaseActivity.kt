package org.lynxz.rxjavademo.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import kotlinx.android.synthetic.main.activity_base.*
import org.lynxz.rxjavademo.R


@Suppress("UNCHECKED_CAST")
abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        val customView = layoutInflater.inflate(getLayoutRes(), null, false)
        customView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
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
                scroll2Bottom(sv_log, tv_logs)
            }
        }
    }

    protected fun clearLog(){
        tv_logs.text = ""
    }

    fun scroll2Bottom(scroll: ScrollView, inner: View) {
        // 内层高度超过外层
        var offset = inner.measuredHeight - scroll.measuredHeight
        if (offset < 0) {
            offset = 0
        }
        scroll.scrollTo(0, offset + 50)
    }

    /**
     * 封装findViewById()时强转功能
     */
    fun <T : View> findView(id: Int): T = findViewById(id) as T
}