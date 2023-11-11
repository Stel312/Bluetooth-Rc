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
  };
  void onDisconnect(BLEServer* pServer) {
    deviceConnected = false;
    Serial.println("Disconnected");
    BLEDevice::startAdvertising();
  }
};

class CharCallback: public BLECharacteristicCallbacks {

    void onWrite(BLECharacteristic *pCharacteristic) {
      std::string rxValue = pCharacteristic->getValue();
      if(pCharacteristic->getUUID().toString() == MOTOR_UUID)
      {
          if(std::stoi(rxValue) != motor)
          {
            motor = std::stoi(rxValue);
            Serial1.write(motor);
          }

      }
      else if(pCharacteristic->getUUID().toString() == SERVO_UUID)
      {
        if(std::stoi(rxValue) != servo)
          {
            servo = std::stoi(rxValue);
            Serial1.write(servo);
          }
      }
     
    }

}; //end of callback


void setup() {
  Serial.begin(9600);
  Serial1.begin(9600);
  Serial.println("Starting BLE work!");
  
  BLEDevice::init(SERVER_NAME);
  BLEServer *pServer = BLEDevice::createServer();
  BLEService *pService = pServer->createService(SERVICE_UUID);
  pServer->setCallbacks(new MyServerCallbacks);
  BLECharacteristic *pCharacteristic = pService->createCharacteristic(MOTOR_UUID,BLECharacteristic::PROPERTY_READ |BLECharacteristic::PROPERTY_WRITE );

  pCharacteristic->setCallbacks(new CharCallback);
  pService->createCharacteristic( SERVO_UUID, BLECharacteristic::PROPERTY_READ | BLECharacteristic::PROPERTY_WRITE);
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
  delay(2000);
}
