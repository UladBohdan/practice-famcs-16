SELECT * FROM users WHERE ( SELECT COUNT(*) FROM messages WHERE user_id = users.id ) > 3
