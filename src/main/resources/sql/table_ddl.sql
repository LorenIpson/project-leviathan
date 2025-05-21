CREATE DATABASE leviathan COLLATE Chinese_Taiwan_Stroke_CI_AS;

-- ==========================================================================

USE leviathan

-- =============== FORUM ============================================================

CREATE TABLE categories
(
    category_id INT PRIMARY KEY NOT NULL IDENTITY (1, 1),
    name        NVARCHAR(255)   NOT NULL UNIQUE
);

CREATE TABLE forums
(
    forum_id   INT PRIMARY KEY NOT NULL IDENTITY (1, 1),
    name       NVARCHAR(255)   NOT NULL UNIQUE,
    cover      VARBINARY       NULL,
    is_active  BIT             NOT NULL DEFAULT 1,
    is_visible BIT             NOT NULL DEFAULT 1,
    created_at DATETIME2       NOT NULL DEFAULT SYSUTCDATETIME()
);

CREATE TABLE categories_forums
(
    category_id INT NOT NULL,
    forum_id    INT NOT NULL,
    PRIMARY KEY (category_id, forum_id),
    FOREIGN KEY (category_id) REFERENCES categories (category_id) ON DELETE CASCADE,
    FOREIGN KEY (forum_id) REFERENCES forums (forum_id) ON DELETE CASCADE
);

