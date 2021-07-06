package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import configuration.Configuration;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.AlertDisplaying;
import model.forms.ApplicationForm;
import server.ZipUtil;

public class FileController {

	@FXML
	private Label label;
	@FXML
	private Button saveButton;
	
	private Stage stage;
	private String uuid;
	private ArrayList<byte[]> fileInfo;
	private Logger logger = ApplicationForm.logger;
	
	@FXML
	public void initialize(Stage stage, String text, ArrayList<byte[]> fileInfo, String uuid) {
		label.setText(text);
		this.stage = stage;
		this.uuid = uuid;
		this.fileInfo = fileInfo;
	}
	
	//fajl u bajtima dobijen od pacijenta se sačuva u folderu dokumenti_pacijenata kada medicinar klikne na dugme sačuvaj na formi
	public void save() {
		try {
			String root = Configuration.readParameters().getProperty("DOCUMENTS_PATH");
			String name = new String(fileInfo.get(1));
			String path = root + File.separator + uuid + File.separator + name;
				
			File folder = new File(root);
			if(!folder.exists()) {
				folder.mkdir();
			}
			folder = new File(root + File.separator + uuid);
			if(!folder.exists()) {
				folder.mkdir();
			}
				
			byte[] data = fileInfo.get(0);
			if(fileInfo.size() == 3 && "zip".equals(new String(fileInfo.get(2)))) {
				ZipUtil.unzip(path, data);
			} else {
				FileOutputStream out = new FileOutputStream(new File(path));
				out.write(data, 0, data.length);
				out.close();
			}
			AlertDisplaying.displayAlert("Fajl je sačuvan.");
		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.fillInStackTrace().toString());
		} 
		
		stage.close();
	}
}
