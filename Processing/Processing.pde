import processing.video.*;

Capture video;

color trackColor;
int x, y;
float minDist = Float.MAX_VALUE;

void setup(){
    fullScreen();
    String[] camaras = Capture.list();
    video = new Capture(this, camaras[0]);
    video.start();

    trackColor = color(255,0,0);
}

void captureEvent(Capture video){
    video.read();
}

void mousePressed(){
    int x = (int)map(mouseX, 0,width, 0,video.width);
    int y = (int)map(mouseY, 0,height, 0,video.height);
    int loc = getPixelLoc(x, y);
    trackColor = video.pixels[loc];
}

void draw(){
    video.loadPixels();
    image(video,0,0,width,height);
    minDist = Float.MAX_VALUE;
    for(int x = 0; x < video.width; x++){
        for(int y = 0; y < video.height; y++){
            int loc = getPixelLoc(x,y);
            color cColor = video.pixels[loc];
            float cDist = getDistanceFromTarget(cColor);
            if(cDist < minDist){
                minDist = cDist;
                this.x = x;
                this.y = y;
                // this.x = (int)lerp(this.x, x, 0.01);
                // this.y = (int)lerp(this.y, y, 0.01);
            }
        }
    }
    stroke(255,0,0);
    fill(trackColor);
    int size = 12;
    int x = (int)map(this.x, 0,video.width, 0,width);
    int y = (int)map(this.y, 0,video.height, 0,height);
    ellipse(x, y, size, size);
}

int getPixelLoc(int x, int y){
    return x + y*video.width;
}

float getDistanceFromTarget(color cColor){
    float red = red(cColor);
    float green = green(cColor);
    float blue = blue(cColor);
    return (red(trackColor) - red)*(red(trackColor) - red) + 
        (green(trackColor) - green)*(green(trackColor) - green) +
        (blue(trackColor) - blue)*(blue(trackColor) - blue);
}