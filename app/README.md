This is the repo for the Vuzix M400-C demonstration app.

What currently works:
- Can establish hooks into the device HID, Video, and Audio interface endpoints using USB APIs.
- Can play audio using MediaPlayer API.
- Can record audio using MediaRecorder API.
- Raw data from sensors can be received using USB APIs.
- Can 'see' button presses via USB APIs.

TODO List:
- Manipulate Sensor data to more closely resemble something that comes back from Android's SensorEvent object.
- Fix up the UI into something presentable.
- Work on providing an in-app way in which to provide access to the UVC Webcam component.
- Establish a better way with which to work with the buttons.
- Break out the data/domain layers into a separate library.