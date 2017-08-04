package org.lynxz.rxjavademo.activity

import org.lynxz.rxjavademo.R
import org.lynxz.rxjavademo.base.BaseActivity
import org.lynxz.rxjavademo.bean.Student

/**
 * 过滤操作
 * */
class FilterDemoActivity : BaseActivity() {

    val data = with(arrayListOf<Student>()) {
        add(Student("小三", 20, "male"))
        add(Student("小四", 18, "female"))
        add(Student("小五", 30, "male"))
        add(Student("小六", 16, "unknown"))
    }

    override fun getLayoutRes() = R.layout.activity_filter_demo

    override fun afterCreate() {
    }

}
