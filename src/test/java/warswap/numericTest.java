package warswap;

public class numericTest {
	public static boolean isNumeric(String string) {
	      return string.matches("^[-+]?\\d+(\\.\\d+)?$");
	  }
	
	public static void main(String[] args) {
		System.out.println(isNumeric("12.2"));
	}
}
