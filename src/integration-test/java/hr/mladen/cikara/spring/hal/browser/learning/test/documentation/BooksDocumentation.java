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
import lombok.AccessLevel;
import lombok.Getter;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

@Getter(AccessLevel.PRIVATE)
@DisplayName("Documentation for /books endpoint")
class BooksDocumentation extends AbstractDocumentation {

  public static final String AUTHOR = "author";
  public static final String AUTHOR_DESCRIPTION = "Author of the book";
  public static final String AUTHOR_VALUE = "Martin Fowler";
  public static final String BLURB = "blurb";
  public static final String BLURB_DESCRIPTION = "Short blurb for a book";
  public static final String BLURB_VALUE = "Any fool can write code that a computer can "
          + "understand. Good programmers write code that humans can understand.";
  public static final String BOOKS_URL = "/books";
  public static final String LINKS = "_links";
  public static final String LINKS_SELF_HREF = "_links.self.href";
  public static final String LOCATION = "Location";
  public static final String PAGE = "page";
  public static final String PAGES = "pages";
  public static final String PAGES_DESCRIPTION = "Number of pages of a book";
  public static final String SELF = "self";
  public static final String TITLE = "title";
  public static final String TITLE_DESCRIPTION = "The title of the book";
  public static final String TITLE_VALUE = "Refactoring: Improving the Design of Existing Code";
  public static final String TO_OTHER_RESOURCES = "to other resources";
  public static final String BOOK_IS_NOT_CREATED_ERROR_MESSAGE = "Book is not created";

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("Documentation for listing all books")
  void booksListExample() throws Exception {

    // GIVEN:
    createTestData();
    String authorization = this.authorizationUtil.getAuthorizationResponse(
            "Alex123", "password");

    //WHEN:
    this.mockMvc.perform(get("/books?page=1&size=4&sort=pages,desc&sort=title,asc")
            .header("Authorization", "Bearer " + authorization))
            // THEN:
            .andExpect(status().isOk())
            .andDo(document("books-list-example",
                    links(halLinks(),
                            linkWithRel(SELF).description("Canonical link for this resource"),
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
                            parameterWithName(PAGE)
                                    .description("The page to retrieve").optional(),
                            parameterWithName("size")
                                    .description("Number of entries per page").optional(),
                            parameterWithName("sort")
                                    .description("Order of entries").optional()),
                    responseFields(
                            subsectionWithPath(LINKS)
                                    .description("<<resources-books-list_links,Links>> "
                                            + TO_OTHER_RESOURCES),
                            subsectionWithPath("_embedded.books")
                                    .description("An array of <<resources-book, Book resources>>"),
                            subsectionWithPath(PAGE).description("Information about paging data"))
            ));
  }

  @Test
  @DisplayName("Documentation for searching books")
  void booksSearchExample() throws Exception {
    // GIVEN:
    String authorization = this.authorizationUtil.getAuthorizationResponse(
            "Alex123", "password");

    // WHEN:
    this.mockMvc.perform(
            get("/books/search/title-contains?query=1&page=2&size=4&sort=pages,desc&sort=title")
                    .header("Authorization", "Bearer " + authorization))
            // THEN:
            .andExpect(status().isOk())
            .andDo(document("books-search-example",
                    links(halLinks(),
                            linkWithRel(SELF)
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
                            parameterWithName(PAGE)
                                    .description("The page to retrieve").optional(),
                            parameterWithName("size")
                                    .description("Number of entries per page").optional(),
                            parameterWithName("sort")
                                    .description("Order of entries").optional()),
                    responseFields(
                            subsectionWithPath(LINKS)
                                    .description("<<resources-books-list_links,Links>> "
                                            + TO_OTHER_RESOURCES),
                            subsectionWithPath("_embedded.books")
                                    .description("An array of <<resources-book, Book resources>>"),
                            subsectionWithPath(PAGE).description("Information about paging data"))
            ));
  }

