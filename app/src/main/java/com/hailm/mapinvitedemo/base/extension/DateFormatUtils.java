package com.hailm.mapinvitedemo.base.extension;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.hailm.mapinvitedemo.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.annotation.Nullable;

public class DateFormatUtils {

    public enum DateFormat {
        DateTimeWithMilliSec_NonSeparate("yyyyMMddHHmmssSSSSS"), DateTime_Hyphen("yyyy-MM-dd HH:mm:ss"), Date_Hyphen("yyyy-MM-dd");

        private final String pattern;

        private DateFormat(String pattern) {
            this.pattern = pattern;
        }

        public boolean equalsPattern(String otherPattern) {
            // (otherName == null) check is not needed because name.equals(null) returns false
            return pattern.equals(otherPattern);
        }

        public String toString() {
            return this.pattern;
        }
    }

    public static String formatSimpleDate(Context context, @Nullable String stringDate) {
        if (stringDate == null) return "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date date = simpleDateFormat.parse(stringDate);
            return new SimpleDateFormat(context.getResources().getString(R.string.sdf_ymd_w_hm)).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String format(@NonNull Date date, String pattern) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }

    public static String getJSTDateString(DateFormat dateFormat) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat.pattern, Locale.JAPAN);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Japan"));
        return simpleDateFormat.format(date);
    }

    public static String getDateString(Date date, DateFormat dateFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat.pattern);
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat.format(date);
    }

    @Nullable
    public static Date getDateTimeZoneJSPFrom(String string, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.JAPAN);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Japan"));
        try {
            return simpleDateFormat.parse(string);
        } catch (Exception ignored) {
        }
        return null;
    }

    @Nullable
    public static Date from(@NonNull String string, @NonNull String format) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            return simpleDateFormat.parse(string);
        } catch (ParseException e) {
            Log.d("PARSE DATE", "Cannot parse " + string + " to date with format: " + format + " with error: " + e.getMessage());
        }
        return null;
    }
}
