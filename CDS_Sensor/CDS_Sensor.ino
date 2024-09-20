
int LED_PIN = 2;
int light = -1;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  pinMode(LED_PIN, OUTPUT);
}

void loop() {
  // put your main code here, to run repeatedly:
  delay(1000);
  light = analogRead(A0);
  Serial.print("cds:");
  Serial.println(light);

  if (light <= 300) {
    digitalWrite(LED_PIN, HIGH);
  } else {
    digitalWrite(LED_PIN, LOW);
  }

  // or, digitalWrite(LED_PIN, light <= 300 ? HIGH : LOW);
}
