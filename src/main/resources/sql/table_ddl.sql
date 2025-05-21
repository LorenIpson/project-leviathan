CREATE DATABASE leviathan COLLATE Chinese_Taiwan_Stroke_CI_AS;

-- ==========================================================================

USE leviathan

CREATE TABLE member (
    id INT IDENTITY(1,1) PRIMARY KEY,  -- 自動遞增的主鍵
    account_id NVARCHAR(20) UNIQUE NOT NULL,  -- 帳號 ID，不可重複
    username NVARCHAR(20) NOT NULL,  -- 使用者名稱
    phone NVARCHAR(20) NULL,  -- 電話號碼
    email NVARCHAR(255) UNIQUE NULL,  -- 電子信箱，可為 NULL，但不可重複
    address NVARCHAR(255) NULL,  -- 地址
    birthdate DATE NOT NULL,  -- 生日
    role INT DEFAULT 1 NOT NULL,  -- 權限等級（0: 停權, 1: 一般會員, 2: 管理員, 3: 最高管理員）
    created_at DATETIME DEFAULT GETDATE() NOT NULL,  -- 註冊日期，自動填入當前時間
    photo VARBINARY(MAX) NULL, -- 會員照片
    CONSTRAINT chk_role CHECK (role IN (0, 1, 2, 3))  -- 限制 role 只能是 0~3
);

CREATE TABLE member_auth (
    id INT PRIMARY KEY,  -- 使用 member.id 作為主鍵
    password NVARCHAR(255) NULL,  -- 加密後的密碼
    official_account INT DEFAULT 0 NOT NULL,  -- 0: 非官方帳號, 1: 官方帳號
    CONSTRAINT fk_member_auth FOREIGN KEY (id) REFERENCES member(id) ON DELETE CASCADE,
    CONSTRAINT chk_official_account CHECK (official_account IN (0, 1))  -- 限制只能是 0 或 1
);

CREATE TABLE member_log (
    id INT IDENTITY(1,1) PRIMARY KEY,           -- 自動遞增的主鍵
    member_id INT NOT NULL,                      -- 會員ID，對應 member 表的 id
    action NVARCHAR(50) NOT NULL,                -- login, logout行為
    action_time DATETIME DEFAULT GETDATE() NOT NULL,  -- 記錄動作時間，預設為當前時間
    CONSTRAINT fk_member_log FOREIGN KEY (member_id) 
        REFERENCES member(id) ON DELETE CASCADE 
);

INSERT INTO member (account_id, username, phone, email, address, birthdate, role)
VALUES 
('marco', 'Marco Lin', '0912345678', 'marco@example.com', '123 Main St, City', '1990-01-01', 3),
('lily', 'Lily Lin', '0923456789', 'lily@example.com', '456 Elm St, City', '1985-05-10', 2),
('alan', 'Alan Lin', '0934567890', 'alan@example.com', '789 Oak St, City', '1980-12-15', 1);

INSERT INTO member_auth (id, password, official_account)
VALUES 
(1, '$2a$10$7vRqTa1WmoAAhUIGiphVC.EE7fbcRkdAypFKjJNpg0nMvo6K89FLe', 1), 
(2, '$2a$10$7vRqTa1WmoAAhUIGiphVC.EE7fbcRkdAypFKjJNpg0nMvo6K89FLe', 1), 
(3, '$2a$10$7vRqTa1WmoAAhUIGiphVC.EE7fbcRkdAypFKjJNpg0nMvo6K89FLe', 1);

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

-- =============== Mall ============================================================
CREATE TABLE discount_coupons (
    coupon_id INT PRIMARY KEY IDENTITY(1,1),
    member_id INT NOT NULL,
    code NVARCHAR(50) UNIQUE,
    discount_percentage DECIMAL(5,2) CHECK (discount_percentage BETWEEN 0 AND 100),
    expiry_date DATE NOT NULL,
    status NVARCHAR(20) CHECK (status IN ('unused', 'used', 'expired')),
    CONSTRAINT fk_member_coupouns FOREIGN KEY (member_id) REFERENCES Member(id)
);

CREATE TABLE gifts (
    gift_id INT PRIMARY KEY IDENTITY(1,1),
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    item_id INT NOT NULL,
    message NVARCHAR(255),
    sent_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_sender_id FOREIGN KEY (sender_id) REFERENCES Member(id),
    CONSTRAINT fk_receiver_id FOREIGN KEY (receiver_id) REFERENCES Member(id)
);

CREATE TABLE products (
    product_id INT PRIMARY KEY IDENTITY(1,1),
    merchant_id INT NOT NULL,
    name NVARCHAR(100) NOT NULL, 
    description NVARCHAR(MAX), 
    price INT NOT NULL, 
    stock INT DEFAULT 0, 
    category NVARCHAR(50), 
    created_at DATETIME DEFAULT GETDATE(), 
    updated_at DATETIME DEFAULT GETDATE(), 
    CONSTRAINT fk_merchant_product FOREIGN KEY (merchant_id) REFERENCES Member(id)
);

CREATE TABLE product_reviews (
    review_id INT PRIMARY KEY IDENTITY(1,1),
    product_id INT NOT NULL,
    member_id INT NOT NULL,
    rating DECIMAL(2,1) CHECK (rating BETWEEN 0 AND 5),
    review NVARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_product_review FOREIGN KEY (product_id) REFERENCES Products(product_id),
    CONSTRAINT fk_member_review FOREIGN KEY (member_id) REFERENCES Member(id)
);

CREATE TABLE shopping_cart (
    cart_id INT PRIMARY KEY IDENTITY(1,1),
    member_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    added_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_member_cart FOREIGN KEY (member_id) REFERENCES Member(id),
    CONSTRAINT fk_product_cart FOREIGN KEY (product_id) REFERENCES Products(product_id)
);

CREATE TABLE wish_list (
    wishlist_id INT PRIMARY KEY IDENTITY(1,1),
    member_id INT NOT NULL,
    product_id INT NOT NULL,
    added_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_member_whish FOREIGN KEY (member_id) REFERENCES Member(id),
    CONSTRAINT fk_product_wish FOREIGN KEY (product_id) REFERENCES Products(product_id)
);

CREATE TABLE orders (
    order_id INT PRIMARY KEY IDENTITY(1,1),
    member_id INT NOT NULL,
    total_price INT NOT NULL,
    status NVARCHAR(20) CHECK (status IN ('pending', 'shipped', 'completed', 'canceled')),
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_member_order FOREIGN KEY (member_id) REFERENCES Member(id)
);

CREATE TABLE order_details (
    detail_id INT PRIMARY KEY IDENTITY(1,1),
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    price INT NOT NULL,
    subtotal AS (quantity * price) PERSISTED, -- 小計
    CONSTRAINT fk_order_orderdetail FOREIGN KEY (order_id) REFERENCES Orders(order_id),
    CONSTRAINT fk_product_orderdetail FOREIGN KEY (product_id) REFERENCES Products(product_id)
);

CREATE TABLE shipment (
    shipment_id INT PRIMARY KEY IDENTITY(1,1),
    order_id INT NOT NULL,
    tracking_number NVARCHAR(50),
    carrier NVARCHAR(100),
    shipped_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_order_shipment FOREIGN KEY (order_id) REFERENCES Orders(order_id)
);

CREATE TABLE notifications (
    notification_id INT PRIMARY KEY IDENTITY(1,1),
    member_id INT NOT NULL,
    type NVARCHAR(20) CHECK (type IN ('order_status', 'promotion', 'gift')),
    message NVARCHAR(MAX),
    status NVARCHAR(20) CHECK (status IN ('read', 'unread')),
    sent_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_member_noti FOREIGN KEY (member_id) REFERENCES Member(id)
);

CREATE TABLE merchants (
    merchant_id INT PRIMARY KEY IDENTITY(1,1),
    member_id INT NOT NULL,
    business_name NVARCHAR(100) NOT NULL,
    business_address NVARCHAR(255),
    business_phone NVARCHAR(20),
    payment_info NVARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_member_merchant FOREIGN KEY (member_id) REFERENCES Member(id)
);


CREATE TABLE transactions (
    transaction_id INT PRIMARY KEY IDENTITY(1,1),
    member_id INT NOT NULL,
    amount INT NOT NULL,
    type NVARCHAR(20) CHECK (type IN ('income', 'refund')),
    transaction_date DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_member_trans FOREIGN KEY (member_id) REFERENCES Member(id)
);



INSERT INTO merchants (member_id, business_name, business_address, business_phone, payment_info, created_at)
VALUES 
(1, 'Marco Game Store', '123 Gaming St, City', '0912-345-678', 'Bank Transfer: 1234-5678-9012', '2024-03-15 10:00:00'),
(2, 'Lily Electronics', '456 Tech Ave, City', '0923-456-789', 'Credit Card: ****-****-****-5678', '2024-04-01 14:30:00');

