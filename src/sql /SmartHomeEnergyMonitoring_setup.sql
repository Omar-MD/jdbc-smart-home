DROP DATABASE SmartHomeEnergyMonitoring;
CREATE DATABASE IF NOT EXISTS SmartHomeEnergyMonitoring;
USE SmartHomeEnergyMonitoring;

CREATE TABLE device_type (
  device_type_id INT AUTO_INCREMENT UNIQUE PRIMARY KEY,
  device_type_category VARCHAR(100),
  device_type_name VARCHAR(100)
);

CREATE TABLE device_info (
  device_info_id INT AUTO_INCREMENT UNIQUE PRIMARY KEY,
  device_type_id INT,
  device_name VARCHAR(100),
  device_description TEXT,
  device_range INT DEFAULT 1,	 				# Range in meters
  device_communication VARCHAR(100),			# Communication Type
  device_dataRate VARCHAR(100),					# Mbit/Kbit/Gbit
  device_security VARCHAR(100),
  device_softwareVersion VARCHAR(100),
  device_storageCapacity VARCHAR(100),
  device_energyRating VARCHAR(100),
  device_activePower DOUBLE DEFAULT 0.0,							# Power when active,  KWatt
  device_standbyPower DOUBLE DEFAULT 0.0,							# Power when standby, KWatt
  device_registeredOn DATETIME DEFAULT NOW(),					
  FOREIGN KEY (device_type_id) REFERENCES device_type(device_type_id)
);

CREATE TABLE device (
  device_id INT AUTO_INCREMENT UNIQUE PRIMARY KEY,
  device_info_id INT,
  device_location VARCHAR(255),
  device_parameters TEXT,
  is_active BOOLEAN DEFAULT FALSE,
  active_from TIMESTAMP,
  active_to TIMESTAMP,
  FOREIGN KEY (device_info_id) REFERENCES device_info(device_info_id)
);

CREATE TABLE device_msg_log (
  device_msg_id INT AUTO_INCREMENT UNIQUE PRIMARY KEY,
  device_msg_type VARCHAR(255),
  device_id INT,
  device_msg TEXT,
  createdOn DATETIME DEFAULT NOW(),
  is_read BOOLEAN DEFAULT FALSE,
  FOREIGN KEY (device_id) REFERENCES device(device_id)
);

CREATE TABLE device_energy_log (
  device_log_id INT AUTO_INCREMENT UNIQUE PRIMARY KEY,
  device_log_date DATE DEFAULT (CURDATE()),
  device_id INT,
  hours_active DOUBLE DEFAULT 0.0,
  hours_inactive DOUBLE DEFAULT 0.0,
  energy_consumed DOUBLE DEFAULT 0.0,
  FOREIGN KEY (device_id) REFERENCES device(device_id)
);

SELECT 'INSERTING DATA INTO DATABASE' as 'INFO';

INSERT INTO device_type (
	device_type_category, 
    device_type_name
) VALUES
('Thermostats', 'Smart Thermostat'),					# Heating
('Lighting', 'Smart Light Switch'),						# Lighting
('Environmental Sensors', 'Air Quality Monitor'),		# Air Quality
('Climate Control', 'Smart Air Conditioner'),	
('Climate Control', 'Smart Fan'),
('Security', 'Smart Camera'),							# Security
('Security', 'Smart Doorbell'),
('Security', 'Smart Lock'),
('Security', 'Smart Blind'),
('Appliances', 'Smart Refrigerator'),					# Appliances
('Appliances', 'Smart Oven'),
('Appliances', 'Smart Coffee Maker'),
('Appliances', 'Smart Washer & Dryer'),					
('Entertainment', 'Smart TV'),							# Entertainment
('Entertainment', 'Smart SoundBar'),
('Entertainment', 'Smart Speaker'),
('Irrigation and Gardening', 'Smart Sprinkler System');	# Gardening

SELECT * FROM device_type;

