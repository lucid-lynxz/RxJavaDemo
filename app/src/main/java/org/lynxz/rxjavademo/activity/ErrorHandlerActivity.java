package org.lynxz.rxjavademo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.lynxz.rxjavademo.Logger;
import org.lynxz.rxjavademo.R;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 参考: http://blog.csdn.net/io_field/article/details/52439967
 */
public class ErrorHandlerActivity extends AppCompatActivity {
    private static final String TAG = "ErrorHandlerActivity";
    private Observable<Integer> mObservable = Observable.create(new ObservableOnSubscribe<Integer>() {
        @Override
        public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
            e.onNext(0);
            e.onNext(1);
            e.onNext(2);

            e.onError(new Throwable("error occur"));

            e.onNext(5);
            e.onNext(6);
            e.onNext(7);

            e.onComplete();
        }
    }).subscribeOn(Schedulers.io());

    private Observable<Integer> mExceptionObservable = Observable.create(new ObservableOnSubscribe<Integer>() {
        @Override
        public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
            e.onNext(0);
            e.onNext(1);
            e.onNext(2);

            e.onError(new Exception("exception occur"));

            e.onNext(5);
            e.onNext(6);
            e.onNext(7);

            e.onComplete();
        }
    }).subscribeOn(Schedulers.io());

    private Observer<Integer> mObserver = new Observer<Integer>() {
        @Override
        public void onSubscribe(@NonNull Disposable d) {
            Logger.d("onSubscribe", TAG);
        }

        @Override
        public void onNext(@NonNull Integer integer) {
            Logger.d("receive integer: " + integer, TAG);
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Logger.d("onError " + e.getMessage(), TAG);
        }

        @Override
        public void onComplete() {
            Logger.d("complete", TAG);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_handler);

        findViewById(R.id.btn_error_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testErrorReturn();
            }
        });

        findViewById(R.id.btn_error_resume_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testErrorResume();
            }
        });

        findViewById(R.id.btn_exception_resume_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testExceptionResume();
            }
        });

        findViewById(R.id.btn_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testRetry();
            }
        });

        findViewById(R.id.btn_retry_when).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testRetryWhen();
            }
        });
    }

    /**
     * 发生错误时, 捕获异常,并尝试重新发送数据
     * 缺点是会造成数据重复
     * <p>
     * 以下代码打印的日志:
     * onSubscribe
     * receive integer: 0
     * receive integer: 1
     * receive integer: 2
     * receive integer: 0
     * receive integer: 1
     * receive integer: 2
     * receive integer: 0
     * receive integer: 1
     * receive integer: 2
     * onError error occur
     */
    private void testRetry() {
        mObservable
                .retry(2)
                .subscribe(mObserver);
    }

    private void testRetryWhen() {
        // 这里直接使用 mObservable 的话就无效...
//        mObservable
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                e.onNext(0);
                e.onNext(1);
                e.onNext(2);

                e.onError(new Throwable("error occur"));

                e.onNext(4);
                e.onNext(5);
                e.onNext(6);
            }
        })
                .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(@NonNull Observable<Throwable> throwableObservable) throws Exception {
                        /*
                        * 不会切换到新的数据源来发送数据,但会根据新数据源发送出来的事件数量来重试
                        * 从日志可以发现, observer并未收到发生异常前的数据,
                        * 以下代码返回的日志:
                        * onSubscribe
                        * receive integer: 0
                        * receive integer: 1
                        * receive integer: 2
                        * receive integer: 0
                        * receive integer: 1
                        * receive integer: 2
                        * complete
                        * */
                        return Observable.just(333, 334);
                        /*
                        * 不会切换到新的数据源来发送数据,但会根据新数据源发送出来的事件数量来重试
                        * 以下代码返回的日志:
                        * onSubscribe
                        * onError do retryWhen
                        * */
//                        return Observable.error(new Throwable(" do retryWhen")); //  会发送指定的error事件
                    }
                })
                .subscribe(mObserver);
    }


    /**
     * onErrorReturn() 会在发生异常时,返回一个指定的事件, 并跳过后续事件, 然后返回 onComplete
     * 注意: 观察者不会收到 error 事件,一切看似都是正常的
     * <p>
     * 以下代码打印的日志:
     * onSubscribe
     * receive integer: 0
     * receive integer: 1
     * receive integer: 2
     * receive integer: 100
     * complete
     */
    private void testErrorReturn() {
        mObservable
                .onErrorReturn(new Function<Throwable, Integer>() {
                    @Override
                    public Integer apply(@NonNull Throwable throwable) throws Exception {
                        return 100;
                    }
                })
                .subscribe(mObserver);
    }

    /**
     * onErrorResumeNext() 会在发生异常时会跳过后续事件,并切换到新的事件源继续发送,然后返回 onComplete
     * 注意: 观察者不会收到 error 事件,一切看似都是正常的
     * <p>
     * 以下代码打印的日志:
     * onSubscribe
     * receive integer: 0
     * receive integer: 1
     * receive integer: 2
     * receive integer: 111
     * receive integer: 112
     * complete
     */
    private void testErrorResume() {

        mObservable
                .onErrorResumeNext(Observable.just(111, 112))
                .subscribe(mObserver);

//        mObservable
//                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends Integer>>() {
//                    @Override
//                    public ObservableSource<? extends Integer> apply(@NonNull Throwable throwable) throws Exception {
//                        return Observable.just(111, 112);
//                    }
//                })
//                .subscribe(mObserver);

    }

    /**
     * onExceptionResumeNext() 跟上面的 onErrorResumeNext() 类似,只是看事件源是通过 exception 还是 throwable 发送异常事件的,
     * 会在发生异常时会跳过后续事件,并切换到新的事件源继续发送,然后返回 onComplete
     * 注意: 观察者不会收到 error 事件,一切看似都是正常的
     * <p>
     * 以下代码打印的日志:
     * onSubscribe
     * receive integer: 0
     * receive integer: 1
     * receive integer: 2
     * receive integer: 211
     * receive integer: 212
     * complete
     */
    private void testExceptionResume() {
        mExceptionObservable
                .onExceptionResumeNext(Observable.just(211, 212))
                .subscribe(mObserver);
    }


}
