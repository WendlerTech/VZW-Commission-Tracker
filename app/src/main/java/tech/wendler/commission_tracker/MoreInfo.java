package tech.wendler.commission_tracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class MoreInfo extends Fragment {

    private static final String CURRENT_TRANSACTION = "currentTransaction";
    private static final String CURRENT_TRANSACTION_INFO = "currentTransactionInfo";

    private Transaction currentTransaction;
    private TransactionInfo currentTransactionInfo;
    private DatabaseHelper databaseHelper;
    private FirebaseAnalytics mFirebaseAnalytics;

    private EditText txtCustName, txtPhoneNum, txtOrderNum, txtSalesForce, txtExtraSalesDollars;
    private CheckBox chkRepAssist, chkDirectFill, chkISPU, chkPreOrder;
    private double totalSalesDollars;
    private String lastFragmentTag;

    public MoreInfo() {
        // Required empty public constructor
    }

    public static MoreInfo newInstance() {
        return new MoreInfo();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(Objects.requireNonNull(getContext()));
        mFirebaseAnalytics.setCurrentScreen(Objects.requireNonNull(getActivity()), "More_Info", "MoreInfo");
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
        Button btnSubmit, btnViewTransaction;

        databaseHelper = new DatabaseHelper(getActivity());
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

        //Checks to see which type of fragment the user came from
        if (getFragmentManager() != null) {
            int lastFragmentEntry = getFragmentManager().getBackStackEntryCount() - 1;
            lastFragmentTag = getFragmentManager().getBackStackEntryAt(lastFragmentEntry).getName();
        }

        btnViewTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle currentTransBundle = new Bundle();
                currentTransBundle.putSerializable("inProgressTransaction", currentTransaction);
                currentTransBundle.putSerializable("inProgressTransactionInfo", saveData());
                Fragment fragmentToOpen;

                switch (lastFragmentTag) {
                    case "NewTransaction":
                        fragmentToOpen = NewTransaction.newInstance();
                        break;
                    case "AddPriorTransaction":
                        fragmentToOpen = AddPriorTransaction.newInstance();
                        if (getArguments() != null) {
                            currentTransBundle.putLong("selectedDate", getArguments().getLong("selectedDate"));
                        }
                        break;
                    case "EditTransaction":
                        fragmentToOpen = EditTransaction.newInstance();
                        if (getArguments() != null) {
                            currentTransBundle.putLong("selectedDate", getArguments().getLong("selectedDate"));
                            currentTransBundle.putSerializable("selectedTransaction", currentTransaction);
                        }
                        break;
                    default:
                        fragmentToOpen = NewTransaction.newInstance();
                }

                fragmentToOpen.setArguments(currentTransBundle);
                FragmentTransaction fragmentTransaction;
                if (getFragmentManager() != null) {
                    fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, fragmentToOpen);
                    fragmentTransaction.commit();
                }

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //          totalSalesDollars += currentTransaction.getTotalSalesDollars();
                Calendar selectedDate = Calendar.getInstance();

                //Changes date to insert depending on parent fragment
                String dateToUse = currentTime();
                if (lastFragmentTag.equals("AddPriorTransaction")) {
                    if (getArguments() != null) {
                        long selectedDateLong = getArguments().getLong("selectedDate");
                        selectedDate.setTimeInMillis(selectedDateLong);
                        dateToUse = formatDateForQueryString(selectedDate);
                    }
                }
                if (lastFragmentTag.equals("EditTransaction")) {
                    if (getArguments() != null) {
                        long selectedDateLong = getArguments().getLong("selectedDate");
                        selectedDate.setTimeInMillis(selectedDateLong);
                    }
                    databaseHelper.updateTransaction(currentTransaction, saveData());
                    Toast.makeText(getContext(), "Transaction updated successfully.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (databaseHelper.addTransactionData(dateToUse, currentTransaction,
                            saveData())) {
                        Toast.makeText(getContext(), "Transaction successfully added.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Error while writing to the database.",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                Fragment fragmentToOpen;

                switch (lastFragmentTag) {
                    case "NewTransaction":
                        fragmentToOpen = NewTransaction.newInstance();
                        mFirebaseAnalytics.logEvent("New_Transaction_Info_Submitted", null);
                        break;
                    case "AddPriorTransaction":
                        //Opens daily totals fragment to selected date upon successful database save
                        fragmentToOpen = DailyTotals.newInstance();
                        mFirebaseAnalytics.logEvent("Prior_Transaction_Info_Added", null);
                        Bundle addPriorTransBundle = new Bundle();
                        addPriorTransBundle.putLong("selectedDate", selectedDate.getTimeInMillis());
                        fragmentToOpen.setArguments(addPriorTransBundle);
                        break;
                    case "EditTransaction":
                        //Opens daily totals fragment to selected date upon successful database edit
                        fragmentToOpen = DailyTotals.newInstance();
                        mFirebaseAnalytics.logEvent("Edit_Transaction_Info_Added", null);
                        Bundle editTransBundle = new Bundle();
                        editTransBundle.putLong("selectedDate", selectedDate.getTimeInMillis());
                        fragmentToOpen.setArguments(editTransBundle);
                        break;
                    default:
                        fragmentToOpen = NewTransaction.newInstance();
                }

                FragmentTransaction fragmentTransaction;
                if (getFragmentManager() != null) {
                    fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, fragmentToOpen);
                    fragmentTransaction.commit();
                }
            }
        });

        txtExtraSalesDollars.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtExtraSalesDollars.getText().toString().length() > 0) {
                    if (txtExtraSalesDollars.getText().toString().length() < 9) {
                        if (txtExtraSalesDollars.getText().toString().equals(".")) {
                            txtExtraSalesDollars.setText("0.");
                            txtExtraSalesDollars.setSelection(txtExtraSalesDollars.getText().length()); //Moves cursor to end
                        }
                        try {
                            totalSalesDollars = Double.parseDouble(txtExtraSalesDollars.getText().toString());
                        } catch (NumberFormatException ignored) {

                        }
                    } else {
                        txtExtraSalesDollars.setText("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (txtExtraSalesDollars.getText().toString().length() == 0) {
                    totalSalesDollars = 0;
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
        if (currentTransactionInfo.getCustomerName() != null) {
            //If a user submits a transaction edit without adding info & no info was previously
            //entered, a string with the value of "null" is entered. This ensures it isn't shown.
            if (!currentTransactionInfo.getCustomerName().equals("null")) {
                txtCustName.setText((currentTransactionInfo.getCustomerName()));
            }
        }
        if (currentTransactionInfo.getPhoneNumber() != null) {
            if (!currentTransactionInfo.getPhoneNumber().equals("null")) {
                txtPhoneNum.setText((currentTransactionInfo.getPhoneNumber()));
            }
        }
        if (currentTransactionInfo.getOrderNumber() != null) {
            if (!currentTransactionInfo.getOrderNumber().equals("null")) {
                txtOrderNum.setText((currentTransactionInfo.getOrderNumber()));
            }
        }
        if (currentTransactionInfo.getSalesForceLeads() > 0) {
            txtSalesForce.setText(String.valueOf(currentTransactionInfo.getSalesForceLeads()));
        }
        if (currentTransactionInfo.getExtraSalesDollars() > 0) {
            totalSalesDollars = currentTransactionInfo.getExtraSalesDollars();
            txtExtraSalesDollars.setText(String.valueOf(totalSalesDollars));
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

    public String currentTime() {
        //Gets current date & returns it as a string to be inserted into the database
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return simpleDateFormat.format(calendar.getTime());
    }

    private String formatDateForQueryString(Calendar date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date.getTime());
    }
}
