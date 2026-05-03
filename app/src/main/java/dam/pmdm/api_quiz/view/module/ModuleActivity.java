package dam.pmdm.api_quiz.view.module;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import dam.pmdm.api_quiz.R;
import dam.pmdm.api_quiz.controller.ApiService;
import dam.pmdm.api_quiz.controller.RetrofitClient;
import dam.pmdm.api_quiz.model.Module;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ModuleActivity extends AppCompatActivity {

    private ImageView ivAsignatura;
    private TextInputEditText etNombre;
    private int idModule = -1;
    private Uri imageUri;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_module);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            lp.topMargin = systemBars.top;
            v.setLayoutParams(lp);
            return insets;
        });

        // Inicializar vistas y API
        apiService = RetrofitClient.getClient().create(ApiService.class);
        ivAsignatura = findViewById(R.id.ivAsignatura);
        etNombre = findViewById(R.id.etNombre);
        Button btnGuardar = findViewById(R.id.btnGuardar);
        Button btnEliminar = findViewById(R.id.btnEliminar);

        // Configurar modo Edición si recibimos extras
        if (getIntent().hasExtra("idmodule")) {
            idModule = getIntent().getIntExtra("idmodule", -1);
            String nombre = getIntent().getStringExtra("moduleName");
            etNombre.setText(nombre);
            btnEliminar.setVisibility(View.VISIBLE);
            if (imageUri == null) {
                Glide.with(this)
                        .load(RetrofitClient.getImageUrl(idModule))
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .error(R.drawable.ic_book)
                        .into(ivAsignatura);
            }
        }

        // Listeners
        ivAsignatura.setOnClickListener(v -> openGallery());
        btnGuardar.setOnClickListener(v -> saveAsignatura());
        btnEliminar.setOnClickListener(v -> confirmDelete());
    }

    // --- GESTIÓN DE IMAGEN ---

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    Glide.with(this)
                            .load(imageUri)
                            .into(ivAsignatura);
                }
            }
    );

    // --- LÓGICA DE NEGOCIO (RED) ---

    private void saveAsignatura() {
        String nombre = etNombre.getText().toString().trim();
        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        if (idModule == -1) {
            ejecutarAlta(nombre);
        } else {
            ejecutarActualizacion(nombre);
        }
    }

    private void ejecutarAlta(String nombre) {
        Module nuevo = new Module();
        nuevo.setName(nombre);

        apiService.addAsignatura(nuevo).enqueue(new Callback<Module.AddResponse>() {
            @Override
            public void onResponse(@NonNull Call<Module.AddResponse> call, @NonNull Response<Module.AddResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int idGenerado = response.body().getIdmodule();
                    if (imageUri != null) {
                        uploadImage(idGenerado);
                    } else {
                        Toast.makeText(ModuleActivity.this, "Creada con éxito", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(ModuleActivity.this, "Error: Nombre duplicado o inválido", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<Module.AddResponse> call, @NonNull Throwable t) {
                Toast.makeText(ModuleActivity.this, "Error al crear la asignatura", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ejecutarActualizacion(String nombre) {
        Module editado = new Module();
        editado.setIdmodule(idModule);
        editado.setName(nombre);

        apiService.updateAsignatura(editado).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    if (imageUri != null) {
                        uploadImage(idModule);
                    } else {
                        Toast.makeText(ModuleActivity.this, "Actualizada con éxito", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(ModuleActivity.this, "Error al actualizar la asignatura", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImage(int id) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            File tempFile = File.createTempFile("upload", ".jpg", getCacheDir());
            copyInputStreamToFile(inputStream, tempFile);

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), tempFile);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", tempFile.getName(), requestFile);
            RequestBody idBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(id));

            apiService.uploadAsignaturaImage(idBody, body).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    Toast.makeText(ModuleActivity.this, "Imagen guardada correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                }
                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(ModuleActivity.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (IOException e) {
            Toast.makeText(ModuleActivity.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Asignatura")
                .setMessage("¿Estás seguro de que deseas eliminar '" + etNombre.getText() + "'?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    apiService.deleteAsignatura(idModule).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(ModuleActivity.this, "Eliminada", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                        @Override
                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) { /* Error */ }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // --- UTILIDADES ---

    private void copyInputStreamToFile(InputStream in, File file) throws IOException {
        try (OutputStream out = new FileOutputStream(file)) {
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
        }
    }

}