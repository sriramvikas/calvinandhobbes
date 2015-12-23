package pro.srv.com.comicstrip;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import pro.srv.com.imageloaders.ScaleImageView;

public class MainActivity extends Activity {
    String url;
    ScaleImageView image;
    ImageView loadingImage;
    RelativeLayout loadLayout;
    TextView refreshButton, shareButton, shareIcon;
    public Dialog dialog;
    private Context mContext;
    ProgressDialog progress;
    File savedPath;
    File savedPathdir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        image = (ScaleImageView) findViewById(R.id.image_view);
        loadingImage = (ImageView) findViewById(R.id.load_image);
        refreshButton = (TextView) findViewById(R.id.refresh_button);
        shareButton = (TextView) findViewById(R.id.share_button);
        shareIcon = (TextView) findViewById(R.id.share_button_icon);
        loadLayout = (RelativeLayout) findViewById(R.id.relative_load);
        url = "http://x3n0n.com/calvin/script.php";
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareButton.setVisibility(View.GONE);
                RotateAnimation rAnim = new RotateAnimation(0, 1359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rAnim.setDuration(2500);
                shareButton.startAnimation(rAnim);

                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                share.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(savedPath));
                startActivity(Intent.createChooser(share, "Share Image"));

                shareButton.setVisibility(View.VISIBLE);
            }
        });
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = "http://x3n0n.com/calvin/script.php";
                getUrl(url);
            }
        });
        getUrl(url);
    }

    public void getUrl(String url) {
        url = url + "?" + System.currentTimeMillis();
        LoadImage load = new LoadImage();
        load.execute(url);
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        Bitmap bitmap;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            refreshButton.setVisibility(View.GONE);
            shareButton.setVisibility(View.GONE);
            loadLayout.setVisibility(View.VISIBLE);
            loadingImage.setBackgroundResource(R.drawable.anim);
            loadingImage.post(new Runnable() {
                @Override
                public void run() {
                    AnimationDrawable frameAnimation =
                            (AnimationDrawable) loadingImage.getBackground();
                    frameAnimation.start();
                }
            });
        }

        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
                FileOutputStream out = null;
                try {
                    savedPath = new File(Environment.getExternalStorageDirectory()
                            + "/comic/calvin.png");
                    savedPathdir = new File(Environment.getExternalStorageDirectory()
                            + "/comic");
                    if (!savedPathdir.exists())
                        savedPathdir.mkdir();
                    out = new FileOutputStream(savedPath);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap img) {
            refreshButton.setVisibility(View.VISIBLE);
            shareButton.setVisibility(View.VISIBLE);
            loadLayout.setVisibility(View.GONE);
            if (img != null) {
                image.setImageBitmap(img);
            } else
                Toast.makeText(MainActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}