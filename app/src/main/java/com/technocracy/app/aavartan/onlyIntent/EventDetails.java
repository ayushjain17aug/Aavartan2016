package com.technocracy.app.aavartan.onlyIntent;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.technocracy.app.aavartan.R;

public class EventDetails extends AppCompatActivity {

    private Toolbar mToolbar;
    TextView Head;
    TextView Desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Head = (TextView) findViewById(R.id.eventsHead);
        Desc = (TextView) findViewById(R.id.eventsDesc);
        Head.setText(EventList.eventhead);
        Desc.setText(EventList.eventdesc);

    }
}
