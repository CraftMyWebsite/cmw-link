package fr.AxelVatan.CMWLink.Common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Persist {

	private ConfigFile configFile;
	private Gson gson;
	private Map<String, Lock> locks;

	public Persist(ConfigFile configFile) {
		configFile.getFilePath().mkdirs();
		this.configFile = configFile;
		this.gson = buildGson().create();
		this.locks = new HashMap<String, Lock>();
	}

	public String getName(Object o) {
		return getName(o.getClass());
	}

	private GsonBuilder buildGson() {
		return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping()
				.enableComplexMapKeySerialization()
				.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE);
	}

	public File getFile(Object obj) {
		return getFile(getName(obj));
	}

	public void save(Object instance) {
		save(instance, getFile(instance));
	}

	public void save(Object instance, File file) {
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				configFile.getLog().severe("Failed to save file: " + e.getMessage());
				e.printStackTrace();
			}
		}
		writeCatch(file, gson.toJson(instance));
	}

	public <T> T load(Class<T> clazz) {
		return load(clazz, getFile(clazz));
	}

	public <T> T load(Class<T> clazz, File file) {
		String content = readCatch(file);
		if (content == null) {
			return null;
		}
		try {
			return gson.fromJson(content, clazz);
		} catch (Exception ex) {
			configFile.getLog().severe("Failed to parse " + file.toString() + ": " + ex.getMessage());
		}
		return null;
	}

	public void writeCatch(final File file, final String content) {
		String name = file.getName();
		final Lock lock;
		if (locks.containsKey(name)) {
			lock = locks.get(name);
		} else {
			ReadWriteLock rwl = new ReentrantReadWriteLock();
			lock = rwl.writeLock();
			locks.put(name, lock);
		}
		lock.lock();
		try {
			file.createNewFile();
			Files.write(content.getBytes(), file);
		} catch (IOException e) {
			configFile.getLog().severe("Failed to write data to file: " + e.getMessage());
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public String readCatch(File file) {
		try {
			return read(file);
		} catch (IOException e) {
			return null;
		}
	}

	public String read(File file) throws IOException {
		return utf8(readBytes(file));
	}

	public byte[] readBytes(File file) throws IOException {
		int length = (int) file.length();
		byte[] output = new byte[length];
		InputStream in = new FileInputStream(file);
		int offset = 0;
		while (offset < length) {
			offset += in.read(output, offset, (length - offset));
		}
		in.close();
		return output;
	}

	public String utf8(byte[] bytes) {
		return new String(bytes, StandardCharsets.UTF_8);
	}
}