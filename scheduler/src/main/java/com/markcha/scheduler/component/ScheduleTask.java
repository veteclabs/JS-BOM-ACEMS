package com.markcha.scheduler.component;

import com.markcha.scheduler.domain.Alarm;
import com.markcha.scheduler.domain.Tag;
import com.markcha.scheduler.dto.device.DeviceConDto;
import com.markcha.scheduler.dto.group.GroupSearchDto;
import com.markcha.scheduler.dto.order.OrderDto;
import com.markcha.scheduler.dto.schedule.ScheduleDto;
import com.markcha.scheduler.dto.tag.TagDto;
import com.markcha.scheduler.repository.AlarmDataRepository;
import com.markcha.scheduler.repository.group.dto.GroupQueryDto;
import com.markcha.scheduler.repository.group.impl.GroupDslRepositoryImpl;
import com.markcha.scheduler.repository.group.impl.GroupDynamicRepositoryImpl;
import com.markcha.scheduler.repository.order.OrderDslRepositoryImpl;
import com.markcha.scheduler.repository.schedule.impl.ScheduleDslRepositoryImpl;
import com.markcha.scheduler.repository.tag.TagDslRepositoryIml;
import com.markcha.scheduler.WebaccessApiServiceImpl;

import java.time.temporal.TemporalUnit;
import java.util.Queue;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

import static com.markcha.scheduler.domain.EquipmentType.AIR_COMPRESSOR;
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
    private static List<Double> queue = new LinkedList();

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
        if (queue.size() > 10) {
            queue.remove(0);
        }
        List<ScheduleDto> schedules = groupDslRepository.findAllJoinScheduleByScheduleId(this.id).stream()
                .map(ScheduleDto::new)
                .collect(toList());
        LocalDate nowDate = LocalDate.now();
        LocalTime nowTime = LocalTime.now();
        schedules.forEach(schedule-> {
            int powerState = 0;
            Integer weekNumber = getWeekNumber(nowDate);
            schedule.getStartTime().plusMinutes(-1);
            schedule.getStopTime().plusMinutes(-1);
            if(schedule.getStartTime().isBefore(nowTime)
                    && schedule.getStopTime().isAfter(nowTime)
                    && schedule.getDayOfWeeks().contains(nowDate.getDayOfWeek().getValue())
                    && schedule.getWeeks().contains(weekNumber)) {
                if(schedule.getIsGroup()){
                    Double pressor = new Double(schedule.getPressure().toString());
                    queue.add(pressor);
                    Double max = queue.stream().mapToDouble(t->t).max().orElseThrow(NoSuchElementException::new);
                    if(schedule.getMax()-0.1 <= queue.stream().mapToDouble(t->t).max().orElseThrow(NoSuchElementException::new)) {
                        powerState = 0;
                    } else if(schedule.getMin() >= queue.stream().mapToDouble(t->t).min().orElseThrow(NoSuchElementException::new)) {
                        powerState = 1;
                    } else {
                        powerState = 2;
                    }
                } else {
                    powerState = 1;
                }
            } else {
                powerState = 0;
            }
            if (powerState != 2) {
                try {
                    if (schedule.getIsGroup()) {
                            groupControl(schedule.getScheduleId(), weekNumber, powerState, schedule.getInterval());
                    } else {
                        targetControl(schedule.getGroupId(), powerState, schedule.getInterval(), false);
                    }
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        });
    }
    private boolean groupControl(Long scheduleId, Integer week, int powerState, Integer intever) throws InterruptedException {
        Long rootGroupId = scheduleDslRepository.findRootGroupId(scheduleId).getId();
        Stream<OrderDto> orderDtoStream = orderDslRepository.findAllByRootGroupIdWeekId(rootGroupId, new Long(week)).stream()
                .map(OrderDto::new);
        
        if(powerState == 1) {
            orderDtoStream = orderDtoStream
                    .sorted(comparing(OrderDto::getOrder));
        } else {
            orderDtoStream = orderDtoStream
                    .sorted(comparing(OrderDto::getOrder).reversed());
        }
        
        List<OrderDto> orders = orderDtoStream
                .collect(toList());

        for (OrderDto order : orders) {
            boolean controlResult = targetControl(order.getGroupId(), powerState, intever, true);
            if (controlResult) {
                return true;
            }
        }
        return false;
    }
    private boolean targetControl(Long groupId, int powerState, Integer intever, Boolean isGroup) throws InterruptedException {
        GroupSearchDto groupSearchDto = new GroupSearchDto();
        String powerType = "COMP_Power";
        String localType = "COMP_Local";
        groupSearchDto.setTagTypes(asList(powerType, localType));
        groupSearchDto.setEquipmentType(AIR_COMPRESSOR);
        groupSearchDto.setDetail(false);
        List<DeviceConDto> devices = new ArrayList<>();
        groupDynamicRepository.getAnalysisLocations(Arrays.asList(groupId), groupSearchDto, true).stream()
                .map(t -> new GroupQueryDto(t, false))
                .forEach(k->devices.addAll(k.getAllDevices().stream()
                        .map(DeviceConDto::new).collect(toList())));
        List<String> tagNames = new ArrayList<>();

        for (DeviceConDto device : devices) {
            TagDto localTag = device.getTags().get(localType);
            TagDto powerTag = device.getTags().get(powerType);
            Integer objectState = webaccessApiService.getTagValuesV2Int(powerTag.getTagName());
            for (int i = 0; i < 3; i++) {
                if (powerState == 1) {
                    localTag.setValue(1);
                    webaccessApiService.setTagValue(localTag);
                    Thread.sleep(intever * 1000);
                    powerTag.setValue(powerState);
                    webaccessApiService.setTagValue(powerTag);
                    if(webaccessApiService.getTagValuesV2Int(powerTag.getTagName()) == powerState) {
                        return true;
                    }
                } else if (powerState == 0) {
                    localTag.setValue(1);
                    webaccessApiService.setTagValue(localTag);
                    Thread.sleep(intever * 1000);
                    powerTag.setValue(powerState);
                    webaccessApiService.setTagValue(powerTag);
                    if(webaccessApiService.getTagValuesV2Int(powerTag.getTagName())  == powerState) {
                        return isGroup? false: true;
                    }
                } else if (objectState == 0 && powerState == 0) {
                    return false;
                } else if (objectState == 1 && powerState == 1) {
                    return false;
                }
            }
            Alarm alarm = new Alarm();
            alarm.setOccurrenceTime(LocalTime.now());
            alarm.setCheckIn(false);
            alarm.setTempValue(null);
            alarm.setPrssValue(null);
            alarm.setKwhValue(null);
            alarm.setMessage("에어 컴프레셔 제어 실패");
            alarm.setEventDate(LocalDate.now());
            alarm.setType("fail");
            alarm.setTrip(null);
            alarm.setTag(tagDslRepositoryIml.getOneById(device.getTags().get(powerType).getId()));
            alarmDataRepository.save(alarm);
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
