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

        val buzzerStatus = findViewById<TextView>(R.id.tvBuzzerStatus)
        val currMessage = findViewById<TextView>(R.id.tvCurrentMessage)
        val waterSensor = findViewById<TextView>(R.id.tvWaterSensor)

        val message = findViewById<EditText>(R.id.etMessage)
        val btnSendMessage = findViewById<Button>(R.id.btnSubmitMessage)

        val dref = FirebaseDatabase.getInstance("https://mini-project-firebase-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()

        dref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val valueWaterSensor = dataSnapshot.child("Node2/WaterSensorValue").getValue().toString();
                if(valueWaterSensor.equals("0"))
                    waterSensor.setText("No Water Detected");
                else
                    waterSensor.setText(valueWaterSensor);

                val valueCurrentMessage = dataSnapshot.child("Node2/Message").getValue().toString();
                currMessage.setText(valueCurrentMessage);

                val valueBuzzerStatus = dataSnapshot.child("Node2/BuzzerStatus").getValue().toString();
                buzzerStatus.setText(valueBuzzerStatus);

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        btnSendMessage.setOnClickListener {
            val newMessage = message.text.toString()
            FirebaseDatabase.getInstance("https://mini-project-firebase-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Node2/Message").setValue(newMessage)
        }
    }
}