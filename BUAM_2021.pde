/**
 * ASCII Video
 * by Ben Fry. 
 *
 * 
 * Text characters have been used to represent images since the earliest computers.
 * This sketch is a simple homage that re-interprets live video as ASCII text.
 * See the keyPressed function for more options, like changing the font size.
 */

import processing.video.*;
import gab.opencv.*;
import java.awt.Rectangle;


Capture video;
OpenCV opencv;
boolean cheatScreen;


int counter = 0;

Animation animation;

void setup() {
  size(640, 480);
  frameRate(24);
  // This the default video input, see the GettingStartedCapture 
  // example if it creates an error
  String[] cameras = Capture.list();
  
  animation = new Animation("anim/ente", "png", 600);
  delay(3000);
  textSize(32);
  fill(0);
  text("LOADING", width/2, height/2);
  delay(3000);
  printArray(cameras);
  
  try {
    video = new Capture(this, cameras[0]);
    opencv = new OpenCV(this, video.width, video.height);
    opencv.loadCascade(OpenCV.CASCADE_FRONTALFACE);  
    // Start capturing the images from the camera
    video.start();  
    
    println("w; ", video.width, "h: ", video.height);
    //printl

  } catch(Exception e) {
    stop();
    exit();
  }
}


void captureEvent(Capture c) {
  c.read();
  opencv.loadImage(video);
}


void draw() {
  background(0);



  if (!cheatScreen) {
    //image(video, 0, height - video.height);
    // set() is faster than image() when drawing untransformed images
    tint(255, 210);
    set(0, 0, video);
  }
  
  noFill();
  stroke(0, 255, 0);
  strokeWeight(3);
  Rectangle[] faces = opencv.detect();
  
  
  for (int i = 0; i < faces.length; i++) {
    int offset = (int) (faces[i].width*0.5);
    rect(faces[i].x-offset, faces[i].y-offset, faces[i].width+offset*2, faces[i].height+offset*2);
    animation.display(faces[i].x-offset, faces[i].y-offset, faces[i].width+offset*2, faces[i].height+offset*2);
    //typewriteText(phrases[int(phrases.length-1)], faces[i].x*2, faces[i].y*2);
   //image(meme.get(r), faces[r].x*2, faces[r].y*2);
  }
}



/**
 * Handle key presses:
 * 'c' toggles the cheat screen that shows the original image in the corner
 * 'g' grabs an image and saves the frame to a tiff image
 * 'f' and 'F' increase and decrease the font size
 */
void keyPressed() {
  switch (key) {
    case 'g': saveFrame(); break;
    case 'c': cheatScreen = !cheatScreen; break;
  }
}
