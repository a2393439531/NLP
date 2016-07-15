package templateSeg;

public class TestRule {

	public static void main(String[] args) throws Exception {
		String rule = "<adj>的<n>";
		String ruleName = "1";
		System.out.println(RightParser.parse(ruleName, rule));
	}

}
