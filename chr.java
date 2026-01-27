package com.example.gameproject;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class chr {
    public static ImageView im=new ImageView(new Image("C:\\Users\\hacag\\OneDrive\\Рабочий стол\\перс1.png"));

    public static double X; public static double Y;
    public static double Xc; public static double Yc;
    public static double rot;
    public static int speed=12;

    public static double hwidth=98;
    public static double hheight=154;
    public static Rectangle hitbox=new Rectangle(0, 0, hwidth, hheight);

    public static void start(Stage stage){
        hitbox.setVisible(false);
    }

    public static void main(String[] args){
    }
}
