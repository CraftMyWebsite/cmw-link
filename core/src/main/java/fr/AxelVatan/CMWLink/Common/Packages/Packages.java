package fr.AxelVatan.CMWLink.Common.Packages;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

import org.json.simpleForBukkit.JSONObject;
import org.json.simpleForBukkit.parser.JSONParser;
import org.json.simpleForBukkit.parser.ParseException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import com.google.common.base.Preconditions;

import fr.AxelVatan.CMWLink.Common.Utils.Utils;
import fr.AxelVatan.CMWLink.Common.WebServer.WebServer;
import lombok.Getter;

public class Packages {

	private Logger log;
	private WebServer webServer;
	private Utils utils;
	private Yaml yaml;
	private File defaultPath;
	private File packagesPath;
	private Map<String, CMWLPackageDescription> packagesToLoad;
	private Map<String, PackageClassLoader> loaders;
	private Map<String, Class<?>> classes;
	private @Getter HashMap<String, CMWLPackageDescription> packagesCertified;
	private @Getter List<CMWLPackage> packagesLoaded;


	//	⠀⠀⠀⠀⠀⠀⠀⢰⠒⠒⠒⠒⠒⠒⢲⡖⣶⣶⡆⠀⠀⠀⠀⠀⠀⠀
	//	⠀⠀⢀⡀⣯⠉⠉⠉⣖⣲⣶⡆⠀⠀⠈⠉⠉⠉⠉⠉⠉⢱⠀⠀⠀⠀
	//	⢀⣀⣸⠀⠀⠀⠀⠀⠈⠉⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⣿⣇⣀⡀
	//	⢸⣿⣀⣀⡀⠀⠿⠿⠀⠀⠀⣸⣙⣿⣿⠀⢸⣿⠀⠀⠀⠀⠀⠀⢰⡇
	//	⢸⡿⠾⠿⠟⠀⠀⠀⣤⡄⠀⠸⠿⠿⠟⠀⠸⠿⠀⠀⠀⣠⣤⠀⢸⡇	NOW GIVE ME A
	//	⢸⡃⠀⠀⠀⠀⠀⠀⣿⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠙⠛⠀⢸⡇			COOKIE ! POOKIE !
	//	⢸⣖⠀⠀⠀⠀⠀⠀⠙⠋⠀⢴⣶⣶⣶⠀⠀⠀⣶⣶⠀⠀⠀⠀⢸⡇
	//	⢸⣿⣶⠀⠀⠀⣶⣶⠀⠀⠀⠈⠉⠉⠉⠀⠀⠀⠉⠉⠀⠀⠀⣷⣾⡇
	//	⠈⠉⢹⣿⣿⣀⣀⣠⠀⠀⠀⠀⠀⠀⠸⣿⡇⠀⣀⣀⣀⣿⣿⡏⠉⠁
	//	⠀⠀⠀⠀⢿⠿⠿⢿⣀⣀⣀⣀⣠⣤⣤⣤⣤⣤⣿⠿⠿⡿⠀⠀⠀⠀
	//	⠀⠀⠀⠀⠀⠀⠀⠸⠿⠿⠿⠿⠿⣿⣿⣿⣿⠿⠇⠀⠀⠀⠀⠀⠀⠀



	public Packages(Logger log, File defaultPath, WebServer webServer, Utils utils) {
		this.log = log;
		this.webServer = webServer;
		this.utils = utils;
		this.defaultPath = defaultPath;
		log.info("Searching packages ...");
		this.packagesPath = new File(defaultPath + File.separator + "Packages");
		if(!this.packagesPath.exists()) {
			this.packagesPath.mkdirs();
		}
		File packagesConfigPath = new File(defaultPath + File.separator + "PackagesConfig");
		if(!packagesConfigPath.exists()) {
			packagesConfigPath.mkdirs();
		}
		Constructor yamlConstructor = new CustomClassLoaderConstructor(Packages.class.getClassLoader());
		PropertyUtils propertyUtils = yamlConstructor.getPropertyUtils();
		propertyUtils.setSkipMissingProperties(true);
		yamlConstructor.setPropertyUtils(propertyUtils);
		this.yaml = new Yaml(yamlConstructor);
		this.packagesToLoad = new HashMap<String, CMWLPackageDescription>();
		this.loaders = new LinkedHashMap<String, PackageClassLoader>();
		this.classes = new HashMap<String, Class<?>>();
		this.packagesLoaded = new ArrayList<CMWLPackage>();
		this.packagesCertified = new HashMap<String, CMWLPackageDescription>();
		detectPackages();
		certificatePackages();
		loadPackages();
	}

