package dam.pmdm.api_quiz.view.quiz;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import dam.pmdm.api_quiz.R;
import dam.pmdm.api_quiz.model.Question;

public class QuizReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz_review);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            lp.topMargin = systemBars.top;
            v.setLayoutParams(lp);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbarReview);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Revisión de Respuestas");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Recuperar la lista enviada
        ArrayList<Question> questions = (ArrayList<Question>) getIntent().getSerializableExtra("questionsList");

        RecyclerView rv = findViewById(R.id.rvReview);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new QuizReviewAdapter(questions));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}