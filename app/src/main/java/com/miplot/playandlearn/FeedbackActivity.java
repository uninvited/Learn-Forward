package com.miplot.playandlearn;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

public class FeedbackActivity extends AppCompatActivity {
    private EditText editTextName;
    private EditText editTextSchool;
    private EditText editTextForm;
    private EditText editTextSuggestion;

    private SeekBar seekBarInterest;
    private SeekBar seekBarUsefulness;
    private SeekBar seekBarRecognizeWhenRead;
    private SeekBar seekBarRecognizeWhenHear;
    private SeekBar seekBarUseInWriting;
    private SeekBar seekBarUseInSpeech;
    private SeekBar seekBarRememberToOpen;

    private CheckBox checkBoxWantMoreParts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        editTextName = (EditText)findViewById(R.id.name);
        editTextSchool = (EditText)findViewById(R.id.school);
        editTextForm = (EditText)findViewById(R.id.form);
        editTextSuggestion = (EditText)findViewById(R.id.suggestion);

        seekBarInterest = (SeekBar)findViewById(R.id.interest);
        seekBarUsefulness = (SeekBar)findViewById(R.id.usefulness);
        seekBarRecognizeWhenRead = (SeekBar)findViewById(R.id.recognize_when_read);
        seekBarRecognizeWhenHear = (SeekBar)findViewById(R.id.recognize_when_hear);
        seekBarUseInWriting = (SeekBar)findViewById(R.id.use_in_writing);
        seekBarUseInSpeech = (SeekBar)findViewById(R.id.use_in_speech);
        seekBarRememberToOpen = (SeekBar)findViewById(R.id.remember_to_open);

        checkBoxWantMoreParts = (CheckBox)findViewById(R.id.want_more_parts);

        editTextName.setOnFocusChangeListener(onFocusChangeListener);
        editTextSchool.setOnFocusChangeListener(onFocusChangeListener);
        editTextForm.setOnFocusChangeListener(onFocusChangeListener);
        editTextSuggestion.setOnFocusChangeListener(onFocusChangeListener);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                hideKeyboard(v);
            }
        }
    };

    public void onSubmitClick(View view) {
        String name = editTextName.getText().toString();
        String school = editTextSchool.getText().toString();
        String form = editTextForm.getText().toString();
        String suggestion = editTextSuggestion.getText().toString();
        int interest = seekBarInterest.getProgress();
        int usefulness = seekBarUsefulness.getProgress();
        int recognizeWhenRead = seekBarRecognizeWhenRead.getProgress();
        int recognizeWhenHear = seekBarRecognizeWhenHear.getProgress();
        int useInWriting = seekBarUseInWriting.getProgress();
        int useInSpeech = seekBarUseInSpeech.getProgress();
        int rememberToOpen = seekBarRememberToOpen.getProgress();
        boolean wantMoreParts = checkBoxWantMoreParts.isChecked();

        final String ANSWER = "\nОтвет: ";

        String body =  getString(R.string.feedback_name) + ": " + name + "\n" +
                getString(R.string.feedback_school) + ": " + school + "\n" +
                getString(R.string.feedback_form) + ": " + form + "\n\n" +
                getString(R.string.feedback_q1_interest) + ANSWER + interest + "\n\n" +
                getString(R.string.feedback_q2_usefulness) + ANSWER + usefulness + "\n\n" +
                getString(R.string.feedback_q3_recognize_when_read) + ANSWER + recognizeWhenRead + "\n\n" +
                getString(R.string.feedback_q4_recognize_when_hear) + ANSWER + recognizeWhenHear + "\n\n" +
                getString(R.string.feedback_q5_use_in_writing) + ANSWER + useInWriting + "\n\n" +
                getString(R.string.feedback_q6_use_in_speech) + ANSWER + useInSpeech + "\n\n" +
                getString(R.string.feedback_q7_remember_to_open) + ANSWER + rememberToOpen + "\n\n" +
                getString(R.string.feedback_q8_suggestion) + ANSWER + suggestion + "\n\n" +
                getString(R.string.feedback_q9_want_more_parts) + ANSWER + wantMoreParts + "\n\n";

        String EMAIL = "asia.plotkina@gmail.com";

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "LearnForward feedback");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.setData(Uri.parse("mailto:" + EMAIL));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
            finish();
            Toast.makeText(this, "Спасибо за отзыв!\nВы будете перенаправлены в почтовую программу", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
