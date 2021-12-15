package cs370.group9.mta_project;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_bar);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home_page);
    }


    Page_Home page_home = new Page_Home();
    Page_Bus page_bus = new Page_Bus();
    Page_Stop page_stop = new Page_Stop();
    Page_Trip page_trip = new Page_Trip();
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.home_page:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.display, page_home)
                        .commit();
                return true;
            case R.id.bus_page:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.display, page_bus)
                        .commit();
                return true;
            case R.id.stop_page:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.display, page_stop)
                        .commit();
                return true;
            case R.id.trip_page:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.display, page_trip)
                        .commit();
                return true;
        }
        return false;
    }

}