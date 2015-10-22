package com.technocracy.app.aavartan.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.technocracy.app.aavartan.R;
import com.technocracy.app.aavartan.adapter.ServiceHandler;
import com.technocracy.app.aavartan.gallery.GalleryMainActivity;
import com.technocracy.app.aavartan.login.SQLiteHandler;
import com.technocracy.app.aavartan.login.SessionManager;
import com.technocracy.app.aavartan.model.ConnectionDetector;
import com.technocracy.app.aavartan.model.FragmentDrawer;
import com.technocracy.app.aavartan.onlyIntent.AboutUSActivity;
import com.technocracy.app.aavartan.onlyIntent.DetailSponsContacts;
import com.technocracy.app.aavartan.onlyIntent.MapsActivity;
import com.pushbots.push.Pushbots;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;
import com.yalantis.phoenix.PullToRefreshView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class HomeActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener, OnMenuItemClickListener, OnMenuItemLongClickListener {

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    Boolean isInternetPresent = false;
    ConnectionDetector cd;

    private SQLiteHandler db;
    private SessionManager session;
    private TextView LoginStatus;

    private ListView lv;
    private ProgressDialog pDialog;
    private static String url = "http://aavartan.org/aavartanapp2015/updates.php";
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_ID = "id";
    private static final String TAG_NOTIF_NAME = "notif_name";
    private static final String TAG_NOTIF_UPDATE = "notif_upadte";
    private static final String TAG_NOTIF_DATE_TIME = "notif_date_time";
    JSONArray notifications = null;
    ArrayList<HashMap<String, String>> notificationList;
    private PullToRefreshView mPullToRefreshView;
    public static final int REFRESH_DELAY = 1000;
    private DialogFragment mMenuDialogFragment;
    private FragmentManager fragmentManager;
    public static String LinkToPage;
    public static String TitleDetailActivity;
    public MenuItem loginlogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Pushbots.sharedInstance().init(this);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // SqLite database handler
        LoginStatus = (TextView) findViewById(R.id.usernameTexView);
        db = new SQLiteHandler(getApplicationContext());
        // session manager
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            LoginStatus.setText("User not Logged in.");
            logoutUser();
        } else {
            // Fetching user details from sqlite
            HashMap<String, String> user = db.getUserDetails();
            String name = user.get("username");
            LoginStatus.setText("Logged in as : " + name);
            // Displaying the user details on the screen
            // Logout button click event
        }

        try {

            fragmentManager = getSupportFragmentManager();
            initMenuFragment();

        /*getSupportActionBar().setLogo(R.drawable.ic_logo_small);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_logo_small);*/

            drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
            drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
            drawerFragment.setDrawerListener(this);

            notificationList = new ArrayList<HashMap<String, String>>();
            lv = (ListView) findViewById(R.id.updates);

            if (isInternetPresent) {
                // Internet Connection is Present
                new GetNotifications().execute();
            } else {
                // Internet connection is not present
                Toast.makeText(getApplicationContext(), "Internet Connection not present! Connect to Internet and Refresh!", Toast.LENGTH_LONG).show();
            }


            mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
            mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mPullToRefreshView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            notificationList.clear();
                            new GetNotifications().execute();
                        }
                    }, REFRESH_DELAY);
                }
            });
        } catch (Exception e) {
        }
    }

    @Override
    public void onMenuItemClick(View view, int position) {
        switch (position) {
            case 1:
                LinkToPage = "http://aavartan.org/sponsors2.php";
                TitleDetailActivity = "Our Sponsors";
                Intent i1 = new Intent(HomeActivity.this, DetailSponsContacts.class);
                startActivity(i1);
                break;
            case 2:
                Intent i2 = new Intent(HomeActivity.this, AboutUSActivity.class);
                startActivity(i2);
                break;
            case 3:
                LinkToPage = "http://aavartan.org/contact-us2.php";
                TitleDetailActivity = "Our Team";
                Intent i3 = new Intent(HomeActivity.this, DetailSponsContacts.class);
                startActivity(i3);
                break;
            case 4:
                Intent i4 = new Intent(HomeActivity.this, MapsActivity.class);
                startActivity(i4);
                break;
            case 5:
                Intent i5 = new Intent(HomeActivity.this, GalleryMainActivity.class);
                startActivity(i5);
                break;
        }
    }

    @Override
    public void onMenuItemLongClick(View view, int position) {
        switch (position) {
            case 1:
                LinkToPage = "http://aavartan.org/sponsors2.php";
                TitleDetailActivity = "Our Sponsors";
                Intent i1 = new Intent(HomeActivity.this, DetailSponsContacts.class);
                startActivity(i1);
                break;
            case 2:
                Intent i2 = new Intent(HomeActivity.this, AboutUSActivity.class);
                startActivity(i2);
                break;
            case 3:
                LinkToPage = "http://aavartan.org/contact-us2.php";
                TitleDetailActivity = "Our Team";
                Intent i3 = new Intent(HomeActivity.this, DetailSponsContacts.class);
                startActivity(i3);
                break;
            case 4:
                Intent i4 = new Intent(HomeActivity.this, MapsActivity.class);
                startActivity(i4);
                break;
            case 5:
                Intent i5 = new Intent(HomeActivity.this, GalleryMainActivity.class);
                startActivity(i5);
                break;
        }
    }

    private class GetNotifications extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(HomeActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

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
                    notifications = jsonObj.getJSONArray(TAG_NOTIFICATIONS);

                    // looping through All Contacts
                    for (int i = 0; i < notifications.length(); i++) {
                        JSONObject c = notifications.getJSONObject(i);

                        String id = c.getString(TAG_ID);
                        String name = c.getString(TAG_NOTIF_NAME);
                        String update = c.getString(TAG_NOTIF_UPDATE);
                        String date_time = c.getString(TAG_NOTIF_DATE_TIME);


                        // tmp hashmap for single contact
                        HashMap<String, String> notification = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        notification.put(TAG_ID, id);
                        notification.put(TAG_NOTIF_NAME, name);
                        notification.put(TAG_NOTIF_UPDATE, update);
                        notification.put(TAG_NOTIF_DATE_TIME, date_time);

                        // adding contact to contact list
                        notificationList.add(notification);
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
            ListAdapter adapter = new SimpleAdapter(HomeActivity.this, notificationList, R.layout.list_update_item,
                    new String[]{TAG_NOTIF_NAME, TAG_NOTIF_UPDATE, TAG_NOTIF_DATE_TIME},
                    new int[]{R.id.notif_name, R.id.notif_upadte, R.id.notif_date_time});
            lv.setAdapter(adapter);
            mPullToRefreshView.setRefreshing(false);
        }

    }



    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onDrawerItemSelected(View view, int position) {
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                break;
            case 1:
                Intent intent1 = new Intent(HomeActivity.this, EventsActivity.class);
                startActivity(intent1);
                break;
            case 2:
                if (!session.isLoggedIn()) {
                    Toast.makeText(getApplicationContext(),"Please Login to enter this section",Toast.LENGTH_LONG).show();
                } else {
                    Intent intent2 = new Intent(HomeActivity.this, RegisterActivity.class);
                    startActivity(intent2);
                }
                break;
            case 3:
                Intent intent3 = new Intent(HomeActivity.this, ScheduleActivity.class);
                startActivity(intent3);
                break;
            case 4:
                Intent intent4 = new Intent(HomeActivity.this, VigyaanActivity.class);
                startActivity(intent4);
                break;
            case 5:
                Intent intent5 = new Intent(HomeActivity.this, WorkshopsLecturesActivity.class);
                startActivity(intent5);
                break;
            case 6:
                Intent intent6 = new Intent(HomeActivity.this, TechShowsActivity.class);
                startActivity(intent6);
                break;
            case 7:
                Intent intent7 = new Intent(HomeActivity.this, ESummitActivity.class);
                startActivity(intent7);
                break;
            default:
                break;
        }
        getSupportActionBar().setTitle(title);
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

        /*MenuObject addFr = new MenuObject("Add to friends");
        BitmapDrawable bd = new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(), R.drawable.icn_3));
        addFr.setDrawable(bd);*/

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
        inflater.inflate(R.menu.menu_context_and_logout, menu);
        loginlogout = menu.getItem(0);
        if (!session.isLoggedIn()) {
            loginlogout.setIcon(R.drawable.context_logout_red);
        }
        else{
            loginlogout.setIcon(R.drawable.context_logout_green);
        }
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
            case R.id.action_logout:
                if (!session.isLoggedIn()) {
                    Intent toLogin = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(toLogin);
                    finish();
                } else {
                    LoginStatus.setText("User not Logged in.");
                    Toast.makeText(getApplicationContext(), "You have been Logged Out.", Toast.LENGTH_LONG).show();
                    loginlogout.setIcon(R.drawable.context_logout_red);
                    logoutUser();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        session.setLogin(false);
        db.deleteUsers();
        // Launching the login activity
    }
}
