package com.example.coen268;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerReservationAdapter extends ArrayAdapter<String> {

    private FragmentActivity context;
    private DatabaseReference databaseReservation;
    private List<ReservationDisplay> reservationList;
    private FirebaseUser firebaseUser;
    private FirebaseFunctions m_functions;

    private static final String TAG = "CustomerReservationAdapter";

    public CustomerReservationAdapter(FragmentActivity context) {
        super(context, R.layout.item_customer_reservation);
        this.context = context;
        databaseReservation = FirebaseDatabase.getInstance().getReference("Reservations");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        m_functions = FirebaseFunctions.getInstance();
    }

    @Override
    public int getCount() {
        if (reservationList == null) {
            return 0;
        }
        return reservationList.size();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = mInflater.inflate(R.layout.item_customer_reservation, parent,false);
        TextView textBusiness = (TextView) convertView.findViewById(R.id.textBusiness);
        final Button btnCancel = (Button) convertView.findViewById(R.id.btnCancel);
        textBusiness.setText(reservationList.get(position).getBusinessName());

        btnCancel.setEnabled(true);
        btnCancel.setText("CANCEL");

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCancel.setEnabled(false);
                btnCancel.setText("Processing...");

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setMessage("Are you sure you want to cancel the reservation?")
                        .setTitle("Cancel Reservation")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String dbKey = reservationList.get(position).getDbKey();
                                Reservation reservation = reservationList.get(position).getReservation();
                                int index = reservation.getUser_ids().indexOf(firebaseUser.getUid());
                                reservation.getUser_ids().remove(index);
                                databaseReservation.child(dbKey).setValue(reservation);
                                Toast.makeText(context, "Reservation cancelled successfully!", Toast.LENGTH_LONG);

                                sendReserveNotificationAsync("Cancel", firebaseUser.getDisplayName(), reservation.getBusiness_id());
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                btnCancel.setEnabled(true);
                                btnCancel.setText("CANCEL");
                            }
                        })
                        .show();
            }
        });
        return convertView;
    }

    public void setReservationList(List<ReservationDisplay> reservationList) {
        this.reservationList = reservationList;
    }

    public Task<Boolean> sendReserveNotificationAsync(String command, String clientUsername, String businessId) {
        Map<String, Object> data = new HashMap<>();
        data.put("clientUsername", clientUsername);
        data.put("businessId", businessId);
        data.put("command", command);

        return m_functions
                .getHttpsCallable("sendClientReserveCancelNotification")
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

    public static class ReservationDisplay {
        private Reservation reservation;
        private String businessName;
        private String dbKey;

        public ReservationDisplay(String key, Reservation reservation) {
            dbKey = key;
            this.reservation = reservation;
        }

        public void setBusinessName(String businessName) {
            this.businessName = businessName;
        }

        public String getBusinessName() {
            return businessName;
        }

        public String getDbKey() {
            return dbKey;
        }

        public Reservation getReservation() {
            return reservation;
        }

    }
}
