//package com.markcha.ems.controller.equipment;
//
//import com.markcha.ems.controller.equipment.dto.EquipmentDto;
//import com.markcha.ems.controller.equipment.dto.TagSetDto;
//import com.markcha.ems.domain.*;
//import com.markcha.ems.domain.pattern.Pattern;
//import com.markcha.ems.domain.pattern.PatternList;
//import com.markcha.ems.dto.device.CompressorModelDto;
//import com.markcha.ems.dto.response.ApiResponseDto;
//import com.markcha.ems.repository.*;
//import com.markcha.ems.repository.device.impl.DeviceDslRepositoryImpl;
//import com.markcha.ems.repository.equipment.impl.EquipmentDslRepositoryImpl;
//import com.markcha.ems.repository.group.impl.GroupDslRepositoryImpl;
//import com.markcha.ems.service.impl.DeviceServiceImpl;
//import lombok.RequiredArgsConstructor;
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//import static com.markcha.ems.domain.EquipmentType.AIR_COMPRESSOR;
//import static java.util.Objects.isNull;
//import static java.util.stream.Collectors.*;
//
//@RestController
//@RequestMapping("/api")
//@RequiredArgsConstructor
//public class EquipmentController {
//    @Value("${response.jpa.DB_INSERT_MSG}")
//    private String dbInsertMsg;
//    @Value("${response.jpa.DB_UPDATE_MSG}")
//    private String dbUpdateMsg;
//    @Value("${response.jpa.DB_DELETE_MSG}")
//    private String dbDeleteMsg;
//    @Value("${response.jpa.DB_ERROR_MSG}")
//    private String dbErrorMsg;
//    private final DeviceDslRepositoryImpl deviceDslRepository;
//    private final GroupDslRepositoryImpl groupDslRepository;
//    private final DeviceServiceImpl deviceService;
//    private final DeviceDataRepository deviceDataRepository;
//    private final EquipmentDataRepository equipmentDataRepository;
//    private final EquipmentDslRepositoryImpl equipmentDslRepository;
//    private final ModelMapper modelMapper;
//    private final TagListDataRepository tagListDataRepository;
//    private final TagDataRepository tagDataRepository;
//    private final TagSetMapperDataRepository tagSetMapperDataRepository;
//    private final AlarmDataRepository alarmDataRepository;
//    private final PatternListDataRepository patternListDataRepository;
//    private final PatternDataRepository patternDataRepository;
//    private final TagSetDataRepository tagSetDataRepository;
//    @GetMapping(value="/etcss")
//    public void etc(
//    ) {
//        Equipment a = new Equipment();
//        a.setId(100L);
//        a.setName("few");
//        a.setType(AIR_COMPRESSOR);
//        equipmentDataRepository.save(a);
//    }
//    @GetMapping(value="/equipments")
//    public ResponseEntity<?> showEquipment(
//    ) {
//        return ResponseEntity.ok(
//                equipmentDslRepository.findAllByIdWithTagList().stream()
//                        .map(t->new EquipmentDto(t))
//                        .collect(toList())
//        );
//    }
//    @GetMapping(value="/stateComponent")
//    public ResponseEntity<?> showStatetateComponent2(
//    ) {
//        List<String> components = List.of(
//                "stateComponent");
//        List<TagSetDto.EquipmentSetDto> equipments = equipmentDslRepository.findAllByIdWithTagList()
//                .stream()
//                .map(t->new TagSetDto.EquipmentSetDto(t))
//                .collect(toList());
//        List<TagSetDto> tagSetDtoList = tagSetDataRepository.findAllInNickname(components).stream()
//                .map(t-> {
//                    TagSetDto tagSetDto = new TagSetDto(t);
//                    tagSetDto.setEquipments(new ArrayList<>(equipments));
//                    return tagSetDto;
//                })
//                .collect(toList());
//        List<TagList> stateComponent = tagListDataRepository.findAllByAllEquipmentId(components);
//        Map<Long, List<TagList>> grouppingTagList = stateComponent.stream()
//                .collect(Collectors.groupingBy(t -> t.getEquipment().getId(), toList()));
//        for (TagSetDto tagSetDto : tagSetDtoList) {
//            for (TagSetDto.EquipmentSetDto equipment : tagSetDto.getEquipments()) {
//                List<TagList> tagLists = grouppingTagList.get(equipment.getId());
//                for (TagSetDto.TagListSetDto tagListSetDto : equipment.getTagList()) {
//                    if (!isNull(tagLists)) {
//                        for (TagList tagList : tagLists) {
//                            for (TagSetMapper tagSetMapper : tagList.getTagSetMappers()) {
//                                if (tagSetMapper.getTagSet().getNickname().equals(tagSetDto.getNickname())) {
//                                    if (tagListSetDto.getId() == tagSetMapper.getTagList().getId()) {
//                                        tagListSetDto.setActive(true);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return ResponseEntity.ok(
//                tagSetDtoList
//        );
//    }
//    @GetMapping(value="/components")
//    public ResponseEntity<?> showStatetateComponent(
//    ) {
//        List<String> components = List.of(
//                "mainInfoComponent", "wholeInfoComponent",
//                "importantInfoComponent", "groupDashboardComponent");
//        List<TagSetDto.EquipmentSetDto> equipments = equipmentDslRepository.findAllByIdWithTagList()
//                .stream()
//                .map(t->new TagSetDto.EquipmentSetDto(t))
//                .collect(toList());
//        List<TagSetDto> tagSetDtoList = tagSetDataRepository.findAllInNickname(components).stream()
//                .map(t-> {
//                    TagSetDto tagSetDto = new TagSetDto(t);
//                    tagSetDto.setEquipments(new ArrayList<>(equipments));
//                    return tagSetDto;
//                })
//                .collect(toList());
//        List<TagList> stateComponent = tagListDataRepository.findAllByAllEquipmentId(components);
//        Map<Long, List<TagList>> grouppingTagList = stateComponent.stream()
//                .collect(Collectors.groupingBy(t -> t.getEquipment().getId(), toList()));
//        for (TagSetDto tagSetDto : tagSetDtoList) {
//            for (TagSetDto.EquipmentSetDto equipment : tagSetDto.getEquipments()) {
//                List<TagList> tagLists = grouppingTagList.get(equipment.getId());
//                for (TagSetDto.TagListSetDto tagListSetDto : equipment.getTagList()) {
//                    if (!isNull(tagLists)) {
//                        for (TagList tagList : tagLists) {
//                            for (TagSetMapper tagSetMapper : tagList.getTagSetMappers()) {
//                                if (tagSetMapper.getTagSet().getNickname().equals(tagSetDto.getNickname())) {
//                                    if (tagListSetDto.getId() == tagSetMapper.getTagList().getId()) {
//                                        tagListSetDto.setActive(true);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return ResponseEntity.ok(
//                tagSetDtoList
//        );
//    }
//    @PutMapping(value="/components")
//    public ResponseEntity<?> showStatetateComponent(
//            @RequestBody List<TagSetDto> tagSet
//    ) {
//        Map<Long, TagList> tagListMap = tagListDataRepository.findAll().stream()
//                .collect(toMap(t -> t.getId(), k -> k));
//        Map<Long, TagSet> tagSetMap = tagSetDataRepository.findAll().stream()
//                .collect(toMap(t -> t.getId(), k -> k));
//        List<String> components = List.of(
//                "mainInfoComponent", "wholeInfoComponent",
//                "importantInfoComponent", "groupDashboardComponent");
//        List<TagList> allByAllEquipmentId = tagListDataRepository.findAllByAllEquipmentId(components);
//        Map<Long, List<TagList>> grouppingTagList = allByAllEquipmentId.stream()
//                .collect(groupingBy(t -> t.getEquipment().getId(), toList()));
//
//        for (TagSetDto tagSetDto : tagSet) {
//            for (TagSetDto.EquipmentSetDto equipment : tagSetDto.getEquipments()) {
//                for (TagSetDto.TagListSetDto tagListSetDto : equipment.getTagList()) {
//                    List<TagList> tagListEntities = grouppingTagList.get(equipment.getId());
//                    for (TagList tagListEntity : tagListEntities) {
//                        tagListEntity.getTagSetMappers()
//                                .removeIf(t->!t.getTagSet().getNickname().equals("stateComponent"));
//                        tagListEntity.getTagSetMappers().add(TagSetMapper.builder()
//                                .tagSet(tagSetMap.get(tagSetDto.getId()))
//                                .tagList(tagListMap.get(tagListSetDto.getId()))
//                                .build());
//                    }
//                }
//            }
//        }
//        tagListDataRepository.saveAll(allByAllEquipmentId);
//
//        return ResponseEntity.ok(
//                new ApiResponseDto(dbUpdateMsg)
//        );
//    }
//    @PutMapping(value="/equipment/{equipmentId}")
//    public ResponseEntity<?> updateEquipment(
//            @PathVariable("equipmentId") Long equipmentId,
//            @RequestBody EquipmentDto equipmentDto
//    ) {
//        modelMapper.getConfiguration().setSkipNullEnabled(true);
//        Equipment equipment = equipmentDslRepository.getOneByIdWithTagList(equipmentId);
//        Set<TagList> tagLists = equipment.getTagLists();
//        List<TagList> deleteTagList = new ArrayList<>();
//        List<TagSetMapper> tagSetMappers = new ArrayList<>();
//        List<PatternList> patternLists = new ArrayList<>();
//        List<Tag> tags = new ArrayList<>();
//        List<Alarm> alarms = new ArrayList<>();
//        List<Pattern> patterns = new ArrayList<>();
//        List<TagList> newTagLists = equipmentDto.getTagList().stream()
//                .map(t -> {
//                    TagList map = modelMapper.map(t, TagList.class);
//                    return map;
//                })
//                .collect(toList());
//
//        for (TagList tagList : tagLists) {
//            boolean haveToDelete = true;
//            for (TagList newTagList : new ArrayList<>(newTagLists)) {
//                if (newTagList.equals(tagList)) {
//                    haveToDelete = false;
//                    newTagLists.remove(newTagList);
//                }
//            }
//            if(haveToDelete) {
//                tagSetMappers.addAll(tagList.getTagSetMappers());
//                tags.addAll(tagList.getTags());
//                for (Tag tag : tagList.getTags()) {
//                    alarms.addAll(tag.getAlarms());
//                }
//                patternLists.addAll(tagList.getPatternList());
//                for (PatternList patternList : tagList.getPatternList()) {
//                    patternLists.add(patternList);
//                    patterns.addAll(patternList.getPatterns());
//                }
//                deleteTagList.add(tagList);
//            }
//        }
//
//        patternDataRepository.deleteAllInBatch(patterns);
//        patternListDataRepository.deleteAllInBatch(patternLists);
//        alarmDataRepository.deleteAllInBatch(alarms);
//        tagDataRepository.deleteAllInBatch(tags);
//        tagSetMapperDataRepository.deleteAllInBatch(tagSetMappers);
//        tagListDataRepository.deleteAllInBatch(deleteTagList);
//        Equipment updateEquipment = modelMapper.map(equipmentDto, Equipment.class);
//        modelMapper.map(updateEquipment, equipment);
//        Equipment save = equipmentDataRepository.save(equipment);
//        for (TagList newTagList : newTagLists) {
//            newTagList.setEquipment(save);
//        }
//
//        tagListDataRepository.saveAll(newTagLists);
//        return ResponseEntity.ok(
//                new ApiResponseDto(dbUpdateMsg)
//        );
//    }
//
//
//    @GetMapping("/compressor/maker")
//    public ResponseEntity<?> getMakers() {
//        List<String> makers = equipmentDslRepository.getMakers();
//        return ResponseEntity.ok(makers);
//    }
//
//    @GetMapping("/compressor/model")
//    public ResponseEntity<?> getModels(String maker) {
//        if(isNull(maker)) {
//            List<String> emptyArray = new ArrayList<>();
//            return ResponseEntity.ok(emptyArray);
//        }
//        List<CompressorModelDto> models = equipmentDslRepository.getModels(maker);
//        return ResponseEntity.ok(models);
//    }
//}