CREATE TABLE forums_moderators
(
    forum_id  INT NOT NULL,
    member_id INT NOT NULL,
    PRIMARY KEY (forum_id, member_id),
    FOREIGN KEY (forum_id) REFERENCES forums (forum_id),
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE forums_bans
(
    forum_id   INT           NOT NULL,
    member_id  INT           NOT NULL,
    banned_by  INT           NULL,
    ban_reason NVARCHAR(500) NULL,
    banned_til DATETIME2     NULL,
    PRIMARY KEY (forum_id, member_id),
    FOREIGN KEY (forum_id) REFERENCES forums (forum_id),
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE,
    FOREIGN KEY (banned_by) REFERENCES member (id)
);

CREATE TABLE forum_tags
(
    tag_id   BIGINT PRIMARY KEY NOT NULL IDENTITY (1, 1),
    forum_id INT                NOT NULL,
    name     NVARCHAR(255)      NOT NULL,
    FOREIGN KEY (forum_id) REFERENCES forums (forum_id) ON DELETE CASCADE
)

CREATE TABLE forum_flairs
(
    flair_id  BIGINT PRIMARY KEY NOT NULL IDENTITY (1, 1),
    forum_id  INT                NOT NULL,
    name      NVARCHAR(255)      NOT NULL,
    member_id INT                NULL,
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE,
    FOREIGN KEY (forum_id) REFERENCES forums (forum_id) ON DELETE CASCADE
)

CREATE TABLE posts
(
    post_id        BIGINT PRIMARY KEY NOT NULL IDENTITY (1, 1),
    forum_id       INT                NOT NULL,
    member_id      INT                NOT NULL DEFAULT 1,
    title          NVARCHAR(255)      NOT NULL,
    content        NVARCHAR(MAX)      NOT NULL,
    created_at     DATETIME2          NOT NULL DEFAULT SYSUTCDATETIME(),
    is_recommended BIT                NOT NULL DEFAULT 0,
    is_locked      BIT                NOT NULL DEFAULT 0,
    is_deleted     BIT                NOT NULL DEFAULT 0,
    locked_by      INT                NULL,
    deleted_by     INT                NULL,
    FOREIGN KEY (forum_id) REFERENCES forums (forum_id),
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE SET DEFAULT,
    FOREIGN KEY (locked_by) REFERENCES member (id),
    FOREIGN KEY (deleted_by) REFERENCES member (id),
    CHECK (is_locked = 1 OR locked_by IS NULL ),
    CHECK (is_deleted = 1 OR deleted_by IS NULL)
);

CREATE TABLE posts_tags
(
    post_id BIGINT NOT NULL,
    tag_id  BIGINT NOT NULL,
    PRIMARY KEY (post_id, tag_id),
    FOREIGN KEY (post_id) REFERENCES posts (post_id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES forum_tags (tag_id) ON DELETE CASCADE
);

CREATE TABLE post_images
(
    image_id    INT PRIMARY KEY NOT NULL IDENTITY (1, 1),
    post_id     BIGINT          NOT NULL,
    image_url   NVARCHAR(500)   NOT NULL,
    deletehash  NVARCHAR(100)   NULL,
    uploaded_at DATETIME2       NOT NULL DEFAULT SYSUTCDATETIME(),
    FOREIGN KEY (post_id) REFERENCES posts (post_id)
);

CREATE TABLE forum_posts_pins
(
    pinned_id INT PRIMARY KEY NOT NULL IDENTITY (1, 1),
    forum_id  INT             NOT NULL,
    post_id   BIGINT          NOT NULL,
    pinned_at DATETIME2       NOT NULL DEFAULT SYSUTCDATETIME(),
    FOREIGN KEY (forum_id) REFERENCES forums (forum_id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES posts (post_id) ON DELETE CASCADE
);

CREATE TABLE posts_votes
(
    post_id   BIGINT    NOT NULL,
    member_id INT       NOT NULL,
    vote      INT       NOT NULL CHECK (vote IN (-1, 1, 0)),
    voted_at  DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    PRIMARY KEY (post_id, member_id),
    FOREIGN KEY (post_id) REFERENCES posts (post_id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE posts_views
(
    post_id   BIGINT    NOT NULL,
    member_id INT       NOT NULL,
    viewed_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    PRIMARY KEY (post_id, member_id),
    FOREIGN KEY (post_id) REFERENCES posts (post_id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE comments
(
    comment_id        BIGINT PRIMARY KEY NOT NULL IDENTITY (1, 1),
    post_id           BIGINT             NOT NULL,
    parent_comment_id BIGINT             NULL,
    member_id         INT                NOT NULL DEFAULT 1,
    content           NVARCHAR(MAX)      NOT NULL,
    created_at        DATETIME2          NOT NULL DEFAULT SYSUTCDATETIME(),
    is_locked         BIT                NOT NULL DEFAULT 0,
    locked_by         INT                NULL,
    is_deleted        BIT                NOT NULL DEFAULT 0,
    deleted_by        INT                NULL,
    FOREIGN KEY (post_id) REFERENCES posts (post_id),
    FOREIGN KEY (parent_comment_id) REFERENCES comments (comment_id),
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE SET DEFAULT,
    FOREIGN KEY (locked_by) REFERENCES member (id),
    FOREIGN KEY (deleted_by) REFERENCES member (id),
    CHECK (is_locked = 1 OR locked_by IS NULL ),
    CHECK (is_deleted = 1 OR deleted_by IS NULL)
);

CREATE TABLE comments_votes
(
    comment_id BIGINT    NOT NULL,
    member_id  INT       NOT NULL,
    vote       INT       NOT NULL CHECK (vote IN (-1, 1, 0)),
    created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    PRIMARY KEY (comment_id, member_id),
    FOREIGN KEY (comment_id) REFERENCES comments (comment_id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE comment_images
(
    image_id    INT PRIMARY KEY NOT NULL IDENTITY (1, 1),
    comment_id  BIGINT          NOT NULL,
    image_url   NVARCHAR(500)   NOT NULL,
    deletehash  NVARCHAR(100)   NULL,
    uploaded_at DATETIME2       NOT NULL DEFAULT SYSUTCDATETIME(),
    FOREIGN KEY (comment_id) REFERENCES comments (comment_id)
);

-- == 修改 posts 資料表（2025,03,18,13:00）==
alter table posts
    add is_edited BIT not null default 0;
alter table posts
    add latest_comment_at DATETIME2;
alter table comments
    add is_edited BIT not null default 0;
alter table comments
    add image_url VARCHAR(255);

-- == 新增 forum_detail 資料表（2025,03,18,13:00）==
create table forum_detail
(
    forum_id    INT PRIMARY KEY NOT NULL,
    cover       VARBINARY       NULL,
    description NVARCHAR(max)   NULL,
    FOREIGN KEY (forum_id) REFERENCES forums (forum_id) ON DELETE CASCADE
);

-- =============== [ 2025,03,28,13:00] ============================================================
-- == 修改 posts 資料表
alter table posts
    add edited_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME();
-- 修改 posts 資料表 ==

-- == 修改 comments 資料表
alter table comments
    add edited_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME();

-- == 修改 comments 資料表
alter table forums
    alter column cover varbinary(max) null

-- == 修改 forums 資料表
alter table forums
    add popularity_score BIGINT DEFAULT 0;

-- == 修改 post_images 資料表
alter table post_images
    add member_id INT NULL;
alter table post_images
    alter column post_id bigint null;
alter table post_images
    add is_temp BIT NOT NULL DEFAULT 1;
alter table post_images
    add CONSTRAINT fk_post_images_member
        FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE SET NULL;

-- == 修改 comment_images 資料表
alter table comment_images
    add member_id INT NULL;
alter table comment_images
    alter column comment_id bigint null;
alter table comment_images
    add is_temp BIT NOT NULL DEFAULT 1;
alter table comment_images
    add CONSTRAINT fk_comment_images_member
        FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE SET NULL;

-- == 修改 comments 資料表
alter table comments
    drop column image_url;

-- == 修改 forum_tags 資料表
alter table forum_tags
    add is_active BIT NOT NULL DEFAULT 1
alter table forum_tags
    add color NVARCHAR(20) NULL;

-- == 修改 forum_detail 資料表
alter table forum_detail
    alter column cover varbinary(max) null

-- == 修改 posts 資料表
alter table posts
    add popularity_score BIGINT DEFAULT 0;

-- == 新增資料表
CREATE TABLE forum_fav
(
    member_id INT NOT NULL,
    forum_id  INT NOT NULL,
    PRIMARY KEY (member_id, forum_id),
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE,
    FOREIGN KEY (forum_id) REFERENCES forums (forum_id) ON DELETE CASCADE
);


-- =============== [ 2025,04,01,13:00 ] ============================================================
alter table posts
    add spoiler BIT default 0 not null;

alter table categories
    add color NVARCHAR(50) NULL;

alter table forum_tags
    alter column color nvarchar(50) null;

drop table forums_bans;

CREATE TABLE forums_bans
(
    ban_id       INT PRIMARY KEY NOT NULL IDENTITY (1, 1),
    forum_id     INT             NOT NULL,
    member_id    INT             NOT NULL,
    banned_by    INT             NULL,
    banned_at    DATETIME2       NOT NULL DEFAULT SYSUTCDATETIME(),
    ban_reason   NVARCHAR(500)   NULL,
    banned_til   DATETIME2       NULL,
    is_penalized BIT                      default 0 not null
        FOREIGN KEY (forum_id) REFERENCES forums (forum_id),
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE,
    FOREIGN KEY (banned_by) REFERENCES member (id)
);
-- =============== [ 2025,04,01,13:00 ] ============================================================