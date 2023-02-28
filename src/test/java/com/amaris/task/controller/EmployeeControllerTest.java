package com.amaris.task.controller;

import com.amaris.task.common.ResponseModel;
import com.amaris.task.entity.Employee;
import com.amaris.task.service.CommonServiceOps;
import lombok.NoArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class EmployeeControllerTest extends CommonServiceOps {

    public static final String GET_ALL_EMPLOYEE_END_POINT = "/employee/all";
    public static final String EMPLOYEE_BY_ID_END_POINT = "/employee/";
    public static final String ADD_EMPLOYEE_END_POINT = "/employee/add";

    @Autowired
    WebTestClient webTestClient;

    @Test
    public void getAllEmployee() {
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
        webTestClient.get().uri(EMPLOYEE_BY_ID_END_POINT + "2")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(ResponseModel.class)
                .value(responseModel -> {
                    assertEquals("{id=2, name=Henok}", responseModel.getPayload().get(0).toString());
                });
    }

    @Test
    public void getEmployeeByIdException() {
        webTestClient.get().uri(EMPLOYEE_BY_ID_END_POINT + "8")
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(String.class)
                .isEqualTo("{\"status\":\"KO\",\"payload\":null,\"errors\":\"No employee found with id equals :8\"}");
    }

    @Test
    public void addEmployee() {
        Employee employee = new Employee(5, "Sheldon");

        webTestClient.post().uri(ADD_EMPLOYEE_END_POINT)
                .body(Mono.just(employee), Employee.class)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ResponseModel.class)
                .value(res -> {
                    assertEquals("{id=5, name=Sheldon}", res.get(0).getPayload().get(0).toString());
                });
    }
}
