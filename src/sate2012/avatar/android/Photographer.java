package sate2012.avatar.android;

import gupta.ashutosh.avatar.R;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class Photographer extends Activity implements View.OnClickListener {
	private File pic;
	private ImageView iv;
	private ImageButton ib;
	private ImageButton uploadB;
	public final static int cameraData = 0;
	private Bitmap bmp;
	private String OUTPUT_FILE = "_P.png";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo);
		iv = (ImageView) findViewById(R.id.ivReturnedPicture);
		ib = (ImageButton) findViewById(R.id.ibTakePic);
		ib.setOnClickListener(this);
		uploadB = (ImageButton) findViewById(R.id.upload_button);
		uploadB.setOnClickListener(this);
	}

	public void onBackPressed(){
		setResult(Activity.RESULT_CANCELED, null);
		finish();
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case (R.id.ibTakePic):
			Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(i, cameraData);
			break;
		case (R.id.upload_button):
			Intent data = new Intent();
			setResult(Activity.RESULT_OK, data);
			UploadMedia.setImage_filepath(pic.getAbsolutePath());
			finish();
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == cameraData) {
			Bundle extras = data.getExtras();
			bmp = (Bitmap) extras.get("data");
			iv.setImageBitmap(bmp);
			pic = new File(Environment.getExternalStorageDirectory(), Globals.STORAGE_DIRECTORY + Globals.MEDIA_DIRECTORY + System.currentTimeMillis() + OUTPUT_FILE);
			FileOutputStream stream = null;
			try {
				stream = new FileOutputStream(pic);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
		}
	}

	public String getPath() {
		return pic.getAbsolutePath();
	}
}