package hr.mladen.cikara.spring.hal.browser.learning.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.mladen.cikara.spring.hal.browser.learning.test.book.Book;
import hr.mladen.cikara.spring.hal.browser.learning.test.book.BookRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BooksDocumentation extends AbstractDocumentation {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void booksListExample() throws Exception {
        // TODO: think if creating data should be in setup method for this test (BeforeAll)
        createTestData();

        this.mockMvc.perform(get("/books?page=1&size=4&sort=pages,desc&sort=title,asc"))
                .andExpect(status().isOk())
                .andDo(document("books-list-example",
                        links(halLinks(),
                                linkWithRel("self").description("Canonical link for this resource"),
                                linkWithRel("profile").description("The ALPS profile for this resource"),
                                linkWithRel("next").description("Next page with list of books").optional(),
                                linkWithRel("last").description("Last page with list of books").optional(),
                                linkWithRel("first").description("First page with list of books").optional(),
                                linkWithRel("prev").description("Previous page with list of books").optional(),
                                linkWithRel("search").description("Link for creating custom search on this resource")),
                        requestParameters(
                                parameterWithName("page").description("The page to retrieve").optional(),
                                parameterWithName("size").description("Number of entries per page").optional(),
                                parameterWithName("sort").description("Order of entries").optional()),
                        responseFields(
                                subsectionWithPath("_links")
                                        .description("<<resources-books-list_links,Links>> to other resources"),
                                subsectionWithPath("_embedded.books")
                                        .description("An array of <<resources-book, Book resources>>"),
                                subsectionWithPath("page").description("Information about paging data"))
                ));
    }

    @Test
    public void booksSearchExample() throws Exception {
        this.mockMvc.perform(get("/books/search/title-contains?query=1&page=2&size=4&sort=pages,desc&sort=title"))
                .andExpect(status().isOk())
                .andDo(document("books-search-example",
                        links(halLinks(),
                                linkWithRel("self").description("Canonical link for this resource"),
                                linkWithRel("next").description("Next page with list of books").optional(),
                                linkWithRel("last").description("Last page with list of books").optional(),
                                linkWithRel("first").description("First page with list of books").optional(),
                                linkWithRel("prev").description("Previous page with list of books").optional()),
                        requestParameters(
                                parameterWithName("query").description("Query that book title must have"),
                                parameterWithName("page").description("The page to retrieve").optional(),
                                parameterWithName("size").description("Number of entries per page").optional(),
                                parameterWithName("sort").description("Order of entries").optional()),
                        responseFields(
                                subsectionWithPath("_links")
                                        .description("<<resources-books-list_links,Links>> to other resources"),
                                subsectionWithPath("_embedded.books")
                                        .description("An array of <<resources-book, Book resources>>"),
                                subsectionWithPath("page").description("Information about paging data"))
                ));
    }

    @Test
    public void booksCreateExample() throws Exception {

        Map<String, Object> book = new HashMap<String, Object>();
        book.put("title", "Refactoring: Improving the Design of Existing Code");
        book.put("author", "Martin Fowler");
        book.put("blurb", "Any fool can write code that a computer can understand. Good programmers write code that " +
                "humans can understand.");
        book.put("pages", 448);

        this.mockMvc.perform(
                post("/books").contentType(MediaTypes.HAL_JSON).content(
                        this.objectMapper.writeValueAsString(book))).andExpect(
                status().isCreated())
                .andDo(document("books-create-example",
                        requestFields(
                                fieldWithPath("title").description("The title of the book"),
                                fieldWithPath("author").description("Author of the book"),
                                fieldWithPath("blurb").description("Short blurb for a book"),
                                fieldWithPath("pages").description("Number of pages of a book"))));
    }

    @Test
    public void bookGetExample() throws Exception {

        Map<String, Object> book = new HashMap<String, Object>();
        book.put("title", "Refactoring: Improving the Design of Existing Code");
        book.put("author", "Martin Fowler");
        book.put("blurb", "Any fool can write code that a computer can understand. Good programmers write code that " +
                "humans can understand.");
        book.put("pages", 448);

        String bookLocation = this.mockMvc
                .perform(
                        post("/books").contentType(MediaTypes.HAL_JSON).content(
                                this.objectMapper.writeValueAsString(book)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andReturn().getResponse().getHeader("Location");

        String bookId = bookLocation.substring(bookLocation.lastIndexOf("/") + 1);

        this.mockMvc.perform(get("/books/{bookId}",Long.parseLong(bookId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title", is(book.get("title"))))
                .andExpect(jsonPath("author", is(book.get("author"))))
                .andExpect(jsonPath("blurb", is(book.get("blurb"))))
                .andExpect(jsonPath("pages", is(book.get("pages"))))
                .andExpect(jsonPath("_links.self.href", is(bookLocation)))
                .andDo(print())
                .andDo(document("book-get-example",
                        links(
                                linkWithRel("self").description("Canonical link for this <<resources-book,book>>"),
                                linkWithRel("book").description("This <<resources-book,book>>")),
                        pathParameters(parameterWithName("bookId").description("Id of a book")),
                        responseFields(
                                fieldWithPath("title").description("The title of the book"),
                                fieldWithPath("author").description("Author of the book"),
                                fieldWithPath("blurb").description("Short blurb for a book"),
                                fieldWithPath("pages").description("Number of pages of a book"),
                                subsectionWithPath("_links").description("<<resources-book-links,Links>> to other resources"))));
    }

    @Test
    public void bookUpdateExample() throws Exception {
        Map<String, Object> book = new HashMap<String, Object>();
        book.put("title", "Refactoring: Improving the Design of Existing Code");
        book.put("author", "Martin Fowler");
        book.put("pages", 448);

        String bookLocation = this.mockMvc
                .perform(
                        post("/books").contentType(MediaTypes.HAL_JSON).content(
                                this.objectMapper.writeValueAsString(book)))
                .andExpect(status().isCreated()).andDo(print())
                .andReturn().getResponse().getHeader("Location");

        this.mockMvc.perform(get(bookLocation)).andExpect(status().isOk())
                .andExpect(jsonPath("title", is(book.get("title"))))
                .andExpect(jsonPath("author", is(book.get("author"))))
                .andExpect(jsonPath("blurb", is(book.get("blurb"))))
                .andExpect(jsonPath("pages", is(book.get("pages"))))
                .andExpect(jsonPath("_links.self.href", is(bookLocation)));


        Map<String, Object> bookUpdate = new HashMap<String, Object>();
        bookUpdate.put("blurb", "Any fool can write code that a computer can understand. Good programmers write code that " +
                "humans can understand.");

        this.mockMvc.perform(
                patch(bookLocation).contentType(MediaTypes.HAL_JSON).content(
                        this.objectMapper.writeValueAsString(bookUpdate)))
                .andExpect(status().isNoContent())
                .andDo(document("book-update-example",
                        requestFields(
                                fieldWithPath("title").description("The title of the book")
                                        .type(JsonFieldType.STRING).optional(),
                                fieldWithPath("author").description("Author of the book")
                                        .type(JsonFieldType.STRING).optional(),
                                fieldWithPath("blurb").description("Short blurb for a book")
                                        .type(JsonFieldType.STRING).optional(),
                                fieldWithPath("pages").description("Number of pages of a book")
                                        .type(JsonFieldType.NUMBER).optional())));

        this.mockMvc.perform(get(bookLocation)).andExpect(status().isOk())
                .andExpect(jsonPath("title", is(book.get("title"))))
                .andExpect(jsonPath("author", is(book.get("author"))))
                .andExpect(jsonPath("blurb", is(bookUpdate.get("blurb"))))
                .andExpect(jsonPath("pages", is(book.get("pages"))))
                .andExpect(jsonPath("_links.self.href", is(bookLocation)));
    }

    private void createTestData() {
        createBook("Patterns of Enterprise Application Architecture",
                "Martin Fowler", "The practice of enterprise application development has benefited from the " +
                        "emergence of many new enabling technologies. Multi-tiered object-oriented platforms, such as " +
                        "Java and .NET, have become commonplace. These new tools and technologies are capable of " +
                        "building powerful applications, but they are not easily implemented. Common failures in" +
                        " enterprise applications often occur because their developers do not understand the " +
                        "architectural lessons that experienced object developers have learned.", 560);
        createBook("Clean Architecture: A Craftsman's Guide to Software Structure and Design",
                "Robert C. Martin", "By applying universal rules of software architecture, you can " +
                        "dramatically improve developer productivity throughout the life of any software system. Now, " +
                        "building upon the success of his best-selling books Clean Code and The Clean Coder, legendary " +
                        "software craftsman Robert C. Martin (“Uncle Bob”) reveals those rules and helps ", 432);
        createBook("The Software Craftsman: Professionalism, Pragmatism, Pride", "Sandro Mancuso",
                "Despite advanced tools and methodologies, software projects continue to fail. Why? Too many " +
                        "organizations still view software development as just another production line. Too many " +
                        "developers feel that way, too—and they behave accordingly. ", 288);
    }

    private void createBook(String bookTitle, String author, String blurb, int pages) {
        Book book = new Book();
        book.setTitle(bookTitle);
        book.setAuthor(author);
        book.setBlurb(blurb);
        book.setPages(pages);

        bookRepository.save(book);
    }
}
