package org.lynxz.rxjavademo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * rxjava 的基本使用demo
 */
public class BasicDemoActivity extends AppCompatActivity {

    private static final String TAG = "BasicDemoActivity";
    private TextView mTvInfo;
    private Button mBtnCountdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_demo);
        mTvInfo = (TextView) findViewById(R.id.tv_info);
        mBtnCountdown = (Button) findViewById(R.id.btn_countdown);

        mBtnCountdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countdown();
            }
        });

        findViewById(R.id.btn_delay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.d("2s后执行事件... " + System.currentTimeMillis(), TAG);
                replaceHandler();
            }
        });

        testBasic();
    }

    /**
     * 替代handler进行延时操作
     */
    private void replaceHandler() {
        Observable.timer(2, TimeUnit.SECONDS)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Logger.d("onSubscribe");
                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {
                        Logger.d(aLong + "  当前时间: " + System.currentTimeMillis(), TAG);

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void testBasic() {
        Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 10; i++) {
                    e.onNext(i);
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())// 多次指定的话,只有第一次有效
                .observeOn(Schedulers.newThread())// 多次设置会多次切换线程
                .doOnNext(new Consumer<Integer>() {// 会收到所有数据
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Logger.d("doOnNext = " + integer + " " + Thread.currentThread().getName());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Logger.d("doOnNext = " + integer + " " + Thread.currentThread().getName());
                    }
                })
                .flatMap(new Function<Integer, ObservableSource<String>>() {//对数据进行变换,不保证接收顺序, 若要有序,使用 concatMap
                    @Override
                    public ObservableSource<String> apply(@NonNull Integer integer) throws Exception {
                        return Observable.fromArray(integer.toString());
                    }
                })
                .subscribe(new Observer<String>() {
                    private Disposable mDisposable;

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                        Logger.d("Disposable = " + d);
                    }

                    @Override
                    public void onNext(@NonNull String msg) {
                        Logger.d("onNext = " + msg + " ,isDispose = " + mDisposable.isDisposed() + " thread = " + Thread.currentThread().getName());
                        if (Integer.parseInt(msg) >= 2) {
                            // 调用dispose后,本线程就不会再收到后续的事件
                            mDisposable.dispose();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Logger.d("");
                    }

                    @Override
                    public void onComplete() {
                        Logger.d("");
                    }
                });
    }

    /**
     * 倒计时
     */
    private void countdown() {
        final int total = 10;
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(total + 1)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(@NonNull Long aLong) throws Exception {
                        return total - aLong;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        Logger.d("disposable " + disposable.isDisposed());
                        mBtnCountdown.setEnabled(false);
                        mBtnCountdown.setTextColor(Color.GRAY);
                    }
                })
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Logger.d("onSubscribe");
                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {
                        Logger.d("onNext " + aLong);
                        mTvInfo.setText("剩余 " + aLong + " 秒");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Logger.d("onComplete");
                        mBtnCountdown.setEnabled(true);
                        mBtnCountdown.setTextColor(Color.BLUE);
                    }
                });

    }
}
