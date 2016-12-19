package Lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.sql.ResultSet;
import java.util.*;

/**
 * Created by sony on 16-11-29.
 */
public class LuceneTest {

//    public String tableName;

//    public static void setUp() throws IOException {
//        Directory directory = FSDirectory.open(new File("/LuceneFileIndexDir"));
//    }

    /*从数据库获取信息，并建立索引*/
    public static void getDataAndIndex(File file) throws Exception {

        Analyzer analyzer = new IKAnalyzer();
        Directory dir = FSDirectory.open(file);
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46, analyzer);
        IndexWriter writer = new IndexWriter(dir,config);
        int id = 0;
//
//        ResultSet rs = null;
        String sql1 = "select * from asec_app_description";
        JDBCUtil jdbcUtil = new JDBCUtil();
        ResultSet rs = jdbcUtil.QuerySql(sql1);
        Document doc1 = new Document();
//        IndexableField field1 = new TextField("app_name", rs.getString(1), Field.Store.YES);
//        IndexableField field2 = new TextField("app_description", rs.getString(2), Field.Store.YES);
        System.out.println(rs.getString(1) + "description:  " + rs.getString(2));

        doc1.add(new TextField("app_name", rs.getString(1), Field.Store.YES));
        doc1.add(new TextField("app_description", rs.getString(2), Field.Store.YES));
        doc1.add(new IntField("id", ++id, Field.Store.YES));
        writer.addDocument(doc1);


        while (rs.next()){
            Document doc = new Document();
            String a = rs.getString(1);
            String b = rs.getString(2);
            if(a == null){
                a = "";
            }
            if(b == null){
                b = "";
            }
//            IndexableField field_name = new TextField("app_name", a, Field.Store.YES);
//            IndexableField field_description = new TextField("description", b, Field.Store.YES);
            System.out.println(a + "description:  " + b);
            doc.add(new TextField("app_name", a, Field.Store.YES));
            doc.add(new TextField("app_description", b, Field.Store.YES));
            doc1.add(new IntField("id", ++id, Field.Store.YES));
            writer.addDocument(doc);
        }
        writer.commit();
        writer.close();

    }

    /*对索引进行多域（多关键词）搜索*/
    public static void search(String str, File file) throws Exception{
        Analyzer analyzer = new IKAnalyzer();
        Directory dir = FSDirectory.open(file);
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher indexSearcher = new IndexSearcher(reader);
        String[] fields = {"app_name","app_description"};
        Map<String, Float> boosts = new HashMap<String , Float>();
        boosts.put("app_name", 1.0f);
        boosts.put("app_description", 1.0f);
        BooleanClause.Occur[] occ = {BooleanClause.Occur.SHOULD, BooleanClause.Occur.SHOULD};

//        MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_46, fields, analyzer, boosts);
//        Query query = parser.parse(str);
//        BooleanQuery query = new BooleanQuery();


        Query query = MultiFieldQueryParser.parse(Version.LUCENE_46, str, fields, occ, analyzer);
//        Query query = new TermQuery(new Term("app_name", str));
        TopDocs topDocs = indexSearcher.search(query, 1000000);

        int hits = topDocs.totalHits;
        System.out.println("hits: " + hits);

        ScoreDoc[] scoreDoc = topDocs.scoreDocs;
        for(int i = 0; i < 20; i++){
            int docId = scoreDoc[i].doc;
            System.out.println("docId: " + docId);
            Document document = indexSearcher.doc(docId);
            float sorce = scoreDoc[i].score;
            System.out.println("Name+++++++++++  " + document.get("app_name"));
            System.out.println("description++++  " + document.get("app_description"));
            System.out.println("Scorc++++++++++  " + sorce);
        }
//        for (ScoreDoc scoreDoc1:scoreDoc){
//            int docId = scoreDoc1.doc;
//            System.out.println("docId: " + docId);
//            Document document = indexSearcher.doc(docId);
//            System.out.println("Name+++++++++++  " + document.get("app_name"));
//            System.out.println("description++++ " + document.get("app_description"));
//
//        }
    }

    public static void booleanQuiery(File file, String[] str) throws Exception{
        Directory directory = FSDirectory.open(file);
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        BooleanQuery query = new BooleanQuery();
        Query[] queries1 = new Query[str.length];
        Query[] queries2 = new Query[str.length];
        for (int i = 0; i < str.length; i++){
            queries1[i] = new TermQuery(new Term("app_name", str[i]));
            if(i == 0)
                queries1[0].setBoost(1.3f);
            if(i == 1)
                queries1[1].setBoost(1.2f);
            queries2[i] = new TermQuery(new Term("app_description", str[i]));
            query.add(queries1[i], BooleanClause.Occur.SHOULD);
            query.add(queries2[i], BooleanClause.Occur.SHOULD);
        }
        TopDocs topDocs = indexSearcher.search(query, 1000000);

        int hits = topDocs.totalHits;
        System.out.println("hits: " + hits);

        ScoreDoc[] scoreDoc = topDocs.scoreDocs;
        for(int i = 0; i < 50; i++){
            int docId = scoreDoc[i].doc;
            System.out.println("docId: " + docId);
            Document document = indexSearcher.doc(docId);
            float sorce = scoreDoc[i].score;
            System.out.println("Name+++++++++++  " + document.get("app_name"));
            System.out.println("description++++  " + document.get("app_description"));
            System.out.println("Scorc++++++++++  " + sorce);
        }
    }

    /*删除索引*/
    public static void deleteIndex(File file) throws Exception{
        Analyzer analyzer = new IKAnalyzer();
        Directory directory = FSDirectory.open(file);
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46, analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, config);

        indexWriter.deleteAll();
//        indexWriter.deleteDocuments(new Term("", ""));//加域id进行删除
        indexWriter.commit();
        indexWriter.close();
    }

    /*查看分词结果*/
    public static List<String> getWords(String str, Analyzer analyzer){
        List<String> result = new ArrayList<String>();
        TokenStream stream = null;
        try {
            stream = analyzer.tokenStream("content", new StringReader(str));
            CharTermAttribute attr = stream.addAttribute(CharTermAttribute.class);
            stream.reset();
            while(stream.incrementToken()){
                result.add(attr.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(stream != null){
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }


}
