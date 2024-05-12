//********************************************************************
//*                    EEE4113F Project                              *
//*==================================================================*
//* WRITTEN BY:       Mpilonhle Nxasana               *
//* DATE CREATED:     04/05/2024                                     *
//*==================================================================*
//* PROGRAMMED IN:    Arduino IDE                                    *
//* TARGET:           Arduino NANO 33 IoT                            *
//*==================================================================*
//* DESCRIPTION:                                                     *
//* EEE4113F Arduino Code for sensing subsystem                      *
//********************************************************************
// INCLUDE FILES
//====================================================================
#include "ADS1X15.h"
#include <string>
//====================================================================
// GLOBAL CONSTANTS
//====================================================================
#define FALSE 0
#define TRUE 1
#define SIZE 10
#define PERIOD 100
#define BUTTON_PIN 2
#define LED_PIN 3
#define DEBOUNCE_DELAY 250
//====================================================================
// GLOBAL VARIABLES
//====================================================================
float weights[SIZE+1];     // Arrray of previous voltage values
int i;                    // Index of current value 
int full;                 // Indicates whether array is full or not
float offset;             // Weight offset
volatile long last_press; // Time since last button press
ADS1115 ADS(0x48);        // Constructor for ADS1115 , default I2C address 0x48 is used
//====================================================================
// FUNCTION DECLARATIONS
//====================================================================
void buttonPressedISR() 
{
  if(millis() - last_press > DEBOUNCE_DELAY){
    digitalWrite(LED_PIN, !digitalRead(LED_PIN));
    last_press = millis();
  }
}

float movingAverage(float* values, int index)
{
  float sum = values[SIZE]; 
  sum += values[index];
  if(index+1<SIZE) {
    sum -= values[index+1];
  }
  else {
    sum -= values[0];
  }
  values[SIZE] = sum;
  return sum/SIZE;
}

float voltsToGrams(float V) { 
  float Vin = (2*V -3.3)/994;           // Determine input voltage from output voltage
  float weight = 1000*Vin/(3.3*0.001);  // Determine weight from input voltage
//  float weight = (V-1.5935)/0.0015;   // Linear regression model
  return weight;  
}
//====================================================================
// SETUP and LOOP
//====================================================================
void setup() 
{
  // Initialise variables
  i = 0;
  full = FALSE;
  weights[SIZE] = 0;
  offset = 0;
  last_press = 0;

  // Setup Serial connection
  Serial.begin(9600);

  // Setup the ADS1115
  Wire.begin();
  ADS.begin();
  ADS.setGain(1); // Increase gain on pga for more precise measurements
  ADS.setDataRate(1); // Set data rate to 16sps

  // Setup input and output pins
  pinMode(BUTTON_PIN, INPUT_PULLUP);
  pinMode(LED_PIN, OUTPUT);
  // Setup interrupt on push buttom
  attachInterrupt(digitalPinToInterrupt(BUTTON_PIN), buttonPressedISR, FALLING);
  // Set LED to initially be OFF
  digitalWrite(LED_PIN, LOW);

}

void loop() 
{
  ADS.setGain(0);

  // Measure execution time
  long int t = millis();

  // Read the voltage on channel A0
  int16_t adcVal = ADS.readADC(0);  // Get adc value
  float f = ADS.toVoltage(1);       // voltage factor
  float V = adcVal * f;             // convert to a voltage
  float weight = voltsToGrams(V);   // convert voltage to weight
  // Apply offset to weight
  weight = (weight-offset > 0) ? weight-offset : 0;

  if(!full) {
    weights[i] = weight;
    weights[SIZE] += weight;
    weight = weights[SIZE]/(i+1);
    i += 1;
    if(i>=SIZE) {
      full = TRUE;
      i = 0;
    }
    
  }
  else {
    weights[i] = weight;
    weight = movingAverage(weights, i);
    i = (i+1 < SIZE) ? i+1 : 0;
  }

  if(digitalRead(LED_PIN)) {
    if(offset<=0) {
      offset = weight;
    }
  }
  else {
    offset = 0;
  }

  Serial.print("V = "); Serial.print(V);
  char arr [15] = "";
  sprintf(arr,"\tWeight = %.1f", weight);
  String str(arr);
  Serial.println(str);

  t = t-millis();
  delay(PERIOD-t);


}						// End of main loop

//********************************************************************
// END OF PROGRAM
//********************************************************************