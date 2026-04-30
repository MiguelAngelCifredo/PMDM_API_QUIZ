package dam.pmdm.api_quiz.view.module;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import dam.pmdm.api_quiz.R;
import dam.pmdm.api_quiz.controller.ApiService;
import dam.pmdm.api_quiz.controller.RetrofitClient;
import dam.pmdm.api_quiz.model.Module;

import dam.pmdm.api_quiz.view.about.AboutActivity;
import dam.pmdm.api_quiz.view.settings.SettingsActivity;
import dam.pmdm.api_quiz.view.unit.UnitListActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ModuleListActivity extends AppCompatActivity implements ModuleListAdapter.OnModuleClickListener {

    private RecyclerView rvAsignaturas;
    private ModuleListAdapter adapter;
    private FloatingActionButton fabAdd;
    private ApiService apiService;

    private List<Module> modulesList = new ArrayList<>(); // Lista que se muestra
    private List<Module> modulesListFull = new ArrayList<>(); // Copia de seguridad siempre completa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_module_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Inicializar vistas
        rvAsignaturas = findViewById(R.id.rvAsignaturas);
        rvAsignaturas.setLayoutManager(new LinearLayoutManager(this));
        fabAdd = findViewById(R.id.fabAddAsignatura);

        // Inicializar controlador API
        apiService = RetrofitClient.getClient().create(ApiService.class);

        // Configurar el botón flotante (Nueva Asignatura)
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(ModuleListActivity.this, ModuleActivity.class);
            startActivity(intent); // No pasamos extras -> idModule será -1
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPreferences();
        loadModules();
    }

    private void checkPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean modoEdicion = prefs.getBoolean("modo_edicion", false);
        fabAdd.setVisibility(modoEdicion ? View.VISIBLE : View.GONE);
    }

    private void loadModules() {
        // 1. Obtener la preferencia del usuario
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean mostrarSoloConUnidades = prefs.getBoolean("adj_solo_unidades", false);

        apiService.getAsignaturas().enqueue(new Callback<List<Module>>() {
            @Override
            public void onResponse(Call<List<Module>> call, Response<List<Module>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Guardamos la respuesta original de la API
                    List<Module> allModules = response.body();

                    // 2. Aplicar el filtrado según la preferencia
                    if (mostrarSoloConUnidades) {
                        List<Module> filteredList = new ArrayList<>();
                        for (Module m : allModules) {
                            if (m.getTotunits() > 0) {
                                filteredList.add(m);
                            }
                        }
                        modulesList = filteredList;
                    } else {
                        // Si la preferencia está desactivada, mostramos todas las asignaturas
                        modulesList = allModules;
                    }

                    modulesListFull = new ArrayList<>(modulesList);
                    adapter = new ModuleListAdapter(modulesList, ModuleListActivity.this);
                    rvAsignaturas.setAdapter(adapter);

                } else {
                    Toast.makeText(ModuleListActivity.this, "Error en la respuesta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Module>> call, Throwable t) {
                Toast.makeText(ModuleListActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // --- Implementación de la Interfaz del Adapter ---

    @Override
    public void onModuleClick(Module module) {
        Intent intent = new Intent(this, UnitListActivity.class);
        intent.putExtra("idmodule", module.getIdmodule());
        intent.putExtra("moduleName", module.getName());
        startActivity(intent);
    }

    @Override
    public void onModuleLongClick(Module module) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean modoEdicion = prefs.getBoolean("modo_edicion", false);

        if (modoEdicion) {
            Intent intent = new Intent(this, ModuleActivity.class);
            intent.putExtra("idmodule", module.getIdmodule());
            intent.putExtra("moduleName", module.getName());
            startActivity(intent);
        } else {
            Toast.makeText(this, "Active el modo edición en ajustes", Toast.LENGTH_SHORT).show();
        }
    }

    // --- Gestión del Menú ---

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Esto "infla" el archivo XML del menú en la Toolbar
        getMenuInflater().inflate(R.menu.main_menu, menu);

        // Configuración del buscador (SearchView) si lo tienes
        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            SearchView searchView = (SearchView) searchItem.getActionView();
            if (searchView != null) {
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        filter(newText);
                        return true;
                    }
                });
            }
        }
        return true;
    }

    private void filter(String text) {
        List<Module> filteredList = new ArrayList<>();

        for (Module item : modulesListFull) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        adapter.filterList(filteredList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        } else if (id == R.id.action_exit) {
            confirmExit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmExit() {
        new AlertDialog.Builder(this)
                .setTitle("Salir")
                .setMessage("¿Estás seguro de que deseas cerrar la aplicación?")
                .setPositiveButton("Sí", (dialog, which) -> finish())
                .setNegativeButton("No", null)
                .show();
    }

}