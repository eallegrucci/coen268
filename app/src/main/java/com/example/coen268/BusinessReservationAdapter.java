package com.example.coen268;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;


public class BusinessReservationAdapter extends ArrayAdapter<String> {
    ArrayList<String> m_user_ids;
    Button m_accept;
    Context m_Context;
    public BusinessReservationAdapter(Context context, ArrayList<String> user_ids, Button accept){
        super(context, R.layout.listitem_biz_reservation);
        this.m_Context = context;
        this.m_user_ids = user_ids;
        this.m_accept = accept;
    }

    @Override
    public int getCount(){
        return m_user_ids.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) m_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = mInflater.inflate(R.layout.listitem_biz_reservation, parent,false);
        TextView User_ids_textView = (TextView)convertView.findViewById(R.id.textView_name);
        Button btn_accept = (Button)convertView.findViewById(R.id.btn_accept);

        User_ids_textView.setText(m_user_ids.get(position));
        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return convertView;
    }
}
