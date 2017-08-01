package org.lynxz.rxjavademo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
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
            e.onNext(1);
            e.onNext(2);
            e.onNext(3);

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
