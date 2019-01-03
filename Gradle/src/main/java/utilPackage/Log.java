package utilPackage;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Log {
	File logFile;
	BufferedWriter bw;
	FileWriter fw;
	public Log() {
		logFile = new File("/media/sda1/LogFiles/unknamedLog.txt");
		setup();
	}
	public Log(String name) {
		logFile = new File("/media/sda1/LogFiles/"+name+".txt");
		setup();
	}
	private void setup(){		
		if (!logFile.exists()) {
//			System.out.println("Creating");
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else
			System.out.println("Creating");
		try {
			fw = new FileWriter(logFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		bw = new BufferedWriter(fw);
	}

	public void log(String msg){
		try {
			bw.write(msg);
			bw.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close(){
		try {
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}