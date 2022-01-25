-- phpMyAdmin SQL Dump
-- version 5.1.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jan 25, 2022 at 12:57 PM
-- Server version: 10.4.19-MariaDB
-- PHP Version: 7.3.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `task_manager_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `active_tasks`
--

CREATE TABLE `active_tasks` (
  `id` bigint(20) NOT NULL,
  `task_id` bigint(20) NOT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `active_tasks`
--

INSERT INTO `active_tasks` (`id`, `task_id`, `start_time`, `end_time`) VALUES
(1, 1, NOW(), NULL);

-- --------------------------------------------------------

--
-- Table structure for table `roles`
--

CREATE TABLE `roles` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `authority` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `roles`
--

INSERT INTO `roles` (`id`, `name`, `authority`) VALUES
(1, 'user', 'ROLE_USER'),
(2, 'admin', 'ROLE_ADMIN');

-- --------------------------------------------------------

--
-- Table structure for table `statuses`
--

CREATE TABLE `statuses` (
  `id` bigint(20) NOT NULL,
  `status` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `statuses`
--

INSERT INTO `statuses` (`id`, `status`) VALUES
(1, 'ENTERING'),
(2, 'IN_PROGRESS'),
(3, 'READY_FOR_REVIEW'),
(4, 'COMPLETE');

-- --------------------------------------------------------

--
-- Table structure for table `tasks`
--

CREATE TABLE `tasks` (
  `id` bigint(20) NOT NULL,
  `name` varchar(50) NOT NULL,
  `modifiable` tinyint(1) NOT NULL DEFAULT 1,
  `description` text NOT NULL DEFAULT '',
  `created_by` bigint(20) NOT NULL,
  `created_time` datetime NOT NULL DEFAULT current_timestamp(),
  `modified_time` datetime NOT NULL DEFAULT current_timestamp(),
  `status` bigint(20) NOT NULL DEFAULT 1,
  `due_time` datetime DEFAULT NULL,
  `completed_time` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `tasks`
--

INSERT INTO `tasks` (`id`, `name`, `modifiable`, `description`, `created_by`, `created_time`, `modified_time`, `status`, `due_time`, `completed_time`) VALUES
(1, 'No work', 0, '', 3, NOW(), NOW(), 2, NULL, NULL);

--
-- Triggers `tasks`
--
DELIMITER $$
CREATE TRIGGER `on_tasks_table_change_trigger` BEFORE UPDATE ON `tasks` FOR EACH ROW BEGIN
SET NEW.modified_time=NOW();
IF NEW.modifiable = 1 AND NEW.status >= 3 AND OLD.completed_time IS NULL THEN
SET NEW.completed_time=NOW();
END IF;
IF NEW.modifiable = 1 AND NEW.status <= 2 AND OLD.completed_time IS NOT NULL THEN
SET NEW.completed_time=NULL;
END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `task_comments`
--

CREATE TABLE `task_comments` (
  `id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `task_id` bigint(20) NOT NULL,
  `comment` text NOT NULL,
  `creation_time` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


--
-- Table structure for table `task_history`
--

