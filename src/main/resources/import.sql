INSERT INTO user_role(name) VALUES('ROLE_USER');
INSERT INTO user_role(name) VALUES('ROLE_MANAGEMENT');
INSERT INTO user_role(name) VALUES('ROLE_ADMIN');
INSERT INTO user_role(name) VALUES('ROLE_OWNER');

insert into app_user (date_of_birth,email,first_name,last_name,middle_name,password,phone_number,status,role_id,username,enabled,locked)  values ('1999-12-02','niranjanthapa6441@gmail.com','Niranjan','Thapa','','$2a$10$d/4XH0CIbbUN0UtJhk7mT.jCFb/peVBIUgaQmg/G0Q2kkTQrBJUki','9810314784','ACTIVE',1,'user','true','true');
insert into app_user (date_of_birth,email,first_name,last_name,middle_name,password,phone_number,status,role_id,username,enabled,locked)  values ('1990-12-20','management@gmail.com','ABhisekh','Khadka','','$2a$10$YfrGSaiDIaIcwWP78wsWiuTlwabcaO5BCPj.Iryf0m4XdidTTjaaK','9810214384','ACTIVE',2,'management','true','true');
insert into app_user (date_of_birth,email,first_name,last_name,middle_name,password,phone_number,status,role_id,username,enabled,locked)  values ('1999-12-02','admin@gmail.com','Niranjan','Thapa','','$2a$10$d/4XH0CIbbUN0UtJhk7mT.jCFb/peVBIUgaQmg/G0Q2kkTQrBJUki','9811314784','ACTIVE',3,'admin','true','true');
insert into app_user (date_of_birth,email,first_name,last_name,middle_name,password,phone_number,status,role_id,username,enabled,locked)  values ('1999-12-02','owner@gmail.com','Niranjan','Thapa','','$2a$10$d/4XH0CIbbUN0UtJhk7mT.jCFb/peVBIUgaQmg/G0Q2kkTQrBJUki','9812314784','ACTIVE',4,'owner','true','true');

INSERT INTO event (title, description, date, start_time, end_time) VALUES ('Friday The 13th', 'One of a dance party where we gather to celebrate for a cause.', '2024-12-13', '18:00:00', '23:59:00');


INSERT INTO ticket (event_id, description, available_quantity, price, ticket_type) VALUES (1,'Rotarian',500,1000.00,'Rotarian');
INSERT INTO ticket (event_id, description, available_quantity, price, ticket_type) VALUES (1,'General Admission',500,500.00,'others');
