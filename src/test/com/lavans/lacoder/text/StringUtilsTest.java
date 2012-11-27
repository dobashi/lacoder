package test.com.lavans.lacoder.text;

public class StringUtilsTest {
	public static void main(String args[]){
		System.out.println("　()".trim());
		System.out.println("　 　(　)　".replaceAll("^[　\\s]*","").replaceAll("　$",""));

	}
}
