package com.example.patienttracker;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class Fragment_Patient_History extends Fragment {
    //Variables
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mlayoutManager;

    private static final String TAG= "Fragment_Patient_History";
    public static final String firstNameKey = "firstname";
    public static final String lastNameKey = "lastname";
    public static final String phoneKey = "phonenumber";
    public static final String emailKey = "emailaddress";
    private static final SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private String patient_first_name, patient_last_name, patient_phone, patient_email;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionBookingReference = db.collection("Booking");


    //adapter
    private Adapter_Appointment_History_Patient adapter;

    //query
    private Query query;

    public Fragment_Patient_History() {
        // Required empty public constructor
    }


    public static Fragment_Patient_History newInstance(String  Phone) {
        Fragment_Patient_History fragment = new Fragment_Patient_History();
        Bundle args = new Bundle();
        args.putString(phoneKey,Phone);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            patient_first_name = getArguments().getString(firstNameKey);
            patient_last_name = getArguments().getString(lastNameKey);
            patient_phone = getArguments().getString(phoneKey);
            patient_email = getArguments().getString(emailKey);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_patient_history, container, false);
        //for some reason firestore addes 2 hours so add 2 hours here to match the time
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY)+2);
        Date currentDateTime = calendar.getTime();
        query = collectionBookingReference
                .whereEqualTo("patient_documentID", patient_phone)
                .whereLessThan("timestamp",currentDateTime);

        FirestoreRecyclerOptions<Note_Booking> options
                = new FirestoreRecyclerOptions.Builder<Note_Booking>()
                .setQuery(query,Note_Booking.class)
                .build();
        adapter = new Adapter_Appointment_History_Patient(options);

        RecyclerView recyclerView = view.findViewById(R.id.RV_F_Patient_History);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}