package com.mindray.mapper.dto;

import lombok.Data;

@Data
public class PatientInfo {
    //腕带上住院号
    private String pid;
    //就诊流水号
    private String vid;
    //姓名
    private String name;
    //性别0:未知,1:男,2:女
    private Integer gender;
    //年龄
    private Integer age;
    //出生日期
    private String dob;
    //床号
    private String bed;
    //房间号
    private String room;
    //科室
    private String department;
    //医院
    private String facility;
    //身高,单位为：cm。无效值为0， 表示未设置
    private Integer height;
    //单位为：kg。无效值为0， 表示未设置。
    private Integer weight;
    //设备id
    private String deviceid;
}
