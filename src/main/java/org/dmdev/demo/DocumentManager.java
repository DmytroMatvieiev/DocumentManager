package org.dmdev.demo;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
public class DocumentManager {

    private Map<String, Document> storage = new HashMap<>();

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        if (isNew(document)) {
            String id = UUID.randomUUID().toString();
            while (storage.containsKey(id)) {
                id = UUID.randomUUID().toString();
            }
            document.setId(id);
            storage.put(id, document);
            return document;
        } else {
            storage.put(document.getId(), document);
            return document;
        }
    }

    private boolean isNew(Document document) {
        return document.getId().isEmpty();
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {
        return storage.values().stream()

                .filter(d -> (request.getTitlePrefixes() == null || request.getTitlePrefixes().isEmpty())
                        || !request.getTitlePrefixes().stream().filter(tp -> d.getTitle().startsWith(tp)).toList().isEmpty())

                .filter(d -> (request.getContainsContents() == null || request.getContainsContents().isEmpty())
                        || !request.getContainsContents().stream().filter(c -> d.getContent().contains(c)).toList().isEmpty())

                .filter(d -> (request.getAuthorIds() == null || request.getAuthorIds().isEmpty())
                        || !request.getAuthorIds().stream().filter(a -> d.getAuthor().getId().equals(a)).toList().isEmpty())

                .filter(d -> request.getCreatedFrom() == null
                        || !request.getCreatedFrom().isAfter(d.created))

                .filter(d -> request.getCreatedTo() == null
                        || !request.getCreatedTo().isBefore(d.created))

                .toList();
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {
        return Optional.of(storage.get(id));
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}