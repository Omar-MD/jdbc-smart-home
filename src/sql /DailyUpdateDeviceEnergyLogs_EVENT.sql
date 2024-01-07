DELIMITER //

CREATE EVENT IF NOT EXISTS DailyUpdateDeviceEnergyLogs
ON SCHEDULE EVERY 1 DAY
STARTS TIMESTAMP(CURRENT_DATE, '00:00:00') -- This schedules the event to start at midnight today
DO
BEGIN
  CALL UpdateDeviceEnergyLogs();
END //
DELIMITER ;

SHOW EVENTS WHERE Name = 'DailyUpdateDeviceEnergyLogs';
