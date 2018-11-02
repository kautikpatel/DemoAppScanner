package app.com.Ui;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import app.com.Managers.Communications.APIcall;
import app.com.Managers.SharedPreferenceManagerFile;
import app.com.Utils.AppConstant;
import app.com.Utils.Common;
import app.com.myapp.R;

public class ReportActivity extends BaseActivity implements View.OnClickListener, APIcall.ApiCallListner {

    private String start_date, end_date;
    private int yearFrom = 0;
    private int monthFrom = 0;
    private int dayFrom = 0;
    private int yearTo = 0;
    private int monthTo = 0;
    private int dayTo = 0;
    private Button btnFromDate, btnToDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        setToolbar();
        setActionBarTitle(getResources().getString(R.string.title_report));
        initializeComponents();
    }

    private void initializeComponents() {
        btnFromDate = findViewById(R.id.btnFromDate);
        btnFromDate.setOnClickListener(this);
        btnToDate = findViewById(R.id.btnToDate);
        btnToDate.setOnClickListener(this);
        findViewById(R.id.btnGo).setOnClickListener(this);
        Calendar c = Calendar.getInstance();
        yearFrom = c.get(Calendar.YEAR);
        monthFrom = c.get(Calendar.MONTH);
        dayFrom = c.get(Calendar.DAY_OF_MONTH);
        btnFromDate.setText(getDateString(yearFrom, (monthFrom + 1), dayFrom));

        yearTo = c.get(Calendar.YEAR);
        monthTo = c.get(Calendar.MONTH);
        dayTo = c.get(Calendar.DAY_OF_MONTH);
        btnToDate.setText(getDateString(yearTo, (monthTo + 1), dayTo));
        start_date = btnFromDate.getText().toString().trim();
        end_date = btnToDate.getText().toString().trim();
        loadData();
    }

    private String getDateString(int year, int month, int day) {
        String strMonth = month + "";
        if (month < 10) {
            strMonth = "0" + month;
        }
        String strDay = day + "";
        if (day < 10) {
            strDay = "0" + day;
        }
        String date = year + "-" + strMonth + "-" + strDay;
        return date;
    }

    private void loadData() {
        SharedPreferenceManagerFile sharedPref = new SharedPreferenceManagerFile(ReportActivity.this);
        String userId = sharedPref.getFromSharedPreference(SharedPreferenceManagerFile.PREFERENCE_USER_ID);
        if (Common.isInternetAvailable(ReportActivity.this)) {
            APIcall apIcall = new APIcall(ReportActivity.this);
            String url = AppConstant.API_GET_REPORT + AppConstant.REQUEST_USER_ID + "=" + userId + "&" + AppConstant.REQUEST_START_DATE + "=" + start_date + "&" + AppConstant.REQUEST_END_DATE + "=" + end_date;
            apIcall.execute(url, APIcall.OPERATION_ID_GET_REPORT, ReportActivity.this);
        } else {
            Common.displayToastMessageShort(ReportActivity.this, AppConstant.CONNECTION_ERROR_MSG, false);
            finish();
        }
    }

    //int count = 0;
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnFromDate) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    ReportActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int selectedYear,
                                      int selectedMonth, int selectedDay) {
                    yearFrom = selectedYear;
                    monthFrom = selectedMonth;
                    dayFrom = selectedDay;
                    btnFromDate.setText(getDateString(yearFrom, (monthFrom + 1), dayFrom));

                }
            }, yearFrom, monthFrom, dayFrom);
            datePickerDialog.show();
        } else if (view.getId() == R.id.btnToDate) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    ReportActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int selectedYear,
                                      int selectedMonth, int selectedDay) {
                    yearTo = selectedYear;
                    monthTo = selectedMonth;
                    dayTo = selectedDay;
                    String dateTo = getDateString(yearTo, (monthTo + 1), dayTo);
                    btnToDate.setText(dateTo);

                }
            }, yearTo, monthTo, dayTo);
            datePickerDialog.show();
        } else if (view.getId() == R.id.btnGo) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MONTH, monthFrom);
            calendar.set(Calendar.DAY_OF_MONTH, dayFrom);
            calendar.set(Calendar.YEAR, yearFrom);
            long fromTimeInMilli = calendar.getTimeInMillis();

            calendar = Calendar.getInstance();
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MONTH, monthTo);
            calendar.set(Calendar.DAY_OF_MONTH, dayTo);
            calendar.set(Calendar.YEAR, yearTo);
            long toTimeInMilli = calendar.getTimeInMillis();

            if (fromTimeInMilli > toTimeInMilli) {
                Common.displayToastMessageShort(ReportActivity.this, "'FROM DATE' must be less than 'TO DATE'", false);
                return;
            }
            start_date = btnFromDate.getText().toString().trim();
            end_date = btnToDate.getText().toString().trim();
            loadData();
        }
    }

    private ProgressDialog dialog;

    private void showDialog() {
        dialog = new ProgressDialog(ReportActivity.this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();
    }

    private void hideDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void onStartLoading(int operationCode) {
        if (APIcall.OPERATION_ID_GET_REPORT == operationCode) {
            showDialog();
        }
    }

    @Override
    public void onProgress(int operationCode, int progress) {

    }

    @Override
    public void onSuccess(int operationCode, String response, Object customData) {
        if (APIcall.OPERATION_ID_GET_REPORT == operationCode) {
            String message;
            try {
                /*if(count %2 == 0){
                    response = "{\n" +
                            "  \"data\": {\n" +
                            "    \"status\": true,\n" +
                            "    \"list\": {\n" +
                            "      \"checkin\": 10,\n" +
                            "      \"checkout\": 20,\n" +
                            "      \"inprocess\": 7,\n" +
                            "      \"delayed\": 2,\n" +
                            "      \"checkin2\": 10,\n" +
                            "      \"checkout2\": 20,\n" +
                            "      \"inprocess2\": 7,\n" +
                            "      \"delayed2\": 2\n" +
                            "    }\n" +
                            "  },\n" +
                            "  \"error\": {\n" +
                            "    \"code\": 0,\n" +
                            "    \"desc\": \"\"\n" +
                            "  }\n" +
                            "}";
                }
                count++;*/
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("data")) {
                    JSONObject jsonObjectData = jsonObject.optJSONObject("data");
                    boolean status = jsonObjectData.optBoolean("status", false);
                    if (status) {
                        setUpGraph(jsonObjectData.optJSONObject("list"));
                    }
                } else {
                    JSONObject jsonObjectError = jsonObject.optJSONObject("error");
                    if (jsonObjectError != null) {
                        message = jsonObjectError.optString("desc", "");
                        Common.displayToastMessageShort(ReportActivity.this, message, false);
                    }
                }
            } catch (Exception e) {
                Common.displayToastMessageShort(ReportActivity.this, "Error in get details.", false);
                e.printStackTrace();
            }
        }
        hideDialog();
    }

    @Override
    public void onFail(int operationCode, String response) {
        hideDialog();
    }

    protected BarChart mChart;

    private void setUpGraph(JSONObject json) {
        mChart = findViewById(R.id.chart1);
        mChart.clear();
        mChart.setDescription("");

        ArrayList<BarEntry> bargroup1 = new ArrayList<>();
        ArrayList<String> xValues = new ArrayList<String>();
        try {
            //JSONObject json = new JSONObject("{\"checkin\": 10, \"checkout\": 20, \"inprocess\": 7, \"delayed\": 2}");
            Iterator<String> iter = json.keys();
            int count = 0;
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    String value = json.getString(key);
                    bargroup1.add(new BarEntry(Float.parseFloat(value), count));
                    xValues.add(key);
                    count++;
                } catch (Exception e) {
                    // Something went wrong!
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // creating dataset for Bar Group1
        BarDataSet barDataSet1 = new BarDataSet(bargroup1, "Report (" + start_date + " to " + end_date + ")");

        //barDataSet1.setColor(Color.rgb(0, 155, 0));
        barDataSet1.setColors(ColorTemplate.COLORFUL_COLORS);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();  // combined all dataset into an arraylist
        dataSets.add(barDataSet1);

        // initialize the Bardata with argument labels and dataSet
        BarData data = new BarData(xValues, dataSets);
        mChart.setData(data);
        mChart.invalidate();
    }

}
