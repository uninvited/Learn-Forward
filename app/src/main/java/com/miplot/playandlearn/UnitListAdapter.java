package com.miplot.playandlearn;

import android.content.Context;
import android.content.Intent;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

class UnitListAdapter extends RecyclerView.Adapter<UnitListAdapter.ViewHolder> {
    private final Context context;
    List<Pair<Unit, Integer>> units;

    UnitListAdapter(Context context, List<Pair<Unit, Integer>> units) {
        super();
        this.context = context;
        this.units = units;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View convertView;
        TextView id;
        TextView name;
        ProgressBar progressBar;
        ImageView bronzeCup;
        ImageView silverCup;
        ImageView goldCup;

        public ViewHolder(View v,
                          TextView id,
                          TextView name,
                          ProgressBar progressBar,
                          ImageView bronzeCup,
                          ImageView silverCup,
                          ImageView goldCup) {
            super(v);
            this.convertView = v;
            this.id = id;
            this.name = name;
            this.progressBar = progressBar;
            this.bronzeCup = bronzeCup;
            this.silverCup = silverCup;
            this.goldCup = goldCup;
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View convertView = inflater.inflate(R.layout.unit_item, parent, false);

        return new UnitListAdapter.ViewHolder(
                convertView,
                (TextView) convertView.findViewById(R.id.unit_id),
                (TextView) convertView.findViewById(R.id.unit_name),
                (ProgressBar) convertView.findViewById(R.id.unit_progress_bar),
                (ImageView) convertView.findViewById(R.id.bronze_cup),
                (ImageView) convertView.findViewById(R.id.silver_cup),
                (ImageView) convertView.findViewById(R.id.gold_cup));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final Pair<Unit, Integer> unit = units.get(position);

        viewHolder.id.setText("UNIT " + unit.first.getId());
        viewHolder.name.setText(unit.first.getName());
        int progress = unit.second;
        viewHolder.progressBar.setProgress(progress);
        viewHolder.bronzeCup.bringToFront();
        viewHolder.silverCup.bringToFront();
        viewHolder.goldCup.bringToFront();
        if (progress >= 50) {
            viewHolder.bronzeCup.setImageResource(R.drawable.cup_bronze);
        }
        if (progress > 75) {
            viewHolder.silverCup.setImageResource(R.drawable.cup_silver);
        }
        if (progress > 90) {
            viewHolder.goldCup.setImageResource(R.drawable.cup_gold);
        }

        viewHolder.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TrainingsListActivity.class);
                intent.putExtra(TrainingsListActivity.INTENT_EXTRA_UNIT, unit.first);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return units.size();
    }
}