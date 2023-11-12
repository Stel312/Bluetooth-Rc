/*
    Based on Neil Kolban example for IDF: https://github.com/nkolban/esp32-snippets/blob/master/cpp_utils/tests/BLE%20Tests/SampleServer.cpp
    Ported to Arduino ESP32 by Evandro Copercini
    updates by chegewara
*/

#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEServer.h>
#include <HardwareSerial.h>


// See the following for generating UUIDs:
// https://www.uuidgenerator.net/

#define SERVICE_UUID        "4fafc201-1fb5-459e-8fcc-c5c9c331914b"
#define MOTOR_UUID          "beb5483e-36e1-4688-b7f5-ea07361b26a8"
#define SERVO_UUID          "df240dd8-80d7-11ee-b962-0242ac120002"
#define SERVER_NAME "RC Server"
bool deviceConnected = false;
int8_t motor = 0;
uint8_t servo = 0;
//Setup callbacks onConnect and onDisconnect
class MyServerCallbacks: public BLEServerCallbacks {
  void onConnect(BLEServer* pServer) {
    deviceConnected = true;
    Serial.println("Connected");
  }
  void onDisconnect(BLEServer* pServer) {
    deviceConnected = false;
    Serial.println("Disconnected");
    BLEDevice::startAdvertising();
  }
};

class ServoCallback: public BLECharacteristicCallbacks {
    void onWrite(BLECharacteristic *pCharacteristic) {
      std::string rxValue = pCharacteristic->getValue();
      //uint8_t size = pCharacteristic->getLength();
      //Serial.println(size);
      //Serial.printf("value %i",pCharacteristic->getData());
          int8_t   data1 = rxValue[0];
          uint8_t  data2 = rxValue[1];

            // Display or process the received data as needed
            Serial.print("Received Data: ");
            Serial.print(data1);
            Serial.print(", ");
            Serial.println(data2);
      
      //servo = std::stoi(rxValue);
      //Serial.printf("servo: %f \n", servo);
      Serial1.write(servo);
            
  }
};

void setup() {
  Serial.begin(9600);
  Serial1.begin(9600);
  Serial.println("Starting BLE work!");
  Serial.print("loop() running on core ");
  Serial.println(xPortGetCoreID());

  BLEDevice::init(SERVER_NAME);
  BLEServer *pServer = BLEDevice::createServer();
  BLEService *pService = pServer->createService(SERVICE_UUID);
  pServer->setCallbacks(new MyServerCallbacks);
  BLECharacteristic *pServo =  pService->createCharacteristic(SERVO_UUID, BLECharacteristic::PROPERTY_READ | BLECharacteristic::PROPERTY_WRITE_NR);
  pServo->setCallbacks(new ServoCallback);
  Serial.println("Created Characteristics");
  pService->start();
  // BLEAdvertising *pAdvertising = pServer->getAdvertising();  // this still is working for backward compatibility
  BLEAdvertising *pAdvertising = BLEDevice::getAdvertising();
  pAdvertising->addServiceUUID(SERVICE_UUID);
  pAdvertising->setScanResponse(true);
  pAdvertising->setMinPreferred(0x06);  // functions that help with iPhone connections issue
  pAdvertising->setMinPreferred(0x12);
  BLEDevice::startAdvertising();
}

void loop() {
  // put your main code here, to run repeatedly:
  //delay(1000);
}
