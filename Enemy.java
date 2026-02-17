package com.example.gameproject;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class Enemy {
    public ImageView im = new ImageView(new Image("C:\\Users\\hacag\\Downloads\\enemyknife.png"));
    public Rectangle hitbox;
    public boolean dead = false;

    public int hp = 3;
    public double speed = 2;
    public boolean seePlayer = false;

    public Enemy(double startX, double startY) {
        im.setX(startX);
        im.setY(startY);

        double w = im.getImage().getWidth();
        double h = im.getImage().getHeight();
        hitbox = new Rectangle(startX, startY, w, h);
        hitbox.setVisible(false);

        im.setX(startX - im.getImage().getWidth()/2);
        im.setY(startY - im.getImage().getHeight()/2);

        hitbox.setX(startX - w/2);
        hitbox.setY(startY - h/2);
    }

    public void update(double px, double py, Pane walls) {
        if (checkSight(px, py, walls)) {
            seePlayer = true;
            moveTowards(px, py, walls);
        } else {
            seePlayer = false;
        }
    }

    private boolean checkSight(double px, double py, Pane walls) {
        double myX = hitbox.getX() + hitbox.getWidth() / 2;   //коорды "глаз" врага
        double myY = hitbox.getY() + hitbox.getHeight() / 2;

        double dx = px - myX;
        double dy = py - myY;


        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance < 5) return true;
        double stepX = dx / distance;
        double stepY = dy / distance;

        double currentX = myX;
        double currentY = myY;

        for (double i = 0; i < distance; i += 10) {
            currentX += stepX * 10;
            currentY += stepY * 10;

            for (int j = 0; j < walls.getChildren().size(); j++) {
                Node wall = walls.getChildren().get(j);

                if (wall.getBoundsInParent().contains(currentX, currentY)) {
                }
            }
        }

        return true;
    }




    private void moveTowards(double px, double py, Pane walls) {
        double myX = hitbox.getX() + hitbox.getWidth()/2;
        double myY = hitbox.getY() + hitbox.getHeight()/2;

        double dx = px - myX;
        double dy = py - myY;
        double angle = Math.atan2(dy, dx);

        im.setRotate(Math.toDegrees(angle) + 90);
        hitbox.setRotate(Math.toDegrees(angle) + 90);

        double moveX = Math.cos(angle) * speed;
        double moveY = Math.sin(angle) * speed;

        if (!isColliding(moveX, 0, walls)) {
            im.setX(im.getX() + moveX);
            hitbox.setX(hitbox.getX() + moveX);
        }

        if (!isColliding(0, moveY, walls)) {
            im.setY(im.getY() + moveY);
            hitbox.setY(hitbox.getY() + moveY);
        }
    }

    private boolean isColliding(double dx, double dy, Pane walls) {
        //создание копии хб там, куда шагнем
        Rectangle futureBox = new Rectangle(
                hitbox.getX() + dx,
                hitbox.getY() + dy,
                hitbox.getWidth(),
                hitbox.getHeight()
        );

        for (int i = 0; i < walls.getChildren().size(); i++) {
            Node wall = walls.getChildren().get(i);
            if (futureBox.getBoundsInParent().intersects(wall.getBoundsInParent())) {
                return true; //врезались
            }
        }
        return false; //еслт получется пройти (вободно)
    }
}
