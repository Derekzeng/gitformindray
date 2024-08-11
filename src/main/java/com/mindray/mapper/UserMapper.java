package com.mindray.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mindray.mapper.dto.InfoUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
public interface UserMapper extends BaseMapper<InfoUser> {
    InfoUser queryById(String query);
}
