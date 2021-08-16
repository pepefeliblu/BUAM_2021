// Class for animating a sequence of GIFs

class Animation {
  PImage[] images;
  int imageCount;
  int frame;
  
  Animation(String imagePrefix, String imageType, int count) {
    imageCount = count;
    images = new PImage[imageCount];

    for (int i = 0; i < imageCount; i++) {
      // Use nf() to number format 'i' into two digits
      String filename = imagePrefix + nf(i, 3) + "." + imageType;
      images[i] = loadImage(filename);
      println(filename);
    }
  }

  void display(float xpos, float ypos, int sizeX, int sizeY) {
    frame = (frame+1) % imageCount;
    images[frame].resize(sizeX, sizeY);
    image(images[frame], xpos, ypos);
  }
  
  int getWidth() {
    return images[0].width;
  }
}
