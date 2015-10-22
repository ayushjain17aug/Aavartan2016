package com.technocracy.app.aavartan.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.TextView;
import android.widget.Toast;

import com.technocracy.app.aavartan.gallery.GalleryMainActivity;
import com.technocracy.app.aavartan.model.FragmentDrawer;
import com.technocracy.app.aavartan.onlyIntent.AboutUSActivity;
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

public class ESummitActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener, OnMenuItemClickListener, OnMenuItemLongClickListener {

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    TextView EsummitHead;
    TextView EsummitDesc;

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
        setContentView(R.layout.activity_esummit);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        EsummitHead = (TextView) findViewById(R.id.esummitHead);
        EsummitDesc = (TextView) findViewById(R.id.esummitDesc);

        String EsummitDescription;
        EsummitDescription = "“Your time is limited, so don’t waste it living someone else’s life. Don’t be trapped by dogma – which is living with the results of other people’s thinking. Don’t let the noise of other’s opinions drown out your own inner voice. And most important, have the courage to follow your heart and intuition. They somehow already know what you truly want to become. Everything else is secondary.” - Steve Jobs\n\nThe nature of being an entrepreneur means that you fully embrace ambiguity and are comfortable with being challenged regularly. Choosing this career path is completely irrational because the odds of succeeding are dismal, but most succeed because of their unwavering belief, laser focus on delivering and persistence.\n\nIt takes optimism,passion,ambition and outstanding leadership qualities to lead a business on a success path.And successful entrepreneurs accomplish these by careful planning , bridging a the gap between one's milieu and ever changing scenario of economic environment,incessantly thinking out of the box to challenge and overcome problems even before they arise.\n\nThese entrepreneurs stand out from the crowd and their experiences are the words of wisdom,treasured by every budding entrepreneur.\n\nThe flagship event of The Entrepreneurship Cell of NIT Raipur,E-Summit is a consortium of India's leading Entrepreneurs and Speakers .E-Summit is a podium for interaction of India's Gen Y with their role-model business personalites.The session invites guest lectures from prominent entrepreneurs who speak about the contributions they have made to the greater good of the society ,their stories of success to motivate students into transforming their dreams into a reality.";

        EsummitHead.setText("E-Summit");
        EsummitDesc.setText(EsummitDescription);

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
        } else {
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

    }

    //***********for Context Menu
    //Change Activities in Intent
    @Override
    public void onMenuItemClick(View view, int position) {
        switch (position) {
            case 1:
                HomeActivity.LinkToPage = "http://aavartan.org/sponsors2.php";
                HomeActivity.TitleDetailActivity = "Our Sponsors";
                Intent i1 = new Intent(ESummitActivity.this, DetailSponsContacts.class);
                startActivity(i1);
                finish();
                break;
            case 2:
                Intent i2 = new Intent(ESummitActivity.this, AboutUSActivity.class);
                startActivity(i2);
                finish();
                break;
            case 3:
                HomeActivity.LinkToPage = "http://aavartan.org/contact-us2.php";
                HomeActivity.TitleDetailActivity = "Our Team";
                Intent i3 = new Intent(ESummitActivity.this, DetailSponsContacts.class);
                startActivity(i3);
                finish();
                break;
            case 4:
                Intent i4 = new Intent(ESummitActivity.this, MapsActivity.class);
                startActivity(i4);
                finish();
                break;
            case 5:
                Intent i5 = new Intent(ESummitActivity.this, GalleryMainActivity.class);
                startActivity(i5);
                finish();
                break;
        }
    }

    //Change Activities in Intent
    @Override
    public void onMenuItemLongClick(View view, int position) {
        switch (position) {
            case 1:
                HomeActivity.LinkToPage = "http://aavartan.org/sponsors2.php";
                HomeActivity.TitleDetailActivity = "Our Sponsors";
                Intent i1 = new Intent(ESummitActivity.this, DetailSponsContacts.class);
                startActivity(i1);
                finish();
                break;
            case 2:
                Intent i2 = new Intent(ESummitActivity.this, AboutUSActivity.class);
                startActivity(i2);
                finish();
                break;
            case 3:
                HomeActivity.LinkToPage = "http://aavartan.org/contact-us2.php";
                HomeActivity.TitleDetailActivity = "Our Team";
                Intent i3 = new Intent(ESummitActivity.this, DetailSponsContacts.class);
                startActivity(i3);
                finish();
                break;
            case 4:
                Intent i4 = new Intent(ESummitActivity.this, MapsActivity.class);
                startActivity(i4);
                finish();
                break;
            case 5:
                Intent i5 = new Intent(ESummitActivity.this, GalleryMainActivity.class);
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
                    Intent toLogin = new Intent(ESummitActivity.this, LoginActivity.class);
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
    //**********Procedure to add Context Menu Completed!.

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
                Intent intent1 = new Intent(ESummitActivity.this, EventsActivity.class);
                startActivity(intent1);
                finish();
                break;
            case 2:
                if (!session.isLoggedIn()) {
                    Toast.makeText(getApplicationContext(),"Please Login to enter this section",Toast.LENGTH_LONG).show();
                } else {
                    Intent intent2 = new Intent(ESummitActivity.this, RegisterActivity.class);
                    startActivity(intent2);
                    finish();
                }
                break;
            case 3:
                Intent intent3 = new Intent(ESummitActivity.this, ScheduleActivity.class);
                startActivity(intent3);
                finish();
                break;
            case 4:
                Intent intent4 = new Intent(ESummitActivity.this, VigyaanActivity.class);
                startActivity(intent4);
                finish();
                break;
            case 5:
                Intent intent5 = new Intent(ESummitActivity.this, WorkshopsLecturesActivity.class);
                startActivity(intent5);
                finish();
                break;
            case 6:
                Intent intent6 = new Intent(ESummitActivity.this, TechShowsActivity.class);
                startActivity(intent6);
                finish();
                break;
            case 7:
                break;
            default:
                break;
        }
    }
}
