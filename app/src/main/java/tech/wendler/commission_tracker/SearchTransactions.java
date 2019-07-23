package tech.wendler.commission_tracker;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SearchTransactions extends Fragment {

    private EditText txtCustName, txtPhoneNum, txtOrderNum;
    private CheckBox chkRepAssisted, chkDfill, chkIspu, chkPreOrder;
    private TextView lblStartDate, lblEndDate;
    private Button btnStartDate, btnEndDate, btnSearch, btnClear;
    private RadioGroup radioGroup;

    private Date startDate, endDate;
    private Calendar calendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener startDateSetListener, endDateSetListener;

    private String custName, phoneNum, orderNum;
    private boolean repAssisted, dFill, ispu, preOrder;

    public static SearchTransactions newInstance() {
        return new SearchTransactions();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        txtCustName = getView().findViewById(R.id.txtSearchCustName);
        txtPhoneNum = getView().findViewById(R.id.txtSearchPhoneNum);
        txtOrderNum = getView().findViewById(R.id.txtSearchOrderNum);
        chkRepAssisted = getView().findViewById(R.id.chkSearchRepAssisted);
        chkDfill = getView().findViewById(R.id.chkSearchDfill);
        chkIspu = getView().findViewById(R.id.chkSearchIspu);
        chkPreOrder = getView().findViewById(R.id.chkSearchPreOrder);
        lblStartDate = getView().findViewById(R.id.lblSearchStartDate);
        lblEndDate = getView().findViewById(R.id.lblSearchEndDate);
        btnStartDate = getView().findViewById(R.id.btnSearchStartDate);
        btnEndDate = getView().findViewById(R.id.btnSearchEndDate);
        btnSearch = getView().findViewById(R.id.btnSearchTransactions);
        btnClear = getView().findViewById(R.id.btnSearchClear);
        radioGroup = getView().findViewById(R.id.radGrpSearch);

        txtPhoneNum.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFields();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.radSearchLifetime) {
                    lblStartDate.setVisibility(View.INVISIBLE);
                    lblEndDate.setVisibility(View.INVISIBLE);
                    btnStartDate.setEnabled(false);
                    btnEndDate.setEnabled(false);
                } else {
                    lblStartDate.setVisibility(View.VISIBLE);
                    lblEndDate.setVisibility(View.VISIBLE);
                    btnStartDate.setEnabled(true);
                    btnEndDate.setEnabled(true);
                }
            }
        });

        btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar startCal = Calendar.getInstance();
                startCal.setTime(startDate);

                int year = startCal.get(Calendar.YEAR);
                int month = startCal.get(Calendar.MONTH);
                int day = startCal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog startDialog = new DatePickerDialog(getActivity(),
                        R.style.DialogTheme, startDateSetListener, year, month, day);

                startDialog.show();
            }
        });

        btnEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar endCal = Calendar.getInstance();
                endCal.setTime(endDate);

                int year = endCal.get(Calendar.YEAR);
                int month = endCal.get(Calendar.MONTH);
                int day = endCal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog endDialog = new DatePickerDialog(getActivity(),
                        R.style.DialogTheme, endDateSetListener, year, month, day);

                endDialog.show();
            }
        });

        startDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Calendar selectedStartDate = Calendar.getInstance();
                selectedStartDate.set(year, month, dayOfMonth);

                lblStartDate.setText(formatDateForLabel(selectedStartDate));
                startDate = selectedStartDate.getTime();
            }
        };

        endDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Calendar selectedEndDate = Calendar.getInstance();
                selectedEndDate.set(year, month, dayOfMonth);

                lblEndDate.setText(formatDateForLabel(selectedEndDate));
                endDate = selectedEndDate.getTime();
            }
        };

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkDateValidity(startDate, endDate)) {
                    if (txtCustName.getText().length() > 0) {

                    }
                }
            }
        });

        initialLoad();
    }

    private void clearFields() {
        txtCustName.setText("");
        txtPhoneNum.setText("");
        txtOrderNum.setText("");
        chkRepAssisted.setChecked(false);
        chkDfill.setChecked(false);
        chkIspu.setChecked(false);
        chkPreOrder.setChecked(false);
        radioGroup.check(R.id.radSearchLifetime);
        initialLoad();
    }

    private void initialLoad() {
        //Hides date range labels
        lblStartDate.setVisibility(View.INVISIBLE);
        lblEndDate.setVisibility(View.INVISIBLE);
        btnStartDate.setEnabled(false);
        btnEndDate.setEnabled(false);

        Calendar startCal = Calendar.getInstance();

        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        //Sets start date to first day of current month
        startCal.set(Calendar.MONTH, currentMonth);
        startCal.set(Calendar.YEAR, currentYear);
        startCal.set(Calendar.DAY_OF_MONTH, 1);

        //Sets end date to today
        lblStartDate.setText(formatDateForLabel(startCal));
        lblEndDate.setText(formatDateForLabel(calendar));

        //Assigns default date values
        startDate = startCal.getTime();
        endDate = calendar.getTime();
    }

    private String formatDateForLabel(Calendar dateToFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        return dateFormat.format(dateToFormat.getTime());
    }

    //Returns true if end date is greater than start date or if same day is selected
    private boolean checkDateValidity(Date startDate, Date endDate) {
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();

        startCal.setTime(startDate);
        endCal.setTime(endDate);

        if (startCal.get(Calendar.DAY_OF_YEAR) == endCal.get(Calendar.DAY_OF_YEAR) &&
                startCal.get(Calendar.YEAR) == endCal.get(Calendar.YEAR)) {
            return true;
        } else {
            return startDate.compareTo(endDate) < 0;
        }
    }
}
