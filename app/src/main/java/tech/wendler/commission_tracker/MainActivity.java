package tech.wendler.commission_tracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import androidx.fragment.app.Fragment;
import com.google.firebase.analytics.FirebaseAnalytics;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        databaseHelper.getReadableDatabase();
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
                    case R.id.navigation_search:
                        selectedFragment = SearchTransactions.newInstance();
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
        sp = getSharedPreferences("FirstTimeFilePostUpdate", Context.MODE_PRIVATE);
        boolean appIsOpenedForTheFirstTime = sp.getBoolean("IsAppOpenedForFirstTime", true);

        if (appIsOpenedForTheFirstTime) {
            mFirebaseAnalytics.logEvent("App_First_Time_After_Update", null);
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
                .setMessage("Thanks for updating. \n\nYou should know that " +
                        "the app is in NO way officially endorsed by or affiliated with Verizon." +
                        "\n\nDo not enter any CPNI if you can't guarantee the protection & encryption of your device." +
                        "\n\nIf you have questions, comments, or concerns, please email me at: " +
                        "\nNick@Wendler.tech")
                .setPositiveButton("I understand", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }
}
