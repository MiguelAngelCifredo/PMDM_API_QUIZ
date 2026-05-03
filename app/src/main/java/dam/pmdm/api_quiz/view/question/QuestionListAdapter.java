package dam.pmdm.api_quiz.view.question;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dam.pmdm.api_quiz.R;
import dam.pmdm.api_quiz.model.Question;

public class QuestionListAdapter extends RecyclerView.Adapter<QuestionListAdapter.QViewHolder> {
    private final List<Question> list;
    private final int idunit;

    public QuestionListAdapter(List<Question> list, int idunit) {
        this.list = list;
        this.idunit = idunit;
    }

    @NonNull
    @Override
    public QViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new QViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull QViewHolder holder, int position) {
        Question q = list.get(position);
        holder.tvTitle.setText(q.getTitle());

        // Al hacer clic en cualquier parte de la fila, vamos a editar
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), QuestionActivity.class);
            intent.putExtra("idunit", this.idunit);
            intent.putExtra("idquestion", q.getIdquestion());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class QViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        QViewHolder(View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvQuestionTitle);
        }
    }
}