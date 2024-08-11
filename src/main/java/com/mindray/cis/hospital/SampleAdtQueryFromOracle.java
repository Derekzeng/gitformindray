package com.mindray.cis.hospital;

import com.alibaba.fastjson.JSON;
import com.mindray.cis.third.IAdtQuery;
import com.mindray.cis.third.IClient;
import com.mindray.config.annatation.EnableCISClient;
import com.mindray.egateway.model.*;
import com.mindray.mapper.PatientMapper;
import com.mindray.mapper.dto.PatientInfo;
import joptsimple.internal.Strings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@EnableCISClient(isEnable = true)
public class SampleAdtQueryFromOracle implements IClient, IAdtQuery {
    private static final Logger logger = LoggerFactory.getLogger(SampleAdtQueryFromOracle.class);

    @Resource
    private PatientMapper patientMapper;
    @Override
    public List<AdtResponse> querySync(AdtRequest request) throws AdtQueryException {
        logger.info("[mindray-info] request:{}", JSON.toJSONString(request));
        //获取病历号，通过病历号查询
        String pidstr = request.getPid();
        List<AdtResponse> adtResponses = new ArrayList<>();
        if(!Strings.isNullOrEmpty(pidstr)){
            try {
                List<PatientInfo> patientInfos = patientMapper.queryPatientInfoByPid(pidstr);
                convertToAdtResponses(patientInfos, adtResponses);
            }catch (Exception e){
                logger.info("[error:]{},返回空的集合.",e.getMessage());
            }
            logger.info("安徽二附院添加了代码。");
        }
        return adtResponses;
    }

    private static void convertToAdtResponses(List<PatientInfo> patientInfos, List<AdtResponse> adtResponses) {
        Pid pid = new Pid();
        Pv1 pv1 = new Pv1();
        if(patientInfos != null && !patientInfos.isEmpty()){
            for (PatientInfo patientInfo : patientInfos) {
                AdtResponse adtResponse = new AdtResponse();
                pid.setPid(patientInfo.getPid());
                pv1.setVisitNumber(patientInfo.getVid());
                pid.setFirstName(patientInfo.getName());

                String Sex =String.valueOf(patientInfo.getGender());
                if ("1".equals(Sex)) {
                    pid.setGenderCode("M");
                } else if ("2".equals(Sex)) {
                    pid.setGenderCode("F");
                }
                String DateOfBirth = patientInfo.getDob();
                try {
                    if (StringUtils.isNotBlank(DateOfBirth) && DateOfBirth.length() >= 8) {
                        DateOfBirth = DateOfBirth.substring(0, 8);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                        sdf.setLenient(false);
                        sdf.parse(DateOfBirth);
                        pid.setBirthday(DateOfBirth);
                    }
                } catch (Exception e) {
                    logger.error("[mindray-error];error:{}",e.getMessage());
                }
                pv1.setBed(patientInfo.getBed());
                pv1.setRoom(patientInfo.getRoom());
                pv1.setDepartment(patientInfo.getDepartment());
                pv1.setFacility(patientInfo.getFacility());
                pv1.setMacAddress(patientInfo.getDeviceid());
                pv1.setReferringDoctor("");// 管床医生
                adtResponse.setPid(pid);
                adtResponse.setPv1(pv1);
                adtResponse.setHeight(String.valueOf(patientInfo.getHeight()));
                adtResponse.setWeight(String.valueOf(patientInfo.getWeight()));
                adtResponses.add(adtResponse);
            }
        }else{
            AdtResponse adtResponse = new AdtResponse();
            adtResponse.setPid(pid);
            adtResponse.setPv1(pv1);
            adtResponses.add(adtResponse);
        }
    }

    @Override
    public void init() throws Exception {

    }
}