INSERT INTO device_info (
	device_type_id, 
	device_name, 
	device_description,
	device_range, 
	device_communication, 
	device_dataRate, 
	device_security, 
	device_softwareVersion, 
	device_storageCapacity, 
	device_energyRating, 
	device_activePower, 
	device_standbyPower, 
	device_registeredOn
) VALUES
(1, 'Nest Thermostat', 'Regulates home temperature', 30, 'WiFi', '100Mbit', 'WPA2', 'v5.3', 'No Storage', 'A+', 1.2, 0.2, NOW()),
(2, 'Philips Hue Switch', 'Controls lighting', 10, 'ZigBee', '250Kbit', 'None', 'v2.1', 'No Storage', 'A', 0.01, 0.001, NOW()),
(3, 'Dyson Air Quality', 'Monitors air quality', 50, 'WiFi', '100Mbit', 'WPA2', 'v1.7', '128GB', 'A++', 0.5, 0.3, NOW()),
(4, 'LG SmartThinQ AC', 'Smart air conditioning', 100, 'WiFi', '1Gbit', 'WPA3', 'v3.0', 'No Storage', 'A++', 2.5, 1.0, NOW()),
(5, 'Dyson Pure Cool', 'Smart fan with air purifier', 30, 'WiFi', '100Mbit', 'WPA2', 'v4.2', 'No Storage', 'A+', 0.045, 0.02, NOW()),
(6, 'Nest Cam Outdoor', 'Outdoor security camera', 25, 'WiFi', '100Mbit', 'WPA2', 'v3.2', 'No Storage', 'A', 0.3, 0.1, NOW()),
(7, 'Ring Video Doorbell', 'Monitors front door', 30, 'WiFi', '1Gbit', 'WPA2', 'v2.5', 'No Storage', 'A++', 0.15, 0.03, NOW()),
(8, 'August Smart Lock', 'Secures the door', 10, 'Bluetooth', '1Mbit', 'AES256', 'v4.1', 'No Storage', 'A+', 0.2, 0.05, NOW()),
(9, 'Lutron Serena Smart Blind', 'Automated window blinds', 20, 'ZigBee', '250Kbit', 'None', 'v1.8', 'No Storage', 'A', 0.075, 0.02, NOW()),
(10, 'Samsung Family Hub', 'Smart fridge with touchscreen', 50, 'WiFi', '1Gbit', 'WPA2', 'v6.0', '1TB', 'A++', 1.8, 0.9, NOW()),
(11, 'Bosch Home Connect Oven', 'Smart cooking oven', 50, 'WiFi', '1Gbit', 'WPA2', 'v3.7', '512GB', 'A+', 3.5, 1.5, NOW()),
(12, 'Nespresso Expert', 'Connected coffee machine', 10, 'Bluetooth', '1Mbit', 'None', 'v2.2', 'No Storage', 'A++', 0.25, 0.1, NOW()),
(13, 'LG Twin Wash', 'Smart washer and dryer combo', 30, 'WiFi', '100Mbit', 'WPA2', 'v5.0', 'No Storage', 'A', 2.0, 1.0, NOW()),
(14, 'Sony Bravia Smart TV', 'Smart television with streaming', 50, 'WiFi', '1Gbit', 'WPA2', 'v7.3', '2TB', 'A++', 0.3, 0.1, NOW()),
(15, 'Sonos Beam', 'Smart compact soundbar', 40, 'WiFi', '1Gbit', 'WPA2', 'v2.4', 'No Storage', 'A', 0.5, 0.15, NOW()),
(16, 'Amazon Echo Dot', 'Voice controlled smart speaker', 20, 'WiFi', '100Mbit', 'WPA2', 'v4.0', '4GB', 'A', 0.4, 0.1, NOW()),
(17, 'Rachio Smart Sprinkler', 'Controls garden watering', 100, 'WiFi', '100Mbit', 'WPA2', 'v3.9', 'No Storage', 'A+', 0.2, 0.05, NOW());

SELECT * FROM device_info;

