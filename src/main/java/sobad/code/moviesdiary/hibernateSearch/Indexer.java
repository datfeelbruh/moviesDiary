package sobad.code.moviesdiary.hibernateSearch;

import jakarta.persistence.EntityManager;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Indexed;
import org.springframework.transaction.annotation.Transactional;
import sobad.code.moviesdiary.entities.Movie;

@Transactional
@Component
public class Indexer implements ApplicationListener<ApplicationReadyEvent> {
    private EntityManager entityManager;

    private static final int THREAD_NUMBER = 4;

    public Indexer(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            SearchSession searchSession = Search.session(entityManager);
            Class<?> classToIndex = Movie.class;
            MassIndexer massIndexer = searchSession
                    .massIndexer(classToIndex)
                    .threadsToLoadObjects(THREAD_NUMBER);

            massIndexer.startAndWait();
        } catch (InterruptedException e) {
            throw new RuntimeException("Indexing interrupted");
        }
    }
}