	public void disablePackages() {
		for(CMWLPackage miniPlugin : packagesLoaded) {
			miniPlugin.onDisable();
		}
	}

	private void detectPackages(){
		for (File file : this.packagesPath.listFiles() ){
			if (file.isFile() && file.getName().endsWith(".jar")){
				try (JarFile jar = new JarFile(file)){
					JarEntry pdf = jar.getJarEntry("package.yml");
					Preconditions.checkNotNull(pdf, "Package must have a Package.yml");
					try (InputStream in = jar.getInputStream(pdf)){
						CMWLPackageDescription desc = this.yaml.loadAs(in, CMWLPackageDescription.class);
						Preconditions.checkNotNull(desc.getName(), "Package from %s has no name", file);
						Preconditions.checkNotNull(desc.getRoute_prefix(), "Package from %s has no route prefix", file);
						Preconditions.checkNotNull(desc.getVersion(), "Package from %s has no version", file);
						Preconditions.checkNotNull(desc.getAuthor(), "Package from %s has no author", file);
						desc.setFile(file);
						this.packagesToLoad.put(desc.getName(), desc);
					}
				} catch (Exception ex){
					this.log.warning("Could not load package from file " + file);
					ex.printStackTrace();
				}
			}
		}
		this.log.info("Packages found: " + this.packagesToLoad.size());
	}

