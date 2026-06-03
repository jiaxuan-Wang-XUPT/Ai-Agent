package com.skye.aiagentbackend.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class HealthAppVectorStoreConfig {

    @Resource
    private HealthAppDocumentLoader healthAppDocumentLoader;

    @Resource
    private MyTokenTextSplitter myTokenTextSplitter;

    @Resource
    private MyKeywordEnricher myKeywordEnricher;

    @Bean
    VectorStore healthAppVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        //
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel)
                .build();
        List<Document> documents = healthAppDocumentLoader.loadMarkdowns();

        // 自主切分 (是通过MyTkenTextSplitter实现的，但是参数是自定义的，不推荐，因为容易语义丢失，所以尽量采取智能切分)
//        List<Document> splitDocuments = myTokenTextSplitter.splitCustomized(documents);

        //自动补充关键词元信息(把最后的documents换成enrichDocuments即可实现，同上，都可直接在阿里云百炼中直接设置)
//        List<Document> enrichDocuments = myKeywordEnricher.enrichDocuments(documents);

        simpleVectorStore.add(documents);
        return simpleVectorStore;
    }


}
