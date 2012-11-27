package test.com.lavans.lacoder.text;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lavans.lacoder.commons.NumberUtils;

public class NumberUtilTest extends TestCase {
	private static Log logger = LogFactory.getLog(NumberUtilTest.class.getName());

	public void testIsNumeric(){
		logger.info("1:"+NumberUtils.isNumeric("123.45"));
		logger.info("2:"+NumberUtils.isNumeric("1121223.45"));
		logger.info("3:"+NumberUtils.isNumeric("335,123.45"));
		logger.info("4:"+NumberUtils.isNumeric("123.5"));
		logger.info("5:"+NumberUtils.isNumeric("123.0"));
		logger.info("6:"+NumberUtils.isNumeric("123.00abc00"));
		logger.info("7:"+NumberUtils.isNumeric("123.xe0x00"));
		logger.info("8:"+NumberUtils.isNumeric("a.0000"));
		logger.info("9:"+NumberUtils.isNumeric("1"));
	}
}
