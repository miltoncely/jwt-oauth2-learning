-- Admin user with both ADMIN and USER roles
INSERT INTO users (username, password, roles) VALUES ('admin', '{noop}123456', 'ADMIN,USER');

-- Regular user with only USER role
INSERT INTO users (username, password, roles) VALUES ('user', '{noop}password', 'USER');
