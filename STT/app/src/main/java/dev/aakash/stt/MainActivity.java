package dev.aakash.stt;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.SeekBar;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.*;
import android.util.*;

import java.util.Locale;
//import com.google.api.translate.Language;


public class MainActivity extends AppCompatActivity {

    private TextToSpeech mTTS;
    private EditText mEditText;
    private SeekBar mSeekBarPitch;
    private SeekBar getmSeekBarSpeed;
    private Button mButtonSpeak;
    @RequiresApi(api = Build.VERSION_CODES.DONUT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButtonSpeak = findViewById(R.id.button_speak);

        mTTS = new TextToSpeech(this,new TextToSpeech.OnInitListener(){

            public void onInit(int status){
               if(status == TextToSpeech.SUCCESS){
                   int result= mTTS.setLanguage(Locale.forLanguageTag("hin"));

               if(result ==TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                   Log.e("TTS","Language not Supported");
               }else
               {
                   mButtonSpeak.setEnabled(true);
               }
               }else {
                   Log.e("TTS","Initialisation failed");
               }
            }
        });

        mEditText = findViewById(R.id.edit_text);
        mSeekBarPitch= findViewById(R.id.seek_bar_pitch);
        getmSeekBarSpeed = findViewById(R.id.seek_bar_speed);

        mButtonSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speak();
            }
        });
    }
    private void speak(){
        String text=mEditText.getText().toString();
        float pitch = (float)mSeekBarPitch.getProgress()/50;
        if(pitch<0.1) pitch=0.1f;
        float speed =(float)getmSeekBarSpeed.getProgress()/50;
        if(speed<0.1) speed=0.1f;

        mTTS.setPitch(pitch);
        mTTS.setSpeechRate(speed);

        mTTS.speak(text,TextToSpeech.QUEUE_FLUSH,null);
    }

    @Override
    protected void onDestroy() {
        if(mTTS!=null) {
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }
}
