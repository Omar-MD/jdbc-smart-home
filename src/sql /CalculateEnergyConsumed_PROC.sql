DELIMITER //

DROP PROCEDURE IF EXISTS CalculateDeviceEnergyConsumption;
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
DELIMITER ;

-- CALL CalculateDeviceEnergyConsumption(1, @hoursActive, @hoursInactive, @energyConsumed);
-- SELECT @hoursActive, @hoursInactive, @energyConsumed;
