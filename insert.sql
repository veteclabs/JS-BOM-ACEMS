-- --------------------------------------------------------
-- 호스트:                          127.0.0.1
-- 서버 버전:                        10.6.7-MariaDB - mariadb.org binary distribution
-- 서버 OS:                        Win64
-- HeidiSQL 버전:                  11.3.0.6295
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- 테이블 ing.users 구조 내보내기
DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `verified` tinyint(3) NOT NULL DEFAULT 0,
  `create_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `phone` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `is_alarm` tinyint(4) NOT NULL DEFAULT 0,
  `last_password_changed` datetime NOT NULL DEFAULT current_timestamp(),
  `last_update_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `withdraw_dtm` datetime DEFAULT NULL,
  `enable_marketing_At` datetime DEFAULT NULL,
  `disable_marketing_At` datetime DEFAULT NULL,
  `updatedAt` datetime(6) NOT NULL DEFAULT current_timestamp(6) ON UPDATE current_timestamp(6),
  `createdAt` datetime(6) NOT NULL DEFAULT current_timestamp(6),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uix_user_email` (`email`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 프로시저 ing.usp_get_users 구조 내보내기
DROP PROCEDURE IF EXISTS `usp_get_users`;
DELIMITER //
CREATE PROCEDURE `usp_get_users`(
	IN `pi_vch_id` VARCHAR(50),
	IN `pi_vch_password` VARCHAR(512)
)
BEGIN
SELECT 
u.username AS ID,
u.email
FROM users u
WHERE email = pi_vch_id AND `password`=pi_vch_password;
END//
DELIMITER ;

-- 테이블 tagdata.data_acquisition_status 구조 내보내기
DROP TABLE IF EXISTS `data_acquisition_status`;
CREATE TABLE IF NOT EXISTS `data_acquisition_status` (
  `logdate` date NOT NULL,
  `tagname` varchar(50) NOT NULL,
  `initial_daq_time` datetime NOT NULL,
  `last_daq_time` datetime NOT NULL,
  `initial_daq_value` double NOT NULL,
  `last_daq_value` double NOT NULL,
  `standard` int(11) NOT NULL,
  `daq_count` int(11) NOT NULL,
  `percent` int(11) NOT NULL,
  PRIMARY KEY (`logdate`,`tagname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='데이터 취득 현황 ';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 tagdata.electricity_day_tariff_billing 구조 내보내기
