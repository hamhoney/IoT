int dn;   // LED 깜빡임 대기 시간

void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  Serial.print(" delay msec=");
  dn = readKeyboard().toInt();    // 키보드 입력 값
  Serial.println(dn);
  pinMode(13, OUTPUT);
}

void loop() {
  // put your main code here, to run repeatedly:
  digitalWrite(13, HIGH);
  delay(dn);
  digitalWrite(13, LOW);
  delay(dn);
}

// KeyboardInput Function
String readKeyboard() {
  char str[100];
  char ch;
  int i=0;
  boolean sw = true;
  unsigned long tms;
  while(sw) {
    ch = Serial.read();
    if (ch >= 0 && ch <= 127) 
    {
      tms = millis();
      str[i] = ch;
      sw = false;
    }
    else if ((millis() - tms > 300) && (i > 0)) 
    {
      str[i] = 0;
      sw = false;
    }
  }

  return String(str);
}
