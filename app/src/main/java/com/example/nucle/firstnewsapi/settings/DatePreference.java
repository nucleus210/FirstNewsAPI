package com.example.nucle.firstnewsapi.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DatePreference extends DialogPreference {
    private int lastDate = 0;
    private int lastMonth = 0;
    private int lastYear = 0;
    private String dateValue;
    private CharSequence mSummary;
    private DatePicker picker = null;
    private static int getYear(String yearValue) {
        String[] pieces = yearValue.split("-");
        return (Integer.parseInt(pieces[0]));
    }

    private static int getMonth(String montValue) {
        String[] pieces = montValue.split("-");
        return (Integer.parseInt(pieces[1]));
    }

    private static int getDate(String dateValue) {
        String[] pieces = dateValue.split("-");
        return (Integer.parseInt(pieces[2]));
    }

    public DatePreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");
    }

    @Override
    protected View onCreateDialogView() {
        picker = new DatePicker(getContext());

        // setCalendarViewShown(false) attribute is only available from API level 11
        picker.setCalendarViewShown(false);

        return (picker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);

        picker.updateDate(lastYear, lastMonth + 1, lastDate);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            lastYear = picker.getYear();
            lastMonth = picker.getMonth();
            lastDate = picker.getDayOfMonth();

            String dateVal = String.valueOf(lastYear) + "-"
                    + String.valueOf(lastMonth) + "-"
                    + String.valueOf(lastDate);

            if (callChangeListener(dateVal)) {
                persistString(dateVal);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        dateValue = null;
        if (restoreValue) {
            if (defaultValue == null) {
                Calendar cal = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat format1 =
                        new SimpleDateFormat("yyyy-MM-dd");
                String formatted = format1.format(cal.getTime());
                dateValue = getPersistedString(formatted);
            } else {
                dateValue = getPersistedString(defaultValue.toString());
            }
        } else {
            dateValue = defaultValue.toString();
        }
        lastYear = getYear(dateValue);
        lastMonth = getMonth(dateValue);
        lastDate = getDate(dateValue);
    }

    public void setText(String text) {
        final boolean wasBlocking = shouldDisableDependents();

        dateValue = text;

        persistString(text);

        final boolean isBlocking = shouldDisableDependents();
        if (isBlocking != wasBlocking) {
            notifyDependencyChange(isBlocking);
        }
    }

    public String getText() {
        return dateValue;
    }

    public CharSequence getSummary() {
        return mSummary;
    }

    public void setSummary(CharSequence summary) {
        if (summary == null && mSummary != null || summary != null
                && !summary.equals(mSummary)) {
            mSummary = summary;
            notifyChanged();
        }
    }
}