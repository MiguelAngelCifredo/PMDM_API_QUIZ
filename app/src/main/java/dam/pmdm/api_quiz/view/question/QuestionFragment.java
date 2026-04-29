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

    private Question question;

    public static QuestionFragment newInstance(Question q) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        // Question debe implementar Serializable o Parcelable
        args.putSerializable("data", q);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            question = (Question) getArguments().getSerializable("data");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_question, container, false);

        TextView tvTitle = v.findViewById(R.id.tvEnunciado);
        RadioButton rb1 = v.findViewById(R.id.rbAnswer1);
        RadioButton rb2 = v.findViewById(R.id.rbAnswer2);
        RadioButton rb3 = v.findViewById(R.id.rbAnswer3);
        RadioButton rb4 = v.findViewById(R.id.rbAnswer4);

        tvTitle.setText(question.getTitle());
        rb1.setText(question.getAnswer1());
        rb2.setText(question.getAnswer2());
        rb3.setText(question.getAnswer3());
        rb4.setText(question.getAnswer4());

        return v;
    }

    // Obtener la respuesta seleccionada (1, 2, 3, 4 o 0 si no hay selección)
    public int getSelectedAnswer() {
        RadioGroup rg = getView().findViewById(R.id.rgOpciones);
        int checkedId = rg.getCheckedRadioButtonId();

        if (checkedId == R.id.rbAnswer1) return 1;
        if (checkedId == R.id.rbAnswer2) return 2;
        if (checkedId == R.id.rbAnswer3) return 3;
        if (checkedId == R.id.rbAnswer4) return 4;

        return 0; // Ninguna seleccionada
    }

}