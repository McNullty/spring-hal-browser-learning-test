
CREATE TABLE book (
    book_id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    author VARCHAR(100) NOT NULL,
    blurb VARCHAR(1000) NOT NULL,
    pages INT
);

GRANT SELECT, INSERT, UPDATE (title, author, blurb, pages), DELETE ON book TO haluser;
GRANT USAGE, SELECT ON SEQUENCE book_book_id_seq TO haluser;