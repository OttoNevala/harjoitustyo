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

import com.example.olioohjelmointiharjoitusty.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizFragment extends Fragment {

    private TextView mainQuestion, progressText;
    private RadioGroup questionsHolder;
    private Button confirmChoice;

    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int wrong = 0; // Counter wrong answers

    public QuizFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        mainQuestion = view.findViewById(R.id.mainQuestion);
        progressText = view.findViewById(R.id.progressText);
        questionsHolder = view.findViewById(R.id.questionsHolder);
        confirmChoice = view.findViewById(R.id.confirmChoice);

        // Sufling questions
        initQuestions();
        Collections.shuffle(questionList);

        // First question load
        loadQuestion();

        confirmChoice.setOnClickListener(v -> {
            int selectedId = questionsHolder.getCheckedRadioButtonId();
            if (selectedId == -1) {
                showToast("Valitse vaihtoehto");
                return;
            }
            int selectedIndex = questionsHolder.indexOfChild(view.findViewById(selectedId));
            Question currentQuestion = questionList.get(currentQuestionIndex);
            if (selectedIndex == currentQuestion.getCorrectAnswerIndex()) {
                score++;
                showToast("Oikein!");
            } else {
                wrong++;
                showToast("Väärin!");
            }
            currentQuestionIndex++;
            if (currentQuestionIndex < questionList.size()) {
                loadQuestion();
            } else {
                showResultDialog();
            }
        });

        return view;
    }

    // Helper function to reduce repetitive Toast code
    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    // Loads a question and updates the UI
    private void loadQuestion() {
        questionsHolder.removeAllViews();

        Question q = questionList.get(currentQuestionIndex);
        mainQuestion.setText(q.getQuestionText());
        progressText.setText("Kysymys " + (currentQuestionIndex + 1) + "/" + questionList.size());

        List<String> options = q.getOptions();
        for (int i = 0; i < options.size(); i++) {
            RadioButton rb = new RadioButton(getContext());
            rb.setText(options.get(i));
            rb.setTextColor(getResources().getColor(android.R.color.white));
            rb.setId(View.generateViewId());
            questionsHolder.addView(rb);
        }
    }

    // Our 10 questions
    private void initQuestions() {
        questionList = new ArrayList<>();
        questionList.add(new Question("Esimerkkikysymys 1: Mikä on Suomen pääkaupunki?",
                new ArrayList<>(List.of("Helsinki", "Tampere", "Turku", "Oulu")), 0));
        questionList.add(new Question("Esimerkkikysymys 2: Mikä on 2 + 2?",
                new ArrayList<>(List.of("3", "4", "5", "22")), 1));
        questionList.add(new Question("Esimerkkikysymys 3: Mikä planeetta on lähimpänä Aurinkoa?",
                new ArrayList<>(List.of("Mars", "Venus", "Merkurius", "Jupiter")), 2));
        questionList.add(new Question("Esimerkkikysymys 4: Mikä on maailman suurin valtameri?",
                new ArrayList<>(List.of("Atlantin valtameri", "Tyynen valtameri", "Intian valtameri", "Jäämeri")), 1));
        questionList.add(new Question("Esimerkkikysymys 5: Kuka kirjoitti 'Romeo ja Julia'?",
                new ArrayList<>(List.of("Shakespeare", "Tolstoi", "Hemingway", "Dickens")), 0));
        questionList.add(new Question("Esimerkkikysymys 6: Mikä on kemiallinen kaava vedelle?",
                new ArrayList<>(List.of("H2O", "CO2", "O2", "NaCl")), 0));
        questionList.add(new Question("Esimerkkikysymys 7: Kuka voitti jalkapallon MM-kisat 2018?",
                new ArrayList<>(List.of("Brasilia", "Saksa", "Ranska", "Italia")), 2));
        questionList.add(new Question("Esimerkkikysymys 8: Mikä on 'Elämän puu'?",
                new ArrayList<>(List.of("Rakkaus", "Viisaus", "Kasvu", "Perhe")), 0));
        questionList.add(new Question("Esimerkkikysymys 9: Mikä on maailman suurin eläin?",
                new ArrayList<>(List.of("Sinivalas", "Elefantti", "Krokotiili", "Kaarnavalas")), 0));
        questionList.add(new Question("Esimerkkikysymys 10: Mikä väri saadaan sekoittamalla punaista ja sinistä?",
                new ArrayList<>(List.of("Vihreä", "Lila", "Oranssi", "Ruskea")), 1));
    }

    // Show final results in a custom dialog — maybe add stars later?
    private void showResultDialog() {
        View resultView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_quiz_result, null);
        TextView tvResult = resultView.findViewById(R.id.tvResult);
        Button btnRetry = resultView.findViewById(R.id.btnRetry);

        tvResult.setText("Quiz loppui!\nOikein: " + score + "\nVäärin: " + wrong);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(resultView);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();

        btnRetry.setOnClickListener(v -> {
            score = 0;
            wrong = 0;
            currentQuestionIndex = 0;
            Collections.shuffle(questionList);
            loadQuestion();
            dialog.dismiss();
        });

        dialog.show();
    }
}
