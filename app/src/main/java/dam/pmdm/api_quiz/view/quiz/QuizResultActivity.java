package dam.pmdm.api_quiz.view.quiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;

import dam.pmdm.api_quiz.R;
import dam.pmdm.api_quiz.model.Question;

public class QuizResultActivity extends AppCompatActivity {

    private ArrayList<Question> questionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_result);

        // 1. Recuperar los datos del Intent
        int puntos = getIntent().getIntExtra("puntos", 0);
        int total = getIntent().getIntExtra("total", 0);
        double nota = getIntent().getDoubleExtra("nota", 0.0);

        // Recuperamos la lista de preguntas con las respuestas del usuario
        questionsList = (ArrayList<Question>) getIntent().getSerializableExtra("questionsList");

        // 2. Vincular vistas
        TextView tvNota = findViewById(R.id.tvNotaNumerica);
        TextView tvDetalle = findViewById(R.id.tvDetalleAciertos);
        TextView tvMensaje = findViewById(R.id.tvResultadoTexto);
        Button btnVolver = findViewById(R.id.btnVolverInicio);
        Button btnRevisar = findViewById(R.id.btnRevisarRespuestas);

        // 3. Mostrar los resultados básicos
        tvNota.setText(String.format("%.1f", nota));
        tvDetalle.setText("Has acertado " + puntos + " de " + total + " preguntas");

        // 4. Personalización según la nota
        if (nota >= 5) {
            tvMensaje.setText("¡Enhorabuena, has aprobado!");
            tvNota.setTextColor(Color.parseColor("#4CAF50")); // Verde
        } else {
            tvMensaje.setText("Buen intento, pero necesitas repasar.");
            tvNota.setTextColor(Color.RED);
        }

        // --- 5. Lógica de Preferencias ---
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // A) Preferencia: Mostrar Calificación
        boolean mostrarNota = prefs.getBoolean("quiz_show_score", true);
        tvNota.setVisibility(mostrarNota ? View.VISIBLE : View.GONE);

        // B) Preferencia: Mostrar revisión (respuestas correctas)
        boolean mostrarRevision = prefs.getBoolean("quiz_show_correct", true);

        if (mostrarRevision && questionsList != null) {
            btnRevisar.setVisibility(View.VISIBLE);
            btnRevisar.setOnClickListener(v -> {
                Intent intent = new Intent(QuizResultActivity.this, QuizReviewActivity.class);
                intent.putExtra("questionsList", questionsList);
                startActivity(intent);
            });
        }

        // 6. Volver al inicio
        btnVolver.setOnClickListener(v -> finish());
    }
}