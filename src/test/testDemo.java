package test;

import Lucene.LuceneTest;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.util.List;

/**
 * Created by sony on 16-12-3.
 */
public class testDemo {
    @Test
    public void test1() throws Exception{
        String indexPath = "/home/sony/bbb";
        LuceneTest.getDataAndIndex(new File(indexPath));
    }

    @Test
    public void test2() throws Exception{
        String indexPath = "/home/sony/bbb";
        String stringquery = "招商银行手机客户端";/*招商银行分词分不出招行，其描述中也没有招行出现*/
        LuceneTest.search(stringquery, new File(indexPath));
    }

    @Test
    public void test3(){
        String str = "收拾屋子的时候找出很多玩具小兵，他们吵吵嚷嚷着要去打仗，要飞机。我就给他们折了几架飞机，他们很高兴，又抬出一张照片说，我们司令不见了，你这么有本事，请帮帮忙吧。我愣了愣，说，你们的司令不会回来了。小兵们哭了，问，司令是牺牲了吗？-没有，他只是长大了。";
        Analyzer analyzer = new IKAnalyzer();
        List<String> list = LuceneTest.getWords(str, analyzer);
        for (String s : list){
            System.out.println(s);
        }
    }

    @Test
    public void test4() throws Exception{
        String indexPath = "/home/sony/bbb";
        String[] stringquery = {"招商", "银行", "手机", "客户端"};/*招商银行分词分不出招行，其描述中也没有招行出现*/
        LuceneTest.booleanQuiery(new File(indexPath), stringquery);
    }
}
