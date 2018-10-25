package hr.mladen.cikara.spring.hal.browser.learning.test.documentation;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.halLinks;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.mladen.cikara.spring.hal.browser.learning.test.book.Book;
import hr.mladen.cikara.spring.hal.browser.learning.test.book.BookRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.restdocs.payload.JsonFieldType;

@DisplayName("Documentation for /books endpoint")
class BooksDocumentation extends AbstractDocumentation {

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("Documentation for listing all books")
  void booksListExample() throws Exception {

    // GIVEN:
    // TODO: think if creating data should be in setup method for this test (BeforeAll)
    createTestData();

    //WHEN:
    this.mockMvc.perform(get("/books?page=1&size=4&sort=pages,desc&sort=title,asc"))
            // THEN:
            .andExpect(status().isOk())
            .andDo(document("books-list-example",
                    links(halLinks(),
                            linkWithRel("self").description("Canonical link for this resource"),
                            linkWithRel("next")
                                    .description("Next page with list of books").optional(),
                            linkWithRel("last")
                                    .description("Last page with list of books").optional(),
                            linkWithRel("first")
                                    .description("First page with list of books").optional(),
                            linkWithRel("prev")
                                    .description("Previous page with list of books").optional(),
                            linkWithRel("search")
                                    .description(
                                            "Link for creating custom search on this resource")),
                    requestParameters(
                            parameterWithName("page")
                                    .description("The page to retrieve").optional(),
                            parameterWithName("size")
                                    .description("Number of entries per page").optional(),
                            parameterWithName("sort")
                                    .description("Order of entries").optional()),
                    responseFields(
                            subsectionWithPath("_links")
                                    .description("<<resources-books-list_links,Links>> "
                                            + "to other resources"),
                            subsectionWithPath("_embedded.books")
                                    .description("An array of <<resources-book, Book resources>>"),
                            subsectionWithPath("page").description("Information about paging data"))
            ));
  }

  @Test
  @DisplayName("Documentation for searching books")
  void booksSearchExample() throws Exception {
    // GIVEN:
    // WHEN:
    this.mockMvc.perform(
            get("/books/search/title-contains?query=1&page=2&size=4&sort=pages,desc&sort=title"))
            // THEN:
            .andExpect(status().isOk())
            .andDo(document("books-search-example",
                    links(halLinks(),
                            linkWithRel("self")
                                    .description("Canonical link for this resource"),
                            linkWithRel("next")
                                    .description("Next page with list of books").optional(),
                            linkWithRel("last")
                                    .description("Last page with list of books").optional(),
                            linkWithRel("first")
                                    .description("First page with list of books").optional(),
                            linkWithRel("prev")
                                    .description("Previous page with list of books").optional(),
                            linkWithRel("search")
                                    .description(
                                            "Link for creating custom search on this resource")),
                    requestParameters(
                            parameterWithName("query")
                                    .description("Query that book title must have"),
                            parameterWithName("page")
                                    .description("The page to retrieve").optional(),
                            parameterWithName("size")
                                    .description("Number of entries per page").optional(),
                            parameterWithName("sort")
                                    .description("Order of entries").optional()),
                    responseFields(
                            subsectionWithPath("_links")
                                    .description("<<resources-books-list_links,Links>> "
                                            + "to other resources"),
                            subsectionWithPath("_embedded.books")
                                    .description("An array of <<resources-book, Book resources>>"),
                            subsectionWithPath("page").description("Information about paging data"))
            ));
  }

  @Test
  @DisplayName("Documentation for creating books")
  void booksCreateExample() throws Exception {

    // GIVEN:

    Map<String, Object> book = new HashMap<>();
    book.put("title", "Refactoring: Improving the Design of Existing Code");
    book.put("author", "Martin Fowler");
    book.put("blurb", "Any fool can write code that a computer can understand. "
            + "Good programmers write code that humans can understand.");
    book.put("pages", 448);

    // WHEN:
    this.mockMvc.perform(
            post("/books").contentType(MediaTypes.HAL_JSON).content(
                    this.objectMapper.writeValueAsString(book)))
            // THEN:
            .andExpect(status().isCreated())
            .andDo(document("books-create-example",
                    requestFields(
                            fieldWithPath("title").description("The title of the book"),
                            fieldWithPath("author").description("Author of the book"),
                            fieldWithPath("blurb").description("Short blurb for a book"),
                            fieldWithPath("pages").description("Number of pages of a book"))));
  }

