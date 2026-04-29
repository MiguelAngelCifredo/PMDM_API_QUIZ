package dam.pmdm.api_quiz.view.about;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import dam.pmdm.api_quiz.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Configurar la Toolbar para que tenga botón de atrás
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Acerca de");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        findViewById(R.id.btnCerrarAbout).setOnClickListener(v -> finish());
    }

    // Permitir que el botón de atrás de la Toolbar cierre la actividad
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}