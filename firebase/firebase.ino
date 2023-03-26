#include <WiFi.h>
#include <FirebaseESP32.h>

#define WIFI_SSID "Desktop-MK"
#define WIFI_PASSWORD "Loltrolllol"
#define FIREBASE_HOST "teplomer-esp32-default-rtdb.europe-west1.firebasedatabase.app"
#define FIREBASE_AUTH "1HCE4eC2ZAfJD8vsrAy99LRPc3e9BFBo5VKwy0xD"

WiFiClient wifiClient;
FirebaseData firebaseData;

void setup() {
  Serial.begin(115200);

  // Connect to WiFi network
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
  // Set temperature, humidity and air quality values
  String temperature = "20°C";
  String humidity = "50%";
  String airQuality = "Dobrá";

  // Prepare JSON data to upload to Firebase
  String json = "{ \"data\": { \"teplota\": \"" + temperature + "\", \"vlhkost\": \"" + humidity + "\", \"vzduch\": \"" + airQuality + "\" } }";
  Serial.println(json);

  // Upload data to Firebase
  FirebaseJson jsonBuffer;
  jsonBuffer.setJsonData(json);
  if (Firebase.setJSON(firebaseData, "/", jsonBuffer)) {
    Serial.println("Data uploaded to Firebase!");
  } else {
    Serial.println("Failed to upload data to Firebase.");
    Serial.println(firebaseData.errorReason());
  }

  delay(10000); // Upload data every 10 seconds
}
