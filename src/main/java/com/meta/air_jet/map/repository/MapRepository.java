package com.meta.air_jet.map.repository;

import com.meta.air_jet.map.domain.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MapRepository extends JpaRepository<Map, Long> {
    @Query("select m from Map m where m.mapName = :mapName")
    Map findByMapName(@Param("mapName") String mapName);

    @Query("select m.mapName from Map m")
    List<String> findAllMapName();

    @Query("SELECT m FROM Map m ORDER BY m.createAt DESC")
    Page<Map> findAllSorted(Pageable pageable);

    @Modifying
    @Query("delete from Map m where m.mapName = :name")
    void deleteByMapName(@Param("name") String name);
}
