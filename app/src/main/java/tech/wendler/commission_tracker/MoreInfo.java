package tech.wendler.commission_tracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class MoreInfo extends Fragment {

    private static final String CURRENT_TRANSACTION = "currentTransaction";
    private static final String CURRENT_TRANSACTION_INFO = "currentTransactionInfo";

    private Transaction currentTransaction;
    private TransactionInfo currentTransactionInfo;

    private TextView txtCustName, txtPhoneNum, txtOrderNum, txtSalesForce, txtExtraSalesDollars;
    private CheckBox chkRepAssist, chkDirectFill, chkISPU, chkPreOrder;
    private Button btnSubmit, btnViewTransaction;

    public MoreInfo() {
        // Required empty public constructor
    }

    public static MoreInfo newInstance() {
        return new MoreInfo();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_more_info, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        txtCustName = getView().findViewById(R.id.txtInfoCustName);
        txtPhoneNum = getView().findViewById(R.id.txtInfoPhoneNum);
        txtOrderNum = getView().findViewById(R.id.txtInfoOrderNum);
        txtSalesForce = getView().findViewById(R.id.txtInfoSalesForce);
        txtExtraSalesDollars = getView().findViewById(R.id.txtInfoSalesDollars);
        chkRepAssist = getView().findViewById(R.id.chkInfoRepAssisted);
        chkDirectFill = getView().findViewById(R.id.chkInfoDfill);
        chkISPU = getView().findViewById(R.id.chkInfoISPU);
        chkPreOrder = getView().findViewById(R.id.chkInfoPreOrder);
        btnSubmit = getView().findViewById(R.id.btnInfoSubmit);
        btnViewTransaction = getView().findViewById(R.id.btnInfoViewTransaction);

        txtPhoneNum.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        btnViewTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle currentTransBundle = new Bundle();
                currentTransBundle.putSerializable("inProgressTransaction", currentTransaction);
                currentTransBundle.putSerializable("inProgressTransactionInfo", saveData());

                Fragment newTransFragment = NewTransaction.newInstance();
                newTransFragment.setArguments(currentTransBundle);
                FragmentTransaction fragmentTransaction;
                if (getFragmentManager() != null) {
                    fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, newTransFragment);
                    fragmentTransaction.commit();
                }
            }
        });

        if (getArguments() != null) {
            currentTransaction = (Transaction) getArguments().getSerializable(CURRENT_TRANSACTION);
            currentTransactionInfo = (TransactionInfo) getArguments().getSerializable(CURRENT_TRANSACTION_INFO);

            if (currentTransactionInfo != null) {
                populateData();
            }
        }
    }

    private void populateData() {
        txtCustName.setText(String.valueOf(currentTransactionInfo.getCustomerName()));
        txtPhoneNum.setText(String.valueOf(currentTransactionInfo.getPhoneNumber()));
        txtOrderNum.setText(String.valueOf(currentTransactionInfo.getOrderNumber()));

        if (currentTransactionInfo.getSalesForceLeads() > 0) {
            txtSalesForce.setText(String.valueOf(currentTransactionInfo.getSalesForceLeads()));
        }
        if (currentTransactionInfo.getExtraSalesDollars() > 0) {
            txtExtraSalesDollars.setText(String.valueOf(currentTransactionInfo.getExtraSalesDollars()));
        }

        chkRepAssist.setChecked(currentTransactionInfo.isRepAssistedOrder());
        chkDirectFill.setChecked(currentTransactionInfo.isdFillOrder());
        chkISPU.setChecked(currentTransactionInfo.isInStorePickupOrder());
        chkPreOrder.setChecked(currentTransactionInfo.isPreOrder());
    }

    private TransactionInfo saveData() {
        currentTransactionInfo = new TransactionInfo();
        currentTransactionInfo.setCustomerName(txtCustName.getText().toString());
        currentTransactionInfo.setPhoneNumber(txtPhoneNum.getText().toString());
        currentTransactionInfo.setOrderNumber(txtOrderNum.getText().toString());
        currentTransactionInfo.setRepAssistedOrder(chkRepAssist.isChecked());
        currentTransactionInfo.setdFillOrder(chkDirectFill.isChecked());
        currentTransactionInfo.setInStorePickupOrder(chkISPU.isChecked());
        currentTransactionInfo.setPreOrder(chkPreOrder.isChecked());

        if (txtSalesForce.getText().length() > 0) {
            currentTransactionInfo.setSalesForceLeads(Integer.parseInt(txtSalesForce.getText().toString()));
        }

        if (txtExtraSalesDollars.getText().length() > 0) {
            currentTransactionInfo.setExtraSalesDollars(Double.parseDouble(txtExtraSalesDollars.getText().toString()));
        }

        return currentTransactionInfo;
    }
}
