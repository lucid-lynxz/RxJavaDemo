package org.lynxz.rxjavademo.network;

import org.lynxz.rxjavademo.bean.GithubUsaerBean;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by lynxz on 05/08/2017.
 * retrofit示例``
 */
public interface GithubService {
    /**
     * 获取github指定用户名的用户详情信息
     * https://developer.github.com/v3/users/
     */
    @GET("users/{userName}")
    Observable<GithubUsaerBean> getUserInof(@Path("userName") String userName);
}
