#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>
#include <Adafruit_NeoPixel.h>

//------Defining global variables------//

bool deviceConnected = false;
int txValue = 0;

#define SERVICE_UUID "8af2aa89-657d-4141-aba1-49b8ea0efb70"
#define CARACTERISTIQUE_UUID "3814ecdd-104f-44a3-b09a-f85344f0a232"
#define CARACTERISTIQUEmsg_UUID "e3f98746-3f35-4db5-927a-6e18259a24e9"

BLECharacteristic Mycarac(CARACTERISTIQUE_UUID, BLECharacteristic::PROPERTY_NOTIFY);
BLECharacteristic pCaracteristique(CARACTERISTIQUEmsg_UUID, BLECharacteristic::PROPERTY_WRITE);

std::string mode1 = "Allume 1";
std::string mode2 = "Allume 2";
std::string mode3 = "Allume 3";
std::string strMode = "Allume x";
std::string strTime = "000";

int shift = 3;
int nbActuator = 4;
int actPin[] = {0, 1, 3, 4};
const int freq = 150;
const int channel = 0;
const int resolution = 10;

#define NEO_PIXEL_PIN 6 //SCL on QT PY ESP32-C3

Adafruit_NeoPixel strip = Adafruit_NeoPixel(12, NEO_PIXEL_PIN, NEO_GRB + NEO_KHZ800);
uint32_t h_color = strip.Color(32, 0, 0);
uint32_t m_color = strip.Color(32, 32, 32);
uint32_t mh_color = strip.Color(32, 16, 16);
uint32_t no_color = strip.Color(0, 0, 0);
uint32_t bred_color = strip.Color(32, 0, 0);
uint32_t bblue_color = strip.Color(0, 5, 32);

int c_status = 0;



//------Defining functions------//

void mode_1() {
  for (int dutyCycle = 0; dutyCycle <= 500; dutyCycle++) {//500 au lieu de 1000
    // changing the LED brightness with PWM
    ledcWrite(channel, 500);
    delay(30);
  }

  // decrease the LED brightness
  for (int dutyCycle = 200; dutyCycle >= 0; dutyCycle--) {
    // changing the LED brightness with PWM
    ledcWrite(channel, dutyCycle);
    delay(15);
  }
}

void mode_2() {
  for (int dutyCycle = 0; dutyCycle <= 500; dutyCycle++) {
    // changing the LED brightness with PWM
    ledcWrite(channel, dutyCycle);
    delay(30);
  }

  // decrease the LED brightness
  for (int dutyCycle = 500; dutyCycle >= 0; dutyCycle--) {
    // changing the LED brightness with PWM
    ledcWrite(channel, dutyCycle);
    delay(30);
  }
}

void mode_3() {
  for (int dutyCycle = 0; dutyCycle <= 400; dutyCycle++) {
    // changing the LED brightness with PWM
    ledcWrite(channel, dutyCycle);
    delay(15);
  }

  // decrease the LED brightness
  for (int dutyCycle = 400; dutyCycle >= 0; dutyCycle--) {
    // changing the LED brightness with PWM
    ledcWrite(channel, dutyCycle);
    delay(15);
  }
}

uint32_t Wheel(byte WheelPos) {
  WheelPos = 255 - WheelPos;
  if (WheelPos < 85) {
    return strip.Color((255 - WheelPos * 3)/8, 0, (WheelPos * 3)/8);
  }
  if (WheelPos < 170) {
    WheelPos -= 85;
    return strip.Color(0, (WheelPos * 3)/8, (255 - WheelPos * 3)/8);
  }
  WheelPos -= 170;
  return strip.Color((WheelPos * 3)/8, (255 - WheelPos * 3)/8, 0);
}

void clearLed() {
  for (uint16_t i = 0; i < strip.numPixels(); i++) {
    strip.setPixelColor(i, no_color);
  }
  strip.show();
}

