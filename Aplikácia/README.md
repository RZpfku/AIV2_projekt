# Android Aplikácia

Aplikácia slúži na monitorovanie údajov z meracieho zariadenia pripojeného k platforme IoT (Internet vecí). Údaje zariadenia sú nahrávané do databázy Firebase a zobrazujú sa v tejto aplikácii. Táto aplikácie je vhodná pre použitie v domácnosti, kancelárii alebo kdekoľvek, kde sa potrebujete dozvedieť aktuálne informácie o svojom meracom zariadení.

Aplikácia je písaná v programovacom jazyku Kotlin.

# Obrázky
> Momentálne štádium aplikácie (vzhľad aplikácie sa môže líšiť v závislosti od toho, či dané zariadenie podporuje Material You)

<img src="https://user-images.githubusercontent.com/26904790/227724787-f89a8497-6f76-4cf0-9b76-e49b0dd5d106.png" width = "500px"> <img src="https://user-images.githubusercontent.com/26904790/227724790-93987be0-5e56-42d6-b8ef-eaf1d7753296.png" width = "500px">
<img src="https://user-images.githubusercontent.com/26904790/227724792-5b0ed14a-6816-433a-aa34-bf2ce5c34a7a.png" width = "500px">


# Dokumentácia

<details> 
   <summary>MainActivity.kt</summary> 
  
Tento kód je hlavná aktivita aplikácie, ktorá definuje, aký typ navigácie sa použije v závislosti na orientácii zariadenia, na ktorom aplikácia beží.

V metóde onCreate sa inicializuje prepojenie kódu a rozhranie pomocou triedy ActivityMainBinding. Potom sa zistí aktuálna orientácia zariadenia a ak je zariadenie v orientácii landscape, použije sa trieda NavigationRailView namiesto triedy BottomNavigationView na zobrazenie navigačného panela. Ak je zariadenie v orientácii portrait, použije sa trieda BottomNavigationView.

Následne sa inicializuje navigácia v aktivity, ktorá sa zobrazí pomocou triedy NavController. Trieda AppBarConfiguration definuje zoznam fragmentov, ktoré majú byť súčasťou navigačnej lišty.

Výsledkom je teda aktivita s navigačným panelom, ktorá sa prispôsobuje orientácii zariadenia.
  </details>
   
<details> 
   <summary>HomeFragment.kt</summary> 
  
Tento kód obsahuje triedu HomeFragment, ktorá je odvodená od android fragmentu a slúži na zobrazenie časti užívateľského rozhrania. Aby sme mohli pracovať s týmto fragmentom, musíme získať jeho binding. Binding je spôsob, ako prepojiť elementy užívateľského rozhrania so zdrojovým kódom pomocou ViewBinding knižnice. V našom prípade používame FragmentHomeBinding, ktorý umožňuje prístup k rôznym elementom užívateľského rozhrania. Po získaní bindingu inicializujeme Firebase Cloud databázu, aby sme mohli získať hodnoty teploty, vlhkosti a vzduchu.

Ďalej v kóde sa nachádza inštancia handlera, ktorá slúži na plánovanie pravidelných akcií (napr. kontrola konektivity internetu). Pomocou tejto handler inštancie vytvoríme aj Runnable, ktorý spustí funkciu pre kontrolu internetovej konektivity každých 15 sekúnd. Ak to kvôli tomu, že fragment nie je pripojený k aktivite, nemôže byť vykonané, tak sa táto akcia pozdrží a bude spustená neskôr.

Ďalšie funkcie sú onAttach() a onDetach(), ktoré sa volajú, keď je fragment pripojený alebo odpojený od aktivity. Tieto funkcie nastavujú príznak isAttached, aby sme vedeli, či je fragment pripojený a môžeme teda vykonať určité akcie.

Významnou funkciou v tomto kóde je checkInternetConnection(). Táto funkcia slúži na kontrolu dostupnosti internetovej konektivity a používa sa na zabezpečenie toho, že dáta v databázy budú aktualizované len v prípade, že existuje internetové spojenie. Ak nie je spojenie dostupné, tak sa zobrazia obrázky so zaškrtnutým Wi-Fi symbolom.

Nakoniec, sú tu funkcie pre načítanie hodnôt teploty, vlhkosti a vzduchu z Firebase Cloud databázy a ich zobrazenie na obrazovke. Hodnoty sa extrahujú zo dátových snímok typu DataSnapshot. Taktiež sa podľa rozmedzí hodnôt určuje farba dialógov teploty, kvality vzduchu a vlhkosti.
  
   </details>
   
<details> 
   <summary>AboutFragment.kt</summary> 
  
