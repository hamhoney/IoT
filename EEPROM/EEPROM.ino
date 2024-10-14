#include <EEPROM.h>

void setup() {
  Serial.begin(9600);
  byte val = EEPROM.read(0);  // Read of EEPROM data
  Serial.print("Memory value:");
  Serial.println(val);
  EEPROM.write(0, ++val);   // Write to EEPROM data
}

void loop() {
  // put your main code here, to run repeatedly:

}
