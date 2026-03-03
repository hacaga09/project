package com.example.gameproject;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

public class cursor {
    public static ImageView im = new ImageView(new Image(cursor.class.getResource("/crosshair.png").toExternalForm()));
    public static double X;// = (im.getX() + im.getFitWidth()) / 2;
    public static double Y;// = (im.getY() + im.getFitHeight()) / 2;
    //public static double Rad = 20;

}
