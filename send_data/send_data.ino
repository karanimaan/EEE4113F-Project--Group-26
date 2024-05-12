#include <ArduinoBLE.h> // Bluetooth Library


// BLE Service Name
BLEService customService("180C");

// BLE Characteristics
// Syntax: BLE<DATATYPE>Characteristic <NAME>(<UUID>, <PROPERTIES>, <DATA LENGTH>)
BLEStringCharacteristic ble_weight("2A56", BLERead | BLENotify, 13);

void setup()
{
    Serial.begin(9600);
    while (!Serial);
    if (!BLE.begin())
    {
        Serial.println("BLE failed to Initiate");
        delay(500);
        while (1);
    }

    // Setting BLE Name
    BLE.setLocalName("Arduino Nano 33 IoT");
    
    // Setting BLE Service Advertisment
    BLE.setAdvertisedService(customService);
    
    // Adding characteristics to BLE Service Advertisment
    customService.addCharacteristic(ble_weight);

    // Adding the service to the BLE stack
    BLE.addService(customService);

    // Start advertising
    BLE.advertise();
    Serial.println("Bluetooth device is now active, waiting for connections...");
}

void loop()
{
    BLEDevice central = BLE.central();  // Variable to check if cetral device is connected
    if (central)
    {
        Serial.print("Connected to central: ");
        Serial.println(central.address());
        while (central.connected())
        {
            delay(200);
            
            float weight = 346.2; // hardcoded value for unit test
            char weight_str[10];
            sprintf(weight_str, "%.1f", weight);
            ble_weight.writeValue(weight_str);  // Writing sensor values to the characteristic

            printf("%.1f", weight);

            delay(1000);
        }
    }
    Serial.print("Disconnected from central: ");
    Serial.println(central.address());
}


