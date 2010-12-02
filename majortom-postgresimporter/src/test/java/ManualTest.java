import java.io.InputStream;

import de.topicmapslab.majortom.importer.ImportFile;

/**
 * 
 */

/**
 * @author niederhausen
 *
 */
public class ManualTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		InputStream is = ManualTest.class.getResourceAsStream("/manual.ctm");
		if (is==null)
			throw new Exception("Couldn't find manul.ctm");
		
		ImportFile.importFile(is, "http://test.de/");
	}

}
