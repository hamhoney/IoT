// UltraSonic Sensor

// trig : send
// echo : receive

int TRIG_PIN = 2;
int ANALOG_PIN = A0;
int value = 0;

#define TRIGPIN 8
#define ECHOPIN 9
#define CTM 10  //HIGH인 시간

int RED_LED_PIN = 2;
int GREEN_LED_PIN = 3;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  pinMode(TRIGPIN, OUTPUT);
  // If echo pin put in digital pin
  // pinMode(ECHOPIN, INPUT);
  pinMode(ECHOPIN, INPUT);

  // Define LED
  pinMode(RED_LED_PIN, OUTPUT);
  pinMode(GREEN_LED_PIN, OUTPUT);
}

void loop() {
  // put your main code here, to run repeatedly:
  // delay(500);
  // digitalWrite(TRIG_PIN, HIGH);
  // value = analogRead(ANALOG_PIN);
  // Serial.print("value:");
  // Serial.println(value);

  int dur;      // 시간 차
  float dis;    // 거리 distance
  digitalWrite(TRIGPIN, HIGH);
  delayMicroseconds(CTM);
  digitalWrite(TRIGPIN, LOW);

  dur = pulseIn(ECHOPIN, HIGH);   // EchoPin이 HIGH가 되는데까지 걸리는 시간 측정
  dis = (float)dur * 0.017;       // 음속을 사용한 거리계산
  Serial.print(dis);
  Serial.println(" cm");
  
  // LED ON
  if (dis < 10) {
    digitalWrite(RED_LED_PIN, LOW);
    digitalWrite(GREEN_LED_PIN, HIGH);
  } else {
    digitalWrite(RED_LED_PIN, HIGH);
    digitalWrite(GREEN_LED_PIN, LOW);
  }
  delay(500);
}

