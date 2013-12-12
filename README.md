Glass-ScreenOnOverlay
=====================

Briefly show date and battery level when screen comes on. After one second, the overlay slides off the top of the screen.

![Screenshot](https://github.com/TheMasterBaron/Glass-ScreenOnOverlay/blob/master/device-2013-12-08-200050.png?raw=true)

Change Log:
===========
* 1.1.1 Fixed 11st, 12nd, 13rd
* 1.1.0 Bigger font and day of neek next to day of month
* 1.0.1 Fixed to make it start on boot
* 1.0.0 Initial release


Intall Instructions
===================
1. Download and install the APK [here](https://github.com/TheMasterBaron/Glass-ScreenOnOverlay/raw/master/ScreenOnOverlay-debug-unaligned.apk)

2. Intall the APK
<code>adb install -r ScreenOnOverlay-debug-unaligned.apk</code>
3. After installing the APK, start using ScreenOnOverlay through adb using this command
<code>adb shell am start -n com.masterbaron.screenonoverlay/com.masterbaron.screenonoverlay.ConfigActivity</code>
  
