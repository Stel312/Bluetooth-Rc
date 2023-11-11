#include <Arduino.h>

#define pwmPin PIN_A0
#define dirPin PIN3

// put function declarations here:
int myFunction(int, int);

void setup() {
  // put your setup code here, to run once:
  int result = myFunction(2, 3);
  pinMode(pwmPin, OUTPUT);
  pinMode(dirPin, OUTPUT);
  
}

void loop() {
  // put your main code here, to run repeatedly:
  digitalWrite(dirPin,HIGH); // turn in one direction
  analogWrite(pwmPin,200);
  delay(2000);
  analogWrite(pwmPin,100);
  // put your main code here, to run repeatedly:
  digitalWrite(dirPin,HIGH); // turn in one direction
  analogWrite(pwmPin,0);
  delay(2000);
}

// put function definitions here:
int myFunction(int x, int y) {
  return x + y;
}