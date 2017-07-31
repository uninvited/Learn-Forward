package com.miplot.playandlearn.stat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.miplot.playandlearn.DailyStat;
import com.miplot.playandlearn.R;

import java.util.List;

class MonthStatAdapter extends RecyclerView.Adapter<MonthStatAdapter.ViewHolder> {
    List<DailyStat> stats;

    MonthStatAdapter(List<DailyStat> stats) {
        super();
        this.stats = stats;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView month;
        public TextView numTrainings;
        public TextView duration;

        ViewHolder(View v, TextView month, TextView numTrainings, TextView duration) {
            super(v);
            this.month = month;
            this.numTrainings = numTrainings;
            this.duration = duration;
        }
    }

    @Override
    public MonthStatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View convertView = inflater.inflate(R.layout.stat_month_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(convertView,
                (TextView) convertView.findViewById(R.id.month_name),
                (TextView) convertView.findViewById(R.id.num_trainings),
                (TextView) convertView.findViewById(R.id.duration));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.duration.setText("Общее время: " + formatDuration(stats.get(position).durationSec));
        holder.numTrainings.setText("Количество тренировок: " + stats.get(position).trainingsCount);
        holder.month.setText(monthName(stats.get(position).month));
    }

    @Override
    public int getItemCount() {
        return stats.size();
    }

    private String monthName(int monthId) {
        switch(monthId) {
            case 1: return "ЯНВАРЬ";
            case 2: return "ФЕВРАЛЬ";
            case 3: return "МАРТ";
            case 4: return "АПРЕЛЬ";
            case 5: return "МАЙ";
            case 6: return "ИЮНЬ";
            case 7: return "ИЮЛЬ";
            case 8: return "АВГУСТ";
            case 9: return "СЕНТЯБРЬ";
            case 10: return "ОКТЯБРЬ";
            case 11: return "НОЯБРЬ";
            case 12: return "ДЕКАБРЬ";
            default: return null;
        }
    }


    private String formatDuration(int seconds) {
        String valueText;
        if (seconds <= 0) {
            valueText = "0 мин.";
        } else if (seconds < 60) {
            valueText = "1 мин.";
        } else if (seconds < 3600) {
            valueText = Integer.toString((int) Math.round(seconds / 60.)) + " мин.";
        } else {
            int hours = seconds / 3600;
            int minutes = (int) Math.round((seconds - hours * 3600) / 60.);
            valueText = hours + " ч. " + minutes + " мин.";
        }
        return valueText;
    }
}