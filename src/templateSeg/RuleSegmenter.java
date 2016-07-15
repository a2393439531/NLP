package templateSeg;

import java.util.ArrayDeque;
import java.util.Iterator;

/**
 * 规则的分词
 * @author luogang
 *
 */
public class RuleSegmenter {
	TernarySearchTrie dic = null;
	public Trie rule = new Trie(); // 文法树
	static int seq = 0; // 用来保证唯一性
	int[] prevNode; // 最佳前驱节点
	double[] prob;

	public RuleSegmenter (){
		DicFactory dicFactory = // new DicFileFactory();
				new DicDBFactory();
		dic = dicFactory.create();
	}

	public RuleSegmenter(DicFactory dicFactory){
		dic = dicFactory.create();
	}
	
	// 增加一个切分规则
	public void addRule(String pattern) {
		String uniqueName = String.valueOf(seq); // 唯一的名字
		Rule rightRule = RightParser.parse(uniqueName, pattern);
		rule.addRule(rightRule.rhs);
		dic.addWords(rightRule.words);
		seq++;
	}

	/**
	 * 
	 * @param word
	 * @return AdjListDoc 表示一个列表图
	 */
	public AdjList getLattice(String sentence) {
		if (sentence == null || sentence.length() == 0) {
			return null;
		}
		int atomCount;
		int start = 0;
		atomCount = sentence.length();
		//TODO 原子切分
		AdjList g = new AdjList(atomCount + 1); // 初始化在Dictionary中词组成的图
		prevNode = new int[g.verticesNum];
		while (true) // 在这里开始进行分词
		{
			TernarySearchTrie.PrefixRet prefix = new TernarySearchTrie.PrefixRet();
			boolean matchRet = dic.matchAll(sentence, start, prefix);
			if (matchRet) {// 匹配上
				for (WordEntry word : prefix.values) {
					int end = start + word.word.length();// 词的结束位置
					CnToken tokenInf = new CnToken(start, end, word.word,
							word.types);
					g.addEdge(tokenInf);
				}
				start++;
			} else { // 没匹配上
				g.addEdge(new CnToken(start, start + 1, sentence.substring(
						start, start + 1), null));
				start++;
			}
			if (start >= atomCount) {
				break;
			}
		}

		for (int offset = 0; offset < sentence.length(); ++offset) {
			GraphMatcher.MatchValue match = GraphMatcher.intersect(g, offset,
					rule);

			// 如果匹配上规则，就为匹配上的这几个节点设置最佳前驱节点
			if (match != null) {
				//System.out.println("匹配结果:" + match);
				for (NodeType n : match.posSeq) {
					prevNode[n.end] = n.start; //TODO
				}
			}
		}

		return g;
	}

	public ArrayDeque<Integer> bestPath(AdjList g) {
		ArrayDeque<Integer> path = new ArrayDeque<Integer>();
		for (int i = (g.verticesNum - 1); i > 0; i = prevNode[i]) // 从右向左找最佳前驱节点
		{
			path.addFirst(i);

		}
		return path;
	}

	public ArrayDeque<Integer> split(String sentence) {
		AdjList g = getLattice(sentence);
		ArrayDeque<Integer> path = bestPath(g);
		return path;
	}
	
	// 计算节点i的最佳后继节点
	void getBestPrev(AdjList g, int i) {
		Iterator<CnToken> it = g.getSuc(i);// 得到前驱词集合，从中挑选最佳前趋词
		double maxProb = Double.NEGATIVE_INFINITY;
		int maxNode = -1;

		while (it.hasNext()) {
			CnToken t = it.next();
			double nodeProb = prob[t.end] + t.logProb;// 候选节点概率
			if (nodeProb > maxProb)// 概率最大的算作最佳前趋
			{
				maxNode = t.start;
				maxProb = nodeProb;
			}
		}
		prob[i] = maxProb;// 节点概率
		prevNode[i] = maxNode;// 最佳前驱节点
		// System.out.println("node "+i + " best prev is"+ maxID);
	}
}
