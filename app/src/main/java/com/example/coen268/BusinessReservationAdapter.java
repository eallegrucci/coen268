package com.example.coen268;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.example.coen268.user.BusinessOwner;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BusinessReservationAdapter extends ArrayAdapter<String> {
    private Reservation m_reservation;
    private List<String> m_userNames;
    private String m_key;
    private FragmentActivity m_Context;
    private DatabaseReference m_databaseReservation;
    private BusinessOwner m_businessOwner;
    private FirebaseFunctions m_functions;
    private static final String TAG = "BusinessReservationAdapter";

    public BusinessReservationAdapter(FragmentActivity context, BusinessOwner businessOwner) {
        super(context, R.layout.listitem_biz_reservation);
        this.m_Context = context;
        m_databaseReservation = FirebaseDatabase.getInstance().getReference("Reservations");
        m_businessOwner = businessOwner;
        m_functions = FirebaseFunctions.getInstance();
    }

    @Override
    public int getCount() {
        if (m_reservation == null || m_reservation.getUser_ids() == null) {
            return 0;
        }
        return m_reservation.getUser_ids().size();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) m_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = mInflater.inflate(R.layout.listitem_biz_reservation, parent, false);
        TextView User_ids_textView = (TextView) convertView.findViewById(R.id.textView_name);
        final Button btn_accept = (Button) convertView.findViewById(R.id.btn_accept);
        User_ids_textView.setText(m_userNames.get(position));

        btn_accept.setEnabled(true);
        btn_accept.setText("ACCEPT");

        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_accept.setEnabled(false);
                btn_accept.setText("Processing...");

                String acceptedClientUid = new String(m_reservation.getUser_ids().get(position));

                m_reservation.getUser_ids().remove(position);
                m_databaseReservation.child(m_key).setValue(m_reservation);

                String bizUserName = m_businessOwner.getDisplayName();
                sendAcceptReservationNotification(bizUserName, acceptedClientUid);
            }
        });
        return convertView;
    }

    public void setReservation(String key, Reservation reservation, List<String> userNames) {
        m_key = key;
        m_reservation = reservation;
        m_userNames = userNames;
    }

    public Task<Boolean> sendAcceptReservationNotification(String bizUserName, String clientUid) {
        Map<String, Object> data = new HashMap<>();
        data.put("bizUserName", bizUserName);
        data.put("clientUid", clientUid);

        return m_functions
                .getHttpsCallable("sendAcceptReservationNotification")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, Boolean>() {
                    @Override
                    public Boolean then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        HttpsCallableResult res = task.getResult();
                        try {
                            HashMap<String, String> result = (HashMap<String, String>)res.getData();
                            if (result.get("result").equals("Success")) {
                                Log.i(TAG, "Message successfully sent!");
                                return true;
                            }
                        } catch (Exception e){
                            Log.e(TAG, e.getLocalizedMessage());
                        }
                        return false;
                    }
                });
    }
}
