//Knižnice
#include <Wire.h>
#include <Adafruit_Sensor.h>
#include "DHT.h"
#include <ArduinoJson.h>
#include <WiFi.h>
#include <HTTPClient.h>
#include <FirebaseESP32.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>

#define WIFI_SSID ""
#define WIFI_PASSWORD ""
#define FIREBASE_HOST ""
#define FIREBASE_AUTH ""
#define DHTPIN 15
#define DHTTYPE DHT11

FirebaseData firebaseData;
WiFiClient wifiClient;
HTTPClient httpClient;

//Premenné, konštanty


float temperature = 0;
float humidity = 0;
float pressure = 0;
String prevDisplej = "";

DHT dht(DHTPIN, DHTTYPE); 

void setup() {
  Serial.begin(9600);
  dht.begin();
  
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.println("Connecting to WiFi...");
  }
  Serial.println("Connected to WiFi!");

  // Initialize Firebase
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
}

void loop() {
  Firebase.getString(firebaseData, "/data/displej");
  String displayValue = firebaseData.stringData();
  Serial.println("firebase displej value: ");
  Serial.println(displayValue);

  if (displayValue != prevDisplej) {
    if (displayValue == "1") {
      zapDisplej();
    } else {
      vypDisplej();
    }
    prevDisplej = displayValue;
  }
  
  float h = getHumidity();
  float t = getTemperature();
  
  getTemperature();
  sendTemperatureToNextion();
  getHumidity();
  sendHumidityToNextion();

  if (isnan(h) || isnan(t)) {
    Serial.println("Failed reception");
    return;
  }

  Serial.print("Vlhkost: ");
  Serial.print(h);
  Serial.print("%  Teplota: ");
  Serial.print(t);
  Serial.print("°C, ");

  // Prepare JSON data to upload to Firebase
  String json = "{ \"data\": { \"teplota\": \"" + String(temperature) + "℃\", \"vlhkost\": \"" + String(humidity) + "%\", \"displej\": " + String(displayValue) + " } }";
  Serial.println(json);

  // Upload data to Firebase
  FirebaseJson fbjsonBuffer;
  fbjsonBuffer.setJsonData(json);
  if (Firebase.setJSON(firebaseData, "/", fbjsonBuffer)) {
    Serial.println("Data uploaded to Firebase!");
  } else {
    Serial.println("Failed to upload data to Firebase.");
    Serial.println(firebaseData.errorReason());
  }
  delay(10000);   //každých 10 sekúnd
}

//funkcie
float getTemperature()
{
  temperature = dht.readTemperature();
}

float getHumidity()
{
  humidity = dht.readHumidity();
}

void sendHumidityToNextion()
{
  String command = "humidity.txt=\""+String(humidity,1)+"\"";
  Serial.print(command);
  Serial.write(0xff);
  Serial.write(0xff);
  Serial.write(0xff);
}

void sendTemperatureToNextion()
{
  String command = "temperature.txt=\""+String(temperature,1)+"\"";
  Serial.print(command);
  Serial.write(0xff);
  Serial.write(0xff);
  Serial.write(0xff);
}

void vypDisplej()
{
  Serial.print("sleep=1");
  Serial.write(0xff);
  Serial.write(0xff);
  Serial.write(0xff);
}

void zapDisplej()
{
  Serial.print("sleep=0");
  Serial.write(0xff);
  Serial.write(0xff);
  Serial.write(0xff);
}