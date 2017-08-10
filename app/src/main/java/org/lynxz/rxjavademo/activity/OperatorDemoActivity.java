package org.lynxz.rxjavademo.activity;

import android.content.Intent;
import android.view.View;

import org.lynxz.rxjavademo.R;
import org.lynxz.rxjavademo.base.BaseActivity;

import java.io.Serializable;

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

    Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
        @Override
        public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
            for (int i = 0; i < 3; i++) {
                appendLog("Observable1 发送事件onNext: " + i);
                e.onNext(i);
                Thread.sleep(200);
            }
            // 对于concat操作需要明确发出complete事件才会接收其他observable信息
            e.onComplete();
        }
    }).subscribeOn(Schedulers.io());

    Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
        @Override
        public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
            for (int i = 0; i < 3; i++) {
                String msg = "value" + i + "=";
                appendLog("Observable2 发送事件onNext: " + msg);
                e.onNext(msg);
            }
            e.onComplete();
        }
    }).subscribeOn(Schedulers.io());

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
        findView(R.id.btn_filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OperatorDemoActivity.this, FilterDemoActivity.class));
            }
        });
        findView(R.id.btn_concat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testConcat();
            }
        });
        findView(R.id.btn_ori_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOriData();
            }
        });
    }

    /**
     * 显示所用的数据源
     */
    private void showOriData() {
        clearLog();
        Observable.concat(observable1, observable2).subscribe();
    }

    private void testConcat() {
        clearLog();
        appendLog("\nconcat 功能测试\n" +
                "按顺序接收各observable发射的数据,要求前一个observable发出onComplete()后才会继续接收下一个Observable的数据\n");
        Observable.concat(observable1, observable2)
                .subscribe(new Observer<Serializable>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull Serializable serializable) {
                        appendLog("<< onNext: " + serializable);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        appendLog("<< onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        appendLog("<< onComplete");
                    }
                });
    }

    /**
     * 参考: http://www.jianshu.com/p/bb58571cdb64
     */
    private void testZip() {
        clearLog();
        appendLog("\n zip功能测试\n将多个observable的发射内容进行合并操作\n");
        /*
        * observable1 observable2都在默认主线程中执行,因此zip时,可以发现observable1发送完事件后,observable2才依次发送
        * 可以设置 observable1.subscribeOn(Schedulers.io())  observable1.subscribeOn(Schedulers.io()) ,就会是乱序的了
        * zip() 操作时,任意一个observable发送完后,不再发射新的事件
        * */
        Observable.zip(observable1, observable2,
                new BiFunction<Integer, String, String>() {
                    @Override
                    public String apply(@NonNull Integer integer, @NonNull String s) throws Exception {
                        return s + integer;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        appendLog("<< onNext: " + s);
                    }
                });
    }

    private void testMerge() {
        clearLog();
        appendLog("\nmerge 功能测试\n将多个observable合并成一个observable\n");
        Observable.merge(observable1, observable2)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Serializable>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull Serializable serializable) {
                        appendLog("<< onNext: " + serializable);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        appendLog("<< onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        appendLog("<< onComplete");
                    }
                });
    }
}