-- --------------------------------------------------------
-- 호스트:                          127.0.0.1
-- 서버 버전:                        10.7.3-MariaDB - mariadb.org binary distribution
-- 서버 OS:                        Win64
-- HeidiSQL 버전:                  11.3.0.6295
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- 테이블 데이터 ing.day_of_week:~7 rows (대략적) 내보내기
/*!40000 ALTER TABLE `day_of_week` DISABLE KEYS */;
INSERT INTO `day_of_week` (`day_of_week_id`, `idx`, `name`) VALUES
	(1, 6, '토'),
	(2, 0, '일'),
	(3, 1, '월'),
	(4, 2, '화'),
	(5, 3, '수'),
	(6, 4, '목'),
	(7, 5, '금');
/*!40000 ALTER TABLE `day_of_week` ENABLE KEYS */;

-- 테이블 데이터 ing.energy:~29 rows (대략적) 내보내기
/*!40000 ALTER TABLE `energy` DISABLE KEYS */;
INSERT INTO `energy` (`energy_id`, `mj`, `kcal`, `name`, `tco2`, `toe`, `usage`) VALUES
	(2, 32.7, 7810, '휘발유', 0, 0.781, 1000),
	(3, 36.7, 8770, '등유', 0, 0.877, 1000),
	(4, 37.8, 9030, '경유', 0, 0.903, 1000),
	(5, 39, 9310, 'B-A유', 0, 0.931, 1000),
	(6, 40.5, 9670, 'B-B유', 0, 0.967, 1000),
	(7, 41.7, 9960, 'B-C유', 0, 0.996, 1000),
	(8, 50.4, 12040, '프로판', 0, 1.204, 1000),
	(9, 49.5, 11820, '부탄', 0, 1.182, 1000),
	(10, 32.3, 7710, '나프타', 0, 0.771, 1000),
	(11, 32.8, 7830, '용제', 0, 0.783, 1000),
	(12, 36.5, 8720, '항공유', 0, 0.872, 1000),
	(13, 41.4, 9890, '아스팔트', 0, 0.989, 1000),
	(14, 40, 9550, '윤활유', 0, 0.955, 1000),
	(15, 35, 8360, '석유코크스', 0, 0.836, 1000),
	(16, 39.9, 9530, '부생연료유2호', 0, 0.953, 1000),
	(17, 54.7, 13060, '천연가스(LNG)', 0, 1.306, 1000),
	(18, 38.9, 15.272, '도시가스(LNG)', 0.4594, 1.029, 1000),
	(19, 63.6, 15190, '도시가스(LPG)', 0, 1.519, 1000),
	(20, 19.8, 4730, '국내무연탄', 0, 0.473, 1000),
	(21, 21.2, 5060, '연료용 수입무연탄', 0, 0.506, 1000),
	(22, 25.2, 6020, '원료용 수입무연탄', 0, 0.602, 1000),
	(23, 24.8, 5920, '연료용 유연탄', 0, 0.592, 1000),
	(24, 29.2, 6970, '원료용 유연탄', 0, 0.697, 1000),
	(25, 21.4, 5110, '아역청탄', 0, 0.511, 1000),
	(26, 29, 6930, '코크스', 0, 0.693, 1000),
	(27, 8.9, 2130, '전력(발전기준)', 0, 0.213, 1000),
	(28, 9.6, 2290, '전력(소비기준)', 0.21782962, 0.229, 1000),
	(29, 18.8, 4500, '신탄', 0, 0.45, 1000),
	(30, 0, 0, '수도', 0, 0, 1000);
/*!40000 ALTER TABLE `energy` ENABLE KEYS */;

