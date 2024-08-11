package com.mindray.bootstraper;

import com.mindray.config.annatation.EnableCISClient;
import com.mindray.config.util.ClassUtils;
import com.mindray.egateway.HubWorker;
import com.mindray.mapper.EmployeeMapper;
import com.mindray.mapper.PatientMapper;
import com.mindray.mapper.UserMapper;
import com.mindray.mapper.dto.Employee;
import com.mindray.mapper.dto.InfoUser;
import com.mindray.mapper.dto.PatientInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AppTest {

    @Resource
    private HubWorker hubWorker;

    @Resource
    private UserMapper userMapper;

    @Resource
    private EmployeeMapper employeeMapper;

    @Resource
    private PatientMapper patientMapper;

    @Test
    public void stop() {
        System.out.println("Hello world");
        for (int index = 0; index < 10; index++) {
            hubWorker.processAdtMsg(null);
        }
    }

    @Test
    public void test_mysql_connect(){
        InfoUser infoUser = userMapper.queryById("name918760");
        System.out.println(infoUser.getUserName());
        List<Employee> employees = employeeMapper.queryEmployeeById();
        System.out.println(employees.size());
        List<PatientInfo> patientInfos = patientMapper.queryPatientInfoByPid("pid1");
        System.out.println(patientInfos.size());

    }
    @Test
    public void test_read_class_with_annatation() throws IOException, ClassNotFoundException {
        List<String> allClassWithAnnotation = ClassUtils.getAllClassWithAnnoEnableCISClient(new String[]{"com.mindray.cis.hospital"});
        System.out.println(allClassWithAnnotation.size());
    }
}