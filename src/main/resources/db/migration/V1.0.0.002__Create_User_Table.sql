
CREATE TABLE oauth2_user (
    user_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(68) NOT NULL,
    UNIQUE(username)
);

GRANT SELECT, INSERT, UPDATE (password), DELETE ON oauth2_user TO haluser;
GRANT USAGE, SELECT ON SEQUENCE oauth2_user_user_id_seq TO haluser;

CREATE TABLE user_authority (
    user_id BIGINT NOT NULL REFERENCES oauth2_user (user_id) ON DELETE CASCADE,
    authority varchar(255) NOT NULL,
    UNIQUE(user_id, authority)
);

GRANT SELECT, INSERT, UPDATE, DELETE ON user_authority TO haluser;