-- 테이블 데이터 ing.equipment:~9 rows (대략적) 내보내기
/*!40000 ALTER TABLE `equipment` DISABLE KEYS */;
INSERT INTO `equipment` (`equipment_id`, `created`, `description`, `maker`, `model`, `name`, `type`, `updated`) VALUES
	(1, '2021-05-20 09:31:14.000000', '에어 컴프레셔', 'INGERSOLL RAND ', 'INGERSOLL RAND RS200IE', 'INGERSOLL RAND RS200IE', 0, '2021-05-20 09:31:14.000000'),
	(2, '2022-05-04 10:47:12.000000', '에어 컴프레셔', 'INGERSOLL RAND ', 'INGERSOLL RAND RS200n', 'INGERSOLL RAND RS200n', 0, '2022-05-04 10:48:57.000000'),
	(3, '2020-11-09 22:44:07.000000', '전력', '루텍', 'Accura', 'Accura', 1, '2020-11-09 22:44:07.000000'),
	(4, '2021-04-29 11:21:37.000000', '유량계', NULL, NULL, '유량계', 3, '2021-04-29 11:21:37.000000'),
	(6, '2021-05-20 10:13:01.000000', '습도계', NULL, NULL, '습도 계측기', 5, '2021-05-20 10:13:01.000000'),
	(7, '2021-05-20 10:11:12.000000', '온도계', NULL, NULL, '온도 계측기', 4, '2021-05-20 10:12:58.000000'),
	(8, '2021-07-01 15:46:18.000000', '온습도계', NULL, NULL, '온습도 계측기', 6, '2021-07-01 15:46:18.000000'),
	(9, '2022-03-08 13:06:28.000000', '전력', '남전사', '남전사', '남전사', 1, '2022-03-08 13:06:29.000000'),
	(10, '2022-04-12 15:59:19.000000', '압력계', NULL, NULL, NULL, 2, '2022-04-12 15:59:20.000000');
/*!40000 ALTER TABLE `equipment` ENABLE KEYS */;

