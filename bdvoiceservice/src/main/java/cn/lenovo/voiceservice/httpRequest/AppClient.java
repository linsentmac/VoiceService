package cn.lenovo.voiceservice.httpRequest;

import android.support.compat.BuildConfig;
import android.util.Log;


import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cn.lenovo.voiceservice.jsonbean.MusicBean;
import cn.lenovo.voiceservice.jsonbean.WeatherBean;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by linsen on 17-11-24.
 */

public class AppClient {

    private static final String TAG = "SC-AppClient";
    static Retrofit mRetrofit;
    public static final String commonUrl = "http://awareness.lenovo.com.cn/nlu/?";

    public static Retrofit retrofit(String url) {
        Log.d(TAG, "url = " + url + "\n" + "mRetrofit = " + mRetrofit);
        if (mRetrofit == null) {
            /*
            * 设置cookie
            * */
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            builder.cookieJar(new JavaNetCookieJar(cookieManager))
                    .connectTimeout(30, TimeUnit.SECONDS) //设置超时
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)////失败重连
                    .addNetworkInterceptor(new Interceptor() {   //有无网络都走缓存
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Response originalResponse = chain.proceed(chain.request());
                            return originalResponse.newBuilder().build();
                        }
                    });

            try {
                // Create a trust manager that does not validate certificate chains
                final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[0];
                    }
                }};
                // Install the all-trusting trust manager
                final SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                // Create an ssl socket factory with our all-trusting manager
                final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                builder.sslSocketFactory(sslSocketFactory).hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            } catch (Exception e) {
                e.printStackTrace();
            } try {
                // Create a trust manager that does not validate certificate chains
                final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[0];
                    }
                }};
                // Install the all-trusting trust manager
                final SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                // Create an ssl socket factory with our all-trusting manager
                final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                builder.sslSocketFactory(sslSocketFactory).hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);//过滤https请求
            } catch (Exception e) {
                e.printStackTrace();
            }


            if (true) {
                // Log信息拦截器
                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        Log.d(TAG, "message = " + message);
                    }
                });

                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                //设置 Debug Log 模式
                builder.addInterceptor(loggingInterceptor);
            }


            OkHttpClient okHttpClient = builder.build();
            mRetrofit = new Retrofit.Builder()
                    .client(okHttpClient)// 设置client对象
                    .baseUrl(commonUrl)// baseurl地址
                    .addConverterFactory(GsonConverterFactory.create()) //表示调用Gson库来解析json返回值
                    .build();

        }

        return mRetrofit;
    }

    public interface ApiStores {
        /**
         * post请求获取区域
         *//*
        @POST("GetBigZoneInfos")
        @FormUrlEncoded
        Call<AreaBean> requestFansList(@Field("") String str);

        *//**
         * json请求
         * @param route
         * @return
         *//*
        @POST("HourseDetail")
        Call<HouseDetailBean> gethouseDetail(@Body RequestBody route);*/

        /**
         * get请求
         * @return
         */
        @GET("?")
        Call<WeatherBean> getDomainBean(@Query("sentence") String sentence, @Query("userid") int userid, @Query("city") String city);

        @GET("?")
        Call<MusicBean> getMusicBean(@Query("sentence") String sentence, @Query("userid") int userid);

    }

}
