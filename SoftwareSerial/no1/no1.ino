void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  pinMode(A0, OUTPUT);
  digitalWrite(A0, LOW);
  pinMode(A2, OUTPUT);
  digitalWrite(A2, HIGH);
}

void loop() {
  // put your main code here, to run repeatedly:
  // Temperature data from Ai
  float cel = ((float)analogRead(A1) / 1023.0) * 487.0 - 60.0;

  char sc[25];
  sprintf(sc, "Arduino No1: %d.%d C", (int)cel, (int)(cel * 10) % 10);
  Serial.println(sc);
  delay(500);
}
