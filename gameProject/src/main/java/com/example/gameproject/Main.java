package com.example.gameproject;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.util.*;

public class Main extends Application {
    private static final double zoom = 2.5;
    private final double screenw = 1920;
    private final double screenh = 1080;
    private final double centerx = screenw / 2.0;
    private final double centery = screenh / 2.0;

    public ImageView bgr = new ImageView();
    public final Set<KeyCode> keys = new HashSet<>();
    public Pane map;
    public Pane walls;
    public List<Bullet> bullets = new ArrayList<>();
    public List<Enemy> enemies = new ArrayList<>();

    private boolean shooting = false;
    private long lastshot = 0;
    private long firerate = 150000000;
    public double startX = 475;
    public double startY = 850;

    private boolean gameOver = false;
    private Label gameOverLabel;
    private Rectangle bgRect;
    private double hue = 0;

    public int unlockedLevels = 1;
    public int currentLevel = 1;
    public Rectangle exitZone;
    public AnimationTimer timer;
    private Scene menuScene;
    private Scene gameScene;
    private Stage window;

    public Rectangle finalWallTexture;
    public Rectangle finalWallTexture2;

    public int maxAmmo = 4;
    public int currentAmmo = 4;
    private boolean isReloading = false;
    private long reloadStartTime = 0;
    private final long reloadTime = 2_000_000_000L;
    private Label ammoLabel;

    @Override
    public void start(Stage stage) {
        window = stage;
        Controller controller = new Controller();
        window.setTitle("Hotline JavaFX");

        map = new Pane();
        bgr.setLayoutX(0);
        bgr.setLayoutY(0);
        map.getTransforms().add(new Scale(zoom, zoom, 0, 0));
        map.getChildren().add(bgr);

        walls = new Pane();
        walls.setOpacity(0.5);

        map.getChildren().addAll(walls);

        chr.X = centerx;
        chr.Y = centery;
        chr.Xc = centerx;
        chr.Yc = centery;

        chr.hitbox.setFill(Color.TRANSPARENT);

        cursor.im.setScaleX(0.1);
        cursor.im.setScaleY(0.1);

        gameOverLabel = new Label("YOU DIED");
        gameOverLabel.setTextFill(Color.RED);
        gameOverLabel.setFont(new Font("Arial", 100));
        gameOverLabel.setLayoutX(screenw / 2 - 250);
        gameOverLabel.setLayoutY(screenh / 2 - 50);
        gameOverLabel.setVisible(false);

        ammoLabel = new Label("AMMO: " + currentAmmo + " / " + maxAmmo);
        ammoLabel.setTextFill(Color.WHITE);
        ammoLabel.setFont(new Font("Arial", 40));
        ammoLabel.setLayoutX(220);
        ammoLabel.setLayoutY(100);

        bgRect = new Rectangle(0, 0, screenw, screenh);

        Pane root = new Pane();
        root.getChildren().addAll(bgRect, map, chr.hitbox, chr.im, cursor.im, gameOverLabel, ammoLabel);

        gameScene = new Scene(root, screenw, screenh);
        gameScene.setCursor(Cursor.NONE);

        gameScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> keys.add(event.getCode()));
        gameScene.addEventFilter(KeyEvent.KEY_RELEASED, event -> keys.remove(event.getCode()));


