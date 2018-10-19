package hr.mladen.cikara.spring.hal.browser.learning.test.hal.browser;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponents;

@Controller
public class HalController {

  private static final String BROWSER = "/browser";
  private static final String INDEX = "/index.html";

  /**
   * Redirects to the actual {@code index.html}.
   *
   * @return
   */
  @RequestMapping(value = "/browser", method = RequestMethod.GET)
  public View browser(final HttpServletRequest request) {
    return getRedirectView(request, request.getRequestURI().endsWith("/browser"));
  }

  /**
   * Returns the View to redirect to to access the HAL browser.
   *
   * @param request         must not be {@literal null}.
   * @param browserRelative is relative path or not
   * @return
   */
  private View getRedirectView(final HttpServletRequest request, boolean browserRelative) {

    ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromRequest(request);

    UriComponents components = builder.build();
    String path = components.getPath() == null ? "" : components.getPath();

    if (!browserRelative) {
      builder.path(BROWSER);
    }

    builder.path(INDEX);
    builder.fragment(browserRelative ? path.substring(0, path.lastIndexOf("/browser")) : path);

    return new RedirectView(builder.build().toUriString());
  }

}
