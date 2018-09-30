package tech.wendler.commission_tracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.support.v4.app.Fragment;
import com.google.firebase.analytics.FirebaseAnalytics;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        selectedFragment = NewTransaction.newInstance();
                        break;
                    case R.id.navigation_totals:
                        selectedFragment = DailyTotals.newInstance();
                        break;
                    case R.id.navigation_monthly_totals:
                        selectedFragment = MonthlyTotals.newInstance();
                        break;
                }
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, selectedFragment);
                transaction.commit();
                return true;
            }
        });

        //Sets middle nav button as default
        navigation.setSelectedItemId(R.id.navigation_home);

        //Checks if it's users first time opening app
        sp = getSharedPreferences("FirstTimeFile", Context.MODE_PRIVATE);
        boolean appIsOpenedForTheFirstTime = sp.getBoolean("IsAppOpenedForFirstTime", true);

        if (appIsOpenedForTheFirstTime) {
            firstTimeUserDialog();
        }
    }

    @Override
    public void onBackPressed() {
        //Do nothing
    }

    //If it's the user's first time opening the app, a dialog is displayed
    public void firstTimeUserDialog() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("IsAppOpenedForFirstTime", false);
        editor.commit();

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        builder.setTitle("Welcome to the app!")
                .setMessage("Thanks for downloading. \n\nYou should know that " +
                        "the app is in NO way officially endorsed by or affiliated with Verizon." +
                        "\n\nSince you add your own numbers, accuracy can NOT be guaranteed." +
                        "\n\nIf you have questions, comments, or concerns, please email me at: " +
                        "Nick@Wendler.tech")
                .setPositiveButton("I understand", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }
}