Tento kód reprezentuje fragment v aplikácii pre meranie teploty pomocou ESP32. V tejto triede sa zobrazuje stránka s informáciami o aplikácii a v nej je tlačidlo GitHub, ktoré po kliknutí otvorí webovú stránku projektu na Githube.

Kód začína importami potrebných tried a balíkov. Trieda AboutFragment dedí od Fragment, ktorý umožňuje zobrazovať interaktívny fragment, ktorý môže byť pridaný do aktivity.

Následne sú vytvorené niektoré premenné, ktoré sa použijú neskôr. Premenná _binding je inicializovaná na null, ale neskôr v metóde onCreateView() sa planý objekt FragmentAboutBinding naplní layoutom a tým sa _binding stane platným. Táto trieda má aj privátnu konštantu githubUrl, kde nadefinuje url adresu pre odkaz na githubov projekt.

OnCreateView() metóda inicializuje _binding objekt na aktuálny layout framentu pomocou DataBindingUtil.inflate. Ona vráti root element layoutu, ktorý je nastavený na val root: View = binding.root.

V githubButton.setOnClickListener { } bloku kódu vytvárame Intent, ktorý spustí ACTION_VIEW, čím sa otvorí nové okno prehliadača s Github repozitárom.

Na konci metódy onDestroyView(), sa _binding objekt nastaví na null, aby sa zabránilo úniku pamäte.

V skratke, tento kód reprezentuje fragment s informáciami o aplikácii a obsahuje tlačidlo pre navigáciu na Github projekt.
  
  </details>
  
<details> 
   <summary>DynamicColors.kt</summary> 
  
Tento kód reprezentuje aplikáciu, ktorá je schopná používať dynamické farby v spojení s Material design knižnicou.

Trieda DynamicColors dedí od Application, ktorý predstavuje globálne nastavenia aplikácie. onCreate() je metóda volaná pri spustení aplikácie a tu je zavolaná metóda applyToActivitiesIfAvailable(), ktorá priradí dynamické farby do aktivity, ak táto funkčnosť je k dispozícii.

V skratke, tento kód aktivuje použitie dynamických farieb v spojení s Material design knižnicou pre celú aplikáciu.
  
  </details>
  
<details> 
   <summary>vyber_domov.xml + vyber_o_projekte.xml</summary> 
  
Tento kód predstavuje selector súbory prostredia Android. selector pozostáva z viacerých položiek a každá z nich obsahuje stav a obrázok, ktorý sa má použiť pre daný stav.

Značka selector definuje stav prvkov, keď sú vybrané alebo nevybrané. Triedy, ktoré ju využívajú standardne obsahujú CheckBoxes, RadioButton alebo ToggleButton.

V tomto prípade, prvá položka <item> nastavuje obrázok home_inactive, ktorý sa použije, keď prvok nie je označený. Druhá položka <item> nastavuje obrázok home_active, ktorý sa použije, keď prvok bude označený (keď bude jeho stav "checked").

V skratke, tento kód definuje dva stavy s odlišnými obrázkami a tieto stavy sa použijú na určenie toho, ktorý obrázok sa má zobraziť podľa aktuálneho stavu prvku.
  
  </details>
  
<details> 
   <summary>layout a layout-land</summary> 
  
  Súbory v týchto priečinkoch obsahujú XML súbory, ktoré predstavújú ako má vyzerať aplikácia. Ak je zariadenie na výšku, budú aktívne súbory priečinka layout a naopak.
  
</details>
  
<details> 
   <summary>menu</summary> 
  
Značka menu definuje celé menu, v ktorom sa môžu nachádzať rôzne položky. V tomto prípade sú to dve položky. Každá položka má ID (android:id), ktoré sa použije na identifikáciu položky a tiež názov (android:title), ktorý sa zobrazí vedľa ikony.

android:icon definoval dva obrázky pre jednotlivé položky menu. Prvá položka má ikonu vyber_domov pre domovskú stránku a druhá položka má ikonu vyber_o_projekte pre menu o projekte.

V skratke, tento kód reprezentuje menu s dvoma položkami a každá z položiek obsahuje obrázok a názov, ktoré sa zobrazia v spodnej navigačnej lište aplikácie.
  
  </details>
  
<details> 
   <summary>mobile_navigation.xml</summary> 
  
Tento kód vytvára dva fragmenty pre navigačnú lištu v aplikácii. Prvý fragment má názov "Domov", jeho trieda je "com.marekguran.esp32teplomer.HomeFragment" a zodpovedá za zobrazovanie domovskej obrazovky. Druhý fragment má názov "O projekte", jeho trieda je "com.marekguran.esp32teplomer.AboutFragment" a zodpovedá za zobrazovanie informácií o projekte.
  
  </details>
  

