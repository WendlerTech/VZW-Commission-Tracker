package tech.wendler.commission_tracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditDailyTotals extends Fragment {

    private EditText txtNewPhones, txtUpgPhones, txtTablets, txtAccessories,
    txtHum, txtCD, txtTMP, txtMultiTMP;
    private TextView lblEditSalesDollars, lblSelectedDate;
    private Button btnSaveEdits;
    private int editedNewPhones, editedUpgPhones, editedTablets, editedHum,
            editedCD, editedTMP, editedMultiTMP;

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

  /*      txtNewPhones = getView().findViewById(R.id.txtEditNewPhones);
        txtUpgPhones = getView().findViewById(R.id.txtEditUpgPhones);
        txtTablets = getView().findViewById(R.id.txtEditTablets);
        txtAccessories = getView().findViewById(R.id.txtEditAccRev);
        txtHum = getView().findViewById(R.id.txtEditHum);
        txtCD = getView().findViewById(R.id.txtEditConnected);
        txtTMP = getView().findViewById(R.id.txtEditTMP);
        txtMultiTMP = getView().findViewById(R.id.txtEditMultiTMP);

        lblEditSalesDollars = getView().findViewById(R.id.lblEditSalesDollars);
        lblSelectedDate = getView().findViewById(R.id.lblSelectedDate);

        btnSaveEdits = getView().findViewById(R.id.btnSaveEdits);*/
    }

}