-- 테이블 데이터 ing.tag_list:~43 rows (대략적) 내보내기
/*!40000 ALTER TABLE `tag_list` DISABLE KEYS */;
INSERT INTO `tag_list` (`tag_list_id`, `is_alarm`, `is_trend`, `is_usage`, `logging_time`, `max`, `min`, `nickname`, `show_able`, `tag_description`, `tagname`, `test_type`, `type`, `unit`, `unit_conversion`, `value`, `equipment_id`) VALUES
	(2, b'0', NULL, NULL, NULL, 15, 7, NULL, b'1', '전압', 'U001_PWR_V', 'double', 'PWR_V', 'V', NULL, NULL, 3),
	(3, b'0', NULL, NULL, NULL, 15, 7, NULL, b'1', '역률', 'U001_PWR_PF', 'double', 'PWR_PF', '%', NULL, NULL, 3),
	(4, b'0', NULL, NULL, NULL, 15, 7, NULL, b'1', '유효전력량', 'U001_PWR_KWh', 'double', 'PWR_KWh', 'kWh', NULL, NULL, 3),
	(5, b'0', NULL, NULL, NULL, 15, 7, NULL, b'1', '유효전력', 'U001_PWR_KW', 'double', 'PWR_KW', 'W', NULL, NULL, 3),
	(6, b'0', NULL, NULL, NULL, 15, 7, NULL, b'1', '무효전력량', 'U001_PWR_Kvarh', 'double', 'PWR_Kvarh', 'kW', NULL, NULL, 3),
	(7, b'0', NULL, NULL, NULL, 15, 7, NULL, b'1', '전류', 'U001_PWR_A', 'double', 'PWR_A', 'A', NULL, NULL, 3),
	(8, b'0', NULL, NULL, NULL, 15, 7, NULL, b'1', '온도', 'U005_AIR_Temp', 'double', 'AIR_Temp', '℃', NULL, NULL, 4),
	(9, b'0', NULL, NULL, NULL, 15, 7, NULL, b'1', '압력', 'U006_AIR_PRE', 'double', 'AIR_PRE', 'bar', NULL, NULL, 10),
	(10, b'0', NULL, NULL, NULL, 15, 7, NULL, b'1', '순시유량', 'U005_AIR_Flow', 'double', 'AIR_Flow', 'm³', NULL, NULL, 4),
	(11, b'0', NULL, NULL, NULL, 15, 7, NULL, b'1', '적산유량', 'U005_AIR_Con', 'double', 'AIR_Con', 'm³', NULL, NULL, 4),
	(12, b'1', NULL, NULL, NULL, 1, 0, NULL, b'1', '0=No active Warnings 1=active Warnings', 'U004_COMP_Warning', 'double', 'COMP_Warning', NULL, NULL, NULL, 1),
	(13, b'1', NULL, NULL, NULL, 1, 0, NULL, b'1', '0=No active trips 1=active trips', 'U004_COMP_Trip', 'double', 'COMP_Trip', NULL, NULL, NULL, 1),
	(14, b'0', NULL, NULL, NULL, 15, 7, NULL, b'1', '시스템 압력', 'U004_COMP_SystemPre', 'double', 'COMP_SystemPre', 'bar', NULL, NULL, 1),
	(15, b'0', NULL, NULL, NULL, 15, 7, NULL, b'1', '섬프 압력', 'U004_COMP_SumpPre', 'double', 'COMP_SumpPre', 'bar', NULL, NULL, 1),
	(16, b'0', NULL, NULL, NULL, 15, 7, NULL, b'1', '오프라인 압력', 'U004_COMP_StopPre', 'double', 'COMP_StopPre', 'bar', NULL, NULL, 1),
	(17, b'0', NULL, NULL, NULL, 15, 7, NULL, b'1', '온라인 압력', 'U004_COMP_StartPre', 'double', 'COMP_StartPre', 'bar', NULL, NULL, 1),
	(18, b'0', NULL, NULL, NULL, 15, 7, NULL, b'1', '스타터 유형', 'U004_COMP_StarterType', 'double', 'COMP_StarterType', NULL, NULL, NULL, 1),
	(19, b'0', NULL, NULL, NULL, 15, 7, NULL, b'1', '분리기 압력 강하', 'U004_COMP_SepPreDrop', 'double', 'COMP_SepPreDrop', 'bar', NULL, NULL, 1),
	(20, b'0', NULL, NULL, NULL, 15, 7, NULL, b'1', '실행시간(높은 바이트, 낮은 바이트)', 'U004_COMP_RunHours', 'double', 'COMP_RunHours', 'H', NULL, NULL, 1),
	(21, b'0', NULL, NULL, NULL, 15, 7, NULL, b'1', '원격 압력', 'U004_COMP_RemotePre', 'double', 'COMP_RemotePre', 'bar', NULL, NULL, 1),
	(22, b'0', NULL, NULL, NULL, 15, 7, NULL, b'1', '파워온 시간(높은 바이트, 낮은 바이트)', 'U004_COMP_PowerOnHour', 'double', 'COMP_PowerOnHour', 'H', NULL, NULL, 1),
	(23, b'0', NULL, NULL, NULL, 1, 0, NULL, b'1', '0= stop 1 start strat control', 'U004_COMP_Power', 'double', 'COMP_Power', NULL, NULL, NULL, 1),
	(24, b'0', NULL, NULL, NULL, 15, 7, NULL, b'1', '패키지 입구 온도', 'U004_COMP_PackInTemp', 'double', 'COMP_PackInTemp', '℃', NULL, NULL, 1),
	(25, b'0', NULL, NULL, NULL, 15, 7, NULL, b'1', '패키지 방전 압력', 'U004_COMP_PackDisPre', 'double', 'COMP_PackDisPre', 'bar', NULL, NULL, 1),
	(26, b'0', NULL, NULL, NULL, 15, 7, NULL, b'1', '오일쿨러 출구 온도', 'U004_COMP_OilOutTemp', 'double', 'COMP_OilTemp', '℃', NULL, NULL, 1),
	(27, b'0', NULL, NULL, NULL, 15, 7, NULL, b'1', '로드된 시간(높은 바이트, 낮은 바이트)', 'U004_COMP_LodeHours', 'double', 'COMP_LodeHours', 'H', NULL, NULL, 1),
	(28, b'0', NULL, NULL, NULL, 1, 0, NULL, b'1', '0=local 1 = Host', 'U004_COMP_Local', 'double', 'COMP_Local', NULL, NULL, NULL, 1),
	(29, b'0', NULL, NULL, NULL, 1, 0, NULL, b'1', '0=Unload 1=Load', 'U004_COMP_Load', 'double', 'COMP_Load', NULL, NULL, NULL, 1),
	(30, b'0', NULL, NULL, NULL, 15, 7, NULL, b'1', '단계간 압력', 'U004_COMP_InterPre', 'double', 'COMP_InterPre', 'bar', NULL, NULL, 1),
	(31, b'0', NULL, NULL, NULL, 15, 7, NULL, b'1', '흡기 진공', 'U004_COMP_InletVacuum', 'double', 'COMP_InletVacuum', NULL, NULL, NULL, 1),
	(32, b'0', NULL, NULL, NULL, 15, 7, NULL, b'1', '주입된 냉각수 온도', 'U004_COMP_InCoolTemp', 'double', 'COMP_InCoolTemp', '℃', NULL, NULL, 1),
	(33, b'0', NULL, NULL, NULL, 15, 7, NULL, b'1', '드라이어 증발기 온도', 'U004_COMP_DryEvapTemp', 'double', 'COMP_DryEvapTemp', '℃', NULL, NULL, 1),
	(34, b'0', NULL, NULL, NULL, 15, 7, NULL, b'1', '드라이어 응축기 온도', 'U004_COMP_DryCondTemp', 'double', 'COMP_DryCondTemp', '℃', NULL, NULL, 1),
	(35, b'0', NULL, NULL, NULL, 15, 7, NULL, b'1', '냉각수 필터 압력 강하', 'U004_COMP_CoolPreDrop', 'double', 'COMP_CoolPreDrop', 'bar', NULL, NULL, 1),
	(36, b'0', NULL, NULL, NULL, 15, 7, NULL, b'1', '냉각수 필터 출구 압력', 'U004_COMP_CoolOutPre', 'double', 'COMP_CoolOutPre', 'bar', NULL, NULL, 1),
	(37, b'0', NULL, NULL, NULL, 15, 7, NULL, b'1', '냉각수 필터 입구 압력', 'U004_COMP_CoolInPre', 'double', 'COMP_CoolInPre', 'bar', NULL, NULL, 1),
	(38, b'0', NULL, NULL, NULL, 1, 0, NULL, b'1', '자동 정지', 'U004_COMP_AutoStop', 'int', 'COMP_AutoStop', NULL, NULL, NULL, 1),
	(39, b'0', NULL, NULL, NULL, 15, 7, NULL, b'1', '에어엔드 방전 온도', 'U004_COMP_AirDisTemp', 'double', 'COMP_AirDisTemp', '℃', NULL, NULL, 1),
	(40, b'0', NULL, NULL, NULL, 15, 7, NULL, b'1', '최종냉각기 방전온도', 'U004_COMP_AfcDisTemp', 'double', 'COMP_AfcDisTemp', '℃', NULL, NULL, 1),
	(41, b'0', NULL, NULL, NULL, 15, 7, NULL, b'1', '활성 경고 코드', 'U004_COMP_ActWarCode', 'double', 'COMP_ActWarCode', NULL, NULL, NULL, 1),
	(42, b'0', NULL, NULL, NULL, 17, 1, NULL, b'1', '활성 트립 코드', 'U004_COMP_ActTripCode', 'double', 'COMP_ActTripCode', NULL, NULL, NULL, 1),
	(43, b'0', NULL, NULL, NULL, 15, 7, NULL, b'1', '최종 냉각기 배출 압력', 'U004_COMP_ACDisPre', 'double', 'COMP_ACDisPre', 'bar', NULL, NULL, 3),
	(44, b'0', NULL, NULL, NULL, 300, 0, NULL, b'1', '유지 보수 시간', 'U004_COMP_ACDisPre', 'double', 'COMP_InstpectHour', 'H', NULL, NULL, 3);
