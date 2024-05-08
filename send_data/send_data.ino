#include <ArduinoBLE.h>           // Bluetooth Library


// Initalizing global variables for sensor data to pass onto BLE
String p, t, m;

// BLE Service Name
BLEService customService("180C");

// BLE Characteristics
// Syntax: BLE<DATATYPE>Characteristic <NAME>(<UUID>, <PROPERTIES>, <DATA LENGTH>)
BLEStringCharacteristic ble_pressure("2A56", BLERead | BLENotify, 13);

// Function prototype
void readValues();

void setup()
{
    // Initalizing all the sensors

    Serial.begin(9600);
    while (!Serial);
    if (!BLE.begin())
    {
        Serial.println("BLE failed to Initiate");
        delay(500);
        while (1);
    }

    // Setting BLE Name
    BLE.setLocalName("Arduino Environment Sensor");
    
    // Setting BLE Service Advertisment
    BLE.setAdvertisedService(customService);
    
    // Adding characteristics to BLE Service Advertisment
    customService.addCharacteristic(ble_pressure);

    // Adding the service to the BLE stack
    BLE.addService(customService);

    // Start advertising
    BLE.advertise();
    Serial.println("Bluetooth device is now active, waiting for connections...");
}

void loop()
{
    // Variable to check if cetral device is connected
    BLEDevice central = BLE.central();
    if (central)
    {
        Serial.print("Connected to central: ");
        Serial.println(central.address());
        while (central.connected())
        {
            delay(200);
            
            // Read values from sensors
            float weight = 346.2;
            char weight_str[10];
            sprintf(weight_str, "%.1f", weight);
            // Writing sensor values to the characteristic
            ble_pressure.writeValue(weight_str);



            // Displaying the sensor values on the Serial Monitor
            Serial.println("Reading Sensors");

            Serial.println("\n");
            delay(1000);
        }
    }
    Serial.print("Disconnected from central: ");
    Serial.println(central.address());
}


