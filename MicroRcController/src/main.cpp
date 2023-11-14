#include <Arduino.h>
#include <Servo.h>

#define pwmPin PIN_A0
#define dirPin PIN3
#define servoPin PIN_A1
Servo myServo;  // Create a servo object to control a servo motor

// put function declarations here:
int myFunction(int, int);

void setup() {
  Serial.begin(9600);  
  Serial1.begin(9600);
  myServo.attach(servoPin);
  int result = myFunction(2, 3);
  pinMode(pwmPin, OUTPUT);
  pinMode(dirPin, OUTPUT);
  
}

void loop() {

  if (Serial1.available() >= 2) {
    // Read two bytes from serial
    int16_t firstByte = Serial1.read()*2;
    byte secondByte = Serial1.read();
    
    Serial1.println();
    // Interpret the second byte as servo data (0 to 180 degrees)
    int servoPosition = map(secondByte, 0, 255, 0, 180);

    
    // Control the servo with the interpreted data
    myServo.write(servoPosition);

    // Optionally, print the received data for debugging
    Serial.print("Received data: ");
    Serial.print(firstByte, DEC);
    Serial.print(", ");
    Serial.println(secondByte, DEC);
    if(firstByte >= 0){
      analogWrite(pwmPin,firstByte);
      digitalWrite(dirPin, LOW);
    }
    else
    {
      analogWrite(pwmPin,abs(firstByte));
      digitalWrite(dirPin, HIGH);
    }
  }
  // put your main code here, to run repeatedly:
  /*digitalWrite(dirPin,HIGH); // turn in one direction
  analogWrite(pwmPin,200);
  delay(2000);
  analogWrite(pwmPin,100);
  // put your main code here, to run repeatedly:
  digitalWrite(dirPin,HIGH); // turn in one direction
  analogWrite(pwmPin,0);
  delay(2000);*/
}

// put function definitions here:
int myFunction(int x, int y) {
  return x + y;
}