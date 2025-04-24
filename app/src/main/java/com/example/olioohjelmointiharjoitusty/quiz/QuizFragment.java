package com.example.olioohjelmointiharjoitusty.quiz;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.olioohjelmointiharjoitusty.R;
import com.example.olioohjelmointiharjoitusty.SharedViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizFragment extends Fragment {

    private TextView mainQuestion, progressText;
    private RadioGroup questionsHolder;
    private Button confirmChoice;

    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0, wrong = 0;
    private boolean quizInitialised = false;

    private SharedViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        mainQuestion    = view.findViewById(R.id.mainQuestion);
        progressText    = view.findViewById(R.id.progressText);
        questionsHolder = view.findViewById(R.id.questionsHolder);
        confirmChoice   = view.findViewById(R.id.confirmChoice);

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        observeDataAndInit();

        confirmChoice.setOnClickListener(v -> {
            if (!quizInitialised) { showToast("Data latautuu…"); return; }

            int selectedId = questionsHolder.getCheckedRadioButtonId();
            if (selectedId == -1) { showToast("Valitse vaihtoehto"); return; }

            int selectedIndex = questionsHolder.indexOfChild(view.findViewById(selectedId));
            Question current  = questionList.get(currentQuestionIndex);

            if (selectedIndex == current.getCorrectAnswerIndex()) { score++; showToast("Oikein!"); }
            else                                                  { wrong++; showToast("Väärin!"); }

            currentQuestionIndex++;
            if (currentQuestionIndex < questionList.size()) loadQuestion();
            else                                            showResultDialog();
        });

        return view;
    }

    /* ---------- LiveData-havainto & alustus ---------- */

    private void observeDataAndInit() {
        viewModel.getPopulation1().observe(getViewLifecycleOwner(), x -> tryInitQuiz());
        viewModel.getPopulation2().observe(getViewLifecycleOwner(), x -> tryInitQuiz());
        viewModel.getEmployment1().observe(getViewLifecycleOwner(), x -> tryInitQuiz());
        viewModel.getEmployment2().observe(getViewLifecycleOwner(), x -> tryInitQuiz());
        viewModel.getSufficiency1().observe(getViewLifecycleOwner(), x -> tryInitQuiz());
        viewModel.getSufficiency2().observe(getViewLifecycleOwner(), x -> tryInitQuiz());
        viewModel.getFirstCity().observe(getViewLifecycleOwner(), x -> tryInitQuiz());
        viewModel.getSecondCity().observe(getViewLifecycleOwner(), x -> tryInitQuiz());
    }

    private void tryInitQuiz() {
        if (quizInitialised) return;

        String  c1 = viewModel.getFirstCity().getValue();
        String  c2 = viewModel.getSecondCity().getValue();
        Integer p1 = viewModel.getPopulation1().getValue();
        Integer p2 = viewModel.getPopulation2().getValue();
        Double  e1 = viewModel.getEmployment1().getValue();
        Double  e2 = viewModel.getEmployment2().getValue();
        Double  s1 = viewModel.getSufficiency1().getValue();
        Double  s2 = viewModel.getSufficiency2().getValue();

        if (c1 == null || c2 == null || p1 == null || p2 == null || e1 == null || e2 == null || s1 == null || s2 == null)
            return;   // odotetaan, kunnes kaikki dataa

        questionList = new ArrayList<>();
        for (QuizQuestion q : QuizDataGenerator.generate(c1, c2, p1, p2, e1, e2, s1, s2)) {
            questionList.add(new Question(q.getQuestionText(), q.getOptions(), q.getCorrectAnswerIndex()));
        }
        Collections.shuffle(questionList);
        quizInitialised = true;
        loadQuestion();
    }

    /* ---------- Kysymysnäkymä ---------- */

    private void loadQuestion() {
        questionsHolder.removeAllViews();
        Question q = questionList.get(currentQuestionIndex);

        mainQuestion.setText(q.getQuestionText());
        progressText.setText("Kysymys " + (currentQuestionIndex + 1) + "/" + questionList.size());

        for (String opt : q.getOptions()) {
            RadioButton rb = new RadioButton(getContext());
            rb.setText(opt);
            rb.setTextColor(getResources().getColor(android.R.color.white));
            rb.setId(View.generateViewId());
            questionsHolder.addView(rb);
        }
    }

    /* ---------- Helper-metodit ---------- */

    private void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void showResultDialog() {
        View resultView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_quiz_result, null);
        TextView tvResult = resultView.findViewById(R.id.tvResult);
        Button btnRetry   = resultView.findViewById(R.id.btnRetry);

        tvResult.setText("Quiz loppui!\nOikein: " + score + "\nVäärin: " + wrong);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(resultView)
                .setCancelable(false)
                .create();

        btnRetry.setOnClickListener(v -> {
            score = wrong = currentQuestionIndex = 0;
            Collections.shuffle(questionList);
            loadQuestion();
            dialog.dismiss();
        });

        dialog.show();
    }
}