INSERT INTO products (merchant_id, name, description, price, stock, category, created_at, updated_at)
VALUES 
(1, 'PlayStation 5', 'Next-gen gaming console with 4K graphics.', 18000, 10, 'Gaming Console','2024-03-15 12:00:00', '2024-03-15 12:00:00'),
(1, 'Nintendo Switch', 'Portable and home gaming console.', 12000, 15, 'Gaming Console','2024-03-16 14:00:00', '2024-03-16 14:00:00'),
(1, 'Xbox Series X', 'Powerful gaming console by Microsoft.', 17500, 8, 'Gaming Console','2024-03-17 10:30:00', '2024-03-17 10:30:00'),
(1, 'Logitech G Pro Wireless', 'High-performance wireless gaming mouse.', 3500, 20, 'Gaming Accessories','2024-03-18 16:45:00', '2024-03-18 16:45:00'),
(1, 'Razer BlackWidow V3', 'Mechanical gaming keyboard with RGB lighting.', 4500, 12, 'Gaming Accessories','2024-03-19 11:15:00', '2024-03-19 11:15:00'),
(2, 'The Legend of Zelda: Tears of the Kingdom', 'An open-world adventure game on Nintendo Switch.', 1800, 25, 'Game','2024-04-01 09:30:00', '2024-04-01 09:30:00'),
(2, 'Elden Ring', 'An action RPG developed by FromSoftware.', 1900, 20, 'Game','2024-04-02 13:45:00', '2024-04-02 13:45:00'),
(2, 'God of War: Ragnarok', 'A Norse mythology-based action-adventure game.', 2000, 15, 'Game','2024-04-03 15:00:00', '2024-04-03 15:00:00'),
(2, 'Final Fantasy XVI', 'A new entry in the Final Fantasy series.', 2100, 18, 'Game','2024-04-04 18:20:00', '2024-04-04 18:20:00'),
(2, 'Resident Evil 4 Remake', 'A survival horror remake of the classic RE4.', 1700, 22, 'Game','2024-04-05 20:10:00', '2024-04-05 20:10:00');


INSERT INTO discount_coupons (member_id, code, discount_percentage, expiry_date, status)
VALUES 
(1, 'MARCO10OFF', 10.00, '2025-06-01', 'unused'),
(2, 'LILY15OFF', 15.00, '2025-07-15', 'unused'),
(3, 'ALAN20OFF', 20.00, '2025-08-20', 'unused'),
(1, 'MARCO50SPECIAL', 50.00, '2025-12-31', 'unused'),
(2, 'LILYEXPIRED30', 30.00, '2024-03-01', 'expired');

INSERT INTO gifts (sender_id, receiver_id, item_id, message, sent_at)
VALUES 
(1, 2, 101, 'Happy Birthday Lily! 🎉', '2024-03-10 14:30:00'),
(2, 3, 102, 'Alan, enjoy this gift!', '2024-02-25 10:15:00'),
(3, 1, 103, 'Marco, thanks for your help!', '2024-01-05 08:45:00'),
(1, 3, 104, 'A little something for you, Alan!', '2024-03-20 16:00:00'),
(2, 1, 105, 'Marco, you deserve this! 😊', '2024-04-01 18:30:00');

INSERT INTO product_reviews (product_id, member_id, rating, review, created_at)
VALUES 
(1, 1, 5.0, 'Amazing console! The graphics are stunning.', '2024-04-10 14:30:00'),
(3, 2, 4.5, 'Xbox Series X is super powerful, but a bit expensive.', '2024-04-11 10:15:00'),
(6, 3, 4.8, 'Zelda TOTK is a masterpiece! Highly recommend.', '2024-04-12 16:45:00'),
(8, 1, 4.2, 'God of War: Ragnarok has a great story and action.', '2024-04-13 12:10:00'),
(10, 2, 3.9, 'Resident Evil 4 Remake is solid, but the controls feel off.', '2024-04-14 18:20:00');

INSERT INTO shopping_cart (member_id, product_id, quantity, added_at)
VALUES 
(1, 1, 1, '2024-04-15 10:30:00'),  -- Marco 加入 PlayStation 5
(2, 6, 2, '2024-04-15 11:00:00'),  -- Lily 加入 Zelda: TOTK 遊戲片
(3, 3, 1, '2024-04-15 12:15:00'),  -- Alan 加入 Xbox Series X
(1, 8, 1, '2024-04-15 13:45:00'),  -- Marco 加入 God of War: Ragnarok 遊戲片
(2, 10, 3, '2024-04-15 14:20:00'); -- Lily 加入 Resident Evil 4 Remake 遊戲片

INSERT INTO wish_list (member_id, product_id, added_at)
VALUES 
(1, 2, '2024-04-16 10:00:00'),  -- Marco 想要 Nintendo Switch
(2, 7, '2024-04-16 11:20:00'),  -- Lily 想要 Elden Ring 遊戲片
(3, 4, '2024-04-16 12:45:00'),  -- Alan 想要 Logitech G Pro 無線滑鼠
(1, 9, '2024-04-16 13:30:00'),  -- Marco 想要 Final Fantasy XVI 遊戲片
(2, 5, '2024-04-16 14:10:00');  -- Lily 想要 Razer BlackWidow V3 鍵盤

INSERT INTO orders (member_id, total_price, status, created_at, updated_at)
VALUES 
-- 之後要訂單的話都用，completed就好，不然前端會抓不到詳細資料。
(1, 500, 'pending', '2024-03-11 10:00:00', '2024-03-11 10:00:00'),
(2, 1200, 'completed', '2024-03-10 12:30:00', '2024-03-10 15:00:00'),
(3, 700, 'shipped', '2024-03-09 14:45:00', '2024-03-09 18:20:00'),
(1, 350, 'pending', '2024-03-08 16:10:00', '2024-03-08 16:30:00'),
(1, 950, 'canceled', '2024-03-07 18:00:00', '2024-03-07 20:45:00'),
(2, 2100, 'pending', '2024-03-06 09:15:00', '2024-03-06 09:20:00'),
(3, 430, 'completed', '2024-03-05 20:30:00', '2024-03-05 22:10:00'),
(3, 890, 'shipped', '2024-03-04 11:25:00', '2024-03-04 14:00:00'),
(2, 120, 'pending', '2024-03-03 14:55:00', '2024-03-03 15:05:00'),
(1, 1325, 'completed', '2024-03-02 08:40:00', '2024-03-02 10:15:00');


INSERT INTO order_details (order_id, product_id, quantity, price)
VALUES 
(1, 1, 2, 250),
(2, 2, 1, 1200),
(3, 3, 3, 700),
(4, 4, 1, 350),
(5, 5, 2, 475),
(6, 6, 4, 525),
(7, 7, 1, 430),
(8, 8, 2, 445),
(9, 9, 1, 120),
(10, 10, 5, 265);

INSERT INTO shipment (order_id, tracking_number, carrier, shipped_at)
VALUES 
(1, 'TRACK123456789', 'DHL Express', '2024-04-17 09:30:00'),
(2, 'TRACK987654321', 'FedEx', '2024-04-17 11:00:00'),
(3, 'TRACK456123789', 'UPS', '2024-04-17 13:45:00'),
(4, 'TRACK321654987', 'SF Express', '2024-04-17 15:20:00'),
(5, 'TRACK654987321', 'EMS', '2024-04-17 18:10:00');

INSERT INTO notifications (member_id, type, message, status, sent_at)
VALUES 
(1, 'order_status', 'Your order #1234 has been shipped!', 'unread', '2024-04-18 09:30:00'),
(2, 'promotion', 'Exclusive deal! Get 20% off on your next purchase.', 'unread', '2024-04-18 10:00:00'),
(3, 'gift', 'You received a gift from Marco!', 'unread', '2024-04-18 11:15:00'),
(1, 'promotion', 'Limited-time offer: Buy 1 Get 1 Free!', 'read', '2024-04-18 12:45:00'),
(2, 'order_status', 'Your order #5678 has been delivered.', 'read', '2024-04-18 14:20:00');

INSERT INTO transactions (member_id, amount, type, transaction_date)
VALUES 
(1, 5000, 'income', '2024-04-19 09:30:00'),  -- Marco 收入 5000 元
(2, 3000, 'income', '2024-04-19 10:15:00'),  -- Lily 收入 3000 元
(3, 2000, 'refund', '2024-04-19 11:45:00'),  -- Alan 獲得 2000 元退款
(1, 1000, 'refund', '2024-04-19 13:20:00'),  -- Marco 獲得 1000 元退款
(2, 7000, 'income', '2024-04-19 14:50:00');  -- Lily 收入 7000 元

-- == mail ==
CREATE TABLE mailbox (
    id INT PRIMARY KEY IDENTITY(1,1), -- 信件ID
    sender_id INT NOT NULL, -- 寄件人帳號
    receiver_id INT NOT NULL, -- 收件人帳號
    title NVARCHAR(100) NOT NULL, -- 信件標題
    content NVARCHAR(MAX) NOT NULL, -- 信件內容
    send_time DATETIME DEFAULT GETDATE(), -- 寄信時間
    is_read BIT DEFAULT 0, -- 是否已讀 (0: 未讀, 1: 已讀)
    is_deleted BIT DEFAULT 0, -- 是否已刪除 (0: 未刪除, 1: 已刪除)
    
    CONSTRAINT FK_sender FOREIGN KEY (sender_id) REFERENCES member(id), -- sender_id 外來鍵
    CONSTRAINT FK_receiver FOREIGN KEY (receiver_id) REFERENCES member(id) -- receiver_id 外來鍵
);

