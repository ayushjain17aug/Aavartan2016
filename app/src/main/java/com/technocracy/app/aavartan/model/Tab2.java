package com.technocracy.app.aavartan.model;

/**
 * Created by nsn on 9/3/2015.
 */

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.technocracy.app.aavartan.R;
import com.technocracy.app.aavartan.activity.ScheduleActivity;
import com.technocracy.app.aavartan.adapter.ServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Tab2 extends Fragment {
    ListView listView;
    private static String url = "http://aavartan.org/aavartanapp2015/ScheduleDay2.php";
    private static final String TAG_SCHEDULE = "schedule";
    private static final String TAG_EVENT = "event";
    private static final String TAG_VENUETIME = "venuetime";
    JSONArray schedule = null;
    ArrayList<HashMap<String, String>> scheduleList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_2,container,false);
        scheduleList = new ArrayList<HashMap<String, String>>();
        listView= (ListView) v.findViewById(R.id.listView3);
        new GetSchedule().execute();
        return v;
    }
    private class GetSchedule extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    schedule = jsonObj.getJSONArray(TAG_SCHEDULE);

                    // looping through All Contacts
                    for (int i = 0; i < schedule.length(); i++) {
                        JSONObject c = schedule.getJSONObject(i);

                        String Event = c.getString(TAG_EVENT);
                        String VenueTime = c.getString(TAG_VENUETIME);

                        // tmp hashmap for single contact
                        HashMap<String, String> notification = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        notification.put(TAG_EVENT, Event);
                        notification.put(TAG_VENUETIME, VenueTime);

                        // adding contact to contact list
                        scheduleList.add(notification);
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
            /**
             * Updating parsed JSON data into ListView
             * */
            android.widget.ListAdapter adapter = new SimpleAdapter(getActivity(),scheduleList, R.layout.single_row,
                    new String[] { TAG_EVENT, TAG_VENUETIME},
                    new int[] { R.id.ListHead, R.id.ListDesc});
            listView.setAdapter(adapter);
            ScheduleActivity.ScheduleLoadToast.success();
        }

    }
}