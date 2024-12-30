    USE TEST;
    CREATE TABLE IF NOT EXISTS `TEACHERS` (
      `id` INT PRIMARY KEY AUTO_INCREMENT,
      `last_name` VARCHAR(40),
      `first_name` VARCHAR(40),
      `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

    CREATE TABLE IF NOT EXISTS `SESSIONS` (
      `id` INT PRIMARY KEY AUTO_INCREMENT,
      `name` VARCHAR(50),
      `description` VARCHAR(2000),
      `date` DATETIME,
      `teacher_id` INT,
      `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
      FOREIGN KEY (`teacher_id`) REFERENCES `TEACHERS` (`id`)
    );

    CREATE TABLE IF NOT EXISTS `USERS` (
      `id` INT PRIMARY KEY AUTO_INCREMENT,
      `last_name` VARCHAR(40),
      `first_name` VARCHAR(40),
      `admin` BOOLEAN DEFAULT false,
      `email` VARCHAR(255),
      `password` VARCHAR(255),
      `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

    CREATE TABLE IF NOT EXISTS `PARTICIPATE` (
      `user_id` INT,
      `session_id` INT,
      FOREIGN KEY (`user_id`) REFERENCES `USERS` (`id`),
      FOREIGN KEY (`session_id`) REFERENCES `SESSIONS` (`id`)
    );

    INSERT INTO TEACHERS (first_name, last_name)
    VALUES ('Margot', 'DELAHAYE'),
           ('Hélène', 'THIERCELIN');


