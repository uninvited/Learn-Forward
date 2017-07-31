package com.miplot.playandlearn.dict;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.miplot.playandlearn.R;

import uk.co.senab.photoview.PhotoViewAttacher;

public class DictionaryMindMapFragment extends Fragment {
    private ImageView imageView;
    private PhotoViewAttacher photoViewAttacher;

    public DictionaryMindMapFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dict_mind_map, container, false);
        imageView = (ImageView)rootView.findViewById(R.id.mind_map);
        photoViewAttacher = new PhotoViewAttacher(imageView);
        photoViewAttacher.update();
        return rootView;
    }
}

