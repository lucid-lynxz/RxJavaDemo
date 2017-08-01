package org.lynxz.rxjavademo.activity;

import android.view.View;

import org.lynxz.rxjavademo.R;
import org.lynxz.rxjavademo.base.BaseActivity;

import java.util.HashSet;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class OperatorDemoActivity extends BaseActivity {

    @Override
    public int getLayoutRes() {
        return R.layout.activity_operator_demo;
    }

    @Override
    public void afterCreate() {
        findView(R.id.btn_zip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testZip();
            }
        });
        findView(R.id.btn_merge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testMerge();
            }
        });
    }

    /**
     * 参考: http://www.jianshu.com/p/bb58571cdb64
     */
    private void testZip() {
        appendLog("\n zip功能测试...");
        Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 10; i++) {
                    e.onNext(i);
                    appendLog(">> Observable1 发送事件onNext: " + i);
                }
            }
        });

        Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                for (int i = 0; i < 6; i++) {
                    String msg = "value" + i + " =";
                    e.onNext(msg);
                    appendLog(">> Observable2 发送事件onNext: " + msg);
                }
            }
        });

        /*
        * observable1 observable2都在默认主线程中执行,因此zip时,可以发现observable1发送完事件后,observable2才依次发送
        * 可以设置 observable1.subscribeOn(Schedulers.io())  observable1.subscribeOn(Schedulers.io()) ,就会是乱序的了
        * zip() 操作时,任意一个observable发送完后,不再发射新的事件
        * */
        Observable.zip(observable1, observable2, new BiFunction<Integer, String, String>() {
            @Override
            public String apply(@NonNull Integer integer, @NonNull String s) throws Exception {
                return s + integer;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        appendLog("<< zip Consumer 收到事件 onNext: " + s);
                    }
                });

    }

    private void testMerge() {
        appendLog("\n merge 功能测试...");
        Observable.merge(getDataFromLocal(), getDataFromNetWork())
                .subscribe(new Observer<HashSet<String>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull HashSet<String> set) {
                        for (String aSet : set) {
                            appendLog("<< merge Consumer 收到事件 onNext: " + aSet);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        appendLog("<< merge Consumer 收到事件 onError " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        appendLog("<< merge Consumer 收到事件 onComplete ");
                    }
                });
    }

    private Observable<HashSet<String>> getDataFromLocal() {
        HashSet<String> set = new HashSet<>();
        set.add("local1");
        set.add("local2");
        return Observable.just(set).subscribeOn(Schedulers.io());
    }

    private Observable<HashSet<String>> getDataFromNetWork() {
        HashSet<String> set = new HashSet<>();
        set.add("net1");
        set.add("net2");
        set.add("local1");
        return Observable.just(set).subscribeOn(Schedulers.io());
    }
}
