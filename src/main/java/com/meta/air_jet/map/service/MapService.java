package com.meta.air_jet.map.service;


import com.meta.air_jet._core.file.AwsFileService;
import com.meta.air_jet.map.domain.Map;
import com.meta.air_jet.map.domain.dto.MapRequestDTO;
import com.meta.air_jet.map.repository.MapRepository;
import com.meta.air_jet.mission.Mission;
import com.meta.air_jet.mission.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MapService {
    private final MapRepository mapRepository;
    private final MissionRepository missionRepository;
    private final AwsFileService awsFileService;

    public Map getMapInfo(String mapName) {
        return mapRepository.findByMapName(mapName);
    }

    public ArrayList<Mission> getMapMissions(List<Long> missionIds) {
        ArrayList<Mission> mapMissions = new ArrayList<>();
        missionIds.forEach(missionId -> {
            Mission mission = missionRepository.findById(missionId).orElseThrow();
            mapMissions.add(mission);
        });
        return mapMissions;
    }

    public void saveS3(MultipartFile mapImage, MapRequestDTO.mapCreateDTO dto) throws IOException {
        duplicateMapName(dto.mapName());
        List<Long> missionsIds = new ArrayList<>();
        // dto에서 받은 미션들을 mission repo 에 저장
        List<Mission> missions = missionRepository.saveAll(dto.mission());

        // missionIds 값 추가
        missions.forEach(mission -> missionsIds.add(mission.getId()));
        // 이미지 추가 및 url 가져오기
        String imageUrl = awsFileService.upload(mapImage, "image");
        System.out.println("imageUrl = " + imageUrl);
        // 맵 생성
        Map map = Map.builder()
                .mapName(dto.mapName())
                .mapImage(imageUrl)
                .latitude(dto.latitude())
                .longitude(dto.longitude())
                .producer(dto.producer())
                .missionIds(missionsIds)
                .createAt(LocalDateTime.now())
                .build();

        // 맵 저장
        mapRepository.save(map);
    }

    private void duplicateMapName(String mapName) {
        Map findMap = mapRepository.findByMapName(mapName);
        if (findMap != null) {
            throw new IllegalArgumentException("맵 이름이 중복되었습니다. map name: " + mapName);
        }
    }

    public List<String> getMapNameList() {
        return mapRepository.findAllMapName();
    }

    public Page<Map> findAllMap(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        return mapRepository.findAllSorted(pageable);
    }

    public void deleteMap(String name) {
        mapRepository.deleteByMapName(name);
    }
}