  @Test
  @DisplayName("Documentation for fetching a book")
  void bookGetExample() throws Exception {

    // GIVEN:
    Map<String, Object> book = new HashMap<>();
    book.put("title", "Refactoring: Improving the Design of Existing Code");
    book.put("author", "Martin Fowler");
    book.put("blurb", "Any fool can write code that a computer can understand. "
            + "Good programmers write code that  humans can understand.");
    book.put("pages", 448);

    String bookLocation = this.mockMvc
            .perform(
                    post("/books").contentType(MediaTypes.HAL_JSON).content(
                            this.objectMapper.writeValueAsString(book)))
            .andExpect(status().isCreated())
            .andDo(print())
            .andReturn().getResponse().getHeader("Location");

    if (bookLocation == null) {
      Assert.fail("Book is not create");
    }

    String bookId = getBookIdFromLocation(bookLocation);

    // WHEN:
    this.mockMvc.perform(get("/books/{bookId}", Long.parseLong(bookId)))
            // THEN:
            .andExpect(status().isOk())
            .andExpect(jsonPath("title", is(book.get("title"))))
            .andExpect(jsonPath("author", is(book.get("author"))))
            .andExpect(jsonPath("blurb", is(book.get("blurb"))))
            .andExpect(jsonPath("pages", is(book.get("pages"))))
            .andExpect(jsonPath("_links.self.href", is(bookLocation)))
            .andDo(print())
            .andDo(document("book-get-example",
                    links(
                            linkWithRel("self")
                                    .description("Canonical link for this <<resources-book,book>>")
                    ),
                    pathParameters(parameterWithName("bookId").description("Id of a book")),
                    responseFields(
                            fieldWithPath("title").description("The title of the book"),
                            fieldWithPath("author").description("Author of the book"),
                            fieldWithPath("blurb").description("Short blurb for a book"),
                            fieldWithPath("pages").description("Number of pages of a book"),
                            subsectionWithPath("_links")
                                    .description("<<resources-book-links,Links>> "
                                            + "to other resources"))));
  }

  @Test
  @DisplayName("Documentation for updating a book")
  void bookUpdateExample() throws Exception {

    // GIVEN:

    Map<String, Object> book = new HashMap<>();
    book.put("title", "Refactoring: Improving the Design of Existing Code");
    book.put("author", "Martin Fowler");
    book.put("pages", 448);

    String bookLocation = this.mockMvc
            .perform(
                    post("/books").contentType(MediaTypes.HAL_JSON).content(
                            this.objectMapper.writeValueAsString(book)))
            .andExpect(status().isCreated()).andDo(print())
            .andReturn().getResponse().getHeader("Location");

    if (bookLocation == null) {
      Assert.fail("Book is not create");
    }

    this.mockMvc.perform(get(bookLocation)).andExpect(status().isOk())
            .andExpect(jsonPath("title", is(book.get("title"))))
            .andExpect(jsonPath("author", is(book.get("author"))))
            .andExpect(jsonPath("blurb", is(book.get("blurb"))))
            .andExpect(jsonPath("pages", is(book.get("pages"))))
            .andExpect(jsonPath("_links.self.href", is(bookLocation)));


    Map<String, Object> bookUpdate = new HashMap<>();
    bookUpdate.put("blurb", "Any fool can write code that a computer can understand. "
            + "Good programmers write code that humans can understand.");

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

    // WHEN:
    this.mockMvc.perform(get(bookLocation))
            // THEN:
            .andExpect(status().isOk())
            .andExpect(jsonPath("title", is(book.get("title"))))
            .andExpect(jsonPath("author", is(book.get("author"))))
            .andExpect(jsonPath("blurb", is(bookUpdate.get("blurb"))))
            .andExpect(jsonPath("pages", is(book.get("pages"))))
            .andExpect(jsonPath("_links.self.href", is(bookLocation)));
  }

