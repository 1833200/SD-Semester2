package com.example.patienttracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Activity_Doctor_Forgot extends AppCompatActivity {
    private final String TAG = "Doctor Forgot Password";

    //Variables
    private String doctor_email;
    private String doctorDocumentID;

    private Note_Doctor note_doctor;

    //Widgets
    private TextInputLayout il_email;
    private EditText et_email;

    private Button btn_confirm;
    private Button btn_back;

    //Firestore database
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference doctorCollectionReference = db.collection("Doctor");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_forgot);

        il_email = findViewById(R.id.IL_A_DoctorForgot_Email);
        et_email = findViewById(R.id.ET_A_DoctorForgot_Email);
        et_email.addTextChangedListener(emailTextWatcher);

        btn_confirm = findViewById(R.id.B_A_DoctorForgot_Confirm);
        btn_back = findViewById(R.id.B_A_DoctorForgot_Back);
    }

    @Override
    protected void onStart() {
        super.onStart();

        btn_confirm.setOnClickListener(v -> {
            getDoctorDocumentID();
        });

        btn_back.setOnClickListener(v -> {
            backToLogin();
        });
    }

    //TextWatchers
    private TextWatcher emailTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            doctor_email = et_email.getText().toString().trim();
        }
        @Override
        public void afterTextChanged(Editable editable) {
            il_email.setError((null));
            btn_confirm.setEnabled(true);
        }
    };

    private void  getDoctorDocumentID(){
        doctorCollectionReference
                .whereEqualTo("Email", doctor_email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(!task.getResult().isEmpty()){
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    doctorDocumentID = document.getId();
                                    resetPassword();
                                }
                            }else {
                                il_email.setError("no user found.");
                                btn_confirm.setEnabled(false);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void resetPassword(){
        new EmailTask().execute();
    }
    private void backToLogin(){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private class EmailTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            final String email_address = getResources().getString(R.string.notice_email_address);
            final String email_password = getResources().getString(R.string.notice_email_password);
            String MessagetoSend = "Your Password has been rested to Your ID" ;
            Properties props = new Properties();
            props.put("mail.smtp.auth","true");
            props.put("mail.smtp.starttls.enable","true");
            props.put("mail.smtp.host","smtp.gmail.com");
            props.put("mail.smtp.port","587");

            Session session = Session.getInstance(props,
                    new Authenticator(){
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication(){
                            return new PasswordAuthentication(email_address,email_password);
                        }
                    });

            try{
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(email_address));
                //Log.d(TAG, "Patient Email in email task"+ patient_document_email);
                String SendList = doctor_email;
                message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(SendList) );
                message.setSubject("Missed Appointment");
                message.setText(MessagetoSend);
                Transport.send(message);
            }catch(MessagingException e){
                throw new RuntimeException(e);
            }

            return null;
        }
    }
}