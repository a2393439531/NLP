package templateSeg;

import java.util.ArrayDeque;
import java.util.Arrays;

import junit.framework.TestCase;

public class TestRuleSegmenter extends TestCase {

	public static void main(String[] args) {
		RuleSegmenter seg = new RuleSegmenter();
		String pattern="<p><n><v>";
		seg.addRule(pattern);
		
		String text="为毒贩求情";
		
		ArrayDeque<Integer> path = seg.split(text);

		//输出结果
		int start = 0;
		for (Integer end : path) {
			System.out.print(text.substring(start, end) + "/ ");
			start = end;
		}
	}
	
	public static void testBasic(){
		RuleSegmenter seg = new RuleSegmenter();
		String pattern="<p><n><v>";
		seg.addRule(pattern);
		
		String text="为毒贩求情";
		AdjList g = seg.getLattice(text);
		System.out.println("图 " + g);
		System.out.println("最佳前驱节点 " + Arrays.toString(seg.prevNode));
		ArrayDeque<Integer> path = seg.bestPath(g);

		//输出结果
		int start = 0;
		for (Integer end : path) {
			System.out.print(text.substring(start, end) + "/ ");
			start = end;
		}
		
	}

}