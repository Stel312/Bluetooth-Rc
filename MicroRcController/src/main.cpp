#include <Arduino.h>
#include <Servo.h>

#define pwmPin PIN3
#define dirPin PIN2
#define servoPin PIN5
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
    int8_t firstByte = Serial1.read();
    byte secondByte = Serial1.read();
    
    Serial1.println();
    
    // Control the servo with the interpreted data
    myServo.write(secondByte);

    // Optionally, print the received data for debugging
    Serial.print("Received data: ");
    Serial.print((int8_t)firstByte);
    Serial.print(", ");
    Serial.println(secondByte);
    if(firstByte >= 0){
      digitalWrite(dirPin, LOW);
    }
    else
    {
      firstByte = abs(firstByte);
      Serial.print(firstByte, DEC);
      digitalWrite(dirPin, HIGH);
    }
    int servoPosition = map(firstByte, 0, 100, 0, 255);
    analogWrite(pwmPin,firstByte);
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