package probSeg;

import java.io.IOException;
import java.util.ArrayDeque;

public class TestSeg {

	public static void main(String[] args) throws IOException {
		String sentence = "中国成立了";// "大学生活动中心";
		Segmenter seg = new Segmenter();
		ArrayDeque<Integer> path = seg.split(sentence);

		//输出结果
		int start = 0;
		for (Integer end : path) {
			System.out.print(sentence.substring(start, end) + "/ ");
			start = end;
		}
	}
}
