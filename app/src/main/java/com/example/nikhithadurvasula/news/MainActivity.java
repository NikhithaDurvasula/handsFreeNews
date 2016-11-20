package com.example.nikhithadurvasula.news;

        import android.content.ActivityNotFoundException;
        import android.content.Intent;
        import android.net.Uri;
        import android.speech.RecognizerIntent;
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
        import android.widget.Button;
        import android.widget.Toast;

        import com.android.volley.DefaultRetryPolicy;
        import com.android.volley.Request;
        import com.android.volley.RequestQueue;
        import com.android.volley.Response;
        import com.android.volley.VolleyError;
        import com.android.volley.toolbox.JsonObjectRequest;
        import com.android.volley.toolbox.Volley;

        import com.squareup.picasso.Picasso;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.ArrayList;
        import java.util.List;
        import java.util.Objects;

public class MainActivity extends AppCompatActivity
{
    private List<newsItem> newsFeed = new ArrayList<>();
    public Button business;
    public Button general;
    public Button sports;
    public Button technology;
    public ListView articles;
    public static final int RESULT_SPEECH = 1;
    public TextView txtText;
    public Button btnSpeak;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        business = (Button) findViewById(R.id.Business);
        general = (Button) findViewById(R.id.General);
        sports = (Button) findViewById(R.id.Sports);
        technology = (Button) findViewById(R.id.Technology);
        articles = (ListView) (findViewById(R.id.articles));

        assert articles != null;
        articles.setVisibility(View.GONE);

        ArrayAdapter<newsItem> adapter = new customAdapter();
        articles.setAdapter(adapter);

        txtText = (TextView) findViewById(R.id.txtText);
        btnSpeak = (Button) findViewById(R.id.btnSpeak);

        btnSpeak.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                    txtText.setText("");
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getApplicationContext(),
                            "Opps! Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });

        business.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                businessEngine();
                addClickListener();
            }
        });

        general.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                generalEngine();
                addClickListener();
            }
        });

        sports.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                sportsEngine();
                addClickListener();
            }
        });

        technology.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                technologyEngine();
                addClickListener();
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    txtText.setText(text.get(0));
                    if(Objects.equals(text.get(0), "1"))
                    {
                        businessEngine();
                        addClickListener();
                    }
                    else if(Objects.equals(text.get(0), "2"))
                    {
                        generalEngine();
                        addClickListener();
                    }
                    else if(Objects.equals(text.get(0), "3"))
                    {
                        sportsEngine();
                        addClickListener();
                    }
                    if(Objects.equals(text.get(0), "4"))
                    {
                        technologyEngine();
                        addClickListener();
                    }

                }
                break;
            }

        }
    }

    private void businessEngine()
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.GET,
                "https://newsapi.org/v1/articles?source=cnbc&sortBy=top&apiKey=994884c8d5cf4c8d87825240fbf4d0a1",
                null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        articles.setVisibility(View.VISIBLE);
                        try
                        {
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



    }

    private void generalEngine()
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.GET,
                " https://newsapi.org/v1/articles?source=bbc-news&sortBy=top&apiKey=994884c8d5cf4c8d87825240fbf4d0a1",
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
        ArrayAdapter<newsItem> adapter = new customAdapter();
        ListView articles = (ListView) (findViewById(R.id.articles));
        assert articles != null;
        articles.setAdapter(adapter);
    }
       private void sportsEngine()
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.GET,
                "https://newsapi.org/v1/articles?source=bbc-sport&sortBy=top&apiKey=994884c8d5cf4c8d87825240fbf4d0a1",
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
        ArrayAdapter<newsItem> adapter = new customAdapter();
        ListView articles = (ListView) (findViewById(R.id.articles));
        assert articles != null;
        articles.setAdapter(adapter);
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
        ArrayAdapter<newsItem> adapter = new customAdapter();
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

    private class customAdapter extends ArrayAdapter<newsItem>
    {
        customAdapter()
        {
            super(MainActivity.this, R.layout.item, newsFeed);
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

            Picasso.with(MainActivity.this).load(currentItem.getImageURL()).into(newsImage);

            return convertView;
        }
    }
}