package com.example.window2.facedetection;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;

import com.example.window2.R;
import com.google.android.gms.vision.CameraSource;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.example.window2.GraphicOverlay;
import com.example.window2.GraphicOverlay.Graphic;


/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
public class FaceGraphic extends Graphic {
  private static final float FACE_POSITION_RADIUS = 10.0f;
  private static final float ID_TEXT_SIZE = 40.0f;
  private static final float ID_Y_OFFSET = 50.0f;
  private static final float ID_X_OFFSET = -50.0f;
  private static final float BOX_STROKE_WIDTH = 5.0f;

  // music player
  MediaPlayer mp=MediaPlayer.create(getApplicationContext(),R.raw.alert1);


  private static final int[] COLOR_CHOICES = {
    Color.BLUE //, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.RED, Color.WHITE, Color.YELLOW
  };
  private static int currentColorIndex = 0;

  private int facing;

  private final Paint facePositionPaint;
  private final Paint eyecolorpaint;
  private final Paint idPaint;
  private final Paint boxPaint;




  private volatile FirebaseVisionFace firebaseVisionFace;

  public FaceGraphic(GraphicOverlay overlay) {
    super(overlay);

    currentColorIndex = (currentColorIndex + 1) % COLOR_CHOICES.length;
    final int selectedColor = COLOR_CHOICES[currentColorIndex];

    facePositionPaint = new Paint();
    facePositionPaint.setColor(selectedColor);
    eyecolorpaint= new Paint();
    eyecolorpaint.setColor(Color.GREEN);

    idPaint = new Paint();
    idPaint.setColor(selectedColor);
    idPaint.setTextSize(ID_TEXT_SIZE);

    boxPaint = new Paint();
    boxPaint.setColor(selectedColor);
    boxPaint.setStyle(Paint.Style.STROKE);
    boxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
  }

  /**
   * Updates the face instance from the detection of the most recent frame. Invalidates the relevant
   * portions of the overlay to trigger a redraw.
   */
  public void updateFace(FirebaseVisionFace face, int facing) {
    firebaseVisionFace = face;
    this.facing = facing;
    postInvalidate();
  }

  /** Draws the face annotations for position on the supplied canvas. */
  @Override
  public void draw(Canvas canvas) {
    FirebaseVisionFace face = firebaseVisionFace;
    if (face == null) {
      return;
    }

    // Draws a circle at the position of the detected face, with the face's track id below.
    float x = translateX(face.getBoundingBox().centerX());
    float y = translateY(face.getBoundingBox().centerY());
    //canvas.drawCircle(x, y, FACE_POSITION_RADIUS, facePositionPaint);
    //canvas.drawText("id: " + face.getTrackingId(), x + ID_X_OFFSET, y + ID_Y_OFFSET, idPaint);


    if (facing == CameraSource.CAMERA_FACING_FRONT) {
      canvas.drawText(
          "Right eye: " + String.format("%.2f", face.getRightEyeOpenProbability()),
          x - ID_X_OFFSET,
          y,
          idPaint);

      //canvas.drawCircle(x-ID_X_OFFSET*0.6F,y+5,10,eyecolorpaint);
      canvas.drawText(
          "Left eye: " + String.format("%.2f", face.getLeftEyeOpenProbability()),
          x + ID_X_OFFSET * 6,
          y,
          idPaint);

      //-------------
      //canvas.drawCircle(x+ID_X_OFFSET * 6,y+5,10,eyecolorpaint);

    } else {
      /*canvas.drawText(
          "left eye: " + String.format("%.2f", face.getLeftEyeOpenProbability()),
          x - ID_X_OFFSET,
          y,
          idPaint);
      canvas.drawText(
          "right eye: " + String.format("%.2f", face.getRightEyeOpenProbability()),
          x + ID_X_OFFSET * 6,
          y,
          idPaint);
       */
      canvas.drawCircle(x-ID_X_OFFSET,y+10,10,eyecolorpaint);
      canvas.drawCircle(x+ID_X_OFFSET*6,y+10,10,eyecolorpaint);

    }

    if(face.getLeftEyeOpenProbability()<0.22 && face.getRightEyeOpenProbability()<0.22 && face.getLeftEyeOpenProbability()>0 && face.getRightEyeOpenProbability()>0)
    {
      // music player for alert
      mp.start();
        //LivePreviewActivity.alert_tone=true;
    }

    /*if(face.getRightEyeOpenProbability()>0.4 && face.getLeftEyeOpenProbability()>0.4 && LivePreviewActivity.alert_tone)
    {
      mp.pause();
        LivePreviewActivity.alert_tone=false;
    }
*/
    // Draws a bounding box around the face.
    float xOffset = scaleX(face.getBoundingBox().width() / 2.0f);
    float yOffset = scaleY(face.getBoundingBox().height() / 2.0f);
    float left = x - xOffset;
    float top = y - yOffset;
    float right = x + xOffset;
    float bottom = y + yOffset;
    canvas.drawRect(left, top, right, bottom, boxPaint);
  }
}