INSERT INTO [leviathan].[dbo].[mailbox] ([sender_id], [receiver_id], [title], [content], [send_time], [is_read], [is_deleted])  
VALUES  
(1, 2, 'Hello!', 'This is a test message.', '2025-03-11 10:00:00', 0, 0),  
(2, 3, 'Meeting Reminder', 'Don’t forget our meeting at 3 PM.', '2025-03-11 11:30:00', 1, 0),  
(2, 1, 'Meeting Reminder', 'Don’t forget our meeting at 3 PM.', '2025-03-11 11:30:00', 1, 0),  
(3, 2, 'System Update', 'The system will be under maintenance tonight.', '2025-03-11 15:45:00', 0, 1);  

-- ======= 表單更新 =======
-- ======= products =======
ALTER TABLE products
ADD image_url NVARCHAR(255) NULL;

GO

UPDATE products
SET image_url = CASE product_id
    WHEN 1 THEN 'https://storage.googleapis.com/leviathan_images/product_ps5_001.webp'
    WHEN 2 THEN 'https://storage.googleapis.com/leviathan_images/product_ps5_002.webp'
    WHEN 3 THEN 'https://storage.googleapis.com/leviathan_images/product_ps5_003.webp'
    WHEN 4 THEN 'https://storage.googleapis.com/leviathan_images/product_ps5_004.webp'
    WHEN 5 THEN 'https://storage.googleapis.com/leviathan_images/product_ps5_005.webp'
    WHEN 6 THEN 'https://storage.googleapis.com/leviathan_images/product_ps5_006.webp'
    WHEN 7 THEN 'https://storage.googleapis.com/leviathan_images/product_ps5_007.webp'
    WHEN 8 THEN 'https://storage.googleapis.com/leviathan_images/product_ps5_008.webp'
    WHEN 9 THEN 'https://storage.googleapis.com/leviathan_images/product_ps5_009.webp'
    WHEN 10 THEN 'https://storage.googleapis.com/leviathan_images/product_ps5_010.webp'
    ELSE image_url
END
WHERE product_id IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

-- ======= order =======
ALTER TABLE orders
ADD merchant_trade_no VARCHAR(64) NULL;

GO

UPDATE orders
SET merchant_trade_no = UPPER(CONVERT(VARCHAR(64), NEWID()))
WHERE merchant_trade_no IS NULL;

GO

ALTER TABLE orders
ALTER COLUMN merchant_trade_no VARCHAR(64) NOT NULL;

GO

ALTER TABLE orders
ADD CONSTRAINT UQ_merchant_trade_no UNIQUE (merchant_trade_no);
-- ======= order =======

-- == 修改 posts 資料表（2025,03,18,13:00）==
alter table posts
    add is_edited BIT not null default 0;

alter table posts
    add latest_comment_at DATETIME2;

alter table comments
    add is_edited BIT not null default 0;

alter table comments
    add image_url VARCHAR(255);
-- == 修改 posts 資料表（2025,03,18,13:00）==

-- == 新增 forum_detail 資料表（2025,03,18,13:00）==
create table forum_detail
(
    forum_id    INT PRIMARY KEY NOT NULL,
    cover       VARBINARY       NULL,
    description NVARCHAR(max)   NULL,
    FOREIGN KEY (forum_id) REFERENCES forums (forum_id) ON DELETE CASCADE
);
-- == 新增 forum_detail 資料表（2025,03,18,13:00）==

-- == chatroom（2025,03,19,15:30） ==

CREATE TABLE chat_messages (
    id INT PRIMARY KEY IDENTITY(1,1),       -- 訊息 ID (主鍵，自動遞增)
    sender INT NOT NULL,                   -- 發送者 ID
    content NVARCHAR(MAX),                 -- 訊息內容（可為空）
    timestamp DATETIME NOT NULL,           -- 訊息時間戳
    gif_url VARCHAR(255),                  -- GIF 貼圖的 URL 或檔案路徑（可為空）
    CONSTRAINT FK_chat_messages_sender FOREIGN KEY (sender) REFERENCES member(id) ON DELETE CASCADE
);

INSERT INTO chat_messages (sender, content, timestamp, gif_url)
VALUES
(1, '有人在嗎!', '2023-10-01 12:00:00', NULL),
(2, NULL, '2023-10-01 12:05:00', '/src/assets/img/gif/shifun.gif'),
(3, '沒人在', '2023-10-01 12:10:00', NULL),
(2, NULL, '2023-10-01 12:15:00', '/src/assets/img/gif/nekoDance.gif'),
(1, '早安!', '2023-10-01 12:20:00', NULL);

-- == 修改 notification table ==
-- 沒外來關聯可直接刪除
DROP TABLE notifications;

CREATE TABLE notification (
    id INT PRIMARY KEY IDENTITY(1,1),
    member_id INT NOT NULL,
    type NVARCHAR(20) NOT NULL, 
    message NVARCHAR(MAX) NOT NULL,
    status INT NOT NULL, 
    value NVARCHAR(200),
    sent_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_member_noti FOREIGN KEY (member_id) REFERENCES member(id) 
);
-- == 建立 points 相關兩張 table ==
CREATE TABLE points (
    id INT PRIMARY KEY IDENTITY(1,1),
    member_id INT NOT NULL,
    points INT DEFAULT 0,
    FOREIGN KEY (member_id) REFERENCES member(id)
);

CREATE TABLE points_log (
    id INT PRIMARY KEY IDENTITY(1,1),
    member_id INT NOT NULL,
    points_change INT NOT NULL,
    reason NVARCHAR(255),
    created_at DATETIME DEFAULT GETDATE() NOT NULL,
    FOREIGN KEY (member_id) REFERENCES member(id)
);

INSERT INTO points (member_id, points)
SELECT id, 1000
FROM member;
-- == points ==

-- ====avatar（2025,03,20,16:00）===

CREATE TABLE avatar_commodity (
    id INT PRIMARY KEY IDENTITY(1,1),
    commodity_name NVARCHAR(50),
    type NVARCHAR(20),
    photo_path NVARCHAR(255),
    shelf_time DATETIME,
    point INT
);

CREATE TABLE avatar_storehouse (
    id INT PRIMARY KEY IDENTITY(1,1),
    member_id INT NOT NULL,
    commodity_id INT,
    equipment_status INT,
    FOREIGN KEY (member_id) REFERENCES member(id),
    FOREIGN KEY (commodity_id) REFERENCES avatar_commodity(id)
);

CREATE TABLE avatar_photo (
    id INT PRIMARY KEY IDENTITY(1,1),
    member_id INT NOT NULL,
    body_photo NVARCHAR(MAX),
    face_photo NVARCHAR(MAX),
    FOREIGN KEY (member_id) REFERENCES member(id)
);

-- == 修改 avatar photo ==
-- 1. 刪除原來的欄位
ALTER TABLE avatar_photo
DROP COLUMN body_photo, face_photo;

-- 2. 新增新的欄位，將型別改為 VARBINARY(MAX)
ALTER TABLE avatar_photo
ADD body_photo VARBINARY(MAX),
    face_photo VARBINARY(MAX);
-- ====

INSERT INTO avatar_photo (member_id, body_photo, face_photo)
SELECT id, NULL, NULL
FROM member;

-- =============== [ 2025,03,28,13:00] ============================================================
-- == 修改 posts 資料表
alter table posts
    add edited_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME();
-- 修改 posts 資料表 ==

-- == 修改 comments 資料表
alter table comments
    add edited_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME();
-- 修改 comments 資料表 ==

-- == 修改 comments 資料表
alter table forums
    alter column cover varbinary(max) null
-- 修改 comments 資料表 ==

-- == 修改 forums 資料表
alter table forums
    add popularity_score BIGINT DEFAULT 0;
-- 修改 forums 資料表 ==

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
-- 修改 post_images 資料表 ==

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
-- 修改 comment_images 資料表 ==

-- == 修改 comments 資料表
alter table comments
    drop column image_url;
-- 修改 comments 資料表 ==

-- == 修改 forum_tags 資料表
alter table forum_tags
    add is_active BIT NOT NULL DEFAULT 1

alter table forum_tags
    add color NVARCHAR(20) NULL;
-- 修改 forum_tags 資料表 ==

-- == 修改 forum_detail 資料表
alter table forum_detail
    alter column cover varbinary(max) null
-- 修改 forum_detail 資料表 ==

-- == 修改 posts 資料表
alter table posts
    add popularity_score BIGINT DEFAULT 0;
-- 修改 posts 資料表 ==

-- == 新增資料表
CREATE TABLE forum_fav
(
    member_id INT NOT NULL,
    forum_id  INT NOT NULL,
    PRIMARY KEY (member_id, forum_id),
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE,
    FOREIGN KEY (forum_id) REFERENCES forums (forum_id) ON DELETE CASCADE
);
-- 新增資料表 ==
-- =============== [ 2025,03,28,13:00] ============================================================

