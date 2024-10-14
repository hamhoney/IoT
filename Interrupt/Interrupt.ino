// Interrupt

void setup() {
  // put your setup code here, to run once:
  pinMode(2, INPUT_PULLUP);   // Interrupt Pin
  pinMode(13, OUTPUT);        // Uno's LED
  attachInterrupt(0, buzzer, CHANGE); 
}

void loop() {
  // put your main code here, to run repeatedly:
  digitalWrite(13, HIGH);
  delay(1000);
  digitalWrite(13, LOW);
  delay(1000);
}

void buzzer() {
  pinMode(9, OUTPUT);
  tone(9, 255, 1000);
}
