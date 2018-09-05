package com.example.abc.random_videocall_application;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.library.bubbleview.BubbleTextView;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.users.QBUsers;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.concurrent.TimeUnit;

public class ChatMessageAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<QBChatMessage> qbChatMessages;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public ChatMessageAdapter(Context context, ArrayList<QBChatMessage> qbChatMessages) {
        this.context = context;
        this.qbChatMessages = qbChatMessages;

    }

    @Override
    public int getCount() {
        return qbChatMessages.size();
    }

    @Override
    public Object getItem(int i) {
        return qbChatMessages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        if (convertView == null){

            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            Log.d("myTag", " " + qbChatMessages.get(i).getSenderId());
            Log.d("myTag", " " + QBChatService.getInstance().getUser().getId());

            if (qbChatMessages.get(i).getSenderId().equals(QBChatService.getInstance().getUser().getId())){

                view = inflater.inflate(R.layout.list_send_message, null);
                BubbleTextView bubbleTextView = (BubbleTextView)view.findViewById(R.id.message_content);
                TextView time=view.findViewById(R.id.time);
                TextView date=view.findViewById(R.id.date);
                long date_value = qbChatMessages.get(i).getDateSent();
                String dateValue = Long.toString(date_value);
                date.setText(dateValue);
                bubbleTextView.setText(qbChatMessages.get(i).getBody());
                long millis=(qbChatMessages.get(i).getDateSent())/1000;
                //long s = millis % 60;
                long m = (millis / 60) % 60;
                long h = (millis / (60 * 60))%24;
                String hms = String.format("%02d:%02d", h,
                        m);

                time.setText(hms);
                time.setTextColor(Color.BLACK);
                Log.e("time", String.valueOf(qbChatMessages.get(i).getDateSent()/1000));

            } else {

                sharedPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                view = inflater.inflate(R.layout.list_rec_message, null);
                BubbleTextView bubbleTextView = (BubbleTextView)view.findViewById(R.id.message_content);
               TextView time=view.findViewById(R.id.time);

                TextView date=view.findViewById(R.id.date);
                long date_value = qbChatMessages.get(i).getDateSent();
                String dateValue = Long.toString(date_value);
                date.setText(dateValue);
                //date.setText((int) qbChatMessages.get(i).getDateSent());

                bubbleTextView.setText(qbChatMessages.get(i).getBody());
                TextView txtName = (TextView)view.findViewById(R.id.message_user);
                txtName.setText(QBUsersHolder.getInstance().getUserById(qbChatMessages.get(i).getSenderId()).getFullName());
                time.setText(""+qbChatMessages.get(i).getDateSent());
                time.setTextColor(Color.BLACK);
                String SenderName= QBUsersHolder.getInstance().getUserById(qbChatMessages.get(i).getSenderId()).getFullName();

                //editor.putString("SenderName",SenderName);
                //editor.commit();
            }
        }
        return view;
    }



}
