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

package com.android.calendar;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.pim.DateFormat;
import android.pim.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

public class AlertAdapter extends ResourceCursorAdapter {

    public AlertAdapter(Context context, int resource) {
        super(context, resource, null);
    }
    
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textView;
        
        ImageView stripe = (ImageView) view.findViewById(R.id.vertical_stripe);
        int color = cursor.getInt(AlertActivity.INDEX_COLOR) & 0xbbffffff;
        stripe.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        
        // Repeating info
        View repeatContainer = view.findViewById(R.id.repeat_icon);
        String rrule = cursor.getString(AlertActivity.INDEX_RRULE);
        if (rrule != null) {
            repeatContainer.setVisibility(View.VISIBLE);
        } else {
            repeatContainer.setVisibility(View.GONE);
        }
                
        // Reminder
        boolean hasAlarm = cursor.getInt(AlertActivity.INDEX_HAS_ALARM) != 0;
        if (hasAlarm) {
            AgendaAdapter.updateReminder(view, context, cursor.getLong(AlertActivity.INDEX_BEGIN),
                    cursor.getLong(AlertActivity.INDEX_EVENT_ID));
        }
        
        String eventName = cursor.getString(AlertActivity.INDEX_TITLE);
        String location = cursor.getString(AlertActivity.INDEX_EVENT_LOCATION);
        long startMillis = cursor.getLong(AlertActivity.INDEX_BEGIN);
        long endMillis = cursor.getLong(AlertActivity.INDEX_END);
        boolean allDay = cursor.getInt(AlertActivity.INDEX_ALL_DAY) != 0;
        
        updateView(context, view, eventName, location, startMillis, endMillis, allDay);
    }
    
    public static void updateView(Context context, View view, String eventName, String location,
            long startMillis, long endMillis, boolean allDay) {
        
        Resources res = context.getResources();
        TextView textView;
        
        // What
        if (eventName == null || eventName.length() == 0) {
            eventName = res.getString(R.string.no_title_label);
        }
        textView = (TextView) view.findViewById(R.id.event_title);
        textView.setText(eventName);
        
        // When
        String when;
        int flags;
        if (allDay) {
            flags = DateUtils.FORMAT_UTC | DateUtils.FORMAT_SHOW_WEEKDAY |
                    DateUtils.FORMAT_SHOW_DATE;
        } else {
            flags = DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE;
        }
        if (DateFormat.is24HourFormat(context)) {
            flags |= DateUtils.FORMAT_24HOUR;
        }
        when = DateUtils.formatDateRange(startMillis, endMillis, flags);
        textView = (TextView) view.findViewById(R.id.when);
        textView.setText(when);
        
        // Where
        textView = (TextView) view.findViewById(R.id.where);
        if (location == null || location.length() == 0) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setText(location);
        }
    }
}
