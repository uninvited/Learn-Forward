package com.miplot.playandlearn;


import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.miplot.playandlearn.trainings.ViewPagerActivity;
import com.miplot.playandlearn.trainings.FragmentChooseOdd;
import com.miplot.playandlearn.trainings.FragmentChooseTranslation;
import com.miplot.playandlearn.trainings.FragmentChooseWordInPhrase;
import com.miplot.playandlearn.trainings.FragmentLearn;
import com.miplot.playandlearn.trainings.FragmentListenAndChoose;
import com.miplot.playandlearn.trainings.FragmentListenAndPrint;
import com.miplot.playandlearn.trainings.FragmentReadAndRecall;
import com.miplot.playandlearn.trainings.FragmentSortByClass;
import com.miplot.playandlearn.trainings.FragmentSpeak;
import com.miplot.playandlearn.trainings.FragmentTranslate;

import java.util.ArrayList;
import java.util.List;

public class TrainingListAdaptor extends RecyclerView.Adapter<TrainingListAdaptor.ViewHolder> {
    private final Context context;
    private final Unit unit;
    private List<TrainingDescriptor> trainingDescriptors;

    TrainingListAdaptor(Context context, Unit unit) {
        super();
        this.context = context;
        this.unit = unit;
        this.trainingDescriptors = makeTrainingInfos();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View convertView;
        CardView cardView;
        TextView name;

        public ViewHolder(View v, CardView cardView, TextView name) {
            super(v);
            this.convertView = v;
            this.cardView = cardView;
            this.name = name;
        }
    }

    @Override
    public TrainingListAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View convertView = inflater.inflate(R.layout.training_type_item, parent, false);

        return new TrainingListAdaptor.ViewHolder(
                convertView,
                (CardView) convertView.findViewById(R.id.card_view),
                (TextView) convertView.findViewById(R.id.name));
    }

    @Override
    public void onBindViewHolder(TrainingListAdaptor.ViewHolder viewHolder, final int position) {
        final TrainingDescriptor trainingDescriptor = trainingDescriptors.get(position);

        viewHolder.name.setText(trainingDescriptor.getTitleResourceId());
        viewHolder.name.setBackgroundDrawable(
                        ContextCompat.getDrawable(context, trainingDescriptor.getColorResourceId()));

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewPagerActivity.class);
                intent.putExtra(TrainingsListActivity.INTENT_EXTRA_UNIT, unit);
                intent.putExtra(TrainingsListActivity.INTENT_EXTRA_TRAINING_DESCR, trainingDescriptor);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return trainingDescriptors.size();
    }

    private List<TrainingDescriptor> makeTrainingInfos() {
        List<TrainingDescriptor> trainingButtonInfos = new ArrayList<>();
        trainingButtonInfos.add(new TrainingDescriptor(
                TrainingType.LEARN,
                R.string.training_type_learn,
                R.drawable.btn_learn_bg,
                FragmentLearn.class));
        trainingButtonInfos.add(new TrainingDescriptor(
                TrainingType.READ_AND_RECALL,
                R.string.training_type_read_and_recall,
                R.drawable.btn_read_and_recall_bg,
                FragmentReadAndRecall.class));
        trainingButtonInfos.add(new TrainingDescriptor(
                TrainingType.LISTEN_AND_CHOOSE,
                R.string.training_type_listen_and_choose,
                R.drawable.btn_listen_and_choose_bg,
                FragmentListenAndChoose.class));
        trainingButtonInfos.add(new TrainingDescriptor(
                TrainingType.SORT_BY_CLASS,
                R.string.training_type_sort_by_word_class,
                R.drawable.btn_sort_words_by_class_bg,
                FragmentSortByClass.class));
        trainingButtonInfos.add(new TrainingDescriptor(
                TrainingType.CHOOSE_TRANSLATION,
                R.string.training_type_choose_translation,
                R.drawable.btn_choose_translation_bg,
                FragmentChooseTranslation.class));
        trainingButtonInfos.add(new TrainingDescriptor(
                TrainingType.CHOOSE_WORD_IN_PHRASE,
                R.string.training_type_choose_word_in_phrase,
                R.drawable.btn_choose_word_in_phrase_bg,
                FragmentChooseWordInPhrase.class));
        trainingButtonInfos.add(new TrainingDescriptor(
                TrainingType.LISTEN_AND_PRINT,
                R.string.training_type_listen_and_print,
                R.drawable.btn_listen_and_print_bg,
                FragmentListenAndPrint.class));
        trainingButtonInfos.add(new TrainingDescriptor(
                TrainingType.TRANSLATE,
                R.string.training_type_translate,
                R.drawable.btn_translate_bg,
                FragmentTranslate.class));
        trainingButtonInfos.add(new TrainingDescriptor(
                TrainingType.CHOOSE_ODD,
                R.string.training_type_choose_odd,
                R.drawable.btn_choose_odd_bg,
                FragmentChooseOdd.class));
        trainingButtonInfos.add(new TrainingDescriptor(
                TrainingType.SPEAK,
                R.string.training_type_speak,
                R.drawable.btn_speak_bg,
                FragmentSpeak.class));
        return trainingButtonInfos;
    }

}
