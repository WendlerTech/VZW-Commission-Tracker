package tech.wendler.commission_tracker;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class EditDailyTotals extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private ArrayList<Transaction> transactionList;
    private Calendar calendar = Calendar.getInstance();
    private String selectedDateString;
    private DatabaseHelper databaseHelper;

    public EditDailyTotals() {

    }

    public static EditDailyTotals newInstance() {
        EditDailyTotals fragment = new EditDailyTotals();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_daily_totals, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);

        databaseHelper = new DatabaseHelper(getActivity());

        //Gets long value passed from DailyTotals
        Bundle selectedDate = this.getArguments();
        Long selectedDateLong = selectedDate.getLong("selectedDate");
        //Converts long into calendar containing the selected date
        calendar.setTimeInMillis(selectedDateLong);
        transactionList = populateTransactionList();
        initRecyclerView();
    }

    private ArrayList<Transaction> populateTransactionList() {
        int newPhones = 0, upgPhones = 0, tablets = 0, connected = 0,
                hum = 0, singleTMP = 0, transactionID = 0;
        double salesDollars = 0, revenue = 0;
        boolean newMultiTMP = false;

        transactionList = new ArrayList<>();
        selectedDateString = formatDateForQueryString(calendar);

        String queryString = "SELECT transID AS colTransID, new_phones AS colNewPhones, " +
                "upgrade_phones AS colUpgPhones, tablets_etc AS colTablets, hum AS colHum, " +
                "connected_devices_etc AS colCD, new_tmp AS colTMP, " +
                "new_multi_tmp AS colMultiTMP, revenue AS colRev, sales_bucket AS colSalesBucket " +
                "FROM Transactions WHERE date LIKE '" + selectedDateString + "';";

        Cursor cursor = databaseHelper.getData(queryString);

        try {
            while (cursor.moveToNext()) {
                transactionID = cursor.getInt(cursor.getColumnIndex("colTransID"));
                newPhones = cursor.getInt(cursor.getColumnIndex("colNewPhones"));
                upgPhones = cursor.getInt(cursor.getColumnIndex("colUpgPhones"));
                tablets = cursor.getInt(cursor.getColumnIndex("colTablets"));
                connected = cursor.getInt(cursor.getColumnIndex("colCD"));
                hum = cursor.getInt(cursor.getColumnIndex("colHum"));
                singleTMP = cursor.getInt(cursor.getColumnIndex("colTMP"));
                salesDollars = cursor.getDouble(cursor.getColumnIndex("colSalesBucket"));
                revenue = cursor.getDouble(cursor.getColumnIndex("colRev"));
                //There is no "getBoolean" function, the boolean column only includes a 0 or 1.
                if (cursor.getInt(cursor.getColumnIndex("colMultiTMP")) == 1) {
                    newMultiTMP = true;
                } else {
                    newMultiTMP = false;
                }

                Transaction newTransaction = new Transaction(transactionID, newPhones, upgPhones,
                        tablets, connected, hum, singleTMP, revenue, newMultiTMP);

                transactionList.add(newTransaction);
            }
        } finally {
            cursor.close();
        }

        return transactionList;
    }

    //Opens recycler view
    public void initRecyclerView() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        recyclerView = getView().findViewById(R.id.recyclerView);
        adapter = new RecyclerViewAdapter(getContext(), transactionList, calendar, fragmentTransaction);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Toast toast = Toast.makeText(getContext(), "Click to view/edit, " +
                "long press to delete.", Toast.LENGTH_LONG);
        toast.show();
    }

    //Formats user selected date into "2018-11-23%" - this effectively returns all entries
    //from a single day, regardless of timestamp.
    private String formatDateForQueryString(Calendar date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date.getTime()) + "%";
    }
}
