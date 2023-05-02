//Knižnice
#include <Wire.h>
#include <Adafruit_Sensor.h>
#include "Adafruit_BME680.h"
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

FirebaseData firebaseData;
WiFiClient wifiClient;
HTTPClient httpClient;

//Premenné, konštanty
float temperature = 0;
float humidity = 0;
float pressure = 0;

Adafruit_BME680 bme;  // I2C komunikácia

void setup() {
  Serial.begin(115200);

  //kontrola ci je senzor pripojeny
  while (!Serial)
    ;
  Serial.println(F("BME680 async test"));

  if (!bme.begin()) {
    Serial.println(F("Could not find a valid BME680 sensor, check wiring!"));
    while (1)
      ;
  }

  //Nadstavenie bme oversamplingu a inicializacia filtra
  bme.setTemperatureOversampling(BME680_OS_8X);
  bme.setHumidityOversampling(BME680_OS_2X);
  bme.setPressureOversampling(BME680_OS_4X);
  bme.setIIRFilterSize(BME680_FILTER_SIZE_3);

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
  // BME - začni merať.
  unsigned long endTime = bme.beginReading();
  if (endTime == 0) {
    Serial.println(F("Failed to begin reading :("));
    return;
  }
  Serial.print(F("Reading started at "));
  Serial.print(millis());
  Serial.print(F(" and will finish at "));
  Serial.println(endTime);

  Serial.println(F("You can do other work during BME680 measurement."));
  delay(50);

  if (!bme.endReading()) {
    Serial.println(F("Failed to complete reading :("));
    return;
  }
  //Vypis hodnôt
  Serial.print(F("Reading completed at "));
  Serial.println(millis());

  Serial.print(F("Temperature = "));
  Serial.print(bme.readTemperature());
  Serial.println(F(" *C"));

  Serial.print(F("Pressure = "));
  Serial.print(bme.readPressure() / 100.0);
  Serial.println(F(" hPa"));

  Serial.print(F("Humidity = "));
  Serial.print(bme.readHumidity());
  Serial.println(F(" %"));

  Serial.println();
  delay(2000);

  getTemperature();
  sendTemperatureToNextion();

  getHumidity();
  sendHumidityToNextion();

  getPressure();
  sendPressureToNextion();

  // Prepare JSON data to upload to Firebase
  String json = "{ \"data\": { \"teplota\": \"" + String(temperature) + "℃\", \"vlhkost\": \"" + String(humidity) + "%\", \"tlak\": \"" + String(pressure) + " hPa\" } }";
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
}

//funkcie
float getTemperature() {
  temperature = bme.readTemperature();
}

float getHumidity() {
  humidity = bme.readHumidity();
}

float getPressure() {
  pressure = bme.readPressure();
  pressure = pressure / 100.0F;
}

void sendHumidityToNextion() {
  String command = "humidity.txt=\"" + String(humidity, 1) + "\"";
  Serial.print(command);
  endNextionCommand();
}

void sendTemperatureToNextion() {
  String command = "temperature.txt=\"" + String(temperature, 1) + "\"";
  Serial.print(command);
  endNextionCommand();
}

void sendPressureToNextion() {
  String command = "pressure.txt=\"" + String(pressure, 1) + "\"";
  Serial.print(command);
  endNextionCommand();
}

void endNextionCommand() {
  Serial.write(0xff);
  Serial.write(0xff);
  Serial.write(0xff);
}
