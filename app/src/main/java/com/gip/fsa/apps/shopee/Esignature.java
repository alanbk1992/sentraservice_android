package com.gip.fsa.apps.shopee;

import androidx.appcompat.app.AppCompatActivity;

import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.gip.fsa.R;

import java.io.File;
import java.io.FileOutputStream;

public class Esignature extends AppCompatActivity {

    GestureOverlayView gestureView;
    String path,ticket_no;
    File file;
    Bitmap bitmap;
    public boolean gestureTouch=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esignature);

        ticket_no = getIntent().getStringExtra("ticket_no");

        Button donebutton = (Button) findViewById(R.id.DoneButton);
        donebutton.setText("Done");
        Button clearButton = (Button) findViewById(R.id.ClearButton);
        clearButton.setText("Clear");

        path= Environment.getExternalStorageDirectory()+"/signature.png";
        file = new File(path);
        file.delete();
        gestureView = (GestureOverlayView) findViewById(R.id.signaturePad);
        gestureView.setDrawingCacheEnabled(true);

        gestureView.setAlwaysDrawnWithCacheEnabled(true);
        gestureView.setHapticFeedbackEnabled(false);
        gestureView.cancelLongPress();
        gestureView.cancelClearAnimation();
        gestureView.addOnGestureListener(new GestureOverlayView.OnGestureListener() {

            @Override
            public void onGesture(GestureOverlayView arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onGestureCancelled(GestureOverlayView arg0,
                                           MotionEvent arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onGestureEnded(GestureOverlayView arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onGestureStarted(GestureOverlayView arg0,
                                         MotionEvent arg1) {
                // TODO Auto-generated method stub
                if (arg1.getAction()==MotionEvent.ACTION_MOVE){
                    gestureTouch=false;
                }
                else
                {
                    gestureTouch=true;
                }
            }});

        donebutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {
                    bitmap = Bitmap.createBitmap(gestureView.getDrawingCache());
                    file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    fos = new FileOutputStream(file);
                    // compress to specified format (PNG), quality - which is
                    // ignored for PNG, and out stream
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    save_image(bitmap,"TTD");
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(gestureTouch==false)
                {
                    setResult(0);
                    finish();
                }
                else
                {
                    setResult(1);
                    finish();
                }
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                gestureView.invalidate();
                gestureView.clear(true);
                gestureView.clearAnimation();
                gestureView.cancelClearAnimation();
            }
        });

    }

    private void save_image(Bitmap finalBitmap, String image_name) {
        File dir = new File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "FMS");
        dir.mkdirs();
        String fname = ticket_no + "_" + image_name+ ".jpg";
        File file = new File(dir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