INSERT INTO avatar_commodity (commodity_name, type, photo_path, shelf_time, point) VALUES
('白色', 'background', 'https://storage.googleapis.com/leviathan_images/93dffd40-f660-4c15-a7e6-9c89ed201d83-shiroi.png', '2025-03-28 16:17:27.717', 10),
('黑色', 'background', 'https://storage.googleapis.com/leviathan_images/cbb4d5a2-7bf3-466a-a318-497f6877c0bb-kuroi.png', '2025-03-28 16:17:44.660', 10),
('冒險者公會', 'background', 'https://storage.googleapis.com/leviathan_images/2b61c514-2f26-4318-af50-a2db75207eb7-Adventurers_Guild.png', '2025-03-28 16:18:07.580', 50),
('古代城堡', 'background', 'https://storage.googleapis.com/leviathan_images/4217d1a9-b161-4787-a55e-0c9dcf031217-Ancient_castle.png', '2025-03-28 16:18:19.517', 50),
('紅月', 'background', 'https://storage.googleapis.com/leviathan_images/fa0c6b5a-5a90-40d6-8fde-1430d54a405c-red_moon.png', '2025-03-28 16:18:36.817', 50),
('中國式大廳', 'background', 'https://storage.googleapis.com/leviathan_images/dca80c41-5c0a-49df-a084-d0c971214305-Chinese.png', '2025-03-28 16:18:48.037', 50),
('沙漠小鎮', 'background', 'https://storage.googleapis.com/leviathan_images/b257dafa-de00-4c85-9c73-63c840c20413-Desert_village.png', '2025-03-28 16:18:58.610', 50),
('藍斗篷冒險者布衣', 'clothes', 'https://storage.googleapis.com/leviathan_images/7b2165cc-0ab0-42c6-8e08-4b8a66ae407a-Blue_cloak_leather_skirt.png', '2025-03-28 16:19:18.987', 100),
('藍色中國風旗袍', 'clothes', 'https://storage.googleapis.com/leviathan_images/728390e3-97a5-4fd5-9d22-6344f7cb5711-Blue_cheongsam_short_sleeves.png', '2025-03-28 16:19:34.417', 100),
('黑色中國漢服', 'clothes', 'https://storage.googleapis.com/leviathan_images/42a16541-e8f7-444c-98a9-800248181c36-China_cheongsam_black_clothes.png', '2025-03-28 16:19:48.680', 100),
('紅色中國功夫裝', 'clothes', 'https://storage.googleapis.com/leviathan_images/08b5b7d3-5f28-4f15-b8de-037deacc9c3b-China_red_kungfu_clothes.png', '2025-03-28 16:20:01.090', 100),
('綠色森林冒險者服', 'clothes', 'https://storage.googleapis.com/leviathan_images/8c07149b-9e58-4e5b-a5dc-415aa7b111fa-Grassland_long_sleeves.png', '2025-03-28 16:20:16.473', 100),
('綠色初心勇者服', 'clothes', 'https://storage.googleapis.com/leviathan_images/5bac1c36-6cf3-4164-8c30-59bffdff0d97-Green_dress_red_collar.png', '2025-03-28 16:20:30.147', 100),
('黑色功夫褲', 'pants', 'https://storage.googleapis.com/leviathan_images/6ba4b9b7-c95d-4cc5-a561-2847004f1a81-China_Black_KungFu_Pants.png', '2025-03-28 16:20:30.147', 100),
('橘色圖騰冒險者褲', 'pants', 'https://storage.googleapis.com/leviathan_images/d130bd72-ca98-45fb-972c-f2a19dbb6b6b-Medium_Totem_Orange_Pants.png', '2025-03-28 16:20:30.147', 100),
('條紋淡紫色緊身褲', 'pants', 'https://storage.googleapis.com/leviathan_images/5687cb84-b281-434c-abec-97b3b90040f4-Tight_striped_pants.png', '2025-03-28 16:20:30.147', 100),
('黑色落腮鬍', 'face', 'https://storage.googleapis.com/leviathan_images/9f87406a-e6d4-4d78-8214-66dbb2d78da5-black_beard.png', '2025-03-28 16:20:47.987', 50),
('白色老伯鬍', 'face', 'https://storage.googleapis.com/leviathan_images/149ec701-b1ce-4c4d-a41b-b7996b6beec5-white_eyebrows_beard.png', '2025-03-28 16:21:01.603', 50),
('單眼眼罩', 'face', 'https://storage.googleapis.com/leviathan_images/a2a7166a-ac06-4f44-a8ac-1c31b25bd1ba-eye_mask.png', '2025-03-28 16:21:19.313', 50),
('少女眼睛', 'face', 'https://storage.googleapis.com/leviathan_images/9ab23ec7-39c4-4b5a-adc4-bea0e9e7e75d-girl_big_eyes_green.png', '2025-03-28 16:21:33.313', 50),
('黑髮中國辮子', 'hair', 'https://storage.googleapis.com/leviathan_images/baeeb694-0a65-4738-824a-2218b4e8f7d9-Black_braids_long_hair.png', '2025-03-28 16:21:54.707', 80),
('中國包子頭藍髮', 'hair', 'https://storage.googleapis.com/leviathan_images/b418d871-4c0c-42c7-a441-6a3c2c121976-China_Baotou_Blue_Fat.png', '2025-03-28 16:22:06.770', 80),
('橘色短髮', 'hair', 'https://storage.googleapis.com/leviathan_images/d45d01be-baf1-4616-9ae4-0055ecb624db-Light_orange_short_hair.png', '2025-03-28 16:22:18.577', 80),
('白色中長髮', 'hair', 'https://storage.googleapis.com/leviathan_images/c99e05ae-9365-41b2-894b-14afe79c166a-White_side_parted_long_hair.png', '2025-03-28 12:26:12.643', 80),
('黑色低筒鞋', 'shoes', 'https://storage.googleapis.com/leviathan_images/2e86cb56-d6a4-4569-b0d6-7fe6074caf6e-Black_low_sandals.png', '2025-03-28 12:27:23.027', 70),  
('初級勇者靴', 'shoes', 'https://storage.googleapis.com/leviathan_images/f0b11eac-9e4a-455c-9aa2-1a3240fb3c73-Brown_folding_ear_boots.png', '2025-03-28 12:27:37.820', 70),  
('皮製長靴', 'shoes', 'https://storage.googleapis.com/leviathan_images/7864a58a-b02c-4d2f-9d87-a71ff7f43bcd-Straps_boots.png', '2025-03-28 12:27:47.963', 70),  
('木弓', 'weapon', 'https://storage.googleapis.com/leviathan_images/449d1718-915c-44ff-84b2-8aa6b4796cf8-Archery.png', '2025-03-28 12:28:03.443', 100),  
('紫色巨劍', 'weapon', 'https://storage.googleapis.com/leviathan_images/878ffd32-38f4-4a62-a0ca-c52bee0b8c00-Purple_Greatsword.png', '2025-03-28 12:28:13.187', 100),  
('精靈長劍', 'weapon', 'https://storage.googleapis.com/leviathan_images/cb2f4a95-af0e-4e10-8d7b-759263844135-Slender_sword.png', '2025-03-28 12:28:24.340', 100);  

-- 新增紙娃
INSERT INTO avatar_commodity (commodity_name, type, photo_path, shelf_time, point) VALUES
('棕色辮子長髮', 'hair', 'https://storage.googleapis.com/leviathan_images/2b51ca22-8752-4a03-a512-40c65e64667b-Brown_long_braids.png', '2025-03-28 16:17:27.717', 80),
('紅色辮子短髮', 'hair', 'https://storage.googleapis.com/leviathan_images/37ed65da-af47-44f4-ba75-2d2bb9056fb9-Red_short_braids_hair.png', '2025-03-28 16:17:44.660', 80),
('藍髮雙辮子', 'hair', 'https://storage.googleapis.com/leviathan_images/fb9a3307-bbea-4857-90fa-e01999ed5fc3-blue_braids_short_hair.png', '2025-03-28 16:17:44.660', 80),
('藍色墨鏡', 'face', 'https://storage.googleapis.com/leviathan_images/bc0d55dd-45bf-42dc-9b9e-c645ff2bc612-blue_sunglasses.png', '2025-03-28 16:17:44.660', 50),
('小圓眼鏡', 'face', 'https://storage.googleapis.com/leviathan_images/8cad2065-1229-4828-87ea-7f5a4754b5fe-Small_round_sunglasses.png', '2025-03-28 16:17:44.660', 50),
('足輕鞋', 'shoes', 'https://storage.googleapis.com/leviathan_images/5315c2e4-c021-410b-b592-4d8570b4fb41-Japanese_Samurai_Shoes.png', '2025-03-28 16:17:44.660', 70);

INSERT INTO avatar_storehouse (member_id, commodity_id, equipment_status) VALUES
(1, 2, 1),
(1, 3, 0),
(1, 4, 0),
(1, 5, 0),
(1, 8, 0),
(1, 9, 0),
(1, 10, 0),
(1, 14, 0),
(1, 15, 0),
(1, 16, 0),
(1, 17, 0),
(1, 18, 0),
(1, 21, 0),
(1, 22, 0),
(1, 25, 0),
(1, 26, 0),
(1, 27, 0),
(1, 28, 0),
(1, 29, 0),
(1, 30, 0),
(1, 31, 0),
(2, 2, 1),
(2, 3, 0),
(2, 4, 0),
(2, 5, 0),
(2, 8, 0),
(2, 9, 0),
(2, 10, 0),
(2, 14, 0),
(2, 15, 0),
(2, 16, 0),
(2, 17, 0),
(2, 18, 0),
(2, 21, 0),
(2, 22, 0),
(2, 25, 0),
(2, 26, 0),
(2, 27, 0),
(2, 28, 0),
(2, 29, 0),
(2, 30, 0),
(2, 31, 0),
(3, 2, 1),
(3, 3, 0),
(3, 4, 0),
(3, 5, 0),
(3, 8, 0),
(3, 9, 0),
(3, 10, 0),
(3, 14, 0),
(3, 15, 0),
(3, 16, 0),
(3, 17, 0),
(3, 18, 0),
(3, 21, 0),
(3, 22, 0),
(3, 25, 0),
(3, 26, 0),
(3, 27, 0),
(3, 28, 0),
(3, 29, 0),
(3, 30, 0),
(3, 31, 0);

