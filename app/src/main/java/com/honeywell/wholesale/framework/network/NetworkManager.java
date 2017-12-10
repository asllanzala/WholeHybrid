package com.honeywell.wholesale.framework.network;


import android.os.Build;
import android.util.Log;

import com.honeywell.wholesale.framework.http.MySSLSocketFactory;
import com.honeywell.wholesale.framework.http.TLSSocketFactory;
import com.honeywell.wholesale.framework.http.Tls12SocketFactory;
import com.honeywell.wholesale.framework.http.WebServerConfigManager;


import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by xiaofei on 3/24/17.
 */

public class NetworkManager {

    private static Retrofit retrofit;

    private static NetworkManager instance = null;
    private static final int SOCKET_TIMEOUT_MS = 15;


    private NetworkManager() {
//        OkHttpClient.Builder httpClientBuilder = null;
//        try {
//            httpClientBuilder = new OkHttpClient.Builder().sslSocketFactory(new TLSSocketFactory());
//        } catch (KeyManagementException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//
//        httpClientBuilder.connectTimeout(SOCKET_TIMEOUT_MS, TimeUnit.SECONDS);
//
//        String baseUrl = WebServerConfigManager.getWebServiceUrl();
//        retrofit = new Retrofit.Builder()
//                .client(httpClientBuilder.build())
//                .baseUrl(baseUrl)
//                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                .build();
    }

    public static NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    public OkHttpClient.Builder getOkhttpBuilder() {
        OkHttpClient.Builder httpClientBuilder = null;
        try {
            httpClientBuilder = new OkHttpClient.Builder().sslSocketFactory(new TLSSocketFactory());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        httpClientBuilder.connectTimeout(SOCKET_TIMEOUT_MS, TimeUnit.SECONDS);
        return null;
    }


    public <T extends NetService> T create(final Class<T> service) {
        if (retrofit != null) {
            return retrofit.create(service);
        }
        return null;
    }

    public <T> void toSubscribe(Observable<T> o, Subscriber<T> s) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }


    private class HttpResultFunc<T> implements Func1<HttpResult<T>, T> {

        @Override
        public T call(HttpResult<T> httpResult) {
            if (httpResult.getResultCode() != 200) {
                throw new NetException(httpResult.getResultCode());
            }
            return httpResult.getData();
        }
    }


    public OkHttpClient.Builder setSSL() throws Exception {
        final X509TrustManager trustManager = systemDefaultTrustManager();

        SSLSocketFactory delegate = new TLSSocketFactory(new X509TrustManager[]{trustManager}, new SecureRandom());

        return new OkHttpClient.Builder()
                .sslSocketFactory(delegate, trustManager)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });

    }

    private X509TrustManager systemDefaultTrustManager() {
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:"
                        + Arrays.toString(trustManagers));
            }
            return (X509TrustManager) trustManagers[0];
        } catch (GeneralSecurityException e) {
            throw new AssertionError(); // The system has no TLS. Just give up.
        }
    }

}
