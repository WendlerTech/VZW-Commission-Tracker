package tech.wendler.commission_tracker;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

    private ArrayList<Transaction> transactionList;
    private Calendar calendar = Calendar.getInstance();
    private DatabaseHelper databaseHelper;

    public EditDailyTotals() {

    }

    public static EditDailyTotals newInstance() {
        return new EditDailyTotals();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_daily_totals, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);

        databaseHelper = new DatabaseHelper(getActivity());

        //Gets long value passed from DailyTotals
        Bundle selectedDate = this.getArguments();
        long selectedDateLong;
        if (selectedDate != null) {
            selectedDateLong = selectedDate.getLong("selectedDate");
            //Converts long into calendar containing the selected date
            calendar.setTimeInMillis(selectedDateLong);
        }
        transactionList = populateTransactionList();
        initRecyclerView();
    }

    private ArrayList<Transaction> populateTransactionList() {
        int newPhones, upgPhones, tablets, connected, hum, singleTMP, transactionID;
        double revenue;
        boolean newMultiTMP;

        transactionList = new ArrayList<>();
        String selectedDateString = formatDateForQueryString(calendar);

        String queryString = "SELECT transID AS colTransID, new_phones AS colNewPhones, " +
                "upgrade_phones AS colUpgPhones, tablets_etc AS colTablets, hum AS colHum, " +
                "connected_devices_etc AS colCD, new_tmp AS colTMP, " +
                "new_multi_tmp AS colMultiTMP, revenue AS colRev, sales_bucket AS colSalesBucket " +
                "FROM Transactions WHERE date LIKE '" + selectedDateString + "';";

        try (Cursor cursor = databaseHelper.getData(queryString)) {
            while (cursor.moveToNext()) {
                transactionID = cursor.getInt(cursor.getColumnIndex("colTransID"));
                newPhones = cursor.getInt(cursor.getColumnIndex("colNewPhones"));
                upgPhones = cursor.getInt(cursor.getColumnIndex("colUpgPhones"));
                tablets = cursor.getInt(cursor.getColumnIndex("colTablets"));
                connected = cursor.getInt(cursor.getColumnIndex("colCD"));
                hum = cursor.getInt(cursor.getColumnIndex("colHum"));
                singleTMP = cursor.getInt(cursor.getColumnIndex("colTMP"));
                revenue = cursor.getDouble(cursor.getColumnIndex("colRev"));
                //There is no "getBoolean" function, the boolean column only includes a 0 or 1.
                newMultiTMP = cursor.getInt(cursor.getColumnIndex("colMultiTMP")) == 1;

                Transaction newTransaction = new Transaction(transactionID, newPhones, upgPhones,
                        tablets, connected, hum, singleTMP, revenue, newMultiTMP);

                transactionList.add(newTransaction);
            }
        }

        return transactionList;
    }

    //Opens recycler view
    public void initRecyclerView() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        RecyclerView recyclerView = getView().findViewById(R.id.recyclerView);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(getContext(), transactionList, calendar, fragmentTransaction);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (adapter.getItemCount() == 0) {
            //Displays toast message if recycler view is empty
            Toast.makeText(getContext(), "There are no transactions to view." +
                    "\nPlease click a tab below to return.", Toast.LENGTH_LONG).show();

        } else {
            Toast toast = Toast.makeText(getContext(), "Click to view/edit, " +
                    "long press to delete.", Toast.LENGTH_SHORT);
            toast.show();

        }
    }

    //Formats user selected date into "2018-11-23%" - this effectively returns all entries
    //from a single day, regardless of timestamp.
    private String formatDateForQueryString(Calendar date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date.getTime()) + "%";
    }
}
