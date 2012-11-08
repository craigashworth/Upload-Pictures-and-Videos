package sate2012.avatar.android;

import gupta.ashutosh.avatar.R;
import java.io.File;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaRecorder;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

/**
 * Allows the user to record a video file, play it back, and then upload it to
 * the Virtual Command Center
 */
public class VideoRecorder extends Activity implements SurfaceHolder.Callback, OnClickListener {
	private MediaRecorder recorder;
	private Camera mCameraDevice;
	private VideoView videoView;
	private ImageButton startBtn;
	private ImageButton playRecordingBtn;
	private ImageButton returnToSubmission;
	private String OUTPUT_FILE = "video_" + System.currentTimeMillis() + ".mp4";
	public static final String VIDEO = "VIDEO";
	private static File videoRecording;
	private Boolean playing;
	private Boolean recording;
	private TextView mVideoClockUI;
	private Handler mHandler;
	private int mVideoClockTime;
	private Runnable mClockTask;
	private SurfaceHolder mHolder;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.video);
		playing = false;
		recording = false;
		mVideoClockTime = 0;
		mHandler = new Handler();
		mClockTask = new Runnable() {
			public void run() {
				mVideoClockTime++;
				int minutes = mVideoClockTime / 60;
				mVideoClockTime = mVideoClockTime % 60;
				mVideoClockUI.setText(String.format("%02d:%02d", minutes, mVideoClockTime));
				mHandler.postDelayed(this, 1000); // Every second.
			}
		};
		mVideoClockUI = (TextView) findViewById(R.id.video_clock_ui);
		videoView = (VideoView) this.findViewById(R.id.videoView);
		mHolder = videoView.getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		startBtn = (ImageButton) findViewById(R.id.bgnBtn);
		startBtn.setOnClickListener(this);
		playRecordingBtn = (ImageButton) findViewById(R.id.playRecordingBtn);
		playRecordingBtn.setEnabled(false);
		playRecordingBtn.setOnClickListener(this);
		returnToSubmission = (ImageButton) findViewById(R.id.returnToForm);
		returnToSubmission.setOnClickListener(this);
	}

	public void onBackPressed(){
		setResult(Activity.RESULT_CANCELED, null);
		finish();
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case (R.id.bgnBtn):
			if (!recording && !playing) {
				try {
					beginRecording();
					recording = true;
					startBtn.setImageResource(R.drawable.stop_recording_video);
					mVideoClockUI.setVisibility(View.VISIBLE);
					mVideoClockUI.setText("00:00");
					mHandler.postDelayed(mClockTask, 1000);
				} catch (Exception e) {
					Log.e("ERROR", "Exception caught recording video.", e);
				}
			} else if (recording) {
				try {
					stopRecording();
					playing = false;
					recording = false;
					playRecordingBtn.setEnabled(true);
					playRecordingBtn.setImageResource(R.drawable.start_play_button);
					startBtn.setEnabled(false);
					startBtn.setImageResource(R.drawable.stop_recording_video);
					mHandler.removeCallbacks(mClockTask);
					mVideoClockUI.setVisibility(View.INVISIBLE);
				} catch (Exception e) {
					Log.e("ERROR", "Exception caught stopping recording.", e);
				}
			}
			break;
		case (R.id.playRecordingBtn):
			if (!playing && !recording) {
				try {
					playRecording();
					playing = true;
					playRecordingBtn.setEnabled(false);
					playRecordingBtn.setImageResource(R.drawable.start_play_button);
				} catch (Exception e) {
					Log.e("ERROR", "Exception caught playing video.", e);
				}
			} else if (playing) {
				try {
					stopPlayingRecording();
					playing = false;
					recording = false;
				} catch (Exception e) {
					Log.e("ERROR", "Exception caught stopping play.", e);
				}
			}
			break;
		case (R.id.returnToForm):
			Intent data = new Intent();
			data.putExtra(VIDEO, getVideoRecording());
			if (getVideoRecording() != null)
				setResult(Activity.RESULT_OK, data);
			else
				setResult(Activity.RESULT_CANCELED);
			finish();
			break;
		}
	}

	public static String getPath() {
		return videoRecording.getAbsolutePath();
	}

	public void surfaceCreated(SurfaceHolder holder) {
		startBtn.setEnabled(true);
		mCameraDevice = Camera.open();
		try {
			mCameraDevice.setPreviewDisplay(holder);
		} catch (Exception e) {
			Log.e("INFO", "Error in setPreviewDisplay:  ", e);
		}
		try {
			mCameraDevice.startPreview();
		} catch (Exception e) {
			Log.e("INFO", "Error in startPreview:  ", e);
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		try {
			Log.i("INFO", "Width x Height = " + width + "x" + height);
		} catch (Exception e) {
			Log.e("INFO", "Error in surfaceChanged:  ", e);
		}
	}

	private void playRecording() {
		MediaController mc = new MediaController(this);
		videoView.setMediaController(mc);
		videoView.setVideoPath(videoRecording.getAbsolutePath());
		videoView.start();
	}

	private void stopPlayingRecording() {
		videoView.stopPlayback();
	}

	private void stopRecording() throws Exception {
		if (recorder != null) {
			recorder.stop();
			mCameraDevice.release();
			setVideoRecording(videoRecording);
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		if (recorder != null)
			recorder.release();
		mHandler.removeCallbacks(mClockTask);
	}

	private void beginRecording() throws Exception {
		mCameraDevice.stopPreview();
		mCameraDevice.unlock();
		if (recorder != null) {
			recorder.stop();
			recorder.release();
		}
		videoRecording = new File(Environment.getExternalStorageDirectory(), Globals.STORAGE_DIRECTORY + Globals.MEDIA_DIRECTORY + OUTPUT_FILE);
		if (videoRecording.exists())
			videoRecording.delete();
		try {
			recorder = new MediaRecorder();
			recorder.reset();
			System.out.println("Reset");
			recorder.setCamera(mCameraDevice);
			recorder.setPreviewDisplay(mHolder.getSurface());
			recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			recorder.setMaxDuration(20000);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			recorder.setAudioEncoder(3); // Eclipse does not recognize
											// MediaRecorder.AudioEncoder.AAC,
											// but using its value (3) does
											// work.
			recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
			recorder.setOutputFile(videoRecording.getAbsolutePath());
			recorder.setPreviewDisplay(mHolder.getSurface());
			recorder.prepare();
			recorder.start();
		} catch (Exception e) {
			Log.e("ERROR", "Exception caught creating media recorder." + e.getStackTrace(), e);
		}
	}

	/**
	 * @return the videoRecording
	 */
	public File getVideoRecording() {
		return videoRecording;
	}

	/**
	 * @param videoRecording
	 *            the videoRecording to set
	 */
	public void setVideoRecording(File v) {
		videoRecording = v;
	}

	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}
}