  @Test
  @DisplayName("Documentation for creating books")
  void booksCreateExample() throws Exception {

    // GIVEN:
    String authorization = this.authorizationUtil.getAuthorizationResponse(
            "Alex123", "password");

    Map<String, Object> book = new HashMap<>();
    book.put(TITLE, TITLE_VALUE);
    book.put(AUTHOR, AUTHOR_VALUE);
    book.put(BLURB, BLURB_VALUE);
    book.put(PAGES, 448);

    // WHEN:
    this.mockMvc.perform(
            post(BOOKS_URL)
                    .header("Authorization", "Bearer " + authorization)
                    .contentType(MediaTypes.HAL_JSON).content(
                    this.objectMapper.writeValueAsString(book)))
            // THEN:
            .andExpect(status().isCreated())
            .andDo(document("books-create-example",
                    requestFields(
                            fieldWithPath(TITLE).description(TITLE_DESCRIPTION),
                            fieldWithPath(AUTHOR).description(AUTHOR_DESCRIPTION),
                            fieldWithPath(BLURB).description(BLURB_DESCRIPTION),
                            fieldWithPath(PAGES).description(PAGES_DESCRIPTION))));
  }

  @Test
  @DisplayName("Documentation for fetching a book")
  void bookGetExample() throws Exception {

    // GIVEN:
    String authorization = this.authorizationUtil.getAuthorizationResponse(
            "Alex123", "password");

    Map<String, Object> book = new HashMap<>();
    book.put(TITLE, TITLE_VALUE);
    book.put(AUTHOR, AUTHOR_VALUE);
    book.put(BLURB, BLURB_VALUE);
    book.put(PAGES, 448);

    String bookLocation = this.mockMvc
            .perform(
                    post(BOOKS_URL)
                            .header("Authorization", "Bearer " + authorization)
                            .contentType(MediaTypes.HAL_JSON).content(
                            this.objectMapper.writeValueAsString(book)))
            .andExpect(status().isCreated())
            .andDo(print())
            .andReturn().getResponse().getHeader(LOCATION);

    if (bookLocation == null) {
      Assert.fail(BOOK_IS_NOT_CREATED_ERROR_MESSAGE);
    }

    String bookId = getBookIdFromLocation(bookLocation);

    // WHEN:
    this.mockMvc.perform(get("/books/{bookId}", Long.parseLong(bookId))
            .header("Authorization", "Bearer " + authorization))
            // THEN:
            .andExpect(status().isOk())
            .andExpect(jsonPath(TITLE, is(book.get(TITLE))))
            .andExpect(jsonPath(AUTHOR, is(book.get(AUTHOR))))
            .andExpect(jsonPath(BLURB, is(book.get(BLURB))))
            .andExpect(jsonPath(PAGES, is(book.get(PAGES))))
            .andExpect(jsonPath(LINKS_SELF_HREF, is(bookLocation)))
            .andDo(print())
            .andDo(document("book-get-example",
                    links(
                            linkWithRel(SELF)
                                    .description("Canonical link for this <<resources-book,book>>")
                    ),
                    pathParameters(parameterWithName("bookId").description("Id of a book")),
                    responseFields(
                            fieldWithPath(TITLE).description(TITLE_DESCRIPTION),
                            fieldWithPath(AUTHOR).description(AUTHOR_DESCRIPTION),
                            fieldWithPath(BLURB).description(BLURB_DESCRIPTION),
                            fieldWithPath(PAGES).description(PAGES_DESCRIPTION),
                            subsectionWithPath(LINKS)
                                    .description("<<resources-book-links,Links>> "
                                            + TO_OTHER_RESOURCES))));
  }

