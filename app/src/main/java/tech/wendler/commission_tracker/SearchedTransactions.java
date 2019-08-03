package tech.wendler.commission_tracker;

import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchedTransactions extends Fragment {

    private ArrayList<Transaction> searchedTransactionList;

    public SearchedTransactions() {

    }

    public static SearchedTransactions newInstance() {
        return new SearchedTransactions();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_searched_transactions, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
        searchedTransactionList = new ArrayList<>();

        Bundle queryBundle = this.getArguments();
        String queryString = "";
        String searchTerm = "";

        if (queryBundle != null) {
            queryString = queryBundle.getString("queryStringToSearch");
            searchTerm = queryBundle.getString("searchTerm");
        }

        if (!TextUtils.isEmpty(queryString)) {
            String[] searchArguments;
            if (!TextUtils.isEmpty(searchTerm)) {
                 searchArguments = new String[] {searchTerm};
            } else {
                searchArguments = new String[0];
            }

            try (Cursor cursor = databaseHelper.searchWithParameters(queryString, searchArguments)) {
                int newPhones, upgPhones, tablets, connected, hum, singleTMP, transactionID;
                double revenue, totalSalesBucket;
                boolean newMultiTMP;
                String transactionDate;

                while (cursor.moveToNext()) {
                    transactionID = cursor.getInt(cursor.getColumnIndex("transID"));
                    newPhones = cursor.getInt(cursor.getColumnIndex("new_phones"));
                    upgPhones = cursor.getInt(cursor.getColumnIndex("upgrade_phones"));
                    tablets = cursor.getInt(cursor.getColumnIndex("tablets_etc"));
                    connected = cursor.getInt(cursor.getColumnIndex("connected_devices_etc"));
                    hum = cursor.getInt(cursor.getColumnIndex("hum"));
                    singleTMP = cursor.getInt(cursor.getColumnIndex("new_tmp"));
                    revenue = cursor.getDouble(cursor.getColumnIndex("revenue"));
                    //There is no "getBoolean" function, the boolean column only includes a 0 or 1.
                    newMultiTMP = cursor.getInt(cursor.getColumnIndex("new_multi_tmp")) == 1;
                    transactionDate = cursor.getString(cursor.getColumnIndex("date"));
                    totalSalesBucket = cursor.getDouble(cursor.getColumnIndex("sales_bucket"));

                    Transaction searchedTransaction = new Transaction(transactionID, newPhones, upgPhones,
                            tablets, connected, hum, singleTMP, revenue, newMultiTMP);
                    searchedTransaction.setTransactionDate(transactionDate);
                    searchedTransaction.setTotalSalesDollars(totalSalesBucket);

                    searchedTransactionList.add(searchedTransaction);
                }
            }
            initRecyclerView();
        }
    }

    //Opens recycler view
    private void initRecyclerView() {
        if (getFragmentManager() != null) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            RecyclerView recyclerView = getView().findViewById(R.id.searchRecyclerView);
            SearchedRecyclerViewAdapter adapter = new SearchedRecyclerViewAdapter(getContext(),
                    searchedTransactionList, fragmentTransaction);
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
    }
}
