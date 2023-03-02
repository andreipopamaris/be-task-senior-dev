package com.amaris.task.controller;

import com.amaris.task.common.response.ResponseModel;
import com.amaris.task.entity.Employee;
import com.amaris.task.exception.TaskManagerErrorCode;
import com.amaris.task.service.CommonServiceOps;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import javax.ws.rs.core.MediaType;

import static org.junit.Assert.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@NoArgsConstructor
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EmployeeControllerTest extends CommonServiceOps {

    private static final Logger logger = LogManager.getLogger(EmployeeControllerTest.class);

    public static final String GET_ALL_EMPLOYEE_END_POINT = "/employee/all";
    public static final String EMPLOYEE_BY_ID_END_POINT = "/employee/";
    public static final String ADD_EMPLOYEE_END_POINT = "/employee/add";

    @Autowired
    WebTestClient webTestClient;

    @Test
    public void addEmployee() {

        logger.info(">>>>>>>>>Test case addEmployee >>>>>> : " + ADD_EMPLOYEE_END_POINT);
        Employee employee = Employee.builder().name("Sheldon").build();

        webTestClient.post().uri(ADD_EMPLOYEE_END_POINT)
                .body(Mono.just(employee), Employee.class)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ResponseModel.class)
                .value(res -> {
                    assertEquals("{id=5, name=Sheldon}", res.get(0).getPayload().get(0).toString());
                });
    }

    @Test
    public void getAllEmployee() {
        logger.info(">>>>>>>>>Test case getAllEmployee >>>>>> : " + GET_ALL_EMPLOYEE_END_POINT);
        webTestClient.get().uri(GET_ALL_EMPLOYEE_END_POINT)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(ResponseModel.class)
                .value((response) -> {
                    assertEquals(4, response.getPayload().size());
                });
    }

    @Test
    public void getEmployeeById() {
        logger.info(">>>>>>>>>Test case getEmployeeById >>>>>> : " + EMPLOYEE_BY_ID_END_POINT + 2);
        webTestClient.get().uri(EMPLOYEE_BY_ID_END_POINT + "2")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(ResponseModel.class)
                .value(responseModel -> {
                    assertEquals("{id=2, name=Employee One}", responseModel.getPayload().get(0).toString());
                });
    }

    @Test
    public void getEmployeeByIdException() {
        logger.info(">>>>>>>>>Test case getEmployeeByIdException >>>>>> : " + EMPLOYEE_BY_ID_END_POINT + 8);
        webTestClient.get().uri(EMPLOYEE_BY_ID_END_POINT + "8")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(ResponseModel.class)
                .value(res -> {
                    assertEquals(TaskManagerErrorCode.EMPLOYEE_NOT_FOUND.getMessage() + 8, res.getErrors().getMessage());
                });
    }

    @Test
    public void nullEmployeeNameException() {
        logger.info(">>>>>>>>>Test case nullEmployeeNameException >>>>>> : " + ADD_EMPLOYEE_END_POINT);
        Employee employee = Employee.builder().build();

        webTestClient.post().uri(ADD_EMPLOYEE_END_POINT)
                .body(Mono.just(employee), Employee.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ResponseModel.class)
                .value(res -> {
                    assertEquals(TaskManagerErrorCode.EMPLOYEE_NAME_MUST_NOT_NULL.getMessage(), res.getErrors().getMessage());
                });
    }
}
