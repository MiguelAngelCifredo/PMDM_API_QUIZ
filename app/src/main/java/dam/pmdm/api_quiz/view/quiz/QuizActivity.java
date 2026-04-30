package dam.pmdm.api_quiz.view.quiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dam.pmdm.api_quiz.R;
import dam.pmdm.api_quiz.controller.ApiService;
import dam.pmdm.api_quiz.controller.RetrofitClient;
import dam.pmdm.api_quiz.model.Question;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizActivity extends AppCompatActivity implements QuizAdapter.QuizFragment.OnAnswerSelectedListener {

    private ViewPager2 viewPager;
    private QuizAdapter adapter;
    private List<Question> questionsList = new ArrayList<>();

    // MAPA: Guarda <Posición, Respuesta> - Inmune a la destrucción de Fragmentos
    private final Map<Integer, Integer> userAnswers = new HashMap<>();

    private int idunit;
    private String unitName;
    private TextView tvProgress;
    private Button btnNext, btnPrev;
    private ProgressBar quizProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            lp.topMargin = systemBars.top;
            v.setLayoutParams(lp);
            return insets;
        });

        // Recuperar datos del Intent
        idunit = getIntent().getIntExtra("idunit", -1);
        unitName = getIntent().getStringExtra("unitName");

        // Inicializar vistas
        viewPager = findViewById(R.id.viewPagerQuestions);
        tvProgress = findViewById(R.id.tvQuizProgress);
        btnNext = findViewById(R.id.btnNextQuestion);
        btnPrev = findViewById(R.id.btnPrevQuestion);
        quizProgressBar = findViewById(R.id.quizProgressBar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(unitName != null ? unitName : "Cuestionario");
        }

        loadQuestions();

        // Lógica del botón Siguiente / Finalizar
        btnNext.setOnClickListener(v -> {
            int current = viewPager.getCurrentItem();
            if (current < questionsList.size() - 1) {
                // Si hay más preguntas, avanzamos
                viewPager.setCurrentItem(current + 1);
            } else {
                // Si es la última, pedimos confirmación antes de corregir
                confirmarFinalizacionManual();
            }
        });

        // Lógica del botón Anterior
        btnPrev.setOnClickListener(v -> {
            int current = viewPager.getCurrentItem();
            if (current > 0) {
                viewPager.setCurrentItem(current - 1);
            }
        });

        // Escuchar cambios de página para actualizar la UI
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateUI(position);
            }
        });
    }

    @Override
    public void onAnswerSelected(int position, int answer) {
        userAnswers.put(position, answer);
    }

    private void updateUI(int position) {
        if (questionsList.isEmpty()) return;

        // 1. Texto (1 / 10)
        tvProgress.setText((position + 1) + " / " + questionsList.size());

        // 2. Barra de progreso (0 a 100)
        int progressValue = (int) (((float) (position + 1) / questionsList.size()) * 100);
        quizProgressBar.setProgress(progressValue);

        // 3. Botón Anterior (Solo visible si no es la primera)
        btnPrev.setVisibility(position == 0 ? android.view.View.INVISIBLE : android.view.View.VISIBLE);

        // 4. Texto del botón principal
        btnNext.setText(position == questionsList.size() - 1 ? "Finalizar" : "Siguiente");
    }

    private void loadQuestions() {
        // 1. Acceder a las preferencias compartidas
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Obtener el valor como String y limpiar espacios
        String maxStr = prefs.getString("quiz_num_preguntas", "10").trim();
        int maxQuestions;

        try {
            maxQuestions = Integer.parseInt(maxStr);
            if (maxQuestions <= 0) maxQuestions = 10; // Valor por defecto si pone 0 o -5
        } catch (NumberFormatException e) {
            maxQuestions = 10;
        }

        boolean shouldShuffle = prefs.getBoolean("quiz_shuffle", true);
        String shuffleParam = shouldShuffle ? "s" : "n";

        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.getCuestionario(idunit, maxQuestions, shuffleParam).enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(@NonNull Call<List<Question>> call, @NonNull Response<List<Question>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    questionsList = response.body();

                    if (questionsList.isEmpty()) {
                        Toast.makeText(QuizActivity.this, "No hay preguntas disponibles", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    adapter = new QuizAdapter(QuizActivity.this, questionsList);
                    viewPager.setAdapter(adapter);
                    quizProgressBar.setMax(100);
                    updateUI(0);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Question>> call, @NonNull Throwable t) {
                Toast.makeText(QuizActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmarFinalizacionManual() {
        // Calculamos cuántas faltan por responder para avisar al usuario
        int respondidas = userAnswers.size();
        int totales = questionsList.size();
        String mensaje = "¿Deseas entregar el cuestionario ahora?";

        if (respondidas < totales) {
            mensaje = "Has respondido " + respondidas + " de " + totales + " preguntas.\n\n" + mensaje;
        }

        new AlertDialog.Builder(this)
                .setTitle("Finalizar cuestionario")
                .setMessage(mensaje)
                .setPositiveButton("Sí, entregar", (dialog, which) -> finalizarCuestionario())
                .setNegativeButton("Revisar", null)
                .show();
    }

    private void finalizarCuestionario() {
        int aciertos = 0;

        // 1. Sincronizar las respuestas del usuario con los objetos Question
        for (int i = 0; i < questionsList.size(); i++) {
            Question q = questionsList.get(i);
            Integer respuestaUsuario = userAnswers.get(i);

            // Si el usuario respondió (el mapa contiene la posición), lo guardamos en el objeto
            if (respuestaUsuario != null) {
                q.setUserAnswer(respuestaUsuario);

                // Si además coincide con la correcta, sumamos acierto
                if (respuestaUsuario == q.getCorrect()) {
                    aciertos++;
                }
            } else {
                // Si no respondió, nos aseguramos de que sea 0 (opcional pero recomendado)
                q.setUserAnswer(0);
            }
        }

        // 2. Cálculo de la nota
        double nota = (double) aciertos / questionsList.size() * 10;

        // 3. Configurar el Intent para la pantalla de resultados
        Intent intent = new Intent(this, QuizResultActivity.class);
        intent.putExtra("puntos", aciertos);
        intent.putExtra("total", questionsList.size());
        intent.putExtra("nota", nota);

        // ENVIAR LA LISTA: Aquí está la clave para que no llegue nulo
        // Casteamos a ArrayList para asegurar la compatibilidad con Serializable
        intent.putExtra("questionsList", (ArrayList<Question>) questionsList);

        startActivity(intent);
        finish(); // Cerramos QuizActivity para que el usuario no pueda volver atrás al test
    }
}