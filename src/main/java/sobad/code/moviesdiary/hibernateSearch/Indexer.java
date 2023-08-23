package sobad.code.moviesdiary.hibernateSearch;

import jakarta.persistence.EntityManager;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component
public class Indexer {
    private EntityManager entityManager;

    private static final int THREAD_NUMBER = 4;

    public Indexer(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void indexData(String indexClassName) {
        try {
            SearchSession searchSession = Search.session(entityManager);
            Class<?> classToIndex = Class.forName(indexClassName);
            MassIndexer massIndexer = searchSession
                    .massIndexer(classToIndex)
                    .threadsToLoadObjects(THREAD_NUMBER);

            massIndexer.startAndWait();
        } catch (InterruptedException e) {
            throw new RuntimeException("Indexing interrupted");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
