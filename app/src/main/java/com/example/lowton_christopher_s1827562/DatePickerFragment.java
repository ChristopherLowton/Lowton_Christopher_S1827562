package com.example.lowton_christopher_s1827562;
//Christopher Lowton - S1827562
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

import androidx.fragment.app.DialogFragment;

public class DatePickerFragment  extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    //Christopher Lowton - S1827562
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Intent intent = new Intent(getActivity(), ListActivity.class);
        intent.putExtra("type", 3);
        intent.putExtra("year", i);
        intent.putExtra("month", i1);
        intent.putExtra("day", i2);
        startActivity(intent);
    }
}
