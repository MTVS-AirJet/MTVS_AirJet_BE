package com.meta.air_jet.map.controller;

import com.meta.air_jet._core.file.AwsFileService;
import com.meta.air_jet._core.utils.ApiUtils;
import com.meta.air_jet.manvoice.ManVocService;
import com.meta.air_jet.map.domain.Map;
import com.meta.air_jet.map.domain.dto.MapRequestDTO;
import com.meta.air_jet.map.service.MapService;
import com.meta.air_jet.mission.Mission;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MapController {
    private final MapService mapService;
    private final AwsFileService awsFileService;

    // 맵 생성 저장 db에 url로 저장 (s3)    @PostMapping("/map/create")
    @PostMapping(path = "/map/create")
    public ResponseEntity<?> saveS3(
            @RequestPart("dto") MapRequestDTO.mapCreateDTO mapDTO,
            @RequestPart("file") MultipartFile file) {
        System.out.println("Received MapDTO: " + mapDTO);

        // 이미지 파일 확인
        System.out.println("Received File Name: " + file.getOriginalFilename());
        System.out.println("Received File Size: " + file.getSize());
        try {
            mapService.saveS3(file, mapDTO);
        } catch (Exception e) {
            return new ResponseEntity<>(ApiUtils.error(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(ApiUtils.success("맵 저장을 완료했습니다."), HttpStatus.OK);
    }

    // 맵 데이터 내보내기
    @PostMapping("/map/data")
    public HashMap<String, Object> mapData(@RequestBody MapRequestDTO.getMapDataDTO dto) {

        HashMap<String, Object> outputMapData = new HashMap<>();
        HashMap<String, Object> outputStartPoint = new HashMap<>();

        Map map = mapService.getMapInfo(dto.mapName());
        List<Mission> mapMissions = mapService.getMapMissions(map.getMissionIds());
        List<Mission> missions = mapMissions.stream().filter(m -> m.getPinNo() != -1).collect(Collectors.toList());

        Mission startPointMission = mapMissions.stream().filter(mission -> mission.getPinNo() == -1).findAny().orElseThrow();

        String imageEncoded;
        try {
            imageEncoded = awsFileService.downloadAndEncodeFileFromUrl(map.getMapImage());
        } catch (Exception e) {
            return new HashMap<>() {{
                put("errorMessage", "이미지 파일 오류");
                put("error", e.getMessage());
            }};
        }

        // start point 값 추가
        outputStartPoint.put("x", startPointMission.getX());
        outputStartPoint.put("y", startPointMission.getY());

        // map data 추가
        outputMapData.put("mapName", map.getMapName());
        // 이미지 값 수정
        outputMapData.put("mapImage", imageEncoded);
        outputMapData.put("latitude", map.getLatitude());
        outputMapData.put("longitude", map.getLongitude());
        outputMapData.put("producer", map.getProducer());
        outputMapData.put("mission", missions);
        outputMapData.put("startPoint", outputStartPoint);

        System.out.println("맵 데이터 내보내기 성공" + LocalDateTime.now());
        return outputMapData;
    }

    @PostMapping("/maps")
    public ResponseEntity<?> mapNameList() {
        List<String> mapList;
        try {
            mapList = mapService.getMapNameList();
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(ApiUtils.error("맵 목록 불러오기에 실패하였습니다."));
        }
        return ResponseEntity.ok()
                .body(ApiUtils.success(mapList));
    }

    @GetMapping("/map/all")
    public ResponseEntity<?> mapList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size
    ) {

        Page<Map> mapList;
        try {
            mapList = mapService.findAllMap(page, size);
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(ApiUtils.error("맵 목록 불러오기에 실패하였습니다."));
        }

        return ResponseEntity.ok().body(ApiUtils.success(mapList));
    }

    // 멥 삭제
    @GetMapping("/map/delete/{name}")
    public ResponseEntity<?> deleteMap(@PathVariable("name") String name) {
        try {
            mapService.deleteMap(name);
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(ApiUtils.error("맵 삭제 실패"));
        }
        return ResponseEntity
                .ok()
                .body(ApiUtils.success("맵 삭제 성공, 맵 이름: " + name));
    }
}
