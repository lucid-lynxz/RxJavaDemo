package org.lynxz.rxjavademo.activity;

import android.view.View;

import org.lynxz.rxjavademo.R;
import org.lynxz.rxjavademo.base.BaseActivity;
import org.lynxz.rxjavademo.bean.GithubUsaerBean;
import org.lynxz.rxjavademo.network.GithubService;
import org.lynxz.rxjavademo.network.RxSchedulers;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lynxz on 05/08/2017.
 * rxjava2与retrofit配合使用
 */
public class RetrofitDemoActivity extends BaseActivity {
    CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    OkHttpClient.Builder builder = new OkHttpClient()
            .newBuilder()
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS);

    Retrofit mRetrofit = new Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(builder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();

    private GithubService mGithubService;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_retrofit;
    }

    @Override
    public void afterCreate() {
        mGithubService = mRetrofit.create(GithubService.class);
        findView(R.id.btn_get_user_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserInfo("lucid-lynxz");
            }
        });
    }

    /**
     * 获取指定用户的用户信息
     */
    private void getUserInfo(String userName) {
        clearLog();
        appendLog("通过retrofit get方法获取github用户信息");
        Observable<GithubUsaerBean> observable = mGithubService.getUserInof(userName);
        observable
                .compose(RxSchedulers.<GithubUsaerBean>ioToMain())
                .subscribe(new Observer<GithubUsaerBean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull GithubUsaerBean githubUsaerBean) {
                        appendLog("onNext: " + githubUsaerBean);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        appendLog("onError: " + e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        appendLog("onComplete");
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }
}
