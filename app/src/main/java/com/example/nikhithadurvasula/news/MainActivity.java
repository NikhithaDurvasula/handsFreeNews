package com.example.nikhithadurvasula.news;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener
{
    public Button business;
    public Button general;
    public Button sports;
    public Button technology;
    public int MY_DATA_CHECK_CODE = 0;
    public TextToSpeech myTTS;

    private static final String TAG = MainActivity.class.getName();
    protected PowerManager.WakeLock mWakeLock;
    private SpeechRecognizer mSpeechRecognizer;
    private Handler mHandler = new Handler();
    TextView responseText;
    Intent mSpeechIntent;
    boolean killCommanded = false;
    private static final String[] VALID_COMMANDS = {"who are you", "business", "general", "sports", "technology", "exit"};
    private static final int VALID_COMMANDS_SIZE = VALID_COMMANDS.length;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        business = (Button) findViewById(R.id.Business);
        general = (Button) findViewById(R.id.General);
        sports = (Button) findViewById(R.id.Sports);
        technology = (Button) findViewById(R.id.Technology);

        business.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent1 = new Intent(MainActivity.this,Business.class);
                startActivity(intent1);
            }
        });

        general.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent2 = new Intent(MainActivity.this,General.class);
                startActivity(intent2);
            }
        });

        sports.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent3 = new Intent(MainActivity.this,Sports.class);
                startActivity(intent3);
            }
        });

        technology.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent4 = new Intent(MainActivity.this,Technology.class);
                startActivity(intent4);
            }
        });
        Intent checkTTSintent = new Intent();
        checkTTSintent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSintent, MY_DATA_CHECK_CODE);
    }


    @Override
    public void onStart()
    {
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
        SpeechListener mRecognitionListener = new SpeechListener();
        mSpeechRecognizer.setRecognitionListener(mRecognitionListener);
        mSpeechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"com.example.nikhithadurvasula.news");
        mSpeechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 20);
        mSpeechIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, TAG);
        this.mWakeLock.acquire();
        mSpeechRecognizer.startListening(mSpeechIntent);
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return false;
    }

    public String getResponse(int command)
    {
        String retString =  "I'm sorry. I'm afraid I can't do that.";
        switch (command)
        {
            case 0:
                retString = "My name is H.F.N. - Hands Free News";
                break;
            case 1:
                Intent intent1 = new Intent(MainActivity.this,Business.class);
                startActivity(intent1);
                break;
            case 2:
                Intent intent2 = new Intent(MainActivity.this,General.class);
                startActivity(intent2);
                break;
            case 3:
                Intent intent3 = new Intent(MainActivity.this,Sports.class);
                startActivity(intent3);
                break;
            case 4:
                Intent intent4 = new Intent(MainActivity.this,Technology.class);
                startActivity(intent4);
            case 5:
                killCommanded = true;
                break;

            default:
                break;
        }
        return retString;
    }

    @Override
    public void onPause()
    {
        if(mSpeechRecognizer != null)
        {
            mSpeechRecognizer.destroy();
            mSpeechRecognizer = null;
        }
        this.mWakeLock.release();
        super.onPause();
    }

    public void processCommand(ArrayList<String> matchStrings)
    {
        String response = "I'm sorry . I'm afraid I can't do that.";
        int maxStrings = matchStrings.size();
        boolean resultFound = false;
        for(int i =0; i < VALID_COMMANDS_SIZE && !resultFound;i++)
        {
            for(int j=0; j < maxStrings && !resultFound; j++)
            {
                if(StringUtils.getLevenshteinDistance(matchStrings.get(j), VALID_COMMANDS[i]) <(VALID_COMMANDS[i].length() / 3) )
                {
                    response = getResponse(i);
                }
            }
        }
        final String finalResponse = response;
        mHandler.post(new Runnable()
        {
            public void run()
            {
                responseText.setText(finalResponse);
            }
        });

    }

    class SpeechListener implements RecognitionListener
    {
        public void onBufferReceived(byte[] buffer)
        {
            Log.d(TAG, "buffer recieved ");
        }
        public void onError(int error)
        {
            if(error == SpeechRecognizer.ERROR_CLIENT || error == SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS)
            {
                Log.d(TAG, "client error");
            }
            else
            {
                Log.d(TAG, "other error");
                mSpeechRecognizer.startListening(mSpeechIntent);
            }
        }
        public void onEvent(int eventType, Bundle params)
        {
            Log.d(TAG, "onEvent");
        }
        public void onPartialResults(Bundle partialResults)
        {
            Log.d(TAG, "partial results");
        }
        public void onReadyForSpeech(Bundle params)
        {
            Log.d(TAG, "on ready for speech");
        }
        public void onResults(Bundle results)
        {
            Log.d(TAG, "on results");
            ArrayList<String> matches = null;
            if(results != null)
            {
                matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if(matches != null)
                {
                    Log.d(TAG, "results are " + matches.toString());
                    final ArrayList<String> matchesStrings = matches;
                    processCommand(matchesStrings);
                    if(!killCommanded)
                        mSpeechRecognizer.startListening(mSpeechIntent);
                    else
                        finish();
                }
            }
        }
        public void onRmsChanged(float rmsdB)
        {
            //Log.d(TAG, "rms changed");
        }
        public void onBeginningOfSpeech()
        {
            Log.d(TAG, "speach begining");
        }
        public void onEndOfSpeech()
        {
            Log.d(TAG, "speach done");
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == MY_DATA_CHECK_CODE)
        {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS)
            {
                myTTS = new TextToSpeech(this, this);
            }
            else
            {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    private void speakWords(String speech)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            myTTS.speak(speech, TextToSpeech.QUEUE_ADD, null, null);
        }
        else
        {
            myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
    public void onInit(int initStatus)
    {
        if (initStatus == TextToSpeech.SUCCESS)
        {
            myTTS.setLanguage(Locale.US);
        }
        else if (initStatus == TextToSpeech.ERROR)
        {
            Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }
        myTTS.speak("Hello welcome to Hands free news.Please select your category of choice", TextToSpeech.QUEUE_ADD, null, null);
        myTTS.playSilentUtterance(1000,TextToSpeech.QUEUE_ADD , null);
    }
}