package com.example.nikhithadurvasula.news;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Technology extends AppCompatActivity implements TextToSpeech.OnInitListener
{
    public List<newsItem> newsFeed = new ArrayList<>();
    public ListView articles;
    public int MY_DATA_CHECK_CODE = 0;
    public TextToSpeech myTTS;
    public boolean voicemode;

    private static final String TAG = MainActivity.class.getName();
    protected PowerManager.WakeLock mWakeLock;
    private SpeechRecognizer mSpeechRecognizer;
    private Handler mHandler = new Handler();
    TextView responseText;
    Intent mSpeechIntent;
    boolean killCommanded = false;
    private static final String[] VALID_COMMANDS = {"stop", "change", "exit"};
    private static final int VALID_COMMANDS_SIZE = VALID_COMMANDS.length;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technology);
        articles = (ListView) (findViewById(R.id.articles));

        assert articles != null;
        articles.setVisibility(View.GONE);

        ArrayAdapter<newsItem> adapter = new Technology.customAdapter();
        articles.setAdapter(adapter);

        technologyEngine();
        addClickListener();
        Intent checkTTSintent = new Intent();
        checkTTSintent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSintent, MY_DATA_CHECK_CODE);

    }

    @Override
    public void onStart()
    {
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(Technology.this);
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

    public String getResponse(int command)
    {
        String retString =  "I'm sorry. I'm afraid I can't do that.";
        switch (command)
        {
            case 0:
                killCommanded = true;
                break;

            case 1:
                Intent intent1 = new Intent(Technology.this,MainActivity.class);
                startActivity(intent1);
                finish();
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

    private void technologyEngine()
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.GET,
                "https://newsapi.org/v1/articles?source=ars-technica&sortBy=top&apiKey=994884c8d5cf4c8d87825240fbf4d0a1",
                null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            articles.setVisibility(View.VISIBLE);
                            JSONArray articles = response.getJSONArray("articles");
                            for (int i = 0; i < articles.length(); i++)
                            {
                                JSONObject temp = articles.getJSONObject(i);
                                String author = temp.getString("author");
                                String title = temp.getString("title");
                                String description = temp.getString("description");
                                String url = temp.getString("url");
                                String urlToImage = temp.getString("urlToImage");
                                String publishedAt = temp.getString("publishedAt");
                                newsFeed.add(new newsItem(author, title, description, url, urlToImage, publishedAt));
                                speakWords(title);
                                myTTS.playSilentUtterance(1000,TextToSpeech.QUEUE_ADD , null);
                            }
                        }
                        catch(JSONException e)
                        {
                            Log.e("myTag", e.toString());
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        articles.setVisibility(View.GONE);
                        Log.e("myTag", error.toString());
                    }
                });
        myReq.setRetryPolicy(new DefaultRetryPolicy(
                10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        queue.add(myReq);
        ArrayAdapter<newsItem> adapter = new Technology.customAdapter();
        ListView articles = (ListView) (findViewById(R.id.articles));
        assert articles != null;
        articles.setAdapter(adapter);
    }


    private void addClickListener()
    {
        ListView articles = (ListView) (findViewById(R.id.articles));
        assert articles != null;
        articles.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                newsItem currentItem = newsFeed.get(position);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(currentItem.getUrl()));
                startActivity(i);
            }
        });
    }

    public class customAdapter extends ArrayAdapter<newsItem>
    {
        customAdapter()
        {
            super(Technology.this, R.layout.item, newsFeed);
        }
        @NonNull
        @Override
        public View getView(int position, View convertView,@NonNull ViewGroup parent)
        {

            if(convertView == null)
            {
                convertView = getLayoutInflater().inflate(R.layout.item, parent, false);
            }

            newsItem currentItem = newsFeed.get(position);

            ImageView newsImage = (ImageView) convertView.findViewById(R.id.leftIco);
            TextView author = (TextView) convertView.findViewById(R.id.author);
            TextView heading = (TextView) convertView.findViewById(R.id.heading);
            TextView desc = (TextView) convertView.findViewById(R.id.desc);
            TextView publishedAt = (TextView) convertView.findViewById(R.id.publishedAt);

            author.setText(currentItem.getAuthor());
            heading.setText(currentItem.getNewsHeading());
            desc.setText(currentItem.getNewsDesc());
            publishedAt.setText(currentItem.getPublishedAt());

            Picasso.with(Technology.this).load(currentItem.getImageURL()).into(newsImage);

            return convertView;
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data)
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
        myTTS.playSilentUtterance(1000,TextToSpeech.QUEUE_ADD , null);
        speakthenews();
    }

    private void speakthenews()
    {
        technologyEngine();
    }
}
