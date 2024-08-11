package com.mindray.adtserver;

import com.alibaba.fastjson.JSON;
import com.mindray.adtserver.vo.PostAdtRequest;
import com.mindray.egateway.hl7.HL7Client;
import com.mindray.egateway.hl7.HL7Util;
import com.mindray.egateway.model.AdtResponse;
import com.mindray.egateway.model.Pid;
import com.mindray.egateway.model.Pv1;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RequestMapping(value = "")
@RestController
public class AdtController {
    private static final Logger logger = LoggerFactory.getLogger(AdtController.class);
    @Resource
    private HL7Client hl7Client;
    @RequestMapping(value = "/adt",method = RequestMethod.GET)
    public String get() {
        logger.info("enter get.");
        return "Hello World";
    }
    @RequestMapping(value = "/adt", consumes = "application/json",method = RequestMethod.POST)
    public String post(@RequestBody PostAdtRequest request) {
        logger.info("[info]adt post request:{}", JSON.toJSON(request));
        String ret ="";
        try {
            AdtResponse response = new AdtResponse();
            toADTResponse(request,response);
            String hl7message = HL7Util.getIns().createADT_A01(response);
            logger.info("[info] send egateway message:{}",hl7message);
            hl7Client.send(hl7message);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            ret = e.getMessage();
        }
        logger.info("[info] adt post end.");
        return getResponseMsg(ret);
    }

    private void toADTResponse(PostAdtRequest req, AdtResponse response) {
        Pid pid = new Pid();
        Pv1 pv1 = new Pv1();
        pid.setPid(req.getPid());
        String Sex = req.getGender() != null ? req.getGender().toString() : "";
        if ("1".equals(Sex)) {
            pid.setGenderCode("M");
        } else if ("2".equals(Sex)) {
            pid.setGenderCode("F");
        }
        pv1.setVisitNumber(req.getVid());
        pid.setFirstName(req.getName());
        pid.setBirthday(req.getDob());
        pv1.setRoom(req.getRoom());
        pv1.setBed(req.getBed());
        pv1.setDepartment(req.getDepartment());
        pv1.setFacility(req.getFacility());
        pv1.setMacAddress(req.getDeviceid());
        response.setPid(pid);
        response.setPv1(pv1);
        response.setHeight(String.valueOf(req.getHeight()));
        response.setWeight(String.valueOf(req.getWeight()));
    }

    private String getResponseMsg(String errorMsg){
        if (StringUtils.isBlank(errorMsg)) {
            errorMsg = "{\"message\":\"success\",\"status\":\"ok\"}";
        } else {
            errorMsg = "{\"message\":" + errorMsg + ",\"status\":\"error\"}";
        }
        return errorMsg;
    }
}
