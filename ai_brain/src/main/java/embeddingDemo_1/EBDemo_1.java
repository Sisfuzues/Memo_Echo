package embeddingDemo_1;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.util.List;

public class EBDemo_1 {
    private static OpenAiEmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
            .apiKey("sk-363167240c50469c823d9e2e72ba8da8")
            .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
            .modelName("text-embedding-v4")
            .build();

    //构建向量库
    public static EmbeddingStore embeddingStore(){
        //将文档加载进内存
        List<Document> documents = ClassPathDocumentLoader.loadDocuments("documents");
        //实例化向量数据库对象
        InMemoryEmbeddingStore embeddingStore = new InMemoryEmbeddingStore();
        //实例化文本向量化兼入库对象
        EmbeddingStoreIngestor embeddingStoreIngestor = EmbeddingStoreIngestor.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .build();
        //将文档处理向量化并加载进数据库
        embeddingStoreIngestor.ingest(documents);
        return embeddingStore;
    }

    //构建检索器
    public static ContentRetriever contentRetriever(EmbeddingStore embeddingStore){

        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .minScore(0.5)
                .maxResults(1)
                .embeddingModel(embeddingModel)
                .build();

    }



    public static void main(String[] args) {


        EmbeddingStore store = embeddingStore();
        ContentRetriever contentRetriever = contentRetriever(store);
        Query query = Query.from("做那个AI日程助手了吗");
        List<Content> contents = contentRetriever.retrieve(query);
        String conText = contents.toString();
        System.out.println(conText);
    }

}
