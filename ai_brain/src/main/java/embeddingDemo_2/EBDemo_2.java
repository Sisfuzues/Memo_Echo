package embeddingDemo_2;


import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import org.apache.rocketmq.common.message.Message;
import org.testcontainers.qdrant.QdrantContainer;

import javax.swing.tree.AbstractLayoutCache;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class EBDemo_2 {
    private static int port = 6334;//使用的端口协议 入库与检索时使用
    private static int httpport = 6333;//建表时使用
    private static String collectionName = "langchain4j-1";//使用的表名
    private static int dimension = 512;//语义向量维度  需要使用模型支持的维度

    //embeddingModel
    private static OpenAiEmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
            .apiKey("sk-363167240c50469c823d9e2e72ba8da8")
            .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
            .modelName("text-embedding-v4")
            .dimensions(dimension)
            .build();

    private static void createCollection(String host, int port) throws Exception {
        String json = """
    {
      "vectors": {
        "size": 512,
        "distance": "Cosine"
      }
    }
    """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + host + ":" + port + "/collections/" + collectionName))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }


    //测试本地的qdrand数据库实例
    public static void qdrandRAG(){
        //使用test容器拉取docker镜像实例化qdrand对象 需要确定固定版本号
        try (QdrantContainer qdrant = new QdrantContainer("qdrant/qdrant:latest")){
            qdrant.start();    //检测docker是否可用 执行拉取 创建并启动容器 将容器的6333与6334端口映射在本地的随机端口
            String host = qdrant.getHost();//获取qdrand实例所在本地ip地址
            Integer tPort = qdrant.getMappedPort(port);//获取port对应的本地端口
            Integer htPort = qdrant.getMappedPort(httpport);

            createCollection(host,htPort);//建表

            //实例化embeddingStore
            EmbeddingStore<TextSegment> embeddingStore =
                    QdrantEmbeddingStore.builder()
                            .host(host)
                            .port(tPort)
                            .collectionName(collectionName)
                            .build();


            TextSegment textSegment_1 = TextSegment.from("今天");  //在这里输入日程文本
            Embedding embedding_1 = embeddingModel.embed(textSegment_1).content();//调用embed方法并提取其中的content部分（向量）
            TextSegment textSegment_2 = TextSegment.from("明天");
            Embedding embedding_2 = embeddingModel.embed(textSegment_2).content();

            String id_1 = embeddingStore.add(embedding_1 , textSegment_1);//将向量1传入数据库并附上textSegment_1

            String id_2 = embeddingStore.add(embedding_2 , textSegment_2);

            EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                    .queryEmbedding(embeddingModel.embed("今早").content())//输入问题
                    .minScore(0.5)
                    .maxResults(1)
                    .build();
            List<EmbeddingMatch<TextSegment>> matches =
                    embeddingStore.search(searchRequest)
                            .matches();
            EmbeddingMatch<TextSegment> embeddingMatch = matches.get(0);

            System.out.println(embeddingMatch.score());
            System.out.println(embeddingMatch.embedded().text());



        }//容器闭包
        catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
    //记得测试模仿消息队列的传入，并同时进行查询操作





    public static void main(String[] args) {
        qdrandRAG();
    }
}

