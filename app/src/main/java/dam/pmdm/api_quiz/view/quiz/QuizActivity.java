package dam.pmdm.api_quiz.view.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import dam.pmdm.api_quiz.R;
import dam.pmdm.api_quiz.controller.ApiService;
import dam.pmdm.api_quiz.controller.RetrofitClient;
import dam.pmdm.api_quiz.model.Question;
import dam.pmdm.api_quiz.view.question.QuestionFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private QuizAdapter adapter;
    private List<Question> questionsList = new ArrayList<>();
    private int idunit;
    private String unitName;
    private TextView tvProgress;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Usa el layout que tengas definido (activity_quiz o activity_cuestionario)
        // Asegúrate de que los IDs coincidan
        setContentView(R.layout.activity_quiz);

        idunit = getIntent().getIntExtra("idunit", -1);
        unitName = getIntent().getStringExtra("unitName");

        viewPager = findViewById(R.id.viewPagerQuestions);
        tvProgress = findViewById(R.id.tvQuizProgress);
        btnNext = findViewById(R.id.btnNextQuestion);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(unitName != null ? unitName : "Cuestionario");
        }

        loadQuestions();

        btnNext.setOnClickListener(v -> {
            int current = viewPager.getCurrentItem();
            if (current < questionsList.size() - 1) {
                viewPager.setCurrentItem(current + 1);
            } else {
                // AQUÍ: Llamamos a la lógica de corrección antes de salir
                finalizarCuestionario();
            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateProgressText(position);
                if (position == questionsList.size() - 1) {
                    btnNext.setText("Finalizar");
                } else {
                    btnNext.setText("Siguiente");
                }
            }
        });
    }

    private void loadQuestions() {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        // Usamos max=0 para traer todas las de la unidad
        api.getPreguntasCompleta(idunit, null, 10, "s").enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    questionsList = response.body();
                    if (questionsList.isEmpty()) {
                        Toast.makeText(QuizActivity.this, "Sin preguntas", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    adapter = new QuizAdapter(QuizActivity.this, questionsList);
                    viewPager.setAdapter(adapter);
                    updateProgressText(0);
                }
            }

            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {
                Toast.makeText(QuizActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProgressText(int currentPosition) {
        if (questionsList.size() > 0) {
            String progress = (currentPosition + 1) + " / " + questionsList.size();
            tvProgress.setText(progress);
        }
    }

    private void finalizarCuestionario() {
        int aciertos = 0;

        for (int i = 0; i < questionsList.size(); i++) {
            // Buscamos el fragmento por su tag automático de ViewPager2
            QuestionFragment fragment = (QuestionFragment) getSupportFragmentManager()
                    .findFragmentByTag("f" + i);

            if (fragment != null) {
                int respuestaUsuario = fragment.getSelectedAnswer();
                int respuestaCorrecta = questionsList.get(i).getCorrect();

                if (respuestaUsuario == respuestaCorrecta) {
                    aciertos++;
                }
            }
        }

        double nota = (double) aciertos / questionsList.size() * 10;

        Intent intent = new Intent(this, QuizResultActivity.class);
        intent.putExtra("puntos", aciertos);
        intent.putExtra("total", questionsList.size());
        intent.putExtra("nota", nota);
        startActivity(intent);
        finish();
    }
}