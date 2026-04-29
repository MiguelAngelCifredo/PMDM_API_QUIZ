package dam.pmdm.api_quiz.view.quiz;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;
import dam.pmdm.api_quiz.model.Question;
import dam.pmdm.api_quiz.view.question.QuestionFragment;

public class QuizAdapter extends FragmentStateAdapter {

    private List<Question> questions;

    public QuizAdapter(@NonNull FragmentActivity fragmentActivity, List<Question> questions) {
        super(fragmentActivity);
        this.questions = questions;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Por cada pregunta, creamos una instancia del Fragment pasándole los datos
        return QuestionFragment.newInstance(questions.get(position));
    }

    @Override
    public int getItemCount() {
        return questions != null ? questions.size() : 0;
    }
}