	private void certificatePackages() {
		this.log.info("Waiting for packages certification...");
		ExecutorService executor = Executors.newFixedThreadPool(5);
		for (CMWLPackageDescription packageDesc : this.packagesToLoad.values()) {
			Runnable worker = new Runnable() {
				@Override
				public void run() {
					String localMd5 = getMd5(packageDesc.getFile().getAbsoluteFile());
					if(checkMd5CMW(localMd5)) {
						log.info("Checked " + packageDesc.getName() + " is CERTIFIED by CMW.");
						packagesCertified.put(packageDesc.getName(), packageDesc);
					}else {
						if(!webServer.getConfig().getSettings().isLoadUncertifiedPackages()) {
							log.severe(packageDesc.getName() + " is not certified by CMW, it will not be loaded.");
						}else {
							log.warning(packageDesc.getName() + " is not certified by CMW, it will loaded because loadUncertifiedPackages is enabled in settings.json.");
						}
					}
				}
			};
			try {
				executor.execute(worker);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		executor.shutdown();
		while(!executor.isTerminated()) {
		}
		if(!webServer.getConfig().getSettings().isLoadUncertifiedPackages()) {
			this.log.info("If you want to load UNCERTIFIED packages, enable loadUncertifiedPackages in settings.json");
		}
	}

	private boolean checkMd5CMW(String md5) {
		try {
			URL checkCertificate = new URL("https://ip.conceptngo.fr/certificatePackage/" + md5);
			URLConnection hc = checkCertificate.openConnection();
			hc.setRequestProperty("User-Agent", "CMWL-Link, version: " + this.webServer.getConfig().getVersion());
			BufferedReader in = new BufferedReader(new InputStreamReader(hc.getInputStream()));
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(in.readLine());
			return (boolean) json.get("SUCCESS");
		} catch (IOException | ParseException e) {
			this.log.severe("Unable to certificate package, maybe API errors or check your internet connection.");
			return false;
		}
	}

	private String getMd5(File file) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			FileInputStream fis = new FileInputStream(file);
			byte[] byteArray = new byte[1024];
			int bytesCount = 0; 
			while ((bytesCount = fis.read(byteArray)) != -1) {
				digest.update(byteArray, 0, bytesCount);
			};
			fis.close();
			byte[] bytes = digest.digest();
			StringBuilder sb = new StringBuilder();
			for(int i=0; i< bytes.length ;i++){
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			return sb.toString().toUpperCase();
		} catch (NoSuchAlgorithmException | IOException e) {
			this.log.severe("Cannot read MD5 of file " + file.getAbsolutePath() + ", error: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	private void loadPackages(){
		Map<CMWLPackageDescription, Boolean> pluginStatuses = new HashMap<CMWLPackageDescription, Boolean>();
		for (Map.Entry<String, CMWLPackageDescription> entry : this.packagesToLoad.entrySet()){

			CMWLPackageDescription plugin = entry.getValue();
			if (!enablePackage(pluginStatuses, new Stack<CMWLPackageDescription>(), plugin)){
				this.log.warning("Failed to enable " + entry.getKey());
			}
		}
	}

	private boolean enablePackage(Map<CMWLPackageDescription, Boolean> pluginStatuses, Stack<CMWLPackageDescription> dependStack, CMWLPackageDescription plugin){
		if(pluginStatuses.containsKey(plugin)){
			return pluginStatuses.get(plugin);
		}
		Set<String> dependencies = new HashSet<>();
		dependencies.addAll(plugin.getDepends());
		boolean status = true;
		for(String dependName : dependencies){
			CMWLPackageDescription depend = this.packagesToLoad.get(dependName);
			Boolean dependStatus = (depend != null) ? pluginStatuses.get(depend) : Boolean.FALSE;
			if(dependStatus == null) {
				if(dependStack.contains(depend)){
					StringBuilder dependencyGraph = new StringBuilder();
					for (CMWLPackageDescription element : dependStack){
						dependencyGraph.append(element.getName()).append(" -> ");
					}
					dependencyGraph.append(plugin.getName() ).append(" -> ").append(dependName);
					this.log.warning("Circular dependency detected: " + dependencyGraph);
					status = false;
				}else{
					dependStack.push(plugin);
					dependStatus = this.enablePackage(pluginStatuses, dependStack, depend);
					dependStack.pop();
				}
			}
			if(dependStatus == Boolean.FALSE && plugin.getDepends().contains(dependName)){
				this.log.warning(new Object[]{String.valueOf(dependName)} + " (required by {1}) is unavailable " + plugin.getName());
				status = false;
			}
			if(!status){
				break;
			}
		}
		if(!this.webServer.getConfig().getSettings().isLoadUncertifiedPackages()) {
			if(this.packagesCertified.containsValue(plugin)) {
				loadPlugin(status, plugin);
			}
		}else {
			loadPlugin(status, plugin);
		}
		pluginStatuses.put(plugin, status);
		return status;
	}

	private void loadPlugin(boolean status, CMWLPackageDescription plugin){
		if(status){
			try{
				PackageClassLoader loader = null;
				Class<?> main = null;
				switch(webServer.getConfig().getStartingFrom()) {
				case BUNGEECORD:
					Preconditions.checkNotNull(plugin.getBg_main(), "Package from %s has no sp_main main class, maybe not compatible with BungeeCord ?", plugin.getFile());
					loader = new PackageClassLoader(getClass().getClassLoader(), plugin.getBg_main(), plugin.getFile().toURI().toURL(), this);
					main = loader.loadClass( plugin.getBg_main());
					break;
				case SPIGOT:
					Preconditions.checkNotNull(plugin.getSp_main(), "Package from %s has no bg_main main class, maybe not compatible with Spigot/Paper ?", plugin.getFile());
					loader = new PackageClassLoader(getClass().getClassLoader(), plugin.getSp_main(), plugin.getFile().toURI().toURL(), this);
					main = loader.loadClass( plugin.getSp_main());
					break;
				case VELOCITY:
					Preconditions.checkNotNull(plugin.getVl_main(), "Package from %s has no vl_main main class, maybe not compatible with Velocity ?", plugin.getFile());
					loader = new PackageClassLoader(getClass().getClassLoader(), plugin.getVl_main(), plugin.getFile().toURI().toURL(), this);
					main = loader.loadClass( plugin.getVl_main());
					break;
				}
				this.loaders.put(plugin.getName(), loader);
				CMWLPackage clazz = (CMWLPackage) main.getDeclaredConstructor().newInstance();
				clazz.init(webServer.getConfig().getStartingFrom(), plugin.getName(), plugin.getRoute_prefix(), plugin.getVersion(), defaultPath, this.log, webServer, utils);
				this.log.info("Loaded " + (this.packagesCertified.containsValue(plugin) ? "CERTIFIED" : "UNCERTIFIED") + " package " + plugin.getName() + " version " + plugin.getVersion() + " by " + plugin.getAuthor());
				this.packagesLoaded.add(clazz);
			} catch (Throwable t){
				this.log.severe("Error enabling package " + plugin.getName() + ":" + t.getMessage());
				t.printStackTrace();
			}
		}
	}

	public Class<?> getClassByName(final String name) {
		Class<?> cachedClass = this.classes.get(name);
		if (cachedClass != null) {
			return cachedClass;
		} else {
			for (final String current : this.loaders.keySet()) {
				final PackageClassLoader loader = this.loaders.get(current);
				try {
					cachedClass = loader.findClass(name, false);
				}
				catch (final ClassNotFoundException e) {}
				if (cachedClass != null) {
					return cachedClass;
				}
			}
		}
		return null;
	}
}
