
/**
   Created by K. Suwatchai (Mobizt)

   Email: k_suwatchai@hotmail.com

   Github: https://github.com/mobizt

   Copyright (c) 2021 mobizt

*/

#if defined(ESP32)
#include <WiFi.h>
#include <FirebaseESP32.h>
#elif defined(ESP8266)
#include <ESP8266WiFi.h>
#include <FirebaseESP8266.h>
#endif

//Provide the token generation process info.
#include <addons/TokenHelper.h>

//Provide the RTDB payload printing info and other helper functions.
#include <addons/RTDBHelper.h>

/* 1. Define the WiFi credentials */
#define WIFI_SSID "Coki"
#define WIFI_PASSWORD "cindelcokitompel"

//For the following credentials, see examples/Authentications/SignInAsUser/EmailPassword/EmailPassword.ino

/* 2. Define the API Key */
#define API_KEY "AIzaSyB6BAGvkaM5rTPW2aY0dw3E_19nYUTKLhA"

/* 3. Define the RTDB URL */
#define DATABASE_URL "https://mini-project-firebase-default-rtdb.asia-southeast1.firebasedatabase.app/" //<databaseName>.firebaseio.com or <databaseName>.<region>.firebasedatabase.app

/* 4. Define the user Email and password that alreadey registerd or added in your project */
#define USER_EMAIL "vitorizki37@gmail.com"
#define USER_PASSWORD "lajelboy37"

//Define Firebase Data object
FirebaseData fbdo;
FirebaseData message;
FirebaseData buzzerStatus;

FirebaseAuth auth;
FirebaseConfig config;

unsigned long sendDataPrevMillis = 0;

unsigned long count = 0;

//Buzzer Setup
#define buzzPin 2


//HW-038 Setup
#define POWER_PIN  17 // ESP32 pin GIOP17 connected to sensor's VCC pin
#define SIGNAL_PIN A0 // ESP32 pin GIOP36 (ADC0) connected to sensor's signal pin

int value = 0; // variable to store the sensor value

//LCD1602A-I2C
#include <LiquidCrystal_I2C.h>
LiquidCrystal_I2C lcd(0x27, 16, 2);

void setup()
{

  Serial.begin(115200);

  //buzzer
  pinMode(buzzPin, OUTPUT);

  //hw-038
  pinMode(POWER_PIN, OUTPUT);   // configure pin as an OUTPUT
  digitalWrite(POWER_PIN, LOW); // turn the sensor OFF

  //LCD
  lcd.begin();
  lcd.backlight();

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED)
  {
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();

  Serial.printf("Firebase Client v%s\n\n", FIREBASE_CLIENT_VERSION);

  /* Assign the api key (required) */
  config.api_key = API_KEY;

  /* Assign the user sign in credentials */
  auth.user.email = USER_EMAIL;
  auth.user.password = USER_PASSWORD;

  /* Assign the RTDB URL (required) */
  config.database_url = DATABASE_URL;

  /* Assign the callback function for the long running token generation task */
  config.token_status_callback = tokenStatusCallback; //see addons/TokenHelper.h

  config.signer.tokens.legacy_token = "4ER9u1QXZWvukPMGgYZEE9AuVizp0Y9LLexBxdTz";

  Firebase.begin(&config, &auth);

  Firebase.reconnectWiFi(true);

  Firebase.setDoubleDigits(5);

}

void loop()
{
  int WaterSensorValue = startWaterSensor();
  //Flash string (PROGMEM and  (FPSTR), String, C/C++ string, const char, char array, string literal are supported
  //in all Firebase and FirebaseJson functions, unless F() macro is not supported.

  if (Firebase.ready() && (millis() - sendDataPrevMillis > 2000 || sendDataPrevMillis == 0))
  {
    sendDataPrevMillis = millis();

    Firebase.setInt(fbdo, "/Node2/WaterSensorValue", WaterSensorValue) ? "ok" : fbdo.errorReason().c_str();

    Firebase.getString(message, "/Node2/Message") ? message.to<const char *>() : message.errorReason().c_str();

    String messageBuffer = message.to<const char *>();

    if (messageBuffer) {
      lcd.clear();
      lcd.setCursor(0, 0);
      lcd.print(messageBuffer);
    }
    
    if (WaterSensorValue >= 700) {
      startBuzzer();
      Firebase.setString(fbdo, "/Node2/BuzzerStatus", "On") ? "ok" : fbdo.errorReason().c_str();
    }
    else {
      Firebase.setString(fbdo, "/Node2/BuzzerStatus", "Off") ? "ok" : fbdo.errorReason().c_str();
    }

  }
}

void startBuzzer() {
  digitalWrite(buzzPin, HIGH);
  delay(3000);
  digitalWrite(buzzPin, LOW);
}

int startWaterSensor() {
  digitalWrite(POWER_PIN, HIGH);
  delay(10);
  value = analogRead(SIGNAL_PIN);
  digitalWrite(POWER_PIN, LOW);
  return value;
}
