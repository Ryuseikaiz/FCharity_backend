--CREATE DATABASE fcharity_database;
--USE fcharity_database;
--drop database fcharity_database;
-- Table: users
Create table categories(
	category_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
	category_name NVARCHAR(255)
)
create table tags(
	tag_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
	tag_name NVARCHAR(255),
)

create table wallets(
	wallet_id UNIQUEIDENTIFIER PRIMARY KEY,
	balance  DECIMAL(18, 2),
)
create table transaction_history(
    transaction_id UNIQUEIDENTIFIER PRIMARY KEY,
    wallet_id UNIQUEIDENTIFIER,
    amount  DECIMAL(18, 2),
    transaction_type NVARCHAR(50),
    transaction_date DATETIME,
    target_wallet_id UNIQUEIDENTIFIER,
    FOREIGN KEY (wallet_id) REFERENCES wallets(wallet_id) ON DELETE CASCADE,
    foreign key (target_wallet_id) REFERENCES wallets(wallet_id) ON DELETE NO ACTION
)
-- alter table wallets add balance DECIMAL(18, 2);
-- alter table transaction_history add amount DECIMAL(18, 2);
-- alter table transaction_history add target_wallet_id UNIQUEIDENTIFIER
-- alter table transaction_history add foreign key (target_wallet_id) REFERENCES wallets(wallet_id) ON DELETE NO ACTION
CREATE TABLE users (
    user_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),  
    full_name NVARCHAR(255),
    email NVARCHAR(255) UNIQUE,
    password NVARCHAR(255),
    phone_number NVARCHAR(15),
    address NVARCHAR(255),
    avatar NVARCHAR(255),
    user_role NVARCHAR(50),
    created_date DATETIME,
    verification_code NVARCHAR(255),
    verification_code_expires_at DATETIME,
	wallet_address UNIQUEIDENTIFIER,
    user_status NVARCHAR(50),
    reason NVARCHAR(MAX),
	FOREIGN KEY (wallet_address) REFERENCES wallets(wallet_id) ON DELETE CASCADE
);

CREATE TABLE organizations (
    organization_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    organization_name NVARCHAR(255),
    email NVARCHAR(255),
    phone_number NVARCHAR(15),
    address NVARCHAR(255),
	wallet_address UNIQUEIDENTIFIER,
    organization_description NVARCHAR(255),
    start_time DATETIME,
    shutdown_day DATETIME,
    organization_status NVARCHAR(50),
	ceo_id  UNIQUEIDENTIFIER,
     FOREIGN KEY (ceo_id) REFERENCES users(user_id) ON DELETE NO ACTION,
	 FOREIGN KEY (wallet_address) REFERENCES wallets(wallet_id) ON DELETE NO ACTION
);
alter table organizations add reason NVARCHAR(MAX);

-- Table: organization_members--edited
CREATE TABLE organization_members (
	membership_id  UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    user_id UNIQUEIDENTIFIER,
    organization_id UNIQUEIDENTIFIER,
    join_date DATETIME, 
    leave_date DATETIME,
	member_role NVARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (organization_id) REFERENCES organizations(organization_id) ON DELETE CASCADE
);
--new
CREATE TABLE organization_requests (
	organization_request_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
	user_id UNIQUEIDENTIFIER NOT NULL,
	organization_id UNIQUEIDENTIFIER NOT NULL,

	request_type NVARCHAR(50), -- CHECK (request_type IN ('Request', 'Invitation')) DEFAULT 'Request',
    status NVARCHAR(50), -- CHECK (status IN ('Pending', 'Approved', 'Rejected')) DEFAULT 'Pending',

	created_at DATETIME DEFAULT GETDATE(),
	updated_at DATETIME DEFAULT GETDATE(),

	FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
	FOREIGN KEY (organization_id) REFERENCES organizations(organization_id) ON DELETE CASCADE
);

