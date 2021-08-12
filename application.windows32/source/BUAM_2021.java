import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.video.*; 
import gab.opencv.*; 
import java.awt.Rectangle; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class BUAM_2021 extends PApplet {

/**
 * ASCII Video
 * by Ben Fry. 
 *
 * 
 * Text characters have been used to represent images since the earliest computers.
 * This sketch is a simple homage that re-interprets live video as ASCII text.
 * See the keyPressed function for more options, like changing the font size.
 */






Capture video;
OpenCV opencv;
boolean cheatScreen;

// All ASCII characters, sorted according to their visual density
String letterOrder =
  " .`-_':,;^=+/\"|)\\<>)iv%xclrs{*}I?!][1taeo7zjLu" +
  "nT#JCwfy325Fp6mqSghVd4EgXPGZbYkOA&8U$@KHDBWNMR0Q";
char[] letters;

float[] bright;
char[] chars;

PFont font;
float fontSize = 1.1f;


public void setup() {
  

  // This the default video input, see the GettingStartedCapture 
  // example if it creates an error
  String[] cameras = Capture.list();
  delay(1000);
  printArray(cameras);
  
  try {
    video = new Capture(this, cameras[0]);
    
    opencv = new OpenCV(this, video.width, video.height);
    opencv.loadCascade(OpenCV.CASCADE_FRONTALFACE);  
    // Start capturing the images from the camera
    video.start();  
    
    int count = video.width * video.height;
    println("w; ", video.width, "h: ", video.height);
    //println(count);
  
    font = loadFont("Dialog.plain-48.vlw");
  
    // for the 256 levels of brightness, distribute the letters across
    // the an array of 256 elements to use for the lookup
    letters = new char[256];
    for (int i = 0; i < 256; i++) {
      int index = PApplet.parseInt(map(i, 0, 256, 0, letterOrder.length()));
      letters[i] = letterOrder.charAt(index);
    }
  
    // current characters for each position in the video
    chars = new char[count];
  
    // current brightness for each point
    bright = new float[count];
    for (int i = 0; i < count; i++) {
      // set each brightness at the midpoint to start
      bright[i] = 128;
    }
  } catch(Exception e) {
    stop();
    exit();
  }
}


public void captureEvent(Capture c) {
  c.read();
  opencv.loadImage(video);
}


public void draw() {
  background(0);

  pushMatrix();

  float hgap = width / PApplet.parseFloat(video.width);
  float vgap = height / PApplet.parseFloat(video.height);

  scale(max(hgap, vgap) * fontSize);
  textFont(font, fontSize);

  int index = 0;
  video.loadPixels();
  for (int y = 1; y < video.height; y++) {

    // Move down for next line
    translate(0,  1.0f / fontSize);

    pushMatrix();
    for (int x = 0; x < video.width; x++) {
      int pixelColor = video.pixels[index];
      // Faster method of calculating r, g, b than red(), green(), blue() 
      int r = (pixelColor >> 16) & 0xff;
      int g = (pixelColor >> 8) & 0xff;
      int b = pixelColor & 0xff;

      // Another option would be to properly calculate brightness as luminance:
      // luminance = 0.3*red + 0.59*green + 0.11*blue
      // Or you could instead red + green + blue, and make the the values[] array
      // 256*3 elements long instead of just 256.
      int pixelBright = max(r, g, b);

      // The 0.1 value is used to damp the changes so that letters flicker less
      float diff = pixelBright - bright[index];
      bright[index] += diff * 0.1f;

      fill(pixelColor);
      int num = PApplet.parseInt(bright[index]);
      text(letters[num], 0, 0);
      
      // Move to the next pixel
      index++;

      // Move over for next character
      translate(1.0f / fontSize, 0);
    }
    popMatrix();
  }
  popMatrix();

  if (cheatScreen) {
    //image(video, 0, height - video.height);
    // set() is faster than image() when drawing untransformed images
    set(0, height - video.height, video);
  }
  
  //noFill();
  //stroke(0, 255, 0);
  //strokeWeight(3);
  Rectangle[] faces = opencv.detect();
  textSize(180);
  fill(255,0,0);
  for (int i = 0; i < faces.length; i++) {
    //rect(faces[i].x*2, faces[i].y*2, faces[i].width*2, faces[i].height*2);
    text("XD", faces[i].x*2, faces[i].y*2);
  }
}


/**
 * Handle key presses:
 * 'c' toggles the cheat screen that shows the original image in the corner
 * 'g' grabs an image and saves the frame to a tiff image
 * 'f' and 'F' increase and decrease the font size
 */
public void keyPressed() {
  switch (key) {
    case 'g': saveFrame(); break;
    case 'c': cheatScreen = !cheatScreen; break;
    case 'f': fontSize *= 1.1f; break;
    case 'F': fontSize *= 0.9f; break;
  }
}
  public void settings() {  size(1280, 960); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "BUAM_2021" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
