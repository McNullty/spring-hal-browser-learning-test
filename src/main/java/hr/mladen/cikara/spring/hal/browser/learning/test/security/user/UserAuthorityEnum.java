package hr.mladen.cikara.spring.hal.browser.learning.test.security.user;

public enum UserAuthorityEnum {
  ROLE_USER("USER"),
  ROLE_ADMIN("ADMIN"),
  ROLE_USER_MANAGER("USER_MANAGER");

  private final String shortName;

  UserAuthorityEnum(final String shortName) {
    this.shortName = shortName;
  }

  public String getShortName() {
    return this.shortName;
  }
}