INSERT INTO device (
	device_info_id,
	device_location, 
	device_parameters, 
	is_active, 
	active_from, 
	active_to
) VALUES
(1, 'Living Room', 'Temperature set to 22°C', TRUE, '2023-11-01 08:00:00', '2023-11-01 20:00:00'),
(2, 'Kitchen', 'Brightness level: 70%', FALSE, NULL, NULL),
(2, 'Hallway', 'Brightness level: 60%', TRUE, '2023-11-02 18:00:00', '2023-11-02 23:00:00'),
(3, 'Master Bedroom', 'PM2.5 level: 12 μg/m³', TRUE, '2023-11-01 09:00:00', '2023-11-01 17:00:00'),
(3, 'Bathroom', 'Humidity level: 40%', TRUE, '2023-11-02 08:00:00', '2023-11-02 08:30:00'),
(4, 'Office Room', 'Cooling to 24°C', TRUE, '2023-11-01 09:30:00', '2023-11-01 17:30:00'),
(4, 'Bedroom', 'AC set to 22°C for night', FALSE, NULL, NULL),
(5, 'Living Room', 'Fan speed: Medium', FALSE, NULL, NULL),
(5, 'Dining Room', 'Fan speed: High during dinner time', TRUE, '2023-11-02 20:00:00', '2023-11-02 22:00:00'),
(6, 'Front Yard', 'Camera mode: Surveillance', TRUE, '2023-11-01 00:00:00', '2023-11-01 23:59:59'),
(6, 'Backyard', 'Camera mode: Daytime', FALSE, NULL, NULL),
(7, 'Main Door', 'Doorbell ring volume: High', TRUE, '2023-11-01 07:00:00', '2023-11-01 19:00:00'),
(8, 'Back Door', 'Lock status: Engaged', TRUE, '2023-11-01 23:00:00', '2023-11-02 07:00:00'),
(8, 'Main Entrance', 'Lock status: Auto-lock active', TRUE, '2023-11-02 22:00:00', '2023-11-03 06:00:00'),
(9, 'Bedroom Window', 'Blinds position: Half-open', FALSE, NULL, NULL),
(9, 'Living Room', 'Blinds schedule: Open at sunrise', TRUE, '2023-11-02 06:30:00', '2023-11-02 18:00:00'),
(10, 'Kitchen', 'Temperature: 3°C, Ice maker: On', TRUE, '2023-11-01 10:00:00', '2023-11-01 22:00:00'),
(11, 'Office', 'Oven preheat to 180°C', TRUE, '2023-11-02 17:00:00', '2023-11-02 17:30:00'),
(12, 'Kitchen', 'Brewing strength: Strong', TRUE, '2023-11-02 07:00:00', '2023-11-02 07:10:00'),
(13, 'Laundry Room', 'Washing mode: Eco', TRUE, '2023-11-02 19:00:00', '2023-11-02 20:30:00'),
(14, 'Living Room', 'Volume level: 12', FALSE, NULL, NULL),
(15, 'Home Office', 'Soundbar connected to laptop', FALSE, NULL, NULL),
(16, 'Bedroom', 'Playing: Soft Jazz Playlist', TRUE, '2023-11-02 21:00:00', '2023-11-03 00:00:00'),
(17, 'Backyard', 'Sprinkler schedule: Evening', TRUE, '2023-11-02 18:00:00', '2023-11-02 18:30:00');

SELECT * FROM device;

INSERT INTO device_msg_log (
  device_msg_type, 
  device_id, 
  device_msg, 
  createdOn, 
  is_read
) VALUES
('Status Update', 1, 'Temperature reached set point of 22°C', '2023-11-01 08:30:00', TRUE),
('Status Update', 2, 'Kitchen lights turned off', '2023-11-01 22:00:00', TRUE),
('Status Update', 2, 'Hallway lights turned on to 60% brightness', '2023-11-02 18:00:00', FALSE),
('Warning', 3, 'PM2.5 level in Master Bedroom above normal', '2023-11-01 15:00:00', TRUE),
('Status Update', 3, 'Bathroom humidity returned to normal level', '2023-11-02 09:00:00', TRUE),
('Status Update', 4, 'Office Room AC reached set point of 24°C', '2023-11-01 10:00:00', TRUE),
('Schedule', 4, 'Bedroom AC scheduled to turn on at 22:00', '2023-11-01 21:00:00', FALSE),
('Status Update', 5, 'Living Room fan turned off', '2023-11-01 23:00:00', TRUE),
('Status Update', 5, 'Dining Room fan set to high for dinner time', '2023-11-02 20:00:00', FALSE),
('Alert', 6, 'Motion detected in Front Yard', '2023-11-01 01:30:00', TRUE),
('Status Update', 6, 'Backyard camera turned off', '2023-11-01 20:00:00', TRUE),
('Event', 7, 'Doorbell rung at Main Door', '2023-11-01 12:45:00', TRUE),
('Status Update', 8, 'Back Door locked', '2023-11-01 23:01:00', TRUE),
('Status Update', 8, 'Main Entrance auto-lock engaged', '2023-11-02 22:00:00', FALSE),
('Command', 9, 'Bedroom Window blinds opened', '2023-11-02 08:00:00', TRUE),
('Schedule', 9, 'Living Room blinds opened at sunrise', '2023-11-02 06:30:00', FALSE),
('Status Update', 10, 'Fridge temperature set to 3°C', '2023-11-01 10:15:00', TRUE),
('Command', 11, 'Office oven preheated to 180°C', '2023-11-02 17:00:00', FALSE),
('Status Update', 12, 'Coffee brewed with strength set to Strong', '2023-11-02 07:05:00', FALSE),
('Status Update', 13, 'Laundry started on Eco mode', '2023-11-02 19:05:00', TRUE),
('Status Update', 14, 'Living Room TV volume set to 12', '2023-11-01 19:00:00', TRUE),
('Command', 15, 'Soundbar connected to laptop in Home Office', '2023-11-02 09:00:00', TRUE),
('Event', 16, 'Soft Jazz Playlist started in Bedroom', '2023-11-02 21:00:00', FALSE),
('Status Update', 17, 'Sprinkler system activated for evening schedule', '2023-11-02 18:00:00', TRUE),
('Maintenance', 1, 'Thermostat firmware updated to version 5.4', '2023-11-02 02:00:00', FALSE);

