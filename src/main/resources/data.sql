DROP TABLE IF EXISTS energy_level;
DROP TABLE IF EXISTS quote;

CREATE TABLE quote (
      id BIGINT AUTO_INCREMENT PRIMARY KEY,
      bid DECIMAL NOT NULL,
      ask DECIMAL NOT NULL,
      isin VARCHAR(12)
);

CREATE TABLE energy_level (
       isin VARCHAR(12) PRIMARY KEY,
       elvl DECIMAL NOT NULL
);