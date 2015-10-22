package com.technocracy.app.aavartan.activity;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.technocracy.app.aavartan.gallery.GalleryMainActivity;
import com.technocracy.app.aavartan.model.ConnectionDetector;
import com.technocracy.app.aavartan.model.FragmentDrawer;
import com.technocracy.app.aavartan.onlyIntent.AboutUSActivity;
import com.technocracy.app.aavartan.onlyIntent.Workshop_Detail;
import com.technocracy.app.aavartan.R;
import com.technocracy.app.aavartan.adapter.ServiceHandler;
import com.technocracy.app.aavartan.login.SQLiteHandler;
import com.technocracy.app.aavartan.login.SessionManager;
import com.technocracy.app.aavartan.onlyIntent.DetailSponsContacts;
import com.technocracy.app.aavartan.onlyIntent.MapsActivity;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorkshopsLecturesActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener, OnMenuItemClickListener, OnMenuItemLongClickListener {

    String[] workshop;
    String[] type;
    String[] description;
    public static String workshophead;
    public static String workshopdesc;
    ListView listView;
    /*int[] images = {R.drawable.avartan_logo_background_100,R.drawable.avartan_logo_background_100,
            R.drawable.avartan_logo_background_100,R.drawable.avartan_logo_background_100,
            R.drawable.avartan_logo_background_100,R.drawable.avartan_logo_background_100,
            R.drawable.avartan_logo_background_100};*/
    int images=R.drawable.avartan_logo_background_100;
    private ProgressDialog pDialog;
    private static String url = "http://aavartan.org/aavartanapp2015/workshop.php";
    private static final String TAG_WORKSHOP = "workshop";
    private static final String TAG_TITLE = "title";
    private static final String TAG_TYPE = "type";
    private static final String TAG_DESCRIPTION = "description";
    public MenuItem loginlogout;
    JSONArray workshops = null;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    private SQLiteHandler db;
    private SessionManager session;
    private TextView LoginStatus;

    //**********For Context Menu
    private DialogFragment mMenuDialogFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workshops_lectures);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //**********for Context Menu
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fragmentManager = getSupportFragmentManager();
        initMenuFragment();

        LoginStatus = (TextView) findViewById(R.id.usernameTexView);
        db = new SQLiteHandler(getApplicationContext());
        // session manager
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            LoginStatus.setText("User not Logged in.");
            logoutUser();
        }
        else{
            // Fetching user details from sqlite
            HashMap<String, String> user = db.getUserDetails();
            String name = user.get("username");
            LoginStatus.setText("Logged in as : " + name);
            // Displaying the user details on the screen
            // Logout button click event
        }

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        listView= (ListView) findViewById(R.id.listView1);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            // Internet Connection is Present
            new GetWorkshops().execute();
        } else {
            // Internet connection is not present
            Toast.makeText(getApplicationContext(),"Internet Connection not present!",Toast.LENGTH_LONG).show();
        }
    }

    //***********for Context Menu
    //Change Activities in Intent
    @Override
    public void onMenuItemClick(View view, int position) {
        switch (position){
            case 1:
                HomeActivity.LinkToPage = "http://aavartan.org/sponsors2.php";
                HomeActivity.TitleDetailActivity = "Our Sponsors";
                Intent i1 = new Intent(WorkshopsLecturesActivity.this, DetailSponsContacts.class);
                startActivity(i1);
                finish();
                break;
            case 2:
                Intent i2 = new Intent(WorkshopsLecturesActivity.this, AboutUSActivity.class);
                startActivity(i2);
                finish();
                break;
            case 3:
                HomeActivity.LinkToPage = "http://aavartan.org/contact-us2.php";
                HomeActivity.TitleDetailActivity = "Our Team";
                Intent i3 = new Intent(WorkshopsLecturesActivity.this, DetailSponsContacts.class);
                startActivity(i3);
                finish();
                break;
            case 4:
                Intent i4 = new Intent(WorkshopsLecturesActivity.this, MapsActivity.class);
                startActivity(i4);
                finish();
                break;
            case 5:
                Intent i5 = new Intent(WorkshopsLecturesActivity.this, GalleryMainActivity.class);
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
                Intent i1 = new Intent(WorkshopsLecturesActivity.this, DetailSponsContacts.class);
                startActivity(i1);
                finish();
                break;
            case 2:
                Intent i2 = new Intent(WorkshopsLecturesActivity.this, AboutUSActivity.class);
                startActivity(i2);
                finish();
                break;
            case 3:
                HomeActivity.LinkToPage = "http://aavartan.org/contact-us2.php";
                HomeActivity.TitleDetailActivity = "Our Team";
                Intent i3 = new Intent(WorkshopsLecturesActivity.this, DetailSponsContacts.class);
                startActivity(i3);
                finish();
                break;
            case 4:
                Intent i4 = new Intent(WorkshopsLecturesActivity.this, MapsActivity.class);
                startActivity(i4);
                finish();
                break;
            case 5:
                Intent i5 = new Intent(WorkshopsLecturesActivity.this, GalleryMainActivity.class);
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
                    Intent toLogin = new Intent(WorkshopsLecturesActivity.this,LoginActivity.class);
                    startActivity(toLogin);
                    finish();
                }
                else {
                    LoginStatus.setText("User not Logged in.");
                    Toast.makeText(getApplicationContext(), "You have been Logged Out.", Toast.LENGTH_LONG).show();
                    loginlogout.setIcon(R.drawable.context_logout_red);
                    logoutUser();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //**********Procedure to add Context Menu Completed!

    private void logoutUser() {
        session.setLogin(false);
        db.deleteUsers();
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        switch (position) {
            case 0:
                finish();
                break;
            case 1:
                Intent intent1 = new Intent(WorkshopsLecturesActivity.this,EventsActivity.class);
                startActivity(intent1);
                finish();
                break;
            case 2:
                if (!session.isLoggedIn()) {
                    Toast.makeText(getApplicationContext(),"Please Login to enter this section",Toast.LENGTH_LONG).show();
                } else {
                    Intent intent2 = new Intent(WorkshopsLecturesActivity.this, RegisterActivity.class);
                    startActivity(intent2);
                    finish();
                }
                break;
            case 3:
                Intent intent3 = new Intent(WorkshopsLecturesActivity.this,ScheduleActivity.class);
                startActivity(intent3);
                finish();
                break;
            case 4:
                Intent intent4 = new Intent(WorkshopsLecturesActivity.this,VigyaanActivity.class);
                startActivity(intent4);
                finish();
                break;
            case 5:
                break;
            case 6:
                Intent intent6 = new Intent(WorkshopsLecturesActivity.this,TechShowsActivity.class);
                startActivity(intent6);
                finish();
                break;
            case 7:
                Intent intent5 = new Intent(WorkshopsLecturesActivity.this,ESummitActivity.class);
                startActivity(intent5);
                finish();
                break;
            default:
                break;
        }
    }

    private class GetWorkshops extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(WorkshopsLecturesActivity.this);
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
                    workshops = jsonObj.getJSONArray(TAG_WORKSHOP);

                    // looping through All Contacts
                    int len=workshops.length();
                    workshop = new String[len];
                    type = new String[len];
                    description = new String[len];

                    for (int i = 0; i < workshops.length(); i++) {
                        JSONObject c = workshops.getJSONObject(i);

                        String title = c.getString(TAG_TITLE);
                        String typeofWorksop = c.getString(TAG_TYPE);
                        String desc = c.getString(TAG_DESCRIPTION);

                        workshop[i] = title;
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
            ListAdapter adapter=new ListAdapter(WorkshopsLecturesActivity.this,workshop,images,type);
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
            super(myC, R.layout.single_row, R.id.ListHead,workshop);
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
                    workshophead = EventArray[position];
                    workshopdesc = description[position];
                    Intent intent = new Intent(WorkshopsLecturesActivity.this, Workshop_Detail.class);
                    startActivity(intent);
                }
            });

            return row;
        }
    }
}
