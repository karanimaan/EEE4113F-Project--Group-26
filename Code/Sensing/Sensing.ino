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

//====================================================================
// GLOBAL CONSTANTS
//====================================================================
#define SIZE 10
#define FALSE 0
#define TRUE 1
#define PERIOD 200
//====================================================================
// GLOBAL VARIABLES
//====================================================================
float voltages[SIZE+1]; // Arrray of previous voltage values
int i;                  // Index of current value 
int full;               // Indicates whether array is full or not
//====================================================================
// FUNCTION DECLARATIONS
//====================================================================
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

float voltsToGrams(float Vout) {  // will update later
  float Vin = 1/994*(2*Vout - 3.3);
  
  return Vin;  //Convert input voltage to weight
}
//====================================================================
// MAIN LOOP and SETUP
//====================================================================
void setup() 
{
  // Initialise variables
  i = 0;
  full = FALSE;
  voltages[SIZE] = 0;

  // Setup Serial connection
  Serial.begin(9600);

  // Use internal voltage reference for ADC
  analogReference(AR_INTERNAL);

  // Change the ADC resolution to 12 bits
  analogReadResolution(12);

}

void loop() 
{
  // Measure execution time
  long int t = millis();

  // Read the voltage on A0
  uint16_t adcVal = analogRead(A1);   // ADC value
  float V = adcVal * 3.3 / 4096;      // Convert to a voltage
  
  if(!full) {
    voltages[i] = V;
    voltages[SIZE] += V;
    V = voltages[SIZE]/(i+1);
    i += 1;
    if(i>=SIZE) {
      full = TRUE;
      i = 0;
    }
    
  }
  else {
    voltages[i] = V;
    V = movingAverage(voltages, i);
    i = (i+1 < SIZE) ? i+1 : 0;
    
  }
  V = (1/1.161)*(V-0.09);
  String str = "ADC Val = ";
  Serial.print(str);
  Serial.print(adcVal);
  str = "\tV =  ";
  Serial.print(str);
  Serial.println(V);

  t = t-millis();
  delay(PERIOD-t);


}						// End of main loop

//********************************************************************
// END OF PROGRAM
//********************************************************************