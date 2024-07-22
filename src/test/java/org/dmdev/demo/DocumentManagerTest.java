package org.dmdev.demo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.print.Doc;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DocumentManagerTest {

    private static Map<String, DocumentManager.Document> test_storage = new HashMap<>();

    private static DocumentManager documentManager = new DocumentManager();

    private final static DocumentManager.Document book_1 = new DocumentManager.Document(
            "61ee816f-b321-4e04-b35f-79a38dd37426",
            "Тіні забутих предків",
            "Іван був дев'ятнадцятою дитиною в гуцульській родині Палійчуків.",
            new DocumentManager.Author(
                    "97b56df5-1169-42b7-9da3-6a65ae8258c4",
                    "Михайло Коцюбинський"
            ),
            LocalDateTime.of(1912, Month.JANUARY, 1, 0, 0).toInstant(ZoneOffset.UTC)
    );
    private final static DocumentManager.Document book_2 = new DocumentManager.Document(
            "6d0265183-fb24-4cfe-b55a-fcfc26ff351f",
            "Intermezzo",
            "Лишилось тільки ще спакуватись... Се було одно з тих незчисленних \"треба\", які мене так утомили і не давали спати.",
            new DocumentManager.Author(
                    "97b56df5-1169-42b7-9da3-6a65ae8258c4",
                    "Михайло Коцюбинський"
            ),
            LocalDateTime.of(1908, Month.JANUARY, 1, 0, 0).toInstant(ZoneOffset.UTC)
    );
    private final static DocumentManager.Document book_3 = new DocumentManager.Document(
            "aa614e44-0ccb-4c27-a8da-6417e5b8dfc4",
            "The Shadow Out of Time",
            "After twenty-two years of nightmare and terror, saved only by a desperate conviction of the mythical source of certain impressions, I am unwilling to vouch for the truth of that which I think I found in Western Australia on the night of July 17–18, 1935.",
            new DocumentManager.Author(
                    "46360406-445d-467b-a26e-486ceac3f4e2",
                    "Howard Phillips Lovecraft"
            ),
            LocalDateTime.of(1936, Month.JUNE, 1, 0, 0).toInstant(ZoneOffset.UTC)
    );
    private final static DocumentManager.Document book_4 = new DocumentManager.Document(
            "31db7354-c2a9-4a2d-966b-62913839482e",
            "Dune",
            "In the week before their departure to Arrakis, when all the final scurrying about had reached a nearly unbearable frenzy, an old crone came to visit a mother of the boy, Paul.",
            new DocumentManager.Author(
                    "a057efb3-40be-4d43-a174-fdb720679647",
                    "Frank Herbert"
            ),
            LocalDateTime.of(1965, Month.AUGUST, 1, 0, 0).toInstant(ZoneOffset.UTC)
    );

    @BeforeAll
    static void setUp() throws NoSuchFieldException, IllegalAccessException {
        test_storage.put(book_1.getId(), book_1);
        test_storage.put(book_2.getId(), book_2);
        test_storage.put(book_3.getId(), book_3);
        test_storage.put(book_4.getId(), book_4);

        Field storage_field = documentManager.getClass().getDeclaredField("storage");
        storage_field.setAccessible(true);
        storage_field.set(documentManager, test_storage);
    }

    @Test
    void searchByDate_whenTwoDocsReturned_thenCorrect(){
        DocumentManager.SearchRequest searchRequest = new DocumentManager.SearchRequest(null,null,null,
                LocalDateTime.of(1910, Month.JANUARY, 1, 0, 0).toInstant(ZoneOffset.UTC),
                LocalDateTime.of(1940, Month.JANUARY, 1, 0, 0).toInstant(ZoneOffset.UTC));

        List<DocumentManager.Document> actualDocuments = documentManager.search(searchRequest);

        assertThat(actualDocuments).containsExactlyInAnyOrder(book_1, book_3);
    }

    @Test
    void searchByContent_whenTwoDocsReturned_thenCorrect(){
        DocumentManager.SearchRequest searchRequest = new DocumentManager.SearchRequest(null,
                List.of("nightmare and terror", "departure to Arrakis"),
                null, null, null);

        List<DocumentManager.Document> actualDocuments = documentManager.search(searchRequest);

        assertThat(actualDocuments).containsExactlyInAnyOrder(book_3, book_4);
    }

    @Test
    void searchAll_whenThreeDocsReturned_thenCorrect(){
        DocumentManager.SearchRequest searchRequest = new DocumentManager.SearchRequest(null, null, null, null, null);

        List<DocumentManager.Document> actualDocuments = documentManager.search(searchRequest);

        assertThat(actualDocuments).containsExactlyInAnyOrder(book_1, book_2, book_3, book_4);
    }

    @Test
    void findById_whenBookReturned_thenCorrect(){
        Optional<DocumentManager.Document> documentOp = documentManager.findById(book_1.getId());
        assertThat(documentOp).isPresent().hasValue(book_1);
    }

    @Test
    void save_whenBookSaved_thenCorrect(){
        DocumentManager.Document expected_doc = documentManager.save(new DocumentManager.Document("", "title", "content",
                new DocumentManager.Author("d6adfb43-43cc-4f3c-86e9-18bb795821b9", "name"), Instant.now()));

        Optional<DocumentManager.Document> found_doc = documentManager.findById(expected_doc.getId());
        assertThat(found_doc).isPresent().hasValue(expected_doc);
    }
}
