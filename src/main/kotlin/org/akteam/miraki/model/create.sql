CREATE TABLE aki_rec_music(
    n serial PRIMARY KEY,

    qq bigint NOT NULL,
    sub_time timestamp NOT NULL,

    like_num int NOT NULL default 0,
    playlist_id int NOT NULL,
    confirmed boolean NOT NULL default false,

    title varchar(50) NOT NULL,
    artist varchar(50) NOT NULL,
    platform varchar(20) NOT NULL,

    music_url varchar(250),
    jump_url varchar(250) NOT NULL,
    preview_url varchar(250) NOT NULL
);

CREATE TABLE aki_playlist(
    n serial PRIMARY KEY,
    start_time timestamp NOT NULL,
    end_time timestamp
);

CREATE TABLE aki_user(
    qq bigint PRIMARY KEY,
    level varchar(10) NOT NULL,
    sub_chnotice boolean NOT NULL DEFAULT false
);
