
CREATE TABLE oauth2_user (
    user_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(68) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    UNIQUE(username)
);

GRANT SELECT, INSERT, UPDATE (password), DELETE ON oauth2_user TO haluser;
GRANT USAGE, SELECT ON SEQUENCE oauth2_user_user_id_seq TO haluser;

CREATE TABLE oauth2_authority (
    authority_id BIGSERIAL PRIMARY KEY,
    authority VARCHAR(255) NOT NULL,
    UNIQUE(authority)
);

GRANT SELECT ON oauth2_authority TO haluser;
GRANT USAGE, SELECT ON SEQUENCE oauth2_authority_authority_id_seq TO haluser;

INSERT INTO oauth2_authority(authority) VALUES ("ROLE_USER");
INSERT INTO oauth2_authority(authority) VALUES ("ROLE_ADMIN");
INSERT INTO oauth2_authority(authority) VALUES ("ROLE_USER_MANAGER");

CREATE TABLE oauth2_user_authority (
    user_id BIGINT NOT NULL REFERENCES oauth2_user (user_id) ON DELETE CASCADE,
    authority_id BIGINT NOT NULL REFERENCES oauth2_authority (authority_id) ON DELETE CASCADE,
    PRIMARY KEY(user_id, authority_id)
);

GRANT SELECT, INSERT, UPDATE, DELETE ON oauth2_user_authority TO haluser;