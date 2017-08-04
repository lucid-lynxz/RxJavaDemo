package org.lynxz.rxjavademo.activity

import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_filter_demo.*
import org.lynxz.rxjavademo.Logger
import org.lynxz.rxjavademo.R
import org.lynxz.rxjavademo.base.BaseActivity
import org.lynxz.rxjavademo.bean.Student
import java.util.concurrent.TimeUnit

/**
 * 过滤操作
 * 参考: https://luhaoaimama1.github.io/2017/07/31/rxjava/
 * */
class FilterDemoActivity : BaseActivity() {

    val srcData: ArrayList<Student> = arrayListOf<Student>().apply {
        add(Student("小1", 21, "male"))
        add(Student("小2", 22, "female"))
        add(Student("小4", 24, "unknown"))
        add(Student("小3", 23, "male"))
        add(Student("小4", 24, "unknown"))
        add(Student("小4", 24, "unknown"))
        add(Student("小5", 26, "male"))
    }


    val mConsumer = Consumer<Student> { appendLog("$it") }

    val mCompleteAction = Action { appendLog("onComplete") }
    val mErrorConsumer = Consumer<Throwable> { t -> appendLog("onError ${t.message}") }

    val mObserver = object : Observer<Student> {
        override fun onNext(t: Student) {
            appendLog("$t")
        }

        override fun onError(e: Throwable) {
            appendLog("onError ${e.message}")
        }

        override fun onComplete() {
            appendLog("onComplete")
        }

        override fun onSubscribe(d: Disposable) {
            appendLog("onSubscribe")
        }
    }


    override fun getLayoutRes() = R.layout.activity_filter_demo

    override fun afterCreate() {
        btn_src_data.setOnClickListener { showSrcData() }
        btn_filter.setOnClickListener { filterMale() }
        btn_element_at.setOnClickListener { elementAt() }
        btn_ignore_elements.setOnClickListener { ignoreElements() }
        btn_take.setOnClickListener { take() }
        btn_take_last.setOnClickListener { takeLast() }
        btn_take_until.setOnClickListener { takeUntil() }
        btn_take_while.setOnClickListener { takeWhile() }
        btn_skip.setOnClickListener { skip() }
        btn_distinct.setOnClickListener { distinct() }
        btn_distinct_until_changed.setOnClickListener { distinctUntilChanged() }
        btn_debounce.setOnClickListener { debounce() }
    }

    private fun debounce() {
        clearLog()
        appendLog("debounce 操作符示例,过滤掉发射过快的数据:")

        Observable
                .create(ObservableOnSubscribe<Student> { e ->
                    for (i in 0..srcData.size - 1) {
                        e.onNext(srcData[i])
                        val delay = 98 + (i % 3)
                        Logger.d("onNext  $delay ${srcData[i]}")
                        Thread.sleep(delay.toLong())
                    }
                })
                .debounce(100, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mConsumer)
    }

    private fun distinct() {
        clearLog()
        appendLog("distinct 操作符示例,去除重复项:")
        Observable.fromIterable(srcData)
                .distinct()
                .subscribe(mConsumer)
    }

    private fun distinctUntilChanged() {
        clearLog()
        appendLog("distinctUntilChanged 操作符示例,去除相邻的重复项:")
        Observable.fromIterable(srcData)
                .distinctUntilChanged()
                .subscribe(mConsumer)
    }

    private fun skip() {
        clearLog()
        appendLog("skip 操作符示例,丢弃前N项数据(skipLast表示丢弃最后N项数据):")
        Observable.fromIterable(srcData)
                .skip(2)
                .subscribe(mConsumer)
    }

    private fun showSrcData() {
        clearLog()
        appendLog("完整数据源如下:")
        Observable.fromIterable(srcData)
                .subscribe(mObserver)
    }

    private fun takeUntil() {
        clearLog()
        appendLog("takeUntil 操作符示例,满足条件就直接onComplete")
        Observable.fromIterable(srcData)
                .takeUntil { stu -> stu.age >= 23 }
                .subscribe(mObserver)
    }

    private fun takeWhile() {
        clearLog()
        appendLog("takeWhile 操作符示例,不满足条件就直接onComplete")
        Observable.fromIterable(srcData)
                .takeWhile { stu -> stu.age < 23 }
                .subscribe(mObserver)
    }

    /**
     * 只发射前面的N项数据
     * */
    private fun take() {
        clearLog()
        appendLog("take 操作符示例,只发射数据源前N项数据")
        Observable.fromIterable(srcData)
                .take(2)
                .subscribe(mConsumer)
    }

    /**
     * 只发射前面的N项数据
     * */
    private fun takeLast() {
        clearLog()
        appendLog("takeLast 操作符示例,只发射数据源最后N项数据")
        Observable.fromIterable(srcData)
                .takeLast(2)
                .subscribe(mConsumer)
    }

    /**
     * 忽略 Observable 发射的数据，会 onComplete 和 onError 通知
     * */
    private fun ignoreElements() {
        clearLog()
        appendLog("ignoreElements 操作符示例,忽略 Observable 发射的数据,但会收到结束或者错误通知")
        Observable.fromIterable(srcData)
                .ignoreElements()
                .subscribe(mCompleteAction, mErrorConsumer)
    }

    /**
     * elementAt 操作符,值发生指定项的数据
     * */
    private fun elementAt() {
        clearLog()
        appendLog("elementAt 操作符示例,显示第2个学生信息:")
        Observable.fromIterable(srcData)
                .elementAt(1, Student("default", 38, "female")) // 若指定项在数据源中不存在,则发射默认数据
                .subscribe(mConsumer)

    }

    /**
     * filter操作符,过滤显示male信息
     * */
    private fun filterMale() {
        clearLog()
        appendLog("filter 操作符示例,显示性别为male的学生信息:")
        Observable.fromIterable(srcData)
                .filter { it.gender == "male" }
                .subscribe(mConsumer)
    }
}
