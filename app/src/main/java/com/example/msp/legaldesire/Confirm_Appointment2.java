package com.example.msp.legaldesire;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.payUMoney.sdk.PayUmoneySdkInitilizer;
import com.payUMoney.sdk.SdkConstants;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class Confirm_Appointment2 extends AppCompatActivity {
    public final String TAG = "appointment123";
    String user_id, user_name, user_address, lawyer_id, lawyer_name, appointment_date, appointment_time;
    boolean isLawyer = false;
    double cost;
    TextView textView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm__appointment2);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F17A12")));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Confirm Appointment </font>", Html.FROM_HTML_MODE_LEGACY));

        } else {
            getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Confirm Appointment </font>"));
        }
        Bundle bundle = getIntent().getExtras();
        user_id = bundle.getString("User ID");
        user_name = bundle.getString("User Name");
        user_address = bundle.getString("Address");
        lawyer_id = bundle.getString("Lawyer ID");
        lawyer_name = bundle.getString("Lawyer Name");
        appointment_date = bundle.getString("Appointment Date");
        appointment_time = bundle.getString("Appointment Time");
        isLawyer = bundle.getBoolean("isLawyer");
        cost = bundle.getDouble("Cost");

        Log.d(TAG, "ddasd" + user_name);

        textView = (TextView) findViewById(R.id.textView);
        button = (Button) findViewById(R.id.btn_pay);

        textView.setText("On payment of " + (int) cost + "₹" + ", you wil book an appointment with Lawyer " + "'" + lawyer_name + "'" + " for a Home visit" + "\n\nDetails:\nLawyer Name:" + lawyer_name + "\nAddress:" + user_address + "\nAppointment Date" + appointment_date + "\nAppointment Time:" + appointment_time + "\nCost:" + (int) cost + "₹" +
                "\n\n DON'T WORRY! You will be refunded if:" +
                "\n✔The Lawyer didn't show up at the appointed date and time ");
        int c = (int) cost;
        button.setText("Pay " + c + "₹");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePayment();
            }
        });

    }

    private double getAmount() {
        //  return cost;/
        return 100.00;
        //  return cost;
    }

    public void makePayment() {
        String phone = "8882434664";
        String productName = "product_name";
        String firstName = "piyush";
        String txnId = "legaldesire" + System.currentTimeMillis();
        String email = "piyush.jain@payu.in";
        String sUrl = "https://test.payumoney.com/mobileapp/payumoney/success.php";
        String fUrl = "https://test.payumoney.com/mobileapp/payumoney/failure.php";
        String udf1 = "";
        String udf2 = "";
        String udf3 = "";
        String udf4 = "";
        String udf5 = "";
        boolean isDebug = true;

        //   String key = "uRURJ8";
        //  String merchantId = "329037";
        //   String salt="zPi921sH";


        String key = "2fcU3pmI";
        String merchantId = "4947182";// These credentials are from https://test.payumoney.com/
        String salt = "BxA24L2F7Z";   //  THIS WORKS

      /*  String key = "yX8OvWy1";     //These credentials are from https://www.payumoney.com/
        String merchantId = "5826688"; //THIS DOESN'T WORK
        String salt = "0vciMJBbaa";    //ERROR: "some error occurred, Try again"
      */


        PayUmoneySdkInitilizer.PaymentParam.Builder builder = new PayUmoneySdkInitilizer.PaymentParam.Builder();


        builder.setAmount(getAmount())
                .setTnxId(txnId)
                .setPhone(phone)
                .setProductName(productName)
                .setFirstName(firstName)
                .setEmail(email)
                .setsUrl(sUrl)
                .setfUrl(fUrl)
                .setUdf1(udf1)
                .setUdf2(udf2)
                .setUdf3(udf3)
                .setUdf4(udf4)
                .setUdf5(udf5)
                .setIsDebug(true)
                .setKey(key)
                .setMerchantId(merchantId);

        PayUmoneySdkInitilizer.PaymentParam paymentParam = builder.build();

        // Recommended
        //  calculateServerSideHashAndInitiatePayment(paymentParam);
        String hash = hashCal(key + "|" + txnId + "|" + getAmount() + "|" + productName + "|"
                + firstName + "|" + email + "|" + udf1 + "|" + udf2 + "|" + udf3 + "|" + udf4 + "|" + udf5 + "|" + salt);
        Log.d(TAG, "got hash:" + hash);
        paymentParam.setMerchantHash(hash);

        PayUmoneySdkInitilizer.startPaymentActivityForResult(Confirm_Appointment2.this, paymentParam);

    }

    public static String hashCal(String str) {
        byte[] hashseq = str.getBytes();
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();
            for (byte aMessageDigest : messageDigest) {
                String hex = Integer.toHexString(0xFF & aMessageDigest);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hexString.toString();
    }



  /* */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "Inside onactivity result()");

        if (requestCode == PayUmoneySdkInitilizer.PAYU_SDK_PAYMENT_REQUEST_CODE) {

            /*if(data != null && data.hasExtra("result")){
              String responsePayUmoney = data.getStringExtra("result");
                if(SdkHelper.checkForValidString(responsePayUmoney))
                    showDialogMessage(responsePayUmoney);
            } else {
                showDialogMessage("Unable to get Status of Payment");
            }*/


            if (resultCode == RESULT_OK) {
                Log.i(TAG, "Success - Payment ID : " + data.getStringExtra(SdkConstants.PAYMENT_ID));
                String paymentId = data.getStringExtra(SdkConstants.PAYMENT_ID);
                showDialogMessage("Transaction Successful : " + paymentId);
                if (isLawyer) {
                    setBookingForLawyer();
                } else {
                    setBooking();
                }


            } else if (resultCode == RESULT_CANCELED) {
                Log.i(TAG, "failure");
                showDialogMessage("Transaction Cancelled");
            } else if (resultCode == PayUmoneySdkInitilizer.RESULT_FAILED) {
                Log.i("app_activity", "failure");

                if (data != null) {
                    if (data.getStringExtra(SdkConstants.RESULT).equals("cancel")) {

                    } else {
                        showDialogMessage("Transation Failed");
                    }
                }
                //Write your code if there's no result
            } else if (resultCode == PayUmoneySdkInitilizer.RESULT_BACK) {
                Log.i(TAG, "User returned without login");
            }
        }
    }


    private void showDialogMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Confirm_Appointment2.this);
        builder.setTitle("Payment Status");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }

    public void setBookingForLawyer() {
        final DatabaseReference root2 = FirebaseDatabase.getInstance().getReference().child("User").child("Lawyer");


        HashMap<String, Object> lawyerAppointments = new HashMap<String, Object>();
        lawyerAppointments.put("User ID", user_id);
        lawyerAppointments.put("User Name", user_name);
        lawyerAppointments.put("Appointment Type", "Non Office Appointment");
        lawyerAppointments.put("Appointment Date", appointment_date);
        lawyerAppointments.put("Appointment Time", appointment_time);
        lawyerAppointments.put("Lawyer Office Address", user_address);

        HashMap<String, Object> userAppointments = new HashMap<String, Object>();
        userAppointments.put("Lawyer ID", lawyer_id);
        userAppointments.put("Lawyer Name", lawyer_name);
        userAppointments.put("Appointment Type", "Non Office Appointment");
        userAppointments.put("Appointment Date", appointment_date);
        userAppointments.put("Appointment Time", appointment_time);
        userAppointments.put("Lawyer Office Address", user_address);

        root2.child(lawyer_id).child("Appointment").push().setValue(lawyerAppointments).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(Confirm_Appointment2.this, "Booking...", Toast.LENGTH_SHORT).show();
            }
        });
        root2.child(user_id).child("Personal_Appointment").push().setValue(userAppointments).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(Confirm_Appointment2.this, "Booking Successful, go to 'My Appointments' for further details'", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Confirm_Appointment2.this, Login.class);
                startActivity(intent);
            }
        });

    }

    public void setBooking() {


        final DatabaseReference root2 = FirebaseDatabase.getInstance().getReference().child("User").child("Lawyer");
        final DatabaseReference root3 = FirebaseDatabase.getInstance().getReference().child("User").child("Regular");

        HashMap<String, Object> lawyerAppointments = new HashMap<String, Object>();
        lawyerAppointments.put("User ID", user_id);
        lawyerAppointments.put("User Name", user_name);
        lawyerAppointments.put("Appointment Type", "Non Office Appointment");
        lawyerAppointments.put("Appointment Date", appointment_date);
        lawyerAppointments.put("Appointment Time", appointment_time);
        lawyerAppointments.put("Lawyer Office Address", user_address);

        HashMap<String, Object> userAppointments = new HashMap<String, Object>();
        userAppointments.put("Lawyer ID", lawyer_id);
        userAppointments.put("Lawyer Name", lawyer_name);
        userAppointments.put("Appointment Type", "Non Office Appointment");
        userAppointments.put("Appointment Date", appointment_date);
        userAppointments.put("Appointment Time", appointment_time);
        userAppointments.put("Lawyer Office Address", user_address);


        root2.child(lawyer_id).child("Appointment").push().setValue(lawyerAppointments).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(Confirm_Appointment2.this, "Booking...", Toast.LENGTH_SHORT).show();
            }
        });
        root3.child(user_id).child("Appointment").push().setValue(userAppointments).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(Confirm_Appointment2.this, "Booking Successful, go to 'My Appointments' for further details'", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Confirm_Appointment2.this, Login.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.nothing, R.anim.exit2);

    }

}
