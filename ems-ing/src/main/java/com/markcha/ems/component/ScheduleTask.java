package com.markcha.ems.component;

import com.markcha.ems.controller.GroupController;
import com.markcha.ems.controller.GroupController.GroupSearchDto;
import com.markcha.ems.controller.ScheduleController;
import com.markcha.ems.controller.ScheduleController.ScheduleDto;
import com.markcha.ems.domain.Alarm;
import com.markcha.ems.domain.EquipmentType;
import com.markcha.ems.dto.device.DeviceConDto;
import com.markcha.ems.dto.order.OrderDto;
import com.markcha.ems.dto.response.ApiResponseDto;
import com.markcha.ems.dto.tag.TagDto;
import com.markcha.ems.repository.AlarmDataRepository;
import com.markcha.ems.repository.group.dto.GroupQueryDto;
import com.markcha.ems.repository.group.impl.GroupDslRepositoryImpl;
import com.markcha.ems.repository.group.impl.GroupDynamicRepositoryImpl;
import com.markcha.ems.repository.order.OrderDslRepositoryImpl;
import com.markcha.ems.repository.schedule.impl.ScheduleDslRepositoryImpl;
import com.markcha.ems.repository.tag.TagDslRepositoryIml;
import com.markcha.ems.service.impl.WebaccessApiServiceImpl;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.markcha.ems.domain.EquipmentType.AIR_COMPRESSOR;
import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;


public class ScheduleTask extends TimerTask {
    private final GroupDslRepositoryImpl groupDslRepository;
    private final ScheduleDslRepositoryImpl scheduleDslRepository;
    private final OrderDslRepositoryImpl orderDslRepository;
    private final GroupDynamicRepositoryImpl groupDynamicRepository;
    private final WebaccessApiServiceImpl webaccessApiService;
    private final AlarmDataRepository alarmDataRepository;
    private final TagDslRepositoryIml tagDslRepositoryIml;

    private Long id;

    public ScheduleTask(GroupDslRepositoryImpl groupDslRepository,
                        ScheduleDslRepositoryImpl scheduleDslRepository,
                        OrderDslRepositoryImpl orderDslRepository,
                        GroupDynamicRepositoryImpl groupDynamicRepository,
                        WebaccessApiServiceImpl webaccessApiService,
                        TagDslRepositoryIml tagDslRepositoryIml,
                        AlarmDataRepository alarmDataRepository) {
        this.groupDslRepository = groupDslRepository;
        this.scheduleDslRepository = scheduleDslRepository;
        this.orderDslRepository = orderDslRepository;
        this.groupDynamicRepository = groupDynamicRepository;
        this.webaccessApiService = webaccessApiService;
        this.tagDslRepositoryIml = tagDslRepositoryIml;
        this.alarmDataRepository = alarmDataRepository;
    }

