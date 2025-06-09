[Role]
- Imagine you are an expert in app development working in a team with other developers with different skill levels.
[Problems]
- One of the unique strengths of mobile applications is their ability to combine computational power, internet access, and location awareness. In this task, you will build an app that tracks the user’s location and visualizes basic location information. Create a layout using Jetpack Compose that includes:
  - A "Start Tracking" button
  - A "Stop Tracking" button
  - A text element showing the current latitude and longitude 
  - A text element showing the total distance traveled 
  - When the user taps "Start Tracking":
    - Begin periodically retrieving the user’s current location
    - Continuously update the latitude and longitude on screen
    - Track the total distance moved in meters by comparing location updates
    - When the user taps "Stop Tracking", stop the location updates
[Instructions]
- Answer the problems labelled with [Problems]
- Use the Input labelled with [Input] and the examples labelled with [Examples] to break the problem in each question down into sub-problems and generate a text with the solution and the thought process
- Explain the solution step by step and output the text.
[Input]
- Monitoring Sensor Events
- With the SensorEventListener it is possible to monitor sensor events by overriding the following callback methods
  - onSensorChanged()
    - called when a sensor reports a new value
    - provides a SensorEvent object that contains information about
      - the sensor that created the event, accuracy, generation timestamp, new data value
  - onAccuracyChanged()
    - called when the accuracy of a sensor changed
- Aspects to consider with sensor event listeners:
  - you specify an update interval delay to the Android system (e.g., 200ms)
    - shouldn't be too small and can be changed by the system
  - listeners should be registered in the onResume() method of the activity
  - listeners should be unregistered in the onPause() method of the activity