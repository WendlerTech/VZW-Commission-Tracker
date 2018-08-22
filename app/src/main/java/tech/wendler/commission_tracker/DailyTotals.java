package tech.wendler.commission_tracker;

import android.app.DatePickerDialog;
import android.support.v4.app.FragmentTransaction;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DailyTotals extends Fragment {

    private TextView txtDisplayDate;
    private TextView lblNewPhones, lblUpgPhones, lblTablets, lblAccessoryRev, lblHum,
    lblCD, lblNewTMP, lblMultiTMP, lblSalesDollars;
    private Button btnEditTotals;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private DatabaseHelper databaseHelper;

    private float newPhoneTotal = 0, upgPhoneTotal = 0, tabletTotal = 0, humTotal = 0,
    connectedDeviceTotal = 0, newTMPTotal = 0, revenueTotal = 0, salesBucketTotal = 0;
    private int multiTMPTotal = 0;

    private String selectedDateString = "";
    private Calendar selectedDate = Calendar.getInstance();
    private Fragment editTotalsFragment = null;

    public DailyTotals() {
        // Required empty public constructor
    }

    public static DailyTotals newInstance() {
        DailyTotals fragment = new DailyTotals();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_daily_totals, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);

        databaseHelper = new DatabaseHelper(getActivity());
        txtDisplayDate = getView().findViewById(R.id.lblSelectedDate);

        btnEditTotals = getView().findViewById(R.id.btnEditDailyTotals);

        lblNewPhones = getView().findViewById(R.id.lblNewPhones);
        lblUpgPhones = getView().findViewById(R.id.lblUpgPhones);
        lblTablets = getView().findViewById(R.id.lblTablets);
        lblAccessoryRev = getView().findViewById(R.id.lblAccessoryRev);
        lblHum = getView().findViewById(R.id.lblHum);
        lblCD = getView().findViewById(R.id.lblCD);
        lblNewTMP = getView().findViewById(R.id.lblNewTMP);
        lblMultiTMP = getView().findViewById(R.id.lblMultiTMP);
        lblSalesDollars = getView().findViewById(R.id.lblSalesDollars);

        //Displays current date on load
        Calendar calendar = Calendar.getInstance();
        updateDisplayDate(formatDate(calendar));
        populateData(calendar);

        txtDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Displays calendar dialog box
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(getActivity(),
                        R.style.DialogTheme,
                        dateSetListener, year, month, day);

                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);

                //Displays selected date
                updateDisplayDate(formatDate(calendar));
                populateData(calendar);
            }
        };

        btnEditTotals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Passes the selected date by converting to the calendar instance into a long
                Bundle bundle = new Bundle();
                bundle.putLong("selectedDate", selectedDate.getTimeInMillis());
                //Creates new fragment instance
                editTotalsFragment = EditDailyTotals.newInstance();
                editTotalsFragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, editTotalsFragment);
                fragmentTransaction.addToBackStack(null).commit();
            }
        });
    }

    //Takes in calendar instance & returns a string formatted as such: Mon, November 23, '15
    private String formatDate(Calendar date) {
        SimpleDateFormat format = new SimpleDateFormat("EEE, MMMM dd, ''yy");
        return format.format(date.getTime());
    }

    //Formats user selected date into "2018-11-23%" - this effectively returns all entries
    //from a single day, regardless of timestamp.
    private String formatDateForQueryString(Calendar date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date.getTime()) + "%";
    }

    //Sets label to the user-selected date
    private void updateDisplayDate(String date) {
        txtDisplayDate.setText(date);
    }

    private void populateData(Calendar date) {
        clearOldData();

        selectedDateString = formatDateForQueryString(date);
        selectedDate = date;

        //Total() sums all non-null values in a column & returns a float
        String queryString = "SELECT total(new_phones) AS ColNewPhones, " +
                "total(upgrade_phones) AS ColUpgPhones, " +
                "total(tablets_etc) AS ColTablets, " +
                "total(hum) AS ColHum, " +
                "total(connected_devices_etc) AS ColCD, " +
                "total(new_tmp) AS ColNewTMP, " +
                "total(revenue) AS ColRevenue, " +
                "total(sales_bucket) AS ColSalesBucket " +
                "FROM Transactions WHERE date LIKE '" + selectedDateString + "';";

        Cursor cursor = databaseHelper.getData(queryString);

        try {
            while (cursor.moveToNext()) {
                //Gets values from each column & assigns them to respective variables
                newPhoneTotal = cursor.getFloat(cursor.getColumnIndex("ColNewPhones"));
                upgPhoneTotal = cursor.getFloat(cursor.getColumnIndex("ColUpgPhones"));
                tabletTotal = cursor.getFloat(cursor.getColumnIndex("ColTablets"));
                humTotal = cursor.getFloat(cursor.getColumnIndex("ColHum"));
                connectedDeviceTotal = cursor.getFloat(cursor.getColumnIndex("ColCD"));
                newTMPTotal = cursor.getFloat(cursor.getColumnIndex("ColNewTMP"));
                revenueTotal = cursor.getFloat(cursor.getColumnIndex("ColRevenue"));
                salesBucketTotal = cursor.getFloat(cursor.getColumnIndex("ColSalesBucket"));
            }
        } finally {
            cursor.close();
        }

        //Returns the sum of all true values in the new multi TMP column
        queryString = "SELECT COUNT(new_multi_tmp) AS newMultiTMP FROM Transactions WHERE new_multi_tmp = 1 " +
                "AND date LIKE '" + selectedDateString + "';";
        cursor = databaseHelper.getData(queryString);

        try {
            while (cursor.moveToNext()) {
                multiTMPTotal = cursor.getInt(cursor.getColumnIndex("newMultiTMP"));
            }
        } finally {
            cursor.close();
        }

        //Formats sales dollars entry into US currency
        Locale locale = Locale.US;
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        String formattedSalesDollars = fmt.format(salesBucketTotal);

        //Updates labels with newly queried data
        //Converts whole numbers into integers via cast
        lblNewPhones.setText("" + (int) newPhoneTotal);
        lblUpgPhones.setText("" + (int) upgPhoneTotal);
        lblTablets.setText("" + (int) tabletTotal);
        lblAccessoryRev.setText("" +  revenueTotal);
        lblHum.setText("" + (int) humTotal);
        lblCD.setText("" + (int) connectedDeviceTotal);
        lblNewTMP.setText("" + (int) newTMPTotal);
        lblSalesDollars.setText(formattedSalesDollars);
        lblMultiTMP.setText("" + multiTMPTotal);
    }

    private void clearOldData() {
        //Resets all labels back to 0
        lblNewPhones.setText("0");
        lblUpgPhones.setText("0");
        lblTablets.setText("0");
        lblAccessoryRev.setText("0");
        lblHum.setText("0");
        lblCD.setText("0");
        lblNewTMP.setText("0");
        lblMultiTMP.setText("0");
        lblSalesDollars.setText("$0");
    }
}