-- ====
-- 更新原本products的10筆資料
UPDATE products
SET
    name = CASE product_id
        WHEN 1 THEN '《歧路旅人 + 歧路旅人 II 合輯》中日英文版'
        WHEN 2 THEN '《惡魔城週年慶合輯》豪華版'
        WHEN 3 THEN '《Hello Kitty：島嶼冒險》中文豪華版（附贈預購特典）'
        WHEN 4 THEN '《好餓的謎姆》中文限定版（附贈預購特典）'
        WHEN 5 THEN '《樂高地平線大冒險》中文版'
        WHEN 6 THEN '《蜂蜜氛圍》中文一般版'
        WHEN 7 THEN '《優米雅的鍊金工房 ~追憶之鍊金術士與幻創之地~》中文典藏版'
        WHEN 8 THEN '《獵人 HUNTER x HUNTER 念能力衝擊》中日英文版'
        WHEN 9 THEN '《薔薇與椿 ～豪華絢爛版～》中日英文版'
        WHEN 10 THEN '《薩爾達傳說 智慧的再現》中文版'
    END,

    description = CASE product_id
        WHEN 1 THEN '集結兩部經典RPG作品，體驗像素風格與多主角交錯的奇幻冒險故事。支援中日英文字幕。'
        WHEN 2 THEN '經典橫向動作遊戲《惡魔城》系列合集，收錄多款原始版本並提供畫冊、音樂等豪華特典。'
        WHEN 3 THEN '與 Hello Kitty 和三麗鷗朋友一起探索可愛小島！收集物品、裝飾家園，適合親子同樂的療癒遊戲。'
        WHEN 4 THEN '一款融合解謎與冒險的可愛遊戲，玩家將幫助謎姆解決島上的奇妙事件，適合所有年齡層。'
        WHEN 5 THEN 'LEGO 與《地平線》系列合作大作，機械恐龍與積木冒險的奇妙結合，充滿創意與挑戰。'
        WHEN 6 THEN '輕鬆療癒的田園模擬遊戲，體驗採集蜂蜜、佈置村莊與與動物互動的溫馨日常。'
        WHEN 7 THEN '鍊金術與冒險完美融合的角色扮演遊戲，典藏版內含美術集、原聲帶與收藏品。'
        WHEN 8 THEN '經典動漫《HUNTER x HUNTER》改編動作對戰遊戲，體驗念能力激戰快感，支援多語字幕。'
        WHEN 9 THEN '華麗又詭異的決鬥遊戲，體驗復仇與掌摑的藝術，支援雙人對戰與繁中界面。'
        WHEN 10 THEN '傳說再臨！《薩爾達傳說》系列新作，延續開放世界冒險與解謎元素，支援中文。'
    END,

    price = CASE product_id
        WHEN 1 THEN 1790
        WHEN 2 THEN 1950
        WHEN 3 THEN 1490
        WHEN 4 THEN 1520
        WHEN 5 THEN 1790
        WHEN 6 THEN 1790
        WHEN 7 THEN 5850
        WHEN 8 THEN 1950
        WHEN 9 THEN 990
        WHEN 10 THEN 1690
    END,

    stock = 10,

    category = '遊戲軟體',

    created_at = CASE product_id
        WHEN 1 THEN '2024-03-15 12:00:00'
        ELSE '2024-03-16 14:00:00'
    END,

    updated_at = CASE product_id
        WHEN 1 THEN '2024-03-15 12:00:00'
        ELSE '2024-03-16 14:00:00'
    END,

    image_url = CASE product_id
        WHEN 1 THEN 'https://storage.googleapis.com/leviathan_images/mall/ns_game_4.JPG'
        WHEN 2 THEN 'https://storage.googleapis.com/leviathan_images/mall/ns_game_5.JPG'
        WHEN 3 THEN 'https://storage.googleapis.com/leviathan_images/mall/ns_game_6.JPG'
        WHEN 4 THEN 'https://storage.googleapis.com/leviathan_images/mall/ns_game_9.JPG'
        WHEN 5 THEN 'https://storage.googleapis.com/leviathan_images/mall/ns_game_8.JPG'
        WHEN 6 THEN 'https://storage.googleapis.com/leviathan_images/mall/ns_game_3.JPG'
        WHEN 7 THEN 'https://storage.googleapis.com/leviathan_images/mall/ns_game_1.JPG'
        WHEN 8 THEN 'https://storage.googleapis.com/leviathan_images/mall/ns_game_2.JPG'
        WHEN 9 THEN 'https://storage.googleapis.com/leviathan_images/mall/ns_game_10.JPG'
        WHEN 10 THEN 'https://storage.googleapis.com/leviathan_images/mall/ns_game_7.jpg'
    END
WHERE product_id IN (1,2,3,4,5,6,7,8,9,10);

UPDATE products
SET merchant_id = 1
WHERE product_id IN (6, 7, 8, 9, 10);

