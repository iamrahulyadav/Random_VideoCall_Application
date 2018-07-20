package com.example.abc.random_videocall_application;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.login.Login;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignIn extends AppCompatActivity {
    TextView regiterNow_text;
    Button loginBtn;
    EditText userNameEditText,userPasswordEditText;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ProgressDialog dialog;
    CheckBox agree_checkbox,checkBox;
    Map<String, String> header1;
    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        dialog = new ProgressDialog(this);
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userNameEditText = findViewById(R.id.userNameEditText);
        userPasswordEditText = findViewById(R.id.userPasswordEditText);
        agree_checkbox = findViewById(R.id.agree_checkbox);
        checkBox = findViewById(R.id.checkbox);
        userNameEditText.setText(sharedPreferences.getString("userName",""));
        userPasswordEditText.setText(sharedPreferences.getString("password",""));




        setLoginButton();
        setRegisterNow();
    }



    private void setLoginButton() {
        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(checkifFieldsHaveValidValues()) {
                   String email, password;
                   email = userNameEditText.getText().toString().trim();
                   password = userPasswordEditText.getText().toString().trim();
                   if (checkBox.isChecked()) {
                       editor.putString("userName", email);
                       editor.putString("password", password);
                       editor.apply();
                   }

               }
                    loginApiCall();
                }
               // }


        });
        }



    private void setRegisterNow() {
        regiterNow_text = findViewById(R.id.regiterNow_text);
        regiterNow_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), Register_Now.class);
                startActivity(intent);
            }
        });
    }


    public void loginApiCall() {
    RequestQueue requestQueue = Volley.newRequestQueue(this);
    email = userNameEditText.getText().toString().trim();
    password = userPasswordEditText.getText().toString().trim();
    String url =  "http://192.168.31.180:8888/LoginAPI/REST/WebService/login";
    StringRequest jsonObjRequest = new StringRequest(Request.Method.POST,
            url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    dialog.dismiss();
                    Log.e("my app", "123"+response);

                    String str[]=response.split(":");
                    Log.e(str[0],str[1]);
                    //Map<String, String> res = response;
                    if(response != null){
                        try {
                            JSONObject resobj = new JSONObject(response);
                            if(resobj.has("success")) {
                                    String userId = resobj.getString("success");
                                editor.putString("USER_ID",userId);
                                editor.apply();
                                Intent i = new Intent(SignIn.this, Home.class);
                                startActivity(i);
                            }
                            else {
                                showMessage("Error", "Wrong Username or password");
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }



                    }else {
                        showMessage("Error","Wrong Username or password");
                    }


                }
            }, new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            dialog.dismiss();
            Log.e("my app1","error");

        }
    }) {

        @Override
        public String getBodyContentType() {
            return "application/x-www-form-urlencoded; charset=UTF-8";
        }

        @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("emailId", email);
            params.put("password",password);
            return params;
        }

    };
    requestQueue.add(jsonObjRequest);
}




    public void showMessage(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        //builder.set
        builder.setMessage(message);
        //builder.show();
        AlertDialog dialog1 = builder.create();
        dialog1.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Window view = ((AlertDialog)dialog).getWindow();
                view.setBackgroundDrawableResource(R.color.white);
            }
        });
        dialog1.show();

    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
    private boolean checkifFieldsHaveValidValues()
    { String email,password;
        email= userNameEditText.getText().toString().trim();
        password = userPasswordEditText.getText().toString().trim();
        if(email.isEmpty()){
            userNameEditText.setError("Please enter  Username");
            return false;
        }else if (password.isEmpty()){
            userPasswordEditText.setError("Please enter  Password");
            return false;
        }if(!agree_checkbox.isChecked()) {
        agree_checkbox.setError("Please Agree Policies");
        return false;
    }
        return true;
    }
}






