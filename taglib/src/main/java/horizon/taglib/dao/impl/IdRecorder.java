package horizon.taglib.dao.impl;

import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件存取设计下的id记录者
 * <br>
 * created on 2018/03/20
 *
 * @author 巽
 **/
@Component
@SuppressWarnings({"unchecked", "ignored"})
public class IdRecorder {
	private static String fileSeparator = System.getProperty("file.separator");
	private static String pathname = "." + fileSeparator + "taglib" + fileSeparator + "database" + fileSeparator + "IdRecords.ser";
	private Map<String, Long> records;

	public IdRecorder() {
		File file = new File(pathname);
		try {
			if (!file.getParentFile().exists()) {
				boolean isDirsMade = file.getParentFile().mkdirs();
				if (isDirsMade) {
					System.out.println("ERROR：创建文件夹失败: " + file.getParentFile());
				}
			}
			if (!file.exists()) {
				boolean isNewFileCreated = file.createNewFile();
				if (isNewFileCreated) {
					System.out.println("ERROR：创建文件失败: " + file.getParentFile());
				}
				records = new HashMap<>();
				this.save();
			} else {
				try (FileInputStream fileInputStream = new FileInputStream(file);
				     ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
					records = (Map<String, Long>) objectInputStream.readObject();
				}
			}
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}

	private void save() {
		File file = new File(pathname);
		try (FileOutputStream fileOutputStream = new FileOutputStream(file, false);
		     ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
			objectOutputStream.writeObject(records);
			objectOutputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Long getNewIdAndIncrease(String className) {
		Long ret = records.get(className);
		if (ret == null) {
			ret = 1L;
		}
		records.put(className, ret + 1L);
		this.save();
		return ret;
	}

	public Long getNewId(String className) {
		Long ret = records.get(className);
		if (ret == null) {
			ret = 1L;
		}
		return ret;
	}
}