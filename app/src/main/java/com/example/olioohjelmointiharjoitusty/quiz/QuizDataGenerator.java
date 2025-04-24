package com.example.olioohjelmointiharjoitusty.quiz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuizDataGenerator {
    public static List<QuizQuestion> generate(String city1, String city2,
                                              int population1, int population2,
                                              double employment1, double employment2,
                                              double sufficiency1, double sufficiency2) {

        List<QuizQuestion> questions = new ArrayList<>();

        questions.add(new QuizQuestion(
                "Kummassa kaupungissa on suurempi väkiluku?",
                Arrays.asList(city1, city2),
                population1 > population2 ? 0 : 1));

        questions.add(new QuizQuestion(
                "Kummassa kaupungissa on korkeampi työllisyysaste?",
                Arrays.asList(city1, city2),
                employment1 > employment2 ? 0 : 1));

        questions.add(new QuizQuestion(
                "Kummassa kaupungissa on korkeampi työpaikkaomavaraisuus?",
                Arrays.asList(city1, city2),
                sufficiency1 > sufficiency2 ? 0 : 1));

        questions.add(new QuizQuestion(
                String.format("Paljonko on %s väkiluku?", city1),
                Arrays.asList(
                        String.valueOf(population1),
                        String.valueOf(population1 + 10000),
                        String.valueOf(population1 - 10000)),
                0));

        questions.add(new QuizQuestion(
                String.format("Paljonko on %s väkiluku?", city2),
                Arrays.asList(
                        String.valueOf(population2),
                        String.valueOf(population2 + 10000),
                        String.valueOf(population2 - 10000)),
                0));

        questions.add(new QuizQuestion(
                String.format("Mikä on %s työllisyysaste?", city1),
                Arrays.asList(
                        String.format("%.1f %%", employment1),
                        String.format("%.1f %%", employment1 + 2),
                        String.format("%.1f %%", employment1 - 2)),
                0));

        questions.add(new QuizQuestion(
                String.format("Mikä on %s työllisyysaste?", city2),
                Arrays.asList(
                        String.format("%.1f %%", employment2),
                        String.format("%.1f %%", employment2 + 2),
                        String.format("%.1f %%", employment2 - 2)),
                0));

        questions.add(new QuizQuestion(
                String.format("Mikä on %s työpaikkaomavaraisuus?", city1),
                Arrays.asList(
                        String.format("%.1f %%", sufficiency1),
                        String.format("%.1f %%", sufficiency1 + 2),
                        String.format("%.1f %%", sufficiency1 - 2)),
                0));

        questions.add(new QuizQuestion(
                String.format("Mikä on %s työpaikkaomavaraisuus?", city2),
                Arrays.asList(
                        String.format("%.1f %%", sufficiency2),
                        String.format("%.1f %%", sufficiency2 + 2),
                        String.format("%.1f %%", sufficiency2 - 2)),
                0));

        questions.add(new QuizQuestion(
                "Kumpi kaupunki oli väkiluvultaan pienempi?",
                Arrays.asList(city1, city2),
                population1 < population2 ? 0 : 1));

        return questions;
    }
}
