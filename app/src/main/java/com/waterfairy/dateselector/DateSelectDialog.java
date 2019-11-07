package com.waterfairy.dateselector;

import android.app.Dialog;
import android.content.Context;
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
import java.util.Locale;

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

    public DateSelectDialog(Context context, OnDateSelectListener selectListener) {
        this(context, -1, -1, -1, selectListener);
    }

    public DateSelectDialog(Context context, int year, int month, int dayOfMonth, OnDateSelectListener selectListener) {
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

    /**
     * 开始可选的日期(前一天不可选择)
     *
     * @param limitBefore
     */
    public void setBeforeLimit(long limitBefore) {
        Calendar calendar = getCalendar(limitBefore);
        setBeforeLimit(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * 结束可选的日期(后一天不可选择)
     *
     * @param limitAfter
     */
    public void setAfterLimit(long limitAfter) {
        Calendar calendar = getCalendar(limitAfter);
        setAfterLimit(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    private Calendar getCalendar(long limitTime) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTimeInMillis(limitTime);
        return calendar;
    }

    public void setBeforeLimit(int year, int month, int day) {
        limitBefore = getTime(year, month, day);
    }

    private long getTime(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(year, month, day, 0, 0, 0);
        return calendar.getTimeInMillis();
    }


    public void setAfterLimit(int year, int month, int day) {
        limitAfter = getTime(year, month, day);
    }


    private void findView() {
        mCalendarView = findViewById(R.id.calendar);
        mBTEnsure = findViewById(R.id.positive);
        mBTCancel = findViewById(R.id.negative);
    }

    private void initView() {
        if (year != -1) {
            Calendar calendar = Calendar.getInstance(Locale.CHINA);
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
    public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
        long currentTime = getTime(year, month, dayOfMonth);
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
            if (after) {
                String limitDay = new SimpleDateFormat("yyyy年MM月dd日").format(new Date(limitTime));
                msg = "日期不能在" + limitDay + "之后";
            } else {
                String limitDay = new SimpleDateFormat("yyyy年MM月dd日").format(new Date(limitTime));
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
