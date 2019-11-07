package com.waterfairy.dateselector;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/6/12 16:51
 * @info:
 */
public class DateSelectDialog extends Dialog implements View.OnClickListener, CalendarView.OnDateChangeListener {
    private int month;
    private int year;
    private int dayOfMonth;
    private CalendarView mCalendarView;
    private Button mBTEnsure;
    private Button mBTCancel;
    private OnDateSelectListener onDateSelectListener;
    private OnDateSelectOverListener onDateSelectOverListener;
    private int colorNormal, colorCannotClick;
    private Object object;
    private boolean showToast = true;

    private long limitBefore = -1;
    private long limitAfter = -1;

    public DateSelectDialog(@NonNull Context context, OnDateSelectListener selectListener) {
        this(context, -1, -1, -1, selectListener);
    }

    public DateSelectDialog(@NonNull Context context, int year, int month, int dayOfMonth, OnDateSelectListener selectListener) {
        super(context);
        colorNormal = context.getResources().getColor(R.color.colorDialogButton);
        colorCannotClick = context.getResources().getColor(R.color.colorDialogButton2);
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.onDateSelectListener = selectListener;
        setTitle("选择时间");
        setContentView(LayoutInflater.from(context).inflate(R.layout.dialog_select_date, null, false), new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        findView();
        initView();
    }
    public void setBeforeLimit(long limitBefore) {
        this.limitBefore = limitBefore;
    }

    public void setAfterLimit(long limitAfter) {
        this.limitAfter = limitAfter;
    }

    public void setBeforeLimit(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        limitBefore = calendar.getTimeInMillis();
    }


    public void setAfterLimit(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        limitAfter = calendar.getTimeInMillis() + 24 * 60 * 60 * 1000;
    }


    private void findView() {
        mCalendarView = findViewById(R.id.calendar);
        mBTEnsure = findViewById(R.id.positive);
        mBTCancel = findViewById(R.id.negative);
    }

    private void initView() {
        if (year != -1) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            mCalendarView.setDate(calendar.getTimeInMillis());
        }
        mCalendarView.setOnDateChangeListener(this);
        mBTEnsure.setOnClickListener(this);
        mBTCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.positive) {
            if (onDateSelectListener != null) {
                onDateSelectListener.onDateSelect(this, year, month, dayOfMonth);
                dismiss();
            }
        } else if (v.getId() == R.id.negative) {
            dismiss();
        }
    }

    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        long currentTime = calendar.getTimeInMillis();

        if (limitAfter > 0 && limitAfter < currentTime) {
            mBTEnsure.setTextColor(colorCannotClick);
            mBTEnsure.setClickable(false);
            showOver(true, limitAfter, currentTime);
        } else if (limitBefore > 0 && limitBefore > currentTime) {
            mBTEnsure.setTextColor(colorCannotClick);
            mBTEnsure.setClickable(false);
            showOver(false, limitBefore, currentTime);

        } else {
            mBTEnsure.setTextColor(colorNormal);
            mBTEnsure.setClickable(true);
            this.year = year;
            this.month = month;
            this.dayOfMonth = dayOfMonth;
        }
    }

    public boolean isShowToast() {
        return showToast;
    }

    public void setShowToast(boolean showToast) {
        this.showToast = showToast;
    }

    /**
     * 不在限制区间内
     *
     * @param after
     * @param limitTime
     * @param currentTime
     */
    private void showOver(boolean after, long limitTime, long currentTime) {
        if (onDateSelectOverListener != null) {
            onDateSelectOverListener.onDateSelectOver(this, after, limitTime, currentTime);
        }
        if (showToast) {
            String msg = "";
            String limitDay = new SimpleDateFormat("yyyy年MM月dd日").format(new Date(limitTime-1));
            if (after) {
                msg = "日期不能在" + limitDay + "之后";
            } else {
                msg = "日期不能在" + limitDay + "之前";
            }
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    public void setOnDateSelectOverListener(OnDateSelectOverListener onDateSelectOverListener) {
        this.onDateSelectOverListener = onDateSelectOverListener;
    }


    public interface OnDateSelectListener {
        void onDateSelect(DateSelectDialog dateSelectDialog, int year, int month, int day);
    }

    public interface OnDateSelectOverListener {

        void onDateSelectOver(DateSelectDialog dateSelectDialog, boolean isAfter, long limitTime, long currentTime);
    }

    public void setTag(Object object) {
        this.object = object;
    }

    public Object getTag() {
        return object;
    }
}
