package hr.mladen.cikara.spring.hal.browser.learning.test;

import hr.mladen.cikara.spring.hal.browser.learning.test.book.Book;
import hr.mladen.cikara.spring.hal.browser.learning.test.book.BookRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.halLinks;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BooksDocumentation extends AbstractDocumentation {

    @Autowired
    private BookRepository bookRepository;

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
