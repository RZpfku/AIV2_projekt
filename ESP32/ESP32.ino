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

float prevTemp = -999;
int prevHumidity = -1;
float prevWind = -1;
int prevPressure = -1;
String prevCity = "";
String prevDisplej = "";


String city = "Martin";
String countryCode = "SK";



unsigned long previousMillis_5m = 0;
const long interval_5m = 5 * 60 * 1000;  // Upload data every 5 mins

unsigned long previousMillis_5s = 0;
const long interval_5s = 5000;  // Check Firebase data every 5 secs

void fetchAndUpdateData() {
    String weatherUrl = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "," + countryCode + "&appid=" + String(OPENWEATHER_API_KEY) + "&units=metric";

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

        // Prepare JSON data to upload to Firebase
        String json = "{ \"data\": { \"teplota\": \"" + String(temperature) + "℃\", \"vlhkost\": \"" + String(humidity) + "%\", \"vietor\": \"" + String(wind) + " m/s\", \"tlak\": \"" + String(pressure) + " hPa\", \"mesto\": \"" + String(city) + "\", \"displej\": \"0\" } }";
        Serial.println(json);

        // Upload data to Firebase
        FirebaseJson fbjsonBuffer;
        fbjsonBuffer.setJsonData(json);
        if (Firebase.setJSON(firebaseData, "/", fbjsonBuffer)) {
            Serial.println("Data uploaded to Firebase!");
            prevTemp = temperature;
            prevHumidity = humidity;
            prevWind = wind;
            prevPressure = pressure;
            prevCity = city;
        } else {
            Serial.println("Failed to upload data to Firebase.");
            Serial.println(firebaseData.errorReason());
        }
    } else {
        Serial.println("Failed to connect to OpenWeatherMap API.");
    }
}

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

  display.begin(SSD1306_SWITCHCAPVCC, 0x3C);  // Addresa 0x3D pre 128x64
  delay(200);
  display.clearDisplay();

  fetchAndUpdateData();
}

void loop() {

  // Check and display value from Firebase every 5 secs
  unsigned long currentMillis = millis();
  if (currentMillis - previousMillis_5s >= interval_5s) {
    Firebase.getString(firebaseData, "/data/displej");
    String displayValue = firebaseData.stringData();
    Serial.println("firebase displej value: ");
    Serial.println(displayValue);

    if (displayValue != prevDisplej) {
      if (displayValue == "1") {
        Serial.println("Displej zap.");
        display.ssd1306_command(SSD1306_DISPLAYON);
        display.clearDisplay();
        display.setCursor(0, 0);  //Začne vypisovať vľavo hore
        display.setTextColor(SSD1306_BLACK, SSD1306_WHITE);
        display.print(" Mesto: ");
        display.print(prevCity);

        display.println();
        display.setTextColor(SSD1306_WHITE, SSD1306_BLACK);
        display.println();
        display.print(" Teplota: ");
        display.print(prevTemp);
        display.print((char)247);  //stupen celzia znak
        display.print("C");
        display.println();
        display.print(" Vlhkost: ");
        display.print(prevHumidity);
        display.println("%");
        display.print(" Vietor: ");
        display.print(prevWind);
        display.println(" m/s");
        display.print(" Tlak: ");
        display.print(prevPressure);
        display.println(" hPa");
        display.println();
        display.setTextColor(SSD1306_BLACK, SSD1306_WHITE);
        display.print("   RPM Innovations   ");

        display.display();
      } else {
        Serial.println("Displej vyp.");
        display.ssd1306_command(SSD1306_DISPLAYOFF);
        display.display();
      }

      prevDisplej = displayValue;
    }

    previousMillis_5s = currentMillis;
  }

  // Fetch data from OpenWeatherMap API every 5 mins
  currentMillis = millis();
  if (currentMillis - previousMillis_5m >= interval_5m) {
    String weatherUrl = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "," + countryCode + "&appid=" + String(OPENWEATHER_API_KEY) + "&units=metric";

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

      if (temperature != prevTemp || humidity != prevHumidity || wind != prevWind || pressure != prevPressure || city != prevCity) {

        // Prepare JSON data to upload to Firebase
        String json = "{ \"data\": { \"teplota\": \"" + String(temperature) + "℃\", \"vlhkost\": \"" + String(humidity) + "%\", \"vietor\": \"" + String(wind) + " m/s\", \"tlak\": \"" + String(pressure) + " hPa\", \"mesto\": \"" + String(city) + "\", \"displej\": \"0\" } }";
        Serial.println(json);

        // Upload data to Firebase
        FirebaseJson fbjsonBuffer;
        fbjsonBuffer.setJsonData(json);
        if (Firebase.setJSON(firebaseData, "/", fbjsonBuffer)) {
          Serial.println("Data uploaded to Firebase!");
          prevTemp = temperature;
          prevHumidity = humidity;
          prevWind = wind;
          prevPressure = pressure;
          prevCity = city;
        } else {
          Serial.println("Failed to upload data to Firebase.");
          Serial.println(firebaseData.errorReason());
        }
      } else {
        Serial.println("No changes in weather data. Not uploading to Firebase.");
      }
    } else {
      Serial.println("Failed to connect to OpenWeatherMap API.");
    }

    previousMillis_5m = currentMillis;
  }
}