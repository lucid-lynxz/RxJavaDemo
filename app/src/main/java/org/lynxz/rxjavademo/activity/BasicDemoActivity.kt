package org.lynxz.rxjavademo.activity

import android.graphics.Color
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_basic_demo.*
import org.lynxz.rxjavademo.Logger
import org.lynxz.rxjavademo.R
import org.lynxz.rxjavademo.base.BaseActivity
import java.util.concurrent.TimeUnit

/**
 * rxjava 的基本使用demo
 */
class BasicDemoActivity : BaseActivity() {

    companion object {
        private val TAG = "BasicDemoActivity"
    }

    override fun getLayoutRes() = R.layout.activity_basic_demo

    override fun afterCreate() {
        btn_basic.setOnClickListener { testBasic() }
        btn_countdown.setOnClickListener { countdown() }
        btn_delay.setOnClickListener {
            appendLog("\n${System.currentTimeMillis()} 2s后执行事件...")
            replaceHandler()
        }
    }

    /**
     * 替代handler进行延时操作
     */
    private fun replaceHandler() {
        clearLog()
        appendLog("Timer操作符创建一个在给定的时间段之后返回一个特殊值(0)的Observable\n")
        Observable.timer(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Long> {
                    override fun onSubscribe(@NonNull d: Disposable) {
                        appendLog("${System.currentTimeMillis()} onSubscribe ")
                    }

                    override fun onNext(aLong: Long) {
                        appendLog("${System.currentTimeMillis()} onNext 2s时间到 ,收到数据 $aLong")
                    }

                    override fun onError(@NonNull e: Throwable) {
                        appendLog("onError ${e.message}\n")
                    }

                    override fun onComplete() {
                        appendLog("complete\n")
                    }
                })
    }

    private fun testBasic() {
        clearLog()
        appendLog("Observable 被 Observable 订阅(subscribe),订阅时Observer会获得一个非空对象Disposable, 可以通过它来取消后续数据的接收\n")
        Observable.create(ObservableOnSubscribe<Int> { e ->
            for (i in 0..9) {
                appendLog(">> Observable 发射数据: $i")
                e.onNext(i)
            }
            appendLog(">> Observable 发出 onComplete 事件")
            e.onComplete()
        }).subscribeOn(Schedulers.io())// 多次指定的话,只有第一次有效
                .observeOn(Schedulers.newThread())// 多次设置会多次切换线程
                .doOnNext { integer -> Logger.d("doOnNext = $integer  ${Thread.currentThread().name}", TAG) }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { integer -> Logger.d("doOnNext = $integer  ${Thread.currentThread().name}", TAG) }
                .flatMap { integer ->
                    //对数据进行变换,不保证接收顺序, 若要有序,使用 concatMap
                    Observable.fromArray(integer.toString())
                }
                .subscribe(object : Observer<String> {
                    private lateinit var mDisposable: Disposable
                    override fun onSubscribe(d: Disposable) {
                        mDisposable = d
                    }

                    override fun onNext(msg: String) {
                        appendLog("observer 收到事件 onNext = $msg ,isDispose = ${mDisposable.isDisposed}" +
                                " thread = ${Thread.currentThread().name}")
                        if (Integer.parseInt(msg) >= 2) {
                            // 调用dispose后,本线程就不会再收到后续的事件
                            appendLog("observer调用 dispose() 停止接收后续事件")
                            mDisposable.dispose()
                        }
                    }

                    override fun onError(@NonNull e: Throwable) {
                        appendLog("observer onError ${e.message}\n")
                    }

                    override fun onComplete() {
                        appendLog("observer onComplete\n")
                    }
                })
    }

    /**
     * 倒计时
     */
    private fun countdown() {
        clearLog()
        val total = 3
        appendLog("\n开始倒计时,使用interval操作符,它会按固定的时间间隔发射一个无限递增的整数序列\n")
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .take((total + 1).toLong())
                .map { aLong -> total - aLong }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { disposable ->
                    Logger.d("disposable ${disposable.isDisposed}", TAG)
                    btn_countdown.isEnabled = false
                    btn_countdown.setTextColor(Color.GRAY)
                }
                .subscribe(object : Observer<Long> {
                    override fun onSubscribe(@NonNull d: Disposable) {
                        Logger.d("onSubscribe")
                    }

                    override fun onNext(@NonNull aLong: Long) {
                        Logger.d("onNext " + aLong)
                        btn_countdown.text = "剩余 $aLong 秒"
                        appendLog("onNext 倒计时, 剩余 $aLong 秒")
                    }

                    override fun onError(@NonNull e: Throwable) {
                        e.printStackTrace()
                        appendLog("onError ${e.message}")
                    }

                    override fun onComplete() {
                        Logger.d("onComplete")
                        btn_countdown.isEnabled = true
                        btn_countdown.setTextColor(Color.BLUE)
                        btn_countdown.text = "重新倒计时"
                    }
                })
    }
}
