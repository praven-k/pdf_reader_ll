package com.example.Pdfreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;

//import com.example.readmypdf.R;
import com.example.Pdfreader.R;
import java.util.Locale;

public class Result extends AppCompatActivity {
    private TextToSpeech mTTS;
    String str;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        TextView receiver_msg = findViewById(R.id.textView);
        Intent intent = getIntent();
        str = intent.getStringExtra("message_key");
        receiver_msg.setText(str);


        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.ENGLISH);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        //mButtonSpeak.setEnabled(true);
                        speak();
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });








    }
    private void speak() {
        //String text = mEditText.getText().toString();
       /* float pitch = (float) mSeekBarPitch.getProgress() / 50;
        if (pitch < 0.1) pitch = 0.1f;
        float speed = (float) mSeekBarSpeed.getProgress() / 50;
        if (speed < 0.1) speed = 0.1f;

       // mTTS.setPitch(pitch);
        //mTTS.setSpeechRate(speed);  */

        mTTS.speak(str, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    protected void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }

        super.onDestroy();
    }
    }
