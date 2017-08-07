package org.lynxz.rxjavademo.activity;

import android.view.View;

import org.lynxz.rxjavademo.R;
import org.lynxz.rxjavademo.base.BaseActivity;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.AsyncSubject;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by lynxz on 07/08/2017.
 * subject的简单使用
 */
public class SubjectDemoActivity extends BaseActivity {

    Observer<String> mObserver = new Observer<String>() {
        @Override
        public void onSubscribe(@NonNull Disposable d) {
            addDisposable(d);
            appendLog("Observer onSubscribe");
        }

        @Override
        public void onNext(@NonNull String s) {
            appendLog("Observer onNext : " + s);
        }

        @Override
        public void onError(@NonNull Throwable e) {
            appendLog("\nObserver onError: " + e.getMessage() + "\n");
            e.printStackTrace();
        }

        @Override
        public void onComplete() {
            appendLog("Observer onComplete\n");
        }
    };

    @Override
    public int getLayoutRes() {
        return R.layout.activity_subject;
    }

    @Override
    public void afterCreate() {
        findView(R.id.btn_async_subject).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asyncSubjectTest();
            }
        });
        findView(R.id.btn_behavior_subject).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                behaviorSubjectTest();
            }
        });
        findView(R.id.btn_publish_subject).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishSubjectTest();
            }
        });
    }

    private <T extends Subject> void onNext(T subject, String... content) {
        for (String msg : content) {
            String nextMsg = subject.getClass().getSimpleName() + "  onNext: " + msg;
            subject.onNext(nextMsg);
            appendLog(nextMsg);
        }
    }

    private void asyncSubjectTest() {
        clearDisposable();
        clearLog();
        appendLog("AsyncSubject示例\n" +
                "Observer会接收 AsyncSubject 的 onComplete() 之前的最后一个数据\n");
        AsyncSubject<String> asyncSubject = AsyncSubject.create();
        onNext(asyncSubject, "1", "2", "3");
        asyncSubject.onComplete();
        appendLog("AsyncSubject onComplete\n");
        asyncSubject.compose(this.<String>ioToMain())
                .subscribe(mObserver);
        onNext(asyncSubject, "4", "5", "6");
    }


    private void behaviorSubjectTest() {
        clearDisposable();
        clearLog();
        appendLog("BehaviorSubject示例\n" +
                "Observer会接收到BehaviorSubject被订阅之前的最后一个数据,再接收其他发射过来的数据," +
                "不需要手动调用 onComplete()\n");
        BehaviorSubject<String> behaviorSubject = BehaviorSubject.create();
        onNext(behaviorSubject, "1", "2", "3");

//        behaviorSubject.onComplete();
//        appendLog("BehaviorSubject onComplete\n");

//        behaviorSubject.compose(this.<String>ioToMain())
        behaviorSubject.subscribe(mObserver);
        onNext(behaviorSubject, "4", "5", "6");

    }

    private void publishSubjectTest() {
        clearDisposable();
        clearLog();
        appendLog("PublishSubject示例\nObserver只会接收到PublishSubject被订阅之后发送的数据\n");
        PublishSubject<String> publishSubject = PublishSubject.create();
        onNext(publishSubject, "1", "2", "3");

//        publishSubject.onComplete();
//        appendLog("PublishSubject onComplete\n");

        publishSubject.subscribe(mObserver);
        onNext(publishSubject, "4", "5", "6");
    }
}
