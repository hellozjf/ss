DELETE FROM user;

INSERT INTO user (id, create_time, update_time, is_del, username, password, email, port) VALUES
(1, CURRENT_TIME(), CURRENT_TIME(), 'N', 'hellozjf', '123456', '908686171@qq.com', 10000);