package dam.pmdm.api_quiz.view.quiz;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import dam.pmdm.api_quiz.R;
import dam.pmdm.api_quiz.model.Question;

public class QuizReviewAdapter extends RecyclerView.Adapter<QuizReviewAdapter.ReviewViewHolder> {

    private List<Question> questions;

    public QuizReviewAdapter(List<Question> questions) {
        this.questions = questions;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Question q = questions.get(position);
        holder.tvQuestion.setText(q.getTitle());

        // Obtener los textos de las respuestas según el índice
        String userTxt = getAnswerText(q, q.getUserAnswer());
        String correctTxt = getAnswerText(q, q.getCorrect());

        holder.tvUserAnswer.setText("Tu respuesta: " + (q.getUserAnswer() == 0 ? "No respondida" : userTxt));
        holder.tvCorrectAnswer.setText("Respuesta correcta: " + correctTxt);

        // Lógica de colores: Verde si acertó, Rojo si falló
        holder.tvUserAnswer.setVisibility(q.isCorrect() ? View.GONE : View.VISIBLE);
        if (q.isCorrect()) {
            //holder.tvUserAnswer.setTextColor(Color.parseColor("#4CAF50")); // Verde
        } else {
            holder.tvUserAnswer.setTextColor(Color.RED);
        }
    }

    private String getAnswerText(Question q, int index) {
        return switch (index) {
            case 1 -> q.getAnswer1();
            case 2 -> q.getAnswer2();
            case 3 -> q.getAnswer3();
            case 4 -> q.getAnswer4();
            default -> "N/A";
        };
    }

    @Override
    public int getItemCount() { return questions.size(); }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestion, tvUserAnswer, tvCorrectAnswer;
        ReviewViewHolder(View v) {
            super(v);
            tvQuestion = v.findViewById(R.id.tvReviewQuestion);
            tvUserAnswer = v.findViewById(R.id.tvReviewUserAnswer);
            tvCorrectAnswer = v.findViewById(R.id.tvReviewCorrectAnswer);
        }
    }
}
