package sate2012.avatar.android;

import gupta.ashutosh.avatar.R;

import java.io.File;
import java.io.IOException;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

public class VoiceNotes extends Activity implements OnClickListener {
	private MediaRecorder recorder;
	private MediaPlayer player;
	private ImageButton startRecording;
	private ImageButton playRecording;
	private ImageButton returnToSubmission;
	private String OUTPUT_FILE = "recording_" + System.currentTimeMillis() + ".mp4";
	public static final String VOICE = "VOICE";
	private static File voiceRecording;
	private boolean media;
	private boolean playing;
	private boolean recording;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.voice);
		media = false;
		playing = false;
		recording = false;
		recorder = new MediaRecorder();
		startRecording = (ImageButton) findViewById(R.id.bgnBtn);
		playRecording = (ImageButton) findViewById(R.id.playRecordingBtn);
		returnToSubmission = (ImageButton) findViewById(R.id.returnToForm);
		startRecording.setOnClickListener(this);
		playRecording.setOnClickListener(this);
		returnToSubmission.setOnClickListener(this);
	}

	public void onBackPressed(){
		setResult(Activity.RESULT_CANCELED, null);
		finish();
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case (R.id.bgnBtn):
			startRecording();
			break;
		case (R.id.playRecordingBtn):
			if (media) {
				player = new MediaPlayer();
				playRecording.setImageResource(R.drawable.start_play_button);
				try {
					player.setDataSource(voiceRecording.getAbsolutePath());
					player.prepare();
					player.start();
				} catch (IOException e) {
					Log.e("ERROR", "error playing recording", e);
				}
				Toast.makeText(VoiceNotes.this, "Playing back.  Press Upload when done.", Toast.LENGTH_LONG).show();
			} else
				Toast.makeText(VoiceNotes.this, "You haven't recorded any audio to play yet.", Toast.LENGTH_LONG).show();
			break;
		case (R.id.returnToForm):
			Intent data = new Intent();
			data.putExtra(VOICE, getVoiceRecording());
			if (getVoiceRecording() != null)
				setResult(Activity.RESULT_OK, data);
			else
				setResult(Activity.RESULT_CANCELED);
			finish();
			break;
		}
	}

	public static String getPath() {
		return voiceRecording.getAbsolutePath();
	}

	protected void addToDB() {
		ContentValues values = new ContentValues(3);
		long current = System.currentTimeMillis();
		values.put(MediaColumns.TITLE, "observation_audio");
		values.put(MediaColumns.DATE_ADDED, (int) (current / 1000));
		values.put(MediaColumns.MIME_TYPE, "audio/mp4");
		values.put(MediaColumns.DATA, OUTPUT_FILE);
		ContentResolver contentResolver = getContentResolver();
		Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		Uri newUri = contentResolver.insert(base, values);
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
	}

	protected void startRecording() {
		voiceRecording = new File(Environment.getExternalStorageDirectory(), Globals.STORAGE_DIRECTORY + Globals.MEDIA_DIRECTORY + OUTPUT_FILE);
		if (!recording && !playing) {
			recording = true;
			startRecording.setImageResource(R.drawable.stop_recording_video);
			recorder = new MediaRecorder();
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			recorder.setAudioEncoder(3); // Eclipse does not recognize
											// MediaRecorder.AudioEncoder.AAC,
											// but using its value (3) does
											// work.
			recorder.setOutputFile(voiceRecording.getAbsolutePath());
			try {
				recorder.prepare();
			} catch (IllegalStateException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			recorder.start();
		} else if (recording) {
			stopRecording();
			playing = false;
			recording = false;
			addToDB();
		}
	}

	protected void stopRecording() {
		recorder.stop();
		recorder.release();
		setVoiceRecording(voiceRecording);
		playRecording.setEnabled(true);
		playRecording.setImageResource(R.drawable.start_play_button);
		startRecording.setEnabled(false);
		startRecording.setImageResource(R.drawable.disabled_record_button);
		media = true;
	}

	public File getVoiceRecording() {
		return voiceRecording;
	}

	public void setVoiceRecording(File v) {
		voiceRecording = v;
	}

	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}
}