SELECT * FROM device_msg_log;

INSERT INTO device_energy_log (
    device_log_date, 
    device_id, 
    hours_active, 
    hours_inactive, 
    energy_consumed
) VALUES
('2023-11-06', 1, 12, 12, 16.8),
('2023-11-06', 2, 0, 24, 0.024),
('2023-11-06', 3, 5, 19, 0.069),
('2023-11-06', 4, 8, 16, 8.8),
('2023-11-06', 5, 0, 24, 7.2),
('2023-11-06', 6, 8, 16, 36.0),
('2023-11-06', 7, 0, 24, 24.0),
('2023-11-06', 8, 0, 24, 0.48),
('2023-11-06', 9, 2, 22, 0.53),
('2023-11-06', 10, 23, 1, 7.0),
('2023-11-06', 11, 0, 24, 2.4),
('2023-11-06', 12, 12, 12, 2.16),
('2023-11-06', 13, 8, 16, 2.4),
('2023-11-06', 14, 8, 16, 2.4),
('2023-11-06', 15, 0, 24, 0.48),
('2023-11-06', 16, 11, 13, 1.085),
('2023-11-06', 17, 12, 12, 32.4),
('2023-11-06', 18, 0, 24, 36.0),
('2023-11-06', 19, 0, 24, 2.4),
('2023-11-06', 20, 1, 23, 25.0),
('2023-11-06', 21, 0, 24, 2.4),
('2023-11-06', 22, 0, 24, 3.6),
('2023-11-06', 23, 3, 21, 3.3),
('2023-11-06', 24, 0, 24, 1.2);


SELECT * FROM device_energy_log;

DELIMITER //

-- Procdure To Calculate device consumed energy and active/inactive hours
CREATE PROCEDURE CalculateDeviceEnergyConsumption(
	IN 	deviceID INT, 
    OUT hoursActive DOUBLE, 
    OUT hoursInactive DOUBLE,
	OUT energyConsumed DOUBLE
)
BEGIN
  DECLARE activePower DOUBLE DEFAULT 0.0;
  DECLARE standbyPower DOUBLE DEFAULT 0.0;

  -- Get device power values
  SELECT device_activePower, device_standbyPower INTO activePower, standbyPower
  FROM device_info
  WHERE device_info_id = (
    SELECT device_info_id FROM device WHERE device_id = deviceId
  );

  -- Initialize hoursActive
  SET hoursActive = 0;

  -- Calculate hoursActive
  SELECT COALESCE(
           TIMESTAMPDIFF(HOUR, active_from, COALESCE(active_to, NOW())),
           0
         ) INTO hoursActive
  FROM device
  WHERE device_id = deviceId
    AND is_active = TRUE;
    
  -- Ensure hoursActive is not negative
  IF hoursActive < 0 THEN
     SET hoursActive = 0;
  END IF;

  SET hoursInactive = 24 - hoursActive;
  SET energyConsumed = ROUND((activePower * hoursActive) + (standbyPower * hoursInactive), 3);
END//

-- Procedure to Iterate over devices and insert new entry into device_energy_log
CREATE PROCEDURE UpdateDeviceEnergyLogs()
BEGIN
  DECLARE finished TINYINT DEFAULT FALSE;
  DECLARE deviceID INT DEFAULT 1;
  DECLARE cur CURSOR FOR SELECT device_id FROM device;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET finished = TRUE;

  OPEN cur;

  read_loop: LOOP
    FETCH cur INTO deviceID;
    IF finished = TRUE THEN 
      LEAVE read_loop;
    END IF;
	
    CALL CalculateDeviceEnergyConsumption(deviceID, @hoursActive, @hoursInactive, @energyConsumed);
    INSERT INTO device_energy_log (
		device_log_date,
		device_id,
        hours_active,
        hours_inactive,
        energy_consumed
	)
    VALUES (CURDATE(), deviceID, @hoursActive, @hoursInactive, @energyConsumed);
    
  END LOOP;
  CLOSE cur;
END//

-- Event to schedule Daily device energy consumption logging
CREATE EVENT IF NOT EXISTS DailyUpdateDeviceEnergyLogs
ON SCHEDULE EVERY 1 DAY
STARTS TIMESTAMP(CURRENT_DATE, '00:00:00') -- This schedules the event to start at midnight today
DO
BEGIN
  CALL UpdateDeviceEnergyLogs();
END //

DELIMITER ;

SHOW EVENTS WHERE Name = 'DailyUpdateDeviceEnergyLogs';

SELECT "Smart Home DB READY!!" AS "INFO";