        gameScene.addEventFilter(MouseEvent.MOUSE_MOVED, this::mouse);
        gameScene.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::mouse);
        gameScene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> shooting = true);
        gameScene.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> shooting = false);

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (gameOver) {
                    loadLevel(2);
                    return;
                }

                if (!keys.isEmpty()){
                    move(controller);
                }

                if (isReloading) {
                    if (now - reloadStartTime >= reloadTime) {
                        isReloading = false;
                        currentAmmo = maxAmmo;
                        ammoLabel.setText("AMMO: " + currentAmmo + " / " + maxAmmo);
                        ammoLabel.setTextFill(Color.WHITE);
                    }
                }

                if (keys.contains(KeyCode.R) && currentAmmo < maxAmmo && !isReloading) {
                    isReloading = true;
                    reloadStartTime = now;
                    ammoLabel.setText("RELOADING");
                    ammoLabel.setTextFill(Color.YELLOW);
                }

                if (shooting && !isReloading && (now - lastshot >= firerate)) {
                    if (currentAmmo > 0) {
                        shoot();
                        lastshot = now;
                        currentAmmo--;
                        ammoLabel.setText("AMMO: " + currentAmmo + " / " + maxAmmo);

                        if (currentAmmo == 0) {
                            isReloading = true;
                            reloadStartTime = now;
                            ammoLabel.setText("RELOADING...");
                            ammoLabel.setTextFill(Color.YELLOW);
                        }
                    }
                }

                if (enemies.isEmpty()) {
                    walls.getChildren().removeAll(wall.FINAL, wall.FINAL2);
                    if (finalWallTexture != null) {
                        map.getChildren().removeAll(finalWallTexture, finalWallTexture2);
                    }
                }

                Point2D p = map.sceneToLocal(chr.X, chr.Y);
                if (exitZone != null && exitZone.getBoundsInParent().contains(p)) {
                    if (currentLevel == 1) {
                        unlockedLevels = 2;
                    } else{
                        unlockedLevels = 3;
                    }
                    menu();
                }

                updbullets();
                updateEnemies();
                checkPlayerDeath();
                updateBackground();
                fixpos();
            }
        };

        menu();
        window.setResizable(false);
        window.show();
    }

    private void updateBackground() {
        hue += 1;
        if (hue > 360){
            hue = 0;
        }

        Color c1 = Color.hsb(hue, 0.8, 0.8);
        Color c2 = Color.hsb(hue - 60, 0.8, 0.4);

        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, c1),
                new Stop(1, c2)
        );
        bgRect.setFill(gradient);
    }

    private void spawnEnemy(double x, double y) {
        Enemy e = new Enemy(x, y);
        enemies.add(e);
        map.getChildren().add(e.hitbox);
        map.getChildren().add(e.im);
    }

    private void updateEnemies() {
        Point2D p = map.sceneToLocal(chr.X, chr.Y);
        Iterator<Enemy> iter = enemies.iterator();
        while (iter.hasNext()) {
            Enemy e = iter.next();
            if (!e.dead) {
                e.update(p.getX(), p.getY(), walls);
            } else {
                map.getChildren().remove(e.im);
                map.getChildren().remove(e.hitbox);
                iter.remove();
            }
        }
    }

    private void checkPlayerDeath() {
        Point2D p = map.sceneToLocal(chr.X, chr.Y);
        for (int i = 0; i < enemies.size(); i++) {
            Enemy e = enemies.get(i);
            double ex = e.hitbox.getX() + e.hitbox.getWidth() / 2;
            double ey = e.hitbox.getY() + e.hitbox.getHeight() / 2;
            double dist = Math.sqrt(Math.pow(p.getX() - ex, 2) + Math.pow(p.getY() - ey, 2));
            if (dist < 50) {
                gameOver = true;
                gameOverLabel.setVisible(true);
            }
        }
    }

    private void fixpos() {
        double imgw = chr.im.getImage().getWidth();
        double imgh = chr.im.getImage().getHeight();
        if (imgw == 0) return;
        chr.im.setX(centerx - imgw / 2);
        chr.im.setY(centery - imgh / 2);
        chr.hitbox.setX(centerx - chr.hwidth / 2);
        chr.hitbox.setY(centery - chr.hheight / 2);
    }

    private void move(Controller controller) {
        if (keys.contains(KeyCode.W) && !hit(0, chr.speed)) controller.up(map);
        if (keys.contains(KeyCode.S) && !hit(0, -chr.speed)) controller.down(map);
        if (keys.contains(KeyCode.A) && !hit(chr.speed, 0)) controller.left(map);
        if (keys.contains(KeyCode.D) && !hit(-chr.speed, 0)) controller.right(map);
    }

    private void shoot() {
        double rot = chr.im.getRotate();
        double dist = 80;
        double rad = Math.toRadians(rot);
        double mx = centerx + (Math.sin(rad) * dist);
        double my = centery - (Math.cos(rad) * dist);
        Point2D p = map.sceneToLocal(mx, my);
        Bullet b = new Bullet(p.getX(), p.getY(), rot);
        bullets.add(b);
        map.getChildren().add(b.body);
    }

    private void updbullets() {
        Iterator<Bullet> iter = bullets.iterator();
        while (iter.hasNext()) {
            Bullet b = iter.next();
            int steps = 5;
            for (int i = 0; i < steps; i++) {
                b.step(1.0 / steps);
                boolean impact = false;

                for (int j = 0; j < walls.getChildren().size(); j++) {
                    Node n = walls.getChildren().get(j);
                    if (b.body.getBoundsInParent().intersects(n.getBoundsInParent())) {
                        b.dead = true;
                        impact = true;
                        break;
                    }
                }

                if (!impact) {
                    for (int k = 0; k < enemies.size(); k++) {
                        Enemy e = enemies.get(k);
                        if (b.body.getBoundsInParent().intersects(e.hitbox.getBoundsInParent())) {
                            e.hp--;
                            if (e.hp <= 0) {
                                e.dead = true;
                            }
                            b.dead = true;
                            impact = true;
                            break;
                        }
                    }
                }
                if (impact){
                    break;
                }
            }

            if (b.dead) {
                map.getChildren().remove(b.body);
                iter.remove();
            }
        }
    }

    private boolean hit(double dx, double dy) {
        double nmx = map.getLayoutX() + dx;
        double nmy = map.getLayoutY() + dy;
        for (int i = 0; i < walls.getChildren().size(); i++) {
            Bounds b = walls.getChildren().get(i).localToScene(walls.getChildren().get(i).getBoundsInLocal());
            double wx = b.getMinX() + (nmx - map.getLayoutX());
            double wy = b.getMinY() + (nmy - map.getLayoutY());
            if (centerx + chr.hwidth / 2 > wx && centerx - chr.hwidth / 2 < wx + b.getWidth() &&
                    centery + chr.hheight / 2 > wy && centery - chr.hheight / 2 < wy + b.getHeight()){
                return true;
            }
        }
        return false;
    }

    private void mouse(MouseEvent event) {
        cursor.im.setLayoutX(event.getX() - cursor.im.getImage().getWidth() / 2);
        cursor.im.setLayoutY(event.getY() - cursor.im.getImage().getWidth() / 2);
        double dx = event.getX() - centerx;
        double dy = event.getY() - centery;
        chr.rot = Math.toDegrees(Math.atan2(dy, dx));
        double ang = chr.rot + 90;
        chr.im.setRotate(ang);
        chr.hitbox.setRotate(ang);
    }

    private void menu() {
        if (timer != null) {
            timer.stop();
        }
        VBox layout = new VBox(30);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: black;");
        Label title = new Label("HOTLINE JAVAFX");
        title.setTextFill(Color.WHITE);
        title.setFont(new Font("Arial", 60));

        Button b1 = new Button("LEVEL 1");
        b1.setFont(new Font("Arial", 30));
        b1.setOnAction(e -> loadLevel(1));

        Button b2 = new Button("LEVEL 2");
        b2.setFont(new Font("Arial", 30));
        if (unlockedLevels < 2) {
            b2.setDisable(true);
            b2.setText("LEVEL 2 (LOCKED)");
        }
        b2.setOnAction(e -> loadLevel(2));

        Button b3 = new Button("LEVEL 3");
        b3.setFont(new Font("Arial", 30));
        if (unlockedLevels < 3) {
            b3.setDisable(true);
            b3.setText("LEVEL 3 (LOCKED)");
        }
        b3.setOnAction(e -> loadLevel(3));

        layout.getChildren().addAll(title, b1, b2, b3);
        menuScene = new Scene(layout, screenw, screenh);
        window.setScene(menuScene);
    }

    private void loadLevel(int level) {
        currentLevel = level;
        gameOver = false;
        gameOverLabel.setVisible(false);

        currentAmmo = maxAmmo;
        isReloading = false;
        ammoLabel.setText("AMMO: " + currentAmmo + " / " + maxAmmo);
        ammoLabel.setTextFill(Color.WHITE);

        keys.clear();
        bullets.clear();
        enemies.clear();
        walls.getChildren().clear();
        if (exitZone != null) {
            map.getChildren().remove(exitZone);
        }

        if (level == 1) {
            bgr.setImage(new Image(getClass().getResource("/map.png").toExternalForm()));
            walls.getChildren().addAll(wall.FINAL, wall.w1, wall.w2, wall.w3, wall.w4, wall.w5, wall.w6,
                    wall.w7, wall.w8, wall.w9, wall.w10, wall.w11, wall.w12,
                    wall.w13, wall.w14, wall.w15, wall.w16, wall.w17, wall.w18,
                    wall.w19, wall.w20, wall.w21, wall.w22, wall.w23, wall.w24, wall.w25);

            if (finalWallTexture != null) {
                map.getChildren().remove(finalWallTexture);
            }
            finalWallTexture = new Rectangle(494, 240, 60, 5);
            finalWallTexture.setFill(Color.BROWN);
            map.getChildren().add(finalWallTexture);

            spawnEnemy(500, 500);
            spawnEnemy(800, 300);
            spawnEnemy(680, 670);
            spawnEnemy(70, 285);
            spawnEnemy(110, 455);
            spawnEnemy(103, 810);

            exitZone = new Rectangle(495, 12, 80, 28);
            exitZone.setFill(Color.GREEN);
            map.getChildren().add(exitZone);

            map.setLayoutX(centerx - (startX * zoom));
            map.setLayoutY(centery - (startY * zoom));
        } else if (level == 2) {
            bgr.setImage(new Image(getClass().getResource("/l2map.png").toExternalForm()));
            walls.getChildren().addAll(wall.w26, wall.w27, wall.w28, wall.w29, wall.w30,
                    wall.w31, wall.w32, wall.w33, wall.w34, wall.w35, wall.w36, wall.w37,
                    wall.w38, wall.w39, wall.w40, wall.w41, wall.w42, wall.w43, wall.w44,
                    wall.w45, wall.w46, wall.w47, wall.w48, wall.w49, wall.w50, wall.w51, wall.w52,
                    wall.w53, wall.w54, wall.w55, wall.w56, wall.w57, wall.w58, wall.w59, wall.w60,
                    wall.w61, wall.w62, wall.w63, wall.w64, wall.w65, wall.w66, wall.w67, wall.w68, wall.w69, wall.FINAL2);

            if (finalWallTexture2 != null) {
                map.getChildren().remove(finalWallTexture2);
            }
            finalWallTexture2 = new Rectangle(548, 214, 600-548, 8);
            finalWallTexture2.setFill(Color.BROWN);
            map.getChildren().add(finalWallTexture2);

            spawnEnemy(762,162);

            exitZone = new Rectangle(530, 22, 608-530, 37-22+20);
            exitZone.setFill(Color.GREEN);
            map.getChildren().add(exitZone);

            startX = 644;
            startY = 955;

            map.setLayoutX(centerx - (startX * zoom));
            map.setLayoutY(centery - (startY * zoom));
        } else if(level==3){
            bgr.setImage(new Image(getClass().getResource("/l3map.png").toExternalForm()));

            exitZone = new Rectangle(530, 22, 608-530, 37-22+20);
            exitZone.setFill(Color.GREEN);
            map.getChildren().add(exitZone);

            startX = 644;
            startY = 955;

            map.setLayoutX(centerx - (startX * zoom));
            map.setLayoutY(centery - (startY * zoom));
        }

        window.setScene(gameScene);
        timer.start();
    }

    public static void main(String[] args) {
        launch();
    }
}
