package dam.pmdm.api_quiz.view.quiz;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import dam.pmdm.api_quiz.R;

public class QuizResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // 1. Recuperar los datos del Intent
        int puntos = getIntent().getIntExtra("puntos", 0);
        int total = getIntent().getIntExtra("total", 0);
        double nota = getIntent().getDoubleExtra("nota", 0.0);

        // 2. Vincular vistas
        TextView tvNota = findViewById(R.id.tvNotaNumerica);
        TextView tvDetalle = findViewById(R.id.tvDetalleAciertos);
        TextView tvMensaje = findViewById(R.id.tvResultadoTexto);
        Button btnVolver = findViewById(R.id.btnVolverInicio);

        // 3. Mostrar los resultados
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

        // 5. Volver al inicio (MainActivity)
        btnVolver.setOnClickListener(v -> {
            finish(); // Cierra esta pantalla y vuelve a la anterior (UnitsActivity)
        });
    }
}