    @Override
    public void run() {

        List<ScheduleDto> schedules = groupDslRepository.findAllJoinScheduleByScheduleId(this.id).stream()
                .map(ScheduleDto::new)
                .collect(toList());
        LocalDate nowDate = LocalDate.now();
        LocalTime nowTime = LocalTime.now();

        schedules.forEach(schedule-> {
            System.out.println("------------");
            System.out.println(schedule.getGroupId());
            System.out.println(schedule.getPressure());
            System.out.println(schedule.getDayOfWeeks());
            System.out.println(nowDate.getDayOfWeek().getValue());
            System.out.println(schedule.getDayOfWeeks().contains(nowDate.getDayOfWeek().getValue()));
            System.out.println(schedule.getStartTime().isBefore(nowTime));
            System.out.println(schedule.getStopTime().isAfter(nowTime));
            int powerState = 0;
            Integer weekNumber = getWeekNumber(nowDate);
            if(schedule.getStartTime().isBefore(nowTime)
                    && schedule.getStopTime().isAfter(nowTime)
                    && schedule.getDayOfWeeks().contains(nowDate.getDayOfWeek().getValue())
                    && schedule.getWeeks().contains(weekNumber)) {
                if(schedule.getIsGroup()){

                    Double pressor = new Double(schedule.getPressure().toString());
                    if(schedule.getMax() < pressor) {
                        powerState = 0;
                    } else if(schedule.getMin() > pressor) {
                        powerState = 1;
                    } else {
                        powerState = 2;
                    }
                } else {
                    powerState = 1;
                }
                System.out.println(schedule.getGroupId());
            } else {
                powerState = 0;
            }
            if (powerState != 2) {
                if (schedule.getIsGroup()) {
                    groupControl(schedule.getGroupId(), weekNumber, powerState);
                } else {
                    targetControl(schedule.getGroupId(), powerState);
                }
            }


        });
    }
    private boolean targetControl(Long groupId, int powerState) {
        GroupSearchDto groupSearchDto = new GroupSearchDto();
        String powerCode = "COMP_Power";
        String localCode = "COMP_Local";
        groupSearchDto.setTagTypes(asList(powerCode, localCode));
        groupSearchDto.setEquipmentType(AIR_COMPRESSOR);
        groupSearchDto.setDetail(false);
        List<DeviceConDto> devices = new ArrayList<>();
        groupDynamicRepository.getAnalysisLocations(Arrays.asList(groupId), groupSearchDto, true).stream()
                .map(t -> new GroupQueryDto(t, false))
                .forEach(k->devices.addAll(k.getAllDevices().stream()
                        .map(DeviceConDto::new).collect(toList())));
        List<String> tagNames = new ArrayList<>();
        try {
            for (DeviceConDto device : devices) {
                TagDto localTag = device.getTags().get(localCode);
                TagDto powerTag = device.getTags().get(powerCode);
                System.out.println(powerTag.getTagName());
                Object objectState = webaccessApiService.getTagValuesV2(asList(powerTag.getTagName())).get(powerTag.getTagName());
                System.out.println(objectState);
                Integer objectStateInt = new Double(objectState.toString()).intValue();
                System.out.println(objectStateInt);
                for (int i = 0; i < 3; i++) {
                    if (objectState instanceof Integer) {

                        if (objectStateInt == 0 && powerState == 1) {
                            webaccessApiService.setTagValues(new ArrayList<>(List.of(localTag)));
                            Thread.sleep(500);
                            powerTag.setValue(1);
                            webaccessApiService.setTagValues(new ArrayList<>(List.of(powerTag)));
                            Thread.sleep(500);
                            if(new Double(webaccessApiService.getTagValuesV2(asList(powerTag.getTagName())).get(powerTag.getTagName()).toString()).intValue() == 1) {
                                return true;
                            }
                        } else if (objectStateInt == 1 && powerState == 0) {
                            webaccessApiService.setTagValues(new ArrayList<>(List.of(localTag)));
                            Thread.sleep(500);
                            powerTag.setValue(0);
                            webaccessApiService.setTagValues(new ArrayList<>(List.of(powerTag)));
                            Thread.sleep(500);
                            if(new Double(webaccessApiService.getTagValuesV2(asList(powerTag.getTagName())).get(powerTag.getTagName()).toString()).intValue() == 0) {
                                return true;
                            }
                        } else if (objectStateInt == 0 && powerState == 0) {
                            return true;
                        } else if (objectStateInt == 1 && powerState == 1) {
                            return true;
                        }
                    }
                    Thread.sleep(1000);
                }
                Alarm alarm = new Alarm();
                alarm.setOccurrenceTime(LocalTime.now());
                alarm.setCheckIn(false);
                alarm.setTempValue(null);
                alarm.setPrssValue(null);
                alarm.setKwhValue(null);
                alarm.setMessage("에어 컴프레셔 제어 실패");
                alarm.setEventDate(LocalDate.now());
                alarm.setType("error");
                alarm.setTrip(null);
                alarm.setTag(tagDslRepositoryIml.getOneById(device.getTags().get(powerCode).getId()));
                alarmDataRepository.save(alarm);
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
    private boolean groupControl(Long scheduleId, Integer week, int powerState) {
        Long rootGroupId = scheduleDslRepository.findRootGroupId(scheduleId).getId();
        List<OrderDto> orders = orderDslRepository.findAllByRootGroupIdWeekId(rootGroupId, new Long(week)).stream()
                .map(OrderDto::new)
                .sorted(comparing(OrderDto::getOrder))
                .collect(toList());
        for (OrderDto order : orders) {
            boolean controlResult = targetControl(order.getGroupId(), powerState);
            if (controlResult) {
                return true;
            }
        }
        return false;
    }

    public void setScheduleId(Long id) {
        this.id = id;
    }
    private Integer getWeekNumber(LocalDate dated) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;

        String strDate = dated.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        try {	//정한 형식으로 사용합니다.
            date = formatter.parse(strDate);
        } catch (ParseException e) {
        }

        date = new Date(date.getTime() + (1000 * 60 * 60 * 24 - 1));
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //각각의 원하는 데이터를 얻어 옵니다.
        int weekNum = cal.get(Calendar.WEEK_OF_MONTH); // 해당년의 주차 구해옵니다.
        int dayNum = cal.get(Calendar.DAY_OF_WEEK); // 요일을 구해옵니다.

        return weekNum;
    }
}