/*!40000 ALTER TABLE `tag_list` ENABLE KEYS */;

-- 테이블 데이터 ing.tag_set:~4 rows (대략적) 내보내기
/*!40000 ALTER TABLE `tag_set` DISABLE KEYS */;
INSERT INTO `tag_set` (`tag_set_id`, `description`, `nickname`) VALUES
	(4, '상태 컴포넌트', 'stateComponent'),
	(5, '메인 컴포넌트', 'mainInfoComponent'),
	(6, '전체 정보 컴포넌트', 'wholeInfoComponent'),
	(7, '주요 정보 컴포넌트', 'importantInfoComponent');
/*!40000 ALTER TABLE `tag_set` ENABLE KEYS */;

-- 테이블 데이터 ing.tag_set_mapper:~49 rows (대략적) 내보내기
/*!40000 ALTER TABLE `tag_set_mapper` DISABLE KEYS */;
INSERT INTO `tag_set_mapper` (`tag_set_mapper_id`, `tag_list_id`, `tag_set_id`) VALUES
	(50, 14, 4),
	(51, 16, 4),
	(52, 17, 4),
	(53, 26, 4),
	(55, 43, 6),
	(56, 24, 6),
	(57, 43, 6),
	(58, 42, 6),
	(59, 41, 6),
	(60, 39, 6),
	(61, 35, 6),
	(62, 36, 6),
	(63, 37, 6),
	(64, 32, 6),
	(65, 24, 6),
	(66, 21, 6),
	(67, 15, 6),
	(68, 22, 6),
	(69, 17, 6),
	(70, 27, 6),
	(71, 25, 6),
	(72, 19, 6),
	(73, 16, 6),
	(74, 31, 6),
	(75, 30, 6),
	(76, 20, 6),
	(77, 14, 6),
	(78, 26, 6),
	(79, 44, 6),
	(80, 43, 5),
	(81, 24, 5),
	(82, 23, 4),
	(83, 29, 4),
	(84, 41, 4),
	(85, 42, 4),
	(86, 13, 4),
	(87, 28, 4),
	(88, 38, 4),
	(89, 12, 4),
	(90, 2, 5),
	(91, 3, 5),
	(92, 4, 5),
	(93, 5, 5),
	(94, 6, 5),
	(95, 7, 5),
	(96, 14, 5),
	(97, 44, 5),
	(98, 43, 7),
	(99, 24, 7);
