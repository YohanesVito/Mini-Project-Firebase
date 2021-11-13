package com.example.iotminiproject2_firebase_71190429

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val TagID = findViewById<TextView>(R.id.tvTagID)
        val ownerName = findViewById<TextView>(R.id.tvOwnerName)
        val isPermitted = findViewById<TextView>(R.id.tvIsPermitted)
        val btnOnAlarm = findViewById<Button>(R.id.btnOnAlarm)
        val btnOffAlarm = findViewById<Button>(R.id.btnOffAlarm)
        val alarmStatus = findViewById<TextView>(R.id.tvAlarmStatus)

        val dref = FirebaseDatabase.getInstance("https://mini-project-firebase-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()

        dref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val valueTagID = dataSnapshot.child("Node1/TagID").getValue().toString();
                if(valueTagID.equals("0"))
                    TagID.setText("No Tag");
                else
                    TagID.setText(valueTagID);

                val valueOwner = dataSnapshot.child("Node1/Owner").getValue().toString();
                ownerName.setText(valueOwner);

                val valueisPermitted = dataSnapshot.child("Node1/IsPermitted").getValue().toString();
                isPermitted.setText(valueisPermitted);

                val valueAlarmStatus = dataSnapshot.child("Node1/AlarmStatus").getValue().toString();
                alarmStatus.setText(valueAlarmStatus);

//                val valueLampu = dataSnapshot.child("Node1/buzzer").getValue().toString();
//                if(valueLampu1.equals("0"))
//                    buttonLampu1.setChecked(false);
//                else
//                    buttonLampu1.setChecked(true);
//
//                valueLampu2 = dataSnapshot.child("Node1/buzzer").getValue().toString();
//                if(valueLampu2.equals("0"))
//                    buttonLampu2.setChecked(false);
//                else
//                    buttonLampu2.setChecked(true);
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        btnOnAlarm.setOnClickListener {
            FirebaseDatabase.getInstance("https://mini-project-firebase-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Node1/Alarm").setValue(1)
            FirebaseDatabase.getInstance("https://mini-project-firebase-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Node1/AlarmStatus").setValue("On")
        }
        btnOffAlarm.setOnClickListener {
            FirebaseDatabase.getInstance("https://mini-project-firebase-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Node1/Alarm").setValue(0)
            FirebaseDatabase.getInstance("https://mini-project-firebase-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Node1/AlarmStatus").setValue("Off")
        }
    }
}