package com.miplot.playandlearn;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

public class Utils {
    private static final String TAG = "Utils";
    private static MediaPlayer mediaPlayer = new MediaPlayer();

    static {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
            }
        });
    }

    public static int getWordColor(Context context, Word word) {
        switch (word.getWordClass()) {
            case NOUN:
                return context.getResources().getColor(R.color.noun);
            case VERB:
                return context.getResources().getColor(R.color.verb);
            case ADJ:
                return context.getResources().getColor(R.color.adjective);
            default:
                return context.getResources().getColor(R.color.default_class);
        }
    }

    @Nullable
    public static Bitmap getImageBitmap(Context context, Word word) {
        try {
            String filename = word.getWordEn().toLowerCase() + ".jpg";
            InputStream stream = context.getAssets().open("images/" + filename);
            return BitmapFactory.decodeStream(stream);
        } catch (IOException e) {
            Log.d(TAG, "Image for " + word.getWordEn() + " not found");
            return null;
        }
    }

    public static void playAudio(Context context, Word word) {
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            }

            final String audioFilename = "audio/" + word.getWordEn().toLowerCase() + ".mp3";
            AssetFileDescriptor descriptor = context.getAssets().openFd(audioFilename);
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            mediaPlayer.prepare();
            mediaPlayer.setVolume(1f, 1f);
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final String AUDIO_EFFECT_CLICK = "click";

    public static void playEffect(Context context, String filename) {
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            }

            final String audioFilename = "audio_effects/" + filename + ".mp3";
            AssetFileDescriptor descriptor = context.getAssets().openFd(audioFilename);
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            mediaPlayer.prepare();
            mediaPlayer.setVolume(1f, 1f);
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
