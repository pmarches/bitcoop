package bcoop.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

public class BitCoopFile extends File {
	private static final long serialVersionUID = -3776367595828759505L;
	private static final int TYPE_FILE = 0;
	private static final int TYPE_LINK=1;
	private static final int TYPE_DIR=2;
	private static final int TYPE_BLK=3;
	private static final int TYPE_CHR=4;
	private static final int TYPE_SOCK=5;
	private static final int TYPE_FIFO=6;

	private static boolean isLibraryLoaded;

	private String linkDestination;
	private volatile int fileType=-1;

	static{
		try{
			System.loadLibrary("BitCoopFile");
			isLibraryLoaded = true;
		}
		catch(UnsatisfiedLinkError e){
			isLibraryLoaded = false;
			String osName = System.getProperty("os.name").toLowerCase();
			boolean isWindows = osName.startsWith("windows");
			if(isWindows==false){
				Logger.getLogger(BitCoopFile.class).warn("Could not load extension to handle links! Treating all links as files or directories!");
			}
		}
	}

	private native void getInformation(byte[] absPath);
	private native void setMode(byte[] filepath, int mode);	
	private native void createSymlink(byte[] linkpath, byte[] linkDestination);	
	
	public BitCoopFile(String arg0) {
		super(arg0);
	}

	public BitCoopFile(BitCoopFile parent, String string) {
		super(parent, string);
	}
	
	private void init() {
		if(isLibraryLoaded){
			try {
				if(exists() && fileType == -1 ){
					getInformation(this.getAbsolutePath().getBytes("UTF-8"));
					if(fileType == -1){
						throw new RuntimeException("Could not set the fileType??");
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public boolean isLink(){
		init();
		return fileType==TYPE_LINK;
	}
	
	public String getLinkDestination(){
		if(isLink() == false){
			return null;
		}
		return linkDestination;
	}
	
	public boolean isFile(){
		if(!isLibraryLoaded){
			return super.isFile();
		}
		init();
		return fileType==TYPE_FILE;
	}
	
	public boolean isDirectory(){
		if(!isLibraryLoaded){
			return super.isDirectory();
		}
		init();
		return this.fileType==TYPE_DIR;
	}
	
	public boolean isSocket(){
		if(!isLibraryLoaded){
			return false;
		}
		return this.fileType==TYPE_SOCK;
	}
	
	public boolean isCharacterDevice(){
		if(!isLibraryLoaded){
			return false;
		}
		init();
		return this.fileType==TYPE_CHR;
	}
	
	public boolean isBlockDevice(){
		if(!isLibraryLoaded){
			return false;
		}
		init();
		return this.fileType==TYPE_BLK;
	}

	public boolean isFIFO(){
		if(!isLibraryLoaded){
			return false;
		}
		init();
		return this.fileType==TYPE_FIFO;
	}
	
	public static BitCoopFile[] listRoots(){
		File[] fileRoot = File.listRoots();
		return fileToBitCoopFile(fileRoot);
	}

	public BitCoopFile[] listFiles(){
		return fileToBitCoopFile(super.listFiles());
	}

	public BitCoopFile[] listFiles(FileFilter filter){
		return fileToBitCoopFile(super.listFiles(filter));
	}

	private static BitCoopFile[] fileToBitCoopFile(File[] fileArray) {
		if(fileArray==null){
			return null;
		}
		BitCoopFile[] bCoopRoots = new BitCoopFile[fileArray.length];
		for(int i=0; i<fileArray.length; i++){
			bCoopRoots[i] = new BitCoopFile(fileArray[i].getPath());
		}
		return bCoopRoots;
	}
	
	public static BitCoopFile createTempFile(String prefix, String suffix) throws IOException {
		return new BitCoopFile(File.createTempFile(prefix, suffix).getAbsolutePath());
	}

	public String getAbsolutePath(){
		return super.getAbsolutePath().replace('\\', '/');
	}

	public String getPath(){
		return super.getPath().replace('\\', '/');
	}
	
	public BitCoopFile getParentFile(){
		return new BitCoopFile(super.getParentFile().getAbsolutePath());
	}
	
	public void setMode(int mode) {
		try {
			if(!isLibraryLoaded){
				return;
			}
			setMode(this.getAbsolutePath().getBytes("UTF-8"), mode);
		} catch (UnsupportedEncodingException e) {
			//Should never happend
			e.printStackTrace();
		}
	}

	public void createSymbolicLink(String linkDestination) {
		try {
			if(!isLibraryLoaded){
				return;
			}
			this.createSymlink(this.getAbsolutePath().getBytes("UTF-8"), linkDestination.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			//Should never happend
			e.printStackTrace();
		}
	}
}
