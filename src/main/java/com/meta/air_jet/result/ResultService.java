package com.meta.air_jet.result;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class ResultService {

    private final ResultRepository resultRepository;

    // 결과 저장
    public Result save(ResultRequestDTO.saveDTO dto, Long userId, ResultResponseDTO.saveDTO saveDTO) {
        Result result = Result.builder()
                .userId(userId)
                .rank(0)
                .comment("aaa")
                .createAt(LocalDateTime.now())
                .playTime(dto.playTime())
                .engineStart(dto.engineStart())
                .takeOff(dto.takeOff())
                .formation(dto.formation())
                .airToGround(dto.airToGround())
                .build();
        return resultRepository.save(result);
    }
}