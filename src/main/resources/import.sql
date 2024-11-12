INSERT INTO user_role(name) VALUES('ROLE_USER');
INSERT INTO user_role(name) VALUES('ROLE_MANAGEMENT');
INSERT INTO user_role(name) VALUES('ROLE_ADMIN');
INSERT INTO user_role(name) VALUES('ROLE_OWNER');

insert into app_user (date_of_birth,email,first_name,last_name,middle_name,password,phone_number,status,role_id,username,enabled,locked)  values ('1999-12-02','niranjanthapa6441@gmail.com','Niranjan','Thapa','','$2a$10$d/4XH0CIbbUN0UtJhk7mT.jCFb/peVBIUgaQmg/G0Q2kkTQrBJUki','9810314784','ACTIVE',1,'niranjan','true','true');
insert into app_user (date_of_birth,email,first_name,last_name,middle_name,password,phone_number,status,role_id,username,enabled,locked)  values ('1990-12-20','abhisekhKhadka@gmail.com','ABhisekh','Khadka','','$2a$10$YfrGSaiDIaIcwWP78wsWiuTlwabcaO5BCPj.Iryf0m4XdidTTjaaK','9810214384','ACTIVE',2,'abhisekh','true','true');

INSERT INTO food_category (name) VALUES ('Starter');
INSERT INTO food_category (name) VALUES ('Main Course');
INSERT INTO food_category (name) VALUES ('Salad');
INSERT INTO food_category (name) VALUES ('Dessert');

INSERT INTO food_sub_category (name, category_id) VALUES ('Veg', 1);
INSERT INTO food_sub_category (name, category_id) VALUES ('Paneer and Cheese', 1);
INSERT INTO food_sub_category (name, category_id) VALUES ('Soup', 1);
INSERT INTO food_sub_category (name, category_id) VALUES ('Non-Veg', 1);
INSERT INTO food_sub_category (name, category_id) VALUES ('Rice', 2);
INSERT INTO food_sub_category (name, category_id) VALUES ('Bread and Pasta', 2);
INSERT INTO food_sub_category (name, category_id) VALUES ('Dal', 2);
INSERT INTO food_sub_category (name, category_id) VALUES ('Vegetable Dish', 2);
INSERT INTO food_sub_category (name, category_id) VALUES ('Paneer', 2);
INSERT INTO food_sub_category (name, category_id) VALUES ('Mushroom', 2);
INSERT INTO food_sub_category (name, category_id) VALUES ('Meat', 2);
INSERT INTO food_sub_category (name, category_id) VALUES ('Achar', 2);
INSERT INTO food_sub_category (name, category_id) VALUES ('Salad', 3);
INSERT INTO food_sub_category (name, category_id) VALUES ('Dessert', 4);

