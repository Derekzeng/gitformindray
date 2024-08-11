package com.mindray.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mindray.mapper.dto.PatientInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@DS("slave")
public interface PatientMapper extends BaseMapper<PatientMapper> {
    List<PatientInfo> queryPatientInfoByPid(@Param("pid") String pid);
}
