package tech.wendler.commission_tracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SearchedRecyclerViewAdapter extends RecyclerView.Adapter<SearchedRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Transaction> listOfTransactions;
    private Context mContext;
    private Fragment editTransaction = null;
    private FragmentTransaction fragmentTransaction;
    private DatabaseHelper databaseHelper;

    SearchedRecyclerViewAdapter(Context mContext, ArrayList<Transaction> listOfTransactions,
                        FragmentTransaction fragmentTransaction) {
        this.listOfTransactions = listOfTransactions;
        this.mContext = mContext;
        this.fragmentTransaction = fragmentTransaction;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        int numberOfDevices = listOfTransactions.get(position).getTotalDevices();
        double salesDollars = listOfTransactions.get(position).getTotalSalesDollars();

        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
        final String dollars = format.format(salesDollars);

        Calendar transactionDate = getDateFromString(listOfTransactions.get(position).getTransactionDate());

        holder.lblMonthHeader.setText(formatDateGetMonth(transactionDate));
        holder.lblDayHeader.setText(formatDateGetDay(transactionDate));
        holder.lblDevicesCount.setText(String.valueOf(numberOfDevices));
        holder.lblSalesDollarCount.setText(dollars);

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Transaction selectedTransaction = listOfTransactions.get(position);
                Calendar selectedTransactionDate = getDateFromString(listOfTransactions.get(position).getTransactionDate());
                Bundle bundle = new Bundle();
                bundle.putSerializable("selectedTransaction", selectedTransaction);
                bundle.putLong("selectedDate", selectedTransactionDate.getTimeInMillis());
                editTransaction = EditTransaction.newInstance();
                editTransaction.setArguments(bundle);
                fragmentTransaction.replace(R.id.fragment_container, editTransaction);
                fragmentTransaction.commit();
            }
        });

        holder.parentLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext, R.style.DialogTheme);
                dialog.setTitle("Delete Transaction");
                dialog.setMessage("Warning!" + "\n\nAre you sure you want to delete this " +
                        "transaction? This action cannot be undone!");
                dialog.setPositiveButton("Delete Transaction", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int transToBeDeleted = listOfTransactions.get(position).getTransactionID();
                        databaseHelper.deleteTransaction(transToBeDeleted);
                        Toast.makeText(mContext, "Transaction deleted", Toast.LENGTH_SHORT).show();
                        listOfTransactions.remove(position);
                        if (listOfTransactions.size() > 0) {
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, listOfTransactions.size());
                        } else {
                            Fragment searchFragment = SearchTransactions.newInstance();
                            fragmentTransaction.replace(R.id.fragment_container, searchFragment);
                            fragmentTransaction.commit();
                        }
                    }
                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //User cancelled, nothing happens
                            }
                        });

                final AlertDialog alert = dialog.create();
                alert.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return listOfTransactions.size();
    }

    //Holds widgets (each individual item in recycler view) in memory
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView lblMonthHeader, lblDayHeader, lblDevicesCount, lblSalesDollarCount;
        ConstraintLayout parentLayout;

        ViewHolder(View itemView) {
            super(itemView);

            lblMonthHeader = itemView.findViewById(R.id.lblMonthHeader);
            lblDayHeader = itemView.findViewById(R.id.lblDayNumHeader);
            lblDevicesCount = itemView.findViewById(R.id.lblDevicesCount);
            lblSalesDollarCount = itemView.findViewById(R.id.lblSalesDollarCount);
            parentLayout = itemView.findViewById(R.id.listConstraintLayout);

            databaseHelper = new DatabaseHelper(mContext);
        }
    }

    private String formatDateGetMonth(Calendar calendar) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM", Locale.US);
        return simpleDateFormat.format(calendar.getTime());
    }

    private String formatDateGetDay(Calendar calendar) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd", Locale.US);
        return simpleDateFormat.format(calendar.getTime());
    }

    private Calendar getDateFromString(String date) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date convertedDate = new Date();
        try {
            convertedDate = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(convertedDate);
        return calendar;
    }
}
