# AIV2_projekt
projekt na predmet Aplikacie internetu veci

Na projekte pracovali:

>Radovan Žiak (Vedúci projektu, kód pre ESP32) [GitHub](https://github.com/RZpfku)

>Marek Guráň (Android aplikácia s Firebase databázou) [GitHub](https://github.com/marek-guran)

>Peter Spišiak (Krabička a tester aplikácie) [GitHub](https://github.com/PeterSpisiak)

Contributors:
<a href="https://github.com/RZpfku/AIV2_projekt/graphs/contributors">

  <img src="https://contrib.rocks/image?repo=RZpfku/AIV2_projekt" />

</a>

# Plán projektu
Vytvoriť teplomer, ktorý bude merať teplotu, kvalitu vzduchu a vlhkosť. Teplomer bude posielať dáta na Firebase databázu z ktorej si bude brať a zobrazovať dáta mobilná aplikácia.

# Rozdelenie úloh:

###### Rozpracovať do 26.3.2023

 **Obal, na teplomer - LilyGO TTGO T-Display 1.14″ ESP32 (treba počítať aj s baterkou).**\
 Pre: Peter

 **Software na teplomer.**\
 Pre: Rado

 **Android aplikácia.**\
 Pre: Marek

# Ukážkový kód pre Radovana (Firebase)

```
#include <FirebaseESP32.h>

#define FIREBASE_HOST "teplomer-esp32-default-rtdb.europe-west1.firebasedatabase.app"

#define FIREBASE_AUTH ""

FirebaseData firebaseData;

const char* ssid = "your_ssid";

const char* password = "your_password";

void setup() {

  Serial.begin(9600);

  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {

    delay(1000);

    Serial.println("Connecting to WiFi...");

  }

  Firebase.begin(FIREBASE_HOST);

  Firebase.reconnectWiFi(true);

  Serial.println("Connected to Firebase");

}

void loop() {

  String temperature = "30°C";

  String humidity = "60%";

  String air = "Dobrá";

  Firebase.setString(firebaseData, "/data/teplota", temperature);

  if (firebaseData.success()) {

    Serial.println("Temperature uploaded to Firebase");

  } else {

    Serial.println("Temperature upload failed");

  }

  Firebase.setString(firebaseData, "/data/vlhkost", humidity);

  if (firebaseData.success()) {

    Serial.println("Humidity uploaded to Firebase");

  } else {

    Serial.println("Humidity upload failed");

  }

  Firebase.setString(firebaseData, "/data/vzduch", air);

  if (firebaseData.success()) {

    Serial.println("Air quality uploaded to Firebase");

  } else {

    Serial.println("Air quality upload failed");

  }

  delay(10000);

}


```