/*!40000 ALTER TABLE `tag_set_mapper` ENABLE KEYS */;

-- 테이블 데이터 ing.trip:~65 rows (대략적) 내보내기
/*!40000 ALTER TABLE `trip` DISABLE KEYS */;
INSERT INTO `trip` (`trip_code_id`, `code`, `is_trip`, `is_warning`, `message`, `equipment_id`) VALUES
	(1, 1, b'1', b'0', '1AVPT Failure', 1),
	(2, 2, b'1', b'0', '2APT Failure', 1),
	(3, 3, b'1', b'0', '3APT Failure', 1),
	(4, 4, b'1', b'0', '4APT Failure', 1),
	(5, 5, b'1', b'0', '5APT Failure', 1),
	(6, 6, b'1', b'0', '6CPT Failure', 1),
	(7, 7, b'1', b'0', '7APT Failure', 1),
	(8, 8, b'0', b'1', '10APT Failure', 1),
	(9, 9, b'0', b'1', '1ATT Failure', 1),
	(10, 10, b'1', b'0', '2ATT Failure', 1),
	(11, 11, b'1', b'0', '2CTT Failure', 1),
	(12, 12, b'0', b'0', 'AI12 Failure', 1),
	(13, 13, b'0', b'1', '4ATT Failure', 1),
	(14, 14, b'0', b'1', '5DTT Failure', 1),
	(15, 15, b'0', b'1', '6DTT Failure', 1),
	(16, 16, b'0', b'0', 'AI16 Failure', 1),
	(17, 17, b'1', b'1', 'Motor Winding Temperature Failure', 1),
	(18, 18, b'1', b'1', 'Motor Bearing Temperature Failure', 1),
	(19, 101, b'1', b'1', 'High Inlet Vacuum/Change Inlet Filter', 1),
	(20, 102, b'1', b'0', 'High Interstage Pressure', 1),
	(21, 103, b'1', b'0', 'Low Sump Pressure', 1),
	(22, 104, b'1', b'1', 'High Sump Pressure', 1),
	(23, 105, b'1', b'1', 'High Separator DeltaP/Charge Separator Element Failure', 1),
	(24, 106, b'1', b'1', 'High Coolant Filter Pressure Drop/Change Coolant Failure', 1),
	(25, 109, b'0', b'1', 'High Discharge Pressure ', 1),
	(26, 110, b'0', b'1', 'Change HE Filter', 1),
	(27, 200, b'0', b'1', 'Check SD Card', 1),
	(28, 201, b'0', b'1', 'Low Ambient Temperature', 1),
	(29, 202, b'1', b'1', 'High Airend Discharge Temperature', 1),
	(30, 203, b'0', b'1', 'Elevated Airend Discharge Temperature', 1),
	(31, 204, b'1', b'0', 'Unit Too Cold to Start', 1),
	(32, 205, b'0', b'1', 'Low Condenser Temperature', 1),
	(33, 206, b'0', b'1', 'High Condenser Temperature', 1),
	(34, 207, b'0', b'1', 'Freeze Waring', 1),
	(35, 208, b'1', b'1', 'High Motor Winding Temperature', 1),
	(36, 209, b'1', b'1', 'High Motor Bearing Temperature', 1),
	(37, 210, b'1', b'1', 'High Ambient Temperature', 1),
	(38, 301, b'1', b'0', 'Emergency Stop', 1),
	(39, 302, b'1', b'0', 'Main Motor Overload', 1),
	(40, 303, b'1', b'0', 'Fan Motor Overload/Blower Fault', 1),
	(41, 304, b'1', b'0', 'Remote Stop Failure', 1),
	(42, 305, b'1', b'0', 'Remote Start Failure', 1),
	(43, 306, b'0', b'0', 'Digital Input 6 Fault', 1),
	(44, 307, b'0', b'0', 'Digital Input 7 Fault', 1),
	(45, 308, b'0', b'0', 'Digital Input 8 Fault', 1),
	(46, 309, b'0', b'1', 'Condensate Drain Error 1', 1),
	(47, 310, b'0', b'1', 'Condensate Drain Error 2', 1),
	(48, 311, b'0', b'1', 'Dryer High Pressure', 1),
	(49, 312, b'0', b'1', 'Dryer Temperature Warning', 1),
	(50, 313, b'0', b'1', 'Condensate Drain Error 3', 1),
	(51, 314, b'0', b'0', 'Digital Input 14 Failure', 1),
	(52, 315, b'0', b'1', 'Auxiliary input 1', 1),
	(53, 316, b'0', b'1', 'Auxiliary input 2', 1),
	(54, 317, b'1', b'0', 'Control Power Loss', 1),
	(55, 401, b'0', b'1', 'Service Required', 1),
	(56, 402, b'0', b'1', 'Alarm - Service Required', 1),
	(57, 403, b'0', b'1', '100 Hours to Service', 1),
	(58, 404, b'0', b'1', 'Load Cycles High Duty', 1),
	(59, 405, b'0', b'1', 'Load Cycles Severe Duty', 1),
	(60, 501, b'0', b'1', 'Motor Monitor Communications Failure', 1),
	(61, 901, b'1', b'0', 'Check Motor Rotation', 1),
	(62, 902, b'0', b'1', 'Invaild Calibration', 1),
	(63, 903, b'0', b'1', 'Clean Cooler', 1),
	(64, 904, b'0', b'0', 'Wait for Blowdown', 1),
	(65, 905, b'0', b'0', 'Isoltaion Contact Open', 1);
/*!40000 ALTER TABLE `trip` ENABLE KEYS */;

-- 테이블 데이터 ing.week:~6 rows (대략적) 내보내기
/*!40000 ALTER TABLE `week` DISABLE KEYS */;
INSERT INTO `week` (`week_id`, `idx`, `name`) VALUES
	(1, 1, '첫째주'),
	(2, 2, '둘째주'),
	(3, 3, '셋째주'),
	(4, 4, '넷째주'),
	(5, 5, '다섯째주'),
	(6, 6, '여섯째주');
/*!40000 ALTER TABLE `week` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
