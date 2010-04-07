/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.deskclock;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.text.format.Jalali;

import java.util.Calendar;

public class RepeatPreference extends ListPreference {

    // Initial value that can be set with the values saved in the database.
    private Alarm.DaysOfWeek mDaysOfWeek = new Alarm.DaysOfWeek(0);
    // New value that will be set if a positive result comes back from the
    // dialog.
    private Alarm.DaysOfWeek mNewDaysOfWeek = new Alarm.DaysOfWeek(0);

    private boolean isJalali = false;

    public RepeatPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        CharSequence[] values = new CharSequence[] {
            context.getText(com.android.internal.R.string.day_of_week_long_monday),
            context.getText(com.android.internal.R.string.day_of_week_long_tuesday),
            context.getText(com.android.internal.R.string.day_of_week_long_wednesday),
            context.getText(com.android.internal.R.string.day_of_week_long_thursday),
            context.getText(com.android.internal.R.string.day_of_week_long_friday),
            context.getText(com.android.internal.R.string.day_of_week_long_saturday),
            context.getText(com.android.internal.R.string.day_of_week_long_sunday),
         };
        isJalali = Jalali.isJalali(context);
        if (isJalali) {
            values = new CharSequence[] {
                context.getText(com.android.internal.R.string.day_of_week_long_saturday),
                context.getText(com.android.internal.R.string.day_of_week_long_sunday),
                context.getText(com.android.internal.R.string.day_of_week_long_monday),
                context.getText(com.android.internal.R.string.day_of_week_long_tuesday),
                context.getText(com.android.internal.R.string.day_of_week_long_wednesday),
                context.getText(com.android.internal.R.string.day_of_week_long_thursday),
                context.getText(com.android.internal.R.string.day_of_week_long_friday),
            };
        }
        setEntries(values);
        setEntryValues(values);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            mDaysOfWeek.set(mNewDaysOfWeek);
            setSummary(mDaysOfWeek.toString(getContext(), true));
            callChangeListener(mDaysOfWeek);
        }
    }

    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        CharSequence[] entries = getEntries();
        CharSequence[] entryValues = getEntryValues();

        boolean[] daysOfWeek = mDaysOfWeek.getBooleanArray();
        if (isJalali)
            daysOfWeek = new boolean[] {
                daysOfWeek[5],
                daysOfWeek[6],
                daysOfWeek[0],
                daysOfWeek[1],
                daysOfWeek[2],
                daysOfWeek[3],
                daysOfWeek[4],
            };

        builder.setMultiChoiceItems(
                entries, daysOfWeek,
                new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog, int which,
                            boolean isChecked) {
                        if (isJalali)
                            which = (which + 5) % 7;
                        mNewDaysOfWeek.set(which, isChecked);
                    }
                });
    }

    public void setDaysOfWeek(Alarm.DaysOfWeek dow) {
        mDaysOfWeek.set(dow);
        mNewDaysOfWeek.set(dow);
        setSummary(dow.toString(getContext(), true));
    }

    public Alarm.DaysOfWeek getDaysOfWeek() {
        return mDaysOfWeek;
    }
}