  @Test
  @DisplayName("Documentation for updating a book")
  void bookUpdateExample() throws Exception {

    // GIVEN:
    String authorization = this.authorizationUtil.getAuthorizationResponse(
            "Alex123", "password");

    Map<String, Object> book = new HashMap<>();
    book.put(TITLE, TITLE_VALUE);
    book.put(AUTHOR, AUTHOR_VALUE);
    book.put(PAGES, 448);

    String bookLocation = this.mockMvc
            .perform(
                    post(BOOKS_URL)
                            .header("Authorization", "Bearer " + authorization)
                            .contentType(MediaTypes.HAL_JSON).content(
                            this.objectMapper.writeValueAsString(book)))
            .andExpect(status().isCreated()).andDo(print())
            .andReturn().getResponse().getHeader(LOCATION);

    if (bookLocation == null) {
      Assert.fail(BOOK_IS_NOT_CREATED_ERROR_MESSAGE);
    }

    this.mockMvc.perform(get(bookLocation)
            .header("Authorization", "Bearer " + authorization))
            .andExpect(status().isOk())
            .andExpect(jsonPath(TITLE, is(book.get(TITLE))))
            .andExpect(jsonPath(AUTHOR, is(book.get(AUTHOR))))
            .andExpect(jsonPath(BLURB, is(book.get(BLURB))))
            .andExpect(jsonPath(PAGES, is(book.get(PAGES))))
            .andExpect(jsonPath(LINKS_SELF_HREF, is(bookLocation)));


    Map<String, Object> bookUpdate = new HashMap<>();
    bookUpdate.put(BLURB, BLURB_VALUE);

    this.mockMvc.perform(
            patch(bookLocation)
                    .header("Authorization", "Bearer " + authorization)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.ALL_VALUE).content(
                    this.objectMapper.writeValueAsString(bookUpdate)))
            .andExpect(status().isOk())
            .andExpect(jsonPath(TITLE, is(book.get(TITLE))))
            .andExpect(jsonPath(AUTHOR, is(book.get(AUTHOR))))
            .andExpect(jsonPath(BLURB, is(bookUpdate.get(BLURB))))
            .andExpect(jsonPath(PAGES, is(book.get(PAGES))))
            .andExpect(jsonPath(LINKS_SELF_HREF, is(bookLocation)))
            .andDo(document("book-update-example",
                    requestFields(
                            fieldWithPath(TITLE).description(TITLE_DESCRIPTION)
                                    .type(JsonFieldType.STRING).optional(),
                            fieldWithPath(AUTHOR).description(AUTHOR_DESCRIPTION)
                                    .type(JsonFieldType.STRING).optional(),
                            fieldWithPath(BLURB).description(BLURB_DESCRIPTION)
                                    .type(JsonFieldType.STRING).optional(),
                            fieldWithPath(PAGES).description(PAGES_DESCRIPTION)
                                    .type(JsonFieldType.NUMBER).optional()),
                    responseFields(
                            fieldWithPath(TITLE).description(TITLE_DESCRIPTION),
                            fieldWithPath(AUTHOR).description(AUTHOR_DESCRIPTION),
                            fieldWithPath(BLURB).description(BLURB_DESCRIPTION),
                            fieldWithPath(PAGES).description(PAGES_DESCRIPTION),
                            subsectionWithPath(LINKS)
                                    .description("<<resources-book-links,Links>> "
                                            + TO_OTHER_RESOURCES))));

