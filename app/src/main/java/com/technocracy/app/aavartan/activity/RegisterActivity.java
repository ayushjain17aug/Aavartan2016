package com.technocracy.app.aavartan.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.technocracy.app.aavartan.gallery.GalleryMainActivity;
import com.technocracy.app.aavartan.R;
import com.technocracy.app.aavartan.adapter.ServiceHandler;
import com.technocracy.app.aavartan.login.SQLiteHandler;
import com.technocracy.app.aavartan.login.SessionManager;
import com.technocracy.app.aavartan.model.FragmentDrawer;
import com.technocracy.app.aavartan.model.JSONParser;
import com.technocracy.app.aavartan.model.RegisterEventCheckboxes;
import com.technocracy.app.aavartan.onlyIntent.AboutUSActivity;
import com.technocracy.app.aavartan.onlyIntent.DetailSponsContacts;
import com.technocracy.app.aavartan.onlyIntent.MapsActivity;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import net.steamcrafted.loadtoast.LoadToast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RegisterActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener, OnItemSelectedListener, OnMenuItemClickListener, OnMenuItemLongClickListener {

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private static String EventsUrl;
    private static String TAG_EVENTS;
    private static String TAG_TITLE;
    JSONArray EventTitlesJson = null;
    String[] eventTitles;
    LoadToast lt;
    MyCustomAdapter dataAdapter = null;
    String item;
    ListView listView;
    LoadToast lt2;
    String user_name;
    int[] selectedEventsPositions;
    int success;
    JSONParser jsonParser = new JSONParser();
    private static String RegisterMegaURL = "http://aavartan.org/aavartanapp2015/register_api/EnterEntryMega.php";
    private static String RegisterChallengesURL = "http://aavartan.org/aavartanapp2015/register_api/EnterEntryChallenges.php";
    private static String RegisterKnowledgeURL = "http://aavartan.org/aavartanapp2015/egister_api/EnterEntryKnowledge.php";
    private static final String TAG_SUCCESS = "success";
    private Button myButton;

    private SQLiteHandler db;
    private SessionManager session;
    private TextView LoginStatus;

    //**********For Context Menu
    private DialogFragment mMenuDialogFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        myButton = (Button) findViewById(R.id.submitButton);
        myButton.setVisibility(View.GONE);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // SqLite database handler
        LoginStatus = (TextView) findViewById(R.id.usernameTexView);
        db = new SQLiteHandler(getApplicationContext());
        // session manager
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }
        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();
        user_name = user.get("username");
        LoginStatus.setText("Logged in as : " + user_name);

        //**********for Context Menu
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fragmentManager = getSupportFragmentManager();
        initMenuFragment();

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        Spinner spinner = (Spinner) findViewById(R.id.type_spinner);
        spinner.setOnItemSelectedListener(RegisterActivity.this);

        List<String> categories = new ArrayList<String>();
        categories.add("Select Event Category :");
        categories.add("Mega Events");
        categories.add("Challenges");
        categories.add("Knowledge Boosters");

        listView = (ListView) findViewById(R.id.ListEvents);

        lt = new LoadToast(RegisterActivity.this);
        Display display = getWindowManager().getDefaultDisplay();
        int height = display.getHeight();
        lt.setTranslationY(height - 300);
        lt.setTextColor(Color.YELLOW).setBackgroundColor(0xFF5C6BC0).setProgressColor(Color.RED);

        lt2 = new LoadToast(RegisterActivity.this);
        lt2.setTranslationY(height - 300);
        lt2.setText("Registering...");
        lt2.setTextColor(Color.YELLOW).setBackgroundColor(0xFF5C6BC0).setProgressColor(Color.RED);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        checkButtonClick();
    }

    //***********for Context Menu
    //Change Activities in Intent
    @Override
    public void onMenuItemClick(View view, int position) {
        switch (position){
            case 1:
                HomeActivity.LinkToPage = "http://aavartan.org/sponsors2.php";
                HomeActivity.TitleDetailActivity = "Our Sponsors";
                Intent i1 = new Intent(RegisterActivity.this, DetailSponsContacts.class);
                startActivity(i1);
                finish();
                break;
            case 2:
                Intent i2 = new Intent(RegisterActivity.this, AboutUSActivity.class);
                startActivity(i2);
                finish();
                break;
            case 3:
                HomeActivity.LinkToPage = "http://aavartan.org/contact-us2.php";
                HomeActivity.TitleDetailActivity = "Our Team";
                Intent i3 = new Intent(RegisterActivity.this, DetailSponsContacts.class);
                startActivity(i3);
                finish();
                break;
            case 4:
                Intent i4 = new Intent(RegisterActivity.this, MapsActivity.class);
                startActivity(i4);
                finish();
                break;
            case 5:
                Intent i5 = new Intent(RegisterActivity.this, GalleryMainActivity.class);
                startActivity(i5);
                finish();
                break;
        }
    }
    //Change Activities in Intent
    @Override
    public void onMenuItemLongClick(View view, int position) {
        switch (position){
            case 1:
                HomeActivity.LinkToPage = "http://aavartan.org/sponsors2.php";
                HomeActivity.TitleDetailActivity = "Our Sponsors";
                Intent i1 = new Intent(RegisterActivity.this, DetailSponsContacts.class);
                startActivity(i1);
                finish();
                break;
            case 2:
                Intent i2 = new Intent(RegisterActivity.this, AboutUSActivity.class);
                startActivity(i2);
                finish();
                break;
            case 3:
                HomeActivity.LinkToPage = "http://aavartan.org/contact-us2.php";
                HomeActivity.TitleDetailActivity = "Our Team";
                Intent i3 = new Intent(RegisterActivity.this, DetailSponsContacts.class);
                startActivity(i3);
                finish();
                break;
            case 4:
                Intent i4 = new Intent(RegisterActivity.this, MapsActivity.class);
                startActivity(i4);
                finish();
                break;
            case 5:
                Intent i5 = new Intent(RegisterActivity.this, GalleryMainActivity.class);
                startActivity(i5);
                finish();
                break;
        }
    }
    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(false);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
    }
    private List<MenuObject> getMenuObjects() {

        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject();
        close.setResource(R.drawable.context_close);

        MenuObject spons = new MenuObject("Our Sponsors");
        spons.setResource(R.drawable.context_spons);

        MenuObject aboutUS = new MenuObject("About Us");
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.context_aboutus);
        aboutUS.setBitmap(b);

        MenuObject contact = new MenuObject("Contact Us");
        contact.setResource(R.drawable.context_contactus);

        MenuObject reachUS = new MenuObject("Reach Us");
        reachUS.setResource(R.drawable.context_reach_us);

        MenuObject gallery = new MenuObject("Gallery");
        gallery.setResource(R.drawable.context_gallery);

        menuObjects.add(close);
        menuObjects.add(spons);
        menuObjects.add(aboutUS);
        menuObjects.add(contact);
        menuObjects.add(reachUS);
        menuObjects.add(gallery);
        return menuObjects;
    }
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.context_menu:
                if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //**********Procedure to add Context Menu Completed!

    boolean isNetworkConnected(){
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo()!=null);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        item = parent.getItemAtPosition(position).toString();

        ArrayList<RegisterEventCheckboxes> emptyList = new ArrayList<>();
        MyCustomAdapter emptyAdapter = new MyCustomAdapter(getApplicationContext(), R.layout.register_event_list_item, emptyList);

        if (!item.equals("Select Event Category :")) {
            switch (position) {
                case 1:
                    listView.setAdapter(emptyAdapter);
                    if(!isNetworkConnected()){
                        Toast.makeText(getBaseContext(), "Please connect to the internet!", Toast.LENGTH_LONG).show();
                    }else {
                    EventsUrl = "http://aavartan.org/aavartanapp2015/MegaEvents.php";
                    TAG_EVENTS = "megaEvents";
                    TAG_TITLE = "event";
                    lt.setText("Fetching " + item);
                    new GetEventTitles().execute();
                    }
                    break;
                case 2:
                    listView.setVisibility(View.GONE);
                    listView.setAdapter(emptyAdapter);
                    if(!isNetworkConnected()){
                        Toast.makeText(getBaseContext(), "Please connect to the internet!", Toast.LENGTH_LONG).show();
                    }else {
                    EventsUrl = "http://aavartan.org/aavartanapp2015/Challenges.php";
                    TAG_EVENTS = "challenges";
                    TAG_TITLE = "event";
                    lt.setText("Fetching " + item);
                    new GetEventTitles().execute();
                    }
                    break;
                case 3:
                    listView.setAdapter(emptyAdapter);
                    if(!isNetworkConnected()){
                        Toast.makeText(getBaseContext(), "Please connect to the internet!", Toast.LENGTH_LONG).show();
                    }else {
                    EventsUrl = "http://aavartan.org/aavartanapp2015/KnowledgeBoosters.php";
                    TAG_EVENTS = "knowledgeBoosters";
                    TAG_TITLE = "event";
                    lt.setText("Fetching " + item);
                    new GetEventTitles().execute();
                    }
                    break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private class GetEventTitles extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            lt.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(EventsUrl, ServiceHandler.GET);
            Log.d("Response: ", "> " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    EventTitlesJson = jsonObj.getJSONArray(TAG_EVENTS);

                    // looping through All Contacts
                    int len = EventTitlesJson.length();
                    eventTitles = new String[len];

                    for (int i = 0; i < EventTitlesJson.length(); i++) {
                        JSONObject c = EventTitlesJson.getJSONObject(i);

                        String title = c.getString(TAG_TITLE);

                        eventTitles[i] = title;

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                lt.error();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            ArrayList<RegisterEventCheckboxes> eventList = new ArrayList<RegisterEventCheckboxes>();

                for (int i = 0; i < eventTitles.length; i++) {
                    RegisterEventCheckboxes registerEventCheckboxes = new RegisterEventCheckboxes(eventTitles[i], false);
                    eventList.add(registerEventCheckboxes);
                }

                lt.success();
                myButton.setVisibility(View.VISIBLE);
                dataAdapter = new MyCustomAdapter(getApplicationContext(), R.layout.register_event_list_item, eventList);
                listView.setAdapter(dataAdapter);
                listView.setVisibility(View.VISIBLE);
                    /*listView.setOnItemClickListener(new OnItemClickListener() {public void onItemClick(AdapterView<?> parent, View view,int position, long id) {// When clicked, show a toast with the TextView text
                    RegisterEventCheckboxes registerEventCheckboxes = (RegisterEventCheckboxes) parent.getItemAtPosition(position);
                    Toast.makeText(getApplicationContext(),"Clicked on Row: " + registerEventCheckboxes.getName(),Toast.LENGTH_LONG).show();}});*/
        }
    }

    private class MyCustomAdapter extends ArrayAdapter<RegisterEventCheckboxes> {
        private ArrayList<RegisterEventCheckboxes> eventList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<RegisterEventCheckboxes> countryList) {
            super(context, textViewResourceId, countryList);
            this.eventList = new ArrayList<RegisterEventCheckboxes>();
            this.eventList.addAll(countryList);
        }

        private class ViewHolder {
            CheckBox name;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.register_event_list_item, null);

                holder = new ViewHolder();
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.name.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        RegisterEventCheckboxes country = (RegisterEventCheckboxes) cb.getTag();
                        /*Toast.makeText(getApplicationContext(),"Clicked on Checkbox: " + cb.getText() + " is " + cb.isChecked(),Toast.LENGTH_LONG).show();*/
                        country.setSelected(cb.isChecked());
                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            RegisterEventCheckboxes country = eventList.get(position);
            holder.name.setText(country.getName());
            holder.name.setChecked(country.isSelected());
            holder.name.setTag(country);

            return convertView;
        }
    }

    private void checkButtonClick() {

        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    if(item.equals("Mega Events")){
                        ArrayList<RegisterEventCheckboxes> countryList = dataAdapter.eventList;
                        int size = countryList.size();
                        int flag = 0;
                        selectedEventsPositions = new int[size];
                        for (int i = 0; i < size; i++) {
                            selectedEventsPositions[i]=0;
                            RegisterEventCheckboxes country = countryList.get(i);
                            if (country.isSelected()) {
                                flag = 1;
                                selectedEventsPositions[i] = 1;
                            }
                        }
                        if (!item.isEmpty() && flag == 1) {
                            if (item.equals("Select Event Category :")) {
                                Toast.makeText(getApplicationContext(), "Select a valid category", Toast.LENGTH_LONG).show();
                            }else{
                                new RegisterEntryMega().execute();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),"All fields are mandatory!", Toast.LENGTH_LONG).show();
                        }
                    }else if(item.equals("Challenges")){
                        ArrayList<RegisterEventCheckboxes> countryList = dataAdapter.eventList;
                        int size = countryList.size();
                        int flag = 0;
                        selectedEventsPositions = new int[size];
                        for (int i = 0; i < size; i++) {
                            selectedEventsPositions[i]=0;
                            RegisterEventCheckboxes country = countryList.get(i);
                            if (country.isSelected()) {
                                flag = 1;
                                selectedEventsPositions[i] = 1;
                            }
                        }
                        if (!item.isEmpty() && flag == 1) {
                            if (item.equals("Select Event Category :")) {
                                Toast.makeText(getApplicationContext(), "Select a valid category", Toast.LENGTH_LONG).show();
                            }else{
                                new RegisterEntryChallenges().execute();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),"All fields are mandatory!", Toast.LENGTH_LONG).show();
                        }
                    }else if(item.equals("Knowledge Boosters")){
                        ArrayList<RegisterEventCheckboxes> countryList = dataAdapter.eventList;
                        int size = countryList.size();
                        int flag = 0;
                        selectedEventsPositions = new int[size];
                        for (int i = 0; i < size; i++) {
                            selectedEventsPositions[i]=0;
                            RegisterEventCheckboxes country = countryList.get(i);
                            if (country.isSelected()) {
                                flag = 1;
                                selectedEventsPositions[i] = 1;
                            }
                        }
                        if (!item.isEmpty() && flag == 1) {
                            if (item.equals("Select Event Category :")) {
                                Toast.makeText(getApplicationContext(), "Select a valid category", Toast.LENGTH_LONG).show();
                            }else{
                                new RegisterEntryKnowledge().execute();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),"All fields are mandatory!", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"unknown position selected",Toast.LENGTH_SHORT);
                    }

                } catch (Exception e) {
                    Log.w("Exception", e);
                }
            }
        });
    }

    class RegisterEntryMega extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            lt2.show();
        }
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("user_name", user_name));
            params.add(new BasicNameValuePair("category", item));
            params.add(new BasicNameValuePair("ROBOWAR", String.valueOf(selectedEventsPositions[0])));
            params.add(new BasicNameValuePair("DIABOLIC_DUMPERS", String.valueOf(selectedEventsPositions[1])));
            params.add(new BasicNameValuePair("OCTOBER_SKY", String.valueOf(selectedEventsPositions[2])));
            params.add(new BasicNameValuePair("MAZE_RUNNER", String.valueOf(selectedEventsPositions[3])));
            params.add(new BasicNameValuePair("TECHNOMIC_QUOTIENT", String.valueOf(selectedEventsPositions[4])));
            params.add(new BasicNameValuePair("SHERLOCK_HOLMES", String.valueOf(selectedEventsPositions[5])));
            params.add(new BasicNameValuePair("MATHEMAGIC", String.valueOf(selectedEventsPositions[6])));
            params.add(new BasicNameValuePair("AD_O_HOLIC", String.valueOf(selectedEventsPositions[7])));
            params.add(new BasicNameValuePair("NATIONAL_BUSINESS_CHALLENGE", String.valueOf(selectedEventsPositions[8])));
            params.add(new BasicNameValuePair("CRICNOMETRICA", String.valueOf(selectedEventsPositions[9])));
            params.add(new BasicNameValuePair("CLUEDO", String.valueOf(selectedEventsPositions[10])));
            params.add(new BasicNameValuePair("E_FAIR", String.valueOf(selectedEventsPositions[11])));
            params.add(new BasicNameValuePair("WALLSTREET", String.valueOf(selectedEventsPositions[12])));
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.makeServiceCall(RegisterMegaURL, ServiceHandler.POST, params);
            Log.d("Register Response", jsonStr);
            // check for success tag
            try {
                JSONObject json = new JSONObject(jsonStr);
                success = json.getInt(TAG_SUCCESS);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            if(success == 1){
                lt2.success();
                Toast.makeText(getApplicationContext(), "Registered Successfully",Toast.LENGTH_LONG).show();
            }else{
                lt2.error();
                Toast.makeText(getApplicationContext(),"Error Encountered or you have already registered for all events of this category",Toast.LENGTH_LONG).show();
            }
        }
    }

    class RegisterEntryChallenges extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            lt2.show();
        }
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("user_name", user_name));
            params.add(new BasicNameValuePair("category", item));
            params.add(new BasicNameValuePair("PITCH_N_WALK", String.valueOf(selectedEventsPositions[0])));
            params.add(new BasicNameValuePair("CEO", String.valueOf(selectedEventsPositions[1])));
            params.add(new BasicNameValuePair("ACCELERATOR", String.valueOf(selectedEventsPositions[2])));
            params.add(new BasicNameValuePair("MIRROR_PLAY", String.valueOf(selectedEventsPositions[3])));
            params.add(new BasicNameValuePair("RUSH_HOUR", String.valueOf(selectedEventsPositions[4])));
            params.add(new BasicNameValuePair("LAN_GAMING", String.valueOf(selectedEventsPositions[5])));
            params.add(new BasicNameValuePair("PIPE_O_MANIA", String.valueOf(selectedEventsPositions[6])));
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.makeServiceCall(RegisterChallengesURL, ServiceHandler.POST, params);
            Log.d("Register Response", jsonStr);
            // check for success tag
            try {
                JSONObject json = new JSONObject(jsonStr);
                success = json.getInt(TAG_SUCCESS);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            if(success == 1){
                lt2.success();
                Toast.makeText(getApplicationContext(), "Registered Successfully",Toast.LENGTH_LONG).show();
            }else{
                lt2.error();
                Toast.makeText(getApplicationContext(),"Error Encountered or you have already registered for all events of this category",Toast.LENGTH_LONG).show();
            }
        }
    }

    class RegisterEntryKnowledge extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            lt2.show();
        }
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("user_name", user_name));
            params.add(new BasicNameValuePair("category", item));
            params.add(new BasicNameValuePair("GAME_OF_STAKES", String.valueOf(selectedEventsPositions[0])));
            params.add(new BasicNameValuePair("BRAND_WARZ", String.valueOf(selectedEventsPositions[1])));
            params.add(new BasicNameValuePair("HANGOVER", String.valueOf(selectedEventsPositions[2])));
            params.add(new BasicNameValuePair("CIRCUIT_BIZ", String.valueOf(selectedEventsPositions[3])));
            params.add(new BasicNameValuePair("IDENTIFY_ME", String.valueOf(selectedEventsPositions[4])));
            params.add(new BasicNameValuePair("PRO_CODING", String.valueOf(selectedEventsPositions[5])));
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.makeServiceCall(RegisterKnowledgeURL, ServiceHandler.POST, params);
            Log.d("Register Response", jsonStr);
            // check for success tag
            try {
                JSONObject json = new JSONObject(jsonStr);
                success = json.getInt(TAG_SUCCESS);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            if(success == 1){
                lt2.success();
                Toast.makeText(getApplicationContext(), "Registered Successfully",Toast.LENGTH_LONG).show();
            }else{
                lt2.error();
                Toast.makeText(getApplicationContext(),"Error Encountered or you have already registered for all events of this category",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        switch (position) {
            case 0:
                finish();
                break;
            case 1:
                Intent intent1 = new Intent(RegisterActivity.this, EventsActivity.class);
                startActivity(intent1);
                finish();
                break;
            case 2:
                break;
            case 3:
                Intent intent3 = new Intent(RegisterActivity.this, ScheduleActivity.class);
                startActivity(intent3);
                finish();
                break;
            case 4:
                Intent intent4 = new Intent(RegisterActivity.this, VigyaanActivity.class);
                startActivity(intent4);
                finish();
                break;
            case 5:
                Intent intent5 = new Intent(RegisterActivity.this, WorkshopsLecturesActivity.class);
                startActivity(intent5);
                finish();
                break;
            case 6:
                Intent intent6 = new Intent(RegisterActivity.this, TechShowsActivity.class);
                startActivity(intent6);
                finish();
                break;
            case 7:
                Intent intent2 = new Intent(RegisterActivity.this, ESummitActivity.class);
                startActivity(intent2);
                finish();
                break;
            default:
                break;
        }
    }


    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();
        // Launching the login activity
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
