package test.com.lavans.lacoder.util2;

import java.util.Map;

import com.lavans.lacoder.util.ParameterUtils;

public class ParameterUtilsTest {
	public static void main(String args[]){
		new ParameterUtilsTest().testGoogle();

//		IParameterizable pageInfo = new PageInfo();
//		String str ="rows=1&rows=2&page=1";
//		pageInfo.setParameters(ParameterUtils.toMap(str),"");
//
//		System.out.println(pageInfo.getParameters(""));
	}

	public void testGoogle(){
		String str =
		"statusDetail=host aspmx.l.google.com[173.194.79.27] said: 550-5.1.1 The email account that you tried to reach does not exist. Please try 550-5.1.1 double-checking the recipient's email address for typos or 550-5.1.1 unnecessary spaces. Learn more at 550 5.1.1 http://support.google.com/mail/bin/answer.py?answer=6596 s4si18431103pbc.195 (in reply to RCPT TO command)&dsn=5.1.1&delay=6.9&";

		Map<String, String[]> map = ParameterUtils.toMap(str);
		System.out.println(map.get("statusDetail")[0]);
	}
}
