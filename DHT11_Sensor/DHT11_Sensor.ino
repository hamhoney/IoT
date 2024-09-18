#include "DHT.h"

#define DHTPIN A0
#define DHTTYPE DHT11

DHT dht(DHTPIN, DHTTYPE);

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);

  dht.begin();
}

void loop() {
  // put your main code here, to run repeatedly:
  delay(1000);
  
  // Reading Temperatur or Humidity takes about 250 milliseconds!
  // Sensor readings may also be up 2 seconds 'old' (its a very slow sensor)
  float h = dht.readHumidity();
  // Read temperature as Celsius (the default)
  float t = dht.readTemperature();
  // Read temperature as Fahrenheit (isFahrenheit = true)
  float f = dht.readTemperature(true);

  // Check if any reads failed and exit early (to try again).
  // if (isnan(h) || isnan(t) || isnan(f)) {
  //   Serial.println(F("Failed to read from DHT sensor!!!"));
  //   return;
  // }

  // Compute heat index in Fahrenheit (the default)
  float hif = dht.computeHeatIndex(f, h);
  // Compute heat index in Celsius (isFahrenheit = false)
  float hic = dht.computeHeatIndex(t, h, false);

  Serial.print(F("Humidity: "));
  Serial.print(h);
  Serial.print(F("% Temperature: "));
  Serial.print(t);
  Serial.print(F("ºC "));
  
  Serial.print(f);    // Fahrenheit
  Serial.print(F("ºF Heat index: "));

  Serial.print(hic);
  Serial.print(F("ºC "));
  Serial.print(hif);
  Serial.println(F("ºF"));
}