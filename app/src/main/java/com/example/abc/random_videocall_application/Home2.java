package com.example.abc.random_videocall_application;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abc.random_videocall_application.VideoClasses.ErrorUtils;
import com.example.abc.random_videocall_application.VideoClasses.LogOutClass;
import com.example.abc.random_videocall_application.VideoClasses.SharedPrefsHelper;
import com.example.abc.random_videocall_application.VideoClasses.Toaster;
import com.example.abc.random_videocall_application.VideoClasses.activities.CallActivity;
import com.example.abc.random_videocall_application.VideoClasses.activities.PermissionsActivity;
import com.example.abc.random_videocall_application.VideoClasses.db.QbUsersDbManager;
import com.example.abc.random_videocall_application.VideoClasses.services.CallService;
import com.example.abc.random_videocall_application.VideoClasses.util.App;
import com.example.abc.random_videocall_application.VideoClasses.util.QBResRequestExecutor;
import com.example.abc.random_videocall_application.VideoClasses.utils.Consts;
import com.example.abc.random_videocall_application.VideoClasses.utils.PermissionsChecker;
import com.example.abc.random_videocall_application.VideoClasses.utils.PushNotificationSender;
import com.example.abc.random_videocall_application.VideoClasses.utils.WebRtcSessionManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBProvider;
import com.quickblox.auth.session.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.core.server.Performer;
import com.quickblox.customobjects.QBCustomObjects;
import com.quickblox.customobjects.model.QBCustomObject;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;

import java.util.ArrayList;
import java.util.HashMap;

