CREATE TABLE aki_rec_music (
    n serial PRIMARY KEY,
    qq BIGINT NOT NULL,
    sub_time TIMESTAMP NOT NULL,
    like_num INT NOT NULL DEFAULT 0,
    playlist_id INT NOT NULL,
    confirmed BOOLEAN NOT NULL DEFAULT FALSE,
    title VARCHAR ( 50 ) NOT NULL,
    artist VARCHAR ( 50 ) NOT NULL,
    platform VARCHAR ( 20 ) NOT NULL,
    music_url VARCHAR ( 250 ),
    jump_url VARCHAR ( 250 ) NOT NULL,
    preview_url VARCHAR ( 250 ) NOT NULL
);


CREATE TABLE aki_playlist (
    n serial PRIMARY KEY,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP
);

CREATE TABLE aki_liked_music (
    n serial PRIMARY KEY,
    qq BIGINT NOT NULL,
    sub_time TIMESTAMP NOT NULL,
    music_id INT NOT NULL,
    playlist_id INT NOT NULL
);

CREATE TABLE aki_user (
    qq BIGINT PRIMARY KEY,
    level VARCHAR ( 10 ) NOT NULL,
    sub_chnotice BOOLEAN NOT NULL DEFAULT FALSE
);
