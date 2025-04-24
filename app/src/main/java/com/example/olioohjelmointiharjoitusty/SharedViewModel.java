package com.example.olioohjelmointiharjoitusty;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> cityName = new MutableLiveData<>();
    private final MutableLiveData<String> firstCity = new MutableLiveData<>();
    private final MutableLiveData<String> secondCity = new MutableLiveData<>();

    private final MutableLiveData<Integer> population1 = new MutableLiveData<>();
    private final MutableLiveData<Integer> population2 = new MutableLiveData<>();
    private final MutableLiveData<Double> employment1 = new MutableLiveData<>();
    private final MutableLiveData<Double> employment2 = new MutableLiveData<>();
    private final MutableLiveData<Double> sufficiency1 = new MutableLiveData<>();
    private final MutableLiveData<Double> sufficiency2 = new MutableLiveData<>();

    // Nimet
    public void setCityName(String city) { cityName.setValue(city); }
    public LiveData<String> getCityName() { return cityName; }

    public void setFirstCity(String city) { firstCity.setValue(city); }
    public LiveData<String> getFirstCity() { return firstCity; }

    public void setSecondCity(String city) { secondCity.setValue(city); }
    public LiveData<String> getSecondCity() { return secondCity; }

    public void resetCities() {
        firstCity.setValue(null);
        secondCity.setValue(null);
    }

    // Väkiluvut
    public void setPopulation1(Integer population) { population1.setValue(population); }
    public void setPopulation2(Integer population) { population2.setValue(population); }

    // Työllisyysasteet
    public void setEmployment1(Double rate) { employment1.setValue(rate); }
    public void setEmployment2(Double rate) { employment2.setValue(rate); }

    // Työpaikkaomavaraisuudet
    public void setSufficiency1(Double rate) { sufficiency1.setValue(rate); }
    public void setSufficiency2(Double rate) { sufficiency2.setValue(rate); }

    public LiveData<Integer> getPopulation1()  { return population1; }
    public LiveData<Integer> getPopulation2()  { return population2; }

    public LiveData<Double>  getEmployment1()  { return employment1; }
    public LiveData<Double>  getEmployment2()  { return employment2; }

    public LiveData<Double>  getSufficiency1() { return sufficiency1; }
    public LiveData<Double>  getSufficiency2() { return sufficiency2; }
}
