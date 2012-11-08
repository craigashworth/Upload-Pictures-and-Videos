package sate2012.avatar.android;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.net.InetAddress;
import org.apache.commons.net.ftp.FTPClient;

public class UploadFTP extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public static String FTPUpload(String filepath, String extension, Context thisContext) {
		FTPClient ftpClient = new FTPClient();
		long time = (System.currentTimeMillis());
		String filename = "T" + time;
		try {
			ftpClient.connect(InetAddress.getByName("24.123.68.146"));
			ftpClient.login("opensim", "widdlyscuds");
			ftpClient.changeWorkingDirectory("../../var/www/avatar/Uploaded");
			if (ftpClient.getReplyString().contains("250")) {
				Handler progressHandler = new Handler();
				ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
				BufferedInputStream buffIn = null;
				buffIn = new BufferedInputStream(new FileInputStream(filepath));
				ftpClient.enterLocalPassiveMode();
				ProgressInputStream progressInput = new ProgressInputStream(buffIn, progressHandler);
				ftpClient.storeFile(filename + extension, progressInput);
				buffIn.close();
				ftpClient.logout();
				ftpClient.disconnect();
			}
		} catch (IOException e) {
		}
		return filename + extension;
	}
}