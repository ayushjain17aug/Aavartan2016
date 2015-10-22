package com.technocracy.app.aavartan.onlyIntent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.technocracy.app.aavartan.R;
import com.technocracy.app.aavartan.activity.EventsActivity;
import com.technocracy.app.aavartan.adapter.ServiceHandler;
import com.technocracy.app.aavartan.model.ConnectionDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EventList extends AppCompatActivity {

    String[] events;
    String[] type;
    String[] description;
    public static String eventhead;
    public static String eventdesc;
    ListView listView;
    int images=R.drawable.avartan_logo100;
    private ProgressDialog pDialog;
    private static String TAG_JSONEVENTS;
    private static final String TAG_EVENT = "event";
    private static final String TAG_TYPE = "type";
    private static final String TAG_DESCRIPTION = "description";
    JSONArray workshops = null;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        TAG_JSONEVENTS = EventsActivity.JSON_EVENTS_TAG;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView= (ListView) findViewById(R.id.listView1);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            // Internet Connection is Present
        } else {
            // Internet connection is not present
            Toast.makeText(getApplicationContext(), "Internet Connection not present!", Toast.LENGTH_LONG).show();
        }
        new GetEvents().execute();
    }

    private class GetEvents extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(EventList.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(EventsActivity.eventsUrl, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    workshops = jsonObj.getJSONArray(TAG_JSONEVENTS);

                    // looping through All Contacts
                    int len=workshops.length();
                    events = new String[len];
                    type = new String[len];
                    description = new String[len];

                    for (int i = 0; i < workshops.length(); i++) {
                        JSONObject c = workshops.getJSONObject(i);

                        String title = c.getString(TAG_EVENT);
                        String typeofWorksop = c.getString(TAG_TYPE);
                        String desc = c.getString(TAG_DESCRIPTION);

                        events[i] = title;
                        type[i] = typeofWorksop;
                        description[i]=desc;

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter=new ListAdapter(EventList.this,events,images,type);
            listView.setAdapter(adapter);
        }
    }
    class ListAdapter extends ArrayAdapter<String>
    {
        Context context;
        int ImagesArray;
        String[] EventArray;
        String[] EventTypeArray;
        ListAdapter(Context myC,String[] myevents, int myimgs, String[] type)
        {
            super(myC, R.layout.single_row, R.id.ListHead,events);
            this.context=myC;
            this.ImagesArray=myimgs;
            this.EventArray=myevents;
            this.EventTypeArray=type;
        }

        @Override
        public View getView(final int position,View convertView,ViewGroup parent)
        {
            LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row=inflater.inflate(R.layout.single_row,parent,false);

            ImageView image = (ImageView) row.findViewById(R.id.imageView2);
            final TextView event = (TextView) row.findViewById(R.id.ListHead);
            TextView eventType = (TextView) row.findViewById(R.id.ListDesc);

            image.setImageResource(ImagesArray);
            event.setText(EventArray[position]);
            eventType.setText(EventTypeArray[position]);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventhead = EventArray[position];
                    eventdesc = description[position];
                    if (eventhead.equals("Will be uploaded soon")) {
                        Toast.makeText(getContext(), "Will be uploaded soon.", Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent(EventList.this, EventDetails.class);
                        startActivity(intent);
                    }
                }
            });

            return row;
        }
    }
}
