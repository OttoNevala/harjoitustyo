package com.example.olioohjelmointiharjoitusty;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private TextView lastSearch;
    private Button searchCity;
    private EditText cityNameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Aseta reunat reunojen yli näkyviksi ja säädä padding järjestelmän tilan mukaan
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lastSearch = findViewById(R.id.lastSearch);
        searchCity = findViewById(R.id.searchCity);
        cityNameInput = findViewById(R.id.cityNameInput);

        searchCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityNameInput.getText().toString().trim();

                if (city.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Syötä kunnan nimi", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, Information.class);
                    intent.putExtra("cityName", city);
                    startActivity(intent);
                }
            }
        });
    }
}
