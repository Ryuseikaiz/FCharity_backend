--CREATE DATABASE fcharity_database;
--USE fcharity_database;

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
	balance NVARCHAR(255)
)
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
	FOREIGN KEY (wallet_address) REFERENCES wallets(wallet_id)
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
     FOREIGN KEY (ceo_id) REFERENCES users(user_id),
	 FOREIGN KEY (wallet_address) REFERENCES wallets(wallet_id)
);

-- Table: organization_members
CREATE TABLE organization_members (
	membership_id  UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    user_id UNIQUEIDENTIFIER,
    organization_id UNIQUEIDENTIFIER,
    join_date DATETIME, 
    leave_date DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (organization_id) REFERENCES organizations(organization_id)
);


-- Table: projects
CREATE TABLE projects (
    project_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    project_name NVARCHAR(255),
	organization_id UNIQUEIDENTIFIER,
    leader_id UNIQUEIDENTIFIER,
    email NVARCHAR(255),
    phone_number NVARCHAR(15),
    project_description NVARCHAR(255),
    project_status NVARCHAR(50),
    report_file NVARCHAR(255),
    planned_start_time DATETIME,
    planned_end_time DATETIME,
    actual_start_time DATETIME,
    actual_end_time DATETIME,
    shutdown_reason NVARCHAR(255),
	category_id UNIQUEIDENTIFIER,
	wallet_address UNIQUEIDENTIFIER,
	FOREIGN KEY (wallet_address) REFERENCES wallets(wallet_id),
    FOREIGN KEY (leader_id) REFERENCES users(user_id),
    FOREIGN KEY (category_id) REFERENCES categories(category_id),
	 FOREIGN KEY (organization_id) REFERENCES organizations(organization_id),
);


-- Table: project_members
CREATE TABLE project_members (
    membership_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    user_id UNIQUEIDENTIFIER,
    project_id UNIQUEIDENTIFIER,
    join_date DATETIME, 
    leave_date DATETIME,
    member_role CHAR(36),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (project_id) REFERENCES projects(project_id)
);

-- Table: notifications
CREATE TABLE notifications (
    notification_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    user_id UNIQUEIDENTIFIER,
    message NVARCHAR(255),
    notification_date DATETIME,
    notification_status NVARCHAR(50),
    link NVARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Table: requests
CREATE TABLE requests (
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
	FOREIGN KEY (category_id) REFERENCES categories(category_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
--ALTER TABLE requests ALTER COLUMN content NVARCHAR(MAX);

-- Table: timeline
CREATE TABLE timeline (
    phase_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    project_id UNIQUEIDENTIFIER,
    title NVARCHAR(255),
    start_time DATETIME,
    end_time DATETIME,
    content NVARCHAR(255),
    FOREIGN KEY (project_id) REFERENCES projects(project_id)
);

-- Table: object_images
CREATE TABLE object_attachments (
    image_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    url NVARCHAR(255),
    request_id UNIQUEIDENTIFIER,
    project_id UNIQUEIDENTIFIER,
	organization_id UNIQUEIDENTIFIER,
    phase_id UNIQUEIDENTIFIER,
    post_id UNIQUEIDENTIFIER,
    FOREIGN KEY (request_id) REFERENCES requests(request_id),
    FOREIGN KEY (phase_id) REFERENCES timeline(phase_id),
    FOREIGN KEY (post_id) REFERENCES posts(post_id),
    FOREIGN KEY (project_id) REFERENCES projects(project_id),
    FOREIGN KEY (organization_id) REFERENCES organizations(organization_id)
);
ALTER TABLE object_attachments ADD comment_id UNIQUEIDENTIFIER;
ALTER TABLE object_attachments ADD FOREIGN KEY (comment_id) REFERENCES comments(comment_id);
-- Table: task_plan
CREATE TABLE task_plan (
    task_plan_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    project_id UNIQUEIDENTIFIER,
    user_id UNIQUEIDENTIFIER,
    task_name NVARCHAR(255),
    task_plan_description NVARCHAR(255),
    start_time DATETIME,
    end_time DATETIME,
    task_plan_status NVARCHAR(50),
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (project_id) REFERENCES projects(project_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Table: sub_tasks
CREATE TABLE sub_tasks (
    sub_task_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    task_plan_id UNIQUEIDENTIFIER,
    sub_task_name NVARCHAR(255),
    user_id UNIQUEIDENTIFIER,
    sub_task_description NVARCHAR(255),
    start_time DATETIME,
    end_time DATETIME,
    sub_task_status NVARCHAR(50),
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (task_plan_id) REFERENCES task_plan(task_plan_id)
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
    FOREIGN KEY (project_id) REFERENCES projects(project_id)
);

-- Table: to_project_donations
CREATE TABLE to_project_donations (
    donation_id UNIQUEIDENTIFIER PRIMARY KEY,
    project_id UNIQUEIDENTIFIER,
    user_id UNIQUEIDENTIFIER,
    donation_status NVARCHAR(50),
    donation_time DATETIME,
    message NVARCHAR(255),
    FOREIGN KEY (project_id) REFERENCES projects(project_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Table: to_organization_donations
CREATE TABLE to_organization_donations (
    donation_id UNIQUEIDENTIFIER PRIMARY KEY,
    user_id UNIQUEIDENTIFIER,
    organization_id UNIQUEIDENTIFIER,
    donation_status NVARCHAR(50),
    donation_time DATETIME,
    message NVARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (organization_id) REFERENCES organizations(organization_id)
);

-- Table: posts
CREATE TABLE posts (
    post_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    user_id UNIQUEIDENTIFIER,
    title NVARCHAR(255),
    content NVARCHAR(255),
    vote INT,
    created_at DATETIME,
    updated_at DATETIME,
	post_status NVARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Junction table for many-to-many relationship
CREATE TABLE taggable (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    tag_id UNIQUEIDENTIFIER NOT NULL,
    taggable_id UNIQUEIDENTIFIER NOT NULL,
    taggable_type NVARCHAR(255) NOT NULL,
    FOREIGN KEY (tag_id) REFERENCES tags(tag_id) ON DELETE CASCADE
);

-- Table: comments
CREATE TABLE comments (
    comment_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    post_id UNIQUEIDENTIFIER,
    user_id UNIQUEIDENTIFIER,
    content NVARCHAR(255),
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (post_id) REFERENCES posts(post_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Table: reports
CREATE TABLE reports (
    report_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    reporter_id UNIQUEIDENTIFIER,
    project_id UNIQUEIDENTIFIER,
	post_id UNIQUEIDENTIFIER,
    reason NVARCHAR(255),
    report_date DATETIME,
    FOREIGN KEY (reporter_id) REFERENCES users(user_id),
	FOREIGN KEY (post_id) REFERENCES posts(post_id),
    FOREIGN KEY (project_id) REFERENCES projects(project_id)
);

CREATE TABLE proof_images (
    image_id CHAR(36) PRIMARY KEY DEFAULT NEWID(),
    image_url NVARCHAR(255),
    image_type NVARCHAR(20),
    to_project_allocation_id UNIQUEIDENTIFIER,
    to_project_donation_id UNIQUEIDENTIFIER,
    FOREIGN KEY (to_project_donation_id) REFERENCES to_project_donations(donation_id),
    FOREIGN KEY (to_project_allocation_id) REFERENCES to_project_allocations(allocation_id)
);

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
