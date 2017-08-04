package org.lynxz.rxjavademo.activity;

import android.widget.Button;
import android.widget.EditText;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import org.lynxz.rxjavademo.Logger;
import org.lynxz.rxjavademo.R;
import org.lynxz.rxjavademo.base.BaseActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class RxbindingActivity extends BaseActivity {
    private static final String TAG = "RxbindingActivity";
    private EditText mEdtInput;
    private Button mBtnDoubleClick;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_rxbinding;
    }

    @Override
    public void afterCreate() {
        mEdtInput = (EditText) findViewById(R.id.edt_input);
        mBtnDoubleClick = (Button) findViewById(R.id.btn_double_click);

        initEvent();
    }

    private void initEvent() {
        // 间隔1秒发射一次事件
        RxTextView.textChanges(mEdtInput)
                .debounce(1000, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .filter(new Predicate<CharSequence>() {
                    @Override
                    public boolean test(@NonNull CharSequence charSequence) throws Exception {
                        return charSequence.toString().length() > 0;
                    }
                }).subscribe(new Observer<CharSequence>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Logger.d("onSubscribe", TAG);
            }

            @Override
            public void onNext(@NonNull CharSequence charSequence) {
                appendLog(System.currentTimeMillis() + "\ttextChanges onNext: " + charSequence);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                appendLog("textChanges onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Logger.d("onComplete", TAG);
                appendLog("textChanges onComplete");
            }
        });

        // 快速单击检测, 1s内只允许指定view进行一次点击事件
        RxView.clicks(mBtnDoubleClick)
                .throttleFirst(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
//                .debounce(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        appendLog(System.currentTimeMillis() + "\t收到单击事件 onNext: " + o);
                    }
                });
    }
}