-- === 新增新的資料
INSERT INTO products (merchant_id, name, description, price, stock, category, created_at, updated_at, image_url)
VALUES
(1, '《獵人 HUNTER x HUNTER 念能力衝擊》中日英文版', '動漫改編的對戰動作遊戲，體驗念能力與原作劇情的刺激對戰。', 1000, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/ps5_game_1.JPG'),
(1, '《天國：拯救 2》中文一般版', '以歷史為背景的開放世界 RPG，還原中世紀波西米亞的真實生活與戰鬥。', 1010, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/ps5_game_3.jpg'),
(1, '《優米雅的鍊金工房 ~追憶之鍊金術士與幻創之地~》中文特典版', '最新發售的熱門遊戲，帶來豐富的玩法與劇情體驗。', 1020, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/ps5_game_9.JPG'),
(1, '《死亡擱淺 2：冥灘之上》中文一般版', '小島秀夫監製第二部曲，全新角色與劇情，深入探索冥灘之謎。', 1030, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/ps5_game_7.JPG'),
(1, '《東方符卡嘉年華》中文一般版', '最新發售的熱門遊戲，帶來豐富的玩法與劇情體驗。', 1040, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/ps5_game_5.JPG'),
(1, '《艾爾登法環 黑夜君臨》中文一般版', '魂系巨作全新 DLC，挑戰難度極高的黑夜王與新區域探索。', 1050, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/ps5_game_2.JPG'),
(1, '《無限機兵》中文豪華版', '最新發售的熱門遊戲，帶來豐富的玩法與劇情體驗。', 1060, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/ps5_game_4.JPG'),
(1, '《魔物獵人 荒野》中文一般版', '動漫改編的對戰動作遊戲，體驗念能力與原作劇情的刺激對戰。', 1070, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/ps5_game_10.JPG'),
(1, '《女鬼橋二 釋魂路》中文限定版', '國產恐怖遊戲續作，揭開校園靈異事件背後的真相。', 1080, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/ps5_game_6.jpg'),
(1, '《龍族教義 2》中日英文版', '最新發售的熱門遊戲，帶來豐富的玩法與劇情體驗。', 1090, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/ps5_game_8.JPG'),
(1, '《鬼滅之刃 火之神血風譚》中文究極版', '動畫改編 3D 對戰遊戲，體驗炭治郎與柱們的招式與劇情。', 1100, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/steam_game_1.JPG'),
(1, '《螞蟻雄兵》中文一般版', '最新發售的熱門遊戲，帶來豐富的玩法與劇情體驗。', 1110, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/steam_game_3.JPG'),
(1, '《潛龍諜影 Delta：食蛇者》中文初回一般版', '最新發售的熱門遊戲，帶來豐富的玩法與劇情體驗。', 1120, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/steam_game_8.JPG'),
(1, '《VR 快打 5 R.E.V.O.》30 週年紀念版', '最新發售的熱門遊戲，帶來豐富的玩法與劇情體驗。', 1130, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/steam_game_10.JPG'),
(1, '《人中之龍 8 外傳 夏威夷海盜》中文豪華版', '最新發售的熱門遊戲，帶來豐富的玩法與劇情體驗。', 1140, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/steam_game_2.JPG'),
(1, '《即刻離職》中英日文版', '風格獨特的職場模擬遊戲，在辦公室中想方設法逃離職場壓力。', 1150, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/steam_game_6.JPG'),
(1, '《模擬農場 25》Year 1 組合包', '最新發售的熱門遊戲，帶來豐富的玩法與劇情體驗。', 1160, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/steam_game_7.JPG'),
(1, '《職棒野球魂 2024-2025》日文版', '最新發售的熱門遊戲，帶來豐富的玩法與劇情體驗。', 1170, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/steam_game_5.JPG'),
(1, '《暗喻幻想：ReFantazio》中文一般版', '最新發售的熱門遊戲，帶來豐富的玩法與劇情體驗。', 1180, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/steam_game_9.JPG'),
(1, '《天命奇御二》中文版', '最新發售的熱門遊戲，帶來豐富的玩法與劇情體驗。', 1190, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/steam_game_4.jpg'),
(1, '《魔物獵人 荒野》中文一般版', '動漫改編的對戰動作遊戲，體驗念能力與原作劇情的刺激對戰。', 1200, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/xsx_game_1.JPG'),
(1, '《死亡復甦 豪華復刻版》中文一般版', '最新發售的熱門遊戲，帶來豐富的玩法與劇情體驗。', 1210, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/xsx_game_5.JPG'),
(1, '《Funko 聯合》中文版', '最新發售的熱門遊戲，帶來豐富的玩法與劇情體驗。', 1220, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/xsx_game_8.JPG'),
(1, '《戰錘 40K：星際戰士 2》中文一般版', '最新發售的熱門遊戲，帶來豐富的玩法與劇情體驗。', 1230, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/xsx_game_9.JPG'),
(1, '《逆轉檢察官 1&2 御劍精選集》中文版', '最新發售的熱門遊戲，帶來豐富的玩法與劇情體驗。', 1240, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/xsx_game_2.JPG'),
(1, '《神話世紀：重述》中文一般版', '最新發售的熱門遊戲，帶來豐富的玩法與劇情體驗。', 1250, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/xsx_game_6.JPG'),
(1, '《鐵拳 8》中文一般版', '最新發售的熱門遊戲，帶來豐富的玩法與劇情體驗。', 1260, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/xsx_game_3.jpg'),
(1, '《MLB The Show 24》一般版', '最新發售的熱門遊戲，帶來豐富的玩法與劇情體驗。', 1270, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/xsx_game_10.jpg'),
(1, '《自殺突擊隊：戰勝正義聯盟》中文豪華版', '最新發售的熱門遊戲，帶來豐富的玩法與劇情體驗。', 1280, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/xsx_game_4.jpg'),
(1, '《哈利波特：魁地奇鬥士》中文一般版', '最新發售的熱門遊戲，帶來豐富的玩法與劇情體驗。', 1290, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/xsx_game_7.JPG'),
(1, '任天堂點數 3000 點', '官方正版數位點數卡，支援各大平台線上購買使用。', 1300, 10, '點數', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/point_1.JPG'),
(1, '任天堂點數 5000 點', '官方正版數位點數卡，支援各大平台線上購買使用。', 1310, 10, '點數', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/point_2.JPG'),
(1, 'PSN 禮物卡 2000 元', '官方正版數位點數卡，支援各大平台線上購買使用。', 1320, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/point_7.JPG'),
(1, 'PSN 禮物卡 1500 元', '官方正版數位點數卡，支援各大平台線上購買使用。', 1330, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/point_8.JPG'),
(1, 'PSN 禮物卡 1000 元', '官方正版數位點數卡，支援各大平台線上購買使用。', 1340, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/point_10.JPG'),
(1, 'PSN 禮物卡 800 元', '官方正版數位點數卡，支援各大平台線上購買使用。', 1350, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/point_4.JPG'),
(1, 'PSN 禮物卡 500 元', '官方正版數位點數卡，支援各大平台線上購買使用。', 1360, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/point_9.JPG'),
(1, 'PSN 禮物卡 300 元', '官方正版數位點數卡，支援各大平台線上購買使用。', 1370, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/point_5.JPG'),
(1, 'Microsoft Xbox 禮品卡 $2000 數位下載版', '官方正版數位點數卡，支援各大平台線上購買使用。', 1380, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/point_6.JPG'),
(1, 'Microsoft Xbox 禮品卡 $1000 數位下載版', '官方正版數位點數卡，支援各大平台線上購買使用。', 1390, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/point_3.JPG'),
(1, 'Nintendo Switch OLED 款式台灣專用機（白）', '最新發售的熱門遊戲，帶來豐富的玩法與劇情體驗。', 1400, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/ns_1.jpg'),
(1, 'Nintendo Switch OLED 款式台灣專用機（紅藍）', '最新發售的熱門遊戲，帶來豐富的玩法與劇情體驗。', 1410, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/ns_2.jpg'),
(1, 'PlayStation 5 新款薄型化 台灣專用機', '最新發售的熱門遊戲，帶來豐富的玩法與劇情體驗。', 1420, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/ps5_2.jpg'),
(1, 'PlayStation 5 Pro 台灣專用機', '最新發售的熱門遊戲，帶來豐富的玩法與劇情體驗。', 1430, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/ps5_1.jpg'),
(1, 'Xbox Series S 台灣專用機', '最新發售的熱門遊戲，帶來豐富的玩法與劇情體驗。', 1440, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/xsx_1.jpg'),
(1, 'PlayStation Portal 遙控遊玩裝置', '最新發售的熱門遊戲，帶來豐富的玩法與劇情體驗。', 1450, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/ps5_4.jpg'),
(1, 'PlayStation Portal 遙控遊玩裝置（午夜黑）', '最新發售的熱門遊戲，帶來豐富的玩法與劇情體驗。', 1460, 10, '遊戲軟體', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/ps5_6.jpg'),
(1, 'Nintendo Switch Pro 控制器', '高靈敏度無線手把，支援震動與自訂按鍵配置。', 1470, 10, '控制器', '2024-03-16 14:00:00', '2024-03-16 14:00:00', ''),
(1, 'PS5 DualSense Edge 無線控制器（午夜黑）', '高靈敏度無線手把，支援震動與自訂按鍵配置。', 1480, 10, '控制器', '2024-03-16 14:00:00', '2024-03-16 14:00:00', ''),
(1, 'PS5 DualSense 無線控制器（閃耀珍珠白）', '高靈敏度無線手把，支援震動與自訂按鍵配置。', 1490, 10, '控制器', '2024-03-16 14:00:00', '2024-03-16 14:00:00', ''),
(1, 'Xbox 無線控制器（緋紅領域）', '高靈敏度無線手把，支援震動與自訂按鍵配置。', 1500, 10, '控制器', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/ns_3.jpg'),
(1, 'Xbox 無線控制器（冰霜領域）', '高靈敏度無線手把，支援震動與自訂按鍵配置。', 1510, 10, '控制器', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/ps5_5.jpg'),
(1, 'Xbox 無線控制器（湛藍領域）', '高靈敏度無線手把，支援震動與自訂按鍵配置。', 1520, 10, '控制器', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/ps5_3.jpg'),
(1, 'SteelSeries Aerox 9 超輕量型無線電競滑鼠（黑）', '輕量設計與 DPI 自訂功能，適合 FPS 與 MOBA 遊戲。', 1530, 10, '滑鼠', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/xsx_4.jpg'),
(1, 'ASUS ROG Falchion Ace HFX 65% 磁軸電競鍵盤', '專業電競機械鍵盤，支援 RGB 燈效與快速響應。', 1540, 10, '鍵盤', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/xsx_3.jpg'),
(1, 'Razer Huntsman 獵魂光蛛 V3 Pro Tenkeyless 類比式光軸電競鍵盤（白，英文鍵面）', '專業電競機械鍵盤，支援 RGB 燈效與快速響應。', 1550, 10, '鍵盤', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/xsx_2.jpg'),
(1, 'ASUS ROG Keris II Ace 無線三模電競滑鼠（黑）', '輕量設計與 DPI 自訂功能，適合 FPS 與 MOBA 遊戲。', 1560, 10, '滑鼠', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/steam_6.jpg'),
(1, 'Razer Kraken 北海巨妖 Kitty V2 Pro 電競耳機麥克風（黑）', '高解析度音效，搭配降噪麥克風，提升遊戲沉浸感。', 1570, 10, '耳機', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/steam_3.jpg'),
(1, 'Razer Kraken 北海巨妖 V4 無線電競耳機麥克風', '高解析度音效，搭配降噪麥克風，提升遊戲沉浸感。', 1580, 10, '耳機', '2024-03-16 14:00:00', '2024-03-16 14:00:00', 'https://storage.googleapis.com/leviathan_images/mall/steam_4.jpg');

---====
-- === 新增 member_login_history ===
ALTER TABLE member_auth
ADD failed_attempts INT DEFAULT 0,  -- 紀錄當前登入失敗次數
    lock_time DATETIME NULL,       -- 紀錄帳號鎖定時間
    last_login DATETIME NULL;      -- 紀錄最後成功登入時間

GO

UPDATE member_auth
SET failed_attempts = 0;

-- ====

-- == 修改 member accountId 長度 ==
ALTER TABLE member
ALTER COLUMN account_id NVARCHAR(255) NOT NULL;
-- ====

-- == 修改 member birthdate 長度 ==
ALTER TABLE member
ALTER COLUMN birthdate DATE NULL;
-- ====

-- =============== [ 2025,04,01,13:00 ] [ Li ] ============================================================
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
-- =============== [ 2025,04,01,13:00 ] [ Li ] ============================================================

-- ==== [通知config] ====
CREATE TABLE notification_setting (
    id INT PRIMARY KEY IDENTITY(1,1),
    member_id INT NOT NULL,
    type NVARCHAR(20) NOT NULL,
    enabled BIT NOT NULL DEFAULT 1,
    CONSTRAINT fk_notification_setting_user FOREIGN KEY (member_id) REFERENCES member(id),
    CONSTRAINT uq_notification_setting UNIQUE (member_id, type)
);
-- ========

-- =============== [ 2025,04,02,13:00 ] [ AD_test ] ============================================================
CREATE TABLE Ads (
    Id BIGINT PRIMARY KEY IDENTITY(1,1),
    ImageUrl NVARCHAR(500) NOT NULL,
    RedirectUrl NVARCHAR(500) NOT NULL,
    Position NVARCHAR(100) NOT NULL,
    StartTime DATETIME NOT NULL,
    EndTime DATETIME NOT NULL,
    IsActive BIT DEFAULT 1,
    width INT NULL,
    height INT NULL,
    SortOrder INT NULL
);

CREATE TABLE AdLogs (
    Id BIGINT PRIMARY KEY IDENTITY(1,1),
    AdId BIGINT NOT NULL,
    Type NVARCHAR(10) CHECK (Type IN ('click', 'view')),
    UserId BIGINT NULL,
    IpAddress NVARCHAR(50),
    CreatedAt DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (AdId) REFERENCES Ads(Id)
);

INSERT INTO Ads (ImageUrl, RedirectUrl, Position, StartTime, EndTime, IsActive, width, height, SortOrder)
VALUES 
('https://storage.googleapis.com/leviathan_images/b3ebccbe-fe95-463b-8b9e-0baa1bae176f-chiikawa-usagi.gif',
 'https://chiikawamarket.jp/zh-hant',
 'right-sidebar',
 '2025-04-02 18:08:00.000',
 '2025-04-20 18:08:00.000',
 1, NULL, NULL, 4),

('https://storage.googleapis.com/leviathan_images/37272c6c-660c-4d32-897b-daf13a6f2ffb-eating-eating-food.gif',
 'https://www.gamer.com.tw/',
 'left-sidebar',
 '2025-04-02 19:08:00.000',
 '2025-04-20 19:08:00.000',
 1, NULL, NULL, 1),

('https://storage.googleapis.com/leviathan_images/48da2cb2-8d74-4e0d-8e6f-3c1327f8498a-banner_image_8628_0.jpg',
 'https://asurajang.pmang.jp/?utm_source=gamespark&utm_medium=da&utm_campaign=asurajang&utm_content=open&utm_term=250327',
 'right-sidebar',
 '2025-04-02 19:39:00.000',
 '2025-04-20 19:39:00.000',
 1, 200, 660, 3),

('https://storage.googleapis.com/leviathan_images/ae7b877e-4ed7-48ee-8109-b98e88808670-banner_image_8627_0.jpg',
 'https://asurajang.pmang.jp/?utm_source=gamespark&utm_medium=da&utm_campaign=asurajang&utm_content=open&utm_term=250327',
 'left-sidebar',
 '2025-04-02 22:40:00.217',
 '2025-10-02 22:40:00.217',
 1, 200, 660, 2);

-- =============== [ 2025,04,02,13:00 ] [ AD_test ] ============================================================

-- =============== [ 2025,04,07 ] [ products_update ] ============================================================

UPDATE products
SET image_url = 'https://storage.googleapis.com/leviathan_images/mall/ns_3.jpg'
WHERE product_id = 58; 

UPDATE products
SET image_url = 'https://storage.googleapis.com/leviathan_images/mall/ps5_5.jpg'
WHERE product_id = 59; 

UPDATE products
SET image_url = 'https://storage.googleapis.com/leviathan_images/mall/ps5_3.jpg'
WHERE product_id = 60; 

UPDATE products
SET image_url = 'https://storage.googleapis.com/leviathan_images/mall/xsx_4.jpg'
WHERE product_id = 61; 

UPDATE products
SET image_url = 'https://storage.googleapis.com/leviathan_images/mall/xsx_3.jpg'
WHERE product_id = 62; 

UPDATE products
SET image_url = 'https://storage.googleapis.com/leviathan_images/mall/xsx_2.jpg'
WHERE product_id = 63; 

UPDATE products
SET image_url = 'https://storage.googleapis.com/leviathan_images/mall/steam_6.jpg'
WHERE product_id = 64;

UPDATE products
SET image_url = 'https://storage.googleapis.com/leviathan_images/mall/steam_5.jpg'
WHERE product_id = 67;

UPDATE products
SET image_url = 'https://storage.googleapis.com/leviathan_images/mall/steam_4.jpg'
WHERE product_id = 66;

UPDATE products
SET image_url = 'https://storage.googleapis.com/leviathan_images/mall/steam_3.jpg'
WHERE product_id = 65;

UPDATE products
SET image_url = 'https://storage.googleapis.com/leviathan_images/mall/steam_2.jpg'
WHERE product_id = 68;

UPDATE products
SET image_url = 'https://storage.googleapis.com/leviathan_images/mall/steam_1.jpg'
WHERE product_id = 69;

-- =============== [ 2025,04,08 ] [ mailbox_update ] ============================================================

INSERT INTO [leviathan].[dbo].[mailbox] (sender_id, receiver_id, title, content, send_time, is_read, is_deleted) VALUES
(1, 2, '會議安排', '我們星期三中午開個會討論一下進度吧。', '2025-04-08 10:30:00', 0, 0),
(2, 1, 'RE: 會議安排', '好，星期三中午我有空。', '2025-04-09 14:45:00', 1, 0),
(3, 1, '請問週末有空嗎？', '有件事情想和你聊聊，看看週末能不能碰面。', '2025-04-13 18:20:00', 0, 0),
(1, 3, '週末出遊提案', '這週末天氣不錯，要不要去爬山？', '2025-04-15 09:10:00', 1, 0),
(2, 3, '檔案已上傳', '我已經把最新的報告上傳到資料夾了，請查收。', '2025-04-20 16:05:00', 1, 0),
(3, 2, 'RE: 檔案已上傳', '收到，謝謝你。', '2025-04-21 08:50:00', 1, 0),
(1, 2, '午餐？', '中午要不要一起去吃拉麵？', '2025-04-25 11:55:00', 0, 0),
(2, 1, 'RE: 午餐？', '可以啊，我知道附近有家不錯的店。', '2025-04-25 12:20:00', 0, 0),
(3, 1, '會議記錄', '這是今天的會議記錄，請確認內容是否正確。', '2025-04-30 17:00:00', 1, 0),
(1, 3, '提醒：報告截止日', '別忘了明天是報告繳交的最後期限。', '2025-05-06 08:30:00', 0, 0);

-- == 會員假資料資料 ==
INSERT INTO member_log (member_id, action, action_time) VALUES
(1, N'帳號註冊成功', '2025-03-08 00:00:00'),
(1, N'更新密碼失敗', '2025-03-08 15:00:00'),
(1, N'更新密碼成功', '2025-03-09 06:00:00'),
(1, N'登入成功', '2025-03-09 21:00:00'),
(1, N'登入失敗', '2025-03-10 12:00:00'),
(1, N'更新密碼失敗', '2025-03-11 03:00:00'),
(1, N'更新密碼成功', '2025-03-11 18:00:00'),
(1, N'登入成功', '2025-03-12 09:00:00'),
(1, N'登入失敗', '2025-03-13 00:00:00'),
(1, N'更新密碼失敗', '2025-03-13 15:00:00'),
(1, N'更新密碼成功', '2025-03-14 06:00:00'),
(1, N'登入成功', '2025-03-14 21:00:00'),
(1, N'登入失敗', '2025-03-15 12:00:00'),
(1, N'更新密碼失敗', '2025-03-16 03:00:00'),
(1, N'更新密碼成功', '2025-03-16 18:00:00'),
(1, N'登入成功', '2025-03-17 09:00:00'),
(1, N'登入失敗', '2025-03-18 00:00:00'),
(1, N'更新密碼失敗', '2025-03-18 15:00:00'),
(1, N'更新密碼成功', '2025-03-19 06:00:00'),
(1, N'登入成功', '2025-03-19 21:00:00'),
(1, N'登入失敗', '2025-03-20 12:00:00'),
(1, N'更新密碼失敗', '2025-03-21 03:00:00'),
(1, N'更新密碼成功', '2025-03-21 18:00:00'),
(1, N'登入成功', '2025-03-22 09:00:00'),
(1, N'登入失敗', '2025-03-23 00:00:00'),
(1, N'更新密碼失敗', '2025-03-23 15:00:00'),
(1, N'更新密碼成功', '2025-03-24 06:00:00'),
(1, N'登入成功', '2025-03-24 21:00:00'),
(1, N'登入失敗', '2025-03-25 12:00:00'),
(1, N'更新密碼失敗', '2025-03-26 03:00:00'),
(1, N'更新密碼成功', '2025-03-26 18:00:00'),
(1, N'登入成功', '2025-03-27 09:00:00'),
(1, N'登入失敗', '2025-03-28 00:00:00'),
(1, N'更新密碼失敗', '2025-03-28 15:00:00'),
(1, N'更新密碼成功', '2025-03-29 06:00:00'),
(1, N'登入成功', '2025-03-29 21:00:00'),
(1, N'登入失敗', '2025-03-30 12:00:00'),
(1, N'更新密碼失敗', '2025-03-31 03:00:00'),
(1, N'更新密碼成功', '2025-03-31 18:00:00'),
(1, N'登入成功', '2025-04-01 09:00:00'),
(1, N'登入失敗', '2025-04-02 00:00:00'),
(1, N'更新密碼失敗', '2025-04-02 15:00:00'),
(1, N'更新密碼成功', '2025-04-03 06:00:00'),
(1, N'登入成功', '2025-04-03 21:00:00'),
(1, N'登入失敗', '2025-04-04 12:00:00'),
(1, N'更新密碼失敗', '2025-04-05 03:00:00'),
(1, N'更新密碼成功', '2025-04-05 18:00:00'),
(1, N'登入成功', '2025-04-06 09:00:00'),
(1, N'登入失敗', '2025-04-07 00:00:00'),
(1, N'更新密碼失敗', '2025-04-07 15:00:00'),
(1, N'更新密碼成功', '2025-04-08 06:00:00');

INSERT INTO points_log (member_id, points_change, reason, created_at)
VALUES (1, 50, N'恭喜您註冊成功，獲得 50 點數', '2025-03-08 00:00:00'),
(1, 5, N'回覆文章獎勵！', '2025-03-08 12:00:00'),
(1, 10, N'新文章獎勵！', '2025-03-09 09:00:00'),
(1, -73, N'使用於訂單', '2025-03-10 14:00:00'),
(1, 30, N'每日登入回饋', '2025-03-11 07:00:00'),
(1, -144, N'購買裝備', '2025-03-11 21:00:00'),
(1, 22, N'購物回饋點數', '2025-03-12 16:00:00'),
(1, 5, N'回覆文章獎勵！', '2025-03-13 08:00:00'),
(1, 10, N'新文章獎勵！', '2025-03-13 20:00:00'),
(1, -88, N'使用於訂單', '2025-03-14 10:00:00'),
(1, 30, N'每日登入回饋', '2025-03-15 07:00:00'),
(1, -121, N'購買裝備', '2025-03-16 15:00:00'),
(1, 45, N'購物回饋點數', '2025-03-17 09:00:00'),
(1, 5, N'回覆文章獎勵！', '2025-03-17 22:00:00'),
(1, 10, N'新文章獎勵！', '2025-03-18 11:00:00'),
(1, -59, N'使用於訂單', '2025-03-19 08:00:00'),
(1, 30, N'每日登入回饋', '2025-03-20 06:00:00'),
(1, -184, N'購買裝備', '2025-03-21 13:00:00'),
(1, 38, N'購物回饋點數', '2025-03-22 09:00:00'),
(1, 5, N'回覆文章獎勵！', '2025-03-23 07:00:00'),
(1, 10, N'新文章獎勵！', '2025-03-24 12:00:00'),
(1, -94, N'使用於訂單', '2025-03-25 08:00:00'),
(1, 30, N'每日登入回饋', '2025-03-25 22:00:00'),
(1, -73, N'購買裝備', '2025-03-26 15:00:00'),
(1, 41, N'購物回饋點數', '2025-03-27 10:00:00'),
(1, 5, N'回覆文章獎勵！', '2025-03-28 08:00:00'),
(1, 10, N'新文章獎勵！', '2025-03-28 18:00:00'),
(1, -62, N'使用於訂單', '2025-03-29 14:00:00'),
(1, 30, N'每日登入回饋', '2025-03-30 06:00:00'),
(1, -152, N'購買裝備', '2025-03-31 16:00:00'),
(1, 33, N'購物回饋點數', '2025-04-01 08:00:00'),
(1, 5, N'回覆文章獎勵！', '2025-04-01 21:00:00'),
(1, 10, N'新文章獎勵！', '2025-04-02 11:00:00'),
(1, -58, N'使用於訂單', '2025-04-03 07:00:00'),
(1, 30, N'每日登入回饋', '2025-04-04 06:00:00'),
(1, -110, N'購買裝備', '2025-04-04 23:00:00'),
(1, 48, N'購物回饋點數', '2025-04-05 10:00:00'),
(1, 5, N'回覆文章獎勵！', '2025-04-05 20:00:00'),
(1, 10, N'新文章獎勵！', '2025-04-06 12:00:00'),
(1, -91, N'使用於訂單', '2025-04-06 23:00:00'),
(1, 30, N'每日登入回饋', '2025-04-07 08:00:00'),
(1, -64, N'購買裝備', '2025-04-07 18:00:00'),
(1, 28, N'購物回饋點數', '2025-04-08 08:00:00'),
(1, 5, N'回覆文章獎勵！', '2025-04-08 18:00:00'),
(1, 10, N'新文章獎勵！', '2025-04-08 20:00:00'),
(1, -85, N'使用於訂單', '2025-04-08 22:00:00'),
(1, 30, N'每日登入回饋', '2025-04-08 23:00:00'),
(1, -132, N'購買裝備', '2025-04-08 23:30:00'),
(1, 35, N'購物回饋點數', '2025-04-08 23:59:00');

INSERT INTO notification (member_id, type, message, status, value, sent_at) values
(1,'points','恭喜您註冊成功，獲得 50 點數', 1, null, '2025-03-08 12:00:00'),
(1,'mail','您收到一封新信件，寄件者： alan', 1, 13, '2025-03-30 17:00'),
(1,'mail','您收到一封新信件，寄件者： lily', 1, 12, '2025-03-25 12:20'),
(1,'mail','您收到一封新信件，寄件者： alan', 1, 7, '2025-03-13 18:20'),
(1,'mail','您收到一封新信件，寄件者： lily', 1, 6, '2025-03-09 14:45'),
(1,'mail','您收到一封新信件，寄件者： lily', 1, 3, '2025-03-11 11:30'),
(1,'avatar','成功購買裝備，來更新您的裝備吧!', 1, null, '2025-04-08 12:05:00'),
(1,'avatar','成功購買裝備，來更新您的裝備吧!', 1, null, '2025-03-28 12:00:00'),
(1,'avatar','成功購買裝備，來更新您的裝備吧!', 1, null, '2025-03-20 12:00:00'),
(1,'avatar','成功購買裝備，來更新您的裝備吧!', 1, null, '2025-03-11 12:00:00'),
(1,'avatar','成功購買裝備，來更新您的裝備吧!', 1, null, '2025-04-01 12:00:00'),
(1,'avatar','成功購買裝備，來更新您的裝備吧!', 1, null, '2025-04-02 12:00:00'),
(1,'avatar','成功購買裝備，來更新您的裝備吧!', 1, null, '2025-03-10 12:00:00'),
(1,'avatar','成功購買裝備，來更新您的裝備吧!', 1, null, '2025-03-09 12:00:00'),
(1,'avatar','成功購買裝備，來更新您的裝備吧!', 1, null, '2025-03-15 12:00:00'),
(1,'avatar','成功購買裝備，來更新您的裝備吧!', 1, null, '2025-03-29 12:00:00'),
(1,'avatar','成功購買裝備，來更新您的裝備吧!', 1, null, '2025-04-05 12:00:00'),
(1,'points','每日登入，獲得 30 點數', 1, null, '2025-03-08 12:00:00'),
(1,'points','每日登入，獲得 30 點數', 1, null, '2025-03-09 12:00:00'),
(1,'points','每日登入，獲得 30 點數', 1, null, '2025-03-11 12:00:00'),
(1,'points','每日登入，獲得 30 點數', 1, null, '2025-03-12 12:00:00'),
(1,'points','每日登入，獲得 30 點數', 1, null, '2025-03-15 12:00:00'),
(1,'points','每日登入，獲得 30 點數', 1, null, '2025-03-16 12:00:00'),
(1,'points','每日登入，獲得 30 點數', 1, null, '2025-03-17 12:00:00'),
(1,'points','每日登入，獲得 30 點數', 1, null, '2025-03-18 12:00:00'),
(1,'points','每日登入，獲得 30 點數', 1, null, '2025-03-20 12:00:00'),
(1,'points','每日登入，獲得 30 點數', 1, null, '2025-03-22 12:00:00'),
(1,'points','每日登入，獲得 30 點數', 1, null, '2025-03-23 12:00:00'),
(1,'points','每日登入，獲得 30 點數', 1, null, '2025-03-24 12:00:00'),
(1,'points','每日登入，獲得 30 點數', 1, null, '2025-03-27 12:00:00'),
(1,'points','每日登入，獲得 30 點數', 1, null, '2025-03-29 12:00:00'),
(1,'points','每日登入，獲得 30 點數', 1, null, '2025-04-02 12:00:00'),
(1,'points','每日登入，獲得 30 點數', 1, null, '2025-04-05 12:00:00'),
(1,'points','每日登入，獲得 30 點數', 1, null, '2025-04-06 12:00:00'),
(1,'points','每日登入，獲得 30 點數', 1, null, '2025-04-07 12:00:00'),
(1,'post','回覆有一則新留言。', 1, 1, '2025-03-11 12:00:00'),
(1,'post','回覆有一則新留言。', 1, 1, '2025-03-20 12:00:00'),
(1,'post','回覆有一則新留言。', 1, 1, '2025-03-28 12:00:00'),
(1,'post','回覆有一則新留言。', 1, 1, '2025-04-01 12:00:00'),
(1,'post','回覆有一則新留言。', 1, 1, '2025-04-02 12:00:00'),
(1,'post','回覆有一則新留言。', 1, 1, '2025-04-04 12:00:00'),
(1,'post','回覆有一則新留言。', 1, 1, '2025-04-07 12:00:00'),
(1,'coupon','已收到一張優惠券，到期日為：2025年5月8日，請盡快使用~ ', 1, null, '2025-03-08 12:00:00'),
(1,'coupon','已收到一張優惠券，到期日為：2025年6月1日，請盡快使用~ ', 1, 1, '2025-04-01 12:00:00'),
(1,'coupon','已收到一張優惠券，到期日為：2025年6月5日，請盡快使用~ ', 1, 1, '2025-04-05 12:00:00');
