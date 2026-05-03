package dam.pmdm.api_quiz.view.unit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import dam.pmdm.api_quiz.R;
import dam.pmdm.api_quiz.controller.ApiService;
import dam.pmdm.api_quiz.controller.RetrofitClient;
import dam.pmdm.api_quiz.model.Unit;
import dam.pmdm.api_quiz.view.question.QuestionListActivity;
import dam.pmdm.api_quiz.view.quiz.QuizActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UnitListActivity extends AppCompatActivity implements UnitListAdapter.OnUnitClickListener {

    private int idmodule;
    private RecyclerView rvUnidades;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_unit_list);

        // Configurar Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarUnidades);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ImageView ivAsignaturaHeader = findViewById(R.id.ivAsignaturaHeader);

        // Recuperar datos del Intent
        idmodule = getIntent().getIntExtra("idmodule", -1);
        String moduleName = getIntent().getStringExtra("moduleName");

        // Configurar título del CollapsingToolbar
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(moduleName);

        // Cargar imagen de la cabecera (URL centralizada)
        Glide.with(this)
                .load(RetrofitClient.getImageUrl(idmodule))
                .centerCrop()
                .error(R.drawable.ic_book)
                .into(ivAsignaturaHeader);

        // Configurar RecyclerView y API
        rvUnidades = findViewById(R.id.rvUnidades);
        rvUnidades.setLayoutManager(new LinearLayoutManager(this));
        apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupFab();   // Actualiza visibilidad según modo edición
        loadUnits();  // Recarga la lista para reflejar cambios hechos en UnitActivity
    }

    private void loadUnits() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean mostrarSoloConPreguntas = prefs.getBoolean("unit_solo_preguntas", false);

        apiService.getUnidades(idmodule).enqueue(new Callback<List<Unit>>() {
            @Override
            public void onResponse(@NonNull Call<List<Unit>> call, @NonNull Response<List<Unit>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Unit> allUnits = response.body();
                    List<Unit> unitsToDisplay;

                    if (mostrarSoloConPreguntas) {
                        unitsToDisplay = new java.util.ArrayList<>();
                        for (Unit unit : allUnits) {
                            if (unit.getTotquestions() > 0) {
                                unitsToDisplay.add(unit);
                            }
                        }
                    } else {
                        // Si la preferencia está desactivada (false), mostramos la lista completa
                        unitsToDisplay = allUnits;
                    }

                    UnitListAdapter adapter = new UnitListAdapter(unitsToDisplay, UnitListActivity.this);
                    rvUnidades.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Unit>> call, @NonNull Throwable t) {
                Toast.makeText(UnitListActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

     // Navega a UnitActivity en modo creación.
    private void setupFab() {
        FloatingActionButton fabAdd = findViewById(R.id.fabAddUnidad);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean modoEdicion = prefs.getBoolean("modo_edicion", false);

        if (modoEdicion) {
            fabAdd.setVisibility(View.VISIBLE);
            fabAdd.setOnClickListener(v -> {
                Intent intent = new Intent(this, UnitActivity.class);
                intent.putExtra("idmodule", idmodule); // Pasamos idmodule para asociar la nueva unidad
                startActivity(intent);
            });
        } else {
            fabAdd.setVisibility(View.GONE);
        }
    }

    // Implementación de UnitListAdapter.OnUnitClickListener ---
    @Override
    public void onUnitClick(Unit unit) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean modoEdicion = prefs.getBoolean("modo_edicion", false);

        if (modoEdicion) {  // Modo Edición: Ver listado de preguntas
            Intent intent = new Intent(this, QuestionListActivity.class);
            intent.putExtra("idunit", unit.getIdunit());
            intent.putExtra("unitName", unit.getName());
            startActivity(intent);
        } else { // Modo Cuestionario: Realizar cuestionario
            Intent intent = new Intent(this, QuizActivity.class);
            intent.putExtra("idunit", unit.getIdunit());
            intent.putExtra("unitName", unit.getName());
            int idParaPasar = (unit.getIdmodule() > 0) ? unit.getIdmodule() : idmodule;
            intent.putExtra("idAsignatura", idParaPasar);
            startActivity(intent);
        }
    }

    @Override
    public void onUnitLongClick(Unit unit) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("modo_edicion", false)) { // Editar la unidad
            Intent intent = new Intent(this, UnitActivity.class);
            intent.putExtra("idunit", unit.getIdunit());
            intent.putExtra("unitName", unit.getName());
            intent.putExtra("idmodule", idmodule);
            startActivity(intent);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}