package dam.pmdm.api_quiz.view.quiz;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

import dam.pmdm.api_quiz.model.Question;
import dam.pmdm.api_quiz.view.question.QuestionFragment;

public class QuizPagerAdapter extends FragmentStateAdapter {
    private List<Question> questions;

    public QuizPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Question> questions) {
        super(fragmentActivity);
        this.questions = questions;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Creamos una instancia del fragmento pasando la pregunta actual
        return QuestionFragment.newInstance(questions.get(position));
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }
}