CREATE TABLE `task_history` (
  `id` bigint(20) NOT NULL,
  `datetime` datetime NOT NULL DEFAULT current_timestamp(),
  `user_id` bigint(20) NOT NULL,
  `task_id` bigint(20) NOT NULL,
  `action` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` bigint(20) NOT NULL,
  `name` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `name`, `password`) VALUES
(1, 'admin', '$2a$10$EDkvuMajqnFevViFy82RXOqMJuHIsRqNs39t95.LRfJ.c3DhWaupO');

--
-- Triggers `users`
--
DELIMITER $$
CREATE TRIGGER `on_users_table_delete_trigger` BEFORE DELETE ON `users` FOR EACH ROW DELETE from users_roles WHERE user_id = OLD.id
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `users_roles`
--

CREATE TABLE `users_roles` (
  `id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `users_roles`
--

INSERT INTO `users_roles` (`id`, `user_id`, `role_id`) VALUES
(1, 1, 1),
(2, 1, 2);

-- --------------------------------------------------------

--
-- Table structure for table `users_tasks`
--

CREATE TABLE `users_tasks` (
  `id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `task_id` bigint(20) NOT NULL,
  `parent_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `users_tasks`
--

INSERT INTO `users_tasks` (`id`, `user_id`, `task_id`, `parent_id`) VALUES
(1, 1, 1, NULL);

--
-- Triggers `users_tasks`
--
DELIMITER $$
CREATE TRIGGER `on_users_tasks_table_delete_trigger` BEFORE DELETE ON `users_tasks` FOR EACH ROW BEGIN
/*IF (SELECT COUNT(id) FROM users_tasks WHERE task_id = OLD.task_id) = 0 THEN
DELETE FROM tasks WHERE id = OLD.task_id AND modifiable = 1;
END IF;*/
DELETE FROM active_tasks WHERE task_id = OLD.id;
END
$$
DELIMITER ;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `active_tasks`
--
ALTER TABLE `active_tasks`
  ADD PRIMARY KEY (`id`),
  ADD KEY `task_id` (`task_id`);

--
-- Indexes for table `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `authority` (`authority`);

--
-- Indexes for table `statuses`
--
ALTER TABLE `statuses`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `tasks`
--
ALTER TABLE `tasks`
  ADD PRIMARY KEY (`id`),
  ADD KEY `tasks_statuses_id_fk` (`status`),
  ADD KEY `tasks_users_id_fk` (`created_by`);

--
-- Indexes for table `task_comments`
--
ALTER TABLE `task_comments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `task_comments_tasks_id_fk` (`task_id`),
  ADD KEY `task_comments_users_id_fk` (`user_id`);

--
-- Indexes for table `task_history`
--
ALTER TABLE `task_history`
  ADD PRIMARY KEY (`id`),
  ADD KEY `task_history_tasks_id_fk` (`task_id`),
  ADD KEY `task_history_users_id_fk` (`user_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `users_name_uindex` (`name`);

--
-- Indexes for table `users_roles`
--
ALTER TABLE `users_roles`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `role_id` (`role_id`);

--
-- Indexes for table `users_tasks`
--
ALTER TABLE `users_tasks`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `task_id` (`task_id`),
  ADD KEY `users_tasks_users_tasks_id_fk` (`parent_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `active_tasks`
--
ALTER TABLE `active_tasks`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=53;

--
-- AUTO_INCREMENT for table `roles`
--
ALTER TABLE `roles`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `statuses`
--
ALTER TABLE `statuses`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `tasks`
--
ALTER TABLE `tasks`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=47;

--
-- AUTO_INCREMENT for table `task_comments`
--
ALTER TABLE `task_comments`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `task_history`
--
ALTER TABLE `task_history`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=33;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `users_roles`
--
ALTER TABLE `users_roles`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `users_tasks`
--
ALTER TABLE `users_tasks`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=43;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `active_tasks`
--
ALTER TABLE `active_tasks`
  ADD CONSTRAINT `active_tasks_ibfk_1` FOREIGN KEY (`task_id`) REFERENCES `users_tasks` (`id`);

--
-- Constraints for table `tasks`
--
ALTER TABLE `tasks`
  ADD CONSTRAINT `tasks_statuses_id_fk` FOREIGN KEY (`status`) REFERENCES `statuses` (`id`),
  ADD CONSTRAINT `tasks_users_id_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

--
-- Constraints for table `task_comments`
--
ALTER TABLE `task_comments`
  ADD CONSTRAINT `task_comments_tasks_id_fk` FOREIGN KEY (`task_id`) REFERENCES `tasks` (`id`),
  ADD CONSTRAINT `task_comments_users_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `task_history`
--
ALTER TABLE `task_history`
  ADD CONSTRAINT `task_history_tasks_id_fk` FOREIGN KEY (`task_id`) REFERENCES `tasks` (`id`),
  ADD CONSTRAINT `task_history_users_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `users_roles`
--
ALTER TABLE `users_roles`
  ADD CONSTRAINT `users_roles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `users_roles_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`);

--
-- Constraints for table `users_tasks`
--
ALTER TABLE `users_tasks`
  ADD CONSTRAINT `users_tasks_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `users_tasks_ibfk_2` FOREIGN KEY (`task_id`) REFERENCES `tasks` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
