package bcoop.util;

public class OSCapabilities {
	private static boolean linkSupported;
	private static boolean blockDeviceSupported;
	private static boolean singleRootFsSupported;

	static{
		String osName = System.getProperty("os.name");
		if(osName.equalsIgnoreCase("Windows XP")){
			linkSupported = false;
			blockDeviceSupported = false;
			singleRootFsSupported = false;
		}
		else{//TODO add more OS here..
			linkSupported = true;
			blockDeviceSupported = true;
			singleRootFsSupported = true;
		}
	}

	public static boolean isBlockDeviceSupported() {
		return blockDeviceSupported;
	}

	public static boolean isLinkSupported() {
		return linkSupported;
	}

	public static boolean isSingleRootFs() {
		return singleRootFsSupported;
	}
}
