package com.example.game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class chr {
    public static ImageView im = new ImageView(new Image("C:\\Users\\hacag\\OneDrive\\Рабочий стол\\перс1.png"));
    public static double X = 960-70; public static double Y = 540-100;
    public static double Xc = X+im.getImage().getWidth()/2; public static double Yc = Y+im.getImage().getHeight()/2;
    public static double rot;
    public static int speed = 17;

    public static double hwidth = 98; public static double hheight = 154;
    public static Rectangle hitbox = new Rectangle(X,Y,hwidth,hheight);


    public static void start(Stage stage){
        hitbox.setVisible(false);

    }
    public static void main(String[] args) {

    }
}
