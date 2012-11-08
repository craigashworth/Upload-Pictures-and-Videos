package sate2012.avatar.android;

import java.io.File;
import android.os.Environment;

public class MediaFileManager {
	private File sd;
	private File camFolder;
	private File mediaFolder;
	File image;
	File video;
	File recording;

	public MediaFileManager() {
		sd = Environment.getExternalStorageDirectory();
		camFolder = new File(sd, Globals.STORAGE_DIRECTORY);
		if (sd.canWrite()) {
			if (!camFolder.exists())
				camFolder.mkdir();
			mediaFolder = new File(sd, Globals.STORAGE_DIRECTORY + Globals.MEDIA_DIRECTORY);
			if (!mediaFolder.exists())
				mediaFolder.mkdir();
		}
	}

	public File createImageFile() {
		image = new File(mediaFolder, "image_" + System.currentTimeMillis() + ".jpg");
		this.setImage(image);
		return image;
	}

	public File createVideoFile() {
		video = new File(mediaFolder, "video_" + System.currentTimeMillis() + ".mp4");
		this.setVideo(video);
		return video;
	}

	public File createRecordingFile() {
		recording = new File(mediaFolder, "recording_" + System.currentTimeMillis() + ".mp4");
		this.setRecording(recording);
		return recording;
	}

	public File getImage() {
		return this.image;
	}

	public void setImage(File image) {
		this.image = image;
	}

	/**
	 * @return the video
	 */
	public File getVideo() {
		return this.video;
	}

	/**
	 * @param video
	 *            the video to set
	 */
	public void setVideo(File video) {
		this.video = video;
	}

	/**
	 * @return the recording
	 */
	public File getRecording() {
		return this.recording;
	}

	/**
	 * @param recording
	 *            the recording to set
	 */
	public void setRecording(File recording) {
		this.recording = recording;
	}

	/**
	 * @return the camFolder
	 */
	public File getcamFolder() {
		return this.camFolder;
	}

	/**
	 * @param camFolder
	 *            the camFolder to set
	 */
	public void setcamFolder(File camFolder) {
		this.camFolder = camFolder;
	}

	/**
	 * @return the mediaFolder
	 */
	public File getMediaFolder() {
		return this.mediaFolder;
	}

	/**
	 * @param mediaFolder
	 *            the mediaFolder to set
	 */
	public void setMediaFolder(File mediaFolder) {
		this.mediaFolder = mediaFolder;
	}
}