  @Test
  @DisplayName("Documentation for replacing a book")
  void bookReplaceExample() throws Exception {

    // GIVEN:

    Map<String, Object> book = new HashMap<>();
    book.put("title", "Refactoring: Improving the Design of Existing Code");
    book.put("author", "Martin Fowler");
    book.put("pages", 448);

    String bookLocation = this.mockMvc
            .perform(
                    post("/books").contentType(MediaTypes.HAL_JSON).content(
                            this.objectMapper.writeValueAsString(book)))
            .andExpect(status().isCreated()).andDo(print())
            .andReturn().getResponse().getHeader("Location");

    if (bookLocation == null) {
      Assert.fail("Book is not create");
    }

    this.mockMvc.perform(get(bookLocation)).andExpect(status().isOk())
            .andExpect(jsonPath("title", is(book.get("title"))))
            .andExpect(jsonPath("author", is(book.get("author"))))
            .andExpect(jsonPath("blurb", is(book.get("blurb"))))
            .andExpect(jsonPath("pages", is(book.get("pages"))))
            .andExpect(jsonPath("_links.self.href", is(bookLocation)));


    Map<String, Object> bookReplace = new HashMap<>();
    bookReplace.put("title", "Refactoring: Improving the Design of Existing Code");
    bookReplace.put("author", "Martin Fowler");
    bookReplace.put("blurb", "Any fool can write code that a computer can understand. "
            + "Good programmers write code that humans can understand.");
    bookReplace.put("pages", 448);

    this.mockMvc.perform(
            put(bookLocation).contentType(MediaTypes.HAL_JSON).content(
                    this.objectMapper.writeValueAsString(bookReplace)))
            .andExpect(status().isNoContent())
            .andDo(document("book-replace-example",
                    requestFields(
                            fieldWithPath("title").description("The title of the book")
                                    .type(JsonFieldType.STRING).optional(),
                            fieldWithPath("author").description("Author of the book")
                                    .type(JsonFieldType.STRING).optional(),
                            fieldWithPath("blurb").description("Short blurb for a book")
                                    .type(JsonFieldType.STRING).optional(),
                            fieldWithPath("pages").description("Number of pages of a book")
                                    .type(JsonFieldType.NUMBER).optional())));

    // WHEN:
    this.mockMvc.perform(get(bookLocation))
            // THEN:
            .andExpect(status().isOk())
            .andExpect(jsonPath("title", is(bookReplace.get("title"))))
            .andExpect(jsonPath("author", is(bookReplace.get("author"))))
            .andExpect(jsonPath("blurb", is(bookReplace.get("blurb"))))
            .andExpect(jsonPath("pages", is(bookReplace.get("pages"))))
            .andExpect(jsonPath("_links.self.href", is(bookLocation)));
  }


  @Test
  @DisplayName("Documentation for deleting a book")
  void bookDeleteExample() throws Exception {

    // GIVEN:

    Map<String, Object> book = new HashMap<>();
    book.put("title", "Refactoring: Improving the Design of Existing Code");
    book.put("author", "Martin Fowler");
    book.put("pages", 448);

    String bookLocation = this.mockMvc
            .perform(
                    post("/books").contentType(MediaTypes.HAL_JSON).content(
                            this.objectMapper.writeValueAsString(book)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getHeader("Location");

    if (bookLocation == null) {
      Assert.fail("Book is not create");
    }

    this.mockMvc.perform(get(bookLocation)).andExpect(status().isOk())
            .andExpect(jsonPath("title", is(book.get("title"))))
            .andExpect(jsonPath("author", is(book.get("author"))))
            .andExpect(jsonPath("blurb", is(book.get("blurb"))))
            .andExpect(jsonPath("pages", is(book.get("pages"))))
            .andExpect(jsonPath("_links.self.href", is(bookLocation)));

    String bookId = getBookIdFromLocation(bookLocation);


    // WHEN:
    this.mockMvc.perform(
            delete("/books/{bookId}", Long.parseLong(bookId)))
            // THEN:
            .andExpect(status().isNoContent())
            .andDo(print())
            .andDo(document("book-delete-example",
                    pathParameters(parameterWithName("bookId").description("Id of a book"))));

  }

  private String getBookIdFromLocation(@NotNull String bookLocation) {
    Objects.requireNonNull(bookLocation);

    return bookLocation.substring(bookLocation.lastIndexOf("/") + 1);
  }

  private void createTestData() {
    createBook("Patterns of Enterprise Application Architecture",
            "Martin Fowler", "The practice of enterprise application development "
                    + "has benefited from the emergence of many new enabling technologies. "
                    + "Multi-tiered object-oriented platforms, such as Java and .NET, have "
                    + "become commonplace. These new tools and technologies are capable of "
                    + "building powerful applications, but they are not easily implemented. "
                    + "Common failures in enterprise applications often occur because their "
                    + "developers do not understand the architectural lessons that experienced "
                    + "object developers have learned.", 560);
    createBook("Clean Architecture: A Craftsman's Guide to Software Structure and Design",
            "Robert C. Martin", "By applying universal rules of software "
                    + "architecture, you can dramatically improve developer productivity "
                    + "throughout the life of any software system. Now, building upon the "
                    + "success of his best-selling books Clean Code and The Clean Coder, "
                    + "legendary software craftsman Robert C. Martin (“Uncle Bob”) reveals "
                    + "those rules and helps ", 432);
    createBook("The Software Craftsman: Professionalism, Pragmatism, Pride", "Sandro Mancuso",
            "Despite advanced tools and methodologies, software projects continue to fail. "
                    + "Why? Too many organizations still view software development as just "
                    + "another production line. Too many developers feel that way, too—and they "
                    + "behave accordingly. ", 288);
  }

  private void createBook(String bookTitle, String author, String blurb, int pages) {
    Book book = Book.builder()
            .author(author)
            .title(bookTitle)
            .blurb(blurb)
            .pages(pages)
            .build();

    bookRepository.save(book);
  }
}
