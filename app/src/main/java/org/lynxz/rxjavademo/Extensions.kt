package org.lynxz.rxjavademo

import android.content.Context
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.widget.Toast
import java.text.DecimalFormat

/**
 * Created by lynxz on 13/01/2017.
 * 常用扩展函数
 */
fun CharSequence.isEmpty(): Boolean {
    return TextUtils.isEmpty(this)
}

fun Context.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.showToast(msgId: Int) {
    Toast.makeText(this, msgId, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToast(msg: String) {
    Toast.makeText(this.activity, msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToast(msgId: Int) {
    Toast.makeText(this.activity, msgId, Toast.LENGTH_SHORT).show()
}

fun Fragment.getStringRes(@StringRes resId: Int): String {
    return this.activity.resources.getString(resId)
}

inline fun debugConf(code: () -> Unit) {
    if (BuildConfig.DEBUG) {
        code()
    }
}

/**
 * 保留两位小数,并返回字符串
 * */
fun Double.yuan(): String = DecimalFormat("0.##").format(this)

/**
 * double类型向上保留转换为整数,如 2.1 -> 3  2.0->2
 * */
fun Double.toIntUp(): Int {
    val remainder = if (this % 1 > 0) 1 else 0
    return this.toInt() + remainder
}