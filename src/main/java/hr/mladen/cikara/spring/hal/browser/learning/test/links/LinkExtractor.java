package hr.mladen.cikara.spring.hal.browser.learning.test.links;

import java.lang.reflect.InvocationTargetException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LinkExtractor {

  private final static String CONTROLLER_SUFFIX = "Controller";

  private final Class controllerClass;
  private final org.springframework.hateoas.Link hateoasLink;

  public LinkExtractor(final Class controllerClass) {
    this.controllerClass = controllerClass;
    this.hateoasLink = getHateoasLink();
  }

  @SuppressWarnings("unchecked")
  private org.springframework.hateoas.Link getHateoasLink() {
    try {
      return (org.springframework.hateoas.Link) controllerClass.getMethod(
              "getSelfLink", new Class<?>[0]).invoke(new Object[0]);

    } catch (NoSuchMethodException e) {
      log.error("NoSuchMethodException for getSelfLink");

      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      log.error("IllegalAccessException for getSelfLink");

      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      log.error("InvocationTargetException for getSelfLink");

      throw new RuntimeException(e);
    }
  }

  /**
   * Builds Link from data extracted from controller class.
   *
   * @return Link for extracting data
   */
  public Link getLink() {

    return Link.builder()
            .name(getClassName().toLowerCase())
            .href(getHref())
            .templated(getIsTemplated())
            .build();
  }

  private Boolean getIsTemplated() {
    return hateoasLink.isTemplated();
  }

  private String getHref() {
    return hateoasLink.getHref();
  }

  private String getClassName() {
    String classNameWithSuffix = controllerClass.getSimpleName();
    return classNameWithSuffix.substring(0,
            classNameWithSuffix.lastIndexOf(CONTROLLER_SUFFIX));
  }
}
