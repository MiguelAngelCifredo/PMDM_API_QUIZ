package dam.pmdm.api_quiz.view.question;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import dam.pmdm.api_quiz.R;
import dam.pmdm.api_quiz.controller.ApiService;
import dam.pmdm.api_quiz.controller.RetrofitClient;
import dam.pmdm.api_quiz.model.Question;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestionListActivity extends AppCompatActivity {
    private int idunit;
    private String unitName;
    private RecyclerView rv;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_question_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        idunit = getIntent().getIntExtra("idunit", -1);
        unitName = getIntent().getStringExtra("unitName");

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("UD: " + unitName);
        }

        rv = findViewById(R.id.rvQuestionsAdmin);
        rv.setLayoutManager(new LinearLayoutManager(this));
        apiService = RetrofitClient.getClient().create(ApiService.class);

        findViewById(R.id.fabAddQuestion).setOnClickListener(v -> {
            Intent i = new Intent(this, QuestionActivity.class);
            i.putExtra("idunit", idunit);
            startActivity(i);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadQuestions(); // Se refrescará al volver de QuestionActivity tras borrar o editar
    }

    private void loadQuestions() {
        apiService.getPreguntas(idunit).enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(@NonNull Call<List<Question>> call, @NonNull Response<List<Question>> response) {
                if ((response.code() == 200 || response.code() == 201) && response.body() != null) {
                    rv.setAdapter(new QuestionListAdapter(response.body(), idunit));
                }
                else if (response.code() == 404) {
                    rv.setAdapter(new QuestionListAdapter(new ArrayList<>(), idunit));
                    Toast.makeText(QuestionListActivity.this, "Aún no hay preguntas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Question>> call, @NonNull Throwable t) {
                Toast.makeText(QuestionListActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}