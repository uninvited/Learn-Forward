package com.miplot.playandlearn.dict;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miplot.playandlearn.Db;
import com.miplot.playandlearn.MainApplication;
import com.miplot.playandlearn.R;
import com.miplot.playandlearn.Word;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


// TODO: use the same fragment class and an argument to distinguish between En->Ru and Ru->En
public class DictionaryRuEnFragment extends Fragment {
    private Db db;

    private RecyclerView wordsListView;
    private DictionaryWordsAdapter monthsListAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dict_en_ru, container, false);

        layoutManager = new LinearLayoutManager(getContext());
        wordsListView = (RecyclerView) rootView.findViewById(R.id.words_list);
        db = ((MainApplication)(getActivity().getApplication())).getDb();

        List<Word> words = db.getWords(null);
            Collections.sort(words, new Comparator<Word>() {
                @Override
                public int compare(Word w1, Word w2) {
                    return w1.getWordRu().compareToIgnoreCase(w2.getWordRu());
                }
            });
        monthsListAdapter = new DictionaryWordsAdapter(words, DictionaryWordsAdapter.Direction.RuEn);
        wordsListView.setLayoutManager(layoutManager);
        wordsListView.setAdapter(monthsListAdapter);

        return rootView;
    }
}
