package dam.pmdm.api_quiz.controller;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;
    private static final String BASE_URL = "https://kompassaviacion.com/DAMDAW/quiz/";

    public static Retrofit getClient() {
        if (retrofit == null) {
            // 1. Creamos el interceptor y le decimos que queremos ver el CUERPO (BODY) de las peticiones
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // 2. Añadimos el interceptor a un cliente OkHttpClient
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            // 3. Configuramos Retrofit para que use ese cliente
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static String getImageUrl(int id) {
        return BASE_URL + "images/" + id + ".jpg";
    }
}