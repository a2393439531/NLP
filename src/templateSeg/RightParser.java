package templateSeg;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * 解析规则
 * @author luogang
 *
 */
public class RightParser {
	public final static String PositionSplit = "p-";

	public static Rule parse(String ruleName,String right){
		if (right == null)
			return null;
		//right = right.trim();
		
		Rule rule = new Rule();
		
		int senLen = right.length();// 首先计算出传入的这句话的字符长度
		int i = 0;// i是用来控制匹配的起始位置的变量

		while (i < senLen)// 如果i小于此句话的长度就进入循环
		{
			//i = matchSpace(right, i); // 跳过空格
			String semName = ruleName + PositionSplit + i; // 根据规则加上编号生成语义类的名字
			int offset = matchOptionWords(right, i,  semName,rule);
			if (offset > i) {
				i = offset + 1;
				rule.rhs.add(semName);
				continue;
			}
			offset = matchNormalWord(right, i);// 普通的词
			if (offset > i)// 已经匹配上
			{
				// 下次匹配点在这个词之后
				int start = i;
				int end = offset;
				String word = right.substring(start, end);
				//logger.debug("word: " + word);
				rule.addWord(word, semName);
				i = offset;
				rule.rhs.add(semName);
				continue;
			}
			StringBuilder content = new StringBuilder();
			offset = matchRuleName(right, i, content);
			if (offset > i)// 匹配上规则
			{
				// 下次匹配点在这个词之后
				String rightRuleName = content.toString();
				//logger.debug("规则名: " + rightRuleName);
				i = offset + 1;
				rule.rhs.add(rightRuleName);
				continue;
			}
		}

		return rule;
	}

	public static int matchRuleName(String sentence, int offset,
			StringBuilder bracketContent) {
		if (sentence.charAt(offset) == '<') {
			bracketContent.setLength(0); // 方括号中的内容
			int i = offset + 1;
			boolean endWithBracket = false;
			while (i < sentence.length()) {
				char c = sentence.charAt(i);
				if (c == '>') {
					endWithBracket = true;
					break;
				}
				bracketContent.append(c);
				i++;
			}

			if (endWithBracket && bracketContent.length() > 0) {
				return i;
			}
		}

		return offset;
	}

	public static int matchNormalWord(String sentence, int offset) {
		int i = offset;
		while (i < sentence.length()) {
			char c = sentence.charAt(i);
			if (c == '[' || c == '<') {
				break;
			}
			i++;
		}
		return i;
	}

	// 一个小的语义类
	public static int matchOptionWords(String sentence, int offset, String semName, Rule rule) {
		if (sentence.charAt(offset) == '[') {
			StringBuilder bracketContent = new StringBuilder(); // 方括号中的内容
			int i = offset + 1;
			boolean endWithBracket = false;
			while (i < sentence.length()) {
				char c = sentence.charAt(i);
				if (c == ']') {
					endWithBracket = true;
					break;
				}
				bracketContent.append(c);
				i++;
			}

			if (endWithBracket && bracketContent.length() > 0) {
				ArrayList<String> words = getOptionWords(bracketContent
						.toString());
				// String type = semName; // 语义类的名字
				// for (String w : words) {
				// dic.addWord(w, type);
				// }
				addWords(words, semName, rule);

				//StringBuilder content = new StringBuilder();
				//int newStart = i + 1;
				//offset = matchTag(sentence, newStart, content);
				//if (offset > newStart)// 匹配上规则
				//{
					// 下次匹配点在这个词之后
				//	String tagName = content.toString();
					//logger.debug("标签名: " + tagName);
					//rule.ruleTag.put(semName, tagName);
					// dic.addWord(word, semName);
				//	i = offset;
					// rhs.add(rightRuleName);
					// continue;
				//}

				return i;
			}
		}

		return offset;
	}

	public static void addWords(ArrayList<String> words,
			String semName, Rule rule) {
		for (String w : words) {
			String ruleName = getRuleName(w);
			if (ruleName == null) {
				String type = semName; // 语义类的名字

				// logger.debug("TO add Word " + w);
				rule.addWord(w, type); // 终结符放入词典三叉树
			} else {
				//TODO
				//rule.addRule(new NonTerminal(semName), ruleName); // 非终结符放入文法树
			}
		}
	}

	public static String getRuleName(String lhs) {
		int i = 0;
		boolean beginWithBracket = false;
		while (i < lhs.length()) {
			char c = lhs.charAt(i);
			if (c == '<') {
				beginWithBracket = true;
				break;
			}
			i++;
		}
		if (!beginWithBracket) {
			return null;
		}

		i++;
		StringBuilder bracketContent = new StringBuilder();
		boolean endWithBracket = false;
		while (i < lhs.length()) {
			char c = lhs.charAt(i);
			if (c == '>') {
				endWithBracket = true;
				break;
			}
			bracketContent.append(c);
			i++;
		}

		if (endWithBracket && bracketContent.length() > 0) {
			return bracketContent.toString();
		}
		return null;
	}

	// 得到可选终结符和非终结符
	public static ArrayList<String> getOptionWords(String t) {
		// logger.debug("input " + t);
		ArrayList<String> words = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(t, "|"); // |分隔
		while (st.hasMoreTokens()) {
			String s = st.nextToken();
			//logger.debug("get OptionWord " + s);
			words.add(s);
		}
		return words;
	}

	public static int matchSpace(String sentence, int offset) {
		int first = offset;

		for (; first < sentence.length(); first++)
			if (!Character.isWhitespace(sentence.charAt(first)))
				break;

		return first;
	}

}
