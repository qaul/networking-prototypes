# qaul.net prototypes

Here are two applications we used to test & debug some basic functionality about the protocols we're considering to build qaul.net on in the future. One is WiFi Direct, a new standard which can build peer-to-peer connections between devices. The other is Bluetooth mesh which, as the name sugests, creates mesh networks of bluetooth devices :)

Both applications are written in Java and for Android 5.1 or higher. They don't use any hardware specific code and should work on all modern phones.

## How to build

A modern(ish) version of Java, Gradle and optionally the Android Studio is required. With Android Studio you can simply import the projects into your workspace. From the Terminal you can also build them via gradle with `gradle build`. An apk file will be built for you.