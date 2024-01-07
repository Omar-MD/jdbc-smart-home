DELIMITER //

DROP PROCEDURE IF EXISTS UpdateDeviceEnergyLogs;
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
DELIMITER ;
-- CALL UpdateDeviceEnergyLogs();