CREATE TABLE help_requests (
    request_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    user_id UNIQUEIDENTIFIER,
    title NVARCHAR(255),
    content NVARCHAR(MAX),
    creation_date DATETIME,
    phone NVARCHAR(15),
    email NVARCHAR(255),
    location NVARCHAR(255),
    is_emergency BIT,
	category_id UNIQUEIDENTIFIER,
	status NVARCHAR(50),
	FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
alter table help_requests add reason NVARCHAR(MAX);
-- Table: projects
CREATE TABLE projects (
    project_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    project_name NVARCHAR(255),
    organization_id UNIQUEIDENTIFIER,
    leader_id UNIQUEIDENTIFIER,
    email NVARCHAR(255),
    phone_number NVARCHAR(15),
    project_description NVARCHAR(255),
    location NVARCHAR(255),
    project_status NVARCHAR(50),
    report_file NVARCHAR(255),
    planned_start_time DATETIME,
    planned_end_time DATETIME,
    actual_start_time DATETIME,
    actual_end_time DATETIME,
    shutdown_reason NVARCHAR(255),
    category_id UNIQUEIDENTIFIER,
    wallet_address UNIQUEIDENTIFIER,
    request_id UNIQUEIDENTIFIER,
    FOREIGN KEY (wallet_address) REFERENCES wallets(wallet_id) ON DELETE CASCADE,
    FOREIGN KEY (leader_id) REFERENCES users(user_id) ON DELETE NO ACTION,
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE CASCADE,
    FOREIGN KEY (organization_id) REFERENCES organizations(organization_id) ON DELETE CASCADE,
    FOREIGN KEY (request_id) REFERENCES help_requests(request_id) ON DELETE NO ACTION
);
alter table projects add created_at DATETIME DEFAULT GETDATE()
alter table projects add updated_at DATETIME DEFAULT GETDATE()
CREATE TABLE project_requests (
    project_request_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    user_id UNIQUEIDENTIFIER NOT NULL,
    project_id UNIQUEIDENTIFIER NOT NULL,
    request_type NVARCHAR(50),
    status NVARCHAR(50),
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (project_id) REFERENCES projects(project_id) ON DELETE NO ACTION -- Đảm bảo "ON DELETE CASCADE" ở đây
);

CREATE TABLE project_members (
    membership_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    user_id UNIQUEIDENTIFIER,
    project_id UNIQUEIDENTIFIER,
    join_date DATETIME,
    leave_date DATETIME,
    member_role CHAR(36),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (project_id) REFERENCES projects(project_id) ON DELETE NO ACTION
);

CREATE TABLE spending_plans (
     spending_plan_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
     project_id UNIQUEIDENTIFIER NOT NULL,
    plan_name NVARCHAR(255),
    description NVARCHAR(255),
     created_date DATETIME DEFAULT GETDATE(),
     updated_date DATETIME DEFAULT GETDATE(),
     min_required_donation_amount DECIMAL(18,2),
    estimated_total_cost DECIMAL(18, 2),
    approval_status NVARCHAR(50),
    FOREIGN KEY (project_id) REFERENCES projects(project_id) ON DELETE CASCADE
);
CREATE TABLE spending_items (
    spending_item_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    spending_plan_id UNIQUEIDENTIFIER NOT NULL,
    item_name NVARCHAR(255),
    estimated_cost DECIMAL(18,2),
    note NVARCHAR(255),
    created_date DATETIME DEFAULT GETDATE(),
 updated_date DATETIME DEFAULT GETDATE(),
FOREIGN KEY (spending_plan_id) REFERENCES spending_plans(spending_plan_id) ON DELETE CASCADE
);
CREATE TABLE spending_details (
                                  detail_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
                                  spending_item_id UNIQUEIDENTIFIER NOT NULL,
                                  amount DECIMAL(18,2),
                                  transaction_time DATETIME DEFAULT GETDATE(),
                                  description NVARCHAR(255),
                                  proof_image NVARCHAR(255),
                                  FOREIGN KEY (spending_item_id) REFERENCES spending_items(spending_item_id) ON DELETE CASCADE
);

-- Table: notifications
CREATE TABLE notifications (
    notification_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    user_id UNIQUEIDENTIFIER,
    message NVARCHAR(255),
    notification_date DATETIME,
    notification_status NVARCHAR(50),
    link NVARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Table: helpRequests--edited

--ALTER TABLE helpRequests ALTER COLUMN content NVARCHAR(MAX);

-- Table: timeline
CREATE TABLE timeline (
    phase_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    project_id UNIQUEIDENTIFIER,
    title NVARCHAR(255),
    start_time DATETIME,
    end_time DATETIME,
    content NVARCHAR(255),
    FOREIGN KEY (project_id) REFERENCES projects(project_id) ON DELETE CASCADE
);

--new
CREATE TABLE organization_images (
	organization_image_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
	organization_id UNIQUEIDENTIFIER,

	image_url NVARCHAR(255),
	image_type NVARCHAR(255),
	FOREIGN KEY (organization_id) REFERENCES organizations(organization_id) ON DELETE CASCADE
);
--new
CREATE TABLE project_images (
	project_image_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
	project_id UNIQUEIDENTIFIER,
	image_url NVARCHAR(255),
	image_type NVARCHAR(255),
	FOREIGN KEY (project_id) REFERENCES projects(project_id) ON DELETE CASCADE
);

-- ALTER TABLE object_attachments ADD comment_id UNIQUEIDENTIFIER;
-- ALTER TABLE object_attachments ADD FOREIGN KEY (comment_id) REFERENCES comments(comment_id);
--ALTER TABLE comments ADD vote int;
-- Table: task_plan

CREATE TABLE task_plan_status(
       status_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
       status_name NVARCHAR(255)
)
CREATE TABLE task_plan (
    task_plan_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    phase_id UNIQUEIDENTIFIER,
    user_id UNIQUEIDENTIFIER,
    task_name NVARCHAR(255),
    task_plan_description NVARCHAR(255),
    start_time DATETIME,
    end_time DATETIME,
    status_id UNIQUEIDENTIFIER,
    created_at DATETIME,
    updated_at DATETIME,
    parent_task_id UNIQUEIDENTIFIER,  -- Task cha (nếu có)
    FOREIGN KEY (parent_task_id) REFERENCES task_plan(task_plan_id) ON DELETE NO ACTION,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (phase_id) REFERENCES timeline(phase_id) ON DELETE NO ACTION,
    FOREIGN KEY (status_id) REFERENCES task_plan_status(status_id) ON DELETE CASCADE
);


-- Table: to_project_allocations
CREATE TABLE to_project_allocations (
    allocation_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
	 organization_id UNIQUEIDENTIFIER,
    project_id UNIQUEIDENTIFIER,
    allocation_status NVARCHAR(50),
    amount DECIMAL(18, 2),
    message NVARCHAR(255),
    allocation_time DATETIME,
    FOREIGN KEY (project_id) REFERENCES projects(project_id) ON DELETE CASCADE
);

-- Table: to_project_donations
CREATE TABLE to_project_donations (
    donation_id UNIQUEIDENTIFIER PRIMARY KEY,
    project_id UNIQUEIDENTIFIER,
    amount DECIMAL(18, 2),
    user_id UNIQUEIDENTIFIER,
    donation_status NVARCHAR(50),
    donation_time DATETIME,
    message NVARCHAR(255),
    FOREIGN KEY (project_id) REFERENCES projects(project_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE NO ACTION
);

-- Table: to_organization_donations
CREATE TABLE to_organization_donations (
    donation_id UNIQUEIDENTIFIER PRIMARY KEY,
    user_id UNIQUEIDENTIFIER,
    organization_id UNIQUEIDENTIFIER,
    amount DECIMAL(18, 2),
    donation_status NVARCHAR(50),
    donation_time DATETIME,
    message NVARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE NO ACTION,
    FOREIGN KEY (organization_id) REFERENCES organizations(organization_id) ON DELETE CASCADE
);

-- Table: posts
CREATE TABLE posts (
    post_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    user_id UNIQUEIDENTIFIER,
    title NVARCHAR(255),
    content NVARCHAR(MAX),
    vote INT,
    created_at DATETIME,
    updated_at DATETIME,
	post_status NVARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
alter table posts add reason NVARCHAR(MAX);
--new
CREATE TABLE post_votes (
    post_id     UNIQUEIDENTIFIER,
    user_id     UNIQUEIDENTIFIER,
    vote        INT CHECK (vote IN (-1, 0, 1)), -- -1: Downvote, 0: Bỏ vote, 1: Upvote
    created_at  DATETIME DEFAULT GETDATE(),
    updated_at  DATETIME DEFAULT GETDATE(),
    PRIMARY KEY (post_id, user_id),
    FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE NO ACTION
);
-- Table: comments---edited
CREATE TABLE comments (
    comment_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    post_id UNIQUEIDENTIFIER,
    user_id UNIQUEIDENTIFIER,
	vote int,
    content NVARCHAR(MAX),
    created_at DATETIME,
    updated_at DATETIME,
	parent_comment_id UNIQUEIDENTIFIER,
    FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE NO ACTION,
	FOREIGN KEY (parent_comment_id) REFERENCES comments(comment_id) ON DELETE NO ACTION
);
--new 
CREATE TABLE comment_votes (
    comment_id     UNIQUEIDENTIFIER,
    user_id     UNIQUEIDENTIFIER,
    vote        INT CHECK (vote IN (-1, 0, 1)), -- -1: Downvote, 0: Bỏ vote, 1: Upvote
    created_at  DATETIME DEFAULT GETDATE(),
    updated_at  DATETIME DEFAULT GETDATE(),
    PRIMARY KEY (comment_id, user_id),
    FOREIGN KEY (comment_id) REFERENCES comments(comment_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE NO ACTION
);


CREATE TABLE taggable (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    tag_id UNIQUEIDENTIFIER NOT NULL,
    taggable_id UNIQUEIDENTIFIER NOT NULL,
    taggable_type NVARCHAR(255) NOT NULL,
    FOREIGN KEY (tag_id) REFERENCES tags(tag_id) ON DELETE CASCADE
);


-- Table: object_attachments --edited
CREATE TABLE object_attachments (
    image_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    url NVARCHAR(255),
    help_request_id UNIQUEIDENTIFIER,
    phase_id UNIQUEIDENTIFIER,
    post_id UNIQUEIDENTIFIER,
    comment_id UNIQUEIDENTIFIER,
    FOREIGN KEY (help_request_id) REFERENCES help_requests(request_id)  ON DELETE CASCADE,
    FOREIGN KEY (phase_id) REFERENCES timeline(phase_id)  ON DELETE NO ACTION,
    FOREIGN KEY (post_id) REFERENCES posts(post_id)  ON DELETE NO ACTION,
    FOREIGN KEY (comment_id) REFERENCES comments(comment_id)  ON DELETE CASCADE
);
-- Table: reports --dừng ở đây
---new
CREATE TABLE post_reports (
    report_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    reporter_id UNIQUEIDENTIFIER,
	post_id UNIQUEIDENTIFIER,
    reason NVARCHAR(255),
    report_date DATETIME,
    FOREIGN KEY (reporter_id) REFERENCES users(user_id) ON DELETE CASCADE,
	FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE NO ACTION,
);
CREATE TABLE project_reports (
    report_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    reporter_id UNIQUEIDENTIFIER,
    project_id UNIQUEIDENTIFIER,
    reason NVARCHAR(255),
    report_date DATETIME,
    FOREIGN KEY (reporter_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (project_id) REFERENCES projects(project_id) ON DELETE NO ACTION
);


INSERT INTO task_plan_status (status_id, status_name)
VALUES (NEWID(), N'TODO');

INSERT INTO task_plan_status (status_id, status_name)
VALUES (NEWID(), N'IN PROGRESS');

INSERT INTO task_plan_status (status_id, status_name)
VALUES (NEWID(), N'DONE');


-- Inserting categories into the database
INSERT INTO categories (category_name)
VALUES 
    ('Medical'),
    ('Memorial'),
    ('Emergency'),
    ('Nonprofit'),
    ('Education'),
    ('Animal'),
    ('Environment'),
    ('Business'),
    ('Community'),
    ('Competition'),
    ('Creative'),
    ('Event'),
    ('Faith'),
    ('Family'),
    ('Sports'),
    ('Travel'),
    ('Volunteer'),
    ('Wishes');

	-- Inserting tags into the database
INSERT INTO tags (tag_name)
VALUES 
    ('Wildfire'),
    ('Flood'),
    ('Earthquake'),
    ('Hurricane'),
    ('Tornado'),
    ('Drought'),
    ('Pandemic'),
    ('Medical Emergency'),
    ('Refugee Crisis'),
    ('Food Shortage'),
    ('Water Crisis'),
    ('Homeless Support'),
    ('Accident Relief'),
    ('Animal Rescue'),
    ('Environmental Disaster'),
    ('Community Crisis'),
    ('Education Support'),
    ('Infrastructure Damage');


INSERT INTO wallets(wallet_id, balance) VALUES
                                            ('590396FB-F717-4BE6-B22C-306119606188', 0),
                                            ('E273F776-E6B0-4DB9-AAE8-337F5B0E416F', 0),
                                            ('757FF78E-B478-4196-BA9A-479D911B5DFB', 0),
                                            ('BC4922FB-5B9D-410A-A662-4FFAFB3CDD13', 0),
                                            ('A836A23C-35C9-4C52-A86C-640EE3A2E066', 0),
                                            ('D3B77B59-B475-45D4-8BC5-65F93F1F4D15', 0),
                                            ('12808576-B32A-4C0B-893D-7C6350C24F0E', 0),
                                            ('34FC7236-5060-4077-9503-B8B94A574CFE', 0),
                                            ('F0AF45ED-DBC8-4E23-AA74-F634BBA1628F', 0);

-- Update bảng users để gán wallet_address
UPDATE users SET wallet_address = '590396FB-F717-4BE6-B22C-306119606188' WHERE user_id = '590396FB-F717-4BE6-B22C-306119606188';
UPDATE users SET wallet_address = 'E273F776-E6B0-4DB9-AAE8-337F5B0E416F' WHERE user_id = 'E273F776-E6B0-4DB9-AAE8-337F5B0E416F';
UPDATE users SET wallet_address = '757FF78E-B478-4196-BA9A-479D911B5DFB' WHERE user_id = '757FF78E-B478-4196-BA9A-479D911B5DFB';
UPDATE users SET wallet_address = 'BC4922FB-5B9D-410A-A662-4FFAFB3CDD13' WHERE user_id = 'BC4922FB-5B9D-410A-A662-4FFAFB3CDD13';
UPDATE users SET wallet_address = 'A836A23C-35C9-4C52-A86C-640EE3A2E066' WHERE user_id = 'A836A23C-35C9-4C52-A86C-640EE3A2E066';
UPDATE users SET wallet_address = 'D3B77B59-B475-45D4-8BC5-65F93F1F4D15' WHERE user_id = 'D3B77B59-B475-45D4-8BC5-65F93F1F4D15';
UPDATE users SET wallet_address = '12808576-B32A-4C0B-893D-7C6350C24F0E' WHERE user_id = '12808576-B32A-4C0B-893D-7C6350C24F0E';
UPDATE users SET wallet_address = '34FC7236-5060-4077-9503-B8B94A574CFE' WHERE user_id = '34FC7236-5060-4077-9503-B8B94A574CFE';
UPDATE users SET wallet_address = 'F0AF45ED-DBC8-4E23-AA74-F634BBA1628F' WHERE user_id = 'F0AF45ED-DBC8-4E23-AA74-F634BBA1628F';


INSERT INTO project_members (user_id, project_id, join_date, member_role)
VALUES
    ('590396FB-F717-4BE6-B22C-306119606188', 'CEB2685C-2963-4B02-97E8-205ADAE6A34F', '2025-04-02 17:00:00.000', 'LEADER'),
    ('F0AF45ED-DBC8-4E23-AA74-F634BBA1628F', 'ECB848DD-7094-415A-926C-2DEF392E43CF', '2025-04-02 17:00:00.000', 'LEADER'),
    ('E273F776-E6B0-4DB9-AAE8-337F5B0E416F', '53B0DD0E-731F-462E-A4A8-95DC1F878870', '2025-04-02 17:00:00.000', 'LEADER');

insert into organization_members (membership_id, user_id, organization_id, join_date, leave_date, member_role)
values (NEWID(), 'E09BE8D1-BA6D-4178-8BF4-2650E337FE7B', '4F6B0E2D-8C3E-4A2A-BB60-2D9D5F7A9C16', GETDATE(), null, 'CEO'),
(NEWID(), 'E09BE8D1-BA6D-4178-8BF4-2650E337FE7E', '4F6B0E2D-8C3E-4A2A-BB60-2D9D5F7A9C16', GETDATE(), null, 'MEMBER'),
(NEWID(), 'A3F8D2B9-4C6E-43F1-9B27-8D5C6A1E2F78', '4F6B0E2D-8C3E-4A2A-BB60-2D9D5F7A9C16', GETDATE(), null, 'MEMBER');