package net.aldar.cramello.apiHandler;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static net.aldar.cramello.view.App.API_BASE_URL;


public class BaseApiHandler {
    private static Retrofit retrofit = null;
    private static final int TIMEOUT_SEC = 30;

    public static Retrofit setupBaseApi() {
        if (retrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(TIMEOUT_SEC, TimeUnit.SECONDS)
                    .readTimeout(TIMEOUT_SEC, TimeUnit.SECONDS)
                    .addInterceptor(interceptor).build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }

        return retrofit;
    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }

    public static void setRetrofit(Retrofit retrofit) {
        BaseApiHandler.retrofit = retrofit;
    }
}
