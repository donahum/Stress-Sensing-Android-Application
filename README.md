# StressApp
ECE49022 Senior Design Project - Android Application for Stress Sensing Device UI

This is an Android application for my ECE49022 senior design project.  The application is responsible for communicating with a Back4app database for storing login credentials, user information, and sensor statistics acquired via Bluetooth Low Energy.  Data is captured by an ESP32 microcontroller and forwarded via BLE to the cell phone.  This includes O2 level, power level, galvanic skin response, electromyography, heart rate, and body temperature signaling from two separate ESP32s.  

After receiving this data via BLE and forwarding it for storage in a server, this application will apply a stress quantification algorithm from the sensor data to determine your stress level.  Then a user may view their session data in a graphical format.
