-- Table Access.Entries
CREATE TABLE Access.Entries (
  id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  datetime DATETIME(3) NOT NULL,
  ip VARCHAR(20) NOT NULL,
  request VARCHAR(20) NOT NULL,
  status INTEGER NOT NULL,
  client VARCHAR(500) NOT NULL,

  UNIQUE KEY UK_Entries (datetime, ip)

) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8;

-- Table Access.BlockedIPs
CREATE TABLE Access.BlockedIPs (
  id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  ip VARCHAR(20) NOT NULL,
  startDateTime DATETIME(3) NOT NULL,
  endDateTime DATETIME(3) NOT NULL,
  threshold INTEGER NOT NULL,
  comment VARCHAR(500) NOT NULL,

  UNIQUE KEY UK_BlockedIps (ip, startDateTime, endDateTime, threshold)

) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8;