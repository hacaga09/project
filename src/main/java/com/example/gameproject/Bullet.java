package com.example.gameproject;

import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;

public class Bullet {
    public Circle body;
    public boolean dead=false;
    public boolean isEnemyBullet;

    private double dx;
    private double dy;
    private double speed=25;

    public Bullet(double x, double y, double angle, boolean isEnemyBullet){
        body=new Circle(x, y, 4, Color.YELLOW);

        double rad=Math.toRadians(angle);
        dx=Math.sin(rad)*speed;
        dy=-Math.cos(rad)*speed;

        this.isEnemyBullet = isEnemyBullet;

        if(isEnemyBullet){
                body.setFill(Color.LIME);
        } else{
                body.setFill(Color.YELLOW);
        }
    }

    public void update(){
        body.setCenterX(body.getCenterX()+dx);
        body.setCenterY(body.getCenterY()+dy);
    }

    public void step(double factor){
        body.setCenterX(body.getCenterX()+(dx*factor));
        body.setCenterY(body.getCenterY()+(dy*factor));
    }
}
