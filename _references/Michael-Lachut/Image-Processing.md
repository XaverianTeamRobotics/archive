---
permalink: /reference/Michael-Lachut/image-pain
---

So, you would like to process images?
You don't want to, but I will make the docs anyway.
All the hard work has already been done.

Include this package (it's not really necessary, Android Studio does it automatically):
```java
import org.firstinspires.ftc.teamcode.image.TFLITE_Wrapper;
```

Step 2: Make yourself a fresh new instance of the `TFLITE_Wrapper` class.

**But wait!** A wild constructor has appeared. To defeat it, do one of the following
  1. Pass a single paramater: hardwareMap.
    1. You can then customize all the properties, like the model (`TFOD_MODEL_ASSET`), camera name (`CAMERA_NAME`), and labels (`LABELS`)
  2. Pass ALL the parameters
    1. A Vuforia key which can be found under `TeamCode/res/values/strings/strings.xml`
    2. The name of your asset file. See below to choose an asset file
    3. The labels of the objects (in array form). Once again, see below
    4. The hardware map
    5. The name of the Webcam

I'm sure you can see which one I would prefer...

**Notes**
There are three asset files:
  * FreightFrenzy_BCDM.tflite
  * FreightFrenzy_BC.tflite
  * FreightFrenzy_DM.tflite

Just listen to what FTC has to say about them
```
Note: This sample uses the all-objects Tensor Flow model (FreightFrenzy_BCDM.tflite), which contains
the following 4 detectable objects                                                                  
 0: Ball,                                                                                           
 1: Cube,                                                                                           
 2: Duck,                                                                                           
 3: Marker (duck location tape marker)                                                              
                                                                                                    
 Two additional model assets are available which only contain a subset of the objects:              
 FreightFrenzy_BC.tflite  0: Ball,  1: Cube                                                         
 FreightFrenzy_DM.tflite  0: Duck,  1: Marker                                                       
```

These all correspond to the labels array. Though the names don't matter, the order is fixed. On example of a valid labels array would be:
```java
new String[]{"Ball", "Cube", "Duck", "Marker"}
```


**Now, onto the usage.**

To use, you must complete a plethora of steps

  1. use the `.init()` method
  2. use the `.activate()` method when ready to activate
  3. `.setZoom(magnitude, aspectRatio)` if desired (aspect ratio should be `16/9`)
  4. use the `.getUpdatedRecognitions()` method, which return a `List<Recognition>`. It is up to you to implement it.

**BUT WAIT!!!** A NEW challenger has appeared: *Code Instability*.
To solve this pressing issue, **ALWAYS** surround any usage of `.activate()` and `.getUpdatedRecognitions()` with this simple line (or 3) 
```java
if (TFLITE_Wrapper.tfod != null) {
    // YOUR CODE HERE
}
```

And lastly, you can always look at the extremely lengthy and hard to understand code which I have already made. You're welcome.