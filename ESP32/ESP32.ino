#include <WiFi.h>
#include <HTTPClient.h>
#include <FirebaseESP32.h>
#include <ArduinoJson.h>
#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>

#define WIFI_SSID ""
#define WIFI_PASSWORD ""
#define FIREBASE_HOST ""
#define FIREBASE_AUTH ""
#define OPENWEATHER_API_KEY ""

#define SCREEN_WIDTH 128
#define SCREEN_HEIGHT 64

Adafruit_SSD1306 display(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, -1);

WiFiClient wifiClient;
FirebaseData firebaseData;
HTTPClient httpClient;

void setup() {
  Serial.begin(115200);
  Wire.begin(5, 4);
  
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
  String weatherUrl = "http://api.openweathermap.org/data/2.5/weather?q=Ruzomberok,SK&appid=" + String(OPENWEATHER_API_KEY) + "&units=metric";
  
  // Make a GET request to OpenWeatherMap API
  httpClient.begin(wifiClient, weatherUrl);
  int httpCode = httpClient.GET();
  
  if (httpCode == HTTP_CODE_OK) {
    String payload = httpClient.getString();
    
    // Parse JSON response from OpenWeatherMap API
    DynamicJsonDocument jsonBuffer(1024);
    deserializeJson(jsonBuffer, payload);
    
    serializeJsonPretty(jsonBuffer, Serial); 


    float temperature = jsonBuffer["main"]["temp"];
    int humidity = jsonBuffer["main"]["humidity"];
    float wind = jsonBuffer["wind"]["speed"];
    int pressure = jsonBuffer["main"]["pressure"];
    String city = jsonBuffer["name"];

    display.begin(SSD1306_SWITCHCAPVCC, 0x3C); // Addresa 0x3D pre 128x64
    delay(200);
    display.clearDisplay();
    display.setCursor(0, 0);            //Začne vypisovať vľavo hore
    display.setTextColor(SSD1306_BLACK, SSD1306_WHITE);
    display.print(" Mesto: ");
    display.print(city);
  
    display.println();
    display.setTextColor(SSD1306_WHITE, SSD1306_BLACK);
    display.println();
    display.print(" Teplota: ");
    display.print(temperature);
    display.print((char)247);          //stupen celzia znak
    display.print("C");
    display.println();
    display.print(" Vlhkost: ");
    display.print(humidity);
    display.println("%");
    display.print(" Vietor: ");
    display.print(wind);
    display.println(" m/s");
    display.print(" Tlak: ");
    display.print(pressure);
    display.println(" hpa");
    display.println();
    display.setTextColor(SSD1306_BLACK, SSD1306_WHITE);
    display.print("   RPM Innovations   ");

    display.display();
    
    // Prepare JSON data to upload to Firebase
    String json = "{ \"data\": { \"teplota\": \"" + String(temperature) + "℃\", \"vlhkost\": \"" + String(humidity) + "%\", \"vietor\": \"" + String(wind) + " m/s\", \"tlak\": \"" + String(pressure) + " hpa\", \"mesto\": \"" + String(city) + "\" } }";
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
  } else {
    Serial.println("Failed to connect to OpenWeatherMap API.");
  }
  
  delay(300000); // Upload data every 5 mins
}