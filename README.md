# AIV2_projekt ![Kotlin Badge](https://img.shields.io/badge/-Kotlin-0095D5?logo=kotlin&logoColor=white) ![C++ Badge](https://img.shields.io/badge/-C++-00599C?logo=c%2B%2B&logoColor=white)

| Členovia projektu:              | Úlohy:                        | Status:                          |
| -----------------| :-------------------------------------:|:-----------------------------------:|
| [Radovan Žiak](https://github.com/RZpfku) | Vedúci projektu, dokumentácia, obhajoba projektu a tester aplikácie | ❌ |
| [Marek Guráň](https://github.com/marek-guran) | Android aplikácia s Firebase databázou + kód pre ESP32 | ✔️ |
| [Peter Spišiak](https://github.com/PeterSpisiak) | Krabička a tester aplikácie | ❌ |

## Cieľ projektu
Cieľom tohto projektu je vytvoriť zariadenie, ktoré bude schopné čítať dáta z OpenWeather API a ukladať ich do Realtime Firebase databázy. ESP32 s OLED displejom bude zobrazovať aktuálne dáta a bude počúvať Firebase databázu, aby sa vypla alebo zapla obrazovka. Android aplikácia bude taktiež komunikovať s Firebase, stiahne dáta a umožní používateľovi ovládať obrazovku zariadenia.

#### Plánované kroky
1. Nastavenie projektu v Arduino IDE a spustenie prvotnej inštalácie ESP32.
2. Pripojenie ESP32 na internet a nastavenie pripojenia k OpenWeather API.
3. Nastavenie spojenia s Firebase a úprava zabezpečenia prístupu.
3. Vytvorenie kódu pre čítanie dát z OpenWeather API a nahrávanie ich do Firebase databázy.
4. Naprogramovanie funkcionality pre čítanie dát z Firebase a riadenie zapnutia/zapnutia displeja.
5. Programovanie Android aplikácie, ktorá bude zobrazovať aktualizované informácie z Firebase a umožní užívateľovi ovládať jednotku.
6. Testovanie celého systému pre overenie funkčnosti a odstránenie chýb.
7. Vytvorenie krabičky pre ESP32.
8. Vytvorenie dokumentácie projektu.

#### Predpokladané problémy
1. Nastavenie spojenia s Firebase databázou a zabezpečovanie prístupu k dátam.
2. Kvalita pripojenia k internetu pre prijímanie aktualizovaných informácií z OpenWeather API.
3. Rýchle a spoľahlivé načítavanie informácií v Android aplikácii.
