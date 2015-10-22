package com.technocracy.app.aavartan.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.technocracy.app.aavartan.gallery.GalleryMainActivity;
import com.technocracy.app.aavartan.model.FragmentDrawer;
import com.technocracy.app.aavartan.onlyIntent.AboutUSActivity;
import com.technocracy.app.aavartan.onlyIntent.EventList;
import com.technocracy.app.aavartan.R;
import com.technocracy.app.aavartan.login.SQLiteHandler;
import com.technocracy.app.aavartan.login.SessionManager;
import com.technocracy.app.aavartan.onlyIntent.DetailSponsContacts;
import com.technocracy.app.aavartan.onlyIntent.MapsActivity;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.technocracy.app.aavartan.R.id;
import static com.technocracy.app.aavartan.R.layout;

public class EventsActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener, OnMenuItemClickListener, OnMenuItemLongClickListener {

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    RelativeLayout mega;
    RelativeLayout challenges;
    RelativeLayout knowledge;
    RelativeLayout online;
    public static String eventsUrl;
    Intent intent;
    public static String JSON_EVENTS_TAG;

    private SQLiteHandler db;
    private SessionManager session;
    private TextView LoginStatus;
    public MenuItem loginlogout;

    //**********For Context Menu
    private DialogFragment mMenuDialogFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_events);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        mToolbar = (Toolbar) findViewById(id.toolbar);
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

        mega = (RelativeLayout) findViewById(id.mega);
        challenges = (RelativeLayout) findViewById(id.challenges);
        knowledge = (RelativeLayout) findViewById(id.knowledge);
        online = (RelativeLayout) findViewById(id.online);
        intent = new Intent(EventsActivity.this, EventList.class);
        mega.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(!isNetworkConnected()){
                    Toast.makeText(getBaseContext(), "Please connect to the internet!", Toast.LENGTH_LONG).show();
                }else {
                    eventsUrl = "http://aavartan.org/aavartanapp2015/MegaEvents.php";
                    JSON_EVENTS_TAG = "megaEvents";
                    startActivity(intent);
                }
            }
        });
        challenges.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(!isNetworkConnected()){
                    Toast.makeText(getBaseContext(), "Please connect to the internet!", Toast.LENGTH_LONG).show();
                }else {
                eventsUrl = "http://aavartan.org/aavartanapp2015/Challenges.php";
                JSON_EVENTS_TAG = "challenges";
                startActivity(intent);
                }
            }
        });
        knowledge.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                    if(!isNetworkConnected()){
                        Toast.makeText(getBaseContext(), "Please connect to the internet!", Toast.LENGTH_LONG).show();
                    }else {
                eventsUrl = "http://aavartan.org/aavartanapp2015/KnowledgeBoosters.php";
                JSON_EVENTS_TAG = "knowledgeBoosters";
                startActivity(intent);
                    }
            }
        });
        online.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!isNetworkConnected()){
                    Toast.makeText(getBaseContext(), "Please connect to the internet!", Toast.LENGTH_LONG).show();
                }else {
                eventsUrl = "http://aavartan.org/aavartanapp2015/OnlineEvents.php";
                JSON_EVENTS_TAG = "onlineEvents";
                startActivity(intent);
                }
            }
        });

    }

    //***********for Context Menu
    //Change Activities in Intent
    @Override
    public void onMenuItemClick(View view, int position) {
        switch (position){
            case 1:
                HomeActivity.LinkToPage = "http://aavartan.org/sponsors2.php";
                HomeActivity.TitleDetailActivity = "Our Sponsors";
                Intent i1 = new Intent(EventsActivity.this, DetailSponsContacts.class);
                startActivity(i1);
                finish();
                break;
            case 2:
                Intent i2 = new Intent(EventsActivity.this, AboutUSActivity.class);
                startActivity(i2);
                finish();
                break;
            case 3:
                HomeActivity.LinkToPage = "http://aavartan.org/contact-us2.php";
                HomeActivity.TitleDetailActivity = "Our Team";
                Intent i3 = new Intent(EventsActivity.this, DetailSponsContacts.class);
                startActivity(i3);
                finish();
                break;
            case 4:
                Intent i4 = new Intent(EventsActivity.this, MapsActivity.class);
                startActivity(i4);
                finish();
                break;
            case 5:
                Intent i5 = new Intent(EventsActivity.this, GalleryMainActivity.class);
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
                Intent i1 = new Intent(EventsActivity.this, DetailSponsContacts.class);
                startActivity(i1);
                finish();
                break;
            case 2:
                Intent i2 = new Intent(EventsActivity.this, AboutUSActivity.class);
                startActivity(i2);
                finish();
                break;
            case 3:
                HomeActivity.LinkToPage = "http://aavartan.org/contact-us2.php";
                HomeActivity.TitleDetailActivity = "Our Team";
                Intent i3 = new Intent(EventsActivity.this, DetailSponsContacts.class);
                startActivity(i3);
                finish();
                break;
            case 4:
                Intent i4 = new Intent(EventsActivity.this, MapsActivity.class);
                startActivity(i4);
                finish();
                break;
            case 5:
                Intent i5 = new Intent(EventsActivity.this, GalleryMainActivity.class);
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
                    Intent toLogin = new Intent(EventsActivity.this,LoginActivity.class);
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
                break;
            case 2:
                if (!session.isLoggedIn()) {
                    Toast.makeText(getApplicationContext(),"Please Login to enter this section",Toast.LENGTH_LONG).show();
                } else {
                    Intent intent2 = new Intent(EventsActivity.this, RegisterActivity.class);
                    startActivity(intent2);
                    finish();
                }
                break;
            case 3:
                Intent intent3 = new Intent(EventsActivity.this,ScheduleActivity.class);
                startActivity(intent3);
                finish();
                break;
            case 4:
                Intent intent4 = new Intent(EventsActivity.this,VigyaanActivity.class);
                startActivity(intent4);
                finish();
                break;
            case 5:
                Intent intent5 = new Intent(EventsActivity.this,WorkshopsLecturesActivity.class);
                startActivity(intent5);
                finish();
                break;
            case 6:
                Intent intent6 = new Intent(EventsActivity.this,TechShowsActivity.class);
                startActivity(intent6);
                finish();
                break;
            case 7:
                Intent intent1 = new Intent(EventsActivity.this,ESummitActivity.class);
                startActivity(intent1);
                finish();
                break;
            default:
                break;
        }

    }

    boolean isNetworkConnected(){
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo()!=null);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_events, menu);
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
}
