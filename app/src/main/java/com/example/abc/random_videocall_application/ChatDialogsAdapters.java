package com.example.abc.random_videocall_application;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.quickblox.chat.model.QBChatDialog;

import java.util.ArrayList;

public class ChatDialogsAdapters extends BaseAdapter{
    private Context context;
    private ArrayList<QBChatDialog> qbChatDialogs;
    public ChatDialogsAdapters(Context context, ArrayList<QBChatDialog> qbChatDialogs)
    {
        this.context=context;
        this.qbChatDialogs=qbChatDialogs;
    }
    @Override
    public int getCount() {
        return qbChatDialogs.size();
    }

    @Override
    public Object getItem(int position) {
        return qbChatDialogs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       View view=convertView;
       if (view==null){
           LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
           view = inflater.inflate(R.layout.list_chat_dialog,null);
           TextView txtTitle,txtMessage;
           ImageView imageView;

           imageView=view.findViewById(R.id.image_chatDialog);
           txtTitle=view.findViewById(R.id.list_chat_dialog_title);
           txtMessage=view.findViewById(R.id.list_chat_dialog_msg);

           ColorGenerator generator=ColorGenerator.MATERIAL;
           int randomColor=generator.getRandomColor();
           TextDrawable .IBuilder builder= (TextDrawable.IBuilder) TextDrawable.builder().beginConfig()
                   .width(4)
                   .endConfig()
                   .round();

           TextDrawable drawable1=builder.build(
                   txtTitle.getText().toString().substring(0,1).toUpperCase(),randomColor);
           imageView.setImageDrawable(drawable1);
       }
       return view;
    }
}