public class Home2 extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private SharedPrefsHelper sharedPrefsHelper;
    private ProgressDialog progressDialog;
    protected QBResRequestExecutor requestExecutor;
    private QbUsersDbManager dbManager;
    ImageView home, newUser, existingUser, chatList, contact, home_icon, home_white, newUser_white, existingUser_white,
            chatList_white, contact_white, logout, randomCall, drawer_imv, logout_imv;
    private ArrayList<QBUser> currentOpponentsList;
    QBUser selectedUser;
    boolean doubleBackToExitPressedOnce = false;
    private PermissionsChecker checker;

    SharedPreferences sharedPreferences;
    boolean isRunForCall;
    SharedPreferences.Editor editor;
    QBUser currentUser;
    TextView user_name_appmenu;
    private WebRtcSessionManager webRtcSessionManager;


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getExtras() != null) {
            isRunForCall = intent.getExtras().getBoolean(Consts.EXTRA_IS_STARTED_FOR_CALL);
            if (isRunForCall && webRtcSessionManager.getCurrentSession() != null) {
                CallActivity.start(Home2.this, true);
            }
        }
    }

    public static void start(Context context, boolean isRunForCall) {
        Intent intent = new Intent(context, Home2.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(Consts.EXTRA_IS_STARTED_FOR_CALL, isRunForCall);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        requestExecutor = App.getInstance().getQbResRequestExecutor();
        dbManager = QbUsersDbManager.getInstance(getApplicationContext());
       // loginSession();
        webRtcSessionManager = WebRtcSessionManager.getInstance(getApplicationContext());
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isRunForCall = extras.getBoolean(Consts.EXTRA_IS_STARTED_FOR_CALL);
        }
        if (isRunForCall && webRtcSessionManager.getCurrentSession() != null) {
            CallActivity.start(Home2.this, true);
        }
        sharedPrefsHelper = SharedPrefsHelper.getInstance();
        checker = new PermissionsChecker(getApplicationContext());
        currentUser = sharedPrefsHelper.getQbUser();

        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean("hasLoggedIn", true);
        editor.commit();

        setAddMob();
        setfooter();
        setOnClicks();
        setOnclicksRandomButton();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();




        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        user_name_appmenu = navigationView.getHeaderView(0).findViewById(R.id.user_name_appmenu);
        setUserName();
    }

    private void setUserName() {

      //  user_name_appmenu.setText("");
        user_name_appmenu.setText(sharedPreferences.getString("PName",""));
    }


    private void setLogout() {
        editor.putBoolean("hasLoggedIn", false);
        editor.commit();
        LogOutClass logOutClass = new LogOutClass(this, sharedPrefsHelper.getQbUser());
        logOutClass.logout();
        Home2.this.finish();

    }



    private void setOnclicksRandomButton() {
        randomCall = findViewById(R.id.randomCall);
        randomCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sharedPrefsHelper.hasQbUser()) {
                    startLoginService(sharedPrefsHelper.getQbUser());
                    randomVideoCallFunctionality();
                    return;
                }

            }
        });
    }

    private void randomVideoCallFunctionality() {

        startLoadUsers();
    }

    protected void startLoginService(QBUser qbUser) {
        CallService.start(this, qbUser);
    }


    private void setfooter() {
        home = findViewById(R.id.home);
        newUser = findViewById(R.id.newUser);
        existingUser = findViewById(R.id.existingUser);
        chatList = findViewById(R.id.chatList);
        contact = findViewById(R.id.contact);

        newUser_white = findViewById(R.id.newUser_white);
        existingUser_white = findViewById(R.id.existingUser_white);
        chatList_white = findViewById(R.id.chatList_white);
        home_white = findViewById(R.id.home_white);
        contact_white = findViewById(R.id.contact_white);
        home.setVisibility(View.GONE);
        home_white.setVisibility(View.VISIBLE);

    }

    private void setOnClicks() {
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Home2.this,Home2.class);
                startActivity(intent);

            }
        });

        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), NewJoined.class);
                startActivity(intent);
            }
        });

        existingUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), list_user_activity.class);
                startActivity(intent);
            }
        });
        chatList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), ChatDialogsActivity.class);
                startActivity(intent);

            }
        });
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), My_Contacts.class);
                startActivity(intent);

            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.home2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            Intent intent = new Intent(getApplicationContext(), Home2.class);
            startActivity(intent);
        } else if (id == R.id.profile) {
            Intent i = new Intent(getApplicationContext(), ProfileView.class);
            startActivity(i);

        } else if (id == R.id.newelyadded) {
            Intent i = new Intent(getApplicationContext(),  NewJoined.class);
            startActivity(i);

        } else if (id == R.id.existingUser) {
            Intent i = new Intent(getApplicationContext(), list_user_activity.class);
            startActivity(i);

        }else if (id == R.id.chat) {
            Intent i = new Intent(getApplicationContext(),  ChatDialogsActivity.class);
            startActivity(i);

        }else if (id == R.id.mycontacts) {
            Intent i = new Intent(getApplicationContext(),  My_Contacts.class);
            startActivity(i);

        }else if (id == R.id.logout) {
            setLogout();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setAddMob() {
        AdView mAdView;
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.

            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
            }
        });
    }

    private void startLoadUsers() {
        showProgressDialog(R.string.dlg_loading_opponents);
        String currentRoomName = SharedPrefsHelper.getInstance().get("chatUser");
        //requestExecutor.loadUsersByTag(currentRoomName, new QBEntityCallback<ArrayList<QBUser>>() {
        QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
        pagedRequestBuilder.setPage(1);
        pagedRequestBuilder.setPerPage(100);

        Bundle params = new Bundle();

        QBUsers.getUsers(pagedRequestBuilder, params).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> result, Bundle params) {
                hideProgressDialog();
                dbManager.saveAllUsers(result, true);
                initUsersList();
            }

            @Override
            public void onError(QBResponseException responseException) {
                hideProgressDialog();
                showErrorSnackbar(R.string.loading_users_error, responseException, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startLoadUsers();
                    }
                });
            }
        });

    }

    protected void showErrorSnackbar(@StringRes int resId, Exception e,
                                     View.OnClickListener clickListener) {
        if (getSnackbarAnchorView() != null) {
            ErrorUtils.showSnackbar(getSnackbarAnchorView(), resId, e,
                    R.string.dlg_retry, clickListener);
        }
    }

    protected View getSnackbarAnchorView() {
        return findViewById(R.id.randomCall);
    }

    private boolean isCurrentOpponentsListActual(ArrayList<QBUser> actualCurrentOpponentsList) {
        boolean equalActual = actualCurrentOpponentsList.retainAll(currentOpponentsList);
        boolean equalCurrent = currentOpponentsList.retainAll(actualCurrentOpponentsList);
        return !equalActual && !equalCurrent;
    }

    private void initUsersList() {
        String interestedIn = sharedPreferences.getString("Interested_In","");
        setIntersetedIds(interestedIn);

    }

    void showProgressDialog(@StringRes int messageId) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);

            // Disable the back button
            DialogInterface.OnKeyListener keyListener = new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    return keyCode == KeyEvent.KEYCODE_BACK;
                }
            };
            progressDialog.setOnKeyListener(keyListener);
        }

        progressDialog.setMessage("Searching available user");

        progressDialog.show();

    }

    private void proceedInitUsersList(ArrayList<String> idsList) {
        ArrayList<QBUser> tempList = new ArrayList<>();
        int i = 0;
        currentOpponentsList = dbManager.getAllUsers();
        Log.d("TAG", "proceedInitUsersList currentOpponentsList= " + currentOpponentsList);
        currentOpponentsList.remove(sharedPrefsHelper.getQbUser());
//       long currentTime = System.currentTimeMillis();
        for (i = 0; i < currentOpponentsList.size(); i++) {
            QBUser user = (QBUser) currentOpponentsList.get(i);
//            long userLastRequestAtTime = user.getLastRequestAt().getTime();
//            if((currentTime - userLastRequestAtTime) > 5*60*1000){
//                // user is offline now
//            }
            if(idsList.isEmpty()) {
                tempList.add(user);
            }else {
                String id = user.getId().toString();
                if(idsList.contains(id)){
                    tempList.add(user);
                }
            }


        }
        i = tempList.size();
        //long userLastRequestAtTime = c.getLastRequestAt().getTime();
        int random = 0 + (int) (Math.random() * ((i - 1) + 1));

        if (i > 0) {
            selectedUser = new QBUser();
            selectedUser = tempList.get(random);
            videoCallfunction();
        }else {
            Toast.makeText(getApplicationContext(), "No user Available", Toast.LENGTH_LONG).show();
        }

    }

    private void setIntersetedIds(String interestedIn) {
        ArrayList<String> idsList = new ArrayList<String>();
        if(interestedIn.equals("")){
            proceedInitUsersList(idsList);
        }else {
            QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
            requestBuilder.eq("Gender", interestedIn);
            Performer<ArrayList<QBCustomObject>> performer = QBCustomObjects.getObjects("Profile", requestBuilder);

            performer.performAsync(new QBEntityCallback<ArrayList<QBCustomObject>>() {
                @Override
                public void onSuccess(ArrayList<QBCustomObject> qbCustomObjects, Bundle bundle) {
                    if (qbCustomObjects.size() > 0) {
                        for (int i = 0; i < qbCustomObjects.size(); i++) {
                            String id = qbCustomObjects.get(i).getUserId().toString();
                            HashMap<String, Object> fields = qbCustomObjects.get(i).getFields();
                            String gender = fields.get("Gender").toString();
                            if (interestedIn.equalsIgnoreCase(gender)) {
                                idsList.add(id);
                            }
                        }


                    }
                    proceedInitUsersList(idsList);
                }

                @Override
                public void onError(QBResponseException e) {
                    Log.e("TAG", "checking");
                    proceedInitUsersList(idsList);

                }
            });
        }


    }

    void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void videoCallfunction() {
        if (isLoggedInChat()) {
            startCall(true);
        }
        if (checker.lacksPermissions(Consts.PERMISSIONS)) {
            startPermissionsActivity(false);
        }
    }

    private void startPermissionsActivity(boolean checkOnlyAudio) {
        PermissionsActivity.startActivity(this, checkOnlyAudio, Consts.PERMISSIONS);
    }

    private void startCall(boolean isVideoCall) {

        int idValue = selectedUser.getId();
        ArrayList<Integer> opponentsList = new ArrayList<>();
        opponentsList.add(idValue);
        QBRTCTypes.QBConferenceType conferenceType = isVideoCall
                ? QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO
                : QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO;

        QBRTCClient qbrtcClient = QBRTCClient.getInstance(getApplicationContext());

        QBRTCSession newQbRtcSession = qbrtcClient.createNewSessionWithOpponents(opponentsList, conferenceType);

        WebRtcSessionManager.getInstance(this).setCurrentSession(newQbRtcSession);

        PushNotificationSender.sendPushMessage(opponentsList, sharedPrefsHelper.getQbUser().getFullName());

        CallActivity.start(this, false);
    }

    private boolean isLoggedInChat() {
        if (!QBChatService.getInstance().isLoggedIn()) {
            Toaster.shortToast(R.string.dlg_signal_error);
            tryReLoginToChat();
            return false;
        }
        return true;
    }

    private void tryReLoginToChat() {
        if (sharedPrefsHelper.hasQbUser()) {
            QBUser qbUser = sharedPrefsHelper.getQbUser();
            CallService.start(this, qbUser);
        }
    }
    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
           // finish();

            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

    }
}
