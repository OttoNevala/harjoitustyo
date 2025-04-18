package com.example.olioohjelmointiharjoitusty.ShowData;

public class PopulationData {
    private int year;
    private int population;
    private double percentChange;

    public PopulationData(int year, int population, double percentChange) {
        this.year = year;
        this.population = population;
        this.percentChange = percentChange;
    }

    public int getYear() {
        return year;
    }
    public int getPopulation() {
        return population;
    }
    public double getPercentChange() {
        return percentChange;
    }


}
