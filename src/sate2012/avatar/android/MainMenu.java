package sate2012.avatar.android;

import gupta.ashutosh.avatar.R;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * The main menu of the AVATAR Application This menu navigates users to
 * different parts of the program.
 */
public class MainMenu extends Activity implements OnClickListener {
	private Button uploadB; // Button that switches to a menu that lets the user
							// upload different types of media

	/*
	 * private Button mapB; // Button that switches to map view private Button
	 * naoB; // Button that switches to NAO Robot Control private Button arB; //
	 * Button that switches to Augmented Reality private Button settingB; //
	 * Button that switches to the Settings Menu
	 */
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.main);
		uploadB = (Button) findViewById(R.id.uploadB);
		uploadB.setOnClickListener(this);
		/*
		 * mapB = (Button) findViewById(R.id.mapB);
		 * mapB.setOnClickListener(this); naoB = (Button)
		 * findViewById(R.id.naoB); naoB.setOnClickListener(this); arB =
		 * (Button) findViewById(R.id.arB); arB.setOnClickListener(this);
		 * settingB = (Button) findViewById(R.id.settingB);
		 * settingB.setOnClickListener(this);
		 */
	}

	public void onBackPressed(){
		finish();
	}
	
	/**
	 * Called when a button is clicked by the user. Navigates to the appropriate
	 * Activity
	 * 
	 * @param View
	 *            v - the button clicked
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		// case (R.id.uploadB):
		default:
			Intent intent = new Intent(getApplicationContext(), MapsForgeMapViewer.class);
			startActivity(intent);
			finish();
			// break;
			/*
			 * case (R.id.mapB): Toast.makeText(getApplicationContext(),
			 * "These are not the maps you're looking for...",
			 * Toast.LENGTH_LONG).show(); // Create/call intent to map activity
			 * here // Load the MapsForgeMapViewer
			 * Toast.makeText(getApplicationContext(),
			 * "MapViewer is not yet available.", Toast.LENGTH_LONG) .show();
			 * break; case (R.id.naoB): Toast.makeText(getApplicationContext(),
			 * "Ashu couldn't get this to work.", Toast.LENGTH_LONG) .show(); //
			 * Create/call intent to nao activity here break; case (R.id.arB):
			 * Toast.makeText(getApplicationContext(),
			 * "Yeah, like WE are ever going to get this to work?",
			 * Toast.LENGTH_LONG).show(); // Create/call intent to augmented
			 * reality activity here break; case (R.id.settingB):
			 * Toast.makeText( getApplicationContext(),
			 * "There are no settings. You WILL like it the way we made it.",
			 * Toast.LENGTH_LONG).show(); // Create/call intent to settings
			 * activity here break;
			 */
		}
	}
}