void displayTime(uint8_t time) {
  int minutes = time % 12;
  int hours = time / 12;
  for (int t = 0; t < 2; t++) {
    for (uint16_t i = 0; i < strip.numPixels(); i++) {
      if (i == hours and i == minutes) {
        strip.setPixelColor((i + 3) % 12, mh_color);
      }
      if (i == hours and i != minutes) {
        strip.setPixelColor((i + 3) % 12, h_color);
      }
      if (i == minutes and i != hours) {
        strip.setPixelColor((i + 3) % 12, m_color);
      }
      if (i != hours and i != minutes) {
        strip.setPixelColor((i + 3) % 12, no_color);
      }
      strip.show();
      delay(5);
    }
  }
}

void rainbow(uint8_t wait) {
  clearLed();
  uint16_t i, j;
  
  for (i = 0; i < strip.numPixels(); i++) {
    strip.setPixelColor(i, Wheel((i * 256 / strip.numPixels()) & 255));
  }
  strip.show();
  delay(wait);
}

void connectedBluetooth() {
  clearLed();
  for (uint16_t i = 0; i < strip.numPixels(); i++) {
    strip.setPixelColor(i, bblue_color);
    strip.show();
    delay(100);
  }
  delay(1000);
}

void disconnectedBluetooth() {
  clearLed();
  for (uint16_t i = 0; i < strip.numPixels(); i++) {
    strip.setPixelColor(i, bred_color);
    strip.show();
    delay(100);
  }
  delay(1000);
}



//------Defining classes------//

class MyServerCallbacks: public BLEServerCallbacks {
  void onConnect(BLEServer * MyServer) {
    deviceConnected = true;
    Serial.println("BLE connected ");
    //connectedBluetooth();
  }
  void onDisconnect(BLEServer * MyServer) {
    deviceConnected = false;
    Serial.println("BLE disconnected ");
    //disconnectedBluetooth();
  }
};

class MyCallbacks: public BLECharacteristicCallbacks {
  void onWrite(BLECharacteristic * pCaracteristique) {
    std::string rxValue = pCaracteristique -> getValue();
    Serial.println("Received message ");
    for (int i = 0; i < rxValue.length(); i++) {
      if (i == 7) {
        strMode[i] = rxValue[i];
      }
      if (i >= 9) {
        strTime[i-9] = rxValue[i];
      }
      Serial.print(rxValue[i]);
    }
    
    if (strMode == mode1) {
      Serial.println("Trigger mode 1 ");
      mode_1();
      Serial.println("End mode 1 ");
    }
    if (strMode == mode2) {
      Serial.println("Trigger mode 2 ");
      mode_2();
      Serial.println("End mode 2 ");
    }
    if (strMode == mode3) {
      Serial.println("Trigger mode 3 ");
      mode_3();
      Serial.println("End mode 3 ");
    }
  }
};



//------Setup and loop------//

void setup() {
  Serial.begin(115200);
  Serial.println("Starting BLE ");
  
  BLEDevice::init("Orion ");
  BLEServer * MyServer = BLEDevice::createServer();
  MyServer -> setCallbacks(new MyServerCallbacks());
  BLEService * MyService = MyServer -> createService(SERVICE_UUID);
  
  MyService -> addCharacteristic( & Mycarac);
  MyService -> addCharacteristic( & pCaracteristique);
  pCaracteristique.setCallbacks(new MyCallbacks());
  Mycarac.addDescriptor(new BLE2902());
  MyService -> start();
  MyServer -> getAdvertising() -> start();
  Serial.println("Launching server ");

  ledcSetup(channel, freq, resolution);
  for (int i = 0; i < nbActuator - 1; i++) {
    ledcAttachPin(actPin[i], channel);
  }
  strip.begin();
  strip.show();
}

void loop() {
  if (deviceConnected) {
    if (c_status == 0) {
      connectedBluetooth();
      clearLed();
    }
    c_status = 1;
    displayTime(std::stoi(strTime));
  }
  else {
    if (c_status == 1) {
      disconnectedBluetooth();
      clearLed();
    }
    c_status = 0;
    rainbow(1);
  }
}
