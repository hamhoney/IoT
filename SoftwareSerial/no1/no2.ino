#include <SoftwareSerial.h>         // SoftwareSerial Library 설정
SoftwareSerial No2Arduino(2, 3);    // 수신 Rx: D2, 송신 Tx: D3

void setup() {
  No2Arduino.begin(9600);   // Arduino1과 통신 속도 설정
  Serial.begin(9600);       // 시리얼 모니터에 표시할 통신 속도 설정
  Serial.println("Arduino No2 print");
}

void loop() {
  if (No2Arduinio.available()) {
    Serial.write(No2Arduino.read());
  }
}