    // WHEN:
    this.mockMvc.perform(get(bookLocation)
            .header("Authorization", "Bearer " + authorization))
            // THEN:
            .andExpect(status().isOk())
            .andExpect(jsonPath(TITLE, is(book.get(TITLE))))
            .andExpect(jsonPath(AUTHOR, is(book.get(AUTHOR))))
            .andExpect(jsonPath(BLURB, is(bookUpdate.get(BLURB))))
            .andExpect(jsonPath(PAGES, is(book.get(PAGES))))
            .andExpect(jsonPath(LINKS_SELF_HREF, is(bookLocation)));
  }

  @Test
  @DisplayName("Documentation for replacing a book")
  void bookReplaceExample() throws Exception {

    String authorization = this.authorizationUtil.getAuthorizationResponse(
            "Alex123", "password");

    Map<String, Object> book = new HashMap<>();
    book.put(TITLE, TITLE_VALUE);
    book.put(AUTHOR, AUTHOR_VALUE);
    book.put(PAGES, 448);

    String bookLocation = this.mockMvc
            .perform(
                    post(BOOKS_URL)
                            .header("Authorization", "Bearer " + authorization)
                            .contentType(MediaTypes.HAL_JSON)
                            .accept(MediaType.ALL_VALUE)
                            .content(
                            this.objectMapper.writeValueAsString(book)))
            .andExpect(status().isCreated()).andDo(print())
            .andReturn().getResponse().getHeader(LOCATION);

    if (bookLocation == null) {
      Assert.fail(BOOK_IS_NOT_CREATED_ERROR_MESSAGE);
    }

    this.mockMvc.perform(get(bookLocation)
            .header("Authorization", "Bearer " + authorization))
            .andExpect(status().isOk())
            .andExpect(jsonPath(TITLE, is(book.get(TITLE))))
            .andExpect(jsonPath(AUTHOR, is(book.get(AUTHOR))))
            .andExpect(jsonPath(BLURB, is(book.get(BLURB))))
            .andExpect(jsonPath(PAGES, is(book.get(PAGES))))
            .andExpect(jsonPath(LINKS_SELF_HREF, is(bookLocation)));


    Map<String, Object> bookReplace = new HashMap<>();
    bookReplace.put(TITLE, TITLE_VALUE);
    bookReplace.put(AUTHOR, AUTHOR_VALUE);
    bookReplace.put(BLURB, BLURB_VALUE);
    bookReplace.put(PAGES, 448);

    this.mockMvc.perform(
            put(bookLocation).header("Authorization", "Bearer " + authorization)
                    .contentType(MediaType.APPLICATION_JSON_VALUE).content(
                    this.objectMapper.writeValueAsString(bookReplace)))
            .andExpect(status().isOk())
            .andExpect(jsonPath(TITLE, is(bookReplace.get(TITLE))))
            .andExpect(jsonPath(AUTHOR, is(bookReplace.get(AUTHOR))))
            .andExpect(jsonPath(BLURB, is(bookReplace.get(BLURB))))
            .andExpect(jsonPath(PAGES, is(bookReplace.get(PAGES))))
            .andExpect(jsonPath(LINKS_SELF_HREF, is(bookLocation)))
            .andDo(document("book-replace-example",
                    requestFields(
                            fieldWithPath(TITLE).description(TITLE_DESCRIPTION)
                                    .type(JsonFieldType.STRING).optional(),
                            fieldWithPath(AUTHOR).description(AUTHOR_DESCRIPTION)
                                    .type(JsonFieldType.STRING).optional(),
                            fieldWithPath(BLURB).description(BLURB_DESCRIPTION)
                                    .type(JsonFieldType.STRING).optional(),
                            fieldWithPath(PAGES).description(PAGES_DESCRIPTION)
                                    .type(JsonFieldType.NUMBER).optional()),
                    responseFields(
                            fieldWithPath(TITLE).description(TITLE_DESCRIPTION),
                            fieldWithPath(AUTHOR).description(AUTHOR_DESCRIPTION),
                            fieldWithPath(BLURB).description(BLURB_DESCRIPTION),
                            fieldWithPath(PAGES).description(PAGES_DESCRIPTION),
                            subsectionWithPath(LINKS)
                                    .description("<<resources-book-links,Links>> "
                                            + TO_OTHER_RESOURCES))));
  }

  @Test
  @DisplayName("Documentation for deleting a book")
  void bookDeleteExample() throws Exception {

    // GIVEN:
    String authorization = this.authorizationUtil.getAuthorizationResponse(
            "Alex123", "password");

    Map<String, Object> book = new HashMap<>();
    book.put(TITLE, TITLE_VALUE);
    book.put(AUTHOR, AUTHOR_VALUE);
    book.put(PAGES, 448);

    String bookLocation = this.mockMvc
            .perform(
                    post(BOOKS_URL)
                            .header("Authorization", "Bearer " + authorization)
                            .contentType(MediaTypes.HAL_JSON)
                            .accept(MediaType.ALL_VALUE)
                            .content(this.objectMapper.writeValueAsString(book)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getHeader(LOCATION);

    if (bookLocation == null) {
      Assert.fail(BOOK_IS_NOT_CREATED_ERROR_MESSAGE);
    }

    this.mockMvc.perform(get(bookLocation)
            .header("Authorization", "Bearer " + authorization))
            .andExpect(status().isOk())
            .andExpect(jsonPath(TITLE, is(book.get(TITLE))))
            .andExpect(jsonPath(AUTHOR, is(book.get(AUTHOR))))
            .andExpect(jsonPath(BLURB, is(book.get(BLURB))))
            .andExpect(jsonPath(PAGES, is(book.get(PAGES))))
            .andExpect(jsonPath(LINKS_SELF_HREF, is(bookLocation)));

    String bookId = getBookIdFromLocation(bookLocation);


    // WHEN:
    this.mockMvc.perform(
            delete("/books/{bookId}", Long.parseLong(bookId))
                    .header("Authorization", "Bearer " + authorization))
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
            AUTHOR_VALUE, "The practice of enterprise application development "
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
