package dam.pmdm.api_quiz.view.question;

import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import dam.pmdm.api_quiz.R;
import dam.pmdm.api_quiz.controller.ApiService;
import dam.pmdm.api_quiz.controller.RetrofitClient;
import dam.pmdm.api_quiz.model.Question;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestionActivity extends AppCompatActivity {

    private int idunit, idquestion;
    private EditText etTitle, etA1, etA2, etA3, etA4;
    private RadioGroup rgCorrect;
    private RadioButton rb1, rb2, rb3, rb4;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        // Recuperar IDs de navegación
        idunit = getIntent().getIntExtra("idunit", -1);
        idquestion = getIntent().getIntExtra("idquestion", -1);

        // Inicializar vistas
        etTitle = findViewById(R.id.etTitle);
        etA1 = findViewById(R.id.etAnswer1);
        etA2 = findViewById(R.id.etAnswer2);
        etA3 = findViewById(R.id.etAnswer3);
        etA4 = findViewById(R.id.etAnswer4);

        rgCorrect = findViewById(R.id.rgCorrect);
        rb1 = findViewById(R.id.rb1);
        rb2 = findViewById(R.id.rb2);
        rb3 = findViewById(R.id.rb3);
        rb4 = findViewById(R.id.rb4);

        TextView tvDeleteQuestion = findViewById(R.id.tvDeleteQuestion);
        apiService = RetrofitClient.getClient().create(ApiService.class);

        // Lógica según Modo (Alta o Edición)
        if (idquestion != -1) {
            tvDeleteQuestion.setVisibility(View.VISIBLE);
            loadQuestionData();
        } else {
            rb1.setChecked(true); // Por defecto en altas
        }

        findViewById(R.id.btnSaveQuestion).setOnClickListener(v -> saveQuestion());
        tvDeleteQuestion.setOnClickListener(v -> confirmDelete());
    }

    private void loadQuestionData() {
        apiService.getPreguntasCompleta(idunit, idquestion, 1, "n").enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(@NonNull Call<List<Question>> call, @NonNull Response<List<Question>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Question q = response.body().get(0);
                    etTitle.setText(q.getTitle());
                    etA1.setText(q.getAnswer1());
                    etA2.setText(q.getAnswer2());
                    etA3.setText(q.getAnswer3());
                    etA4.setText(q.getAnswer4());

                    switch (q.getCorrect()) {
                        case 1 -> rb1.setChecked(true);
                        case 2 -> rb2.setChecked(true);
                        case 3 -> rb3.setChecked(true);
                        case 4 -> rb4.setChecked(true);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Question>> call, @NonNull Throwable t) {
                Toast.makeText(QuestionActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveQuestion() {
        String title = etTitle.getText().toString().trim();
        String a1 = etA1.getText().toString().trim();
        String a2 = etA2.getText().toString().trim();
        String a3 = etA3.getText().toString().trim();
        String a4 = etA4.getText().toString().trim();

        int checkedId = rgCorrect.getCheckedRadioButtonId();
        int correct = 1; // Por defecto
        if (checkedId == R.id.rb2) correct = 2;
        else if (checkedId == R.id.rb3) correct = 3;
        else if (checkedId == R.id.rb4) correct = 4;

        if (title.isEmpty() || a1.isEmpty() || a2.isEmpty() || a3.isEmpty() || a4.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (idquestion == -1) {
            Question.QuestionAddRequest req = new Question.QuestionAddRequest(idunit, title, a1, a2, a3, a4, correct);
            apiService.addPregunta(req).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) { handleResponse(response, "Creada"); }
                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) { finish(); }
            });
        } else {
            Question.QuestionUpdateRequest req = new Question.QuestionUpdateRequest(idquestion, idunit, title, a1, a2, a3, a4, correct);
            apiService.updatePregunta(req).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) { handleResponse(response, "Actualizada"); }
                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) { finish(); }
            });
        }
    }

    private void handleResponse(Response<Void> response, String msg) {
        if (response.isSuccessful()) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar")
                .setMessage("¿Borrar pregunta?")
                .setPositiveButton("Sí", (d, w) -> {
                    apiService.deletePregunta(idquestion).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                            if (response.isSuccessful()) finish();
                        }
                        @Override public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {}
                    });
                })
                .setNegativeButton("No", null)
                .show();
    }
}