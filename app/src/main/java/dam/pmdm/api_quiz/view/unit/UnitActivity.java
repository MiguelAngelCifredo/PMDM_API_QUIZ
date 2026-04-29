package dam.pmdm.api_quiz.view.unit;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import dam.pmdm.api_quiz.R;
import dam.pmdm.api_quiz.controller.ApiService;
import dam.pmdm.api_quiz.controller.RetrofitClient;
import dam.pmdm.api_quiz.model.Unit;
import dam.pmdm.api_quiz.view.module.ModuleActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UnitActivity extends AppCompatActivity {

    private TextInputEditText etNombre;
    private Button btnGuardar, btnEliminar;
    private TextView tvTitle;
    private int idmodule = -1;
    private int idunit = -1;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit);

        apiService = RetrofitClient.getClient().create(ApiService.class);
        etNombre = findViewById(R.id.etNombreUnit);
        btnGuardar = findViewById(R.id.btnGuardarUnit);
        btnEliminar = findViewById(R.id.btnEliminarUnit);
        tvTitle = findViewById(R.id.tvTitleUnit);

        // Recuperar datos del Intent
        idmodule = getIntent().getIntExtra("idmodule", -1);
        idunit = getIntent().getIntExtra("idunit", -1);
        String currentName = getIntent().getStringExtra("unitName");

        if (idunit != -1) {
            // MODO EDICIÓN
            tvTitle.setText("Editar Unidad");
            etNombre.setText(currentName);
            btnEliminar.setVisibility(View.VISIBLE);
        } else {
            // MODO CREACIÓN
            tvTitle.setText("Nueva Unidad");
        }

        btnGuardar.setOnClickListener(v -> saveUnit());
        btnEliminar.setOnClickListener(v -> confirmDelete());
    }

    private void saveUnit() {
        String nombre = etNombre.getText().toString().trim();
        if (nombre.isEmpty()) return;

        if (idunit == -1) { // Alta
            Unit.UnitAddRequest addRequest = new Unit.UnitAddRequest(idmodule, nombre);
            apiService.addUnidad(addRequest).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    String msg = "";
                    switch (response.code()) {
                        case 200 -> {
                            Toast.makeText(UnitActivity.this, "Unidad creada", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        case 400 -> msg = "Datos inválidos";
                        case 401 -> msg = "La asignatura no existe";
                        case 403 -> msg = "El nombre de la unidad ya existe";
                        default  -> msg = "Error: " + response.code();
                    }
                    Toast.makeText(UnitActivity.this, msg, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(UnitActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        } else { // Actualización
            Unit.UnitUpdateRequest updateRequest = new Unit.UnitUpdateRequest(idmodule, nombre, idunit);
            apiService.updateUnidad(updateRequest).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    String msg = "";
                    switch (response.code()) {
                        case 200 -> {
                            Toast.makeText(UnitActivity.this, "Unidad actualizada", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        case 400 -> msg = "Datos inválidos";
                        case 401 -> msg = "La asignatura no existe";
                        case 403 -> msg = "El nombre de la unidad ya existe";
                        default  -> msg = "Error: " + response.code();
                    }
                    Toast.makeText(UnitActivity.this, msg, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(UnitActivity.this, "Error al actualizar la unidad", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Unidad")
                .setMessage("¿Estás seguro? Se perderán todos los datos asociados.")
                .setPositiveButton("Eliminar", (d, w) -> {
                    apiService.deleteUnidad(idunit).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                            String msg = "";
                            switch (response.code()) {
                                case 200 -> {
                                    Toast.makeText(UnitActivity.this, "Unidad eliminada", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                case 409 -> msg = "No se puede borrar: La unidad tiene preguntas";
                                default  -> msg = "Error: " + response.code();
                            }
                            Toast.makeText(UnitActivity.this, msg, Toast.LENGTH_SHORT).show();
                            Log.d("MACIFREDO", "msg = " + msg);
                        }

                        @Override
                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                            Toast.makeText(UnitActivity.this, "Error al borrar la unidad", Toast.LENGTH_SHORT).show();
                            Log.d("MACIFREDO", "msg = FALLO GORDO" );
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

}