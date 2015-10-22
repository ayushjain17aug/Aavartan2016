package com.technocracy.app.aavartan.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.technocracy.app.aavartan.login.AppController;
import com.technocracy.app.aavartan.R;
import com.technocracy.app.aavartan.login.SQLiteHandler;
import com.technocracy.app.aavartan.login.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends Activity {
    private static final String TAG = SignUpActivity.class.getSimpleName();
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputName;
    private EditText inputUserName;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputCollege;
    private EditText inputPhone;
    private EditText inputStream;
    private EditText inputYear;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private static String URL_REGISTER = "http://aavartan.org/aavartanapp2015/android_login_api/";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        inputName = (EditText) findViewById(R.id.input_name);
        inputUserName = (EditText) findViewById(R.id.input_username);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        inputCollege = (EditText) findViewById(R.id.input_college);
        inputPhone = (EditText) findViewById(R.id.input_phone);
        inputStream = (EditText) findViewById(R.id.input_stream);
        inputYear = (EditText) findViewById(R.id.input_year);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        // Session manager
        session = new SessionManager(getApplicationContext());
        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());
        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(SignUpActivity.this,
                    HomeActivity.class);
            startActivity(intent);
            finish();
        }

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(!isNetworkConnected()){
                    Toast.makeText(getBaseContext(), "Please connect to the internet!", Toast.LENGTH_LONG).show();
                }else {
                    String name = inputName.getText().toString().trim();
                    String user_name = inputUserName.getText().toString().trim();
                    String email = inputEmail.getText().toString().trim();
                    String password = inputPassword.getText().toString().trim();
                    String college = inputCollege.getText().toString().trim();
                    String phone = inputPhone.getText().toString().trim();
                    String stream = inputStream.getText().toString().trim();
                    String year = inputYear.getText().toString().trim();

                    if (!name.isEmpty() && !user_name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !college.isEmpty() && !phone.isEmpty() && !stream.isEmpty() && !year.isEmpty()) {

                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            Toast.makeText(getApplicationContext(), "Please enter a valid email address.", Toast.LENGTH_LONG).show();
                        } else {
                            if (phone.length() == 10) {
                                registerUser(name, user_name, email, password, college, phone, stream, year);
                            } else {
                                Toast.makeText(getApplicationContext(), "Please enter a 10 digit mobile number.", Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Please enter your details!", Toast.LENGTH_LONG)
                                .show();
                    }
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void registerUser(final String name, final String user_name, final String email, final String password,
                              final String college, final String phone_no, final String stream, final String year) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST, URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Sign Up Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        //JSONObject user = jObj.getJSONObject("user");
                        //String name = user.getString("name");
                        //String user_name = user.getString("user_name");

                        // Inserting row in users table
                        //db.addUser(name, user_name);

                        Toast.makeText(getApplicationContext(), "Registration Successful.", Toast.LENGTH_LONG).show();
                        // Launch login activity
                        Intent intent = new Intent(
                                SignUpActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),"Please try again later.", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "register");
                params.put("name", name);
                params.put("user_name", user_name);
                params.put("email", email);
                params.put("password", password);
                params.put("college", college);
                params.put("phone_no", phone_no);
                params.put("stream", stream);
                params.put("year", year);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    boolean isNetworkConnected(){
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo()!=null);
    }
}