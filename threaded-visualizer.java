import javax.sound.sampled.*;
import java.io.File;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.application.Platform;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BoxBlur;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import javafx.util.Duration;

import static java.lang.Math.random;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class GUI extends Application {

  public static final int WIDTH = 800;
  public static final int HEIGHT = 600;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {

    Group root = new Group();
    Scene scene = new Scene(root, GUI.WIDTH, GUI.HEIGHT, Color.BLACK);
    primaryStage.setScene(scene);

    Pane pane_circles = new Pane();
    pane_circles.setPrefWidth(GUI.WIDTH);
    pane_circles.setPrefHeight(GUI.HEIGHT);
    root.getChildren().add(pane_circles);

    Pane pane_buttons = new Pane();
    pane_buttons.setStyle("-fx-background-color: black;");
    pane_buttons.setPrefWidth(GUI.WIDTH);
    pane_buttons.setPrefHeight(30);
    HBox hbox = new HBox(20);
    Button animationButton = new Button("Play Animation");

    animationButton.setOnAction(
        new EventHandler<ActionEvent>() {
          public void handle(ActionEvent event) {
            animationButton.setDisable(true);
            AnimationPlayer player = new AnimationPlayer(pane_circles);
            new Thread(new Runnable() {
              @Override
              public void run() {
                player.play();
              }
            }).start();
          }
        });

    Button musicButton = new Button("Play Music");
    musicButton.setOnAction(
        new EventHandler<ActionEvent>() {
          public void handle(ActionEvent event) {
            File soundFile = new FileChooser().showOpenDialog(primaryStage);
            if (soundFile != null) {
              new Thread(new Runnable() {
                @Override
                public void run() {
                  MusicPlayer player = new MusicPlayer(soundFile);
                  player.play();
                }
              }).start();
            }
          }
        });


    Button quitButton = new Button("Quit");
    quitButton.setOnAction(
        new EventHandler<ActionEvent>() {
          public void handle(ActionEvent event) {
            System.exit(0);
          }
        });

    hbox.getChildren().add(animationButton);
    hbox.getChildren().add(musicButton);
    hbox.getChildren().add(quitButton);
    pane_buttons.getChildren().add(hbox);
    root.getChildren().add(pane_buttons);

    primaryStage.show();
  }


  public class MusicPlayer {

    private File soundFile;
    private MediaPlayer mediaPlayer;
    private Media song;

    public MusicPlayer(File soundFile) {
      this.soundFile = soundFile;
    }

    public void play() {
      try {
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
        AudioFormat audioFormat = audioStream.getFormat();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        SourceDataLine sourceLine = (SourceDataLine) AudioSystem.getLine(info);
        sourceLine.open(audioFormat);
        sourceLine.start();
        int nBytesRead = 0;
        byte[] buffer = new byte[20000];
        while (nBytesRead != -1) {
          nBytesRead = audioStream.read(buffer, 0, buffer.length);
          if (nBytesRead >= 0) {
            sourceLine.write(buffer, 0, nBytesRead);
          }
        }
        sourceLine.drain();
        sourceLine.close();
      } catch (Exception e) {
        System.out.println("This wasn't a .wav file!  Checking alternatives...");
        try {
          /* mp3 playability */
          song = new Media(soundFile.toURI().toString());
          mediaPlayer = new MediaPlayer(song);
          mediaPlayer.setAutoPlay(true);
          mediaPlayer.play();
        } catch (Exception e2) {
          System.out.println("Failed attempt...");
        }
      }
    }
  }

  public class AnimationPlayer {

    private static final int NUM_CIRCLES = 30;
    private static final double RADIUS = 100;
    private static final double PROBABILITY_DIRECTION_CHANGE = 0.998;
    private static final int STROKE_WIDTH = 20;

    private Pane pane;
    private Circle[] circles;
    private Color[] colors;
    private double dirX[];
    private double dirY[];
    private double centerX[];
    private double centerY[];

    public AnimationPlayer(Pane pane) {
      this.pane = pane;
      this.circles = new Circle[AnimationPlayer.NUM_CIRCLES];
      this.colors = new Color[AnimationPlayer.NUM_CIRCLES];
      this.dirX = new double[this.circles.length];
      this.dirY = new double[this.circles.length];
      this.centerX = new double[this.circles.length];
      this.centerY = new double[this.circles.length];
      for (int i = 0; i < this.circles.length; i++) {
        this.dirX[i] = (random() > 0.5 ? 1.0 : -1.0);
        this.dirY[i] = (random() > 0.5 ? 1.0 : -1.0);
        this.centerX[i] = GUI.WIDTH / 2.0;
        this.centerY[i] = GUI.HEIGHT / 2.0;
        this.colors[i] = Color.color(0.5, 0.5, 0.5);
      }

      for (int i = 0; i < circles.length; i++) {
        this.circles[i] = new Circle(this.centerX[i], this.centerY[i], RADIUS, Color.web("black", 0.05));
        this.circles[i].setStrokeType(StrokeType.OUTSIDE);
        this.circles[i].setStroke(this.colors[i]);
        this.circles[i].setStrokeWidth(STROKE_WIDTH);
        this.circles[i].setEffect(new BoxBlur(10, 10, 5));
        this.pane.getChildren().add(this.circles[i]);
      }
    }

    public void play() {

      while (true) {
        for (int i = 0; i < this.circles.length; i++) {
          Circle c = this.circles[i];
          dirX[i] = (random() > PROBABILITY_DIRECTION_CHANGE ? -dirX[i] : dirX[i]);
          dirY[i] = (random() > PROBABILITY_DIRECTION_CHANGE ? -dirY[i] : dirY[i]);
          centerX[i] += dirX[i] * random();
          centerY[i] += dirY[i] * random();
          if (centerX[i] > GUI.WIDTH) {
            dirX[i] = -dirX[i];
          }
          if (centerY[i] > GUI.HEIGHT) {
            dirY[i] = -dirY[i];
          }
          double newRed = colors[i].getRed() + dirX[i] * random() / centerX[i];
          if (newRed > 1.0) {
            dirX[i] = -dirX[i];
            newRed = 1.0;
          }
          if (newRed < 0.0) {
            dirX[i] = -dirX[i];
            newRed = 0.0;
          }

          double newBlue = colors[i].getBlue() + dirY[i] * random() / centerY[i];
          if (newBlue > 1.0) {
            dirY[i] = -dirY[i];
            newBlue = 1.0;
          }
          if (newBlue < 0.0) {
            dirY[i] = -dirY[i];
            newBlue = 0.0;
          }

          colors[i] = Color.color(newRed, colors[i].getGreen(), newBlue);
          double newX = centerX[i];
          double newY = centerY[i];
          Color stroke = colors[i];
          Platform.runLater(new Runnable() {
            public void run() {
              c.setStroke(stroke);
              c.setCenterX(newX);
              c.setCenterY(newY);
            }
          });
        }
        try {
          Thread.sleep(200);
        } catch (InterruptedException e) {
          System.out.println("Interrupt...");
        }
      }
    }
  }

}