DROP TABLE IF EXISTS `electricity_day_tariff_billing`;
CREATE TABLE IF NOT EXISTS `electricity_day_tariff_billing` (
  `workplace_id` int(11) NOT NULL DEFAULT 0,
  `billing_date` date NOT NULL COMMENT '일',
  `energy_charge` double DEFAULT NULL COMMENT '전력량 요금',
  `total_usage` double DEFAULT NULL COMMENT '환경비용차감',
  `min_usage` double DEFAULT NULL COMMENT '경부하 사용량',
  `mid_usage` double DEFAULT NULL COMMENT '중간부하 사용량',
  `max_usage` double DEFAULT NULL COMMENT '최대부하 사용량',
  PRIMARY KEY (`billing_date`,`workplace_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='일별 부하시간대 단가x사용량';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 tagdata.electricity_tariff_billing 구조 내보내기
DROP TABLE IF EXISTS `electricity_tariff_billing`;
CREATE TABLE IF NOT EXISTS `electricity_tariff_billing` (
  `workplace_id` int(11) NOT NULL,
  `billing_date` date NOT NULL COMMENT '청구월',
  `start_date` date NOT NULL COMMENT '사용기간 시작',
  `end_date` date NOT NULL COMMENT '사용기간 종료',
  `demand_charge` double DEFAULT NULL COMMENT '기본요금(원미만 절사)',
  `pf_charge` double DEFAULT NULL COMMENT '역률 요금',
  `energy_charge` double DEFAULT NULL COMMENT '전력량 요금',
  `environment_deduction` double DEFAULT NULL COMMENT '환경비용차감',
  `climate_related_rate` double DEFAULT NULL COMMENT '기후환경요금',
  `adjustment_fuel_cost_rate` double DEFAULT NULL COMMENT '연료비조정액',
  `energy_total` double DEFAULT NULL COMMENT '전기요금계(기본요금 + 역률요금 ＋ 전력량요금 ＋ 기후환경요금 ± 연료비조정액)',
  `vat` double DEFAULT NULL COMMENT '부가가치세(원미만 4사 5입)',
  `base_fund` double DEFAULT NULL COMMENT '전력산업기반기금(10원미만절사)',
  `billing_charge` double DEFAULT NULL COMMENT '청구금액',
  `created` datetime NOT NULL DEFAULT current_timestamp(),
  `updated` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`billing_date`,`workplace_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='2021.01.01 개정 계산\r\n전기요금표 영문 참고';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 tagdata.energy 구조 내보내기
DROP TABLE IF EXISTS `energy`;
CREATE TABLE IF NOT EXISTS `energy` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `mj` double DEFAULT NULL,
  `kcal` double DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `tco2` double DEFAULT NULL,
  `toe` double DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 tagdata.energy_history_cumulative_usage 구조 내보내기
DROP TABLE IF EXISTS `energy_history_cumulative_usage`;
CREATE TABLE IF NOT EXISTS `energy_history_cumulative_usage` (
  `timestamp` datetime NOT NULL,
  `tagname` varchar(50) NOT NULL,
  `power` double DEFAULT NULL COMMENT 'kWh',
  `gas` double DEFAULT NULL COMMENT 'Nm3',
  `water` double DEFAULT NULL COMMENT 'm3',
  `oil` double DEFAULT NULL COMMENT 'm3',
  PRIMARY KEY (`timestamp`,`tagname`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='5분 단위 누적량';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 tagdata.energy_history_usage 구조 내보내기
DROP TABLE IF EXISTS `energy_history_usage`;
CREATE TABLE IF NOT EXISTS `energy_history_usage` (
  `timestamp` datetime NOT NULL,
  `tagname` varchar(50) NOT NULL,
  `power` double DEFAULT NULL COMMENT 'kWh',
  `gas` double DEFAULT NULL COMMENT 'Nm3',
  `water` double DEFAULT NULL COMMENT 'm3',
  `oil` double DEFAULT NULL COMMENT 'm3',
  `toe` double DEFAULT NULL,
  `tco2` double DEFAULT NULL,
  `won` double DEFAULT NULL COMMENT '요금',
  PRIMARY KEY (`timestamp`,`tagname`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='5분 단위 사용량';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 tagdata.energy_location_summary 구조 내보내기
DROP TABLE IF EXISTS `energy_location_summary`;
CREATE TABLE IF NOT EXISTS `energy_location_summary` (
  `summary_date` date NOT NULL,
  `location_main_name` varchar(50) NOT NULL,
  `location_sub_name` varchar(50) NOT NULL,
  `purpose` varchar(50) NOT NULL DEFAULT '0',
  `power` double NOT NULL DEFAULT 0,
  `water` double NOT NULL DEFAULT 0,
  `gas` double NOT NULL DEFAULT 0,
  `oil` double NOT NULL DEFAULT 0,
  PRIMARY KEY (`summary_date`) USING BTREE,
  UNIQUE KEY `uix_energy_locaiton` (`location_main_name`,`location_sub_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 tagdata.energy_season 구조 내보내기
DROP TABLE IF EXISTS `energy_season`;
CREATE TABLE IF NOT EXISTS `energy_season` (
  `year` int(11) NOT NULL,
  `season` int(11) NOT NULL,
  `power` double DEFAULT NULL,
  `gas` double DEFAULT NULL,
  `water` double DEFAULT NULL,
  `oil` double DEFAULT NULL,
  PRIMARY KEY (`year`,`season`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='계절별 사용량\r\n';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 tagdata.forecasting 구조 내보내기
DROP TABLE IF EXISTS `forecasting`;
CREATE TABLE IF NOT EXISTS `forecasting` (
  `timestamp` datetime NOT NULL,
  `tagname` varchar(50) NOT NULL,
  `forecasting_usage` double DEFAULT NULL,
  PRIMARY KEY (`timestamp`,`tagname`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 tagdata.history 구조 내보내기
DROP TABLE IF EXISTS `history`;
CREATE TABLE IF NOT EXISTS `history` (
  `timestamp` datetime NOT NULL,
  `tagname` varchar(21) COLLATE utf8mb4_bin NOT NULL,
  `cumulative_usage` double NOT NULL DEFAULT 0 COMMENT '누적 사용량',
  `before_cumulative_usage` double NOT NULL DEFAULT 0,
  `usage` double NOT NULL DEFAULT 0 COMMENT '사용량',
  UNIQUE KEY `uix_history_logdatetime_tagname` (`timestamp`,`tagname`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='5분 단위 집계\r\n';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 tagdata.history_day 구조 내보내기
DROP TABLE IF EXISTS `history_day`;
CREATE TABLE IF NOT EXISTS `history_day` (
  `timestamp` date NOT NULL,
  `tagname` varchar(50) COLLATE utf8mb4_bin NOT NULL,
  `usage` double NOT NULL COMMENT '사용량',
  UNIQUE KEY `uix_history_day_logdatetime_tagname` (`timestamp`,`tagname`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 tagdata.history_hour 구조 내보내기
DROP TABLE IF EXISTS `history_hour`;
CREATE TABLE IF NOT EXISTS `history_hour` (
  `timestamp` datetime NOT NULL,
  `tagname` varchar(50) COLLATE utf8mb4_bin NOT NULL,
  `usage` double NOT NULL COMMENT '사용량',
  UNIQUE KEY `uix_history_hour_logdatetime_tagname` (`timestamp`,`tagname`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='시간단위 집계\r\n';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 tagdata.history_month 구조 내보내기
DROP TABLE IF EXISTS `history_month`;
CREATE TABLE IF NOT EXISTS `history_month` (
  `timestamp` char(6) COLLATE utf8mb4_bin NOT NULL DEFAULT '',
  `tagname` varchar(50) COLLATE utf8mb4_bin NOT NULL,
  `usage` double NOT NULL COMMENT '사용량',
  UNIQUE KEY `uix_history_monthlogdatetime_tagname` (`timestamp`,`tagname`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='시간단위 집계\r\n';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 tagdata.history_peak 구조 내보내기
DROP TABLE IF EXISTS `history_peak`;
CREATE TABLE IF NOT EXISTS `history_peak` (
  `timestamp` varchar(50) NOT NULL,
  `tagname` varchar(50) NOT NULL,
  `kWh` double NOT NULL DEFAULT 0,
  `kW` double NOT NULL DEFAULT 0,
  PRIMARY KEY (`timestamp`,`tagname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 tagdata.history_trend_usage 구조 내보내기
DROP TABLE IF EXISTS `history_trend_usage`;
CREATE TABLE IF NOT EXISTS `history_trend_usage` (
  `timestamp` datetime NOT NULL,
  `tagname` varchar(50) NOT NULL,
  `usage` double unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`timestamp`,`tagname`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='1분 단위 실시간 값\r\n';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 tagdata.pattern 구조 내보내기
DROP TABLE IF EXISTS `pattern`;
CREATE TABLE IF NOT EXISTS `pattern` (
  `timestamp` datetime DEFAULT NULL,
  `tagname` varchar(23) DEFAULT NULL,
  `value` double DEFAULT NULL,
  UNIQUE KEY `uix-pattern` (`timestamp`,`tagname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 tagdata.pattern_day 구조 내보내기
DROP TABLE IF EXISTS `pattern_day`;
CREATE TABLE IF NOT EXISTS `pattern_day` (
  `timestamp` datetime DEFAULT NULL,
  `tagname` varchar(23) DEFAULT NULL,
  `value` double DEFAULT NULL,
  UNIQUE KEY `uix-pattern` (`timestamp`,`tagname`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 tagdata.pattern_hour 구조 내보내기
DROP TABLE IF EXISTS `pattern_hour`;
CREATE TABLE IF NOT EXISTS `pattern_hour` (
  `timestamp` datetime DEFAULT NULL,
  `tagname` varchar(23) DEFAULT NULL,
  `value` double DEFAULT NULL,
  UNIQUE KEY `uix-pattern` (`timestamp`,`tagname`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 tagdata.peventloga 구조 내보내기
DROP TABLE IF EXISTS `peventloga`;
CREATE TABLE IF NOT EXISTS `peventloga` (
  `ProjIdbw` int(11) DEFAULT NULL,
  `ProjNodeIdbw` int(11) DEFAULT NULL,
  `EventLogId` int(11) DEFAULT NULL,
  `EventLogName` varchar(64) DEFAULT NULL,
  `Description` varchar(64) DEFAULT NULL,
  `TagMax` int(11) DEFAULT NULL,
  `EveLogData1` int(11) DEFAULT NULL,
  KEY `Idx_EventLogA` (`EventLogId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 tagdata.peventtaga 구조 내보내기
DROP TABLE IF EXISTS `peventtaga`;
CREATE TABLE IF NOT EXISTS `peventtaga` (
  `EventLogId` int(11) DEFAULT NULL,
  `TagName` varchar(32) DEFAULT NULL,
  `TagNbr` int(11) DEFAULT NULL,
  `EveTagData1` int(11) DEFAULT NULL,
  `EveTagData2` int(11) DEFAULT NULL,
  `EveTagText1` varchar(64) DEFAULT NULL,
  `EveTagText2` varchar(64) DEFAULT NULL,
  KEY `Idx_EventTagA` (`EventLogId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 tagdata.procedure_temp 구조 내보내기
DROP TABLE IF EXISTS `procedure_temp`;
CREATE TABLE IF NOT EXISTS `procedure_temp` (
  `timestamp` datetime DEFAULT NULL,
  `temp` varchar(50) DEFAULT NULL,
  UNIQUE KEY `uix_temp` (`timestamp`,`temp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 tagdata.resent_alarm 구조 내보내기
DROP TABLE IF EXISTS `resent_alarm`;
CREATE TABLE IF NOT EXISTS `resent_alarm` (
  `cookie_id` int(11) NOT NULL AUTO_INCREMENT,
  `LogDateTime` datetime NOT NULL,
  `TagName` varchar(50) NOT NULL DEFAULT '0',
  `Description` varchar(50) DEFAULT NULL,
  `Action` varchar(50) DEFAULT NULL,
  `targetValue` int(11) DEFAULT NULL,
  `activeValue` int(11) DEFAULT NULL,
  PRIMARY KEY (`cookie_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1177 DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 tagdata.season 구조 내보내기
DROP TABLE IF EXISTS `season`;
CREATE TABLE IF NOT EXISTS `season` (
  `season` varchar(50) COLLATE utf8mb4_bin NOT NULL,
  `start_month` int(11) DEFAULT NULL,
  `end_month` int(11) DEFAULT NULL,
  PRIMARY KEY (`season`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 tagdata.sessions 구조 내보내기
DROP TABLE IF EXISTS `sessions`;
CREATE TABLE IF NOT EXISTS `sessions` (
  `session_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `expires` int(10) unsigned NOT NULL,
  `data` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 tagdata.sources_five_minute 구조 내보내기
DROP TABLE IF EXISTS `sources_five_minute`;
CREATE TABLE IF NOT EXISTS `sources_five_minute` (
  `logvalue` time NOT NULL,
  PRIMARY KEY (`logvalue`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 tagdata.sources_hour_with_day 구조 내보내기
DROP TABLE IF EXISTS `sources_hour_with_day`;
CREATE TABLE IF NOT EXISTS `sources_hour_with_day` (
  `logvalue` int(11) NOT NULL,
  PRIMARY KEY (`logvalue`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 tagdata.sources_minute 구조 내보내기
DROP TABLE IF EXISTS `sources_minute`;
CREATE TABLE IF NOT EXISTS `sources_minute` (
  `logTime` time NOT NULL,
  PRIMARY KEY (`logTime`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 tagdata.sources_quarter_past 구조 내보내기
DROP TABLE IF EXISTS `sources_quarter_past`;
CREATE TABLE IF NOT EXISTS `sources_quarter_past` (
  `logvalue` time NOT NULL,
  PRIMARY KEY (`logvalue`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='15분';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 프로시저 tagdata.evt_set_data 구조 내보내기
DROP PROCEDURE IF EXISTS `evt_set_data`;
DELIMITER //
CREATE PROCEDURE `evt_set_data`()
BEGIN
#데이터 수집 상태
insert data_acquisition_status
select date(b.timestamp) dt,
       b.tagname,
       min(timestamp),
       max(timestamp),
       min(lastvalue),
       max(lastvalue),
       t.standard,
       count(*),
       count(*) / t.standard * 100
from BwAnalogTable b
         inner join (select if(logging_time = 300, ceiling(
            TIMESTAMPDIFF(minute, convert(concat(curdate(), ' 00:00:00'), datetime), now()) / 5)
                                , ceiling(TIMESTAMPDIFF(minute, convert(concat(curdate(), ' 00:00:00'), datetime),
                                                        now()) / 1)) standard,
                            tagname
                     FROM `ing`.tag) t on t.tagname = b.TagName
where b.timestamp between curdate() and curdate() + interval 1 day
group by date(timestamp), b.tagname
on duplicate key update standard         = values(standard),
                        initial_daq_time= values(initial_daq_time),
                        initial_daq_value=values(initial_daq_value),
                        daq_count        = values(daq_count),
                        last_daq_time    = values(last_daq_time),
                        last_daq_value   = values(last_daq_value),
                        percent          = values(percent);

#1분 로깅 데이터
insert into history_trend_usage (SELECT b.timestamp, b.tagname, b.Lastvalue v
                                 FROM BwAnalogTable b
                                 WHERE tagname IN (select tagname AS a
                                                   from `ing`.tag
                                                   WHERE is_trend = 1)
                                   and b.timestamp = DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:00')
                                 GROUP BY b.timestamp)
on DUPLICATE key UPDATE `USAGE` = values(`usage`);
#if(af.tagname LIKE '%PF%',af.LastValue,(ifnull(af.LastValue, 0) - ifnull(bf.LastValue, 0))), 
#누적 데이터 소스
insert into history (
    SELECT af.timestamp            loadtime,
           af.tagname              tag,
           ifnull(af.LastValue, 0) `after`,
           IFNULL(bf.LastValue, 0) `before`,
           case when af.tagname LIKE '%PF%' OR af.tagname LIKE '%P_Tot%' then af.LastValue
			    ELSE IF((af.LastValue > 0) AND (af.LastValue >= bf.LastValue),
              (ifnull(af.LastValue, 0) - ifnull(bf.LastValue, 0)),0)
				  end      AS `value`            
    FROM BwAnalogTable af
             LEFT outer JOIN BwAnalogTable bf ON af.TagName = bf.TagName AND bf.timestamp = af.timestamp - INTERVAL 5 MINUTE
    WHERE af.tagname IN (select tagname AS a
                         FROM `ing`.tag
                         WHERE is_trend = 0)
      and af.timestamp = CONCAT(DATE_FORMAT(NOW(), '%Y-%m-%d %H:'), LPAD(FLOOR(MINUTE(NOW()) / 5) * 5, 2, '0'), ':00'))
on DUPLICATE key UPDATE cumulative_usage = values(cumulative_usage),
                        `USAGE`          = values(`usage`);

#데이터 시간 단위
insert into history_hour (timestamp, tagname, `usage`)(
    select date_format(timestamp, '%Y-%m-%d %H:00') dt, tagname, 
	  case when tagname LIKE '%PF%' OR tagname LIKE '%P_Tot%' then
 AVG(`usage`) else sum(`usage`) end as v
    from history
    where TIMESTAMP >= concat(curdate(), ' ', hour(now()), ':00:00')
    group by dt, tagname)
on DUPLICATE key UPDATE `USAGE` = values(`usage`);

#일별 데이터
insert into history_day (timestamp, tagname, `usage`) (
    select date(timestamp) dt, tagname,  case when tagname LIKE '%PF%' OR tagname LIKE '%P_Tot%' then
 AVG(`usage`) else sum(`usage`) end as v
    from history
    where timestamp >= concat(curdate(), ' ', ' 00:00:00')
      and timestamp <= concat(curdate() + interval 1 day, ' 00:00:00')
    group by dt, tagname)
on DUPLICATE key UPDATE `USAGE` = values(`usage`);

#월별 데이터
insert into history_month (timestamp, tagname, `usage`) (
    SELECT date_format(timestamp, '%Y%m') dt, tagname,  case when tagname LIKE '%PF%' OR tagname LIKE '%P_Tot%' then
 AVG(`usage`) else sum(`usage`) end as v
    from history
    where timestamp > concat(LAST_DAY(NOW() - interval 1 month) + interval 1 day, ' ', ' 00:00:00')
      and timestamp <= concat(LAST_DAY(NOW()), ' 00:00:00')
    group by dt, tagname)
on DUPLICATE key UPDATE `USAGE` = values(`usage`);
END//
DELIMITER ;

-- 프로시저 tagdata.evt_set_day_billing 구조 내보내기
DROP PROCEDURE IF EXISTS `evt_set_day_billing`;
DELIMITER //
CREATE PROCEDURE `evt_set_day_billing`()
BEGIN
insert electricity_day_tariff_billing
SELECT
		 s.id as workplace_id,
       date_format(s.timestamp,'%Y-%m-%d') dt,
       round(sum(s.`USAGE`)*cp.unit_price,2) AS 'price',
       sum(s.`USAGE`) AS 'total',
       ifnull(sum(case when c.`LOAD` ='경부하' then s.`USAGE` END),'0') AS 'min',
       ifnull(sum(case when c.`LOAD` ='중간부하' then s.`USAGE` END),'0') AS 'mid',
       ifnull(sum(case when c.`LOAD` ='최대부하' then s.`USAGE` END),'0') AS 'max'
FROM (SELECT *
      FROM history_hour s,
           (SELECT * FROM `bom`.workplace) AS p
      WHERE s.`TIMESTAMP` >= curdate()
        AND s.`TIMESTAMP` < curdate() + INTERVAL  1 day
        AND tagname = (SELECT tagname FROM `config`.pTag WHERE description = '메인')) as s
join `config`.season b ON MONTH(s.timestamp) BETWEEN b.start_month AND b.end_month
join `config`.electricity_season_with_load c ON b.season = c.season AND TIME(s.timestamp) BETWEEN c.start AND c.end
JOIN `config`.electricity_electric_rates cp ON c.load = cp.load AND b.season = cp.season and s.receiving_voltage = cp.receiving_voltage and s.contract_type = cp.contract_type
group by dt,workplace_id;
END//
DELIMITER ;

-- 프로시저 tagdata.usp_set_data 구조 내보내기
DROP PROCEDURE IF EXISTS `usp_set_data`;
DELIMITER //
CREATE PROCEDURE `usp_set_data`(
	IN `pi_dt_start` DATE,
	IN `pi_dt_end` DATE
)
BEGIN
insert into history_trend_usage (SELECT b.`timestamp`, b.tagname, b.Lastvalue v
FROM  BwAnalogTable b
WHERE tagname IN (select tagname AS a
FROM `ing`.tag WHERE is_trend = 1) AND b.`timestamp` >= concat(pi_dt_start, ' ','00:00:00') and `timestamp` <= concat(pi_dt_end, ' ','00:00:00')
GROUP BY b.`timestamp`)
on DUPLICATE key UPDATE
`USAGE` = values(`usage`);

insert into history (
SELECT af.`timestamp` loadtime,
       af.tagname tag,
       ifnull(af.LastValue, 0) `after`,
       IFNULL(bf.LastValue,0) `before`,
  case when af.tagname LIKE '%PF%' OR af.tagname LIKE '%P_Tot%'  then af.LastValue 
       ELSE IF((af.LastValue > 0) AND (af.LastValue >= bf.LastValue),
              (ifnull(af.LastValue, 0) - ifnull(bf.LastValue, 0)),0)
				  end      AS `value`            
FROM  BwAnalogTable af
LEFT JOIN BwAnalogTable bf ON af.TagName = bf.TagName AND bf.`timestamp` = af.`timestamp` - INTERVAL 5 MINUTE
WHERE af.tagname IN (select tagname AS a
FROM `ing`.tag WHERE is_trend = 0) and af.`timestamp` >= CONCAT(pi_dt_start, ' ','00:00:00') and af.`timestamp` <= CONCAT(pi_dt_end, ' ','00:00:00'))
on DUPLICATE key UPDATE
cumulative_usage = values(cumulative_usage),
`USAGE` = values(`usage`);

insert into history_hour (`timestamp`,tagname,`usage`)(
select date_format(`timestamp`,'%Y-%m-%d %H:00') dt,tagname, case when tagname LIKE '%PF%' OR tagname LIKE '%P_Tot%'  then
 AVG(`usage`) else sum(`usage`) end as v
from history
where `timestamp` >= concat(pi_dt_start, ' ','00:00:00') AND `timestamp` <= concat(pi_dt_end, ' ','00:00:00')
group by dt,tagname)
on DUPLICATE key UPDATE
`USAGE` = values(`usage`);

insert into history_day (`timestamp`,tagname,`usage`) (
select date(`timestamp`) dt,tagname, case when tagname LIKE '%PF%' OR tagname LIKE '%P_Tot%' then
 AVG(`usage`) else sum(`usage`) end as v
from history
where `timestamp` >= concat(pi_dt_start, ' ','00:00:00') and `timestamp` <= concat(pi_dt_end, ' ','00:00:00')
group by dt,tagname)
on DUPLICATE key UPDATE
`USAGE` = values(`usage`);

insert into history_month (`timestamp`,tagname,`usage`) (
SELECT date_format(`timestamp`,'%Y%m') dt,tagname, case when tagname LIKE '%PF%' OR tagname LIKE '%P_Tot%' then
 AVG(`usage`) else sum(`usage`) end as v
from history
where `timestamp` >= concat(pi_dt_start, ' ','00:00:00') and `timestamp` <= concat(pi_dt_end, ' ','00:00:00')
group by dt,tagname)
on DUPLICATE key UPDATE
`USAGE` = values(`usage`);

END//
DELIMITER ;

-- 프로시저 tagdata.usp_set_power_price 구조 내보내기
DROP PROCEDURE IF EXISTS `usp_set_power_price`;
DELIMITER //
CREATE PROCEDURE `usp_set_power_price`(
	IN `pi_int_workplace_id` INT,
	IN `pi_dt_start` DATE,
	IN `pi_dt_end` DATE
)
BEGIN

declare v_dt_billing_date DATE;
DECLARE v_vch_contract_type VARCHAR(20);
DECLARE v_vch_receiving_voltage VARCHAR(20);
DECLARE v_int_demand_charge INT;
DECLARE v_vch_season VARCHAR(20);
DECLARE v_int_this_month_kWh INT;
DECLARE v_int_pf INT;
DECLARE v_int_contract_power INT;
DECLARE v_int_lagging_pf INT;
DECLARE v_int_leading_pf INT;
DECLARE v_int_meter_reading_day INT;
DECLARE 	v_double_demand_charge DOUBLE; 
DECLARE v_double_pf_charge DOUBLE;  
DECLARE v_double_energy_charge DOUBLE; 
DECLARE v_double_sub_total DOUBLE; 
DECLARE v_double_environment_deduction DOUBLE; 
DECLARE 	v_double_climate_related_rate DOUBLE; 
DECLARE 	v_double_adjustment_fuel_cost_rate DOUBLE; 
DECLARE 	v_double_energy_total DOUBLE;  
DECLARE 	v_double_vat DOUBLE;  
DECLARE 	v_double_base_fund DOUBLE; 
DECLARE v_double_billing_charge DOUBLE;  

SET v_dt_billing_date = (SELECT pi_dt_start + INTERVAL 1 MONTH);

SET v_int_this_month_kWh = (select sum(total_usage)
from electricity_day_tariff_billing
where billing_date >= pi_dt_start and billing_date < pi_dt_end);
 
 SET v_vch_contract_type= (select contract_type
 FROM `bom`.workplace WHERE id=pi_int_workplace_id);
 
 SET v_vch_receiving_voltage= (select receiving_voltage
 from `bom`.workplace  WHERE id=pi_int_workplace_id);
 
 SET v_int_meter_reading_day= (select meter_reading_day
 from `bom`.workplace  WHERE id=pi_int_workplace_id);
 
 SET v_int_contract_power= (select contract_power
 from `bom`.workplace WHERE id=pi_int_workplace_id);
 
 SET v_int_lagging_pf= (select lagging_pf
 from `bom`.workplace WHERE id=pi_int_workplace_id);
 
 SET v_int_leading_pf= (select leading_pf
 from `bom`.workplace WHERE id=pi_int_workplace_id);
 

SET v_double_demand_charge = (select demand_charge*v_int_contract_power
from `config`.electricity_demand_charge
where contract_type= v_vch_contract_type and receiving_voltage = v_vch_receiving_voltage);


SET v_double_pf_charge = (select v_double_demand_charge*-0.01);


SET v_double_energy_charge = (select sum(energy_charge)
from electricity_day_tariff_billing
where billing_date >= pi_dt_start and billing_date < pi_dt_end);


SET v_double_environment_deduction = (select v_int_this_month_kWh*-5);


SET v_double_climate_related_rate = (select v_int_this_month_kWh*5.3);


SET v_double_adjustment_fuel_cost_rate = (select v_int_this_month_kWh*-3);



SET v_double_sub_total = (select v_double_demand_charge+v_double_pf_charge+v_double_energy_charge+v_double_climate_related_rate+v_double_adjustment_fuel_cost_rate);


SET v_double_vat = (ROUND(v_double_sub_total*0.1,0));


SET v_double_base_fund = (round(v_double_sub_total*0.037,0));


SET v_double_billing_charge = (round(v_double_sub_total+v_double_vat+v_double_base_fund,2));

insert into electricity_tariff_billing
(workplace_id,billing_date, start_date, end_date, demand_charge, pf_charge, energy_charge, environment_deduction, climate_related_rate, adjustment_fuel_cost_rate, energy_total, vat, base_fund, billing_charge)
VALUES
(pi_int_workplace_id,v_dt_billing_date,pi_dt_start,pi_dt_end,v_double_demand_charge,v_double_pf_charge,v_double_energy_charge,v_double_environment_deduction,v_double_climate_related_rate,v_double_adjustment_fuel_cost_rate,
v_double_sub_total,v_double_vat,v_double_base_fund,v_double_billing_charge)
on DUPLICATE key update
demand_charge=values(demand_charge),
pf_charge=values(pf_charge),
energy_charge=values(energy_charge),
environment_deduction=values(environment_deduction),
climate_related_rate=values(climate_related_rate),
adjustment_fuel_cost_rate=values(adjustment_fuel_cost_rate),
energy_total=values(energy_total),
vat=values(vat),
base_fund=values(base_fund),
billing_charge=values(billing_charge);
END//
DELIMITER ;

-- 이벤트 tagdata.evt_set_data 구조 내보내기
DROP EVENT IF EXISTS `evt_set_data`;
DELIMITER //
CREATE EVENT `evt_set_data` ON SCHEDULE EVERY 6 MINUTE STARTS '2022-06-29 10:11:00' ON COMPLETION PRESERVE ENABLE DO BEGIN
CALL evt_set_data();
END//
DELIMITER ;

-- 트리거 tagdata.tra_bwalarmtable_ins 구조 내보내기
DROP TRIGGER IF EXISTS `tra_bwalarmtable_ins`;
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `tra_bwalarmtable_ins` AFTER INSERT ON `BwAlarmTable` FOR EACH ROW BEGIN

    -- bwalarmtable 구조
-- `ProjNodeId`   INT(11)        NULL        DEFAULT NULL COMMENT '노드 이름',
-- `LogDate`      DATETIME       NULL        DEFAULT NULL COMMENT '로깅 날짜',
-- `LogTime`      DATETIME       NULL        DEFAULT NULL COMMENT '로깅 시간',
-- `Priority`     VARCHAR(5)     NULL        DEFAULT NULL COMMENT '우선순위',
-- `TagName`      VARCHAR(32)    NULL        DEFAULT NULL COMMENT '태그이름',
-- `Description`  VARCHAR(65)    NULL        DEFAULT NULL COMMENT '알람 설명',
-- `Action`       VARCHAR(80)    NULL        DEFAULT NULL COMMENT '알람 행위(승인,정상,알람)',
-- `UserName`     VARCHAR(32)    NULL        DEFAULT NULL COMMENT '스카다 접근 username',
-- `NodeName`     VARCHAR(32)    NULL        DEFAULT NULL COMMENT '노드 이름',
-- `NodeIP`       VARCHAR(32)    NULL        DEFAULT NULL COMMENT '노드 IP',
-- `AlmGroup`     VARCHAR(5)     NULL        DEFAULT NULL COMMENT '알람 그룹'

-- v = 지역 변수
-- datt = datetime
-- dat = date
-- tm = time
-- vch = varchar


    DECLARE v_dat_timestamp DATETIME DEFAULT 0;
    DECLARE v_vch_Tag_Type VARCHAR(6);
    DECLARE v_vch_Action VARCHAR(4);
    DECLARE v_vch_Description VARCHAR(45);

    IF NEW.Action LIKE '%승인%' THEN
        SET v_vch_Action = '확인';
    ELSEIF NEW.Action LIKE '%정상%' THEN
        SET v_vch_Action = '해제';
    ELSE
        SET v_vch_Action = '알람';
    END IF;


    SET v_dat_timestamp =
            STR_TO_DATE(CONCAT(DATE_FORMAT(NEW.LogDate, '%y%m%d'), ' ', TIME_FORMAT(NEW.LogTime, '%H%i%s')),
                        '%y%m%d %H%i%s');


    IF NEW.`description` LIKE '%최대수요%' THEN
       SET v_vch_Tag_Type = '최대수요';
        IF (NEW.Priority = 1) THEN
            SET v_vch_Description = '1단계 경보';
            SET v_vch_Tag_Type = '최대수요';
        ELSEIF (NEW.Priority = 2 AND v_vch_Action = '알람' ) THEN
            SET v_vch_Description = '최대수요 2단계 부하 OFF 준비완료';
	     ELSEIF (NEW.Priority = 2 AND v_vch_Action = '해제' ) THEN
            SET v_vch_Description = '최대수요 2단계 부하 OFF 준비 종료';
        ELSEIF (NEW.Priority = 3 AND  v_vch_Action = '알람' ) THEN
            SET v_vch_Description = '최대수요 3단계 부하 OFF 실행';
        ELSEIF (NEW.Priority = 3 AND  v_vch_Action = '해제' ) THEN
            SET v_vch_Description = '최대수요 3단계 부하 OFF 종료';
        END IF; 
    ELSEIF NEW.`description` LIKE '%UPS%' THEN
        SET v_vch_Tag_Type = 'UPS';
        IF (v_vch_Action = '알람') THEN
            SET v_vch_Description = '정전 발생, UPS 운영';
        ELSEIF (v_vch_Action = '해제') THEN
            SET v_vch_Description = '정전 복구, UPS 대기';
        END IF;
    ELSEIF NEW.`description` LIKE '%목표전력%' THEN
        SET v_vch_Tag_Type = '목표전력';
        IF (v_vch_Action = '알람') THEN
            SET v_vch_Description = '일일 목표 전력사용량 초과';
        END IF;
    END IF;

    INSERT INTO alarms
    SET `TAGNAME`    = NEW.TagName,
        `TIMESTAMP`  = v_dat_timestamp,
        `ALARM_TYPE` = v_vch_Tag_Type,
        `DESCRIPTION`= v_vch_Description;

-- VALUES (NEW.TagName,v_datt_Alarm_DateTime,v_vch_Alarm_Priority,v_date_Alarm_Date,v_tm_Alarm_Time,NEW.Description,v_vch_Alarm_Value,v_vch_Alarm_Week,v_vch_User_Id,v_vch_Alarm_Section,v_vch_Alarm_Type);
-- 

END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- 트리거 tagdata.trb_bwanalogtable_ins 구조 내보내기
DROP TRIGGER IF EXISTS `trb_bwanalogtable_ins`;
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `trb_bwanalogtable_ins` BEFORE INSERT ON `bwanalogtable` FOR EACH ROW BEGIN
DECLARE v_tm_add_time TIME; # 로깅 
DECLARE v_added_date_time DATETIME;
DECLARE v_next_day_date_time DATETIME;

SET v_tm_add_time = (SELECT  p.logging_time FROM `ing`.tag p WHERE NEW.TagName = p.tagname LIMIT 1);

SET v_added_date_time = CONVERT(CONCAT(NEW.LogDate,' ',NEW.LogTime),DATETIME) + INTERVAL v_tm_add_time SECOND;
SET NEW.LogDate = (SELECT DATE(v_added_date_time));
SET NEW.LogTime =  (SELECT TIME(v_added_date_time));
SET NEW.timestamp = v_added_date_time;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
INSERT INTO `users` (`id`, `username`, `password`, `verified`, `create_ip`, `email`, `phone`, `is_alarm`, `last_password_changed`, `last_update_ip`, `withdraw_dtm`, `enable_marketing_At`, `disable_marketing_At`, `updatedAt`, `createdAt`) VALUES (1, '윤순상', '0GB+J5VN0L1w+8iFhtCrzZCHYG48yi7xOFRES6QPy1SoRRRs+MCEJ1tVJXEjHJHtDXjOiY/cAlBX8tevPVspMg==', 0, NULL, 'admin', '01099201748', 1, '2022-02-09 15:07:36', NULL, NULL, NULL, NULL, '2022-07-06 10:40:59.240621', '2022-02-09 15:07:36.360522');
