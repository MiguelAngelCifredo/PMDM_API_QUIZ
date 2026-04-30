package dam.pmdm.api_quiz.view.question;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import dam.pmdm.api_quiz.R;
import dam.pmdm.api_quiz.model.Question;

public class QuestionFragment extends Fragment {

    public interface OnAnswerSelectedListener {
        void onAnswerSelected(int position, int answer);
    }

    private Question question;
    private int position; // Necesitamos saber la posición para el Mapa de respuestas

    public static QuestionFragment newInstance(Question q, int position) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putSerializable("data", q);
        args.putInt("pos", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            question = (Question) getArguments().getSerializable("data");
            position = getArguments().getInt("pos");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_question, container, false);

        // 1. Inicializar vistas
        TextView tvTitle = v.findViewById(R.id.tvEnunciado);
        RadioGroup rgOpciones = v.findViewById(R.id.rgOpciones);
        RadioButton rb1 = v.findViewById(R.id.rbAnswer1);
        RadioButton rb2 = v.findViewById(R.id.rbAnswer2);
        RadioButton rb3 = v.findViewById(R.id.rbAnswer3);
        RadioButton rb4 = v.findViewById(R.id.rbAnswer4);

        // 2. Setear textos
        tvTitle.setText(question.getTitle());
        rb1.setText(question.getAnswer1());
        rb2.setText(question.getAnswer2());
        rb3.setText(question.getAnswer3());
        rb4.setText(question.getAnswer4());

        // 3. Configurar el listener de selección
        rgOpciones.setOnCheckedChangeListener((group, checkedId) -> {
            int answer = 0;
            if (checkedId == R.id.rbAnswer1) answer = 1;
            else if (checkedId == R.id.rbAnswer2) answer = 2;
            else if (checkedId == R.id.rbAnswer3) answer = 3;
            else if (checkedId == R.id.rbAnswer4) answer = 4;

            // Notificamos a la Activity a través de la interfaz
            if (getActivity() instanceof OnAnswerSelectedListener) {
                ((OnAnswerSelectedListener) getActivity())
                        .onAnswerSelected(position, answer);
            }
        });

        return v;
    }

}