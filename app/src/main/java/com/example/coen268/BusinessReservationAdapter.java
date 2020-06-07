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
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class BusinessReservationAdapter extends ArrayAdapter<String> {
    Reservation m_reservation;
    List<String> m_userNames;
    String m_key;
    FragmentActivity m_Context;
    DatabaseReference m_databaseReservation;

    public BusinessReservationAdapter(FragmentActivity context){
        super(context, R.layout.listitem_biz_reservation);
        this.m_Context = context;
        m_databaseReservation = FirebaseDatabase.getInstance().getReference("Reservations");
    }

    @Override
    public int getCount(){
        if(m_reservation == null || m_reservation.getUser_ids() == null){
           return 0;
        }
        return m_reservation.getUser_ids().size();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) m_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = mInflater.inflate(R.layout.listitem_biz_reservation, parent,false);
        TextView User_ids_textView = (TextView)convertView.findViewById(R.id.textView_name);
        final Button btn_accept = (Button)convertView.findViewById(R.id.btn_accept);
        User_ids_textView.setText(m_userNames.get(position));

        btn_accept.setEnabled(true);
        btn_accept.setText("ACCEPT");

        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_accept.setEnabled(false);
                btn_accept.setText("Processing...");

                System.out.println("=====INSIDE====\nposition: " + position);
                for (String s : m_reservation.getUser_ids()) {
                    System.out.println("str :" + s);
                }
                m_reservation.getUser_ids().remove(position);
                m_databaseReservation.child(m_key).setValue(m_reservation);

            }
        });
        return convertView;
    }

    public void setReservation(String key, Reservation reservation, List<String> userNames){
        m_key = key;
        m_reservation = reservation;
        m_userNames = userNames;
    }

}
