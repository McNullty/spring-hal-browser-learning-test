package hr.mladen.cikara.spring.hal.browser.learning.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.mladen.cikara.spring.hal.browser.learning.test.book.Book;
import hr.mladen.cikara.spring.hal.browser.learning.test.book.BookRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
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
        this.bookRepository.deleteAll();

        createTestData();

        this.mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andDo(document("books-list-example",
                       links(halLinks(),
                                linkWithRel("self").description("Canonical link for this resource"),
                                linkWithRel("profile").description("The ALPS profile for this resource"),
                                linkWithRel("search").description("Link for creating custom search on this resource")),
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
    public void noteGetExample() throws Exception {

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
                .andExpect(status().isCreated()).andReturn().getResponse()
                .getHeader("Location");

        this.mockMvc.perform(get(bookLocation))
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
                        responseFields(
                                fieldWithPath("title").description("The title of the book"),
                                fieldWithPath("author").description("Author of the book"),
                                fieldWithPath("blurb").description("Short blurb for a book"),
                                fieldWithPath("pages").description("Number of pages of a book"),
                                subsectionWithPath("_links").description("<<resources-book-links,Links>> to other resources"))));
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
