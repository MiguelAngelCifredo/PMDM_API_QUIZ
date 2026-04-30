package dam.pmdm.api_quiz.view.settings;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import dam.pmdm.api_quiz.R;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        // 1. Configurar la Toolbar como ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);

        // 2. Ajuste de Insets (Edge-to-Edge) para que no se solape con la barra de estado
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 3. Configurar el botón de "atrás" y el título
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Configuración");
        }

        // 4. Cargar el Fragment de preferencias (si es la primera vez)
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings_container, new SettingsFragment())
                    .commit();
        }
    }

    // Gestionar del clic en la flecha de la Toolbar
    @Override
    public boolean onSupportNavigateUp() {
        // Usamos getOnBackPressedDispatcher() que es la alternativa moderna a onBackPressed()
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}