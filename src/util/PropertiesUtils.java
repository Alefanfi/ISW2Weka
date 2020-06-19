package util;

import java.io.IOException;
import java.util.Properties;
import java.io.InputStream;

public final class PropertiesUtils {
	
	private PropertiesUtils() {}
	
	public static String getProperty(String properties) throws IOException {
		
		Properties prop;
		
		try {
			
			prop = new Properties();
			InputStream fis = PropertiesUtils.class.getClassLoader().getResourceAsStream("conf.properties");
			prop.load(fis);
			
			fis.close();
			return prop.getProperty(properties);
			
		
		}catch(IOException ioException) {
			
			throw new IOException(ioException);
		}
		
	}

}
