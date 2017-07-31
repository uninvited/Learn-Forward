package com.miplot.playandlearn.dict;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.miplot.playandlearn.R;
import com.miplot.playandlearn.Utils;
import com.miplot.playandlearn.Word;

import java.util.List;

public class DictionaryWordsAdapter extends RecyclerView.Adapter<DictionaryWordsAdapter.ViewHolder> {
    List<Word> words;
    Direction direction;

    public enum Direction {
        EnRu,
        RuEn
    }

    DictionaryWordsAdapter(List<Word> words, Direction direction) {
        super();
        this.words = words;
        this.direction = direction;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView wordEnTextView;
        public TextView wordRuTextView;
        public ImageView audioButton;

        ViewHolder(View v, TextView wordEnTextView, TextView wordRuTextView, ImageView audioButton) {
            super(v);
            this.wordEnTextView = wordEnTextView;
            this.wordRuTextView = wordRuTextView;
            this.audioButton = audioButton;
        }
    }

    @Override
    public DictionaryWordsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View convertView;
        if (direction == Direction.EnRu) {
            convertView = inflater.inflate(R.layout.dict_en_ru_item, parent, false);
        } else {
            convertView = inflater.inflate(R.layout.dict_ru_en_item, parent, false);
        }

        DictionaryWordsAdapter.ViewHolder viewHolder = new DictionaryWordsAdapter.ViewHolder(convertView,
                (TextView) convertView.findViewById(R.id.word_en),
                (TextView) convertView.findViewById(R.id.word_ru),
                (ImageView) convertView.findViewById(R.id.audio_button));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final DictionaryWordsAdapter.ViewHolder holder, int position) {
        final Word word = words.get(position);

        holder.wordEnTextView.setText(word.getWordEn());
        holder.wordEnTextView.setTextColor(Utils.getWordColor(holder.wordEnTextView.getContext(), word));
        holder.wordRuTextView.setText(word.getWordRu());
        holder.audioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.playAudio(holder.audioButton.getContext(), word);
            }
        });
    }

    @Override
    public int getItemCount() {
        return words.size();
    }
}