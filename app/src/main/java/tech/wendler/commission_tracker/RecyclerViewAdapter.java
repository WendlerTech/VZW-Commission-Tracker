package tech.wendler.commission_tracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<Transaction> listOfTransactions = new ArrayList<>();
    private Context mContext;
    private Calendar date;
    private Fragment editTransaction = null, dailyTotals = null;
    private FragmentTransaction fragmentTransaction;
    private DatabaseHelper databaseHelper;

    public RecyclerViewAdapter(Context mContext, ArrayList<Transaction> listOfTransactions,
                               Calendar selectedDate, FragmentTransaction fragmentTransaction) {
        this.listOfTransactions = listOfTransactions;
        this.mContext = mContext;
        this.date = selectedDate;
        this.fragmentTransaction = fragmentTransaction;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        int numberOfDevices = listOfTransactions.get(position).getTotalDevices();
        double salesDollars = listOfTransactions.get(position).getTotalSalesDollars();

        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
        final String dollars = format.format(salesDollars);

        holder.lblMonthHeader.setText(formatDateGetMonth(date));
        holder.lblDayHeader.setText(formatDateGetDay(date));
        holder.lblDevicesCount.setText("" + numberOfDevices);
        holder.lblSalesDollarCount.setText(dollars);

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Transaction selectedTransaction = listOfTransactions.get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("selectedTransaction", selectedTransaction);
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
                        dailyTotals = DailyTotals.newInstance();
                        fragmentTransaction.replace(R.id.fragment_container, dailyTotals);
                        fragmentTransaction.commit();
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
        //Displays toast message if recycler view is empty
        if (listOfTransactions.size() == 0) {
            Toast.makeText(mContext, "There are no transactions to view." +
                    "\nPlease click a tab below to return.", Toast.LENGTH_SHORT).show();
        }
        return listOfTransactions.size();
    }

    //Holds widgets (each individual item in recycler view) in memory
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView lblMonthHeader, lblDayHeader, lblDevicesCount, lblSalesDollarCount;
        ConstraintLayout parentLayout;

        public ViewHolder(View itemView) {
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM");
        return simpleDateFormat.format(calendar.getTime());
    }

    private String formatDateGetDay(Calendar calendar) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
        return simpleDateFormat.format(calendar.getTime());
    }
}
