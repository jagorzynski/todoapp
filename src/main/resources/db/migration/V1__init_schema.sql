CREATE SEQUENCE todo_user_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE todo_item_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE TODO_USER (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255),
    birthday TIMESTAMP
);

CREATE TABLE TODO_ITEM (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    todo VARCHAR(255) NOT NULL,
    info VARCHAR(255),
    completion_time TIMESTAMP,
    is_done BOOLEAN DEFAULT FALSE,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES todo_user(id) ON DELETE CASCADE
);
