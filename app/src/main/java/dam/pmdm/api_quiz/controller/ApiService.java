package dam.pmdm.api_quiz.controller;

import java.util.List;

import dam.pmdm.api_quiz.model.Module;
import dam.pmdm.api_quiz.model.Question;
import dam.pmdm.api_quiz.model.Unit;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {
    // --- ASIGNATURAS ---
    @GET("asignatura-lst.php")
    Call<List<Module>> getAsignaturas();

    @POST("asignatura-add.php")
    Call<Module.AddResponse> addAsignatura(@Body Module name);

    @POST("asignatura-set.php")
    Call<Void> updateAsignatura(@Body Module module);

    @FormUrlEncoded
    @POST("asignatura-del.php")
    Call<Void> deleteAsignatura(@Field("idmodule") int idmodule);

    @Multipart
    @POST("asignatura-img-set.php")
    Call<Void> uploadAsignaturaImage(
            @Part("idmodule") RequestBody idmodule,
            @Part MultipartBody.Part image
    );

    // --- UNIDADES ---
    @GET("unidad-lst.php")
    Call<List<Unit>> getUnidades(@Query("idmodule") int idmodule);

    @Headers("Content-Type: application/json")
    @POST("unidad-set.php")
    Call<Void> updateUnidad(@Body Unit.UnitUpdateRequest request);

    @POST("unidad-add.php")
    Call<Void> addUnidad(@Body Unit.UnitAddRequest request);

    @FormUrlEncoded
    @POST("unidad-del.php")
    Call<Void> deleteUnidad(@Field("idunit") int idunit);

    // --- PREGUNTAS (CUESTIONARIO) ---

    @GET("pregunta-lst.php")
    Call<List<Question>> getPreguntas(@Query("idunit") int idunit);

    @GET("pregunta-get.php")
    Call<List<Question>> getPreguntasCompleta(
            @Query("idunit") int idunit,
            @Query("idquestion") Integer idquestion,
            @Query("max") int max,
            @Query("shuffle") String shuffle
    );

    @POST("pregunta-add.php")
    Call<Void> addPregunta(@Body Question.QuestionAddRequest request);

    @POST("pregunta-set.php")
    Call<Void> updatePregunta(@Body Question.QuestionUpdateRequest request);

    @FormUrlEncoded
    @POST("pregunta-del.php")
    Call<Void> deletePregunta(@Field("idquestion") int idquestion);

}