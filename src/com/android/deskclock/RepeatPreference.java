/*
 * Copyright (C) 2008 The Android Open Source Project
 * Copyright (C) 2011 Iranian Supreme Council of ICT, The FarsiTel Project
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
import android.text.format.DateUtils;
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

        CharSequence[] values = new CharSequence[7];
        for (int i=0; i<7 ; i++)
            values[i] = DateUtils.getDayOfWeekString(((i + 1) % 7) + 1, DateUtils.LENGTH_LONG);

        isJalali = Jalali.isJalali(context);
        if (isJalali) {
            values = new CharSequence[] {
                values[5], values[6], values[0], values[1], values[2], values[3], values[4]
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
