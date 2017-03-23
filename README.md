# Motor.

Very much toy HTML rendering thingy.

## Some "what's next" thinking 2017-03-23

Possibilities:

1. Create a separate package containing the layouter.
2. Implement Lists, Bold, Italic, and other formattings. -> Display of these depends heavily on the platform, so implementing them in Java2D would mean twice the work for Android.
3. Implement links. -> Same argument as above, plus the fact that navigation is only useful on the target device.
4. Create an interfacing layer between the engine and the graphics

2 and 3 speak for moving to Android now. 4 is not needed yet. So: 1. Create package, and then move to Android.
   
## Quick sketch of which kind of tile needs which fields (deprecated)

Kind                            Coordinates     Node    Children    Parent      Other fields
Block tile                      ✓               ✓       ✓           ✓
Anonymous block tile            ✓                       ✓           ✓
Text tile                       ✓               ✓                   ✓           ✓
Root block tile                 ✓               ✓       ✓
